-- =====================================================
-- SGR: Sistema de Gestión de Restaurantes CSS
-- Script de inicialización de la Base de Datos
-- Motor: PostgreSQL 16
-- =====================================================

-- =====================================================
-- TABLA: mesero
-- Almacena los datos del personal de servicio
-- =====================================================
CREATE TABLE IF NOT EXISTS mesero (
    id_mesero   SERIAL PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL,
    apellido    VARCHAR(50)  NOT NULL,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);

-- =====================================================
-- TABLA: mesa
-- Representa las mesas físicas del restaurante
-- =====================================================
CREATE TABLE IF NOT EXISTS mesa (
    id_mesa     SERIAL PRIMARY KEY,
    capacidad   INTEGER      NOT NULL DEFAULT 4,
    estado      VARCHAR(20)  NOT NULL DEFAULT 'Libre',
    id_mesero   INTEGER      REFERENCES mesero(id_mesero) ON DELETE SET NULL
);

-- =====================================================
-- TABLA: catalogo_producto
-- Catálogo de productos disponibles del restaurante
-- =====================================================
CREATE TABLE IF NOT EXISTS catalogo_producto (
    id_producto SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    precio      DECIMAL(10,2) NOT NULL,
    categoria   VARCHAR(20)  NOT NULL CHECK (categoria IN ('comida', 'bebida', 'postre')),
    stock       INTEGER      NOT NULL DEFAULT 0,
    estado      INTEGER      NOT NULL DEFAULT 1  -- 1 = disponible, 0 = no disponible
);

-- =====================================================
-- TABLA: cuenta
-- Asocia una sesión de consumo a una mesa
-- =====================================================
CREATE TABLE IF NOT EXISTS cuenta (
    id_cuenta       SERIAL PRIMARY KEY,
    id_mesa         INTEGER      NOT NULL REFERENCES mesa(id_mesa),
    fecha_apertura  TIMESTAMP    NOT NULL DEFAULT NOW(),
    fecha_cierre    TIMESTAMP,
    estado          INTEGER      NOT NULL DEFAULT 1  -- 1 = abierta, 0 = cerrada
);

-- =====================================================
-- TABLA: pedido
-- Detalle de cada producto solicitado en una cuenta
-- =====================================================
CREATE TABLE IF NOT EXISTS pedido (
    id_pedido       SERIAL PRIMARY KEY,
    id_cuenta       INTEGER      NOT NULL REFERENCES cuenta(id_cuenta),
    id_producto     INTEGER      NOT NULL REFERENCES catalogo_producto(id_producto),
    cantidad        INTEGER      NOT NULL DEFAULT 1,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'Pendiente'
                    CHECK (estado IN ('Pendiente', 'En preparación', 'Servido', 'Cancelado')),
    fecha_hora      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- =====================================================
-- DATOS INICIALES: Meseros
-- =====================================================
INSERT INTO mesero (nombre, apellido,activo) VALUES
    ('Carlos',  'González',  TRUE),
    ('María',   'Rodríguez', TRUE),
    ('Juan',    'Pérez',     TRUE),
    ('Ana',     'Martínez',  TRUE);

-- =====================================================
-- DATOS INICIALES: Mesas (12 mesas del restaurante)
-- =====================================================
INSERT INTO mesa (capacidad, estado) VALUES
    (4, 'Libre'), (4, 'Libre'), (2, 'Libre'), (6, 'Libre'),
    (4, 'Libre'), (2, 'Libre'), (8, 'Libre'), (4, 'Libre'),
    (4, 'Libre'), (6, 'Libre'), (2, 'Libre'), (4, 'Libre');

-- =====================================================
-- DATOS INICIALES: Productos del menú
-- =====================================================
INSERT INTO catalogo_producto (nombre, precio, categoria, stock, estado) VALUES
    -- Comidas
    ('Milanesa con papas',      350.00, 'comida',  50, 1),
    ('Hamburguesa clásica',     280.00, 'comida',  40, 1),
    ('Pizza muzzarella',        400.00, 'comida',  30, 1),
    ('Ensalada César',          250.00, 'comida',  25, 1),
    ('Pasta bolognesa',         320.00, 'comida',  35, 1),
    ('Lomo a la plancha',       500.00, 'comida',  20, 1),
    ('Empanadas (x3)',          200.00, 'comida',  60, 1),
    ('Suprema napolitana',      420.00, 'comida',  30, 1),
    -- Bebidas
    ('Coca-Cola 500ml',          80.00, 'bebida',  100, 1),
    ('Agua mineral 500ml',       50.00, 'bebida',  100, 1),
    ('Jugo de naranja',          90.00, 'bebida',  40, 1),
    ('Cerveza artesanal',       150.00, 'bebida',  50, 1),
    ('Limonada',                 70.00, 'bebida',  45, 1),
    ('Café con leche',           60.00, 'bebida',  80, 1),
    -- Postres
    ('Flan con dulce de leche', 150.00, 'postre',  30, 1),
    ('Brownie con helado',      180.00, 'postre',  25, 1),
    ('Tiramisú',                200.00, 'postre',  20, 1),
    ('Ensalada de frutas',      120.00, 'postre',  35, 1);
