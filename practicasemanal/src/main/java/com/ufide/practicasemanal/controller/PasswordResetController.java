package com.ufide.practicasemanal.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ufide.practicasemanal.entity.Usuario;
import com.ufide.practicasemanal.service.EmailService;
import com.ufide.practicasemanal.service.UsuarioService;

@Controller
public class PasswordResetController {

        private final UsuarioService usuarioService;
        private final EmailService emailService;

        public PasswordResetController(
                        UsuarioService usuarioService,
                        EmailService emailService) {

                this.usuarioService = usuarioService;
                this.emailService = emailService;
        }

        @GetMapping("/olvide-password")
        public String mostrarFormularioOlvidePassword() {
                return "olvide-password";
        }

        @PostMapping("/olvide-password")
        public String procesarOlvidePassword(
                        @RequestParam("email") String email,
                        RedirectAttributes redirectAttributes) {

                String mensajeGeneral = "Si el correo está registrado, recibirás un enlace "
                                + "para restablecer tu contraseña.";

                try {

                        if (email == null || email.isBlank()) {

                                redirectAttributes.addFlashAttribute(
                                                "error",
                                                "Debes ingresar un correo electrónico.");

                                return "redirect:/olvide-password";
                        }

                        Optional<Usuario> usuarioOptional = usuarioService.buscarPorEmail(
                                        email.trim().toLowerCase());

                        if (usuarioOptional.isEmpty()) {

                                redirectAttributes.addFlashAttribute(
                                                "mensaje",
                                                mensajeGeneral);

                                return "redirect:/olvide-password";
                        }

                        Usuario usuario = usuarioOptional.get();

                        String token = usuarioService.generarTokenRecuperacion(usuario);

                        String urlBase = ServletUriComponentsBuilder
                                        .fromCurrentContextPath()
                                        .build()
                                        .toUriString();

                        String enlaceRecuperacion = urlBase
                                        + "/restablecer-password?token="
                                        + token;

                        emailService.enviarLinkRecuperacion(
                                        usuario.getEmail(),
                                        enlaceRecuperacion);

                        redirectAttributes.addFlashAttribute(
                                        "mensaje",
                                        mensajeGeneral);

                } catch (Exception exception) {

                        exception.printStackTrace();

                        redirectAttributes.addFlashAttribute(
                                        "error",
                                        "No fue posible enviar el correo de recuperación. "
                                                        + "Inténtalo nuevamente.");
                }

                return "redirect:/olvide-password";
        }

        @GetMapping("/restablecer-password")
        public String mostrarFormularioRestablecerPassword(
                        @RequestParam("token") String token,
                        Model model) {

                Optional<Usuario> usuarioOptional = usuarioService.buscarPorTokenValido(token);

                if (usuarioOptional.isEmpty()) {

                        model.addAttribute(
                                        "error",
                                        "El enlace de recuperación no es válido o ya venció.");

                        return "token-invalido";
                }

                model.addAttribute("token", token);

                return "restablecer-password";
        }

        @PostMapping("/restablecer-password")
        public String procesarRestablecerPassword(
                        @RequestParam("token") String token,
                        @RequestParam("password") String password,
                        @RequestParam("confirmPassword") String confirmPassword,
                        RedirectAttributes redirectAttributes) {

                if (token == null || token.isBlank()) {

                        redirectAttributes.addFlashAttribute(
                                        "error",
                                        "El token de recuperación no es válido.");

                        return "redirect:/olvide-password";
                }

                if (password == null || password.isBlank()) {

                        redirectAttributes.addFlashAttribute(
                                        "error",
                                        "La contraseña es obligatoria.");

                        redirectAttributes.addAttribute("token", token);

                        return "redirect:/restablecer-password";
                }

                if (password.length() < 8) {

                        redirectAttributes.addFlashAttribute(
                                        "error",
                                        "La contraseña debe tener al menos 8 caracteres.");

                        redirectAttributes.addAttribute("token", token);

                        return "redirect:/restablecer-password";
                }

                if (confirmPassword == null
                                || !password.equals(confirmPassword)) {

                        redirectAttributes.addFlashAttribute(
                                        "error",
                                        "Las contraseñas no coinciden.");

                        redirectAttributes.addAttribute("token", token);

                        return "redirect:/restablecer-password";
                }

                try {

                        boolean actualizado = usuarioService.restablecerPassword(
                                        token,
                                        password);

                        if (!actualizado) {

                                redirectAttributes.addFlashAttribute(
                                                "error",
                                                "El enlace no es válido, ya fue utilizado o venció.");

                                return "redirect:/olvide-password";
                        }

                        redirectAttributes.addFlashAttribute(
                                        "mensaje",
                                        "La contraseña se actualizó correctamente. "
                                                        + "Ya puedes iniciar sesión.");

                        return "redirect:/login";

                } catch (Exception exception) {

                        exception.printStackTrace();

                        redirectAttributes.addFlashAttribute(
                                        "error",
                                        "No fue posible actualizar la contraseña.");

                        redirectAttributes.addAttribute("token", token);

                        return "redirect:/restablecer-password";
                }
        }
}