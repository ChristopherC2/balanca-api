package com.serasa.balanca.mapper;

import com.serasa.balanca.model.entities.TransacaoTransporte;
import com.serasa.balanca.model.response.TransacaoTransporteResponse;
import org.springframework.stereotype.Component;

@Component
public class TransacaoTransporteMapper {

    public TransacaoTransporteResponse toResponse(TransacaoTransporte t) {
        if (t == null) return null;

        return TransacaoTransporteResponse.builder()
                .placaCaminhao(t.getCaminhao().getPlaca())
                .nomeFilial(t.getFilial().getNome())
                .nomeTipoGrao(t.getTipoGrao().getNome())
                .idBalanca(t.getBalanca().getId())
                .pesoBruto(t.getPesoBruto())
                .tara(t.getTara())
                .pesoLiquido(t.getPesoLiquido())
                .custo(t.getCusto())
                .lucro(t.getLucro())
                .dataInicio(t.getDataInicio())
                .dataFim(t.getDataFim())
                .build();
    }
}
