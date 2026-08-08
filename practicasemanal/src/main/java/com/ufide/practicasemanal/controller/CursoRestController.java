package com.ufide.practicasemanal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ufide.practicasemanal.entity.Curso;
import com.ufide.practicasemanal.service.CursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cursos")
public class CursoRestController {

    private final CursoService cursoService;

    public CursoRestController(CursoService cursoService) {

        this.cursoService = cursoService;
    }

    @GetMapping
    public List<Curso> listar() {

        return cursoService.listarConProfesor();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> detalle(
            @PathVariable Long id) {

        return cursoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Curso> crear(
            @Valid @RequestBody Curso curso) {

        curso.setId(null);

        Curso guardado = cursoService.guardar(curso);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(guardado.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(guardado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Curso> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Curso curso) {

        if (cursoService.buscarPorId(id).isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        curso.setId(id);

        Curso actualizado = cursoService.guardar(curso);

        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (cursoService.buscarPorId(id).isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        cursoService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}