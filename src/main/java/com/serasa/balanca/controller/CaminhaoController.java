package com.serasa.balanca.controller;

import com.serasa.balanca.exception.RecursoNaoEncontradoException;
import com.serasa.balanca.model.entities.Caminhao;
import com.serasa.balanca.model.entities.Filial;
import com.serasa.balanca.model.entities.TipoGrao;
import com.serasa.balanca.model.requests.CaminhaoRequest;
import com.serasa.balanca.model.response.CaminhaoResponse;
import com.serasa.balanca.repository.CaminhaoRepository;
import com.serasa.balanca.repository.FilialRepository;
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
@RequestMapping("/api/v1/caminhoes")
@RequiredArgsConstructor
@Tag(name = "Cadastros", description = "Gestão de veículos e ativos")
public class CaminhaoController {
    private final TipoGraoRepository graoRepo;
    private final FilialRepository filialRepo;
    private final CaminhaoRepository caminhaoRepo;

    @PostMapping("")
    public ResponseEntity<CaminhaoResponse> cadastrarCaminhao(@RequestBody CaminhaoRequest request) {
        Filial filial = filialRepo.findById(request.filialId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filial não encontrada: " + request.filialId()));

        TipoGrao grao = graoRepo.findById(request.graoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Grão não encontrado: " + request.graoId()));

        Caminhao caminhao = Caminhao.builder()
                .placa(request.placa())
                .tara(request.tara())
                .filialPadrao(filial)
                .graoPadrao(grao)
                .build();

        Caminhao salvo = caminhaoRepo.save(caminhao);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CaminhaoResponse(
                        salvo.getPlaca(),
                        salvo.getTara(),
                        salvo.getFilialPadrao().getNome(),
                        salvo.getGraoPadrao().getNome()
                )
        );
    }
}
