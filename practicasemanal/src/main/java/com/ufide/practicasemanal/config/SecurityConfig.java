package com.ufide.practicasemanal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {

                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .authorizeHttpRequests(auth -> auth

                                                /*
                                                 * Rutas públicas:
                                                 * no requieren inicio de sesión.
                                                 */
                                                .requestMatchers(
                                                                "/",
                                                                "/login",
                                                                "/olvide-password",
                                                                "/restablecer-password",
                                                                "/403",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/images/**",
                                                                "/favicon.ico")
                                                .permitAll()

                                                /*
                                                 * Administración de usuarios:
                                                 * solamente puede acceder ADMIN.
                                                 */
                                                .requestMatchers("/usuarios/**")
                                                .hasRole("ADMIN")

                                                /*
                                                 * Todas las demás rutas necesitan
                                                 * un usuario autenticado.
                                                 */
                                                .anyRequest()
                                                .authenticated())

                                /*
                                 * Configuración del formulario de login.
                                 */
                                .formLogin(form -> form

                                                .loginPage("/login")

                                                .loginProcessingUrl("/login")

                                                .defaultSuccessUrl("/", true)

                                                .failureUrl("/login?error")

                                                .usernameParameter("username")

                                                .passwordParameter("password")

                                                .permitAll())

                                /*
                                 * Configuración del cierre de sesión.
                                 */
                                .logout(logout -> logout

                                                .logoutUrl("/logout")

                                                .logoutSuccessUrl("/login?logout")

                                                .invalidateHttpSession(true)

                                                .clearAuthentication(true)

                                                .deleteCookies("JSESSIONID")

                                                .permitAll())

                                /*
                                 * Página mostrada cuando el usuario
                                 * no tiene permisos suficientes.
                                 */
                                .exceptionHandling(exception -> exception

                                                .accessDeniedPage("/403"));

                return http.build();
        }
}