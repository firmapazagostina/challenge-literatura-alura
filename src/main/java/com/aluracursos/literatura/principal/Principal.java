package com.aluracursos.literatura.principal;

import com.aluracursos.literatura.DTO.Datos;
import com.aluracursos.literatura.DTO.DatosAutor;
import com.aluracursos.literatura.DTO.DatosLibros;
import com.aluracursos.literatura.model.Autor;
import com.aluracursos.literatura.model.Libro;
import com.aluracursos.literatura.repository.AutorRepository;
import com.aluracursos.literatura.repository.LibroRepository;
import com.aluracursos.literatura.service.ConsumoAPI;
import com.aluracursos.literatura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
@Component
public class Principal {
    @Autowired
    private final LibroRepository libroRepository;
    @Autowired
    private final AutorRepository autorRepository;
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private String json;
    private List<Autor> autores;
    private List<Libro> libros;
    Optional<Autor> autorBuscado;

    private String menu = """
            ------------
            Elija la opción a través de su número:
            1 - buscar libro por título
            2 - listar libros registrados
            3 - listar autores registrados
            4 - listar autores vivos en determinado año
            5 - listar libros por idioma
            6 - mostrar estadísticas
            7 - top 10 libros más descargados
            8 - buscar autor por nombre
            0 - salir
            """;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu(){
        var opcionElegida = -1;
        while (opcionElegida != 0) {
            json = consumoAPI.obtenerDatos(URL_BASE);
            System.out.println(menu);
            opcionElegida = teclado.nextInt();
            teclado.nextLine();
            switch (opcionElegida) {
                case 8 -> buscarAutorPorNombre();
                case 7 -> buscarTop10Libros();
                case 6 -> analizarDescargasDeLibros();
                case 5 -> buscarLibrosPorIdioma();
                case 4 -> buscarAutoresVivos();
                case 3 -> mostrarAutoresBuscados();
                case 2 -> mostrarLibrosBuscados();
                case 1 -> buscarLibroPorTitulo();
                case 0 -> System.out.println("Hasta luego...");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    public void buscarLibroPorTitulo() {
        DatosLibros datosLibro = recibirDatosDelLibro();
        if (datosLibro != null) {
            Libro libro;
            DatosAutor datosAutor = datosLibro.autor().get(0);
            Autor autorExistente = autorRepository.findByNombre(datosAutor.nombre());
            if(autorExistente != null){
                libro = new Libro(datosLibro, autorExistente);
            }else{
                Autor nuevoAutor = new Autor(datosAutor);
                libro = new Libro(datosLibro, nuevoAutor);
                autorRepository.save(nuevoAutor);
            }
            try {
                libroRepository.save(libro);
                System.out.println(libro);
            } catch (Exception e) {
                System.out.println("No puedes registrar un mismo libro más de una vez");
            }
        }else{
            System.out.println("No hemos encontrado el libro en la API =(");
        }
    }

    private DatosLibros recibirDatosDelLibro() {
        System.out.println("Ingrese el título del libro");
        var nombreLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE +
                "?search=" +
                nombreLibro.replace(" ", "+"));
        Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(libro -> libro.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            return libroBuscado.get();
        }else {
            return null;
        }
    }

    private void mostrarLibrosBuscados() {
        libros = libroRepository.findAll();

        libros.stream()
                .sorted(Comparator.comparing(Libro::getIdioma))
                .forEach(System.out::println);
    }

    private void mostrarAutoresBuscados() {
        autores = autorRepository.findAll();

        autores.stream()
                .sorted(Comparator.comparing(Autor::getFechaDeFallecimiento))
                .forEach(System.out::println);
    }

    private void buscarLibrosPorIdioma(){
        System.out.println("Ingrese el idioma para buscar los libros:\nes- español\nen- inglés\nfr- francés\npt- portugués");
        var idioma = teclado.nextLine();
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        librosPorIdioma.forEach(System.out::println);
    }

    private void buscarAutoresVivos(){
        System.out.println("Ingrese el año vivo de auto(es) que desea buscar");
        int anio = Integer.valueOf(teclado.nextLine());
        List<Autor> autoresVivos = autorRepository
                .findByFechaDeNacimientoLessThanEqualAndFechaDeFallecimientoGreaterThanEqual(anio, anio);

        autoresVivos.forEach(System.out::println);
    }

    private void buscarTop10Libros() {
        List<Libro> topLibros = libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();

        System.out.println("📚 Top 10 libros más descargados:");
        topLibros.forEach(l ->
                System.out.printf("Libro: %s Descargas %s\n",
                        l.getTitulo(), l.getNumeroDeDescargas()));
    }

    private void analizarDescargasDeLibros(){
        DoubleSummaryStatistics est = libroRepository.findAll().stream()
                .filter(l -> l.getNumeroDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(Libro::getNumeroDeDescargas));

        System.out.println("Descargas totales: " + est.getSum());
        System.out.println("Promedio de descargas: " + est.getAverage());
        System.out.println("Cantidad mínima de descargas: " + est.getMin());
        System.out.println("Cantidad máxima de descargas: " + est.getMax());
        System.out.println("Cantidad total de libros: " + est.getCount());
    }

    private void buscarAutorPorNombre() {
        System.out.println("🔎 Escribe el nombre del autor que deseas buscar");
        var nombreAutor = teclado.nextLine();

        autorBuscado = autorRepository.findByNombreContainingIgnoreCase(nombreAutor);

        if (autorBuscado.isPresent()) {
            System.out.println("✅ El Autor buscado es:\n" + autorBuscado.get());
        } else {
            System.out.println("❌ Autor no encontrado.");
        }

    }
}
