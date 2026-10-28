-- ============================================================
-- GUÍA DE CREACIÓN DE TABLAS EN SQLITECLOUD - CYBERQHATU
-- Ejecuta estas sentencias SQL en la consola de tu base de datos SqliteCloud
-- ============================================================

-- 1. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    correo TEXT NOT NULL,
    telefono_whatsapp TEXT NOT NULL,
    foto_perfil TEXT,
    estado_verificacion_ci INTEGER DEFAULT 0,
    ci TEXT,
    tipo_rol TEXT DEFAULT 'COMPRADOR',
    fecha_registro INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- 2. Tabla de Tiendas
CREATE TABLE IF NOT EXISTS tiendas (
    id_tienda TEXT PRIMARY KEY,
    id_propietario TEXT NOT NULL,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    logo TEXT,
    foto_portada TEXT,
    tipo TEXT DEFAULT 'VIRTUAL',
    direccion_google_maps TEXT,
    horario_apertura TEXT DEFAULT '08:00',
    horario_cierre TEXT DEFAULT '18:00',
    link_whatsapp TEXT NOT NULL,
    qr_fijo_tienda TEXT,
    strikes INTEGER DEFAULT 0,
    estado TEXT DEFAULT 'ACTIVA',
    FOREIGN KEY(id_propietario) REFERENCES usuarios(id_usuario)
);

-- 3. Tabla de Colaboradores / Miembros de Tienda
CREATE TABLE IF NOT EXISTS tienda_miembros (
    id_miembro TEXT PRIMARY KEY,
    id_tienda TEXT NOT NULL,
    id_usuario TEXT NOT NULL,
    rol TEXT DEFAULT 'VENDEDOR',
    fecha_union INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    FOREIGN KEY(id_tienda) REFERENCES tiendas(id_tienda),
    FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario)
);

-- 4. Tabla de Productos de Inventario
CREATE TABLE IF NOT EXISTS productos (
    id_producto TEXT PRIMARY KEY,
    id_tienda TEXT NOT NULL,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    precio REAL NOT NULL,
    stock INTEGER NOT NULL DEFAULT 1,
    estado TEXT DEFAULT 'ACTIVO',
    condicion TEXT DEFAULT 'NUEVO',
    fecha_creacion INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    FOREIGN KEY(id_tienda) REFERENCES tiendas(id_tienda)
);

-- 5. Tabla de Imágenes de Productos
CREATE TABLE IF NOT EXISTS producto_imagenes (
    id_imagen TEXT PRIMARY KEY,
    id_producto TEXT NOT NULL,
    url_imagen TEXT NOT NULL,
    FOREIGN KEY(id_producto) REFERENCES productos(id_producto)
);

-- 6. Tabla de Reportes de Tiendas (Comprador -> Moderador)
CREATE TABLE IF NOT EXISTS reportes (
    id_reporte TEXT PRIMARY KEY,
    id_reportante TEXT NOT NULL,
    id_tienda_reportada TEXT NOT NULL,
    motivo TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    estado TEXT DEFAULT 'PENDIENTE',
    fecha_creacion INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    FOREIGN KEY(id_reportante) REFERENCES usuarios(id_usuario),
    FOREIGN KEY(id_tienda_reportada) REFERENCES tiendas(id_tienda)
);

-- 7. Tabla de Evidencias / Capturas de Reportes
CREATE TABLE IF NOT EXISTS reporte_evidencias (
    id_evidencia TEXT PRIMARY KEY,
    id_reporte TEXT NOT NULL,
    url_evidencia TEXT NOT NULL,
    FOREIGN KEY(id_reporte) REFERENCES reportes(id_reporte)
);

-- 8. Tabla de Verificaciones de Identidad (Biometría y C.I.)
CREATE TABLE IF NOT EXISTS verificaciones_identidad (
    id_verificacion TEXT PRIMARY KEY,
    id_usuario TEXT NOT NULL,
    numero_ci TEXT,
    porcentaje_similitud REAL NOT NULL,
    es_exitoso INTEGER NOT NULL,
    fecha_verificacion INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario)
);
