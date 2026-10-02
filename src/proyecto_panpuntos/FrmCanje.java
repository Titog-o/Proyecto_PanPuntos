package proyecto_panpuntos;

import javax.swing.*;
import java.sql.*;

public class FrmCanje extends JFrame {

    private JTextField txtBusqueda, txtPuntosRequeridos;
    private JLabel lblClienteInfo, lblSaldoActual, lblStockInfo;
    private JComboBox<String> cbPremios;

    private int idClienteSeleccionado = -1;
    private int puntosActualesCliente = 0;
    private int idProductoPremio = -1;
    private int stockDisponiblePremio = 0;

    public FrmCanje() {
        setTitle("Canje de Recompensas y Premios");
        setSize(550, 410);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        JLabel l1 = new JLabel("ID o Teléfono:");
        l1.setBounds(25, 20, 100, 25);
        add(l1);

        txtBusqueda = new JTextField();
        txtBusqueda.setBounds(125, 20, 130, 25);
        add(txtBusqueda);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(265, 20, 90, 25);
        add(btnBuscar);

        lblClienteInfo = new JLabel("Cliente: No seleccionado");
        lblClienteInfo.setBounds(25, 55, 480, 25);
        add(lblClienteInfo);

        lblSaldoActual = new JLabel("Puntos Disponibles: 0 pts");
        lblSaldoActual.setBounds(25, 80, 480, 25);
        add(lblSaldoActual);

        JSeparator sep = new JSeparator();
        sep.setBounds(25, 115, 480, 10);
        add(sep);

        JLabel l2 = new JLabel("Seleccionar Premio:");
        l2.setBounds(25, 130, 140, 25);
        add(l2);

        cbPremios = new JComboBox<>(new String[]{
            "Seleccione un premio...",
            "Bebida Gratuita (10 pts)",
            "Acompañamiento Gratuito (15 pts)",
            "Sándwich Individual Gratuito (30 pts)",
            "Menú Completo Gratuito (50 pts)"
        });
        cbPremios.setBounds(165, 130, 260, 25);
        add(cbPremios);

        JLabel l3 = new JLabel("Puntos Necesarios:");
        l3.setBounds(25, 170, 140, 25);
        add(l3);

        txtPuntosRequeridos = new JTextField();
        txtPuntosRequeridos.setBounds(165, 170, 100, 25);
        txtPuntosRequeridos.setEditable(false);
        add(txtPuntosRequeridos);

        lblStockInfo = new JLabel("Stock de Premio: N/A");
        lblStockInfo.setBounds(25, 205, 480, 25);
        add(lblStockInfo);

        JButton btnCanjear = new JButton("Procesar Canje");
        btnCanjear.setBounds(165, 250, 170, 40);
        add(btnCanjear);

        btnBuscar.addActionListener(e -> buscarCliente());
        cbPremios.addActionListener(e -> evaluarPremioSeleccionado());
        btnCanjear.addActionListener(e -> procesarCanje());
    }

    private void buscarCliente() {
        String criterio = txtBusqueda.getText().trim();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID o número de teléfono.");
            return;
        }

        try (Connection cn = Conexion.getConexion()) {
            String sql = "SELECT id_cliente, nombre, puntos_acumulados FROM CLIENTE WHERE (TO_CHAR(id_cliente) = ? OR telefono = ?) AND activo = 1";
            PreparedStatement ps = cn.prepareStatement(sql);
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
                limpiarDatosCliente();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + ex.getMessage());
        }
    }

    private void evaluarPremioSeleccionado() {
        int index = cbPremios.getSelectedIndex();
        String categoriaBuscada = "";

        switch (index) {
            case 1 -> { txtPuntosRequeridos.setText("10"); categoriaBuscada = "BEBIDA"; }
            case 2 -> { txtPuntosRequeridos.setText("15"); categoriaBuscada = "ACOMPANAMIENTO"; }
            case 3 -> { txtPuntosRequeridos.setText("30"); categoriaBuscada = "SANDWICH"; }
            case 4 -> { txtPuntosRequeridos.setText("50"); categoriaBuscada = "MENU"; }
            default -> {
                txtPuntosRequeridos.setText("");
                lblStockInfo.setText("Stock de Premio: N/A");
                idProductoPremio = -1;
                stockDisponiblePremio = 0;
                return;
            }
        }

        consultarStockPremioBD(categoriaBuscada);
    }

    private void consultarStockPremioBD(String categoria) {
        try (Connection cn = Conexion.getConexion()) {
            if ("MENU".equals(categoria)) {
                String sql = "SELECT m.id_menu, m.nombre, LEAST(p1.existencia, p2.existencia, p3.existencia) AS stock_menu " +
                             "FROM MENU m " +
                             "JOIN PRODUCTO p1 ON m.id_sandwich = p1.id_producto " +
                             "JOIN PRODUCTO p2 ON m.id_bebida = p2.id_producto " +
                             "JOIN PRODUCTO p3 ON m.id_acompanamiento = p3.id_producto " +
                             "WHERE m.activo = 1 AND ROWNUM = 1";
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idProductoPremio = rs.getInt("id_menu");
                    stockDisponiblePremio = rs.getInt("stock_menu");
                    lblStockInfo.setText("Disponible: " + rs.getString("nombre") + " (Stock: " + stockDisponiblePremio + ")");
                } else {
                    idProductoPremio = -1;
                    stockDisponiblePremio = 0;
                    lblStockInfo.setText("No hay menús activos con stock disponible.");
                }
            } else {
                String sql = "SELECT id_producto, nombre, existencia FROM PRODUCTO WHERE categoria = ? AND activo = 1 AND existencia > 0 AND ROWNUM = 1";
                PreparedStatement ps = cn.prepareStatement(sql);
                ps.setString(1, categoria);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    idProductoPremio = rs.getInt("id_producto");
                    stockDisponiblePremio = rs.getInt("existencia");
                    lblStockInfo.setText("Disponible: " + rs.getString("nombre") + " (Stock: " + stockDisponiblePremio + ")");
                } else {
                    idProductoPremio = -1;
                    stockDisponiblePremio = 0;
                    lblStockInfo.setText("Sin stock disponible para esta categoría.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar premio en inventario: " + ex.getMessage());
        }
    }

    private void procesarCanje() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente activo.");
            return;
        }

        if (cbPremios.getSelectedIndex() <= 0 || txtPuntosRequeridos.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un premio válido.");
            return;
        }

        int puntosRequeridos = Integer.parseInt(txtPuntosRequeridos.getText());

        if (puntosActualesCliente < puntosRequeridos) {
            JOptionPane.showMessageDialog(this, "Puntos insuficientes para este canje.", "Saldo Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idProductoPremio == -1 || stockDisponiblePremio <= 0) {
            JOptionPane.showMessageDialog(this, "El premio seleccionado no tiene existencias en inventario.", "Sin Stock", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String premioSeleccionado = (String) cbPremios.getSelectedItem();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Confirmar el canje de '" + premioSeleccionado + "' por " + puntosRequeridos + " pts?", "Confirmar Canje", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        ejecutarTransaccionCanje(puntosRequeridos, premioSeleccionado);
    }

    private void ejecutarTransaccionCanje(int puntosRequeridos, String premioSeleccionado) {
        try (Connection cn = Conexion.getConexion()) {
            cn.setAutoCommit(false);

            PreparedStatement psUpdate = cn.prepareStatement("UPDATE CLIENTE SET puntos_acumulados = puntos_acumulados - ? WHERE id_cliente = ?");
            psUpdate.setInt(1, puntosRequeridos);
            psUpdate.setInt(2, idClienteSeleccionado);
            psUpdate.executeUpdate();

            PreparedStatement psHist = cn.prepareStatement(
                "INSERT INTO HISTORIAL_PUNTOS (id_movimiento, id_cliente, fecha, tipo, puntos, descripcion) " +
                "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_PUNTOS), ?, SYSDATE, 'CANJE', ?, ?)"
            );
            psHist.setInt(1, idClienteSeleccionado);
            psHist.setInt(2, puntosRequeridos);
            psHist.setString(3, "Canje: " + premioSeleccionado);
            psHist.executeUpdate();

            if (cbPremios.getSelectedIndex() == 4) {
                PreparedStatement psMenu = cn.prepareStatement("SELECT id_sandwich, id_bebida, id_acompanamiento FROM MENU WHERE id_menu = ?");
                psMenu.setInt(1, idProductoPremio);
                ResultSet rsM = psMenu.executeQuery();
                if (rsM.next()) {
                    descontarEInsertarInventario(cn, rsM.getInt("id_sandwich"));
                    descontarEInsertarInventario(cn, rsM.getInt("id_bebida"));
                    descontarEInsertarInventario(cn, rsM.getInt("id_acompanamiento"));
                }
            } else {
                descontarEInsertarInventario(cn, idProductoPremio);
            }

            cn.commit();

            puntosActualesCliente -= puntosRequeridos;
            lblSaldoActual.setText("Puntos Disponibles: " + puntosActualesCliente + " pts");

            JOptionPane.showMessageDialog(this, "¡Canje exitoso!\nNuevo saldo: " + puntosActualesCliente + " pts", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            cbPremios.setSelectedIndex(0);
            txtPuntosRequeridos.setText("");
            lblStockInfo.setText("Stock de Premio: N/A");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error durante el canje: " + ex.getMessage());
        }
    }

    private void descontarEInsertarInventario(Connection cn, int idProd) throws SQLException {
        PreparedStatement psStock = cn.prepareStatement("UPDATE PRODUCTO SET existencia = existencia - 1 WHERE id_producto = ?");
        psStock.setInt(1, idProd);
        psStock.executeUpdate();

        PreparedStatement psInvHist = cn.prepareStatement(
            "INSERT INTO HISTORIAL_INVENTARIO (id_movimiento, id_producto, fecha, cantidad, tipo_movimiento) " +
            "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_INVENTARIO), ?, SYSDATE, 1, 'CANJE')"
        );
        psInvHist.setInt(1, idProd);
        psInvHist.executeUpdate();
    }

    private void limpiarDatosCliente() {
        idClienteSeleccionado = -1;
        puntosActualesCliente = 0;
        lblClienteInfo.setText("Cliente: No seleccionado");
        lblSaldoActual.setText("Puntos Disponibles: 0 pts");
    }
}