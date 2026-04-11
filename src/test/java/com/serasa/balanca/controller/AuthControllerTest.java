package com.serasa.balanca.controller;

import com.serasa.balanca.model.requests.LoginRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

public class AuthControllerTest {

    private AuthController authController;

    @Before
    public void setup() {
        authController = new AuthController();
        AuthController.tokenStore.clear();
    }

    @Test
    public void deveRetornarTokenQuandoCredenciaisForemValidas() {

        LoginRequest request = new LoginRequest();
        request.setUsername("balanca_admin");
        request.setPassword("serasa123");

        Map<String, Object> response = authController.login(request);

        Assert.assertNotNull(response.get("token"));
        Assert.assertEquals("Bearer", response.get("type"));
        Assert.assertEquals(600, response.get("expires_in"));

        String tokenGerado = (String) response.get("token");
        Assert.assertTrue(AuthController.tokenStore.containsKey(tokenGerado));
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoQuandoSenhaForInvalida() {

        LoginRequest request = new LoginRequest();
        request.setUsername("balanca_admin");
        request.setPassword("senha_errada");

        authController.login(request);
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoQuandoUsuarioForInvalido() {
        LoginRequest request = new LoginRequest();
        request.setUsername("usuario_errado");
        request.setPassword("serasa123");

        authController.login(request);
    }
}