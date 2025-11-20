package uts.edu.java.proyecto.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Profesor;
import uts.edu.java.proyecto.modelo.Rol;
import uts.edu.java.proyecto.modelo.Usuario;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.repositorio.ProfesorRepositorio;
import uts.edu.java.proyecto.repositorio.RolRepositorio;
import uts.edu.java.proyecto.repositorio.UsuarioRepositorio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio implements UserDetailsService {
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private EstudianteRepositorio estudianteRepositorio;
    
    @Autowired
    private ProfesorRepositorio profesorRepositorio;
    
    @Autowired
    private RolRepositorio rolRepositorio;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primero intentar con usuarios del sistema (admin)
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (!usuario.getActivo()) {
                throw new UsernameNotFoundException("Usuario inactivo: " + username);
            }
            return User.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPassword())
                    .authorities(getAuthorities(usuario.getRoles()))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(!usuario.getActivo())
                    .build();
        }
        
        // Intentar buscar por correo o ID en Estudiante
        Optional<Estudiante> estudianteOpt = estudianteRepositorio.findByCorreo(username);
        if (!estudianteOpt.isPresent()) {
            // Intentar por ID si es numérico
            try {
                Integer id = Integer.parseInt(username);
                estudianteOpt = estudianteRepositorio.findById(id);
            } catch (NumberFormatException e) {
                // No es un número, continuar
            }
        }
        
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            if (estudiante.getPassword() == null || estudiante.getPassword().isEmpty()) {
                throw new UsernameNotFoundException("Contraseña no configurada para el estudiante: " + username);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
            
            // Si es monitor, agregar rol de monitor
            if (estudiante.getEsMonitor() != null && estudiante.getEsMonitor()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_MONITOR"));
            }
            
            return User.builder()
                    .username(estudiante.getCorreo())
                    .password(estudiante.getPassword())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        }
        
        // Intentar buscar por correo o ID en Profesor
        Optional<Profesor> profesorOpt = profesorRepositorio.findByCorreo(username);
        if (!profesorOpt.isPresent()) {
            try {
                Integer id = Integer.parseInt(username);
                profesorOpt = profesorRepositorio.findById(id);
            } catch (NumberFormatException e) {
                // Continuar
            }
        }
        
        if (profesorOpt.isPresent()) {
            Profesor profesor = profesorOpt.get();
            if (profesor.getPassword() == null || profesor.getPassword().isEmpty()) {
                throw new UsernameNotFoundException("Contraseña no configurada para el profesor: " + username);
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_PROFESOR"));
            
            // Si es tutor, agregar rol de tutor
            if (profesor.getEsTutor() != null && profesor.getEsTutor()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_TUTOR"));
            }
            
            return User.builder()
                    .username(profesor.getCorreo())
                    .password(profesor.getPassword())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        }
        
        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Rol> roles) {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepositorio.save(usuario);
    }
    
    @Transactional
    public Usuario crearUsuarioConRol(String username, String password, String email, 
                                      String nombre, String apellido, String nombreRol) {
        Usuario usuario = new Usuario(username, password, email, nombre, apellido);
        usuario.setPassword(passwordEncoder.encode(password));
        
        Rol rol = rolRepositorio.findByNombre(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));
        
        usuario.agregarRol(rol);
        return usuarioRepositorio.save(usuario);
    }
    
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepositorio.findByUsername(username);
    }
    
    public boolean existeUsername(String username) {
        return usuarioRepositorio.existsByUsername(username);
    }
    
    public boolean existeEmail(String email) {
        return usuarioRepositorio.existsByEmail(email);
    }
}

