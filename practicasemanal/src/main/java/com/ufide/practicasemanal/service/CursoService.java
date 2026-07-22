package com.ufide.practicasemanal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ufide.practicasemanal.entity.Curso;
import com.ufide.practicasemanal.repository.CursoRepository;

@Service
public class CursoService {

    private final CursoRepository repo;

    public CursoService(CursoRepository repo) {
        this.repo = repo;
    }

    public List<Curso> listar() {
        return repo.findAll();
    }

    public Optional<Curso> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Curso guardar(Curso curso) {
        return repo.save(curso);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}