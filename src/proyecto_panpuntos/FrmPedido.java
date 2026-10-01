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
import java.util.ArrayList;

public class FrmPedido extends JFrame {

    private JTextField txtClienteId, txtItemId, txtCantidad;
    private JLabel lblClienteInfo, lblTotal, lblPuntos;
    private JRadioButton rbProducto, rbMenu;
    private ButtonGroup bgTipo;
    private JTable tabla;
    private DefaultTableModel modelo;

    private ArrayList<Venta> listaItems = new ArrayList<>();
    private ArrayList<Integer> listaCantidades = new ArrayList<>();
    private double totalAcumulado = 0.0;
    private int puntosAcumulados = 0;

    public FrmPedido() {
        setTitle("Gestión de Pedidos - Pan, Puntos y Premios");
        setSize(750, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel l1 = new JLabel("ID Cliente:");
        l1.setBounds(20, 20, 80, 25);
        add(l1);

        txtClienteId = new JTextField();
        txtClienteId.setBounds(100, 20, 80, 25);
        add(txtClienteId);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(190, 20, 80, 25);
        add(btnBuscar);

        lblClienteInfo = new JLabel("Cliente: No seleccionado | Puntos: 0");
        lblClienteInfo.setBounds(280, 20, 420, 25);
        add(lblClienteInfo);

        rbProducto = new JRadioButton("Producto", true);
        rbProducto.setBounds(20, 60, 90, 25);
        rbMenu = new JRadioButton("Menú", false);
        rbMenu.setBounds(110, 60, 80, 25);

        bgTipo = new ButtonGroup();
        bgTipo.add(rbProducto);
        bgTipo.add(rbMenu);
        add(rbProducto);
        add(rbMenu);

        JLabel l2 = new JLabel("ID Item:");
        l2.setBounds(200, 60, 60, 25);
        add(l2);

        txtItemId = new JTextField();
        txtItemId.setBounds(260, 60, 60, 25);
        add(txtItemId);

        JLabel l3 = new JLabel("Cantidad:");
        l3.setBounds(330, 60, 65, 25);
        add(l3);

        txtCantidad = new JTextField();
        txtCantidad.setBounds(395, 60, 50, 25);
        add(txtCantidad);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(460, 60, 90, 25);
        add(btnAgregar);

        modelo = new DefaultTableModel(new Object[]{"Tipo", "ID", "Nombre", "Cantidad", "Precio U.", "Subtotal", "Puntos"}, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 100, 690, 320);
        add(scroll);

        lblTotal = new JLabel("Total: Q0.00");
        lblTotal.setBounds(500, 430, 200, 25);
        add(lblTotal);

        lblPuntos = new JLabel("Puntos a ganar: 0");
        lblPuntos.setBounds(500, 460, 200, 25);
        add(lblPuntos);

        JButton btnEfectivo = new JButton("Cobrar (Efectivo)");
        btnEfectivo.setBounds(20, 440, 160, 40);
        add(btnEfectivo);

        JButton btnTarjeta = new JButton("Cobrar (Tarjeta)");
        btnTarjeta.setBounds(190, 440, 160, 40);
        add(btnTarjeta);

        btnBuscar.addActionListener(e -> buscarCliente());
        btnAgregar.addActionListener(e -> agregarItem());
        btnEfectivo.addActionListener(e -> procesarCobro("EFECTIVO"));
        btnTarjeta.addActionListener(e -> procesarCobro("TARJETA"));
    }

    private void buscarCliente() {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("SELECT nombre, puntos_acumulados, activo FROM CLIENTE WHERE id_cliente = ?");
            ps.setInt(1, Integer.parseInt(txtClienteId.getText()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("activo") == 0) {
                    JOptionPane.showMessageDialog(this, "El cliente está inactivo.");
                    return;
                }
                lblClienteInfo.setText("Cliente: " + rs.getString("nombre") + " | Puntos actuales: " + rs.getInt("puntos_acumulados"));
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no existe.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void agregarItem() {
        int id = Integer.parseInt(txtItemId.getText());
        int cant = Integer.parseInt(txtCantidad.getText());

        try (Connection cn = Conexion.getConexion()) {
            if (rbProducto.isSelected()) {
                PreparedStatement ps = cn.prepareStatement("SELECT nombre, categoria, precio, existencia, activo FROM PRODUCTO WHERE id_producto = ?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (rs.getInt("activo") == 0 || rs.getInt("existencia") < cant) {
                        JOptionPane.showMessageDialog(this, "Producto no disponible o sin stock suficiente.");
                        return;
                    }
                    ProductoIndividual p = new ProductoIndividual(id, rs.getString("nombre"), rs.getDouble("precio"), rs.getString("categoria"));
                    listaItems.add(p);
                    listaCantidades.add(cant);
                    double sub = p.getPrecio() * cant;
                    int pts = p.calcularPuntos(cant);
                    totalAcumulado += sub;
                    puntosAcumulados += pts;
                    modelo.addRow(new Object[]{"PRODUCTO", id, p.getNombre(), cant, p.getPrecio(), sub, pts});
                }
            } else {
                PreparedStatement ps = cn.prepareStatement("SELECT m.nombre, m.precio, m.id_sandwich, m.id_bebida, m.id_acompanamiento, "
                        + "p1.existencia AS e1, p2.existencia AS e2, p3.existencia AS e3 "
                        + "FROM MENU m "
                        + "JOIN PRODUCTO p1 ON m.id_sandwich = p1.id_producto "
                        + "JOIN PRODUCTO p2 ON m.id_bebida = p2.id_producto "
                        + "JOIN PRODUCTO p3 ON m.id_acompanamiento = p3.id_producto "
                        + "WHERE m.id_menu = ? AND m.activo = 1");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (rs.getInt("e1") < cant || rs.getInt("e2") < cant || rs.getInt("e3") < cant) {
                        JOptionPane.showMessageDialog(this, "No hay existencias suficientes de los componentes del menú.");
                        return;
                    }
                    MenuCompleto m = new MenuCompleto(id, rs.getString("nombre"), rs.getDouble("precio"), rs.getInt("id_sandwich"), rs.getInt("id_bebida"), rs.getInt("id_acompanamiento"));
                    listaItems.add(m);
                    listaCantidades.add(cant);
                    double sub = m.getPrecio() * cant;
                    int pts = m.calcularPuntos(cant);
                    totalAcumulado += sub;
                    puntosAcumulados += pts;
                    modelo.addRow(new Object[]{"MENU", id, m.getNombre(), cant, m.getPrecio(), sub, pts});
                }
            }
            lblTotal.setText("Total: Q" + String.format("%.2f", totalAcumulado));
            lblPuntos.setText("Puntos a ganar: " + puntosAcumulados);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void procesarCobro(String metodo) {
        if (listaItems.isEmpty()) {
            return;
        }

        if ("EFECTIVO".equals(metodo)) {
            String input = JOptionPane.showInputDialog(this, "Monto Total: Q" + totalAcumulado + "\nIngrese efectivo recibido:");
            if (input == null) return;
            double efectivo = Double.parseDouble(input);
            if (efectivo < totalAcumulado) {
                JOptionPane.showMessageDialog(this, "Monto insuficiente.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Cambio: Q" + (efectivo - totalAcumulado));
        } else {
            int resp = JOptionPane.showConfirmDialog(this, "¿Transacción en POS aprobada?", "Procesando Tarjeta", JOptionPane.YES_NO_OPTION);
            if (resp != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Pago rechazado.");
                return;
            }
        }

        try (Connection cn = Conexion.getConexion()) {
            cn.setAutoCommit(false);
            int idCliente = Integer.parseInt(txtClienteId.getText());

            PreparedStatement psPed = cn.prepareStatement("INSERT INTO PEDIDO (id_pedido, id_cliente, fecha, total, puntos_generados, metodo_pago, estado) "
                    + "VALUES ((SELECT NVL(MAX(id_pedido), 0) + 1 FROM PEDIDO), ?, SYSDATE, ?, ?, ?, 'PAGADO')");
            psPed.setInt(1, idCliente);
            psPed.setDouble(2, totalAcumulado);
            psPed.setInt(3, puntosAcumulados);
            psPed.setString(4, metodo);
            psPed.executeUpdate();

            Statement st = cn.createStatement();
            ResultSet rsMax = st.executeQuery("SELECT MAX(id_pedido) FROM PEDIDO");
            rsMax.next();
            int idPedido = rsMax.getInt(1);

            for (int i = 0; i < listaItems.size(); i++) {
                Venta item = listaItems.get(i);
                int cant = listaCantidades.get(i);

                PreparedStatement psDet = cn.prepareStatement("INSERT INTO DETALLE_PEDIDO (id_detalle, id_pedido, tipo_item, id_item, cantidad, precio_unitario, subtotal) "
                        + "VALUES ((SELECT NVL(MAX(id_detalle), 0) + 1 FROM DETALLE_PEDIDO), ?, ?, ?, ?, ?, ?)");
                psDet.setInt(1, idPedido);
                psDet.setString(2, item.getTipoItem());
                psDet.setInt(3, item.getId());
                psDet.setInt(4, cant);
                psDet.setDouble(5, item.getPrecio());
                psDet.setDouble(6, item.getPrecio() * cant);
                psDet.executeUpdate();

                if (item instanceof ProductoIndividual) {
                    PreparedStatement psInv = cn.prepareStatement("UPDATE PRODUCTO SET existencia = existencia - ? WHERE id_producto = ?");
                    psInv.setInt(1, cant);
                    psInv.setInt(2, item.getId());
                    psInv.executeUpdate();

                    PreparedStatement psHist = cn.prepareStatement("INSERT INTO HISTORIAL_INVENTARIO (id_movimiento, id_producto, fecha, cantidad, tipo_movimiento) "
                            + "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_INVENTARIO), ?, SYSDATE, ?, 'VENTA')");
                    psHist.setInt(1, item.getId());
                    psHist.setInt(2, cant);
                    psHist.executeUpdate();
                } else if (item instanceof MenuCompleto) {
                    MenuCompleto m = (MenuCompleto) item;
                    int[] ids = {m.getIdSandwich(), m.getIdBebida(), m.getIdAcompanamiento()};
                    for (int idComp : ids) {
                        PreparedStatement psInv = cn.prepareStatement("UPDATE PRODUCTO SET existencia = existencia - ? WHERE id_producto = ?");
                        psInv.setInt(1, cant);
                        psInv.setInt(2, idComp);
                        psInv.executeUpdate();

                        PreparedStatement psHist = cn.prepareStatement("INSERT INTO HISTORIAL_INVENTARIO (id_movimiento, id_producto, fecha, cantidad, tipo_movimiento) "
                                + "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_INVENTARIO), ?, SYSDATE, ?, 'VENTA')");
                        psHist.setInt(1, idComp);
                        psHist.setInt(2, cant);
                        psHist.executeUpdate();
                    }
                }
            }

            PreparedStatement psPts = cn.prepareStatement("UPDATE CLIENTE SET puntos_acumulados = puntos_acumulados + ? WHERE id_cliente = ?");
            psPts.setInt(1, puntosAcumulados);
            psPts.setInt(2, idCliente);
            psPts.executeUpdate();

            PreparedStatement psHP = cn.prepareStatement("INSERT INTO HISTORIAL_PUNTOS (id_movimiento, id_cliente, fecha, tipo, puntos, descripcion) "
                    + "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_PUNTOS), ?, SYSDATE, 'ACUMULACION', ?, ?)");
            psHP.setInt(1, idCliente);
            psHP.setInt(2, puntosAcumulados);
            psHP.setString(3, "Compra Pedido #" + idPedido);
            psHP.executeUpdate();

            cn.commit();
            JOptionPane.showMessageDialog(this, "Pedido registrado exitosamente. Comprobante generado.");
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en el pago: " + ex.getMessage());
        }
    }
}
