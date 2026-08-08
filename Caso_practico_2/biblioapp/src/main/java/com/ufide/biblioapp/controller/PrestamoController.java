package com.ufide.biblioapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.service.LibroService;
import com.ufide.biblioapp.service.PrestamoService;
import com.ufide.biblioapp.service.UsuarioService;

@Controller
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/prestamos")
    public String listar(Model model) {
        model.addAttribute("prestamos", prestamoService.listarTodos());
        return "prestamos";
    }

    @GetMapping("/prestamos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("libros", libroService.listar());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "prestamo-form";
    }

    @PostMapping("/prestamos")
    public String registrar(
            @RequestParam Long libroId,
            @RequestParam Long usuarioId) {

        Libro libro = libroService.buscarPorId(libroId).orElse(null);
        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        if (libro == null || usuario == null) {
            return "redirect:/prestamos/nuevo";
        }

        prestamoService.registrarPrestamo(libro, usuario);

        return "redirect:/prestamos";
    }

    @PostMapping("/prestamos/{id}/devolver")
    public String devolver(@PathVariable Long id) {

        prestamoService.registrarDevolucion(id);

        return "redirect:/prestamos";
    }
}