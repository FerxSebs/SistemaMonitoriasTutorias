package uts.edu.java.proyecto.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.modelo.Profesor;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.repositorio.MonitorRepositorio;
import uts.edu.java.proyecto.repositorio.ProfesorRepositorio;
import uts.edu.java.proyecto.repositorio.TutorRepositorio;

import java.util.Collection;
import java.util.Optional;

@Service
public class UsuarioActualServicio {
    
    @Autowired
    private EstudianteRepositorio estudianteRepositorio;
    
    @Autowired
    private ProfesorRepositorio profesorRepositorio;
    
    @Autowired
    private MonitorRepositorio monitorRepositorio;
    
    @Autowired
    private TutorRepositorio tutorRepositorio;
    
    /**
     * Obtiene el ID del estudiante actual si el usuario autenticado es un estudiante
     */
    public Integer getIdEstudianteActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        
        String username = auth.getName();
        
        // Buscar por correo
        Optional<Estudiante> estudianteOpt = estudianteRepositorio.findByCorreo(username);
        if (estudianteOpt.isPresent()) {
            return estudianteOpt.get().getIdEstudiante();
        }
        
        // Buscar por ID si el username es numérico
        try {
            Integer id = Integer.parseInt(username);
            Optional<Estudiante> estudiantePorId = estudianteRepositorio.findById(id);
            if (estudiantePorId.isPresent()) {
                return estudiantePorId.get().getIdEstudiante();
            }
        } catch (NumberFormatException e) {
            // No es un número
        }
        
        return null;
    }
    
    /**
     * Obtiene el ID del monitor actual si el usuario autenticado es un monitor
     */
    public Integer getIdMonitorActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        
        String username = auth.getName();
        
        // Buscar estudiante por correo
        Optional<Estudiante> estudianteOpt = estudianteRepositorio.findByCorreo(username);
        if (estudianteOpt.isPresent()) {
            Estudiante estudiante = estudianteOpt.get();
            // Buscar monitor asociado a este estudiante
            Optional<Monitor> monitorOpt = monitorRepositorio.findAll().stream()
                    .filter(m -> m.getEstudiante() != null && 
                            m.getEstudiante().getIdEstudiante().equals(estudiante.getIdEstudiante()))
                    .findFirst();
            if (monitorOpt.isPresent()) {
                return monitorOpt.get().getIdMonitor();
            }
        }
        
        // Buscar por ID si el username es numérico
        try {
            Integer id = Integer.parseInt(username);
            Optional<Estudiante> estudiantePorId = estudianteRepositorio.findById(id);
            if (estudiantePorId.isPresent()) {
                Estudiante estudiante = estudiantePorId.get();
                Optional<Monitor> monitorOpt = monitorRepositorio.findAll().stream()
                        .filter(m -> m.getEstudiante() != null && 
                                m.getEstudiante().getIdEstudiante().equals(estudiante.getIdEstudiante()))
                        .findFirst();
                if (monitorOpt.isPresent()) {
                    return monitorOpt.get().getIdMonitor();
                }
            }
        } catch (NumberFormatException e) {
            // No es un número
        }
        
        return null;
    }
    
    /**
     * Obtiene el ID del profesor actual si el usuario autenticado es un profesor
     */
    public Integer getIdProfesorActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        
        String username = auth.getName();
        
        // Buscar por correo
        Optional<Profesor> profesorOpt = profesorRepositorio.findByCorreo(username);
        if (profesorOpt.isPresent()) {
            return profesorOpt.get().getIdProfesor();
        }
        
        // Buscar por ID si el username es numérico
        try {
            Integer id = Integer.parseInt(username);
            Optional<Profesor> profesorPorId = profesorRepositorio.findById(id);
            if (profesorPorId.isPresent()) {
                return profesorPorId.get().getIdProfesor();
            }
        } catch (NumberFormatException e) {
            // No es un número
        }
        
        return null;
    }
    
    /**
     * Obtiene el ID del tutor actual si el usuario autenticado es un tutor
     */
    public Integer getIdTutorActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        
        Integer idProfesor = getIdProfesorActual();
        if (idProfesor == null) {
            return null;
        }
        
        // Según la estructura de la BD, el id_tutor es igual al id_profesor
        // Primero intentar buscar directamente por ID
        Optional<Tutor> tutorOpt = tutorRepositorio.findById(idProfesor);
        if (tutorOpt.isPresent()) {
            return tutorOpt.get().getIdTutor();
        }
        
        // Si no se encuentra por ID directo, buscar por relación con profesor
        tutorOpt = tutorRepositorio.findAll().stream()
                .filter(t -> t.getProfesor() != null && 
                        t.getProfesor().getIdProfesor().equals(idProfesor))
                .findFirst();
        
        return tutorOpt.map(Tutor::getIdTutor).orElse(null);
    }
    
    /**
     * Verifica si el usuario actual tiene un rol específico
     */
    public boolean tieneRol(String rol) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rol));
    }
    
    /**
     * Obtiene el nombre del rol principal del usuario actual
     */
    public String getRolPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities.isEmpty()) {
            return null;
        }
        
        // Prioridad: ADMIN > PROFESOR > TUTOR > MONITOR > ESTUDIANTE
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "ADMIN";
        }
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESOR"))) {
            return "PROFESOR";
        }
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"))) {
            return "TUTOR";
        }
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"))) {
            return "MONITOR";
        }
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"))) {
            return "ESTUDIANTE";
        }
        
        return null;
    }
    
    /**
     * Obtiene el Estudiante actual completo
     */
    public Estudiante getEstudianteActual() {
        Integer id = getIdEstudianteActual();
        if (id == null) {
            return null;
        }
        return estudianteRepositorio.findById(id).orElse(null);
    }
    
    /**
     * Obtiene el Profesor actual completo
     */
    public Profesor getProfesorActual() {
        Integer id = getIdProfesorActual();
        if (id == null) {
            return null;
        }
        return profesorRepositorio.findById(id).orElse(null);
    }
    
    /**
     * Obtiene el Monitor actual completo
     */
    public Monitor getMonitorActual() {
        Integer id = getIdMonitorActual();
        if (id == null) {
            return null;
        }
        return monitorRepositorio.findById(id).orElse(null);
    }
    
    /**
     * Obtiene el Tutor actual completo
     */
    public Tutor getTutorActual() {
        Integer id = getIdTutorActual();
        if (id == null) {
            return null;
        }
        return tutorRepositorio.findById(id).orElse(null);
    }
}

