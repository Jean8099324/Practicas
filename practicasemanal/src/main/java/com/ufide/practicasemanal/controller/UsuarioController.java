package com.ufide.practicasemanal.controller;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.practicasemanal.entity.Usuario;
import com.ufide.practicasemanal.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todos los usuarios.
     */
    @GetMapping
    public String listar(Model modelo) {

        modelo.addAttribute(
                "usuarios",
                usuarioService.listar());

        return "usuarios";
    }

    /**
     * Muestra el formulario para registrar un usuario nuevo.
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model modelo) {

        Usuario usuario = new Usuario();
        usuario.setRol("USER");

        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("modoEdicion", false);

        return "usuarios/formulario";
    }

    /**
     * Guarda un usuario nuevo.
     */
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult resultado,
            Model modelo,
            RedirectAttributes redirectAttributes) {

        normalizarDatos(usuario);

        validarDuplicadosAlCrear(usuario, resultado);

        if (resultado.hasErrors()) {

            modelo.addAttribute("modoEdicion", false);

            return "usuarios/formulario";
        }

        try {

            usuarioService.guardar(usuario);

            redirectAttributes.addFlashAttribute(
                    "ok",
                    "Usuario creado correctamente.");

            return "redirect:/usuarios";

        } catch (IllegalArgumentException exception) {

            resultado.reject(
                    "usuario.error",
                    exception.getMessage());

            modelo.addAttribute("modoEdicion", false);

            return "usuarios/formulario";

        } catch (Exception exception) {

            resultado.reject(
                    "usuario.error",
                    "No fue posible guardar el usuario.");

            modelo.addAttribute("modoEdicion", false);

            return "usuarios/formulario";
        }
    }

    /**
     * Muestra el formulario para editar un usuario.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model modelo,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        if (usuarioOptional.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario solicitado no existe.");

            return "redirect:/usuarios";
        }

        Usuario usuario = usuarioOptional.get();

        /*
         * No se muestra el hash BCrypt en el formulario.
         * Si la contraseña se deja vacía, el servicio conserva
         * la contraseña existente.
         */
        usuario.setPassword("");

        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("modoEdicion", true);

        return "usuarios/formulario";
    }

    /**
     * Actualiza un usuario existente.
     */
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @ModelAttribute("usuario") Usuario usuario,
            BindingResult resultado,
            Model modelo,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioExistenteOptional = usuarioService.buscarPorId(id);

        if (usuarioExistenteOptional.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario solicitado no existe.");

            return "redirect:/usuarios";
        }

        normalizarDatos(usuario);

        validarCamposActualizacion(usuario, resultado);

        validarDuplicadosAlEditar(
                id,
                usuario,
                resultado);

        if (resultado.hasErrors()) {

            usuario.setId(id);

            modelo.addAttribute("modoEdicion", true);

            return "usuarios/formulario";
        }

        try {

            usuarioService.actualizar(id, usuario);

            redirectAttributes.addFlashAttribute(
                    "ok",
                    "Usuario actualizado correctamente.");

            return "redirect:/usuarios";

        } catch (IllegalArgumentException exception) {

            resultado.reject(
                    "usuario.error",
                    exception.getMessage());

            usuario.setId(id);

            modelo.addAttribute("modoEdicion", true);

            return "usuarios/formulario";

        } catch (Exception exception) {

            resultado.reject(
                    "usuario.error",
                    "No fue posible actualizar el usuario.");

            usuario.setId(id);

            modelo.addAttribute("modoEdicion", true);

            return "usuarios/formulario";
        }
    }

    /**
     * Elimina un usuario.
     *
     * Se impide eliminar la misma cuenta que mantiene
     * la sesión iniciada.
     */
    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        if (usuarioOptional.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario solicitado no existe.");

            return "redirect:/usuarios";
        }

        Usuario usuario = usuarioOptional.get();

        if (authentication != null
                && authentication.isAuthenticated()
                && usuario.getUsername()
                        .equals(authentication.getName())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No puedes eliminar tu propio usuario "
                            + "mientras tienes la sesión iniciada.");

            return "redirect:/usuarios";
        }

        try {

            usuarioService.eliminar(id);

            redirectAttributes.addFlashAttribute(
                    "ok",
                    "Usuario eliminado correctamente.");

        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    exception.getMessage());

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No fue posible eliminar el usuario.");
        }

        return "redirect:/usuarios";
    }

    /**
     * Elimina espacios innecesarios antes de validar.
     */
    private void normalizarDatos(Usuario usuario) {

        if (usuario.getUsername() != null) {
            usuario.setUsername(
                    usuario.getUsername().trim());
        }

        if (usuario.getEmail() != null) {
            usuario.setEmail(
                    usuario.getEmail()
                            .trim()
                            .toLowerCase());
        }

        if (usuario.getRol() != null) {
            usuario.setRol(
                    usuario.getRol()
                            .trim()
                            .toUpperCase());
        }
    }

    /**
     * Valida duplicados al registrar.
     */
    private void validarDuplicadosAlCrear(
            Usuario usuario,
            BindingResult resultado) {

        if (usuario.getUsername() != null
                && !usuario.getUsername().isBlank()
                && usuarioService.existeUsername(
                        usuario.getUsername())) {

            resultado.rejectValue(
                    "username",
                    "username.duplicado",
                    "Ya existe un usuario con ese username.");
        }

        if (usuario.getEmail() != null
                && !usuario.getEmail().isBlank()
                && usuarioService.existeEmail(
                        usuario.getEmail())) {

            resultado.rejectValue(
                    "email",
                    "email.duplicado",
                    "Ya existe un usuario con ese correo.");
        }
    }

    /**
     * Valida los campos requeridos durante la edición.
     *
     * La contraseña no es obligatoria al editar porque,
     * si queda vacía, se conserva la actual.
     */
    private void validarCamposActualizacion(
            Usuario usuario,
            BindingResult resultado) {

        if (usuario.getUsername() == null
                || usuario.getUsername().isBlank()) {

            resultado.rejectValue(
                    "username",
                    "username.obligatorio",
                    "El username es obligatorio.");
        }

        if (usuario.getEmail() == null
                || usuario.getEmail().isBlank()) {

            resultado.rejectValue(
                    "email",
                    "email.obligatorio",
                    "El correo electrónico es obligatorio.");
        }

        if (usuario.getRol() == null
                || usuario.getRol().isBlank()) {

            resultado.rejectValue(
                    "rol",
                    "rol.obligatorio",
                    "El rol es obligatorio.");
        }

        if (usuario.getRol() != null
                && !usuario.getRol().isBlank()
                && !usuario.getRol().equals("ADMIN")
                && !usuario.getRol().equals("USER")) {

            resultado.rejectValue(
                    "rol",
                    "rol.invalido",
                    "El rol debe ser ADMIN o USER.");
        }

        if (usuario.getPassword() != null
                && !usuario.getPassword().isBlank()
                && usuario.getPassword().length() < 8) {

            resultado.rejectValue(
                    "password",
                    "password.longitud",
                    "La contraseña debe tener al menos 8 caracteres.");
        }
    }

    /**
     * Valida que el username y el email no pertenezcan
     * a otro usuario durante la edición.
     */
    private void validarDuplicadosAlEditar(
            Long id,
            Usuario usuario,
            BindingResult resultado) {

        if (usuario.getUsername() != null
                && !usuario.getUsername().isBlank()) {

            Optional<Usuario> usuarioPorUsername = usuarioService.buscarPorUsername(
                    usuario.getUsername());

            if (usuarioPorUsername.isPresent()
                    && !usuarioPorUsername.get()
                            .getId()
                            .equals(id)) {

                resultado.rejectValue(
                        "username",
                        "username.duplicado",
                        "Ya existe otro usuario con ese username.");
            }
        }

        if (usuario.getEmail() != null
                && !usuario.getEmail().isBlank()) {

            Optional<Usuario> usuarioPorEmail = usuarioService.buscarPorEmail(
                    usuario.getEmail());

            if (usuarioPorEmail.isPresent()
                    && !usuarioPorEmail.get()
                            .getId()
                            .equals(id)) {

                resultado.rejectValue(
                        "email",
                        "email.duplicado",
                        "Ya existe otro usuario con ese correo.");
            }
        }
    }
}