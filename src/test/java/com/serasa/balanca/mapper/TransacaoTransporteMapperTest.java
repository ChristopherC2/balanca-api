package com.serasa.balanca.mapper;

import com.serasa.balanca.model.entities.Balanca;
import com.serasa.balanca.model.entities.Caminhao;
import com.serasa.balanca.model.entities.Filial;
import com.serasa.balanca.model.entities.TipoGrao;
import com.serasa.balanca.model.entities.TransacaoTransporte;
import com.serasa.balanca.model.response.TransacaoTransporteResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;

public class TransacaoTransporteMapperTest {

    private TransacaoTransporteMapper mapper;

    @Before
    public void setup() {
        mapper = new TransacaoTransporteMapper();
    }

    @Test
    public void deveMapearEntidadeParaResponseComSucesso() {
        Filial filial = Filial.builder().nome("Filial MA").build();
        TipoGrao grao = TipoGrao.builder().nome("Soja").build();
        Balanca balanca = Balanca.builder().id("BAL-01").build();
        Caminhao caminhao = Caminhao.builder().placa("ABC1234").build();

        LocalDateTime agora = LocalDateTime.now();

        TransacaoTransporte transacao = TransacaoTransporte.builder()
                .caminhao(caminhao)
                .filial(filial)
                .tipoGrao(grao)
                .balanca(balanca)
                .pesoBruto(50000.0)
                .tara(15000.0)
                .pesoLiquido(35000.0)
                .custo(87500.0)
                .lucro(13125.0)
                .dataInicio(agora.minusMinutes(5))
                .dataFim(agora)
                .build();

        TransacaoTransporteResponse response = mapper.toResponse(transacao);

        Assert.assertNotNull(response);
        Assert.assertEquals("ABC1234", response.placaCaminhao());
        Assert.assertEquals("Filial MA", response.nomeFilial());
        Assert.assertEquals("Soja", response.nomeTipoGrao());
        Assert.assertEquals("BAL-01", response.idBalanca());
        Assert.assertEquals(Double.valueOf(50000.0), response.pesoBruto());
        Assert.assertEquals(Double.valueOf(35000.0), response.pesoLiquido());
        Assert.assertEquals(Double.valueOf(13125.0), response.lucro());
        Assert.assertEquals(agora, response.dataFim());
    }

    @Test
    public void deveRetornarNullQuandoEntidadeForNull() {
        TransacaoTransporteResponse response = mapper.toResponse(null);
        Assert.assertNull(response);
    }
}
