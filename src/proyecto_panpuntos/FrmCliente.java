package proyecto_panpuntos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class FrmCliente extends JFrame {

    private JTextField txtId, txtNombre, txtTelefono;
    private JTable tabla;
    private DefaultTableModel modelo;

    public FrmCliente() {
        setTitle("Gestión de Clientes");
        setSize(700, 480);
        setLayout(null);
        setLocationRelativeTo(null);
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
        btnGuardar.setBounds(350, 20, 120, 25);
        add(btnGuardar);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(350, 55, 120, 25);
        add(btnBuscar);

        JButton btnModificar = new JButton("Modificar");
        btnModificar.setBounds(350, 90, 120, 25);
        add(btnModificar);

        JButton btnDesactivar = new JButton("Desactivar");
        btnDesactivar.setBounds(480, 20, 120, 25);
        add(btnDesactivar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(480, 55, 120, 25);
        add(btnLimpiar);

        JButton btnRefrescar = new JButton("Listar Todos");
        btnRefrescar.setBounds(480, 90, 120, 25);
        add(btnRefrescar);

        modelo = new DefaultTableModel(new Object[]{"ID", "Nombre", "Teléfono", "Puntos", "Activo"}, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 130, 640, 290);
        add(scroll);

        btnGuardar.addActionListener(e -> guardar());
        btnBuscar.addActionListener(e -> buscar());
        btnModificar.addActionListener(e -> modificar());
        btnDesactivar.addActionListener(e -> desactivar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRefrescar.addActionListener(e -> cargarTabla());

        cargarTabla();
    }

    private void guardar() {
        if (txtId.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() || txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos del cliente.");
            return;
        }

        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (?, ?, ?, 0, 1)");
            ps.setInt(1, Integer.parseInt(txtId.getText().trim()));
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtTelefono.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente con saldo inicial de 0 puntos.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar (verifique que el ID no esté duplicado): " + ex.getMessage());
        }
    }

    private void buscar() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del cliente a buscar.");
            return;
        }

        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("SELECT nombre, telefono, puntos_acumulados, activo FROM CLIENTE WHERE id_cliente = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNombre.setText(rs.getString("nombre"));
                txtTelefono.setText(rs.getString("telefono"));
                JOptionPane.showMessageDialog(this, "Cliente encontrado.\nNombre: " + rs.getString("nombre") + "\nPuntos: " + rs.getInt("puntos_acumulados") + "\nActivo: " + (rs.getInt("activo") == 1 ? "SI" : "NO"));
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + ex.getMessage());
        }
    }

    private void modificar() {
        if (txtId.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() || txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los datos a modificar.");
            return;
        }

        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("UPDATE CLIENTE SET nombre = ?, telefono = ? WHERE id_cliente = ?");
            ps.setString(1, txtNombre.getText().trim());
            ps.setString(2, txtTelefono.getText().trim());
            ps.setInt(3, Integer.parseInt(txtId.getText().trim()));
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "Cliente modificado correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el cliente a modificar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage());
        }
    }

    private void desactivar() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del cliente a desactivar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de desactivar este cliente?", "Confirmar Desactivación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("UPDATE CLIENTE SET activo = 0 WHERE id_cliente = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText().trim()));
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "Cliente desactivado exitosamente (no se permite eliminación física).");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
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
}