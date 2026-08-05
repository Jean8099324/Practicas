package com.ufide.practicasemanal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.practicasemanal.entity.Usuario;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByResetToken(String resetToken);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}