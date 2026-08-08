package com.ufide.practicasemanal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.ufide.practicasemanal.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {

                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {

                return authenticationConfiguration
                                .getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        CorsConfigurationSource corsConfigurationSource,
                        JwtAuthFilter jwtAuthFilter)
                        throws Exception {

                http
                                .cors(cors -> cors
                                                .configurationSource(
                                                                corsConfigurationSource))

                                /*
                                 * Los formularios HTML conservan CSRF.
                                 * Las rutas REST se prueban con JWT/Postman.
                                 */
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**"))

                                .authorizeHttpRequests(auth -> auth

                                                /*
                                                 * Rutas públicas.
                                                 */
                                                .requestMatchers(
                                                                "/",
                                                                "/login",
                                                                "/olvide-password",
                                                                "/restablecer-password",
                                                                "/403",
                                                                "/api/auth/login",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/images/**",
                                                                "/favicon.ico")
                                                .permitAll()

                                                /*
                                                 * Administración de usuarios web.
                                                 */
                                                .requestMatchers("/usuarios/**")
                                                .hasRole("ADMIN")

                                                /*
                                                 * API de roles.
                                                 */
                                                .requestMatchers("/api/roles/**")
                                                .authenticated()

                                                /*
                                                 * API de cursos.
                                                 */
                                                .requestMatchers("/api/cursos/**")
                                                .authenticated()

                                                /*
                                                 * Resto de la aplicación.
                                                 */
                                                .anyRequest()
                                                .authenticated())

                                /*
                                 * Login HTML tradicional.
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
                                 * Logout del sistema web.
                                 */
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())

                                .exceptionHandling(exception -> exception
                                                .accessDeniedPage("/403"));

                /*
                 * El JWT se procesa antes del filtro tradicional
                 * de username y password.
                 */
                http.addFilterBefore(
                                jwtAuthFilter,
                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}