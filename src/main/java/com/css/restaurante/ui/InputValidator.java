package com.css.restaurante.ui;

/**
 * Utilidad de validación y sanitización de entradas del usuario.
 * Previene inyección SQL (segunda capa tras PreparedStatements),
 * XSS en campos mostrados, y entradas malformadas.
 */
public final class InputValidator {

    private InputValidator() {
    }

    // ═══ Longitudes máximas ═══
    public static final int MAX_NOMBRE_PRODUCTO = 30;
    public static final int MAX_NOMBRE_PERSONA = 20;
    public static final int MAX_GENERICO = 100;

    /**
     * Sanitiza un string: recorta espacios, elimina caracteres de control
     * y secuencias potencialmente peligrosas.
     */
    public static String sanitizar(String input) {
        if (input == null)
            return "";
        // Eliminar caracteres de control
        String clean = input.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        // Recortar espacios
        clean = clean.trim();
        // Escapar comillas simples (protección extra contra SQL injection)
        clean = clean.replace("'", "'");
        // Eliminar tags HTML/script
        clean = clean.replaceAll("<[^>]*>", "");
        return clean;
    }

    /**
     * Valida que un texto no esté vacío y no exceda la longitud máxima.
     */
    public static boolean esTextoValido(String texto, int maxLength) {
        if (texto == null || texto.trim().isEmpty())
            return false;
        return texto.trim().length() <= maxLength;
    }

    /**
     * Valida un nombre (solo letras, espacios y acentos, 2-30 caracteres).
     */
    public static boolean esNombreValido(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            return false;
        String clean = nombre.trim();
        return clean.length() >= 2 && clean.length() <= MAX_NOMBRE_PERSONA
                && clean.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$");
    }

    /**
     * Valida un precio (positivo, máximo 99999.99).
     */
    public static boolean esPrecioValido(String precioStr) {
        try {
            double precio = Double.parseDouble(precioStr.trim().replace(',', '.'));
            return precio > 0 && precio <= 99999.99;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parsea un precio de forma segura, retorna -1 si es inválido.
     */
    public static double parsePrecio(String precioStr) {
        try {
            double valor = Double.parseDouble(sanitizar(precioStr).replace(',', '.'));
            return (valor > 0 && valor <= 99999.99) ? valor : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Valida stock (entero positivo, máximo 9999).
     */
    public static boolean esStockValido(String stockStr) {
        try {
            int stock = Integer.parseInt(stockStr.trim());
            return stock >= 0 && stock <= 9999;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parsea un stock de forma segura, retorna -1 si es inválido.
     */
    public static int parseStock(String stockStr) {
        try {
            int valor = Integer.parseInt(sanitizar(stockStr));
            return (valor >= 0 && valor <= 9999) ? valor : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Genera un mensaje de error si un campo es inválido.
     * Retorna null si es válido.
     */
    public static String validarCampoProducto(String nombre, String precio, String stock) {
        if (!esTextoValido(nombre, MAX_NOMBRE_PRODUCTO)) {
            return "El nombre del producto es inválido (máx. " + MAX_NOMBRE_PRODUCTO + " caracteres).";
        }
        if (!esPrecioValido(precio)) {
            return "El precio debe ser un número positivo (máx. $99,999.99).";
        }
        if (!esStockValido(stock)) {
            return "El stock debe ser un número entero entre 0 y 9999.";
        }
        return null; // Todo válido
    }

    // ═══ Validación de Seguridad ═══

    /**
     * Valida complejidad de contraseña:
     * - Mínimo 8 caracteres
     * - Al menos una mayúscula, una minúscula, un dígito
     */
    public static boolean esContrasenaSegura(char[] contrasena) {
        if (contrasena == null || contrasena.length < 8) return false;
        boolean tieneUpper = false, tieneLower = false, tieneDigit = false;
        for (char c : contrasena) {
            if (Character.isUpperCase(c)) tieneUpper = true;
            else if (Character.isLowerCase(c)) tieneLower = true;
            else if (Character.isDigit(c)) tieneDigit = true;
        }
        return tieneUpper && tieneLower && tieneDigit;
    }

    /**
     * Retorna mensaje descriptivo si la contraseña no cumple complejidad.
     * Retorna null si es segura.
     */
    public static String mensajeContrasenaInsegura(char[] contrasena) {
        if (contrasena == null || contrasena.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }
        boolean tieneUpper = false, tieneLower = false, tieneDigit = false;
        for (char c : contrasena) {
            if (Character.isUpperCase(c)) tieneUpper = true;
            else if (Character.isLowerCase(c)) tieneLower = true;
            else if (Character.isDigit(c)) tieneDigit = true;
        }
        if (!tieneUpper) return "La contraseña debe contener al menos una letra mayúscula.";
        if (!tieneLower) return "La contraseña debe contener al menos una letra minúscula.";
        if (!tieneDigit) return "La contraseña debe contener al menos un dígito.";
        return null;
    }

    /**
     * Valida nombre de usuario (alfanumérico + guion bajo, 3-30 caracteres).
     */
    public static boolean esUsuarioValido(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) return false;
        String clean = usuario.trim();
        return clean.length() >= 3 && clean.length() <= 30
                && clean.matches("^[a-zA-Z0-9_]+$");
    }
}
