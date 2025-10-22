package com.Santander.desafio.repository;

import com.Santander.desafio.model.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, Long> {
    // JpaRepository já fornece findAll(), save(), etc.
}
