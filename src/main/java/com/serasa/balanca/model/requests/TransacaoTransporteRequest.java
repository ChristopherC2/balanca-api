package com.serasa.balanca.model.requests;
import lombok.Data;

@Data
public class TransacaoTransporteRequest {
    private String balancaId;
    private String plate;  // placa
    private Double weight; // peso
}