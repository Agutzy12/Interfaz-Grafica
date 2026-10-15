/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public abstract class Libro {
    private String titulo;
    private String autor;
    private double precioBase;
    private EstadoLibro estado;

    public Libro(String titulo, String autor, double precioBase) {
        this.titulo = titulo;
        this.autor = autor;
        this.precioBase = precioBase;
        this.estado = EstadoLibro.DISPONIBLE;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public double getPrecioBase() { return precioBase; }
    public EstadoLibro getEstado() { return estado; }
    public void setEstado(EstadoLibro estado) { this.estado = estado; }

    public void mostrarInfo() {
        System.out.println("Titulo: " + titulo + "Autor: " + autor + " | Estado: " + estado);
    }

    // Usamos el nombre exacto de tu archivo: LibroNoDisponible
    public void vender() throws LibroNoDisponible {
        if (this.estado == EstadoLibro.AGOTADO) {
            throw new LibroNoDisponible("No se puede vender '" + titulo + "': El libro esta AGOTADO.");
        }
        System.out.println("Venta procesada con exito para: " + titulo);
    }

    public abstract double calcularPrecioFinal();
    public abstract void mostrarDetalle();
}