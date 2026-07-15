package com.ufide.practicasemanal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.practicasemanal.entity.Curso;

public interface CursoRepository
        extends JpaRepository<Curso, Long> {
}
