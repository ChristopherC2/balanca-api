package com.serasa.balanca.controller;

import com.serasa.balanca.model.entities.Balanca;
import com.serasa.balanca.model.entities.Filial;
import com.serasa.balanca.model.requests.BalancaRequest;
import com.serasa.balanca.model.requests.FilialRequest;
import com.serasa.balanca.model.response.BalancaResponse;
import com.serasa.balanca.model.response.FilialResponse;
import com.serasa.balanca.repository.BalancaRepository;
import com.serasa.balanca.repository.FilialRepository;
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
public class UnidadeControllerTest {

    @InjectMocks
    private UnidadeController unidadeController;

    @Mock
    private FilialRepository filialRepo;

    @Mock
    private BalancaRepository balancaRepo;

    @Test
    public void deveCadastrarBalancaComSucesso() {
        BalancaRequest request = new BalancaRequest();
        request.setId("BAL-01");
        request.setModelo("Toledo");
        request.setLocalizacao("Docas");

        Balanca balancaSalva = Balanca.builder()
                .id("BAL-01")
                .modelo("Toledo")
                .localizacao("Docas")
                .build();

        Mockito.when(balancaRepo.save(Mockito.any(Balanca.class))).thenReturn(balancaSalva);

        ResponseEntity<BalancaResponse> response = unidadeController.cadastrarBalanca(request);

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals("BAL-01", response.getBody().getId());
        Mockito.verify(balancaRepo, Mockito.times(1)).save(Mockito.any(Balanca.class));
    }

    @Test
    public void deveCadastrarFilialComSucesso() {
        FilialRequest request = new FilialRequest();
        request.setNome("Filial Norte");

        Filial filialSalva = Filial.builder()
                .id(1L)
                .nome("Filial Norte")
                .build();

        Mockito.when(filialRepo.save(Mockito.any(Filial.class))).thenReturn(filialSalva);

        ResponseEntity<FilialResponse> response = unidadeController.cadastrarFilial(request);

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(Long.valueOf(1L), response.getBody().getId());
        Assert.assertEquals("Filial Norte", response.getBody().getNome());
        Mockito.verify(filialRepo, Mockito.times(1)).save(Mockito.any(Filial.class));
    }
}