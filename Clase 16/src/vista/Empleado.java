package vista;

/** Datos de un empleado. Campos públicos para mantenerlo simple. */
public class Empleado {

    public String legajo;
    public String nombre;
    public String correo;
    public String rol;
    public String estado;   // ACTIVO, PENDIENTE o INACTIVO
    public String fecha;    // dd/MM/yyyy

    public Empleado(String legajo, String nombre, String correo,
                    String rol, String estado, String fecha) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
        this.fecha = fecha;
    }
}
