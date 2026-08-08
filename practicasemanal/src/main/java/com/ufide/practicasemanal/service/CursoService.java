package com.ufide.practicasemanal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufide.practicasemanal.entity.Curso;
import com.ufide.practicasemanal.repository.CursoRepository;

@Service
public class CursoService {

    private final CursoRepository repo;

    public CursoService(CursoRepository repo) {
        this.repo = repo;
    }

    /*
     * Lista los cursos junto con su profesor.
     */
    @Transactional(readOnly = true)
    public List<Curso> listar() {

        return repo.findAllConProfesor();
    }

    /*
     * Método usado específicamente por el controlador REST.
     */
    @Transactional(readOnly = true)
    public List<Curso> listarConProfesor() {

        return repo.findAllConProfesor();
    }

    /*
     * Busca un curso por su ID.
     */
    @Transactional(readOnly = true)
    public Optional<Curso> buscarPorId(Long id) {

        if (id == null) {
            return Optional.empty();
        }

        return repo.findById(id);
    }

    /*
     * Guarda un curso nuevo o actualiza uno existente.
     */
    @Transactional
    public Curso guardar(Curso curso) {

        if (curso == null) {

            throw new IllegalArgumentException(
                    "El curso no puede ser nulo.");
        }

        return repo.save(curso);
    }

    /*
     * Elimina un curso por ID.
     */
    @Transactional
    public void eliminar(Long id) {

        if (id == null) {

            throw new IllegalArgumentException(
                    "El ID del curso es obligatorio.");
        }

        if (!repo.existsById(id)) {

            throw new IllegalArgumentException(
                    "El curso con ID " + id + " no existe.");
        }

        repo.deleteById(id);
    }
}