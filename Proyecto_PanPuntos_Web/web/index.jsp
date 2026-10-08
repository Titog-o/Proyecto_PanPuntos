<%@page isELIgnored="true" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Pan, Puntos y Premios - Sistema Integral</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .ticket-box {
            font-family: 'Courier New', Courier, monospace;
            background-color: #fffdf5;
            border: 2px dashed #bbb;
        }
        .nav-tabs .nav-link {
            font-weight: 600;
            color: #495057;
        }
        .nav-tabs .nav-link.active {
            color: #0d6efd;
            border-bottom: 3px solid #0d6efd;
        }
    </style>
</head>
<body class="bg-light">

    <!-- BARRA SUPERIOR -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container-fluid">
            <span class="navbar-brand fw-bold fs-4">
                <i class="bi bi-shop text-warning me-2"></i>Pan, Puntos y Premios
            </span>
            <span class="navbar-text text-light small d-none d-md-block">
                Sistema de Gestión de Pedidos, Inventario y Fidelización
            </span>
        </div>
    </nav>

    <!-- NAVEGACIÓN POR PESTAÑAS (LOS MÓDULOS DEL SISTEMA) -->
    <div class="container-fluid bg-white border-bottom shadow-sm">
        <div class="container">
            <ul class="nav nav-tabs border-0 pt-2" id="modulosTab" role="tablist">
                <li class="nav-item">
                    <button class="nav-link active" id="pos-tab" data-bs-toggle="tab" data-bs-target="#tab-pos" type="button" role="tab">
                        <i class="bi bi-cart4 text-primary"></i> 1. Caja / POS
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link" id="clientes-tab" data-bs-toggle="tab" data-bs-target="#tab-clientes" type="button" role="tab" onclick="cargarClientes()">
                        <i class="bi bi-people text-success"></i> 2. Clientes
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link" id="productos-tab" data-bs-toggle="tab" data-bs-target="#tab-productos" type="button" role="tab" onclick="cargarProductos()">
                        <i class="bi bi-box-seam text-danger"></i> 3. Productos
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link" id="inventario-tab" data-bs-toggle="tab" data-bs-target="#tab-inventario" type="button" role="tab">
                        <i class="bi bi-arrow-up-circle text-info"></i> 4. Abastecimiento
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link" id="canje-tab" data-bs-toggle="tab" data-bs-target="#tab-canje" type="button" role="tab">
                        <i class="bi bi-gift text-warning"></i> 5. Canje de Premios
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link" id="reportes-tab" data-bs-toggle="tab" data-bs-target="#tab-reportes" type="button" role="tab">
                        <i class="bi bi-graph-up text-secondary"></i> 6. Reportes
                    </button>
                </li>
            </ul>
        </div>
    </div>

    <!-- CONTENIDO DE LOS MÓDULOS -->
    <div class="container my-4">
        <div class="tab-content" id="modulosTabContent">

            <!-- ========================================== -->
            <!-- 1. PESTAÑA: PUNTO DE VENTA (POS / CAJA)   -->
            <!-- ========================================== -->
            <div class="tab-pane fade show active" id="tab-pos" role="tabpanel">
                <div class="row g-3">
                    <div class="col-lg-5">
                        <div class="card shadow-sm mb-3">
                            <div class="card-header bg-primary text-white fw-bold">
                                <i class="bi bi-person-fill"></i> 1. Identificar Cliente
                            </div>
                            <div class="card-body">
                                <div class="input-group">
                                    <input type="text" id="txtClienteCriterio" class="form-control" placeholder="ID o Teléfono">
                                    <button class="btn btn-outline-primary" onclick="buscarClientePos()">
                                        <i class="bi bi-search"></i> Buscar
                                    </button>
                                </div>
                                <div id="infoClientePos" class="mt-2 text-muted small">
                                    <em>Cliente no seleccionado</em>
                                </div>
                            </div>
                        </div>

                        <div class="card shadow-sm">
                            <div class="card-header bg-success text-white fw-bold">
                                <i class="bi bi-plus-square"></i> 2. Agregar a la Venta
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rbTipo" id="rbProducto" value="PRODUCTO" checked>
                                        <label class="form-check-label fw-bold" for="rbProducto">Producto</label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rbTipo" id="rbMenu" value="MENU">
                                        <label class="form-check-label fw-bold" for="rbMenu">Menú Completo</label>
                                    </div>
                                </div>
                                <div class="row g-2 mb-3">
                                    <div class="col-7">
                                        <label class="form-label small fw-bold">ID del Ítem:</label>
                                        <input type="number" id="txtItemId" class="form-control" placeholder="Ej: 101">
                                    </div>
                                    <div class="col-5">
                                        <label class="form-label small fw-bold">Cantidad:</label>
                                        <input type="number" id="txtCantidad" class="form-control" value="1" min="1">
                                    </div>
                                </div>
                                <button class="btn btn-success w-100 fw-bold" onclick="agregarItemPos()">
                                    <i class="bi bi-plus-circle"></i> Agregar Ítem
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-7">
                        <div class="card shadow-sm h-100 d-flex flex-column">
                            <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                                <span class="fw-bold"><i class="bi bi-receipt"></i> Detalle del Pedido</span>
                                <span class="badge bg-warning text-dark fs-6" id="badgePuntos">0 Pts a Ganar</span>
                            </div>
                            <div class="card-body p-0 flex-grow-1">
                                <div class="table-responsive" style="max-height: 280px;">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>Tipo</th>
                                                <th>Nombre</th>
                                                <th>Cant</th>
                                                <th>Precio</th>
                                                <th>Subtotal</th>
                                                <th>Puntos</th>
                                                <th></th>
                                            </tr>
                                        </thead>
                                        <tbody id="tablaItemsPos">
                                            <tr><td colspan="7" class="text-center text-muted py-4">No hay productos agregados</td></tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="card-footer bg-white border-top p-3">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <span class="h4 mb-0 text-secondary">Total a Pagar:</span>
                                    <span class="h2 mb-0 text-success fw-bold" id="lblTotalPos">Q0.00</span>
                                </div>
                                <div class="row g-2">
                                    <div class="col-6">
                                        <button class="btn btn-primary w-100 py-3 fw-bold fs-5" onclick="abrirCobroPos('EFECTIVO')">
                                            <i class="bi bi-cash-stack"></i> Cobrar (Efectivo)
                                        </button>
                                    </div>
                                    <div class="col-6">
                                        <button class="btn btn-secondary w-100 py-3 fw-bold fs-5" onclick="abrirCobroPos('TARJETA')">
                                            <i class="bi bi-credit-card"></i> Cobrar (Tarjeta)
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ========================================== -->
            <!-- 2. PESTAÑA: GESTIÓN DE CLIENTES           -->
            <!-- ========================================== -->
            <div class="tab-pane fade" id="tab-clientes" role="tabpanel">
                <div class="row g-3">
                    <div class="col-lg-4">
                        <div class="card shadow-sm">
                            <div class="card-header bg-success text-white fw-bold">
                                <i class="bi bi-person-plus"></i> Administrar Cliente
                            </div>
                            <div class="card-body">
                                <div class="mb-2">
                                    <label class="form-label small fw-bold">ID Cliente:</label>
                                    <input type="number" id="cId" class="form-control" placeholder="Ej: 1">
                                </div>
                                <div class="mb-2">
                                    <label class="form-label small fw-bold">Nombre Completo:</label>
                                    <input type="text" id="cNombre" class="form-control" placeholder="Ej: Juan Pérez">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label small fw-bold">Teléfono:</label>
                                    <input type="text" id="cTelefono" class="form-control" placeholder="Ej: 555-1234">
                                </div>
                                <div class="d-grid gap-2">
                                    <button class="btn btn-success fw-bold" onclick="guardarCliente()"><i class="bi bi-save"></i> Guardar Nuevo (0 pts)</button>
                                    <button class="btn btn-outline-primary" onclick="buscarClienteForm()"><i class="bi bi-search"></i> Buscar por ID</button>
                                    <button class="btn btn-outline-warning text-dark" onclick="modificarCliente()"><i class="bi bi-pencil"></i> Modificar Datos</button>
                                    <button class="btn btn-outline-danger" onclick="desactivarCliente()"><i class="bi bi-person-x"></i> Desactivar Cliente</button>
                                    <button class="btn btn-light border" onclick="limpiarClienteForm()">Limpiar Campos</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-8">
                        <div class="card shadow-sm">
                            <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                                <span class="fw-bold"><i class="bi bi-people"></i> Listado de Clientes Registrados</span>
                                <button class="btn btn-sm btn-outline-light" onclick="cargarClientes()"><i class="bi bi-arrow-clockwise"></i> Refrescar</button>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive" style="max-height: 400px;">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID</th>
                                                <th>Nombre</th>
                                                <th>Teléfono</th>
                                                <th>Puntos Acumulados</th>
                                                <th>Estado</th>
                                            </tr>
                                        </thead>
                                        <tbody id="tablaClientes">
                                            <tr><td colspan="5" class="text-center text-muted py-3">Cargando clientes...</td></tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ========================================== -->
            <!-- 3. PESTAÑA: GESTIÓN DE PRODUCTOS          -->
            <!-- ========================================== -->
            <div class="tab-pane fade" id="tab-productos" role="tabpanel">
                <div class="row g-3">
                    <div class="col-lg-4">
                        <div class="card shadow-sm">
                            <div class="card-header bg-danger text-white fw-bold">
                                <i class="bi bi-box"></i> Administrar Producto
                            </div>
                            <div class="card-body">
                                <div class="mb-2">
                                    <label class="form-label small fw-bold">ID / Código Único:</label>
                                    <input type="number" id="pId" class="form-control" placeholder="Ej: 101">
                                </div>
                                <div class="mb-2">
                                    <label class="form-label small fw-bold">Nombre del Producto:</label>
                                    <input type="text" id="pNombre" class="form-control" placeholder="Ej: Sandwich de Pollo">
                                </div>
                                <div class="mb-2">
                                    <label class="form-label small fw-bold">Categoría:</label>
                                    <select id="pCategoria" class="form-select">
                                        <option value="SANDWICH">SANDWICH (+2 pts)</option>
                                        <option value="BEBIDA">BEBIDA (0 pts)</option>
                                        <option value="ACOMPANAMIENTO">ACOMPANAMIENTO (0 pts)</option>
                                        <option value="POSTRE">POSTRE</option>
                                        <option value="OTRO">OTRO</option>
                                    </select>
                                </div>
                                <div class="row g-2 mb-3">
                                    <div class="col-6">
                                        <label class="form-label small fw-bold">Precio (Q):</label>
                                        <input type="number" step="0.01" id="pPrecio" class="form-control" placeholder="0.00">
                                    </div>
                                    <div class="col-6">
                                        <label class="form-label small fw-bold">Existencia Inicial:</label>
                                        <input type="number" id="pExistencia" class="form-control" placeholder="0">
                                    </div>
                                </div>
                                <div class="d-grid gap-2">
                                    <button class="btn btn-danger fw-bold" onclick="guardarProducto()"><i class="bi bi-save"></i> Registrar Producto</button>
                                    <button class="btn btn-outline-primary" onclick="buscarProductoForm()"><i class="bi bi-search"></i> Buscar por ID</button>
                                    <button class="btn btn-outline-warning text-dark" onclick="modificarProducto()"><i class="bi bi-pencil"></i> Modificar</button>
                                    <button class="btn btn-outline-secondary" onclick="desactivarProducto()"><i class="bi bi-trash"></i> Desactivar</button>
                                    <button class="btn btn-light border" onclick="limpiarProductoForm()">Limpiar</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-8">
                        <div class="card shadow-sm">
                            <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                                <span class="fw-bold"><i class="bi bi-card-list"></i> Catálogo de Productos</span>
                                <button class="btn btn-sm btn-outline-light" onclick="cargarProductos()"><i class="bi bi-arrow-clockwise"></i> Refrescar</button>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive" style="max-height: 400px;">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID</th>
                                                <th>Nombre</th>
                                                <th>Categoría</th>
                                                <th>Precio</th>
                                                <th>Existencia</th>
                                                <th>Estado</th>
                                            </tr>
                                        </thead>
                                        <tbody id="tablaProductos">
                                            <tr><td colspan="6" class="text-center text-muted py-3">Cargando catálogo...</td></tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ========================================== -->
            <!-- 4. PESTAÑA: ABASTECIMIENTO DE INVENTARIO   -->
            <!-- ========================================== -->
            <div class="tab-pane fade" id="tab-inventario" role="tabpanel">
                <div class="row justify-content-center">
                    <div class="col-md-7 col-lg-6">
                        <div class="card shadow-sm">
                            <div class="card-header bg-info text-dark fw-bold fs-5">
                                <i class="bi bi-arrow-up-circle-fill"></i> Abastecimiento de Inventario
                            </div>
                            <div class="card-body p-4">
                                <p class="text-muted small">Seleccione un producto para verificar su existencia actual e ingresar nuevas unidades.</p>
                                <div class="mb-3">
                                    <label class="form-label fw-bold">ID del Producto:</label>
                                    <div class="input-group">
                                        <input type="number" id="abastId" class="form-control" placeholder="Ej: 101">
                                        <button class="btn btn-info" onclick="consultarStockAbastecer()">
                                            <i class="bi bi-search"></i> Consultar Stock
                                        </button>
                                    </div>
                                </div>
                                <div class="alert alert-secondary py-2 mb-3" id="abastStockInfo">
                                    Existencia actual: <strong>-</strong>
                                </div>
                                <div class="mb-4">
                                    <label class="form-label fw-bold">Cantidad a Ingresar (+):</label>
                                    <input type="number" id="abastCantidad" class="form-control form-control-lg text-center" placeholder="Ej: 15" min="1">
                                </div>
                                <button class="btn btn-success btn-lg w-100 fw-bold shadow-sm" onclick="confirmarAbastecimiento()">
                                    <i class="bi bi-check2-circle"></i> Confirmar Abastecimiento
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ========================================== -->
            <!-- 5. PESTAÑA: FIDELIZACIÓN Y CANJE DE PREMIOS -->
            <!-- ========================================== -->
            <div class="tab-pane fade" id="tab-canje" role="tabpanel">
                <div class="row justify-content-center">
                    <div class="col-lg-8">
                        <div class="card shadow-sm">
                            <div class="card-header bg-warning text-dark fw-bold fs-5">
                                <i class="bi bi-gift-fill me-2"></i> Programa de Fidelización - Canje de Premios
                            </div>
                            <div class="card-body p-4">
                                
                                <!-- Búsqueda de Cliente -->
                                <div class="row mb-3">
                                    <div class="col-md-7">
                                        <label class="form-label fw-bold">ID o Teléfono del Cliente:</label>
                                        <div class="input-group">
                                            <input type="text" id="canjeCriterio" class="form-control" placeholder="Ej: 1">
                                            <button class="btn btn-warning fw-bold" onclick="buscarClienteCanje()">
                                                <i class="bi bi-search"></i> Consultar Saldo
                                            </button>
                                        </div>
                                    </div>
                                    <div class="col-md-5 d-flex align-items-end">
                                        <div class="border rounded p-2 bg-light w-100 text-center" id="canjeSaldoBox">
                                            <span class="small text-muted d-block">Puntos Disponibles:</span>
                                            <strong class="fs-4 text-warning" id="canjePuntosDisponibles">0 pts</strong>
                                        </div>
                                    </div>
                                </div>
                                <div id="canjeClienteInfo" class="mb-3 text-muted small"><em>Cliente no consultado</em></div>
                                <hr>

                                <!-- Catálogo Oficial de Recompensas -->
                                <h6 class="fw-bold mb-3"><i class="bi bi-stars text-warning"></i> Catálogo Oficial de Recompensas:</h6>
                                <div class="mb-3">
                                    <select id="canjeComboPremios" class="form-select form-select-lg" onchange="evaluarPremio()">
                                        <option value="">-- Seleccione una recompensa --</option>
                                        <option value="1" data-pts="10" data-cat="BEBIDA">Bebida Gratuita (10 pts)</option>
                                        <option value="2" data-pts="15" data-cat="ACOMPANAMIENTO">Acompañamiento Gratuito (15 pts)</option>
                                        <option value="3" data-pts="30" data-cat="SANDWICH">Sándwich Individual Gratuito (30 pts)</option>
                                        <option value="4" data-pts="50" data-cat="MENU">Menú Completo Gratuito (50 pts)</option>
                                    </select>
                                </div>

                                <div class="alert alert-info py-2" id="canjeStockInfo">
                                    Disponibilidad en inventario: <em>Seleccione una recompensa</em>
                                </div>

                                <button class="btn btn-warning btn-lg w-100 fw-bold mt-2 shadow-sm" onclick="procesarCanjePremio()">
                                    <i class="bi bi-gift"></i> Procesar Canje de Puntos
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ========================================== -->
            <!-- 6. PESTAÑA: REPORTES OBLIGATORIOS         -->
            <!-- ========================================== -->
            <div class="tab-pane fade" id="tab-reportes" role="tabpanel">
                <div class="card shadow-sm">
                    <div class="card-header bg-dark text-white fw-bold d-flex justify-content-between align-items-center">
                        <span><i class="bi bi-file-earmark-bar-graph"></i> Reportes del Sistema</span>
                    </div>
                    <div class="card-body">
                        <div class="row g-2 mb-3 align-items-end">
                            <div class="col-md-8">
                                <label class="form-label fw-bold small">Seleccione el Reporte a Generar:</label>
                                <select id="cbReportes" class="form-select">
                                    <option value="0">1. Ventas por período (Desglose Efectivo / Tarjeta)</option>
                                    <option value="1">2. Productos más vendidos</option>
                                    <option value="2">3. Productos agotados o con bajo inventario (&lt;= 5)</option>
                                    <option value="3">4. Puntos acumulados por cliente</option>
                                    <option value="4">5. Historial de canjes realizados</option>
                                    <option value="5">6. Ventas de menús completos</option>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <button class="btn btn-primary w-100 fw-bold" onclick="consultarReporte()">
                                    <i class="bi bi-play-circle"></i> Generar Reporte
                                </button>
                            </div>
                        </div>

                        <!-- Tabla Dinámica del Reporte -->
                        <div class="table-responsive border rounded" style="min-height: 250px;">
                            <table class="table table-striped table-hover mb-0" id="tablaReportes">
                                <thead class="table-dark" id="theadReportes">
                                    <tr><th>Haga clic en 'Generar Reporte' para visualizar la información</th></tr>
                                </thead>
                                <tbody id="tbodyReportes">
                                    <tr><td class="text-center text-muted py-5">Sin datos cargados</td></tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <!-- MODAL DE COBRO EFECTIVO -->
    <div class="modal fade" id="modalEfectivo" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title"><i class="bi bi-cash"></i> Cobro en Efectivo</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <h3 class="text-center mb-3 text-secondary">Total: <span id="modalTotal" class="text-success fw-bold">Q0.00</span></h3>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Efectivo Recibido (Q):</label>
                        <input type="number" id="txtEfectivo" class="form-control form-control-lg text-center" oninput="calcularCambio()">
                    </div>
                    <div class="alert alert-info d-flex justify-content-between align-items-center">
                        <span class="fs-5">Cambio:</span>
                        <strong class="fs-4" id="lblCambio">Q0.00</strong>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-success fw-bold px-4" onclick="confirmarVentaPos('EFECTIVO')">Confirmar Venta</button>
                </div>
            </div>
        </div>
    </div>

    <!-- MODAL DE COBRO TARJETA (Simulación con número de referencia como exige el PDF) -->
    <div class="modal fade" id="modalTarjeta" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-secondary text-white">
                    <h5 class="modal-title"><i class="bi bi-credit-card"></i> Cobro con Tarjeta (POS)</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <h3 class="text-center mb-3 text-secondary">Total a Cobrar: <span id="modalTotalTarjeta" class="text-success fw-bold">Q0.00</span></h3>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Número de Referencia de Transacción:</label>
                        <input type="text" id="txtRefTarjeta" class="form-control text-center" placeholder="Ej: REF-984210" value="REF-POS-1001">
                    </div>
                    <div class="alert alert-warning small">
                        <strong>Simulación requerida por el proyecto:</strong> Al confirmar, se verificará la aprobación del pago.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Rechazar / Cancelar</button>
                    <button type="button" class="btn btn-success fw-bold px-4" onclick="confirmarVentaPos('TARJETA')">Aprobar y Confirmar</button>
                </div>
            </div>
        </div>
    </div>

    <!-- MODAL COMPROBANTE DE VENTA (TICKET) -->
    <div class="modal fade" id="modalTicket" tabindex="-1">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-body p-3">
                    <div id="ticketContenido" class="ticket-box p-3 text-center"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-dark w-100" onclick="window.print()">
                        <i class="bi bi-printer"></i> Imprimir Ticket
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- BOOTSTRAP JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- SCRIPTS DE LÓGICA WEB -->
    <script>
        // ==========================================
        // 1. LÓGICA DE PUNTO DE VENTA (POS)
        // ==========================================
        let clientePos = null;
        let carritoPos = [];
        let totalPos = 0.0;
        let puntosPos = 0;

        async function buscarClientePos() {
            const criterio = document.getElementById("txtClienteCriterio").value.trim();
            if (!criterio) return alert("Ingrese un ID o teléfono.");

            try {
                const res = await fetch(`PedidoServlet?accion=buscarCliente&criterio=${criterio}`);
                const data = await res.json();
                if (res.ok) {
                    if (data.activo === 0) return alert("El cliente está inactivo en el sistema.");
                    clientePos = data;
                    document.getElementById("infoClientePos").innerHTML = 
                        `<div class="alert alert-success py-1 mb-0 mt-2"><strong>${data.nombre}</strong> (Puntos: ${data.puntos} pts)</div>`;
                } else {
                    alert(data.error);
                }
            } catch (e) {
                alert("Error al conectar con el servidor.");
            }
        }

        async function agregarItemPos() {
            const id = document.getElementById("txtItemId").value.trim();
            const cant = parseInt(document.getElementById("txtCantidad").value);
            const tipo = document.querySelector('input[name="rbTipo"]:checked').value;

            if (!id || cant <= 0) return alert("Ingrese un ID y cantidad válida mayor a cero.");

            try {
                const res = await fetch(`PedidoServlet?accion=buscarItem&tipo=${tipo}&id=${id}`);
                const data = await res.json();
                if (!res.ok) return alert(data.error);
                if (data.stock < cant) return alert(`Stock insuficiente. Disponible: ${data.stock}`);

                let pts = 0;
                if (tipo === "MENU") {
                    pts = cant * 8; // Regla: Menú completo = 8 pts
                } else if (data.categoria === "SANDWICH") {
                    pts = cant * 2; // Regla: Sándwich individual = 2 pts
                }

                carritoPos.push({
                    tipo: tipo,
                    id: data.id,
                    nombre: data.nombre,
                    precio: data.precio,
                    cantidad: cant,
                    subtotal: data.precio * cant,
                    puntos: pts
                });

                renderizarCarritoPos();
                document.getElementById("txtItemId").value = "";
                document.getElementById("txtCantidad").value = "1";
            } catch (e) {
                alert("Error al consultar ítem.");
            }
        }

        function renderizarCarritoPos() {
            const tbody = document.getElementById("tablaItemsPos");
            tbody.innerHTML = "";
            totalPos = 0.0;
            puntosPos = 0;

            if (carritoPos.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">No hay productos agregados</td></tr>';
            } else {
                carritoPos.forEach((item, idx) => {
                    totalPos += item.subtotal;
                    puntosPos += item.puntos;
                    tbody.innerHTML += `
                        <tr>
                            <td><span class="badge ${item.tipo === 'MENU' ? 'bg-warning text-dark' : 'bg-primary'}">${item.tipo}</span></td>
                            <td>${item.nombre}</td>
                            <td>${item.cantidad}</td>
                            <td>Q${item.precio.toFixed(2)}</td>
                            <td class="fw-bold">Q${item.subtotal.toFixed(2)}</td>
                            <td><span class="text-success fw-bold">+${item.puntos}</span></td>
                            <td><button class="btn btn-sm btn-outline-danger" onclick="eliminarItemPos(${idx})">&times;</button></td>
                        </tr>
                    `;
                });
            }

            document.getElementById("lblTotalPos").innerText = `Q${totalPos.toFixed(2)}`;
            document.getElementById("badgePuntos").innerText = `${puntosPos} Pts a Ganar`;
        }

        function eliminarItemPos(idx) {
            carritoPos.splice(idx, 1);
            renderizarCarritoPos();
        }

        function abrirCobroPos(metodo) {
            if (!clientePos) return alert("Debe seleccionar un cliente antes de cobrar.");
            if (carritoPos.length === 0) return alert("El pedido está vacío.");

            if (metodo === 'EFECTIVO') {
                document.getElementById("modalTotal").innerText = `Q${totalPos.toFixed(2)}`;
                document.getElementById("txtEfectivo").value = "";
                document.getElementById("lblCambio").innerText = "Q0.00";
                new bootstrap.Modal(document.getElementById('modalEfectivo')).show();
            } else {
                document.getElementById("modalTotalTarjeta").innerText = `Q${totalPos.toFixed(2)}`;
                new bootstrap.Modal(document.getElementById('modalTarjeta')).show();
            }
        }

        function calcularCambio() {
            const ef = parseFloat(document.getElementById("txtEfectivo").value) || 0;
            const cambio = ef - totalPos;
            document.getElementById("lblCambio").innerText = cambio >= 0 ? `Q${cambio.toFixed(2)}` : "Monto insuficiente";
        }

        async function confirmarVentaPos(metodo) {
            let ef = totalPos;
            let ref = "";

            if (metodo === 'EFECTIVO') {
                ef = parseFloat(document.getElementById("txtEfectivo").value) || 0;
                if (ef < totalPos) return alert("El monto en efectivo es insuficiente.");
            } else {
                ref = document.getElementById("txtRefTarjeta").value.trim() || "REF-POS";
            }

            const params = new URLSearchParams();
            params.append("idCliente", clientePos.id);
            params.append("total", totalPos);
            params.append("puntos", puntosPos);
            params.append("metodoPago", metodo);
            params.append("refTarjeta", ref);
            params.append("efectivo", ef);

            carritoPos.forEach(it => {
                params.append("itemTipo[]", it.tipo);
                params.append("itemId[]", it.id);
                params.append("itemCantidad[]", it.cantidad);
                params.append("itemPrecio[]", it.precio);
            });

            try {
                const res = await fetch("PedidoServlet", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: params
                });

                const data = await res.json();
                if (data.exito) {
                    bootstrap.Modal.getInstance(document.getElementById('modalEfectivo'))?.hide();
                    bootstrap.Modal.getInstance(document.getElementById('modalTarjeta'))?.hide();

                    mostrarTicketVenta(data.idPedido, metodo, ef, data.cambio);
                    carritoPos = [];
                    renderizarCarritoPos();
                    buscarClientePos(); // Actualizar saldo de puntos en vivo
                } else {
                    alert("Error en la venta: " + data.error);
                }
            } catch (e) {
                alert("Error al procesar la venta.");
            }
        }

        function mostrarTicketVenta(idPedido, metodo, ef, cambio) {
            let html = `
                <h6>PAN, PUNTOS Y PREMIOS</h6>
                <small>COMPROBANTE DE VENTA FISCAL</small><hr class="my-1">
                <small>Pedido No: <strong>#${idPedido}</strong></small><br>
                <small>Cliente: ${clientePos.nombre}</small><br>
                <small>Método de Pago: ${metodo}</small><hr class="my-1">
                <table class="w-100 small text-start">
            `;
            carritoPos.forEach(it => {
                html += `<tr><td>${it.cantidad}x ${it.nombre}</td><td class="text-end">Q${it.subtotal.toFixed(2)}</td></tr>`;
            });
            html += `
                </table><hr class="my-1">
                <div class="d-flex justify-content-between fw-bold"><span>TOTAL:</span><span>Q${totalPos.toFixed(2)}</span></div>
            `;
            if (metodo === 'EFECTIVO') {
                html += `<div class="d-flex justify-content-between small"><span>Recibido:</span><span>Q${ef.toFixed(2)}</span></div>
                         <div class="d-flex justify-content-between small"><span>Cambio:</span><span>Q${cambio.toFixed(2)}</span></div>`;
            }
            html += `
                <div class="badge bg-warning text-dark mt-2 p-1 w-100">+${puntosPos} Puntos Ganados</div>
                <div class="small text-muted mt-2">¡Gracias por su compra!</div>
            `;
            document.getElementById("ticketContenido").innerHTML = html;
            new bootstrap.Modal(document.getElementById('modalTicket')).show();
        }

        // ==========================================
        // 2. LÓGICA DE CLIENTES
        // ==========================================
        async function cargarClientes() {
            try {
                const res = await fetch("PedidoServlet?accion=listarClientes");
                const clientes = await res.json();
                const tbody = document.getElementById("tablaClientes");
                tbody.innerHTML = "";

                clientes.forEach(c => {
                    tbody.innerHTML += `
                        <tr>
                            <td><strong>${c.id}</strong></td>
                            <td>${c.nombre}</td>
                            <td>${c.telefono}</td>
                            <td><span class="badge bg-primary">${c.puntos} pts</span></td>
                            <td><span class="badge ${c.activo === 1 ? 'bg-success' : 'bg-danger'}">${c.activo === 1 ? 'ACTIVO' : 'INACTIVO'}</span></td>
                        </tr>
                    `;
                });
            } catch (e) {
                console.error("Error al cargar clientes", e);
            }
        }

        async function guardarCliente() {
            const id = document.getElementById("cId").value.trim();
            const nombre = document.getElementById("cNombre").value.trim();
            const telefono = document.getElementById("cTelefono").value.trim();
            if (!id || !nombre || !telefono) return alert("Complete todos los campos del cliente.");

            const params = new URLSearchParams({ operacion: 'guardarCliente', id, nombre, telefono });
            const res = await fetch("PedidoServlet", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: params });
            const data = await res.json();
            alert(data.mensaje || data.error);
            if (data.exito) { limpiarClienteForm(); cargarClientes(); }
        }

        async function buscarClienteForm() {
            const id = document.getElementById("cId").value.trim();
            if (!id) return alert("Ingrese el ID del cliente a buscar.");

            const res = await fetch(`PedidoServlet?accion=buscarCliente&criterio=${id}`);
            const data = await res.json();
            if (res.ok) {
                document.getElementById("cNombre").value = data.nombre;
                document.getElementById("cTelefono").value = data.telefono;
            } else {
                alert(data.error);
            }
        }

        async function modificarCliente() {
            const id = document.getElementById("cId").value.trim();
            const nombre = document.getElementById("cNombre").value.trim();
            const telefono = document.getElementById("cTelefono").value.trim();
            if (!id || !nombre || !telefono) return alert("Complete los campos para modificar.");

            const params = new URLSearchParams({ operacion: 'modificarCliente', id, nombre, telefono });
            const res = await fetch("PedidoServlet", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: params });
            const data = await res.json();
            alert(data.mensaje || data.error);
            if (data.exito) { cargarClientes(); }
        }

        async function desactivarCliente() {
            const id = document.getElementById("cId").value.trim();
            if (!id) return alert("Ingrese el ID del cliente a desactivar.");
            if (!confirm("¿Está seguro de desactivar este cliente?")) return;

            const params = new URLSearchParams({ operacion: 'desactivarCliente', id });
            const res = await fetch("PedidoServlet", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: params });
            const data = await res.json();
            alert(data.mensaje || data.error);
            if (data.exito) { cargarClientes(); }
        }

        function limpiarClienteForm() {
            document.getElementById("cId").value = "";
            document.getElementById("cNombre").value = "";
            document.getElementById("cTelefono").value = "";
        }

        // ==========================================
        // 3. LÓGICA DE PRODUCTOS
        // ==========================================
        async function cargarProductos() {
            try {
                const res = await fetch("PedidoServlet?accion=listarProductos");
                const prods = await res.json();
                const tbody = document.getElementById("tablaProductos");
                tbody.innerHTML = "";

                prods.forEach(p => {
                    tbody.innerHTML += `
                        <tr>
                            <td><strong>${p.id}</strong></td>
                            <td>${p.nombre}</td>
                            <td><span class="badge bg-secondary">${p.categoria}</span></td>
                            <td>Q${p.precio.toFixed(2)}</td>
                            <td><span class="badge ${p.existencia <= 5 ? 'bg-danger' : 'bg-success'}">${p.existencia} u.</span></td>
                            <td><span class="badge ${p.activo === 1 ? 'bg-success' : 'bg-danger'}">${p.activo === 1 ? 'ACTIVO' : 'INACTIVO'}</span></td>
                        </tr>
                    `;
                });
            } catch (e) {
                console.error("Error al cargar productos", e);
            }
        }

        async function guardarProducto() {
            const id = document.getElementById("pId").value.trim();
            const nombre = document.getElementById("pNombre").value.trim();
            const categoria = document.getElementById("pCategoria").value;
            const precio = document.getElementById("pPrecio").value;
            const existencia = document.getElementById("pExistencia").value;

            if (!id || !nombre || precio === "" || existencia === "") return alert("Complete todos los campos del producto.");

            const params = new URLSearchParams({ operacion: 'guardarProducto', id, nombre, categoria, precio, existencia });
            const res = await fetch("PedidoServlet", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: params });
            const data = await res.json();
            alert(data.mensaje || data.error);
            if (data.exito) { limpiarProductoForm(); cargarProductos(); }
        }

        async function buscarProductoForm() {
            const id = document.getElementById("pId").value.trim();
            if (!id) return alert("Ingrese el ID del producto.");

            const res = await fetch(`PedidoServlet?accion=buscarProducto&id=${id}`);
            const data = await res.json();
            if (res.ok) {
                document.getElementById("pNombre").value = data.nombre;
                document.getElementById("pCategoria").value = data.categoria;
                document.getElementById("pPrecio").value = data.precio;
                document.getElementById("pExistencia").value = data.existencia;
            } else {
                alert(data.error);
            }
        }

        async function modificarProducto() {
            const id = document.getElementById("pId").value.trim();
            const nombre = document.getElementById("pNombre").value.trim();
            const categoria = document.getElementById("pCategoria").value;
            const precio = document.getElementById("pPrecio").value;
            const existencia = document.getElementById("pExistencia").value;

            const params = new URLSearchParams({ operacion: 'modificarProducto', id, nombre, categoria, precio, existencia });
            const res = await fetch("PedidoServlet", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: params });
            const data = await res.json();
            alert(data.mensaje || data.error);
            if (data.exito) { cargarProductos(); }
        }

        async function desactivarProducto() {
            const id = document.getElementById("pId").value.trim();
            if (!id) return alert("Ingrese el ID del producto a desactivar.");
            if (!confirm("¿Está seguro de desactivar este producto del catálogo?")) return;

            const params = new URLSearchParams({ operacion: 'desactivarProducto', id });
            const res = await fetch("PedidoServlet", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: params });
            const data = await res.json();
            alert(data.mensaje || data.error);
            if (data.exito) { cargarProductos(); }
        }

        function limpiarProductoForm() {
            document.getElementById("pId").value = "";
            document.getElementById("pNombre").value = "";
            document.getElementById("pPrecio").value = "";
            document.getElementById("pExistencia").value = "";
        }

        // ==========================================
        // 4. LÓGICA DE ABASTECIMIENTO DE INVENTARIO
        // ==========================================
        async function consultarStockAbastecer() {
            const id = document.getElementById("abastId").value.trim();
            if (!id) return alert("Ingrese el ID del producto.");

            const res = await fetch(`PedidoServlet?accion=buscarProducto&id=${id}`);
            const data = await res.json();
            if (res.ok) {
                document.getElementById("abastStockInfo").innerHTML = 
                    `Producto: <strong>${data.nombre}</strong> (${data.categoria}) | Existencia actual: <strong class="text-primary">${data.existencia} unidades</strong>`;
            } else {
                alert(data.error);
                document.getElementById("abastStockInfo").innerHTML = "Existencia actual: <strong>-</strong>";
            }
        }

        async function confirmarAbastecimiento() {
            const idProducto = document.getElementById("abastId").value.trim();
            const cantidad = document.getElementById("abastCantidad").value.trim();
            if (!idProducto || !cantidad || parseInt(cantidad) <= 0) return alert("Ingrese un producto y cantidad válida mayor a cero.");

            const params = new URLSearchParams({ operacion: 'abastecer', idProducto, cantidad });
            const res = await fetch("PedidoServlet", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: params });
            const data = await res.json();
            alert(data.mensaje || data.error);
            if (data.exito) {
                document.getElementById("abastCantidad").value = "";
                consultarStockAbastecer();
            }
        }

        // ==========================================
        // 5. LÓGICA DE FIDELIZACIÓN Y CANJE
        // ==========================================
        let clienteCanje = null;
        let premioActual = null;

        async function buscarClienteCanje() {
            const criterio = document.getElementById("canjeCriterio").value.trim();
            if (!criterio) return alert("Ingrese ID o teléfono del cliente.");

            const res = await fetch(`PedidoServlet?accion=buscarCliente&criterio=${criterio}`);
            const data = await res.json();
            if (res.ok) {
                if (data.activo === 0) return alert("El cliente está inactivo.");
                clienteCanje = data;
                document.getElementById("canjePuntosDisponibles").innerText = `${data.puntos} pts`;
                document.getElementById("canjeClienteInfo").innerHTML = `Cliente: <strong class="text-success">${data.nombre}</strong> (Tel: ${data.telefono})`;
            } else {
                alert(data.error);
                clienteCanje = null;
                document.getElementById("canjePuntosDisponibles").innerText = "0 pts";
                document.getElementById("canjeClienteInfo").innerHTML = "<em>Cliente no encontrado</em>";
            }
        }

        async function evaluarPremio() {
            const select = document.getElementById("canjeComboPremios");
            const opt = select.options[select.selectedIndex];
            if (!opt.value) {
                premioActual = null;
                document.getElementById("canjeStockInfo").innerHTML = "Disponibilidad en inventario: <em>Seleccione una recompensa</em>";
                return;
            }

            const pts = parseInt(opt.getAttribute("data-pts"));
            const cat = opt.getAttribute("data-cat");

            try {
                const res = await fetch(`PedidoServlet?accion=consultarPremio&categoria=${cat}`);
                const data = await res.json();
                if (res.ok) {
                    premioActual = { id: data.id, nombre: data.nombre, categoria: cat, puntos: pts, stock: data.stock };
                    document.getElementById("canjeStockInfo").innerHTML = 
                        `Disponible para canje: <strong>${data.nombre}</strong> (Stock actual: <strong class="text-success">${data.stock} u.</strong>) | Requiere: <strong>${pts} pts</strong>`;
                } else {
                    premioActual = null;
                    document.getElementById("canjeStockInfo").innerHTML = `<span class="text-danger fw-bold">${data.error}</span>`;
                }
            } catch (e) {
                alert("Error al verificar stock de premio.");
            }
        }

        async function procesarCanjePremio() {
            if (!clienteCanje) return alert("Primero consulte un cliente activo.");
            if (!premioActual) return alert("Seleccione una recompensa con stock disponible.");

            if (clienteCanje.puntos < premioActual.puntos) {
                return alert(`Saldo insuficiente. El cliente tiene ${clienteCanje.puntos} pts y requiere ${premioActual.puntos} pts.`);
            }

            if (!confirm(`¿Confirmar el canje de '${premioActual.nombre}' por ${premioActual.puntos} puntos?`)) return;

            const params = new URLSearchParams({
                operacion: 'procesarCanje',
                idCliente: clienteCanje.id,
                puntosRequeridos: premioActual.puntos,
                premioNombre: premioActual.nombre,
                categoria: premioActual.categoria,
                idPremio: premioActual.id
            });

            const res = await fetch("PedidoServlet", { method: "POST", headers: { "Content-Type": "application/x-www-form-urlencoded" }, body: params });
            const data = await res.json();
            alert(data.mensaje || data.error);
            if (data.exito) {
                buscarClienteCanje(); // Refrescar puntos
                evaluarPremio();      // Refrescar stock
            }
        }

        // ==========================================
        // 6. LÓGICA DE REPORTES OFICIALES
        // ==========================================
        async function consultarReporte() {
            const tipo = document.getElementById("cbReportes").value;
            try {
                const res = await fetch(`PedidoServlet?accion=generarReporte&tipo=${tipo}`);
                const data = await res.json();

                // Construir cabeceras
                let thHtml = "<tr>";
                data.columnas.forEach(col => { thHtml += `<th>${col}</th>`; });
                thHtml += "</tr>";
                document.getElementById("theadReportes").innerHTML = thHtml;

                // Construir filas
                let tbHtml = "";
                if (data.filas.length === 0) {
                    tbHtml = `<tr><td colspan="${data.columnas.length}" class="text-center text-muted py-4">No se encontraron registros en la base de datos</td></tr>`;
                } else {
                    data.filas.forEach(fila => {
                        tbHtml += "<tr>";
                        fila.forEach(celda => { tbHtml += `<td>${celda}</td>`; });
                        tbHtml += "</tr>";
                    });
                }
                document.getElementById("tbodyReportes").innerHTML = tbHtml;

            } catch (e) {
                alert("Error al generar el reporte.");
            }
        }
    </script>
</body>
</html>
