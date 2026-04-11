package com.serasa.balanca.repository;

import com.serasa.balanca.model.entities.TransacaoTransporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoTransporteRepository extends JpaRepository<TransacaoTransporte, Long> {

    List<TransacaoTransporte> findByDataFimBetween(LocalDateTime inicio, LocalDateTime fim);

    List<TransacaoTransporte> findByFilialNomeAndDataFimBetween(String nome, LocalDateTime inicio, LocalDateTime fim);

    List<TransacaoTransporte> findByCaminhaoPlacaAndDataFimBetween(String placa, LocalDateTime inicio, LocalDateTime fim);

    List<TransacaoTransporte> findByTipoGraoNomeAndDataFimBetween(String nome, LocalDateTime inicio, LocalDateTime fim);
}