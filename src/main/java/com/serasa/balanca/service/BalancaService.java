package com.serasa.balanca.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalancaService {

    private final TransacaoTransporteRepository transacaoRepo;
    private final CaminhaoRepository caminhaoRepo;
    private final BalancaRepository balancaRepo;
    private final TransacaoTransporteMapper transacaoTransporteMapper;
    private final Map<String, List<Double>> buffer = new ConcurrentHashMap<>();
    private final Set<String> chavesProcessadas = ConcurrentHashMap.newKeySet();
    private static final int MINIMO_LEITURAS_ESTABILIZACAO = 5;
    private static final double TOLERANCIA_PESO_ESTABILIZACAO = 0.5;
    private static final double MARGEM_LUCRO_PADRAO = 0.15;
    private static final int SIMULACAO_MINUTOS_INICIO_TRANSACAO = 2;

    public void processarComIdempotencia(TransacaoTransporteRequest request, String chaveIdempotencia) {

        if (!chavesProcessadas.add(chaveIdempotencia)) {
            return;
        }

        this.processar(request);
    }

    private void processar(TransacaoTransporteRequest request) {
        String placa = request.getPlate();
        Double pesoLeitura = request.getWeight();

        buffer.computeIfAbsent(placa, k -> new ArrayList<>()).add(pesoLeitura);
        List<Double> historico = buffer.get(placa);

        if (historico.size() >= MINIMO_LEITURAS_ESTABILIZACAO) {

            double ultimoPeso = historico.get(historico.size() - 1);
            boolean estabilizou = historico.stream()
                    .skip(historico.size() - MINIMO_LEITURAS_ESTABILIZACAO)
                    .allMatch(p -> Math.abs(p - ultimoPeso) < TOLERANCIA_PESO_ESTABILIZACAO);

            if (estabilizou) {
                this.efetivarTransacao(request.getBalancaId(), placa, ultimoPeso);
                buffer.remove(placa);
            }
        }
    }

    private void efetivarTransacao(String idBalanca, String placa, Double pesoBruto) {

        Balanca balanca = balancaRepo.findById(idBalanca)
                .orElseThrow(() -> new RuntimeException("Balança não cadastrada!"));

        Caminhao caminhao = caminhaoRepo.findById(placa)
                .orElseThrow(() -> new RuntimeException("Caminhão não cadastrado!"));

        TransacaoTransporte transacao = TransacaoTransporte.builder()
                .balanca(balanca)
                .caminhao(caminhao)
                .filial(caminhao.getFilialPadrao())
                .tipoGrao(caminhao.getGraoPadrao())
                .pesoBruto(pesoBruto)
                .tara(caminhao.getTara())
                .pesoLiquido(pesoBruto - caminhao.getTara())
                .dataInicio(LocalDateTime.now().minusMinutes(SIMULACAO_MINUTOS_INICIO_TRANSACAO))
                .dataFim(LocalDateTime.now())
                .build();

        double precoKg = transacao.getTipoGrao().getPrecoPorKg();
        transacao.setCusto(transacao.getPesoLiquido() * precoKg);
        transacao.setLucro(transacao.getCusto() * MARGEM_LUCRO_PADRAO);

        transacaoRepo.save(transacao);
    }

    public List<TransacaoTransporteResponse> listarRelatorio(String filial, String placa, String grao, LocalDateTime inicio, LocalDateTime fim) {

        if (inicio == null) inicio = LocalDateTime.now().withHour(0).withMinute(0);
        if (fim == null) fim = LocalDateTime.now().withHour(23).withMinute(59);

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

        return transacoes.stream().map(transacaoTransporteMapper::toResponse).collect(Collectors.toList());
    }
}