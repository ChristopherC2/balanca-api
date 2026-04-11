package com.serasa.balanca.repository;

import com.serasa.balanca.model.entities.Balanca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalancaRepository extends JpaRepository<Balanca, String> {
}