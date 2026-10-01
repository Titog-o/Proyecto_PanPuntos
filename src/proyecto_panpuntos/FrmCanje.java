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
import java.sql.*;

public class FrmCanje extends JFrame {

    private JTextField txtBusqueda, txtPuntosRequeridos;
    private JLabel lblClienteInfo, lblSaldoActual;
    private JComboBox<String> cbPremios;
    private int idClienteSeleccionado = -1;
    private int puntosActualesCliente = 0;

    public FrmCanje() {
        setTitle("Canje de Recompensas y Premios");
        setSize(550, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- SECCIÓN BÚSQUEDA CLIENTE ---
        JLabel l1 = new JLabel("ID o Teléfono:");
        l1.setBounds(20, 20, 100, 25);
        add(l1);

        txtBusqueda = new JTextField();
        txtBusqueda.setBounds(120, 20, 120, 25);
        add(txtBusqueda);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(250, 20, 90, 25);
        add(btnBuscar);

        lblClienteInfo = new JLabel("Cliente: No seleccionado");
        lblClienteInfo.setBounds(20, 60, 480, 25);
        add(lblClienteInfo);

        lblSaldoActual = new JLabel("Puntos Disponibles: 0");
        lblSaldoActual.setBounds(20, 90, 480, 25);
        add(lblSaldoActual);

        JSeparator sep = new JSeparator();
        sep.setBounds(20, 125, 490, 10);
        add(sep);

        // --- SECCIÓN CANJE DE PREMIOS ---
        JLabel l2 = new JLabel("Seleccionar Premio:");
        l2.setBounds(20, 140, 140, 25);
        add(l2);

        cbPremios = new JComboBox<>(new String[]{
            "Seleccione un premio...",
            "Pan Dulce Tradicional (50 pts)",
            "Café Americano (80 pts)",
            "Sandwich Especial (150 pts)",
            "Descuento de Q20 en compra (200 pts)"
        });
        cbPremios.setBounds(160, 140, 250, 25);
        add(cbPremios);

        JLabel l3 = new JLabel("Puntos a Canjear:");
        l3.setBounds(20, 180, 140, 25);
        add(l3);

        txtPuntosRequeridos = new JTextField();
        txtPuntosRequeridos.setBounds(160, 180, 100, 25);
        txtPuntosRequeridos.setEditable(false);
        add(txtPuntosRequeridos);

        JButton btnCanjear = new JButton("Procesar Canje");
        btnCanjear.setBounds(160, 230, 160, 40);
        add(btnCanjear);

        // --- EVENTOS ---
        btnBuscar.addActionListener(e -> buscarCliente());
        cbPremios.addActionListener(e -> actualizarPuntosRequeridos());
        btnCanjear.addActionListener(e -> procesarCanje());
    }

    private void buscarCliente() {
        String criterio = txtBusqueda.getText().trim();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID o teléfono.");
            return;
        }

        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(
                "SELECT id_cliente, nombre, puntos_acumulados, activo FROM CLIENTE WHERE (TO_CHAR(id_cliente) = ? OR telefono = ?) AND activo = 1"
            );
            ps.setString(1, criterio);
            ps.setString(2, criterio);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idClienteSeleccionado = rs.getInt("id_cliente");
                puntosActualesCliente = rs.getInt("puntos_acumulados");
                lblClienteInfo.setText("Cliente: " + rs.getString("nombre"));
                lblSaldoActual.setText("Puntos Disponibles: " + puntosActualesCliente + " pts");
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado o inactivo.");
                limpiarCamposCliente();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + ex.getMessage());
        }
    }

    private void actualizarPuntosRequeridos() {
        int index = cbPremios.getSelectedIndex();
        switch (index) {
            case 1 -> txtPuntosRequeridos.setText("50");
            case 2 -> txtPuntosRequeridos.setText("80");
            case 3 -> txtPuntosRequeridos.setText("150");
            case 4 -> txtPuntosRequeridos.setText("200");
            default -> txtPuntosRequeridos.setText("");
        }
    }

    private void procesarCanje() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente válido.");
            return;
        }

        if (cbPremios.getSelectedIndex() <= 0 || txtPuntosRequeridos.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un premio válido.");
            return;
        }

        int puntosRequeridos = Integer.parseInt(txtPuntosRequeridos.getText());

        // Validar si tiene puntos suficientes (Regla de negocio)
        if (puntosActualesCliente < puntosRequeridos) {
            JOptionPane.showMessageDialog(this, 
                "Puntos insuficientes.\nEl cliente tiene: " + puntosActualesCliente + " pts\nRequiere: " + puntosRequeridos + " pts", 
                "Saldo Insuficiente", 
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Confirmar el canje de '" + cbPremios.getSelectedItem() + "' por " + puntosRequeridos + " puntos?",
            "Confirmar Canje", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection cn = Conexion.getConexion()) {
            cn.setAutoCommit(false); // Iniciar transacción

            // 1. Restar puntos al cliente
            PreparedStatement psUpdate = cn.prepareStatement(
                "UPDATE CLIENTE SET puntos_acumulados = puntos_acumulados - ? WHERE id_cliente = ?"
            );
            psUpdate.setInt(1, puntosRequeridos);
            psUpdate.setInt(2, idClienteSeleccionado);
            psUpdate.executeUpdate();

            // 2. Registrar en historial de puntos
            PreparedStatement psHist = cn.prepareStatement(
                "INSERT INTO HISTORIAL_PUNTOS (id_movimiento, id_cliente, fecha, tipo, puntos, descripcion) " +
                "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_PUNTOS), ?, SYSDATE, 'CANJE', ?, ?)"
            );
            psHist.setInt(1, idClienteSeleccionado);
            psHist.setInt(2, puntosRequeridos);
            psHist.setString(3, "Canje de premio: " + cbPremios.getSelectedItem());
            psHist.executeUpdate();

            cn.commit(); // Confirmar cambios en BD

            // Actualizar saldo local
            puntosActualesCliente -= puntosRequeridos;
            lblSaldoActual.setText("Puntos Disponibles: " + puntosActualesCliente + " pts");

            JOptionPane.showMessageDialog(this, 
                "¡Canje realizado exitosamente!\nNuevo Saldo: " + puntosActualesCliente + " puntos.",
                "Canje Exitoso", 
                JOptionPane.INFORMATION_MESSAGE
            );

            cbPremios.setSelectedIndex(0);
            txtPuntosRequeridos.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar el canje: " + ex.getMessage());
        }
    }

    private void limpiarCamposCliente() {
        idClienteSeleccionado = -1;
        puntosActualesCliente = 0;
        lblClienteInfo.setText("Cliente: No seleccionado");
        lblSaldoActual.setText("Puntos Disponibles: 0");
    }
}
