package uts.edu.java.proyecto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uts.edu.java.proyecto.modelo.Rol;
import uts.edu.java.proyecto.modelo.Usuario;
import uts.edu.java.proyecto.repositorio.RolRepositorio;
import uts.edu.java.proyecto.repositorio.UsuarioRepositorio;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RolRepositorio rolRepositorio;
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        if (!rolRepositorio.existsByNombre("ADMIN")) {
            Rol adminRol = new Rol("ADMIN", "Administrador del sistema");
            rolRepositorio.save(adminRol);
        }
        
        if (!rolRepositorio.existsByNombre("USER")) {
            Rol userRol = new Rol("USER", "Usuario estándar");
            rolRepositorio.save(userRol);
        }
        
        if (!rolRepositorio.existsByNombre("PROFESOR")) {
            Rol profesorRol = new Rol("PROFESOR", "Profesor/Tutor");
            rolRepositorio.save(profesorRol);
        }
        
        if (!rolRepositorio.existsByNombre("ESTUDIANTE")) {
            Rol estudianteRol = new Rol("ESTUDIANTE", "Estudiante");
            rolRepositorio.save(estudianteRol);
        }
        
        // Crear usuarios de ejemplo para cada perfil si no existen
        crearUsuarioSiNoExiste("admin", "admin123", "admin@sistema.edu", 
                               "Administrador", "Sistema", "ADMIN");
        crearUsuarioSiNoExiste("user", "user123", "user@sistema.edu", 
                               "Usuario", "Estándar", "USER");
        crearUsuarioSiNoExiste("profesor", "profesor123", "profesor@sistema.edu", 
                               "Juan", "Pérez", "PROFESOR");
        crearUsuarioSiNoExiste("estudiante", "estudiante123", "estudiante@sistema.edu", 
                               "María", "González", "ESTUDIANTE");
    }
    
    private void crearUsuarioSiNoExiste(String username, String password, String email,
                                        String nombre, String apellido, String nombreRol) {
        if (!usuarioRepositorio.existsByUsername(username)) {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setEmail(email);
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setActivo(true);
            
            Rol rol = rolRepositorio.findByNombre(nombreRol)
                    .orElseThrow(() -> new RuntimeException("Rol " + nombreRol + " no encontrado"));
            usuario.agregarRol(rol);
            
            usuarioRepositorio.save(usuario);
            System.out.println("Usuario creado: " + username + " / " + password + " (Rol: " + nombreRol + ")");
        }
    }
}

