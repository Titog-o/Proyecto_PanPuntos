-- ========================================================
-- PROYECTO: PAN, PUNTOS Y PREMIOS
-- SCRIPT PARA SUPABASE (POSTGRESQL EN LA NUBE)
-- ========================================================

-- 1. ELIMINAR TABLAS PREVIAS SI EXISTEN (ORDEN DE LLAVES FORANEAS)
DROP TABLE IF EXISTS DETALLE_PEDIDO CASCADE;
DROP TABLE IF EXISTS HISTORIAL_INVENTARIO CASCADE;
DROP TABLE IF EXISTS HISTORIAL_PUNTOS CASCADE;
DROP TABLE IF EXISTS MENU CASCADE;
DROP TABLE IF EXISTS PEDIDO CASCADE;
DROP TABLE IF EXISTS PRODUCTO CASCADE;
DROP TABLE IF EXISTS CLIENTE CASCADE;

-- 2. CREACION DE TABLAS

-- TABLA CLIENTE
CREATE TABLE CLIENTE (
    id_cliente INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(15),
    puntos_acumulados INT DEFAULT 0,
    activo INT DEFAULT 1,
    CONSTRAINT chk_cliente_puntos CHECK (puntos_acumulados >= 0),
    CONSTRAINT chk_cliente_activo CHECK (activo IN (0, 1))
);

-- TABLA PRODUCTO
CREATE TABLE PRODUCTO (
    id_producto INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(30),
    precio NUMERIC(8,2) NOT NULL,
    existencia INT NOT NULL,
    activo INT DEFAULT 1,
    CONSTRAINT chk_producto_categoria CHECK (categoria IN ('SANDWICH', 'BEBIDA', 'ACOMPANAMIENTO')),
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_existencia CHECK (existencia >= 0),
    CONSTRAINT chk_producto_activo CHECK (activo IN (0, 1))
);

-- TABLA MENU (COMBOS)
CREATE TABLE MENU (
    id_menu INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(8,2) NOT NULL,
    id_sandwich INT REFERENCES PRODUCTO(id_producto),
    id_bebida INT REFERENCES PRODUCTO(id_producto),
    id_acompanamiento INT REFERENCES PRODUCTO(id_producto),
    activo INT DEFAULT 1,
    CONSTRAINT chk_menu_precio CHECK (precio >= 0),
    CONSTRAINT chk_menu_activo CHECK (activo IN (0, 1))
);

-- TABLA PEDIDO
CREATE TABLE PEDIDO (
    id_pedido INT PRIMARY KEY,
    id_cliente INT REFERENCES CLIENTE(id_cliente),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(8,2) NOT NULL,
    puntos_generados INT DEFAULT 0,
    metodo_pago VARCHAR(20),
    estado VARCHAR(20) DEFAULT 'PAGADO',
    CONSTRAINT chk_pedido_metodo CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA')),
    CONSTRAINT chk_pedido_estado CHECK (estado IN ('PENDIENTE', 'PAGADO', 'ENTREGADO', 'ANULADO'))
);

-- TABLA DETALLE_PEDIDO
CREATE TABLE DETALLE_PEDIDO (
    id_detalle INT PRIMARY KEY,
    id_pedido INT REFERENCES PEDIDO(id_pedido),
    tipo_item VARCHAR(20),
    id_item INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario NUMERIC(8,2) NOT NULL,
    subtotal NUMERIC(8,2) NOT NULL,
    CONSTRAINT chk_detalle_tipo CHECK (tipo_item IN ('PRODUCTO', 'MENU')),
    CONSTRAINT chk_detalle_cant CHECK (cantidad > 0)
);

-- TABLA HISTORIAL_INVENTARIO
CREATE TABLE HISTORIAL_INVENTARIO (
    id_movimiento INT PRIMARY KEY,
    id_producto INT REFERENCES PRODUCTO(id_producto),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cantidad INT NOT NULL,
    tipo_movimiento VARCHAR(20),
    CONSTRAINT chk_hist_inv_tipo CHECK (tipo_movimiento IN ('INGRESO', 'VENTA', 'CANJE'))
);

-- TABLA HISTORIAL_PUNTOS
CREATE TABLE HISTORIAL_PUNTOS (
    id_movimiento INT PRIMARY KEY,
    id_cliente INT REFERENCES CLIENTE(id_cliente),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(20),
    puntos INT NOT NULL,
    descripcion VARCHAR(200),
    CONSTRAINT chk_hist_pts_tipo CHECK (tipo IN ('ACUMULACION', 'CANJE'))
);

-- DESACTIVAR ROW LEVEL SECURITY PARA ACCESO DIRECTO
ALTER TABLE CLIENTE DISABLE ROW LEVEL SECURITY;
ALTER TABLE PRODUCTO DISABLE ROW LEVEL SECURITY;
ALTER TABLE MENU DISABLE ROW LEVEL SECURITY;
ALTER TABLE PEDIDO DISABLE ROW LEVEL SECURITY;
ALTER TABLE DETALLE_PEDIDO DISABLE ROW LEVEL SECURITY;
ALTER TABLE HISTORIAL_INVENTARIO DISABLE ROW LEVEL SECURITY;
ALTER TABLE HISTORIAL_PUNTOS DISABLE ROW LEVEL SECURITY;

-- 3. DATOS DE PRUEBA OFICIALES

-- CLIENTES
INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (1, 'tito', '57109010', 30, 1);
INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (2, 'alex', '4052870', 15, 1);
INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (3, 'juan perez', '57106040', 50, 1);

-- PRODUCTOS INDIVIDUALES
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (101, 'Sandwich de Pollo', 'SANDWICH', 25.00, 30, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (102, 'Sandwich de Jamon', 'SANDWICH', 22.00, 25, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (103, 'Gaseosa', 'BEBIDA', 8.00, 50, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (104, 'Te Frio', 'BEBIDA', 9.00, 40, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (105, 'Papas Fritas', 'ACOMPANAMIENTO', 10.00, 35, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (106, 'Aros de Cebolla', 'ACOMPANAMIENTO', 12.00, 20, 1);

-- MENUS COMBOS
INSERT INTO MENU (id_menu, nombre, precio, id_sandwich, id_bebida, id_acompanamiento, activo) VALUES (201, 'Menu Completo de Pollo', 37.00, 101, 103, 105, 1);
INSERT INTO MENU (id_menu, nombre, precio, id_sandwich, id_bebida, id_acompanamiento, activo) VALUES (202, 'Menu Completo de Jamon', 34.00, 102, 104, 106, 1);
