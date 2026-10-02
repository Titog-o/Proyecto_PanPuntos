package proyecto_panpuntos;

public abstract class ItemVenta {

    private int id;
    private String nombre;
    private double precio;

    public ItemVenta(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public abstract int calcularPuntos(int cantidad);

    public abstract String getTipoItem();
}