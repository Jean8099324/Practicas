package com.ufide.practicas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 * Spring escanea automaticamente este paquete y sus sub-paquetes
 * buscando @Controller, @Service, @Repository, @Entity, etc.
 */
@SpringBootApplication
public class PracticasApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticasApplication.class, args);
    }
}