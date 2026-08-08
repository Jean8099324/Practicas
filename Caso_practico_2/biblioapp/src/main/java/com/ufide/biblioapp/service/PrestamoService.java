package com.ufide.biblioapp.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufide.biblioapp.entity.Libro;
import com.ufide.biblioapp.entity.Prestamo;
import com.ufide.biblioapp.entity.Usuario;
import com.ufide.biblioapp.repository.PrestamoRepository;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroService libroService;

    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAll();
    }

    public Prestamo buscarPorId(Long id) {
        return prestamoRepository.findById(id).orElse(null);
    }

    public Prestamo registrarPrestamo(Libro libro, Usuario usuario) {

        if (libro.getCopiasDisponibles() <= 0) {
            throw new IllegalStateException("No hay copias disponibles para este libro.");
        }

        Prestamo prestamo = new Prestamo();

        LocalDate hoy = LocalDate.now();

        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(hoy);
        prestamo.setFechaLimite(hoy.plusDays(14));
        prestamo.setFechaDevolucion(null);

        libro.setCopiasDisponibles(libro.getCopiasDisponibles() - 1);
        libroService.guardar(libro);

        return prestamoRepository.save(prestamo);
    }

    public Prestamo registrarDevolucion(Long prestamoId) {

        Prestamo prestamo = buscarPorId(prestamoId);

        if (prestamo == null) {
            throw new IllegalArgumentException("Prestamo no encontrado.");
        }

        if (prestamo.getFechaDevolucion() != null) {
            throw new IllegalStateException("Este prestamo ya fue devuelto.");
        }

        prestamo.setFechaDevolucion(LocalDate.now());

        Libro libro = prestamo.getLibro();

        libro.setCopiasDisponibles(libro.getCopiasDisponibles() + 1);
        libroService.guardar(libro);

        return prestamoRepository.save(prestamo);
    }
}