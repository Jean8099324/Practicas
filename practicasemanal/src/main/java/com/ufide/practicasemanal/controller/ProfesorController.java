package com.ufide.practicasemanal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ufide.practicasemanal.service.ProfesorService;

@Controller
@RequestMapping("/profesores")
public class ProfesorController {

    private final ProfesorService profesorService;

    public ProfesorController(
            ProfesorService profesorService) {

        this.profesorService = profesorService;
    }

    @GetMapping
    public String listar(Model modelo) {

        modelo.addAttribute(
                "profesores",
                profesorService.listar());

        return "profesores";
    }
}