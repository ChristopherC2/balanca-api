package com.serasa.balanca.repository;

import com.serasa.balanca.model.entities.Caminhao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaminhaoRepository extends JpaRepository<Caminhao, String> {
}