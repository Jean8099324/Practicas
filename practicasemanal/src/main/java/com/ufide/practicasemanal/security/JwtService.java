package com.ufide.practicasemanal.security;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;

    private final long expiracionMilisegundos;

    public JwtService(
            @Value("${app.jwt.secret}") String secretBase64,
            @Value("${app.jwt.expiration-ms:3600000}") long expiracionMilisegundos) {

        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);

        this.key = Keys.hmacShaKeyFor(keyBytes);

        this.expiracionMilisegundos = expiracionMilisegundos;
    }

    /*
     * Genera el JWT.
     *
     * Guarda:
     * - username como subject
     * - rol como claim
     * - fecha de creación
     * - fecha de expiración
     */
    public String generarToken(
            String username,
            String rol) {

        Date ahora = new Date();

        Date expiracion = new Date(
                ahora.getTime()
                        + expiracionMilisegundos);

        return Jwts.builder()
                .claims(
                        Map.of(
                                "rol",
                                rol))
                .subject(username)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();
    }

    /*
     * Extrae el username guardado
     * dentro del subject del JWT.
     */
    public String extraerUsername(
            String token) {

        return extraerClaim(
                token,
                Claims::getSubject);
    }

    /*
     * Extrae el rol guardado
     * como claim dentro del JWT.
     */
    public String extraerRol(
            String token) {

        return extraerClaim(
                token,
                claims -> claims.get(
                        "rol",
                        String.class));
    }

    /*
     * Valida:
     *
     * 1. Que el username del token
     * sea el username esperado.
     *
     * 2. Que el token no esté expirado.
     *
     * 3. La firma se valida automáticamente
     * cuando se ejecuta parseSignedClaims().
     */
    public boolean esValido(
            String token,
            String username) {

        try {

            String usernameToken = extraerUsername(token);

            return usernameToken.equals(username)
                    && !estaExpirado(token);

        } catch (Exception exception) {

            return false;
        }
    }

    /*
     * Verifica si el JWT ya venció.
     */
    private boolean estaExpirado(
            String token) {

        return extraerClaim(
                token,
                Claims::getExpiration).before(new Date());
    }

    /*
     * Método genérico para obtener
     * cualquier claim del JWT.
     *
     * Esta es precisamente la parte
     * que aparece en la imagen del profesor.
     */
    private <T> T extraerClaim(
            String token,
            Function<Claims, T> resolver) {

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }
}