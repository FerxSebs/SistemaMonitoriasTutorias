// Script para generar hashes BCrypt
// Ejecutar este código en un proyecto Java con Spring Security para obtener los hashes

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("Hashes BCrypt generados:");
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("user123: " + encoder.encode("user123"));
        System.out.println("profesor123: " + encoder.encode("profesor123"));
        System.out.println("estudiante123: " + encoder.encode("estudiante123"));
    }
}

