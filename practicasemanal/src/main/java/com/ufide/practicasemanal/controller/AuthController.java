package com.ufide.practicasemanal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufide.practicasemanal.security.JwtService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * POST /api/auth/login
     *
     * Recibe username y password.
     * Si las credenciales son correctas, devuelve un JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        String rol = authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        String token = jwtService.generarToken(
                authentication.getName(),
                rol);

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                authentication.getName(),
                rol);

        return ResponseEntity.ok(response);
    }

    /**
     * JSON esperado:
     *
     * {
     * "username": "admin",
     * "password": "admin123"
     * }
     */
    public record LoginRequest(

            @NotBlank(message = "El username es obligatorio") String username,

            @NotBlank(message = "La contraseña es obligatoria") String password) {
    }

    /**
     * JSON devuelto:
     *
     * {
     * "token": "...",
     * "tipo": "Bearer",
     * "username": "admin",
     * "rol": "ROLE_ADMIN"
     * }
     */
    public record LoginResponse(
            String token,
            String tipo,
            String username,
            String rol) {
    }
}