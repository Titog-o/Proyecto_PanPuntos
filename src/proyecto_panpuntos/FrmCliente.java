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
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class FrmCliente extends JFrame {

    private JTextField txtId, txtNombre, txtTelefono;
    private JTable tabla;
    private DefaultTableModel modelo;

    public FrmCliente() {
        setTitle("Gestión de Clientes");
        setSize(650, 450);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel l1 = new JLabel("ID Cliente:");
        l1.setBounds(20, 20, 100, 25);
        add(l1);

        txtId = new JTextField();
        txtId.setBounds(120, 20, 150, 25);
        add(txtId);

        JLabel l2 = new JLabel("Nombre:");
        l2.setBounds(20, 55, 100, 25);
        add(l2);

        txtNombre = new JTextField();
        txtNombre.setBounds(120, 55, 200, 25);
        add(txtNombre);

        JLabel l3 = new JLabel("Teléfono:");
        l3.setBounds(20, 90, 100, 25);
        add(l3);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(120, 90, 150, 25);
        add(txtTelefono);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(350, 20, 110, 25);
        add(btnGuardar);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(350, 55, 110, 25);
        add(btnBuscar);

        JButton btnDesactivar = new JButton("Desactivar");
        btnDesactivar.setBounds(350, 90, 110, 25);
        add(btnDesactivar);

        modelo = new DefaultTableModel(new Object[]{"ID", "Nombre", "Teléfono", "Puntos", "Activo"}, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 130, 590, 250);
        add(scroll);

        btnGuardar.addActionListener(e -> guardar());
        btnBuscar.addActionListener(e -> cargarTabla());
        btnDesactivar.addActionListener(e -> desactivar());

        cargarTabla();
    }

    private void guardar() {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (?, ?, ?, 0, 1)");
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.setString(2, txtNombre.getText());
            ps.setString(3, txtTelefono.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.");
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try (Connection cn = Conexion.getConexion()) {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM CLIENTE ORDER BY id_cliente");
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getInt("puntos_acumulados"),
                    rs.getInt("activo") == 1 ? "SI" : "NO"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void desactivar() {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("UPDATE CLIENTE SET activo = 0 WHERE id_cliente = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente desactivado.");
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}