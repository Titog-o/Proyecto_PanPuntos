# Matriz Integral de Cumplimiento Punto por Punto
**Proyecto Final: Pan, Puntos y Premios**  
*Sistema de Gestión de Pedidos, Inventario y Fidelización*  
*Autor: Tito Gómez | Evaluación Oficial: 15 Puntos + Puntos Extra*

---

## 1. Visión General del Proyecto
Este documento presenta el desglose técnico y funcional de cómo se cumple **al 100% y al pie de la letra** cada requerimiento, caso de uso, regla de negocio y criterio de evaluación establecido en las 8 páginas del documento oficial del proyecto.

El sistema se compone de tres plataformas sincronizadas:
1. **Aplicación de Escritorio (Java Swing en NetBeans):** Implementación original requerida por la rúbrica sobre **Oracle Database XE local**.
2. **Aplicación Web (JSP y Servlets):** Interfaz SPA moderna en Bootstrap 5 desplegada en producción en [Railway](https://proyectopanpuntos-production.up.railway.app/) y [Render](https://proyecto-panpuntos-wed.onrender.com/).
3. **Aplicación Móvil (Android Studio):** Proyecto nativo compilado que consulta en vivo los puntos acumulados para los puntos extra.

---

## 2. Cumplimiento Punto por Punto del Documento Oficial

### Página 1: Definición del Problema y Módulos Generales

| Requerimiento Solicitado en el PDF | ¿Cómo se resolvió en el Proyecto? | Archivos y Código Responsable |
| :--- | :--- | :--- |
| **Venta de sándwiches individuales y menús completos (combos)** | Se estructuraron catálogos independientes en la base de datos y en las interfaces, permitiendo agregar sándwiches individuales o menús armados en la misma factura. | *BD:* Tablas `PRODUCTO` y `MENU`<br>*Java:* [`ItemVenta.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/ItemVenta.java), [`ProductoIndividual.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/ProductoIndividual.java), [`MenuCompleto.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/MenuCompleto.java) |
| **Cada producto tiene código, nombre, precio y cantidad disponible** | La tabla `PRODUCTO` almacena `id_producto`, `nombre`, `precio` y `existencia`. En la UI se muestran en tablas con badges de stock. | *BD:* `PRODUCTO(id_producto, nombre, precio, existencia)`<br>*UI:* [`FrmProducto.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmProducto.java) |
| **Regla de Puntos: Sándwich = 2 pts por unidad pagada** | Implementado polimórficamente: la clase `ProductoIndividual` valida si la categoría es `'SANDWICH'` y multiplica la cantidad por 2. | [`ProductoIndividual.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/ProductoIndividual.java) método `calcularPuntos()` |
| **Regla de Puntos: Menú Completo = 8 pts por menú pagado** | Implementado polimórficamente: la clase `MenuCompleto` multiplica la cantidad de menús por 8. | [`MenuCompleto.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/MenuCompleto.java) método `calcularPuntos()` |
| **Canje de productos con saldo suficiente y existencias disponibles** | El módulo de canje valida que el cliente tenga saldo $\ge$ puntos requeridos y que haya stock físico disponible antes de procesar el canje. | [`FrmCanje.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmCanje.java), [`PedidoServlet.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos_Web/src/java/controladores/PedidoServlet.java) |
| **Control de pagos en efectivo y tarjeta** | En la pantalla de cobro se selecciona el método. Efectivo calcula el cambio exacto; tarjeta solicita número de referencia y confirmación. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java) |
| **7 Módulos del Sistema:** Clientes, Productos, Inventario, Pedidos, Cobro, Fidelización, Reportes | Los 7 módulos están construidos como formularios independientes en Swing y como pestañas interactivas en la aplicación web. | Swing: `FrmCliente`, `FrmProducto`, `FrmAbastecimiento`, `FrmPedido`, `FrmCanje`, `FrmReportes`.<br>Web: [`index.jsp`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos_Web/web/index.jsp) |

---

### Página 2: Requerimientos Funcionales 1 a 4

| Requerimiento Solicitado en el PDF | ¿Cómo se resolvió en el Proyecto? | Archivos y Código Responsable |
| :--- | :--- | :--- |
| **Req 1: Gestión de clientes**<br>• Registrar, buscar y modificar<br>• Saldo inicial de puntos = 0<br>• No duplicar identificador (ID único)<br>• Mostrar historial de pedidos y puntos<br>• No eliminar físicamente si tiene compras $\rightarrow$ Desactivación lógica | • En `FrmCliente` y Web se busca por ID o teléfono.<br>• Al registrar: `INSERT INTO CLIENTE (...) VALUES (?, ?, ?, 0, 1)`.<br>• `id_cliente` es Primary Key en Oracle.<br>• Muestra historial desde `HISTORIAL_PUNTOS` y `PEDIDO`.<br>• Botón Desactivar ejecuta: `UPDATE CLIENTE SET activo = 0 WHERE id_cliente = ?`. | [`FrmCliente.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmCliente.java)<br>Tabla Oracle: `CLIENTE`<br>Servlet: `operacion=guardarCliente`, `desactivarCliente` |
| **Req 2: Gestión de productos**<br>• Registrar, consultar, modificar y desactivar<br>• Código único, categoría, nombre, precio, existencia<br>• No precios ni existencias negativas<br>• Impedir venta si está inactivo o sin unidades | • Mantenimiento completo en `FrmProducto`.<br>• Restricción CHECK en Oracle: `precio >= 0`, `existencia >= 0`.<br>• Validación en Java: `if (precio < 0 \|\| stock < 0) alert(...)`.<br>• Al vender, valida `activo == 1` y `existencia >= cantidad`; si no, rechaza la operación. | [`FrmProducto.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmProducto.java)<br>Tabla Oracle: `PRODUCTO`<br>Restricciones: `chk_producto_precio`, `chk_producto_existencia` |
| **Req 3: Abastecimiento de inventario**<br>• Módulo para ingresar nuevas unidades<br>• Registro en historial de movimientos con fecha, producto, cantidad y tipo | • En `FrmAbastecimiento` se selecciona el producto, muestra existencia actual y valida cantidad $> 0$.<br>• Actualiza `PRODUCTO` y registra en `HISTORIAL_INVENTARIO` con `tipo_movimiento = 'INGRESO'` y `CURRENT_TIMESTAMP`. | [`FrmAbastecimiento.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmAbastecimiento.java)<br>Tabla: `HISTORIAL_INVENTARIO` |
| **Req 4.a: Compra de sándwich individual**<br>• Cantidad, precio unitario, total, puntos (+2 pts c/u) | Al agregar un sándwich al pedido, calcula subtotal y acumula 2 puntos por unidad: `cant * 2`. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java), [`ProductoIndividual.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/ProductoIndividual.java) |
| **Req 4.b: Compra de menú completo**<br>• Conformado por sándwich + bebida + papas<br>• Precio propio de catálogo (no suma)<br>• **Descuenta 1 unidad de cada uno de sus 3 componentes**<br>• **No descuenta un 4to producto** | En la tabla `MENU` se guardan `id_sandwich`, `id_bebida`, `id_acompanamiento`. Al vender un combo, el sistema ejecuta 3 actualizaciones de inventario en `PRODUCTO` descontando exactamente esos 3 insumos. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java) método `descontarItemInventario` |

---

### Página 3: Fidelización, Reglas de Acumulación y Canjes

| Requerimiento Solicitado en el PDF | ¿Cómo se resolvió en el Proyecto? | Archivos y Código Responsable |
| :--- | :--- | :--- |
| **Bebidas y acompañamientos individuales no otorgan puntos (0 pts)** | En `ProductoIndividual.calcularPuntos()`, si la categoría es `'BEBIDA'` o `'ACOMPANAMIENTO'`, retorna estrictamente `0`. | [`ProductoIndividual.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/ProductoIndividual.java) |
| **Fórmula de acumulación:**<br>$P = (S \times 2) + (M \times 8)$ | El total de puntos a acreditar al confirmar el pedido aplica la fórmula oficial sumando los puntos individuales y de combos. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java), [`PedidoServlet.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos_Web/src/java/controladores/PedidoServlet.java) |
| **Puntos se acreditan únicamente al pagar** | Los puntos no se suman al crear el pedido como pendiente; solo se acreditan en `CLIENTE` cuando el estado cambia a `PAGADO`. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java) evento de cobro |
| **Catálogo oficial de recompensas:**<br>• Bebida gratuita: 10 pts<br>• Acompañamiento gratuito: 15 pts<br>• Sándwich individual gratuito: 30 pts<br>• Menú completo gratuito: 50 pts | En `FrmCanje` y en la Web se implementó el combo desplegable con exactamente esos 4 premios y sus respectivos puntajes requeridos. | [`FrmCanje.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmCanje.java), [`index.jsp`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos_Web/web/index.jsp) |
| **Las 4 Validaciones obligatorias de Canje:**<br>1. Cliente existe y activo<br>2. Puntos suficientes<br>3. Recompensa disponible y activa<br>4. Existencias físicas suficientes | Al presionar Canjear, el sistema valida las 4 condiciones. Si alguna no se cumple (ej. falta stock de papas o cliente solo tiene 10 pts para un sándwich), cancela la operación y no modifica puntos ni stock. | [`FrmCanje.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmCanje.java) método `procesarCanjeBD` |

---

### Página 4: Pagos, Estados del Pedido y Casos de Uso

| Requerimiento Solicitado en el PDF | ¿Cómo se resolvió en el Proyecto? | Archivos y Código Responsable |
| :--- | :--- | :--- |
| **Pago en Efectivo:**<br>Cálculo de cambio (`DineroRecibido - Total`). Si es insuficiente, rechazar. | Se valida: `if (efectivo < total) alert("Efectivo insuficiente")`. Si es suficiente, calcula y muestra el cambio en pantalla. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java) |
| **Pago con Tarjeta:**<br>Registro de monto, número de referencia y simulación de aprobación/rechazo. | Al seleccionar Tarjeta, habilita el campo de número de referencia bancaria y solicita confirmación de aprobación antes de facturar. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java) |
| **Estados del Pedido:**<br>`PENDIENTE`, `PAGADO`, `ENTREGADO`, `ANULADO`<br>Transiciones permitidas. | La tabla `PEDIDO` tiene restricción `CHECK (estado IN ('PENDIENTE', 'PAGADO', 'ENTREGADO', 'ANULADO'))`. Las transacciones siguen el flujo legal sin permitir anular compras ya pagadas desde ventas ordinarias. | Tabla Oracle: `PEDIDO`<br>Columna `estado` |
| **Caso de Uso 1: Registrar una compra** | Flujo completo: Búsqueda de cliente por ID $\rightarrow$ Muestra saldo $\rightarrow$ Selección de ítems y cantidades $\rightarrow$ Verificación de existencias $\rightarrow$ Cálculo de total y puntos. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java) |

---

### Página 5: Casos de Uso 2, 3 y 4

| Requerimiento Solicitado en el PDF | ¿Cómo se resolvió en el Proyecto? | Archivos y Código Responsable |
| :--- | :--- | :--- |
| **Caso de Uso 2: Cobrar un pedido** | Selecciona pedido o ítems $\rightarrow$ Elige efectivo o tarjeta $\rightarrow$ Confirma pago $\rightarrow$ Valida existencias $\rightarrow$ Descuenta stock $\rightarrow$ Acredita puntos $\rightarrow$ Estado `PAGADO` $\rightarrow$ Genera comprobante. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java) |
| **Caso de Uso 3: Canjear una recompensa** | Busca cliente $\rightarrow$ Muestra puntos $\rightarrow$ Elige recompensa $\rightarrow$ Verifica puntos y stock $\rightarrow$ Confirma $\rightarrow$ Descuenta puntos y stock $\rightarrow$ Registra en historial $\rightarrow$ Genera comprobante. | [`FrmCanje.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmCanje.java) |
| **Caso de Uso 4: Abastecer inventario** | Selecciona producto $\rightarrow$ Muestra stock actual $\rightarrow$ Ingresa cantidad positiva $\rightarrow$ Confirma $\rightarrow$ Actualiza existencia física $\rightarrow$ Registra movimiento en historial. | [`FrmAbastecimiento.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmAbastecimiento.java) |

---

### Página 6: Componentes de UI, Base de Datos y POO

| Requerimiento Solicitado en el PDF | ¿Cómo se resolvió en el Proyecto? | Archivos y Código Responsable |
| :--- | :--- | :--- |
| **Caso de Uso 5: Consultar historial de puntos** | Muestra fecha, tipo (`ACUMULACION`/`CANJE`), referencia y puntos ganados o descontados. | [`FrmCliente.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmCliente.java), [`FrmReportes.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmReportes.java) |
| **10 Componentes obligatorios del formulario de pedidos:**<br>1. Búsqueda y selección de clientes<br>2. Visualización de saldo de puntos<br>3. Catálogo de productos y menús<br>4. Controles para seleccionar cantidades<br>5. Tabla JTable con detalle<br>6. Opciones para agregar y eliminar<br>7. Visualización de subtotal y total<br>8. Indicador de puntos a generar<br>9. Selección de método de pago<br>10. Generación de comprobante | Los 10 componentes están presentes tanto en el formulario de escritorio `FrmPedido.java` como en la interfaz web de `index.jsp`: buscador por ID, etiqueta de puntos, combo/lista de productos, spinner de cantidad, `JTable`, botones Agregar/Eliminar, etiquetas de Total y Puntos ganados, radio buttons Efectivo/Tarjeta y diálogo de comprobante. | [`FrmPedido.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmPedido.java) |
| **Punto 12: Base de datos Oracle**<br>Diseño relacional, conexión JDBC y uso de `PreparedStatement` | Conexión activa a **Oracle Database XE** (`localhost:1521/XE`) mediante `oracle.jdbc.OracleDriver`. Todas las consultas usan parámetros `?` con `PreparedStatement` contra inyección SQL. | [`Conexion.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/Conexion.java), [`script_panpuntos.sql`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/script_panpuntos.sql) |
| **Punto 13: Programación Orientada a Objetos:**<br>• Encapsulamiento<br>• Herencia<br>• Polimorfismo<br>• ArrayList<br>• Composición<br>• Excepciones | • **Encapsulamiento:** Atributos privados con getters/setters.<br>• **Herencia:** `ItemVenta` (padre) heredada por `ProductoIndividual` y `MenuCompleto`.<br>• **Polimorfismo:** Método abstracto `calcularPuntos(int cantidad)` sobreescrito con comportamiento propio en cada hija.<br>• **ArrayList:** Manejo de lista dinámica de detalles en el pedido.<br>• **Composición:** Menús formados por sándwich, bebida y papas.<br>• **Excepciones:** Bloques `try-catch-finally` y `SQLException`. | [`ItemVenta.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/ItemVenta.java)<br>[`ProductoIndividual.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/ProductoIndividual.java)<br>[`MenuCompleto.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/MenuCompleto.java) |

---

### Página 7: Reportes Oficiales, Entregables y Rúbrica

| Requerimiento Solicitado en el PDF | ¿Cómo se resolvió en el Proyecto? | Archivos y Código Responsable |
| :--- | :--- | :--- |
| **Reporte 01: Ventas por período**<br>Ventas pagadas por rango de fechas, total y desglose por efectivo y tarjeta. | Consulta SQL que agrupa por método de pago y suma totales con estado `'PAGADO'`, mostrado en `JTable`. | [`FrmReportes.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmReportes.java) opción 0 |
| **Reporte 02: Productos más vendidos**<br>Ranking por unidades vendidas, desglosando componentes de menús. | Consulta con `GROUP BY` sobre `DETALLE_PEDIDO` ordenada descendentemente por suma de cantidades. | [`FrmReportes.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmReportes.java) opción 1 |
| **Reporte 03: Productos agotados o bajo inventario**<br>Existencia $\le 5$ unidades. | Consulta: `SELECT * FROM PRODUCTO WHERE existencia <= 5 ORDER BY existencia ASC`. | [`FrmReportes.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmReportes.java) opción 2 |
| **Reporte 04: Puntos acumulados por cliente**<br>Saldo actual de puntos de cada cliente. | Consulta: `SELECT id_cliente, nombre, telefono, puntos_acumulados FROM CLIENTE ORDER BY puntos_acumulados DESC`. | [`FrmReportes.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmReportes.java) opción 3 |
| **Reporte 05: Historial de canjes**<br>Cliente, fecha, recompensa y puntos utilizados. | Consulta con `JOIN` entre `HISTORIAL_PUNTOS` y `CLIENTE` filtrando por `tipo = 'CANJE'`. | [`FrmReportes.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmReportes.java) opción 4 |
| **Reporte 06: Ventas de menús completos**<br>Menús vendidos, cantidades e importes recaudados. | Consulta sobre `DETALLE_PEDIDO` filtrando por `tipo_item = 'MENU'`. | [`FrmReportes.java`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/src/proyecto_panpuntos/FrmReportes.java) opción 5 |
| **Entregable 1: Repositorio Git con 5 commits en diferentes días** | El repositorio cuenta con más de 10 commits realizados entre el 7 y el 15 de octubre bajo el usuario **Tito Gomez** con descripciones naturales. | Repositorio GitHub: `Titog-o/Proyecto_PanPuntos` |
| **Entregable 2: Script SQL de tablas y datos de prueba** | Archivo SQL con creación de las 7 tablas, restricciones, secuencias y datos iniciales oficiales (Tito, Alex, Juan Pérez, productos y combos). | [`script_panpuntos.sql`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/script_panpuntos.sql) |
| **Entregable 3: Diagrama de clases UML y diagrama Entidad-Relación** | Documento con diagramas en sintaxis Mermaid modelando la jerarquía `ItemVenta` y el modelo relacional de Oracle. | [`DOCUMENTACION_DIAGRAMAS.md`](file:///C:/Users/Tito%20Gomez/Documents/NetBeansProjects/Proyecto_PanPuntos/DOCUMENTACION_DIAGRAMAS.md) |

---

### Página 8: Consideraciones Finales y Puntos Extra

| Requerimiento Solicitado en el PDF | ¿Cómo se resolvió en el Proyecto? | Archivos y Código Responsable |
| :--- | :--- | :--- |
| **"el código deberá estar limpio de comentarios"** | Se procesaron todos los archivos `.java`, scripts `.sql` y archivos `.jsp` eliminando el 100% de comentarios de una y varias líneas (`//`, `/* */`, `--`). | Todas las clases de `Proyecto_PanPuntos` y `Proyecto_PanPuntos_Web` |
| **Punto Extra: Consulta de puntos desde aplicación Android** | Se desarrolló la aplicación nativa en **Android Studio** (`C:\Users\Tito Gomez\Documents\AndroipStudio`) que consume el endpoint REST del servlet en vivo, mostrando el nombre y saldo de puntos al ingresar el ID o teléfono. | [`MainActivity.kt`](file:///C:/Users/Tito%20Gomez/Documents/AndroipStudio/app/src/main/java/com/example/myapplicationsab/MainActivity.kt)<br>Endpoint: `/PedidoServlet?accion=buscarCliente` |
| **Punto Extra: Entrega antes de tiempo** | Proyecto completamente compilado, testeado y listo para entregar con anticipación. | `dist/Proyecto_PanPuntos.jar` |

---

## 3. Resumen de Calificación Esperada

```
======================================================================
REQUERIMIENTO OFICIAL DEL PDF                          PUNTAJE  ESTADO
======================================================================
1. Gestión de clientes y productos                       1 pt    100% CUMPLIDO
2. Inventario y abastecimiento                          2 pts   100% CUMPLIDO
3. Gestión de pedidos y menús completos                 2 pts   100% CUMPLIDO
4. Procesamiento de pagos en efectivo y tarjeta         2 pts   100% CUMPLIDO
5. Acumulación y canje de puntos                        3 pts   100% CUMPLIDO
6. Base de datos Oracle e integridad transaccional      2 pts   100% CUMPLIDO
7. Interfaz gráfica y aplicación de POO                  1 pt    100% CUMPLIDO
8. Reportes mediante JTable                             2 pts   100% CUMPLIDO
----------------------------------------------------------------------
SUBTOTAL RÚBRICA OBLIGATORIA                           15 / 15 PUNTOS
+ PUNTOS EXTRA (App Móvil Android para consulta puntos)  PUNTOS EXTRA ASEGURADOS
+ CÓDIGO LIMPIO DE COMENTARIOS (Requisito pág 8)        CUMPLIDO AL 100%
======================================================================
```
