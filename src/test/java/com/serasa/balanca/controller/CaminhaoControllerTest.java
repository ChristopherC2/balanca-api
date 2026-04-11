package com.serasa.balanca.controller;

import com.serasa.balanca.model.entities.Caminhao;
import com.serasa.balanca.model.entities.Filial;
import com.serasa.balanca.model.entities.TipoGrao;
import com.serasa.balanca.model.requests.CaminhaoRequest;
import com.serasa.balanca.model.response.CaminhaoResponse;
import com.serasa.balanca.repository.CaminhaoRepository;
import com.serasa.balanca.repository.FilialRepository;
import com.serasa.balanca.repository.TipoGraoRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CaminhaoControllerTest {

    @InjectMocks
    private CaminhaoController caminhaoController;

    @Mock
    private TipoGraoRepository graoRepo;
    @Mock
    private FilialRepository filialRepo;
    @Mock
    private CaminhaoRepository caminhaoRepo;

    @Test
    public void deveCadastrarCaminhaoComSucesso() {

        CaminhaoRequest request = new CaminhaoRequest();
        request.setPlaca("ABC1234");
        request.setTara(15000.0);
        request.setFilialId(1L);
        request.setGraoId(1L);

        Filial filialMock = Filial.builder().id(1L).nome("Filial Sao Luis").build();
        TipoGrao graoMock = TipoGrao.builder().id(1L).nome("Soja").build();

        Caminhao caminhaoSalvo = Caminhao.builder()
                .placa("ABC1234")
                .tara(15000.0)
                .filialPadrao(filialMock)
                .graoPadrao(graoMock)
                .build();

        Mockito.when(filialRepo.findById(1L)).thenReturn(Optional.of(filialMock));
        Mockito.when(graoRepo.findById(1L)).thenReturn(Optional.of(graoMock));
        Mockito.when(caminhaoRepo.save(Mockito.any(Caminhao.class))).thenReturn(caminhaoSalvo);

        ResponseEntity<CaminhaoResponse> response = caminhaoController.cadastrarCaminhao(request);

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals("ABC1234", response.getBody().getPlaca());
        Assert.assertEquals("Filial Sao Luis", response.getBody().getFilialNome());
        Assert.assertEquals("Soja", response.getBody().getGraoNome());
    }

    @Test(expected = ResponseStatusException.class)
    public void deveLancarExcecaoQuandoFilialNaoExistir() {

        CaminhaoRequest request = new CaminhaoRequest();
        request.setFilialId(99L);

        Mockito.when(filialRepo.findById(99L)).thenReturn(Optional.empty());

        caminhaoController.cadastrarCaminhao(request);
    }
}