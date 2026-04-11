package com.serasa.balanca.controller;

import com.serasa.balanca.model.requests.LoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
@Tag(name = "Segurança", description = "Gerenciamento de tokens e controle de acesso")
public class AuthController {
    public static final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest creds) {
        if ("balanca_admin".equals(creds.getUsername()) && "serasa123".equals(creds.getPassword())) {
            String token = UUID.randomUUID().toString();
            tokenStore.put(token, System.currentTimeMillis() + (10 * 60 * 1000));
            return Map.of("token", token, "type", "Bearer", "expires_in", 600);
        }
        throw new RuntimeException("Acesso negado");
    }
}