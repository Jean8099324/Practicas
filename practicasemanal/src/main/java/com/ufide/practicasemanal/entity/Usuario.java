package com.ufide.practicasemanal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El username es obligatorio")
    @Size(max = 50, message = "El username no puede superar los 50 caracteres")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "El password es obligatorio")
    @Size(max = 255, message = "El password no puede superar los 255 caracteres")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Size(max = 20, message = "El rol no puede superar los 20 caracteres")
    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "reset_token", unique = true, length = 255)
    private String resetToken;

    @Column(name = "reset_token_expiracion")
    private LocalDateTime resetTokenExpiracion;

    public Usuario() {
    }

    public Usuario(
            String username,
            String password,
            String rol,
            String email) {

        this.username = username;
        this.password = password;
        this.rol = rol;
        this.email = email;
    }

    public Usuario(
            Long id,
            String username,
            String password,
            String rol,
            String email,
            String resetToken,
            LocalDateTime resetTokenExpiracion) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.email = email;
        this.resetToken = resetToken;
        this.resetTokenExpiracion = resetTokenExpiracion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiracion() {
        return resetTokenExpiracion;
    }

    public void setResetTokenExpiracion(
            LocalDateTime resetTokenExpiracion) {

        this.resetTokenExpiracion = resetTokenExpiracion;
    }

    public boolean tokenExpirado() {

        return resetTokenExpiracion == null
                || resetTokenExpiracion.isBefore(LocalDateTime.now());
    }

    public void limpiarTokenRecuperacion() {

        this.resetToken = null;
        this.resetTokenExpiracion = null;
    }
}