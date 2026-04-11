package com.serasa.balanca.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Caminhao {
    @Id
    private String placa;
    private Double tara;

    @ManyToOne
    private Filial filialPadrao;
    @ManyToOne
    private TipoGrao graoPadrao;
}
