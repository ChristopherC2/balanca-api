package com.serasa.balanca.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoTransporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Caminhao caminhao;
    @ManyToOne
    private Filial filial;
    @ManyToOne
    private TipoGrao tipoGrao;
    @ManyToOne
    private Balanca balanca;
    private Double pesoBruto;
    private Double tara;
    private Double pesoLiquido;
    private Double custo;
    private Double lucro;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}