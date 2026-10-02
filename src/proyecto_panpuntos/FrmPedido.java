package proyecto_panpuntos;

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

    private ArrayList<ItemVenta> listaItems = new ArrayList<>();
    private ArrayList<Integer> listaCantidades = new ArrayList<>();
    private double totalAcumulado = 0.0;
    private int puntosAcumulados = 0;

    public FrmPedido() {
        setTitle("Gestión de Pedidos - Pan, Puntos y Premios");
        setSize(770, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        
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
        lblClienteInfo.setBounds(280, 20, 450, 25);
        add(lblClienteInfo);

        rbProducto = new JRadioButton("Producto", true);
        rbProducto.setBounds(20, 60, 90, 25);
        rbMenu = new JRadioButton("Menú", false);
        rbMenu.setBounds(110, 60, 70, 25);

        bgTipo = new ButtonGroup();
        bgTipo.add(rbProducto);
        bgTipo.add(rbMenu);
        add(rbProducto);
        add(rbMenu);

        JLabel l2 = new JLabel("ID Item:");
        l2.setBounds(190, 60, 55, 25);
        add(l2);

        txtItemId = new JTextField();
        txtItemId.setBounds(245, 60, 50, 25);
        add(txtItemId);

        JLabel l3 = new JLabel("Cantidad:");
        l3.setBounds(305, 60, 60, 25);
        add(l3);

        txtCantidad = new JTextField();
        txtCantidad.setBounds(365, 60, 45, 25);
        add(txtCantidad);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(420, 60, 90, 25);
        add(btnAgregar);

        JButton btnEliminar = new JButton("Eliminar Ítem");
        btnEliminar.setBounds(520, 60, 110, 25);
        add(btnEliminar);

        modelo = new DefaultTableModel(new Object[]{"Tipo", "ID", "Nombre", "Cantidad", "Precio U.", "Subtotal", "Puntos"}, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 100, 715, 320);
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
        btnEliminar.addActionListener(e -> eliminarItem());
        btnEfectivo.addActionListener(e -> procesarCobro("EFECTIVO"));
        btnTarjeta.addActionListener(e -> procesarCobro("TARJETA"));
    }

    private void buscarCliente() {
        if (txtClienteId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del cliente.");
            return;
        }

        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("SELECT nombre, puntos_acumulados, activo FROM CLIENTE WHERE id_cliente = ?");
            ps.setInt(1, Integer.parseInt(txtClienteId.getText().trim()));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getInt("activo") == 0) {
                    JOptionPane.showMessageDialog(this, "El cliente seleccionado está inactivo.");
                    return;
                }
                lblClienteInfo.setText("Cliente: " + rs.getString("nombre") + " | Puntos actuales: " + rs.getInt("puntos_acumulados"));
            } else {
                JOptionPane.showMessageDialog(this, "El cliente no existe.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + ex.getMessage());
        }
    }

    private void agregarItem() {
        if (txtItemId.getText().trim().isEmpty() || txtCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del ítem y la cantidad.");
            return;
        }

        int id = Integer.parseInt(txtItemId.getText().trim());
        int cant = Integer.parseInt(txtCantidad.getText().trim());

        if (cant <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
            return;
        }

        if (rbProducto.isSelected()) {
            agregarProducto(id, cant);
        } else {
            agregarMenu(id, cant);
        }
    }

    private void agregarProducto(int id, int cant) {
        try (Connection cn = Conexion.getConexion()) {
            PreparedStatement ps = cn.prepareStatement("SELECT nombre, categoria, precio, existencia, activo FROM PRODUCTO WHERE id_producto = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getInt("activo") == 0 || rs.getInt("existencia") < cant) {
                    JOptionPane.showMessageDialog(this, "Producto no disponible o stock insuficiente.");
                    return;
                }
                ProductoIndividual p = new ProductoIndividual(id, rs.getString("nombre"), rs.getDouble("precio"), rs.getString("categoria"));
                
                registrarEnTabla(p, cant, "PRODUCTO");
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void agregarMenu(int id, int cant) {
        try (Connection cn = Conexion.getConexion()) {
            String sql = "SELECT m.nombre, m.precio, m.id_sandwich, m.id_bebida, m.id_acompanamiento, "
                       + "p1.existencia AS e1, p2.existencia AS e2, p3.existencia AS e3 "
                       + "FROM MENU m "
                       + "JOIN PRODUCTO p1 ON m.id_sandwich = p1.id_producto "
                       + "JOIN PRODUCTO p2 ON m.id_bebida = p2.id_producto "
                       + "JOIN PRODUCTO p3 ON m.id_acompanamiento = p3.id_producto "
                       + "WHERE m.id_menu = ? AND m.activo = 1";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getInt("e1") < cant || rs.getInt("e2") < cant || rs.getInt("e3") < cant) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente en los componentes del menú.");
                    return;
                }
                MenuCompleto m = new MenuCompleto(id, rs.getString("nombre"), rs.getDouble("precio"), 
                        rs.getInt("id_sandwich"), rs.getInt("id_bebida"), rs.getInt("id_acompanamiento"));

                registrarEnTabla(m, cant, "MENU");
            } else {
                JOptionPane.showMessageDialog(this, "Menú no encontrado o inactivo.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void registrarEnTabla(ItemVenta item, int cant, String tipo) {
        listaItems.add(item);
        listaCantidades.add(cant);

        double subtotal = item.getPrecio() * cant;
        int puntos = item.calcularPuntos(cant);

        totalAcumulado += subtotal;
        puntosAcumulados += puntos;

        modelo.addRow(new Object[]{tipo, item.getId(), item.getNombre(), cant, item.getPrecio(), subtotal, puntos});
        actualizarTotales();

        txtItemId.setText("");
        txtCantidad.setText("");
    }

    private void eliminarItem() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem de la tabla para eliminar.");
            return;
        }

        ItemVenta item = listaItems.get(fila);
        int cant = listaCantidades.get(fila);

        totalAcumulado -= (item.getPrecio() * cant);
        puntosAcumulados -= item.calcularPuntos(cant);

        listaItems.remove(fila);
        listaCantidades.remove(fila);
        modelo.removeRow(fila);

        actualizarTotales();
        JOptionPane.showMessageDialog(this, "Ítem eliminado correctamente.");
    }

    private void actualizarTotales() {
        lblTotal.setText(String.format("Total: Q%.2f", totalAcumulado));
        lblPuntos.setText("Puntos a ganar: " + puntosAcumulados);
    }

    private void procesarCobro(String metodo) {
        if (listaItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto al pedido.");
            return;
        }

        double efectivo = 0.0;
        double cambio = 0.0;

        if ("EFECTIVO".equals(metodo)) {
            String input = JOptionPane.showInputDialog(this, String.format("Total a Pagar: Q%.2f\nIngrese efectivo recibido:", totalAcumulado));
            if (input == null) return;
            try {
                efectivo = Double.parseDouble(input);
                if (efectivo < totalAcumulado) {
                    JOptionPane.showMessageDialog(this, "Monto insuficiente.");
                    return;
                }
                cambio = efectivo - totalAcumulado;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Monto ingresado no es válido.");
                return;
            }
        } else {
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Transacción POS aprobada?", "Pago con Tarjeta", JOptionPane.YES_NO_OPTION);
            if (confirmacion != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Pago cancelado o rechazado.");
                return;
            }
        }

        guardarVentaYGenerarTicket(metodo, efectivo, cambio);
    }

    private void guardarVentaYGenerarTicket(String metodo, double efectivo, double cambio) {
        try (Connection cn = Conexion.getConexion()) {
            cn.setAutoCommit(false);
            int idCliente = Integer.parseInt(txtClienteId.getText().trim());

            PreparedStatement psPed = cn.prepareStatement(
                "INSERT INTO PEDIDO (id_pedido, id_cliente, fecha, total, puntos_generados, metodo_pago, estado) " +
                "VALUES ((SELECT NVL(MAX(id_pedido), 0) + 1 FROM PEDIDO), ?, SYSDATE, ?, ?, ?, 'PAGADO')"
            );
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
                ItemVenta item = listaItems.get(i);
                int cant = listaCantidades.get(i);

                PreparedStatement psDet = cn.prepareStatement(
                    "INSERT INTO DETALLE_PEDIDO (id_detalle, id_pedido, tipo_item, id_item, cantidad, precio_unitario, subtotal) " +
                    "VALUES ((SELECT NVL(MAX(id_detalle), 0) + 1 FROM DETALLE_PEDIDO), ?, ?, ?, ?, ?, ?)"
                );
                psDet.setInt(1, idPedido);
                psDet.setString(2, item.getTipoItem());
                psDet.setInt(3, item.getId());
                psDet.setInt(4, cant);
                psDet.setDouble(5, item.getPrecio());
                psDet.setDouble(6, item.getPrecio() * cant);
                psDet.executeUpdate();

                descontarStock(cn, item, cant);
            }

            PreparedStatement psPts = cn.prepareStatement("UPDATE CLIENTE SET puntos_acumulados = puntos_acumulados + ? WHERE id_cliente = ?");
            psPts.setInt(1, puntosAcumulados);
            psPts.setInt(2, idCliente);
            psPts.executeUpdate();

            PreparedStatement psHP = cn.prepareStatement(
                "INSERT INTO HISTORIAL_PUNTOS (id_movimiento, id_cliente, fecha, tipo, puntos, descripcion) " +
                "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_PUNTOS), ?, SYSDATE, 'ACUMULACION', ?, ?)"
            );
            psHP.setInt(1, idCliente);
            psHP.setInt(2, puntosAcumulados);
            psHP.setString(3, "Compra Pedido #" + idPedido);
            psHP.executeUpdate();

            cn.commit();

            mostrarComprobante(idPedido, metodo, efectivo, cambio);
            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error durante el cobro: " + ex.getMessage());
        }
    }

    private void descontarStock(Connection cn, ItemVenta item, int cantidad) throws SQLException {
        if (item instanceof ProductoIndividual) {
            actualizarStockProducto(cn, item.getId(), cantidad);
        } else if (item instanceof MenuCompleto) {
            MenuCompleto m = (MenuCompleto) item;
            actualizarStockProducto(cn, m.getIdSandwich(), cantidad);
            actualizarStockProducto(cn, m.getIdBebida(), cantidad);
            actualizarStockProducto(cn, m.getIdAcompanamiento(), cantidad);
        }
    }

    private void actualizarStockProducto(Connection cn, int idProducto, int cantidad) throws SQLException {
        PreparedStatement psInv = cn.prepareStatement("UPDATE PRODUCTO SET existencia = existencia - ? WHERE id_producto = ?");
        psInv.setInt(1, cantidad);
        psInv.setInt(2, idProducto);
        psInv.executeUpdate();

        PreparedStatement psHist = cn.prepareStatement(
            "INSERT INTO HISTORIAL_INVENTARIO (id_movimiento, id_producto, fecha, cantidad, tipo_movimiento) " +
            "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_INVENTARIO), ?, SYSDATE, ?, 'VENTA')"
        );
        psHist.setInt(1, idProducto);
        psHist.setInt(2, cantidad);
        psHist.executeUpdate();
    }

    private void mostrarComprobante(int idPedido, String metodo, double efectivo, double cambio) {
        StringBuilder ticket = new StringBuilder();
        ticket.append("=========================================\n");
        ticket.append("       PAN, PUNTOS Y PREMIOS            \n");
        ticket.append("          COMPROBANTE DE VENTA           \n");
        ticket.append("=========================================\n");
        ticket.append("Pedido No: ").append(idPedido).append("\n");
        ticket.append("Método de Pago: ").append(metodo).append("\n");
        ticket.append("-----------------------------------------\n");
        ticket.append(String.format("%-20s %-5s %-10s\n", "Producto", "Cant", "Subtotal"));
        ticket.append("-----------------------------------------\n");

        for (int i = 0; i < listaItems.size(); i++) {
            ItemVenta item = listaItems.get(i);
            int cant = listaCantidades.get(i);
            ticket.append(String.format("%-20s %-5d Q%-9.2f\n", item.getNombre(), cant, item.getPrecio() * cant));
        }

        ticket.append("-----------------------------------------\n");
        ticket.append(String.format("TOTAL COMPRA: Q%.2f\n", totalAcumulado));
        if ("EFECTIVO".equals(metodo)) {
            ticket.append(String.format("Efectivo Recibido: Q%.2f\n", efectivo));
            ticket.append(String.format("Cambio: Q%.2f\n", cambio));
        }
        ticket.append("Puntos Ganados: ").append(puntosAcumulados).append(" pts\n");
        ticket.append("=========================================\n");
        ticket.append("      ¡Gracias por su compra!           \n");

        JTextArea textArea = new JTextArea(ticket.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(350, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Comprobante Fiscal - Pedido #" + idPedido, JOptionPane.INFORMATION_MESSAGE);
    }
}