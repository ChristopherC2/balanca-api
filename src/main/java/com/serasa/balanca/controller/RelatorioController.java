package com.serasa.balanca.controller;

import com.serasa.balanca.model.response.TransacaoTransporteResponse;
import com.serasa.balanca.service.BalancaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Consultas e estatísticas de performance")
public class RelatorioController {
    private final BalancaService balancaService;

    @GetMapping("")
    public ResponseEntity<List<TransacaoTransporteResponse>> obterRelatorio(
            @RequestParam(required = false) String filial,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String grao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        return ResponseEntity.ok(balancaService.listarRelatorio(filial, placa, grao, inicio, fim));
    }
}
