package com.serasa.balanca.controller;

import com.serasa.balanca.model.requests.TransacaoTransporteRequest;
import com.serasa.balanca.service.BalancaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pesagens")
@RequiredArgsConstructor
@Tag(name = "Operacional", description = "Endpoints para o hardware ESP32")
public class PesagemController {
    private final BalancaService balancaService;

    @PostMapping("/pesagem")
    public ResponseEntity<?> receber(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("Idempotency-Key") String key,
            @RequestBody TransacaoTransporteRequest request) {

        String token = authHeader.replace("Bearer ", "");
        Long expiry = AuthController.tokenStore.get(token);

        if (expiry == null || System.currentTimeMillis() > expiry) {
            return ResponseEntity.status(401).body("Token inválido ou expirado. Faça login em /auth/login");
        }

        balancaService.processarComIdempotencia(request, key);

        return ResponseEntity.ok().build();
    }
}
