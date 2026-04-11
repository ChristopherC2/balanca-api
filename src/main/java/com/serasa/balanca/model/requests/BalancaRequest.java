package com.serasa.balanca.model.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalancaRequest {
    private String id;
    private String modelo;
    private String localizacao;
}