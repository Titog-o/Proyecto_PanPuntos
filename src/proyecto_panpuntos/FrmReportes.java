package proyecto_panpuntos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class FrmReportes extends JFrame {

    private JComboBox<String> cbReportes;
    private JTable tabla;
    private DefaultTableModel modelo;

    public FrmReportes() {
        setTitle("Reportes del Sistema");
        setSize(750, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel l1 = new JLabel("Seleccione Reporte:");
        l1.setBounds(20, 20, 150, 25);
        add(l1);

        cbReportes = new JComboBox<>(new String[]{
            "1. Ventas por período",
            "2. Productos más vendidos",
            "3. Productos agotados o con bajo inventario (<=5)",
            "4. Puntos acumulados por cliente",
            "5. Historial de canjes",
            "6. Ventas de menús completos"
        });
        cbReportes.setBounds(170, 20, 350, 25);
        add(cbReportes);

        JButton btnGenerar = new JButton("Generar");
        btnGenerar.setBounds(540, 20, 100, 25);
        add(btnGenerar);

        modelo = new DefaultTableModel();
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 60, 690, 370);
        add(scroll);

        btnGenerar.addActionListener(e -> generarReporte());
    }

    private void generarReporte() {
        int idx = cbReportes.getSelectedIndex();
        modelo.setRowCount(0);
        modelo.setColumnCount(0);

        try (Connection cn = Conexion.getConexion()) {
            Statement st = cn.createStatement();
            ResultSet rs;

            switch (idx) {
                case 0:
                    modelo.addColumn("ID Pedido");
                    modelo.addColumn("Cliente");
                    modelo.addColumn("Fecha");
                    modelo.addColumn("Método Pago");
                    modelo.addColumn("Total");
                    rs = st.executeQuery("SELECT p.id_pedido, c.nombre, p.fecha, p.metodo_pago, p.total FROM PEDIDO p JOIN CLIENTE c ON p.id_cliente = c.id_cliente WHERE p.estado = 'PAGADO'");
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), rs.getDouble(5)});
                    }
                    break;
                case 1:
                    modelo.addColumn("Producto");
                    modelo.addColumn("Unidades Vendidas");
                    rs = st.executeQuery("SELECT p.nombre, SUM(d.cantidad) FROM DETALLE_PEDIDO d JOIN PRODUCTO p ON d.id_item = p.id_producto WHERE d.tipo_item = 'PRODUCTO' GROUP BY p.nombre ORDER BY 2 DESC");
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getString(1), rs.getInt(2)});
                    }
                    break;
                case 2:
                    modelo.addColumn("ID");
                    modelo.addColumn("Nombre");
                    modelo.addColumn("Categoría");
                    modelo.addColumn("Existencia");
                    rs = st.executeQuery("SELECT id_producto, nombre, categoria, existencia FROM PRODUCTO WHERE existencia <= 5");
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4)});
                    }
                    break;
                case 3:
                    modelo.addColumn("ID Cliente");
                    modelo.addColumn("Nombre");
                    modelo.addColumn("Puntos Acumulados");
                    rs = st.executeQuery("SELECT id_cliente, nombre, puntos_acumulados FROM CLIENTE ORDER BY puntos_acumulados DESC");
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3)});
                    }
                    break;
                case 4:
                    modelo.addColumn("Cliente");
                    modelo.addColumn("Fecha");
                    modelo.addColumn("Descripción");
                    modelo.addColumn("Puntos Utilizados");
                    rs = st.executeQuery("SELECT c.nombre, h.fecha, h.descripcion, h.puntos FROM HISTORIAL_PUNTOS h JOIN CLIENTE c ON h.id_cliente = c.id_cliente WHERE h.tipo = 'CANJE'");
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getString(1), rs.getDate(2), rs.getString(3), rs.getInt(4)});
                    }
                    break;
                case 5:
                    modelo.addColumn("Menú");
                    modelo.addColumn("Cantidad Vendida");
                    modelo.addColumn("Importe Total (Q)");
                    rs = st.executeQuery("SELECT m.nombre, SUM(d.cantidad), SUM(d.subtotal) FROM DETALLE_PEDIDO d JOIN MENU m ON d.id_item = m.id_menu WHERE d.tipo_item = 'MENU' GROUP BY m.nombre");
                    while (rs.next()) {
                        modelo.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getDouble(3)});
                    }
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + ex.getMessage());
        }
    }
}