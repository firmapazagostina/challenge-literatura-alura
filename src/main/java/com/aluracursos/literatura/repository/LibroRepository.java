package com.aluracursos.literatura.repository;

import com.aluracursos.literatura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByIdioma(String idioma);
    List<Libro> findTop10ByOrderByNumeroDeDescargasDesc();
}
