package com.ufide.practicasemanal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ufide.practicasemanal.entity.Curso;
import com.ufide.practicasemanal.service.CursoService;

@Controller
@RequestMapping("/cursos")
public class CursoController {

        private final CursoService cursoService;

        public CursoController(CursoService cursoService) {
                this.cursoService = cursoService;
        }

        @GetMapping
        public String listar(Model modelo) {
                modelo.addAttribute("cursos", cursoService.listar());
                return "cursos";
        }

        @GetMapping("/{id}")
        public String detalle(@PathVariable Long id, Model modelo) {

                Curso encontrado = cursoService.buscarPorId(id).orElse(null);

                modelo.addAttribute("curso", encontrado);

                return "curso";
        }
}