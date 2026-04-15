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
                .id(request.id())
                .modelo(request.modelo())
                .localizacao(request.localizacao())
                .build();

        Balanca salva = balancaRepo.save(balanca);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new BalancaResponse(salva.getId(), salva.getModelo(), salva.getLocalizacao())
        );
    }

    @PostMapping("/filiais")
    public ResponseEntity<FilialResponse> cadastrarFilial(@RequestBody FilialRequest request) {
        Filial filial = Filial.builder()
                .nome(request.nome())
                .cidade(request.cidade())
                .build();

        Filial salva = filialRepo.save(filial);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new FilialResponse(salva.getId(), salva.getNome(), salva.getCidade())
        );
    }
}
