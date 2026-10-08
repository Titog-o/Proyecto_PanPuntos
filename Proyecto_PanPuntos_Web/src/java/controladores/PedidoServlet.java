package controladores;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import proyecto_panpuntos.Conexion;

@WebServlet(name = "PedidoServlet", urlPatterns = {"/PedidoServlet"})
public class PedidoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection cn = Conexion.getConexion()) {
            
            // ==========================================
            // 1. CLIENTES: BUSCAR Y LISTAR
            // ==========================================
            if ("buscarCliente".equals(accion)) {
                String criterio = request.getParameter("criterio");
                String sql = "SELECT id_cliente, nombre, telefono, puntos_acumulados, activo FROM CLIENTE WHERE (TO_CHAR(id_cliente) = ? OR telefono = ?)";
                PreparedStatement ps = cn.prepareStatement(sql);
                ps.setString(1, criterio);
                ps.setString(2, criterio);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    out.printf("{\"id\":%d, \"nombre\":\"%s\", \"telefono\":\"%s\", \"puntos\":%d, \"activo\":%d}%n",
                            rs.getInt("id_cliente"), escapeJson(rs.getString("nombre")), escapeJson(rs.getString("telefono")),
                            rs.getInt("puntos_acumulados"), rs.getInt("activo"));
                } else {
                    response.setStatus(404);
                    out.print("{\"error\": \"Cliente no encontrado.\"}");
                }
            } 
            else if ("listarClientes".equals(accion)) {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT id_cliente, nombre, telefono, puntos_acumulados, activo FROM CLIENTE ORDER BY id_cliente");
                StringBuilder sb = new StringBuilder("[");
                boolean primero = true;
                while (rs.next()) {
                    if (!primero) sb.append(",");
                    sb.append(String.format("{\"id\":%d, \"nombre\":\"%s\", \"telefono\":\"%s\", \"puntos\":%d, \"activo\":%d}",
                            rs.getInt("id_cliente"), escapeJson(rs.getString("nombre")), escapeJson(rs.getString("telefono")),
                            rs.getInt("puntos_acumulados"), rs.getInt("activo")));
                    primero = false;
                }
                sb.append("]");
                out.print(sb.toString());
            }

            // ==========================================
            // 2. PRODUCTOS: BUSCAR Y LISTAR
            // ==========================================
            else if ("buscarProducto".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement ps = cn.prepareStatement("SELECT id_producto, nombre, categoria, precio, existencia, activo FROM PRODUCTO WHERE id_producto = ?");
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    out.printf("{\"id\":%d, \"nombre\":\"%s\", \"categoria\":\"%s\", \"precio\":%.2f, \"existencia\":%d, \"activo\":%d}%n",
                            rs.getInt("id_producto"), escapeJson(rs.getString("nombre")), escapeJson(rs.getString("categoria")),
                            rs.getDouble("precio"), rs.getInt("existencia"), rs.getInt("activo"));
                } else {
                    response.setStatus(404);
                    out.print("{\"error\": \"Producto no encontrado.\"}");
                }
            }
            else if ("listarProductos".equals(accion)) {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT id_producto, nombre, categoria, precio, existencia, activo FROM PRODUCTO ORDER BY id_producto");
                StringBuilder sb = new StringBuilder("[");
                boolean primero = true;
                while (rs.next()) {
                    if (!primero) sb.append(",");
                    sb.append(String.format("{\"id\":%d, \"nombre\":\"%s\", \"categoria\":\"%s\", \"precio\":%.2f, \"existencia\":%d, \"activo\":%d}",
                            rs.getInt("id_producto"), escapeJson(rs.getString("nombre")), escapeJson(rs.getString("categoria")),
                            rs.getDouble("precio"), rs.getInt("existencia"), rs.getInt("activo")));
                    primero = false;
                }
                sb.append("]");
                out.print(sb.toString());
            }

            // ==========================================
            // 3. POS: BUSCAR ITEM PARA VENTA
            // ==========================================
            else if ("buscarItem".equals(accion)) {
                String tipo = request.getParameter("tipo");
                int id = Integer.parseInt(request.getParameter("id"));

                if ("PRODUCTO".equals(tipo)) {
                    PreparedStatement ps = cn.prepareStatement("SELECT nombre, categoria, precio, existencia, activo FROM PRODUCTO WHERE id_producto = ?");
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getInt("activo") == 0) {
                            response.setStatus(400);
                            out.print("{\"error\": \"Producto inactivo.\"}");
                        } else {
                            out.printf("{\"id\":%d, \"nombre\":\"%s\", \"categoria\":\"%s\", \"precio\":%.2f, \"stock\":%d}%n",
                                    id, escapeJson(rs.getString("nombre")), escapeJson(rs.getString("categoria")), rs.getDouble("precio"), rs.getInt("existencia"));
                        }
                    } else {
                        response.setStatus(404);
                        out.print("{\"error\": \"Producto no encontrado.\"}");
                    }
                } else if ("MENU".equals(tipo)) {
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
                        int minStock = Math.min(rs.getInt("e1"), Math.min(rs.getInt("e2"), rs.getInt("e3")));
                        out.printf("{\"id\":%d, \"nombre\":\"%s\", \"categoria\":\"MENU\", \"precio\":%.2f, \"stock\":%d, \"idSandwich\":%d, \"idBebida\":%d, \"idAcomp\":%d}%n",
                                id, escapeJson(rs.getString("nombre")), rs.getDouble("precio"), minStock,
                                rs.getInt("id_sandwich"), rs.getInt("id_bebida"), rs.getInt("id_acompanamiento"));
                    } else {
                        response.setStatus(404);
                        out.print("{\"error\": \"Menú no encontrado o inactivo.\"}");
                    }
                }
            }

            // ==========================================
            // 4. FIDELIZACIÓN: CONSULTAR STOCK DE PREMIO
            // ==========================================
            else if ("consultarPremio".equals(accion)) {
                String categoria = request.getParameter("categoria");
                if ("MENU".equals(categoria)) {
                    String sql = "SELECT m.id_menu, m.nombre, LEAST(p1.existencia, p2.existencia, p3.existencia) AS stock_menu "
                               + "FROM MENU m "
                               + "JOIN PRODUCTO p1 ON m.id_sandwich = p1.id_producto "
                               + "JOIN PRODUCTO p2 ON m.id_bebida = p2.id_producto "
                               + "JOIN PRODUCTO p3 ON m.id_acompanamiento = p3.id_producto "
                               + "WHERE m.activo = 1 AND ROWNUM = 1";
                    PreparedStatement ps = cn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        out.printf("{\"id\":%d, \"nombre\":\"%s\", \"stock\":%d}%n",
                                rs.getInt("id_menu"), escapeJson(rs.getString("nombre")), rs.getInt("stock_menu"));
                    } else {
                        response.setStatus(404);
                        out.print("{\"error\": \"No hay menús disponibles.\"}");
                    }
                } else {
                    String sql = "SELECT id_producto, nombre, existencia FROM PRODUCTO WHERE categoria = ? AND activo = 1 AND existencia > 0 AND ROWNUM = 1";
                    PreparedStatement ps = cn.prepareStatement(sql);
                    ps.setString(1, categoria);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        out.printf("{\"id\":%d, \"nombre\":\"%s\", \"stock\":%d}%n",
                                rs.getInt("id_producto"), escapeJson(rs.getString("nombre")), rs.getInt("existencia"));
                    } else {
                        response.setStatus(404);
                        out.print("{\"error\": \"Sin stock para esta categoría de premio.\"}");
                    }
                }
            }

            // ==========================================
            // 5. REPORTES OFICIALES
            // ==========================================
            else if ("generarReporte".equals(accion)) {
                int tipo = Integer.parseInt(request.getParameter("tipo"));
                StringBuilder sb = new StringBuilder("{\"columnas\":[");
                Statement st = cn.createStatement();
                ResultSet rs = null;

                switch (tipo) {
                    case 0: // Ventas por período
                        sb.append("\"ID Pedido\",\"Cliente\",\"Fecha\",\"Método Pago\",\"Total\"], \"filas\":[");
                        rs = st.executeQuery("SELECT p.id_pedido, c.nombre, TO_CHAR(p.fecha, 'YYYY-MM-DD'), p.metodo_pago, p.total FROM PEDIDO p JOIN CLIENTE c ON p.id_cliente = c.id_cliente WHERE p.estado = 'PAGADO' ORDER BY p.id_pedido DESC");
                        boolean p0 = true;
                        while (rs.next()) {
                            if (!p0) sb.append(",");
                            sb.append(String.format("[\"%s\", \"%s\", \"%s\", \"%s\", \"Q%.2f\"]",
                                    rs.getString(1), escapeJson(rs.getString(2)), rs.getString(3), rs.getString(4), rs.getDouble(5)));
                            p0 = false;
                        }
                        break;
                    case 1: // Productos más vendidos
                        sb.append("\"Producto\",\"Unidades Vendidas\"], \"filas\":[");
                        rs = st.executeQuery("SELECT p.nombre, SUM(d.cantidad) FROM DETALLE_PEDIDO d JOIN PRODUCTO p ON d.id_item = p.id_producto WHERE d.tipo_item = 'PRODUCTO' GROUP BY p.nombre ORDER BY 2 DESC");
                        boolean p1 = true;
                        while (rs.next()) {
                            if (!p1) sb.append(",");
                            sb.append(String.format("[\"%s\", \"%s\"]", escapeJson(rs.getString(1)), rs.getString(2)));
                            p1 = false;
                        }
                        break;
                    case 2: // Productos bajo inventario
                        sb.append("\"ID\",\"Nombre\",\"Categoría\",\"Existencia\"], \"filas\":[");
                        rs = st.executeQuery("SELECT id_producto, nombre, categoria, existencia FROM PRODUCTO WHERE existencia <= 5 ORDER BY existencia ASC");
                        boolean p2 = true;
                        while (rs.next()) {
                            if (!p2) sb.append(",");
                            sb.append(String.format("[\"%s\", \"%s\", \"%s\", \"%s\"]",
                                    rs.getString(1), escapeJson(rs.getString(2)), rs.getString(3), rs.getString(4)));
                            p2 = false;
                        }
                        break;
                    case 3: // Puntos por cliente
                        sb.append("\"ID Cliente\",\"Nombre\",\"Teléfono\",\"Puntos Acumulados\"], \"filas\":[");
                        rs = st.executeQuery("SELECT id_cliente, nombre, telefono, puntos_acumulados FROM CLIENTE ORDER BY puntos_acumulados DESC");
                        boolean p3 = true;
                        while (rs.next()) {
                            if (!p3) sb.append(",");
                            sb.append(String.format("[\"%s\", \"%s\", \"%s\", \"%s pts\"]",
                                    rs.getString(1), escapeJson(rs.getString(2)), rs.getString(3), rs.getString(4)));
                            p3 = false;
                        }
                        break;
                    case 4: // Historial de canjes
                        sb.append("\"Cliente\",\"Fecha\",\"Descripción Recompensa\",\"Puntos Canjeados\"], \"filas\":[");
                        rs = st.executeQuery("SELECT c.nombre, TO_CHAR(h.fecha, 'YYYY-MM-DD HH24:MI'), h.descripcion, h.puntos FROM HISTORIAL_PUNTOS h JOIN CLIENTE c ON h.id_cliente = c.id_cliente WHERE h.tipo = 'CANJE' ORDER BY h.id_movimiento DESC");
                        boolean p4 = true;
                        while (rs.next()) {
                            if (!p4) sb.append(",");
                            sb.append(String.format("[\"%s\", \"%s\", \"%s\", \"%s pts\"]",
                                    escapeJson(rs.getString(1)), rs.getString(2), escapeJson(rs.getString(3)), rs.getString(4)));
                            p4 = false;
                        }
                        break;
                    case 5: // Ventas menús completos
                        sb.append("\"Menú Completo\",\"Cantidad Vendida\",\"Total Recaudado\"], \"filas\":[");
                        rs = st.executeQuery("SELECT m.nombre, SUM(d.cantidad), SUM(d.subtotal) FROM DETALLE_PEDIDO d JOIN MENU m ON d.id_item = m.id_menu WHERE d.tipo_item = 'MENU' GROUP BY m.nombre");
                        boolean p5 = true;
                        while (rs.next()) {
                            if (!p5) sb.append(",");
                            sb.append(String.format("[\"%s\", \"%s\", \"Q%.2f\"]",
                                    escapeJson(rs.getString(1)), rs.getString(2), rs.getDouble(3)));
                            p5 = false;
                        }
                        break;
                }
                sb.append("]}");
                out.print(sb.toString());
            }

        } catch (Exception ex) {
            response.setStatus(500);
            out.print("{\"error\": \"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String operacion = request.getParameter("operacion");

        try (Connection cn = Conexion.getConexion()) {

            // ==========================================
            // A. CLIENTES: GUARDAR, MODIFICAR, DESACTIVAR
            // ==========================================
            if ("guardarCliente".equals(operacion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String nombre = request.getParameter("nombre");
                String telefono = request.getParameter("telefono");

                PreparedStatement ps = cn.prepareStatement("INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (?, ?, ?, 0, 1)");
                ps.setInt(1, id);
                ps.setString(2, nombre);
                ps.setString(3, telefono);
                ps.executeUpdate();
                out.print("{\"exito\": true, \"mensaje\": \"Cliente registrado exitosamente con 0 puntos iniciales.\"}");
            }
            else if ("modificarCliente".equals(operacion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String nombre = request.getParameter("nombre");
                String telefono = request.getParameter("telefono");

                PreparedStatement ps = cn.prepareStatement("UPDATE CLIENTE SET nombre = ?, telefono = ? WHERE id_cliente = ?");
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.setInt(3, id);
                ps.executeUpdate();
                out.print("{\"exito\": true, \"mensaje\": \"Datos del cliente actualizados correctamente.\"}");
            }
            else if ("desactivarCliente".equals(operacion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement ps = cn.prepareStatement("UPDATE CLIENTE SET activo = 0 WHERE id_cliente = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                out.print("{\"exito\": true, \"mensaje\": \"Cliente desactivado.\"}");
            }

            // ==========================================
            // B. PRODUCTOS: GUARDAR, MODIFICAR, DESACTIVAR
            // ==========================================
            else if ("guardarProducto".equals(operacion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String nombre = request.getParameter("nombre");
                String categoria = request.getParameter("categoria");
                double precio = Double.parseDouble(request.getParameter("precio"));
                int stock = Integer.parseInt(request.getParameter("existencia"));

                if (precio < 0 || stock < 0) {
                    response.setStatus(400);
                    out.print("{\"exito\": false, \"error\": \"El precio y la existencia no pueden ser negativos.\"}");
                    return;
                }

                PreparedStatement ps = cn.prepareStatement("INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (?, ?, ?, ?, ?, 1)");
                ps.setInt(1, id);
                ps.setString(2, nombre);
                ps.setString(3, categoria);
                ps.setDouble(4, precio);
                ps.setInt(5, stock);
                ps.executeUpdate();
                out.print("{\"exito\": true, \"mensaje\": \"Producto registrado exitosamente.\"}");
            }
            else if ("modificarProducto".equals(operacion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String nombre = request.getParameter("nombre");
                String categoria = request.getParameter("categoria");
                double precio = Double.parseDouble(request.getParameter("precio"));
                int stock = Integer.parseInt(request.getParameter("existencia"));

                PreparedStatement ps = cn.prepareStatement("UPDATE PRODUCTO SET nombre = ?, categoria = ?, precio = ?, existencia = ? WHERE id_producto = ?");
                ps.setString(1, nombre);
                ps.setString(2, categoria);
                ps.setDouble(3, precio);
                ps.setInt(4, stock);
                ps.setInt(5, id);
                ps.executeUpdate();
                out.print("{\"exito\": true, \"mensaje\": \"Producto actualizado correctamente.\"}");
            }
            else if ("desactivarProducto".equals(operacion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                PreparedStatement ps = cn.prepareStatement("UPDATE PRODUCTO SET activo = 0 WHERE id_producto = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                out.print("{\"exito\": true, \"mensaje\": \"Producto desactivado del catálogo.\"}");
            }

            // ==========================================
            // C. INVENTARIO: ABASTECIMIENTO
            // ==========================================
            else if ("abastecer".equals(operacion)) {
                int idProd = Integer.parseInt(request.getParameter("idProducto"));
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));

                if (cantidad <= 0) {
                    response.setStatus(400);
                    out.print("{\"exito\": false, \"error\": \"La cantidad a ingresar debe ser mayor a cero.\"}");
                    return;
                }

                cn.setAutoCommit(false);
                PreparedStatement ps1 = cn.prepareStatement("UPDATE PRODUCTO SET existencia = existencia + ? WHERE id_producto = ?");
                ps1.setInt(1, cantidad);
                ps1.setInt(2, idProd);
                ps1.executeUpdate();

                PreparedStatement ps2 = cn.prepareStatement(
                    "INSERT INTO HISTORIAL_INVENTARIO (id_movimiento, id_producto, fecha, cantidad, tipo_movimiento) " +
                    "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_INVENTARIO), ?, SYSDATE, ?, 'INGRESO')"
                );
                ps2.setInt(1, idProd);
                ps2.setInt(2, cantidad);
                ps2.executeUpdate();

                cn.commit();
                out.print("{\"exito\": true, \"mensaje\": \"Inventario abastecido correctamente.\"}");
            }

            // ==========================================
            // D. FIDELIZACIÓN: PROCESAR CANJE DE PUNTOS
            // ==========================================
            else if ("procesarCanje".equals(operacion)) {
                int idCliente = Integer.parseInt(request.getParameter("idCliente"));
                int puntosRequeridos = Integer.parseInt(request.getParameter("puntosRequeridos"));
                String premioNombre = request.getParameter("premioNombre");
                String categoria = request.getParameter("categoria");
                int idPremio = Integer.parseInt(request.getParameter("idPremio"));

                cn.setAutoCommit(false);
                try {
                    // Validar saldo del cliente
                    PreparedStatement psCheck = cn.prepareStatement("SELECT puntos_acumulados FROM CLIENTE WHERE id_cliente = ? AND activo = 1");
                    psCheck.setInt(1, idCliente);
                    ResultSet rsC = psCheck.executeQuery();
                    if (!rsC.next() || rsC.getInt(1) < puntosRequeridos) {
                        cn.rollback();
                        response.setStatus(400);
                        out.print("{\"exito\": false, \"error\": \"Puntos insuficientes o cliente inactivo.\"}");
                        return;
                    }

                    // Descontar puntos
                    PreparedStatement psPts = cn.prepareStatement("UPDATE CLIENTE SET puntos_acumulados = puntos_acumulados - ? WHERE id_cliente = ?");
                    psPts.setInt(1, puntosRequeridos);
                    psPts.setInt(2, idCliente);
                    psPts.executeUpdate();

                    // Registrar en historial de puntos
                    PreparedStatement psHP = cn.prepareStatement(
                        "INSERT INTO HISTORIAL_PUNTOS (id_movimiento, id_cliente, fecha, tipo, puntos, descripcion) " +
                        "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_PUNTOS), ?, SYSDATE, 'CANJE', ?, ?)"
                    );
                    psHP.setInt(1, idCliente);
                    psHP.setInt(2, puntosRequeridos);
                    psHP.setString(3, "Canje: " + premioNombre);
                    psHP.executeUpdate();

                    // Descontar inventario
                    if ("MENU".equals(categoria)) {
                        PreparedStatement psM = cn.prepareStatement("SELECT id_sandwich, id_bebida, id_acompanamiento FROM MENU WHERE id_menu = ?");
                        psM.setInt(1, idPremio);
                        ResultSet rsM = psM.executeQuery();
                        if (rsM.next()) {
                            descontarItemInventario(cn, rsM.getInt("id_sandwich"), 1, "CANJE");
                            descontarItemInventario(cn, rsM.getInt("id_bebida"), 1, "CANJE");
                            descontarItemInventario(cn, rsM.getInt("id_acompanamiento"), 1, "CANJE");
                        }
                    } else {
                        descontarItemInventario(cn, idPremio, 1, "CANJE");
                    }

                    cn.commit();
                    out.print("{\"exito\": true, \"mensaje\": \"¡Canje realizado exitosamente!\"}");
                } catch (Exception ex) {
                    cn.rollback();
                    throw ex;
                }
            }

            // ==========================================
            // E. POS: COBRO DE VENTA
            // ==========================================
            else {
                int idCliente = Integer.parseInt(request.getParameter("idCliente"));
                double total = Double.parseDouble(request.getParameter("total"));
                int puntos = Integer.parseInt(request.getParameter("puntos"));
                String metodoPago = request.getParameter("metodoPago");
                String refTarjeta = request.getParameter("refTarjeta");
                double efectivo = Double.parseDouble(request.getParameter("efectivo"));
                double cambio = efectivo >= total ? (efectivo - total) : 0.0;

                String[] tipos = request.getParameterValues("itemTipo[]");
                String[] ids = request.getParameterValues("itemId[]");
                String[] cantidades = request.getParameterValues("itemCantidad[]");
                String[] precios = request.getParameterValues("itemPrecio[]");

                cn.setAutoCommit(false);
                try {
                    PreparedStatement psPed = cn.prepareStatement(
                        "INSERT INTO PEDIDO (id_pedido, id_cliente, fecha, total, puntos_generados, metodo_pago, estado) " +
                        "VALUES ((SELECT NVL(MAX(id_pedido), 0) + 1 FROM PEDIDO), ?, SYSDATE, ?, ?, ?, 'PAGADO')"
                    );
                    psPed.setInt(1, idCliente);
                    psPed.setDouble(2, total);
                    psPed.setInt(3, puntos);
                    psPed.setString(4, metodoPago + (refTarjeta != null && !refTarjeta.isEmpty() ? " (Ref: " + refTarjeta + ")" : ""));
                    psPed.executeUpdate();

                    Statement st = cn.createStatement();
                    ResultSet rsMax = st.executeQuery("SELECT MAX(id_pedido) FROM PEDIDO");
                    rsMax.next();
                    int idPedido = rsMax.getInt(1);

                    if (ids != null) {
                        for (int i = 0; i < ids.length; i++) {
                            String tipo = tipos[i];
                            int idItem = Integer.parseInt(ids[i]);
                            int cant = Integer.parseInt(cantidades[i]);
                            double prec = Double.parseDouble(precios[i]);

                            PreparedStatement psDet = cn.prepareStatement(
                                "INSERT INTO DETALLE_PEDIDO (id_detalle, id_pedido, tipo_item, id_item, cantidad, precio_unitario, subtotal) " +
                                "VALUES ((SELECT NVL(MAX(id_detalle), 0) + 1 FROM DETALLE_PEDIDO), ?, ?, ?, ?, ?, ?)"
                            );
                            psDet.setInt(1, idPedido);
                            psDet.setString(2, tipo);
                            psDet.setInt(3, idItem);
                            psDet.setInt(4, cant);
                            psDet.setDouble(5, prec);
                            psDet.setDouble(6, prec * cant);
                            psDet.executeUpdate();

                            if ("PRODUCTO".equals(tipo)) {
                                descontarItemInventario(cn, idItem, cant, "VENTA");
                            } else {
                                PreparedStatement psM = cn.prepareStatement("SELECT id_sandwich, id_bebida, id_acompanamiento FROM MENU WHERE id_menu = ?");
                                psM.setInt(1, idItem);
                                ResultSet rsM = psM.executeQuery();
                                if (rsM.next()) {
                                    descontarItemInventario(cn, rsM.getInt("id_sandwich"), cant, "VENTA");
                                    descontarItemInventario(cn, rsM.getInt("id_bebida"), cant, "VENTA");
                                    descontarItemInventario(cn, rsM.getInt("id_acompanamiento"), cant, "VENTA");
                                }
                            }
                        }
                    }

                    PreparedStatement psPts = cn.prepareStatement("UPDATE CLIENTE SET puntos_acumulados = puntos_acumulados + ? WHERE id_cliente = ?");
                    psPts.setInt(1, puntos);
                    psPts.setInt(2, idCliente);
                    psPts.executeUpdate();

                    PreparedStatement psHP = cn.prepareStatement(
                        "INSERT INTO HISTORIAL_PUNTOS (id_movimiento, id_cliente, fecha, tipo, puntos, descripcion) " +
                        "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_PUNTOS), ?, SYSDATE, 'ACUMULACION', ?, ?)"
                    );
                    psHP.setInt(1, idCliente);
                    psHP.setInt(2, puntos);
                    psHP.setString(3, "Compra Pedido #" + idPedido);
                    psHP.executeUpdate();

                    cn.commit();
                    out.printf("{\"exito\": true, \"idPedido\": %d, \"cambio\": %.2f}%n", idPedido, cambio);
                } catch (Exception ex) {
                    cn.rollback();
                    throw ex;
                }
            }

        } catch (Exception ex) {
            response.setStatus(500);
            out.print("{\"exito\": false, \"error\": \"" + escapeJson(ex.getMessage()) + "\"}");
        }
    }

    private void descontarItemInventario(Connection cn, int idProd, int cantidad, String motivo) throws SQLException {
        PreparedStatement psInv = cn.prepareStatement("UPDATE PRODUCTO SET existencia = existencia - ? WHERE id_producto = ?");
        psInv.setInt(1, cantidad);
        psInv.setInt(2, idProd);
        psInv.executeUpdate();

        PreparedStatement psHist = cn.prepareStatement(
            "INSERT INTO HISTORIAL_INVENTARIO (id_movimiento, id_producto, fecha, cantidad, tipo_movimiento) " +
            "VALUES ((SELECT NVL(MAX(id_movimiento), 0) + 1 FROM HISTORIAL_INVENTARIO), ?, SYSDATE, ?, ?)"
        );
        psHist.setInt(1, idProd);
        psHist.setInt(2, cantidad);
        psHist.setString(3, motivo);
        psHist.executeUpdate();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}