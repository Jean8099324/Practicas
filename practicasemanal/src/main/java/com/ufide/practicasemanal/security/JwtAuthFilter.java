package com.ufide.practicasemanal.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final UserDetailsService userDetailsService;

        public JwtAuthFilter(
                        JwtService jwtService,
                        UserDetailsService userDetailsService) {

                this.jwtService = jwtService;
                this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String header = request.getHeader("Authorization");

                /*
                 * Si no existe Authorization o no tiene Bearer,
                 * continúa normalmente.
                 */
                if (header == null
                                || !header.startsWith("Bearer ")) {

                        filterChain.doFilter(
                                        request,
                                        response);

                        return;
                }

                /*
                 * Quita "Bearer " y deja únicamente el JWT.
                 */
                String token = header.substring(7);

                try {

                        String username = jwtService.extraerUsername(token);

                        /*
                         * Solamente intenta autenticar cuando:
                         *
                         * 1. El token contiene username.
                         * 2. No existe autenticación previa.
                         */
                        if (username != null
                                        && !username.isBlank()
                                        && SecurityContextHolder
                                                        .getContext()
                                                        .getAuthentication() == null) {

                                UserDetails userDetails = userDetailsService
                                                .loadUserByUsername(username);

                                /*
                                 * IMPORTANTE:
                                 * Tu JwtService actual tiene esValido(),
                                 * no esTokenValido().
                                 */
                                if (jwtService.esValido(
                                                token,
                                                userDetails.getUsername())) {

                                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                                        userDetails,
                                                        null,
                                                        userDetails.getAuthorities());

                                        authentication.setDetails(
                                                        new WebAuthenticationDetailsSource()
                                                                        .buildDetails(request));

                                        SecurityContextHolder
                                                        .getContext()
                                                        .setAuthentication(
                                                                        authentication);
                                }
                        }

                } catch (Exception exception) {

                        /*
                         * Si el JWT está vencido, modificado
                         * o tiene formato inválido,
                         * limpiamos el contexto.
                         */
                        SecurityContextHolder.clearContext();
                }

                filterChain.doFilter(
                                request,
                                response);
        }
}