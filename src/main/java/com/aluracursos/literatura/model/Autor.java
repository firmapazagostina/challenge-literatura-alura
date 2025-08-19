package com.aluracursos.literatura.model;

import com.aluracursos.literatura.DTO.DatosAutor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer fechaDeNacimiento;
    private Integer fechaDeFallecimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> libros = new ArrayList<>();

    public Autor(){}

    public Autor(DatosAutor datosAutor) {
        this.nombre = String.valueOf(datosAutor.nombre());
        try {
            this.fechaDeNacimiento = Integer.valueOf(datosAutor.fechaDeNacimiento());
        } catch (NumberFormatException e) {
            this.fechaDeNacimiento = 0;
        }
        try {
            this.fechaDeFallecimiento = Integer.valueOf(datosAutor.fechaDeFallecimiento());
        } catch (NumberFormatException e) {
            this.fechaDeFallecimiento = 0;
        }
    }

    @Override
    public String toString() {
        return
                "Autor: " + nombre + "\n" +
                        "Fecha de nacimiento: " + fechaDeNacimiento + "\n" +
                        "Fecha de fallecimiento: " + fechaDeFallecimiento + "\n" +
                        "Libros: " + libros.stream()
                        .map(Libro::getTitulo)
                        .collect(Collectors.toList()) + "\n\n";

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(Integer fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public Integer getFechaDeFallecimiento() {
        return fechaDeFallecimiento;
    }

    public void setFechaDeFallecimiento(Integer fechaDeFallecimiento) {
        this.fechaDeFallecimiento = fechaDeFallecimiento;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        libros.forEach(l -> l.setAutor(this));
        this.libros = libros;
    }
}
