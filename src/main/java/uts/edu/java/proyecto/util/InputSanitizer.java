package uts.edu.java.proyecto.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Utilidad para sanitizar y validar entradas del usuario
 * Previene ataques XSS y otros tipos de inyección
 */
public class InputSanitizer {
    
    // Patrones peligrosos comunes
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
        "<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(
        "javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern ON_EVENT_PATTERN = Pattern.compile(
        "on\\w+\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|vbscript|onload|onerror)");
    
    /**
     * Sanitiza una cadena de texto eliminando caracteres peligrosos
     * @param input Texto a sanitizar
     * @return Texto sanitizado
     */
    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Eliminar etiquetas script
        String sanitized = SCRIPT_PATTERN.matcher(input).replaceAll("");
        
        // Eliminar javascript: en URLs
        sanitized = JAVASCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        
        // Eliminar eventos on* (onclick, onload, etc.)
        sanitized = ON_EVENT_PATTERN.matcher(sanitized).replaceAll("");
        
        // Escapar caracteres HTML especiales
        sanitized = sanitized
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
        
        // Eliminar caracteres de control
        sanitized = sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "");
        
        return sanitized.trim();
    }
    
    /**
     * Valida que un ID sea un número positivo válido
     * @param id ID a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidId(Integer id) {
        return id != null && id > 0;
    }
    
    /**
     * Valida que un ID de cadena sea numérico y positivo
     * @param idString ID como cadena
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidIdString(String idString) {
        if (!StringUtils.hasText(idString)) {
            return false;
        }
        try {
            Integer id = Integer.parseInt(idString);
            return id > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida un email básico
     * @param email Email a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        // Validación básica de formato de email
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailPattern, email);
    }
    
    /**
     * Limpia y normaliza un número de teléfono
     * @param phone Teléfono a limpiar
     * @return Teléfono limpio o null si es inválido
     */
    public static String sanitizePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        // Eliminar caracteres no numéricos excepto +, -, espacios y paréntesis
        String cleaned = phone.replaceAll("[^0-9+\\-() ]", "");
        return cleaned.trim();
    }
    
    /**
     * Valida la longitud de una cadena
     * @param input Cadena a validar
     * @param minLength Longitud mínima
     * @param maxLength Longitud máxima
     * @return true si cumple con los límites, false en caso contrario
     */
    public static boolean isValidLength(String input, int minLength, int maxLength) {
        if (input == null) {
            return minLength == 0;
        }
        int length = input.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Detecta posibles intentos de SQL Injection en una cadena
     * @param input Cadena a analizar
     * @return true si detecta patrones sospechosos, false en caso contrario
     */
    public static boolean containsSqlInjectionPattern(String input) {
        if (!StringUtils.hasText(input)) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }
}

