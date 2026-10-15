/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class LibroUsado extends Libro implements Promocionable {
    private String estadoConservacion;

    public LibroUsado(String titulo, String autor, double precioBase, String estadoConservacion) {
        super(titulo, autor, precioBase);
        this.estadoConservacion = estadoConservacion;
    }

    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase() * 0.50; 
    }

    @Override
    public void mostrarDetalle() {
        System.out.println("Tipo: Usado | Conservacion: " + estadoConservacion);
    }

    @Override
    public double calcularDescuento() {
        return getPrecioBase() * 0.50;
    }

    @Override
    public String getMotivoPromocion() {
        return "Liquidacion de usados";
    }
}
