package com.serasa.balanca.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoGraoResponse {

    private Long id;
    private String nome;
    private Double precoPorKg;
}