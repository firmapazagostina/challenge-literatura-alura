package com.aluracursos.literatura.repository;

import com.aluracursos.literatura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AutorRepository extends JpaRepository<Autor, Long> {
    Autor findByNombre(String nombreAutor);

    List<Autor> findByFechaDeNacimientoLessThanEqualAndFechaDeFallecimientoGreaterThanEqual(int anio, int anio1);

    Optional<Autor> findByNombreContainingIgnoreCase(String nombreAutor);
}
