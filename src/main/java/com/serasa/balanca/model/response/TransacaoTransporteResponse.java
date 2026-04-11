package com.serasa.balanca.model.response;

import com.serasa.balanca.model.entities.Balanca;
import com.serasa.balanca.model.entities.Caminhao;
import com.serasa.balanca.model.entities.Filial;
import com.serasa.balanca.model.entities.TipoGrao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoTransporteResponse {
    private String placaCaminhao;
    private String nomeFilial;
    private String nomeTipoGrao;
    private String idBalanca;
    private Double pesoBruto;
    private Double tara;
    private Double pesoLiquido;
    private Double custo;
    private Double lucro;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}