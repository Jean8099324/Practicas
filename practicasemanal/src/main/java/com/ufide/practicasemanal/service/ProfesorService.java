package com.ufide.practicasemanal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufide.practicasemanal.entity.Profesor;
import com.ufide.practicasemanal.repository.ProfesorRepository;

@Service
public class ProfesorService {

    private final ProfesorRepository profesorRepository;

    public ProfesorService(
            ProfesorRepository profesorRepository) {

        this.profesorRepository = profesorRepository;
    }

    public List<Profesor> listar() {
        return profesorRepository.findAll();
    }

    public Optional<Profesor> buscarPorId(Long id) {
        return profesorRepository.findById(id);
    }

    @Transactional
    public Profesor guardar(Profesor profesor) {
        return profesorRepository.save(profesor);
    }

    @Transactional
    public void eliminar(Long id) {
        profesorRepository.deleteById(id);
    }
}