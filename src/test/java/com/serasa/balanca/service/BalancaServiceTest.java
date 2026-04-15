package com.serasa.balanca.service;

import com.serasa.balanca.config.BalancaProperties;
import com.serasa.balanca.exception.RecursoNaoEncontradoException;
import com.serasa.balanca.mapper.TransacaoTransporteMapper;
import com.serasa.balanca.model.entities.*;
import com.serasa.balanca.model.requests.TransacaoTransporteRequest;
import com.serasa.balanca.model.response.TransacaoTransporteResponse;
import com.serasa.balanca.repository.BalancaRepository;
import com.serasa.balanca.repository.CaminhaoRepository;
import com.serasa.balanca.repository.TransacaoTransporteRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@RunWith(MockitoJUnitRunner.class)
public class BalancaServiceTest {

    @InjectMocks
    private BalancaService balancaService;

    @Mock
    private TransacaoTransporteRepository transacaoRepo;
    @Mock
    private CaminhaoRepository caminhaoRepo;
    @Mock
    private BalancaRepository balancaRepo;
    @Mock
    private TransacaoTransporteMapper transacaoTransporteMapper;
    @Mock
    private PesagemBuffer pesagemBuffer;
    @Mock
    private BalancaProperties props;

    @Before
    public void configurarProps() {
        Mockito.when(props.getTransacao()).thenReturn(new BalancaProperties.Transacao());
    }

    @Test
    public void deveProcessarComIdempotenciaApenasUmaVez() {
        TransacaoTransporteRequest request = new TransacaoTransporteRequest("BAL-01", "ABC1234", 50000.0);
        String chave = "chave-unica";

        Mockito.when(pesagemBuffer.registrarLeitura(Mockito.anyString(), Mockito.anyDouble()))
                .thenReturn(OptionalDouble.empty());

        balancaService.processarComIdempotencia(request, chave);
        balancaService.processarComIdempotencia(request, chave);

        Mockito.verify(pesagemBuffer, Mockito.times(1))
                .registrarLeitura(Mockito.anyString(), Mockito.anyDouble());
    }

    @Test
    public void deveEfetivarTransacaoQuandoPesoEstabilizar() {
        String placa = "XYZ9999";
        String idBalanca = "BAL-01";

        Balanca balanca = Balanca.builder().id(idBalanca).build();
        TipoGrao grao = TipoGrao.builder().nome("Milho").precoPorKg(2.0).build();
        Filial filial = Filial.builder().nome("Filial Sul").build();
        Caminhao caminhao = Caminhao.builder()
                .placa(placa)
                .tara(10000.0)
                .filialPadrao(filial)
                .graoPadrao(grao)
                .build();

        Mockito.when(balancaRepo.findById(idBalanca)).thenReturn(Optional.of(balanca));
        Mockito.when(caminhaoRepo.findById(placa)).thenReturn(Optional.of(caminhao));
        Mockito.when(pesagemBuffer.registrarLeitura(placa, 30000.0))
                .thenReturn(OptionalDouble.of(30000.0));

        TransacaoTransporteRequest request = new TransacaoTransporteRequest(idBalanca, placa, 30000.0);
        balancaService.processarComIdempotencia(request, "key-1");

        Mockito.verify(transacaoRepo, Mockito.times(1)).save(Mockito.any(TransacaoTransporte.class));
    }

    @Test
    public void deveCalcularValoresCorretamenteNaEfetivacao() {
        String placa = "ABC1234";
        double pesoBruto = 50000.0;
        double tara = 20000.0;
        double precoKg = 2.0;

        Caminhao caminhao = Caminhao.builder()
                .placa(placa)
                .tara(tara)
                .filialPadrao(Filial.builder().build())
                .graoPadrao(TipoGrao.builder().precoPorKg(precoKg).build())
                .build();

        Mockito.when(balancaRepo.findById("BAL-01")).thenReturn(Optional.of(Balanca.builder().id("BAL-01").build()));
        Mockito.when(caminhaoRepo.findById(placa)).thenReturn(Optional.of(caminhao));
        Mockito.when(pesagemBuffer.registrarLeitura(placa, pesoBruto))
                .thenReturn(OptionalDouble.of(pesoBruto));

        TransacaoTransporteRequest request = new TransacaoTransporteRequest("BAL-01", placa, pesoBruto);
        balancaService.processarComIdempotencia(request, "idempotencia-1");

        Mockito.verify(transacaoRepo).save(Mockito.argThat(t -> {
            double pesoLiquidoEsperado = pesoBruto - tara;
            double custoEsperado = pesoLiquidoEsperado * precoKg;
            double lucroEsperado = custoEsperado * 0.15;

            return t.getPesoLiquido().equals(pesoLiquidoEsperado) &&
                    t.getCusto().equals(custoEsperado) &&
                    t.getLucro().equals(lucroEsperado);
        }));
    }

    @Test(expected = RecursoNaoEncontradoException.class)
    public void deveLancarExcecaoQuandoBalancaNaoCadastrada() {
        String placa = "ABC1234";
        Mockito.when(balancaRepo.findById("ERR")).thenReturn(Optional.empty());
        Mockito.when(pesagemBuffer.registrarLeitura(placa, 50000.0))
                .thenReturn(OptionalDouble.of(50000.0));

        TransacaoTransporteRequest request = new TransacaoTransporteRequest("ERR", placa, 50000.0);
        balancaService.processarComIdempotencia(request, "err-1");
    }

    @Test
    public void deveFiltrarPorFilialNoRelatorio() {
        String filial = "Matriz";

        TransacaoTransporte t = new TransacaoTransporte();
        TransacaoTransporteResponse mockResponse = new TransacaoTransporteResponse(
                null, filial, null, null, null, null, null, null, null, null, null
        );
        Mockito.when(transacaoRepo.findByFilialNomeAndDataFimBetween(Mockito.eq(filial), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t));
        Mockito.when(transacaoTransporteMapper.toResponse(t)).thenReturn(mockResponse);

        List<TransacaoTransporteResponse> resultado = balancaService.listarRelatorio(filial, null, null, null, null);

        Assert.assertEquals(1, resultado.size());
        Mockito.verify(transacaoRepo).findByFilialNomeAndDataFimBetween(Mockito.eq(filial), Mockito.any(), Mockito.any());
    }

    @Test
    public void deveFiltrarPorPlacaNoRelatorio() {
        String placa = "ABC1234";

        TransacaoTransporte t = new TransacaoTransporte();
        TransacaoTransporteResponse mockResponse = new TransacaoTransporteResponse(
                placa, null, null, null, null, null, null, null, null, null, null
        );
        Mockito.when(transacaoRepo.findByCaminhaoPlacaAndDataFimBetween(Mockito.eq(placa), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t));
        Mockito.when(transacaoTransporteMapper.toResponse(t)).thenReturn(mockResponse);

        List<TransacaoTransporteResponse> resultado = balancaService.listarRelatorio(null, placa, null, null, null);

        Assert.assertEquals(1, resultado.size());
        Mockito.verify(transacaoRepo).findByCaminhaoPlacaAndDataFimBetween(Mockito.eq(placa), Mockito.any(), Mockito.any());
    }

    @Test
    public void deveFiltrarPorGraoNoRelatorio() {
        String grao = "Soja";

        TransacaoTransporte t = new TransacaoTransporte();
        TransacaoTransporteResponse mockResponse = new TransacaoTransporteResponse(
                null, null, grao, null, null, null, null, null, null, null, null
        );
        Mockito.when(transacaoRepo.findByTipoGraoNomeAndDataFimBetween(Mockito.eq(grao), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t));
        Mockito.when(transacaoTransporteMapper.toResponse(t)).thenReturn(mockResponse);

        List<TransacaoTransporteResponse> resultado = balancaService.listarRelatorio(null, null, grao, null, null);

        Assert.assertEquals(1, resultado.size());
        Mockito.verify(transacaoRepo).findByTipoGraoNomeAndDataFimBetween(Mockito.eq(grao), Mockito.any(), Mockito.any());
    }

    @Test
    public void deveFiltrarApenasPorDataQuandoParametrosForemNulos() {
        TransacaoTransporte t = new TransacaoTransporte();
        TransacaoTransporteResponse mockResponse = new TransacaoTransporteResponse(
                null, null, null, null, null, null, null, null, null, null, null
        );
        Mockito.when(transacaoRepo.findByDataFimBetween(Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t));
        Mockito.when(transacaoTransporteMapper.toResponse(t)).thenReturn(mockResponse);

        List<TransacaoTransporteResponse> resultado = balancaService.listarRelatorio(null, null, null, null, null);

        Assert.assertEquals(1, resultado.size());
        Mockito.verify(transacaoRepo).findByDataFimBetween(Mockito.any(), Mockito.any());
    }
}
