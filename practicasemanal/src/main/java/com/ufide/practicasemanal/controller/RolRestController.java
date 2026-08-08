package com.ufide.practicasemanal.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufide.practicasemanal.security.Rol;

@RestController
@RequestMapping("/api/roles")
public class RolRestController {

    @GetMapping
    public List<String> listar() {

        return Arrays.stream(Rol.values())
                .map(Enum::name)
                .toList();
    }

}