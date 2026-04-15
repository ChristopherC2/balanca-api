package com.serasa.balanca.mapper;

import com.serasa.balanca.model.entities.TransacaoTransporte;
import com.serasa.balanca.model.response.TransacaoTransporteResponse;
import org.springframework.stereotype.Component;

@Component
public class TransacaoTransporteMapper {

    public TransacaoTransporteResponse toResponse(TransacaoTransporte t) {
        if (t == null) return null;

        return new TransacaoTransporteResponse(
                t.getCaminhao().getPlaca(),
                t.getFilial().getNome(),
                t.getTipoGrao().getNome(),
                t.getBalanca().getId(),
                t.getPesoBruto(),
                t.getTara(),
                t.getPesoLiquido(),
                t.getCusto(),
                t.getLucro(),
                t.getDataInicio(),
                t.getDataFim()
        );
    }
}
