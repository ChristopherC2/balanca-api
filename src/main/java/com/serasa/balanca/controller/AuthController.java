package com.serasa.balanca.controller;

import com.serasa.balanca.config.BalancaProperties;
import com.serasa.balanca.exception.AcessoNegadoException;
import com.serasa.balanca.model.requests.LoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Segurança", description = "Gerenciamento de tokens e controle de acesso")
public class AuthController {
    public static final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    private final BalancaProperties props;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest creds) {
        if ("balanca_admin".equals(creds.username()) && "serasa123".equals(creds.password())) {
            String token = UUID.randomUUID().toString();
            long expiracao = props.getAuth().getTokenExpiracaoMs();
            tokenStore.put(token, System.currentTimeMillis() + expiracao);
            long expiresInSeconds = expiracao / 1000;
            return Map.of("token", token, "type", "Bearer", "expires_in", expiresInSeconds);
        }
        throw new AcessoNegadoException("Credenciais inválidas");
    }
}
