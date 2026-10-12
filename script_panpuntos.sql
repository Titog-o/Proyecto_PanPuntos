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

CREATE TABLE CLIENTE (
    id_cliente NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    telefono VARCHAR2(15),
    puntos_acumulados NUMBER DEFAULT 0,
    activo NUMBER(1) DEFAULT 1,
    CONSTRAINT chk_cliente_puntos CHECK (puntos_acumulados >= 0),
    CONSTRAINT chk_cliente_activo CHECK (activo IN (0, 1))
);

CREATE TABLE PRODUCTO (
    id_producto NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    categoria VARCHAR2(30),
    precio NUMBER(8,2) NOT NULL,
    existencia NUMBER NOT NULL,
    activo NUMBER(1) DEFAULT 1,
    CONSTRAINT chk_producto_categoria CHECK (categoria IN ('SANDWICH', 'BEBIDA', 'ACOMPANAMIENTO')),
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_existencia CHECK (existencia >= 0),
    CONSTRAINT chk_producto_activo CHECK (activo IN (0, 1))
);

CREATE TABLE MENU (
    id_menu NUMBER PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    precio NUMBER(8,2) NOT NULL,
    id_sandwich NUMBER REFERENCES PRODUCTO(id_producto),
    id_bebida NUMBER REFERENCES PRODUCTO(id_producto),
    id_acompanamiento NUMBER REFERENCES PRODUCTO(id_producto),
    activo NUMBER(1) DEFAULT 1,
    CONSTRAINT chk_menu_precio CHECK (precio >= 0),
    CONSTRAINT chk_menu_activo CHECK (activo IN (0, 1))
);

CREATE TABLE PEDIDO (
    id_pedido NUMBER PRIMARY KEY,
    id_cliente NUMBER REFERENCES CLIENTE(id_cliente),
    fecha DATE DEFAULT SYSDATE,
    total NUMBER(8,2) NOT NULL,
    puntos_generados NUMBER DEFAULT 0,
    metodo_pago VARCHAR2(20),
    estado VARCHAR2(20) DEFAULT 'PAGADO',
    CONSTRAINT chk_pedido_metodo CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA')),
    CONSTRAINT chk_pedido_estado CHECK (estado IN ('PENDIENTE', 'PAGADO', 'ENTREGADO', 'ANULADO'))
);

CREATE TABLE DETALLE_PEDIDO (
    id_detalle NUMBER PRIMARY KEY,
    id_pedido NUMBER REFERENCES PEDIDO(id_pedido),
    tipo_item VARCHAR2(20),
    id_item NUMBER NOT NULL,
    cantidad NUMBER NOT NULL,
    precio_unitario NUMBER(8,2) NOT NULL,
    subtotal NUMBER(8,2) NOT NULL,
    CONSTRAINT chk_detalle_tipo CHECK (tipo_item IN ('PRODUCTO', 'MENU')),
    CONSTRAINT chk_detalle_cant CHECK (cantidad > 0)
);

CREATE TABLE HISTORIAL_INVENTARIO (
    id_movimiento NUMBER PRIMARY KEY,
    id_producto NUMBER REFERENCES PRODUCTO(id_producto),
    fecha DATE DEFAULT SYSDATE,
    cantidad NUMBER NOT NULL,
    tipo_movimiento VARCHAR2(20),
    CONSTRAINT chk_hist_inv_tipo CHECK (tipo_movimiento IN ('INGRESO', 'VENTA', 'CANJE'))
);

CREATE TABLE HISTORIAL_PUNTOS (
    id_movimiento NUMBER PRIMARY KEY,
    id_cliente NUMBER REFERENCES CLIENTE(id_cliente),
    fecha DATE DEFAULT SYSDATE,
    tipo VARCHAR2(20),
    puntos NUMBER NOT NULL,
    descripcion VARCHAR2(200),
    CONSTRAINT chk_hist_pts_tipo CHECK (tipo IN ('ACUMULACION', 'CANJE'))
);

INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (1, 'tito', '57109010', 30, 1);
INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (2, 'alex', '4052870', 15, 1);
INSERT INTO CLIENTE (id_cliente, nombre, telefono, puntos_acumulados, activo) VALUES (3, 'juan perez', '57106040', 50, 1);

INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (101, 'Sandwich de Pollo', 'SANDWICH', 25.00, 30, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (102, 'Sandwich de Jamon', 'SANDWICH', 22.00, 25, 1);

INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (103, 'Gaseosa', 'BEBIDA', 8.00, 50, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (104, 'Te Frio', 'BEBIDA', 9.00, 40, 1);

INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (105, 'Papas Fritas', 'ACOMPANAMIENTO', 10.00, 35, 1);
INSERT INTO PRODUCTO (id_producto, nombre, categoria, precio, existencia, activo) VALUES (106, 'Aros de Cebolla', 'ACOMPANAMIENTO', 12.00, 20, 1);

INSERT INTO MENU (id_menu, nombre, precio, id_sandwich, id_bebida, id_acompanamiento, activo) VALUES (201, 'Menu Completo de Pollo', 37.00, 101, 103, 105, 1);
INSERT INTO MENU (id_menu, nombre, precio, id_sandwich, id_bebida, id_acompanamiento, activo) VALUES (202, 'Menu Completo de Jamon', 34.00, 102, 104, 106, 1);

COMMIT;
