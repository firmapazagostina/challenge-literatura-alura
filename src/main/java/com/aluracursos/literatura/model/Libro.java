package com.aluracursos.literatura.model;

import com.aluracursos.literatura.DTO.DatosLibros;
import jakarta.persistence.*;
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToOne()
    private Autor autor;
    private String idioma;
    private Double numeroDeDescargas;

    public Libro() {
    }

    public Libro(DatosLibros datosLibros, Autor autor) {
        this.titulo = datosLibros.titulo();
        this.autor = autor;
        this.idioma = datosLibros.idiomas().get(0);
        this.numeroDeDescargas = datosLibros.numeroDeDescargas();
    }

    @Override
    public String toString() {
        return
                "----- LIBRO -----\n" +
                        "Título: " + titulo + "\n" +
                        "Autor: " + autor.getNombre() + "\n" +
                        "Idioma: " + idioma + "\n" +
                        "Número de descargas: " + numeroDeDescargas + "\n" +
                        "-----------------\n\n";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Double numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }
}
