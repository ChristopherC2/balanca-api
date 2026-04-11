package com.serasa.balanca.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilialResponse {
    private Long id;
    private String nome;
    private String cidade;
}