package com.serasa.balanca.controller;

import com.serasa.balanca.model.entities.TipoGrao;
import com.serasa.balanca.model.requests.TipoGraoRequest;
import com.serasa.balanca.model.response.TipoGraoResponse;
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

@RunWith(MockitoJUnitRunner.class)
public class ProdutoControllerTest {

    @InjectMocks
    private ProdutoController produtoController;

    @Mock
    private TipoGraoRepository graoRepo;

    @Test
    public void deveCadastrarGraoComSucesso() {
        TipoGraoRequest request = new TipoGraoRequest();
        request.setNome("Milho");
        request.setPrecoPorKg(1.80);

        TipoGrao graoSalvo = TipoGrao.builder()
                .id(1L)
                .nome("Milho")
                .precoPorKg(1.80)
                .build();

        Mockito.when(graoRepo.save(Mockito.any(TipoGrao.class))).thenReturn(graoSalvo);

        ResponseEntity<TipoGraoResponse> response = produtoController.cadastrarGrao(request);

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(Long.valueOf(1L), response.getBody().getId());
        Assert.assertEquals("Milho", response.getBody().getNome());
        Assert.assertEquals(Double.valueOf(1.80), response.getBody().getPrecoPorKg());

        Mockito.verify(graoRepo, Mockito.times(1)).save(Mockito.any(TipoGrao.class));
    }
}