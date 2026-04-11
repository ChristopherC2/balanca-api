package com.serasa.balanca.service;

import com.serasa.balanca.mapper.TransacaoTransporteMapper;
import com.serasa.balanca.model.entities.*;
import com.serasa.balanca.model.requests.TransacaoTransporteRequest;
import com.serasa.balanca.model.response.TransacaoTransporteResponse;
import com.serasa.balanca.repository.BalancaRepository;
import com.serasa.balanca.repository.CaminhaoRepository;
import com.serasa.balanca.repository.TransacaoTransporteRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void deveProcessarComIdempotenciaApenasUmaVez() {
        TransacaoTransporteRequest request = new TransacaoTransporteRequest();
        request.setPlate("ABC1234");
        request.setWeight(50000.0);
        request.setBalancaId("BAL-01");

        String chave = "chave-unica";

        balancaService.processarComIdempotencia(request, chave);
        balancaService.processarComIdempotencia(request, chave);

        Mockito.verify(balancaRepo, Mockito.atMost(1)).findById(Mockito.anyString());
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

        for (int i = 0; i < 5; i++) {
            TransacaoTransporteRequest request = new TransacaoTransporteRequest();
            request.setPlate(placa);
            request.setWeight(30000.0);
            request.setBalancaId(idBalanca);

            balancaService.processarComIdempotencia(request, "key-" + i);
        }

        Mockito.verify(transacaoRepo, Mockito.times(1)).save(Mockito.any(TransacaoTransporte.class));
    }

    @Test
    public void deveCalcularValoresCorretamenteNaEfetivacao() {
        String placa = "ABC1234";
        Double pesoBruto = 50000.0;
        Double tara = 20000.0;
        Double precoKg = 2.0;

        Caminhao caminhao = Caminhao.builder()
                .placa(placa)
                .tara(tara)
                .filialPadrao(Filial.builder().build())
                .graoPadrao(TipoGrao.builder().precoPorKg(precoKg).build())
                .build();

        Mockito.when(balancaRepo.findById("BAL-01")).thenReturn(Optional.of(Balanca.builder().id("BAL-01").build()));
        Mockito.when(caminhaoRepo.findById(placa)).thenReturn(Optional.of(caminhao));

        for (int i = 0; i < 5; i++) {
            TransacaoTransporteRequest request = new TransacaoTransporteRequest();
            request.setPlate(placa);
            request.setWeight(pesoBruto);
            request.setBalancaId("BAL-01");
            balancaService.processarComIdempotencia(request, "idempotencia-" + i);
        }

        Mockito.verify(transacaoRepo).save(Mockito.argThat(t -> {
            Double pesoLiquidoEsperado = pesoBruto - tara;
            Double custoEsperado = pesoLiquidoEsperado * precoKg;
            Double lucroEsperado = custoEsperado * 0.15;

            return t.getPesoLiquido().equals(pesoLiquidoEsperado) &&
                    t.getCusto().equals(custoEsperado) &&
                    t.getLucro().equals(lucroEsperado);
        }));
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoQuandoBalancaNaoCadastrada() {
        String placa = "ABC1234";
        Mockito.when(balancaRepo.findById("ERR")).thenReturn(Optional.empty());

        TransacaoTransporteRequest request = new TransacaoTransporteRequest();
        request.setPlate(placa);
        request.setWeight(50000.0);
        request.setBalancaId("ERR");

        for (int i = 0; i < 5; i++) {
            balancaService.processarComIdempotencia(request, "err-" + i);
        }
    }

    @Test
    public void deveFiltrarPorFilialNoRelatorio() {
        String filial = "Matriz";
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime fim = LocalDateTime.now().withHour(23).withMinute(59);

        TransacaoTransporte t = new TransacaoTransporte();
        Mockito.when(transacaoRepo.findByFilialNomeAndDataFimBetween(Mockito.eq(filial), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t));
        Mockito.when(transacaoTransporteMapper.toResponse(t)).thenReturn(new TransacaoTransporteResponse());

        List<TransacaoTransporteResponse> resultado = balancaService.listarRelatorio(filial, null, null, null, null);

        Assert.assertEquals(1, resultado.size());
        Mockito.verify(transacaoRepo).findByFilialNomeAndDataFimBetween(Mockito.eq(filial), Mockito.any(), Mockito.any());
    }

    @Test
    public void deveFiltrarPorPlacaNoRelatorio() {
        String placa = "ABC1234";

        TransacaoTransporte t = new TransacaoTransporte();
        Mockito.when(transacaoRepo.findByCaminhaoPlacaAndDataFimBetween(Mockito.eq(placa), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t));
        Mockito.when(transacaoTransporteMapper.toResponse(t)).thenReturn(new TransacaoTransporteResponse());

        List<TransacaoTransporteResponse> resultado = balancaService.listarRelatorio(null, placa, null, null, null);

        Assert.assertEquals(1, resultado.size());
        Mockito.verify(transacaoRepo).findByCaminhaoPlacaAndDataFimBetween(Mockito.eq(placa), Mockito.any(), Mockito.any());
    }

    @Test
    public void deveFiltrarPorGraoNoRelatorio() {
        String grao = "Soja";

        TransacaoTransporte t = new TransacaoTransporte();
        Mockito.when(transacaoRepo.findByTipoGraoNomeAndDataFimBetween(Mockito.eq(grao), Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t));
        Mockito.when(transacaoTransporteMapper.toResponse(t)).thenReturn(new TransacaoTransporteResponse());

        List<TransacaoTransporteResponse> resultado = balancaService.listarRelatorio(null, null, grao, null, null);

        Assert.assertEquals(1, resultado.size());
        Mockito.verify(transacaoRepo).findByTipoGraoNomeAndDataFimBetween(Mockito.eq(grao), Mockito.any(), Mockito.any());
    }

    @Test
    public void deveFiltrarApenasPorDataQuandoParametrosForemNulos() {
        TransacaoTransporte t = new TransacaoTransporte();
        Mockito.when(transacaoRepo.findByDataFimBetween(Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(t));
        Mockito.when(transacaoTransporteMapper.toResponse(t)).thenReturn(new TransacaoTransporteResponse());

        List<TransacaoTransporteResponse> resultado = balancaService.listarRelatorio(null, null, null, null, null);

        Assert.assertEquals(1, resultado.size());
        Mockito.verify(transacaoRepo).findByDataFimBetween(Mockito.any(), Mockito.any());
    }
}