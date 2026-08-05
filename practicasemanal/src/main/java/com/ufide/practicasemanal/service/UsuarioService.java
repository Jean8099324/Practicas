package com.ufide.practicasemanal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufide.practicasemanal.entity.Usuario;
import com.ufide.practicasemanal.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final int MINUTOS_VALIDEZ_TOKEN = 30;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {

        if (id == null) {
            return Optional.empty();
        }

        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(
            String username) {

        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository.findByUsername(
                username.trim());
    }

    public Optional<Usuario> buscarPorEmail(
            String email) {

        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository.findByEmail(
                email.trim().toLowerCase());
    }

    public Optional<Usuario> buscarPorResetToken(
            String token) {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository.findByResetToken(
                token.trim());
    }

    public boolean existeUsername(String username) {

        if (username == null || username.isBlank()) {
            return false;
        }

        return usuarioRepository.existsByUsername(
                username.trim());
    }

    public boolean existeEmail(String email) {

        if (email == null || email.isBlank()) {
            return false;
        }

        return usuarioRepository.existsByEmail(
                email.trim().toLowerCase());
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {

        validarUsuarioNuevo(usuario);

        usuario.setUsername(
                usuario.getUsername().trim());

        usuario.setEmail(
                usuario.getEmail()
                        .trim()
                        .toLowerCase());

        usuario.setRol(
                normalizarRol(usuario.getRol()));

        if (!esPasswordBCrypt(usuario.getPassword())) {

            usuario.setPassword(
                    passwordEncoder.encode(
                            usuario.getPassword()));
        }

        usuario.setResetToken(null);
        usuario.setResetTokenExpiracion(null);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(
            Long id,
            Usuario datosUsuario) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio.");
        }

        if (datosUsuario == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios.");
        }

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario con ID "
                                + id
                                + " no existe."));

        if (datosUsuario.getUsername() == null
                || datosUsuario.getUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "El username es obligatorio.");
        }

        if (datosUsuario.getEmail() == null
                || datosUsuario.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "El correo es obligatorio.");
        }

        usuarioExistente.setUsername(
                datosUsuario.getUsername().trim());

        usuarioExistente.setEmail(
                datosUsuario.getEmail()
                        .trim()
                        .toLowerCase());

        usuarioExistente.setRol(
                normalizarRol(datosUsuario.getRol()));

        if (datosUsuario.getPassword() != null
                && !datosUsuario.getPassword().isBlank()) {

            usuarioExistente.setPassword(
                    passwordEncoder.encode(
                            datosUsuario.getPassword()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void eliminar(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio.");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "El usuario con ID "
                            + id
                            + " no existe.");
        }

        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorTokenValido(
            String token) {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository
                .findByResetToken(token.trim())
                .filter(usuario -> usuario.getResetTokenExpiracion() != null
                        && usuario
                                .getResetTokenExpiracion()
                                .isAfter(LocalDateTime.now()));
    }

    @Transactional
    public String generarTokenRecuperacion(
            Usuario usuario) {

        if (usuario == null || usuario.getId() == null) {

            throw new IllegalArgumentException(
                    "El usuario no es válido.");
        }

        String token = UUID.randomUUID().toString();

        usuario.setResetToken(token);

        usuario.setResetTokenExpiracion(
                LocalDateTime.now()
                        .plusMinutes(
                                MINUTOS_VALIDEZ_TOKEN));

        usuarioRepository.save(usuario);

        return token;
    }

    @Transactional
    public boolean restablecerPassword(
            String token,
            String nuevaPassword) {

        if (nuevaPassword == null
                || nuevaPassword.isBlank()) {

            return false;
        }

        Optional<Usuario> usuarioOptional = buscarPorTokenValido(token);

        if (usuarioOptional.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOptional.get();

        usuario.setPassword(
                passwordEncoder.encode(nuevaPassword));

        usuario.setResetToken(null);
        usuario.setResetTokenExpiracion(null);

        usuarioRepository.save(usuario);

        return true;
    }

    private void validarUsuarioNuevo(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo.");
        }

        if (usuario.getUsername() == null
                || usuario.getUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "El username es obligatorio.");
        }

        if (usuario.getEmail() == null
                || usuario.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "El correo es obligatorio.");
        }

        if (usuario.getPassword() == null
                || usuario.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }
    }

    private String normalizarRol(String rol) {

        if (rol == null || rol.isBlank()) {
            return "USER";
        }

        String rolNormalizado = rol.trim().toUpperCase();

        if (!rolNormalizado.equals("ADMIN")
                && !rolNormalizado.equals("USER")) {

            throw new IllegalArgumentException(
                    "El rol debe ser ADMIN o USER.");
        }

        return rolNormalizado;
    }

    private boolean esPasswordBCrypt(String password) {

        if (password == null) {
            return false;
        }

        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}