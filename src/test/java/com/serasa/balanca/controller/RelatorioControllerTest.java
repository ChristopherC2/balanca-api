package com.serasa.balanca.controller;

import com.serasa.balanca.model.response.TransacaoTransporteResponse;
import com.serasa.balanca.service.BalancaService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RelatorioControllerTest {

    @InjectMocks
    private RelatorioController relatorioController;

    @Mock
    private BalancaService balancaService;

    @Test
    public void deveRetornarListaDeRelatoriosComSucesso() {
        String filial = "Matriz";
        String placa = "ABC1234";
        String grao = "Soja";
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        TransacaoTransporteResponse responseItem = TransacaoTransporteResponse.builder()
                .placaCaminhao(placa)
                .nomeFilial(filial)
                .nomeTipoGrao(grao)
                .pesoLiquido(35000.0)
                .custo(87500.0)
                .build();

        List<TransacaoTransporteResponse> mockList = Arrays.asList(responseItem);

        Mockito.when(balancaService.listarRelatorio(filial, placa, grao, inicio, fim))
                .thenReturn(mockList);

        ResponseEntity<List<TransacaoTransporteResponse>> response =
                relatorioController.obterRelatorio(filial, placa, grao, inicio, fim);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(1, response.getBody().size());
        Assert.assertEquals(placa, response.getBody().get(0).getPlacaCaminhao());

        Mockito.verify(balancaService, Mockito.times(1))
                .listarRelatorio(filial, placa, grao, inicio, fim);
    }

    @Test
    public void deveRetornarListaVaziaQuandoNaoHouverDados() {
        Mockito.when(balancaService.listarRelatorio(null, null, null, null, null))
                .thenReturn(Arrays.asList());

        ResponseEntity<List<TransacaoTransporteResponse>> response =
                relatorioController.obterRelatorio(null, null, null, null, null);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().isEmpty());
    }
}