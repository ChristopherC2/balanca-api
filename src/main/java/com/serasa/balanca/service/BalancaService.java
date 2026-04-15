package com.serasa.balanca.service;

import com.serasa.balanca.config.BalancaProperties;
import com.serasa.balanca.exception.RecursoNaoEncontradoException;
import com.serasa.balanca.mapper.TransacaoTransporteMapper;
import com.serasa.balanca.model.entities.Balanca;
import com.serasa.balanca.model.entities.Caminhao;
import com.serasa.balanca.model.entities.TransacaoTransporte;
import com.serasa.balanca.model.requests.TransacaoTransporteRequest;
import com.serasa.balanca.model.response.TransacaoTransporteResponse;
import com.serasa.balanca.repository.BalancaRepository;
import com.serasa.balanca.repository.CaminhaoRepository;
import com.serasa.balanca.repository.TransacaoTransporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalancaService {

    private final TransacaoTransporteRepository transacaoRepo;
    private final CaminhaoRepository caminhaoRepo;
    private final BalancaRepository balancaRepo;
    private final TransacaoTransporteMapper transacaoTransporteMapper;
    private final PesagemBuffer pesagemBuffer;
    private final BalancaProperties props;

    private final Map<String, Long> chavesProcessadas = new ConcurrentHashMap<>();

    public void processarComIdempotencia(TransacaoTransporteRequest request, String chaveIdempotencia) {
        long agora = System.currentTimeMillis();
        long expiracao = props.getTransacao().getExpiracaoChaveMs();

        chavesProcessadas.values().removeIf(exp -> agora > exp);

        Long existente = chavesProcessadas.putIfAbsent(chaveIdempotencia, agora + expiracao);
        if (existente != null) {
            return;
        }

        OptionalDouble pesoEstavel = pesagemBuffer.registrarLeitura(request.plate(), request.weight());
        pesoEstavel.ifPresent(peso -> efetivarTransacao(request.balancaId(), request.plate(), peso));
    }

    private void efetivarTransacao(String idBalanca, String placa, double pesoBruto) {
        Balanca balanca = balancaRepo.findById(idBalanca)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Balança não encontrada: " + idBalanca));

        Caminhao caminhao = caminhaoRepo.findById(placa)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Caminhão não encontrado: " + placa));

        double tara = caminhao.getTara();
        double pesoLiquido = pesoBruto - tara;
        double precoKg = caminhao.getGraoPadrao().getPrecoPorKg();
        double custo = pesoLiquido * precoKg;
        double lucro = custo * props.getTransacao().getMargemLucro();
        int minutosInicio = props.getTransacao().getSimulacaoMinutosInicio();

        TransacaoTransporte transacao = TransacaoTransporte.builder()
                .balanca(balanca)
                .caminhao(caminhao)
                .filial(caminhao.getFilialPadrao())
                .tipoGrao(caminhao.getGraoPadrao())
                .pesoBruto(pesoBruto)
                .tara(tara)
                .pesoLiquido(pesoLiquido)
                .custo(custo)
                .lucro(lucro)
                .dataInicio(LocalDateTime.now().minusMinutes(minutosInicio))
                .dataFim(LocalDateTime.now())
                .build();

        transacaoRepo.save(transacao);
    }

    public List<TransacaoTransporteResponse> listarRelatorio(
            String filial, String placa, String grao,
            LocalDateTime inicio, LocalDateTime fim) {

        if (inicio == null) inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        if (fim == null) fim = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

        List<TransacaoTransporte> transacoes;

        if (filial != null) {
            transacoes = transacaoRepo.findByFilialNomeAndDataFimBetween(filial, inicio, fim);
        } else if (placa != null) {
            transacoes = transacaoRepo.findByCaminhaoPlacaAndDataFimBetween(placa, inicio, fim);
        } else if (grao != null) {
            transacoes = transacaoRepo.findByTipoGraoNomeAndDataFimBetween(grao, inicio, fim);
        } else {
            transacoes = transacaoRepo.findByDataFimBetween(inicio, fim);
        }

        return transacoes.stream()
                .map(transacaoTransporteMapper::toResponse)
                .collect(Collectors.toList());
    }
}
