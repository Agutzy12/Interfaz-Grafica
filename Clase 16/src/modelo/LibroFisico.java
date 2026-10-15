/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class LibroFisico extends Libro {
    private double costoEnvio;
    private int stock;

    public LibroFisico(String titulo, String autor, double precioBase, double costoEnvio, int stock) {
        super(titulo, autor, precioBase);
        this.costoEnvio = costoEnvio;
        this.stock = stock;
    }

    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase() + costoEnvio;
    }

    @Override
    public void mostrarDetalle() {
        System.out.println("Tipo: Fisico | Stock: " + stock + " u. | Envio: $" + costoEnvio);
    }
}
