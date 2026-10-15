/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ControladorLibreria;

import java.util.ArrayList;
import java.util.List;
import modelo.EstadoLibro;
import modelo.Libro;
import modelo.LibroDigital;
import modelo.LibroFisico;
import modelo.LibroNoDisponible;
import modelo.LibroUsado;

public class ControladorLibreria {

    private final List<Libro> catalogo;

    public ControladorLibreria() {
        this.catalogo = new ArrayList<>();
        cargarDatosDeEjemplo();
    }

    // 1. Datos iniciales tomados de tu anterior Clase9
    private void cargarDatosDeEjemplo() {
        LibroFisico l1 = new LibroFisico("Cien Decadas de Soledad", "G. Garcia Marquez", 15000, 2500, 10);
        LibroDigital l2 = new LibroDigital("Clean Code", "Robert C. Martin", 12000, "PDF");
        LibroUsado l3 = new LibroUsado("Rayuela", "Julio Cortazar", 8000, "Muy bueno");

        // Libro agotado para probar las validaciones
        l2.setEstado(EstadoLibro.AGOTADO);

        catalogo.add(l1);
        catalogo.add(l2);
        catalogo.add(l3);
    }

    // 2. Obtener lista completa
    public List<Libro> getCatalogo() {
        return catalogo;
    }

    // 3. Alta de libro
    public void agregarLibro(Libro libro) {
        if (libro != null) {
            catalogo.add(libro);
        }
    }

    // 4. Baja de libro por índice de la tabla
    public void eliminarLibro(int indice) {
        if (indice >= 0 && indice < catalogo.size()) {
            catalogo.remove(indice);
        }
    }

    // 5. Procesar venta capturando la excepción de negocio
    public String venderLibro(int indice) {
        if (indice < 0 || indice >= catalogo.size()) {
            return "Índice de libro inválido.";
        }
        Libro libro = catalogo.get(indice);
        try {
            libro.vender();
            return "Venta procesada con éxito para: " + libro.getTitulo();
        } catch (LibroNoDisponible e) {
            return "Error al vender: " + e.getMessage();
        }
    }

    // 6. Filtro por texto y estado (para el buscador de la vista)
    public List<Libro> filtrar(String texto, String estadoFiltro) {
        List<Libro> filtrados = new ArrayList<>();
        String q = (texto == null) ? "" : texto.trim().toLowerCase();

        for (Libro l : catalogo) {
            boolean coincideTexto = q.isEmpty()
                    || l.getTitulo().toLowerCase().contains(q)
                    || l.getAutor().toLowerCase().contains(q);

            boolean coincideEstado = estadoFiltro == null
                    || estadoFiltro.equalsIgnoreCase("Todos los estados")
                    || l.getEstado().name().equalsIgnoreCase(estadoFiltro);

            if (coincideTexto && coincideEstado) {
                filtrados.add(l);
            }
        }
        return filtrados;
    }
}