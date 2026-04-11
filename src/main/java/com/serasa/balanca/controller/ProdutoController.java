package com.serasa.balanca.controller;

import com.serasa.balanca.model.entities.TipoGrao;
import com.serasa.balanca.model.requests.TipoGraoRequest;
import com.serasa.balanca.model.response.TipoGraoResponse;
import com.serasa.balanca.repository.TipoGraoRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
@Tag(name = "Comercial", description = "Gestão de tipos de grãos e preços")
public class ProdutoController {
    private final TipoGraoRepository graoRepo;

    @PostMapping("/graos")
    public ResponseEntity<TipoGraoResponse> cadastrarGrao(@RequestBody TipoGraoRequest request) {
        TipoGrao grao = TipoGrao.builder()
                .nome(request.getNome())
                .precoPorKg(request.getPrecoPorKg())
                .build();

        TipoGrao salvo = graoRepo.save(grao);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                TipoGraoResponse.builder()
                        .id(salvo.getId())
                        .nome(salvo.getNome())
                        .precoPorKg(salvo.getPrecoPorKg())
                        .build()
        );
    }
}
