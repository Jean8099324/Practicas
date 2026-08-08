package com.ufide.practicasemanal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ufide.practicasemanal.entity.Curso;

public interface CursoRepository
                extends JpaRepository<Curso, Long> {

        @Query("""
                        SELECT DISTINCT c
                        FROM Curso c
                        JOIN FETCH c.profesor
                        ORDER BY c.id
                        """)
        List<Curso> findAllConProfesor();
}





