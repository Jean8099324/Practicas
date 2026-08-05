package com.ufide.practicasemanal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.practicasemanal.entity.Curso;
import com.ufide.practicasemanal.service.CursoService;
import com.ufide.practicasemanal.service.ProfesorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cursos")
public class CursoController {

        @Autowired
        private CursoService cursoService;

        @Autowired
        private ProfesorService profesorService;

        /*
         * Cualquier usuario autenticado puede listar los cursos.
         */
        @GetMapping
        public String listar(Model modelo) {

                modelo.addAttribute(
                                "cursos",
                                cursoService.listarConProfesor());

                return "cursos";
        }

        /*
         * Cualquier usuario autenticado puede ver el detalle.
         */
        @GetMapping("/{id}")
        public String detalle(
                        Model modelo,
                        @PathVariable Long id) {

                Curso encontrado = cursoService
                                .buscarPorId(id)
                                .orElse(null);

                modelo.addAttribute(
                                "curso",
                                encontrado);

                return "curso";
        }

        /*
         * Solo ADMIN puede abrir el formulario para crear.
         */
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/nuevo")
        public String mostrarFormNuevo(Model modelo) {

                modelo.addAttribute(
                                "curso",
                                new Curso());

                modelo.addAttribute(
                                "profesores",
                                profesorService.listar());

                return "cursos/form";
        }

        /*
         * Solo ADMIN puede guardar cursos nuevos.
         */
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public String guardar(
                        @Valid @ModelAttribute("curso") Curso curso,
                        BindingResult result,
                        RedirectAttributes ra,
                        Model modelo) {

                if (result.hasErrors()) {

                        modelo.addAttribute(
                                        "profesores",
                                        profesorService.listar());

                        return "cursos/form";
                }

                cursoService.guardar(curso);

                ra.addFlashAttribute(
                                "ok",
                                "Curso guardado correctamente");

                return "redirect:/cursos";
        }

        /*
         * Solo ADMIN puede abrir el formulario para editar.
         */
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/{id}/editar")
        public String mostrarFormEditar(
                        @PathVariable Long id,
                        Model modelo) {

                Curso curso = cursoService
                                .buscarPorId(id)
                                .orElseThrow();

                modelo.addAttribute(
                                "curso",
                                curso);

                modelo.addAttribute(
                                "profesores",
                                profesorService.listar());

                return "cursos/form";
        }

        /*
         * Solo ADMIN puede actualizar cursos.
         */
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/{id}")
        public String actualizar(
                        @PathVariable Long id,
                        @Valid @ModelAttribute("curso") Curso curso,
                        BindingResult result,
                        RedirectAttributes ra,
                        Model modelo) {

                if (result.hasErrors()) {

                        modelo.addAttribute(
                                        "profesores",
                                        profesorService.listar());

                        return "cursos/form";
                }

                curso.setId(id);

                cursoService.guardar(curso);

                ra.addFlashAttribute(
                                "ok",
                                "Curso actualizado correctamente");

                return "redirect:/cursos";
        }

        /*
         * Solo ADMIN puede eliminar cursos.
         *
         * hasAuthority("ROLE_ADMIN") produce el mismo resultado
         * que hasRole("ADMIN").
         */
        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
        @PostMapping("/{id}/eliminar")
        public String eliminar(
                        @PathVariable Long id,
                        RedirectAttributes ra) {

                cursoService.eliminar(id);

                ra.addFlashAttribute(
                                "ok",
                                "Curso eliminado correctamente");

                return "redirect:/cursos";
        }
}