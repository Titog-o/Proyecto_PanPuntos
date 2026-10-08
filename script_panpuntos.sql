-- ========================================================
-- PROYECTO: PAN, PUNTOS Y PREMIOS
-- SCRIPT DE CREACION DE TABLAS Y DATOS DE PRUEBA
-- MOTOR: ORACLE DATABASE (XE / EE)
-- ========================================================

-- ELIMINACION DE TABLAS PREVIAS (ORDEN DE DEPENDENCIAS)
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE DETALLE_PEDIDO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE HISTORIAL_INVENTARIO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE HISTORIAL_PUNTOS CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE MENU CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE PEDIDO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE PRODUCTO CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE CLIENTE CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- 1. TABLA: CLIENTE
CREATE TABLE CLIENTE (
    id_cliente NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    telefono VARCHAR2(20) NOT NULL,
    puntos_acumulados NUMBER DEFAULT 0 NOT NULL,
    activo NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT chk_cliente_puntos CHECK (puntos_acumulados >= 0),
    CONSTRAINT chk_cliente_activo CHECK (activo IN (0, 1))
);

-- 2. TABLA: PRODUCTO
CREATE TABLE PRODUCTO (
    id_producto NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    categoria VARCHAR2(30) NOT NULL, -- SANDWICH, BEBIDA, ACOMPANAMIENTO, POSTRE, OTRO
    precio NUMBER(8,2) NOT NULL,
    existencia NUMBER NOT NULL,
    activo NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_existencia CHECK (existencia >= 0),
    CONSTRAINT chk_producto_activo CHECK (activo IN (0, 1))
);

-- 3. TABLA: MENU (COMBOS)
CREATE TABLE MENU (
    id_menu NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    precio NUMBER(8,2) NOT NULL,
    id_sandwich NUMBER NOT NULL,
    id_bebida NUMBER NOT NULL,
    id_acompanamiento NUMBER NOT NULL,
    activo NUMBER(1) DEFAULT 1 NOT NULL,
    CONSTRAINT fk_menu_sandwich FOREIGN KEY (id_sandwich) REFERENCES PRODUCTO(id_producto),
    CONSTRAINT fk_menu_bebida FOREIGN KEY (id_bebida) REFERENCES PRODUCTO(id_producto),
    CONSTRAINT fk_menu_acomp FOREIGN KEY (id_acompanamiento) REFERENCES PRODUCTO(id_producto),
    CONSTRAINT chk_menu_precio CHECK (precio >= 0),
    CONSTRAINT chk_menu_activo CHECK (activo IN (0, 1))
);

-- 4. TABLA: PEDIDO
CREATE TABLE PEDIDO (
    id_pedido NUMBER PRIMARY KEY,
    id_cliente NUMBER NOT NULL,
    fecha DATE DEFAULT SYSDATE NOT NULL,
    total NUMBER(8,2) NOT NULL,
    puntos_generados NUMBER DEFAULT 0 NOT NULL,
    metodo_pago VARCHAR2(100) NOT NULL, -- EFECTIVO o TARJETA
    estado VARCHAR2(20) DEFAULT 'PAGADO' NOT NULL, -- PENDIENTE, PAGADO, ENTREGADO, ANULADO
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente),
    CONSTRAINT chk_pedido_total CHECK (total >= 0),
    CONSTRAINT chk_pedido_estado CHECK (estado IN ('PENDIENTE', 'PAGADO', 'ENTREGADO', 'ANULADO'))
);

-- 5. TABLA: DETALLE_PEDIDO
CREATE TABLE DETALLE_PEDIDO (
    id_detalle NUMBER PRIMARY KEY,
    id_pedido NUMBER NOT NULL,
    tipo_item VARCHAR2(20) NOT NULL, -- PRODUCTO o MENU
    id_item NUMBER NOT NULL,
    cantidad NUMBER NOT NULL,
    precio_unitario NUMBER(8,2) NOT NULL,
    subtotal NUMBER(8,2) NOT NULL,
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES PEDIDO(id_pedido),
    CONSTRAINT chk_detalle_cant CHECK (cantidad > 0)
);

-- 6. TABLA: HISTORIAL_INVENTARIO (KARDEX)
CREATE TABLE HISTORIAL_INVENTARIO (
    id_movimiento NUMBER PRIMARY KEY,
    id_producto NUMBER NOT NULL,
    fecha DATE DEFAULT SYSDATE NOT NULL,
    cantidad NUMBER NOT NULL,
    tipo_movimiento VARCHAR2(20) NOT NULL, -- INGRESO, VENTA, CANJE
    CONSTRAINT fk_hist_inv_prod FOREIGN KEY (id_producto) REFERENCES PRODUCTO(id_producto)
);

-- 7. TABLA: HISTORIAL_PUNTOS
CREATE TABLE HISTORIAL_PUNTOS (
    id_movimiento NUMBER PRIMARY KEY,
    id_cliente NUMBER NOT NULL,
    fecha DATE DEFAULT SYSDATE NOT NULL,
    tipo VARCHAR2(20) NOT NULL, -- ACUMULACION o CANJE
    puntos NUMBER NOT NULL,
    descripcion VARCHAR2(200) NOT NULL,
    CONSTRAINT fk_hist_pts_cli FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente)
);

-- ========================================================
-- DATOS DE PRUEBA INICIALES
-- ========================================================

-- CLIENTES (Cliente 1 con saldo para canjes, Cliente 2 recién registrado)
INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (1, 'Juan Perez', '5551234', 20, 1);
INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (2, 'Maria Lopez', '5559876', 0, 1);
INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (3, 'Carlos Gomez', '5554321', 50, 1);

-- PRODUCTOS INDIVIDUALES
-- Sandwiches (+2 pts)
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (101, 'Sandwich de Pollo', 'SANDWICH', 25.00, 30, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (102, 'Sandwich de Jamon', 'SANDWICH', 22.00, 25, 1);

-- Bebidas (0 pts)
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (103, 'Gaseosa', 'BEBIDA', 8.00, 50, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (104, 'Te Frio', 'BEBIDA', 9.00, 40, 1);

-- Acompanamientos (0 pts)
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (105, 'Papas Fritas', 'ACOMPANAMIENTO', 10.00, 35, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (106, 'Aros de Cebolla', 'ACOMPANAMIENTO', 12.00, 20, 1);

-- MENUS COMPLETOS (COMBOS) (+8 pts)
INSERT INTO MENU (id_menu, nombre, precio, id_sandwich, id_bebida, id_acompanamiento, activo) VALUES (201, 'Menu Completo de Pollo', 37.00, 101, 103, 105, 1);
INSERT INTO MENU (id_menu, nombre, precio, id_sandwich, id_bebida, id_acompanamiento, activo) VALUES (202, 'Menu Completo de Jamon', 34.00, 102, 104, 106, 1);

COMMIT;
