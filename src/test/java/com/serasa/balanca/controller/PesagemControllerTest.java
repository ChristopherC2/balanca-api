package com.serasa.balanca.controller;

import com.serasa.balanca.model.requests.TransacaoTransporteRequest;
import com.serasa.balanca.service.BalancaService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class PesagemControllerTest {

    @InjectMocks
    private PesagemController pesagemController;

    @Mock
    private BalancaService balancaService;

    @Before
    public void setup() {
        AuthController.tokenStore.clear();
    }

    @Test
    public void deveReceberPesagemComSucesso() {
        String token = "token-valido";
        AuthController.tokenStore.put(token, System.currentTimeMillis() + 600000);

        TransacaoTransporteRequest request = new TransacaoTransporteRequest();
        request.setPlate("ABC1234");
        request.setWeight(50000.0);
        request.setBalancaId("BAL-01");

        ResponseEntity<?> response = pesagemController.receber("Bearer " + token, "key-123", request);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(balancaService, Mockito.times(1)).processarComIdempotencia(request, "key-123");
    }

    @Test
    public void deveRetornar401QuandoTokenInexistente() {
        TransacaoTransporteRequest request = new TransacaoTransporteRequest();

        ResponseEntity<?> response = pesagemController.receber("Bearer token-invalido", "key-123", request);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assert.assertEquals("Token inválido ou expirado. Faça login em /auth/login", response.getBody());
        Mockito.verifyNoInteractions(balancaService);
    }

    @Test
    public void deveRetornar401QuandoTokenExpirado() {
        String token = "token-expirado";
        AuthController.tokenStore.put(token, System.currentTimeMillis() - 1000);

        TransacaoTransporteRequest request = new TransacaoTransporteRequest();

        ResponseEntity<?> response = pesagemController.receber("Bearer " + token, "key-123", request);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Mockito.verifyNoInteractions(balancaService);
    }
}