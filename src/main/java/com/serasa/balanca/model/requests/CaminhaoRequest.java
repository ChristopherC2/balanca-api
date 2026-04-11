package com.serasa.balanca.model.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaminhaoRequest {

    private String placa;
    private Double tara;
    private Long filialId;
    private Long graoId;
}
