/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_panpuntos;

/**
 *
 * @author Tito Gomez
 */
public class MenuCompleto extends Venta {

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