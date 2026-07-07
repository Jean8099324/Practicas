package com.ufide.eventapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.eventapp.entity.Evento;
import com.ufide.eventapp.service.EventoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("eventos", service.listar());
        return "eventos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("evento", new Evento());
        return "eventos/form";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("evento") Evento evento,
            BindingResult result,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            return "eventos/form";
        }

        service.guardar(evento);

        ra.addFlashAttribute("ok", "Evento guardado correctamente");

        return "redirect:/eventos";
    }

    @GetMapping("/categoria/{categoria}")
    public String listarPorCategoria(
            @PathVariable("categoria") String categoria,
            Model model) {

        model.addAttribute(
                "eventos",
                service.buscarPorCategoria(categoria));

        return "eventos";
    }

    @GetMapping("/{id:[0-9]+}")
    public String detalle(
            @PathVariable("id") Long id,
            Model model) {

        Evento evento = service.buscarPorId(id).orElse(null);

        if (evento == null) {
            return "redirect:/eventos";
        }

        model.addAttribute("evento", evento);

        return "evento";
    }

    @GetMapping("/{id:[0-9]+}/editar")
    public String editar(
            @PathVariable("id") Long id,
            Model model) {

        Evento evento = service.buscarPorId(id).orElse(null);

        if (evento == null) {
            return "redirect:/eventos";
        }

        model.addAttribute("evento", evento);

        return "eventos/form";
    }

    @PostMapping("/{id:[0-9]+}")
    public String actualizar(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("evento") Evento evento,
            BindingResult result,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            return "eventos/form";
        }

        evento.setId(id);

        service.guardar(evento);

        ra.addFlashAttribute("ok", "Evento actualizado correctamente");

        return "redirect:/eventos";
    }

    @PostMapping("/{id:[0-9]+}/eliminar")
    public String eliminar(
            @PathVariable("id") Long id,
            RedirectAttributes ra) {

        service.eliminar(id);

        ra.addFlashAttribute("ok", "Evento eliminado correctamente");

        return "redirect:/eventos";
    }

    @GetMapping("/categoria")
    public String buscarCategoria(
            @RequestParam("categoria") String categoria,
            Model model) {

        model.addAttribute(
                "eventos",
                service.buscarPorCategoria(categoria));

        return "eventos";
    }
}
