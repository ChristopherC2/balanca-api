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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        CaminhaoRequest request = new CaminhaoRequest("ABC1234", 15000.0, 1L, 1L);

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
        Assert.assertEquals("ABC1234", response.getBody().placa());
        Assert.assertEquals("Filial Sao Luis", response.getBody().filialNome());
        Assert.assertEquals("Soja", response.getBody().graoNome());
    }

    @Test(expected = RecursoNaoEncontradoException.class)
    public void deveLancarExcecaoQuandoFilialNaoExistir() {
        CaminhaoRequest request = new CaminhaoRequest(null, null, 99L, null);

        Mockito.when(filialRepo.findById(99L)).thenReturn(Optional.empty());

        caminhaoController.cadastrarCaminhao(request);
    }
}
