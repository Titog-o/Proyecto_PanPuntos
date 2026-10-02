package proyecto_panpuntos;

public class ProductoIndividual  extends ItemVenta {

    private String categoria;

    public ProductoIndividual(int id, String nombre, double precio, String categoria) {
        super(id, nombre, precio);
        this.categoria = categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    @Override
    public int calcularPuntos(int cantidad) {
        if ("SANDWICH".equalsIgnoreCase(categoria)) {
            return cantidad * 2;
        }
        return 0;
    }

    @Override
    public String getTipoItem() {
        return "PRODUCTO";
    }
}