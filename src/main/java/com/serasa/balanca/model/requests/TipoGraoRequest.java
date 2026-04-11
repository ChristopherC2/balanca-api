package com.serasa.balanca.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoGraoRequest {

    private String nome;
    private Double precoPorKg;
}