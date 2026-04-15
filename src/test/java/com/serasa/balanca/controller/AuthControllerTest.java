package com.serasa.balanca.controller;

import com.serasa.balanca.config.BalancaProperties;
import com.serasa.balanca.exception.AcessoNegadoException;
import com.serasa.balanca.model.requests.LoginRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private BalancaProperties props;

    @Before
    public void setup() {
        AuthController.tokenStore.clear();
        Mockito.when(props.getAuth()).thenReturn(new BalancaProperties.Auth());
    }

    @Test
    public void deveRetornarTokenQuandoCredenciaisForemValidas() {
        LoginRequest request = new LoginRequest("balanca_admin", "serasa123");

        Map<String, Object> response = authController.login(request);

        Assert.assertNotNull(response.get("token"));
        Assert.assertEquals("Bearer", response.get("type"));
        Assert.assertEquals(600L, response.get("expires_in"));

        String tokenGerado = (String) response.get("token");
        Assert.assertTrue(AuthController.tokenStore.containsKey(tokenGerado));
    }

    @Test(expected = AcessoNegadoException.class)
    public void deveLancarExcecaoQuandoSenhaForInvalida() {
        LoginRequest request = new LoginRequest("balanca_admin", "senha_errada");
        authController.login(request);
    }

    @Test(expected = AcessoNegadoException.class)
    public void deveLancarExcecaoQuandoUsuarioForInvalido() {
        LoginRequest request = new LoginRequest("usuario_errado", "serasa123");
        authController.login(request);
    }
}
