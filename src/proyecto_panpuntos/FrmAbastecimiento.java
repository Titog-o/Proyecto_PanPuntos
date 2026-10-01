/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_panpuntos;

/**
 *
 * @author Tito Gomez
 */
import javax.swing.*;
import java.sql.*;

public class FrmAbastecimiento extends JFrame {

    private JTextField txtIdProducto, txtCantidad;
    private JLabel lblExistenciaActual;

    public FrmAbastecimiento() {
        setTitle("Abastecimiento de Inventario");
        setSize(400, 250);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel l1 = new JLabel("ID Producto:");
        l1.setBounds(20, 20, 100, 25);
        add(l1);

        txtIdProducto = new JTextField();
        txtIdProducto.setBounds(130, 20, 100, 25);
        add(txtIdProducto);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setBounds(240, 20, 110, 25);
        add(btnConsultar);

        lblExistenciaActual = new JLabel("Existencia Actual: -");
        lblExistenciaActual.setBounds(20, 60, 200, 25);
        add(lblExistenciaActual);

        JLabel l2 = new JLabel("Ingreso:");
        l2.setBounds(20, 100, 100, 25);
        add(l2);

        txtCantidad = new JTextField();
        txtCantidad.setBounds(130, 100, 100, 25);
        add(txtCantidad);

        JButton btnAbastecer = new JButton("Confirmar Abastecimiento");
        btnAbastecer.setBounds(80, 150, 200, 30);
        add(btnAbastecer);

        btnConsultar.addActionListener(e -> consultar());
        btnAbastecer.addActionListener(e -> abastecer());
    }

    private void consultar() {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("SELECT nombre, existencia FROM PRODUCTO WHERE id_producto = ?");
            ps.setInt(1, Integer.parseInt(txtIdProducto.getText()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblExistenciaActual.setText(rs.getString("nombre") + " - Stock: " + rs.getInt("existencia"));
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void abastecer() {
        int cant = Integer.parseInt(txtCantidad.getText());
        if (cant <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
            return;
        }

        try (Connection cn = Conexion.getConexion()) {
            cn.setAutoCommit(false);
            int idProd = Integer.parseInt(txtIdProducto.getText());

            PreparedStatement ps1 = cn.prepareStatement("UPDATE PRODUCTO SET existencia = existencia + ? WHERE id_producto = ?");
            ps1.setInt(1, cant);
            ps1.setInt(2, idProd);
            ps1.executeUpdate();

            PreparedStatement ps2 = cn.prepareStatement("INSERT INTO HISTORIAL_INVENTARIO (id_movimiento, id_producto, fecha, cantidad, tipo_movimiento) VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_INVENTARIO), ?, SYSDATE, ?, 'INGRESO')");
            ps2.setInt(1, idProd);
            ps2.setInt(2, cant);
            ps2.executeUpdate();

            cn.commit();
            JOptionPane.showMessageDialog(this, "Inventario actualizado correctamente.");
            consultar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}