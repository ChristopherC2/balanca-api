package com.serasa.balanca.model.response;

import java.time.LocalDateTime;

public record TransacaoTransporteResponse(
        String placaCaminhao,
        String nomeFilial,
        String nomeTipoGrao,
        String idBalanca,
        Double pesoBruto,
        Double tara,
        Double pesoLiquido,
        Double custo,
        Double lucro,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {}
