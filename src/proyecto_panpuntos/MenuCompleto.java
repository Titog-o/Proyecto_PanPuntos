package proyecto_panpuntos;

public class MenuCompleto extends ItemVenta {

    private int idSandwich;
    private int idBebida;
    private int idAcompanamiento;

    public MenuCompleto(int id, String nombre, double precio, int idSandwich, int idBebida, int idAcompanamiento) {
        super(id, nombre, precio);
        this.idSandwich = idSandwich;
        this.idBebida = idBebida;
        this.idAcompanamiento = idAcompanamiento;
    }

    public int getIdSandwich() {
        return idSandwich;
    }

    public int getIdBebida() {
        return idBebida;
    }

    public int getIdAcompanamiento() {
        return idAcompanamiento;
    }

    @Override
    public int calcularPuntos(int cantidad) {
        return cantidad * 8;
    }

    @Override
    public String getTipoItem() {
        return "MENU";
    }
}