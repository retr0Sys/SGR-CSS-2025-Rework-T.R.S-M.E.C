# 🍽️ Sistema de Gestion de Restaurantes (SGR) | Proyecto en Java

<div align="center">
  
  <img src="https://github.com/user-attachments/assets/367f5203-b59c-4773-bc67-7dacd7cb48fa" alt="Logo de CSS" width="400"/>
  
  <p>Una solucion integral en Java para optimizar las operaciones del restaurante CSS.</p>
  
  <br>

  ---
  
</div>

## ✨ Vision General del Proyecto

El **Sistema de Gestion de Restaurantes (SGR)**, desarrollado en **Java**, es una plataforma robusta diseñada a medida para el restaurante **CSS**.  
Su objetivo es optimizar y automatizar la gestion diaria del establecimiento, abarcando desde la **toma de pedidos** y el **control de cocina** hasta la **facturacion detallada** y la **generacion de informes**.

> **⚠️ Restriccion de Uso:** Este sistema ha sido desarrollado exclusivamente para el restaurante CSS y su uso esta limitado a este establecimiento.

---

## 📌 Versiones

### V2.0.0 (Actual) — Rework Completo

Rework total del sistema con arquitectura moderna, UI renovada y mejoras de seguridad.

| Mejora | Detalle |
| :--- | :--- |
| **Arquitectura Maven** | Estructura estandar `src/main/java`, gestion de dependencias con `pom.xml` |
| **PostgreSQL + Docker** | Base de datos PostgreSQL 16 conteneurizada con `docker-compose` |
| **HikariCP** | Connection pooling de alto rendimiento |
| **FlatLaf** | UI moderna con tema claro/oscuro intercambiable |
| **Seguridad** | Credenciales en `.env`, `InputValidator` anti-XSS, `PreparedStatements` |
| **PDF** | Generacion de facturas PDF con Apache PDFBox |
| **Java 25** | Compatible con la ultima version de Java |
| **Modelo unificado** | `Producto` unico con enum `CategoriaProducto` (reemplaza `Comida`/`Bebida`/`Postre`) |

### V1.0.0 — Version Original

Version academica inicial del sistema.

| Caracteristica | Detalle |
| :--- | :--- |
| **Java Swing + IntelliJ Forms** | UI construida con archivos `.form` de IntelliJ |
| **MariaDB/MySQL** | Base de datos relacional con HeidiSQL |
| **JDBC directo** | Conexiones sin pooling, credenciales hardcodeadas |
| **Modelo separado** | Clases `Comida`, `Bebida`, `Postre` con herencia |

---

## 🚀 Caracteristicas Principales

| Caracteristica | Descripcion |
| :--- | :--- |
| **Mesas y Meseros** | Asignacion, seguimiento del estado de las mesas y administracion del personal de servicio. |
| **Punto de Venta (POS)** | Interfaz rapida e intuitiva para la seleccion de productos y gestion de pedidos en curso. |
| **Control de Cocina** | Visualizacion en tiempo real de los pedidos pendientes con estados color-coded. |
| **Facturacion Detallada** | Generacion de facturas PDF, descuentos, IVA y cierre automatico de mesa. |
| **Informes y Resumenes** | Dashboard con estadisticas, top 3 productos, ventas del dia y pedidos por mesa. |
| **Tema Claro/Oscuro** | Toggle de tema con persistencia entre sesiones (V2). |

---

## 🗂️ Organizacion de Carpetas (V2)

```
src/main/java/com/css/restaurante/
├── dao/            → ConexionDB, ProductoDAO, MesaDAO, MeseroDAO, CuentaDAO, PedidoDAO
├── modelo/         → Producto, Mesa, Mesero, Cuenta, Pedido, CategoriaProducto, EstadoPedido
├── exception/      → StockInsuficienteException
├── ui/             → MenuPuntoVenta, PanelCarta, PanelMesas, PanelCocina, PanelFacturacion, PanelResumen
│                     ThemeManager, InputValidator, FacturaPDF
└── principal/      → BienvenidaMenuInicial (punto de entrada)
```

La estructura sigue el **patron multicapa (DAO - Logica - Presentacion)**.

---

## ⚙️ Requisitos del Sistema (V2)

### 💻 Entorno de Ejecucion

* **Java:** [JDK 25](https://www.oracle.com/java/technologies/downloads/) o superior
* **Maven:** [Apache Maven 3.9+](https://maven.apache.org/download.cgi)
* **Docker:** [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### 📦 Dependencias (gestionadas por Maven)

| Dependencia | Funcion |
| :--- | :--- |
| FlatLaf 3.5.4 | Look & Feel moderno (Dark/Light) |
| HikariCP 5.1.0 | Connection pooling |
| PostgreSQL JDBC 42.7.4 | Driver de base de datos |
| Apache PDFBox 2.0.32 | Generacion de facturas PDF |
| dotenv-java 3.0.2 | Variables de entorno (.env) |
| SLF4J 2.0.16 | Logging |

---

## 🧩 Guia de Ejecucion (V2)

### 1. Instalar Maven
```bash
winget install Apache.Maven
# Reiniciar terminal y verificar:
mvn --version
```

### 2. Levantar la base de datos
```bash
docker-compose up -d
```

### 3. Compilar y ejecutar
```bash
mvn clean compile exec:java
```

### 4. (Opcional) Generar JAR ejecutable
```bash
mvn clean package
java -jar target/sistema-gestion-restaurante-2.0.0.jar
```

---

## 🧭 Flujo de Uso del Sistema

### 1. 📋 Carta
- Visualizar los productos actuales (nombre, precio, stock, disponibilidad, categoria).
- Crear nuevos productos o modificar existentes.
- Filtrar por categoria: Comida, Bebida, Postre.

### 2. 🟢 Mesas
- 12 mesas con estados visuales: **Libre** (verde), **Ocupada** (rojo), **Reservada** (amarillo), **Limpieza** (azul).
- Abrir cuenta, asignar mesero, cargar pedidos.

### 3. 🔥 Cocina
- Comanda digital con estados: **Pendiente → En preparacion → Servido / Cancelado**.
- Auto-refresh cada 15 segundos.

### 4. 💰 Facturacion
- Seleccionar mesa con cuenta abierta.
- Aplicar descuento (0%, 5%, 10%, 15%) y ver desglose con IVA (22%).
- **Generar factura PDF** en una carpeta llamada Facturas (por defecto)

### 5. 📊 Resumen
- Tarjetas de estadisticas: ventas del dia, pedidos, producto top.
- Consultas: ventas del dia, pedidos por mesa, top 3 productos.

### ✅ Flujo General

1. **Carta:** Configurar productos.
2. **Mesas:** Abrir cuenta y asignar mesero.
3. **Pedidos:** Registrar y enviar a cocina.
4. **Cocina:** Preparar y actualizar estados.
5. **Facturas:** Cobrar, generar PDF.
6. **Resumen:** Revisar estadisticas.

---

## 🖥️ Modulos de la Interfaz

### Screenshots V2

<p align="center">
  <i>Modulo de Mesas (V2).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/0bc77c71-d797-49e2-a948-a375d497f6f6" alt="Modulo de Mesas V2" width="600" />
</p>


<p align="center">
  <i>Modulo de Carta (V2).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/82c93d5e-8b8f-4093-8f7e-48dddadbcf6b" alt="Modulo de Carta V2" width="600" />
</p>

<p align="center">
  <i>Modulo de Cocina (V2).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/12f42de3-ef47-4aeb-804f-f829b113197a" alt="Modulo de Cocina V2" width="600" />
</p>


<p align="center">
  <i>Modulo de Facturacion (V2).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/84069547-d764-40b6-af21-85be3c1fe701" alt="Modulo de Facturacion V2" width="600" />
</p>


<p align="center">
  <i>Modulo de Resumen (V2).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/f04b7ef9-a377-42b9-8003-16f7809390b8" alt="Modulo de Resumen V2" width="600" />
</p>


<p align="center">
  <i>Acerca de (V2).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/3fbd9565-19f7-4984-8496-3f44fbe369bc" alt="Acerca de V2" width="600" />
</p>

### Screenshots V1

<p align="center">
  <i>Menu Principal del Sistema (V1).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/3feba958-3e64-4ecf-87c3-c7199cdbfe05" alt="Vista del menu principal del SGR" width="600" />
</p>

<p align="center">
  <i>Ventana de Bienvenida Inicial (V1).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/716df9df-1f92-4152-b494-d9972b6de7a5" alt="Menu de bienvenida inicial" width="600" />
</p>

<p align="center">
  <i>Modulo de Carta (V1).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/d1af79fa-f5a1-4d8f-83de-3d208e3d8520" alt="Interfaz de usuario del modulo de Carta" width="600" />
</p>

<p align="center">
  <i>Modulo de Cocina (V1).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/44e09d64-044e-4f0e-87c9-3a219cf771ff" alt="Interfaz de usuario del modulo de Cocina" width="600" />
</p>

<p align="center">
  <i>Modulo de Facturacion (V1).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/b2919abe-b62e-4adc-8454-af841dc1c884" alt="Interfaz de usuario del modulo de Facturacion" width="600" />  
</p>

<p align="center">
  <i>Seleccion de Mesas (V1).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/7cfe0c5b-5f73-4ed2-b965-65e84c4d725d" alt="Interfaz de usuario para la Seleccion de mesas" width="600" />
</p>

<p align="center">
  <i>Gestion de Mesas (V1).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/9c4a3910-c509-42c6-a8d8-1fbafdb30faa" alt="Interfaz de usuario para el Manejo de mesas" width="600" />
</p>

<p align="center">
  <i>Resumen e Informes (V1).</i>
  <br>
  <img src="https://github.com/user-attachments/assets/92adf3c8-0588-4df6-938d-a37b50c94a24" alt="Interfaz de usuario del Resumen" width="600" />
</p>

---

## 📎 Recursos Externos

### 🔸 Diagrama UML
[**Acceder al diagrama UML**](https://drive.google.com/file/d/1ze_hoKHIy_gUFDDabduETDyWlQq7xwLY/view?usp=sharing)

### 🔸 Base de Datos Original (V1)
[**Acceder a la Base de Datos (SQL)**](https://docs.google.com/document/d/1CbDZiO3eitz26q_SBT3Nu6D-5uoRq0OSP_CJCVtZcW0/edit?usp=sharing)

---

## 🤝 Contribucion y Desarrollo

* **Reporte de Errores / Sugerencias:** Abra un **Issue** en este repositorio.
* **Aportes de Codigo:** Las contribuciones mediante **Pull Requests** son bienvenidas.

---

## 👥 Equipo de Desarrollo

| Nombre | Usuario de GitHub |
| :--- | :--- |
| **Ezequiel Costa** | [@Costa200513](https://github.com/Costa200513) |
| **Thiago Sosa** | [@RetrOSys](https://github.com/RetrOSys) |

---

<p align="center">
  <i>
    MIT License
Copyright (c) 2026 Thiago Rafael Sosa Olivera, Ezequiel Mauricio Costa - Proyecto academico del equipo CSS.</i>
</p>
