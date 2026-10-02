package proyecto_panpuntos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class FrmProducto extends JFrame {

    private JTextField txtId, txtNombre, txtPrecio, txtExistencia;
    private JComboBox<String> cbCategoria;
    private JTable tabla;
    private DefaultTableModel modelo;

    public FrmProducto() {
        setTitle("Gestión de Productos");
        setSize(700, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel l1 = new JLabel("ID Producto:");
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

        JLabel l3 = new JLabel("Categoría:");
        l3.setBounds(20, 90, 100, 25);
        add(l3);

        cbCategoria = new JComboBox<>(new String[]{"SANDWICH", "BEBIDA", "ACOMPANAMIENTO", "POSTRE", "OTRO"});
        cbCategoria.setBounds(120, 90, 200, 25);
        add(cbCategoria);

        JLabel l4 = new JLabel("Precio (Q):");
        l4.setBounds(20, 125, 100, 25);
        add(l4);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(120, 125, 150, 25);
        add(txtPrecio);

        JLabel l5 = new JLabel("Existencia:");
        l5.setBounds(20, 160, 100, 25);
        add(l5);

        txtExistencia = new JTextField();
        txtExistencia.setBounds(120, 160, 150, 25);
        add(txtExistencia);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(350, 20, 120, 25);
        add(btnGuardar);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(350, 55, 120, 25);
        add(btnBuscar);

        JButton btnModificar = new JButton("Modificar");
        btnModificar.setBounds(350, 90, 120, 25);
        add(btnModificar);

        JButton btnDesactivar = new JButton("Desactivar");
        btnDesactivar.setBounds(350, 125, 120, 25);
        add(btnDesactivar);

        modelo = new DefaultTableModel(new Object[]{"ID", "Nombre", "Categoría", "Precio", "Existencia", "Activo"}, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 200, 640, 240);
        add(scroll);

        btnGuardar.addActionListener(e -> guardar());
        btnBuscar.addActionListener(e -> buscar());
        btnModificar.addActionListener(e -> modificar());
        btnDesactivar.addActionListener(e -> desactivar());

        cargarTabla();
    }

    private void guardar() {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (?, ?, ?, ?, ?, 1)"
            );
            ps.setInt(1, Integer.parseInt(txtId.getText().trim()));
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, cbCategoria.getSelectedItem().toString());
            ps.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtExistencia.getText().trim()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Producto registrado exitosamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    private void buscar() {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM PRODUCTO WHERE id_producto = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNombre.setText(rs.getString("nombre"));
                cbCategoria.setSelectedItem(rs.getString("categoria"));
                txtPrecio.setText(String.valueOf(rs.getDouble("precio")));
                txtExistencia.setText(String.valueOf(rs.getInt("existencia")));
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage());
        }
    }

    private void modificar() {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(
                "UPDATE PRODUCTO SET nombre = ?, categoria = ?, precio = ?, existencia = ? WHERE id_producto = ?"
            );
            ps.setString(1, txtNombre.getText().trim());
            ps.setString(2, cbCategoria.getSelectedItem().toString());
            ps.setDouble(3, Double.parseDouble(txtPrecio.getText().trim()));
            ps.setInt(4, Integer.parseInt(txtExistencia.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtId.getText().trim()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage());
        }
    }

    private void desactivar() {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("UPDATE PRODUCTO SET activo = 0 WHERE id_producto = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText().trim()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Producto desactivado.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al desactivar: " + ex.getMessage());
        }
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try (Connection cn = Conexion.getConexion()) {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM PRODUCTO ORDER BY id_producto");
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("existencia"),
                    rs.getInt("activo") == 1 ? "SI" : "NO"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtExistencia.setText("");
        cbCategoria.setSelectedIndex(0);
    }
}