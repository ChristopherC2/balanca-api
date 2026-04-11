package com.serasa.balanca.controller;

import com.serasa.balanca.model.entities.Balanca;
import com.serasa.balanca.model.entities.Filial;
import com.serasa.balanca.model.requests.BalancaRequest;
import com.serasa.balanca.model.requests.FilialRequest;
import com.serasa.balanca.model.response.BalancaResponse;
import com.serasa.balanca.model.response.FilialResponse;
import com.serasa.balanca.repository.BalancaRepository;
import com.serasa.balanca.repository.FilialRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/unidades")
@RequiredArgsConstructor
@Tag(name = "Infraestrutura", description = "Gestão de filiais e equipamentos")
public class UnidadeController {
    private final FilialRepository filialRepo;
    private final BalancaRepository balancaRepo;

    @PostMapping("/balancas")
    public ResponseEntity<BalancaResponse> cadastrarBalanca(@RequestBody BalancaRequest request) {

        Balanca balanca = Balanca.builder()
                .id(request.getId())
                .modelo(request.getModelo())
                .localizacao(request.getLocalizacao())
                .build();

        Balanca salva = balancaRepo.save(balanca);

        BalancaResponse response = BalancaResponse.builder()
                .id(salva.getId())
                .modelo(salva.getModelo())
                .localizacao(salva.getLocalizacao())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/filiais")
    public ResponseEntity<FilialResponse> cadastrarFilial(@RequestBody FilialRequest request) {
        Filial filial = Filial.builder()
                .nome(request.getNome())
                .build();

        Filial salva = filialRepo.save(filial);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                FilialResponse.builder()
                        .id(salva.getId())
                        .nome(salva.getNome())
                        .build()
        );
    }
}
