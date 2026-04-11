package com.serasa.balanca.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaminhaoResponse {

    private String placa;
    private Double tara;
    private String filialNome;
    private String graoNome;
}
