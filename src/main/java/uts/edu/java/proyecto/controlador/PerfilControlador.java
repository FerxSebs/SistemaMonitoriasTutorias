package uts.edu.java.proyecto.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.modelo.Profesor;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.servicio.EstudianteServicio;
import uts.edu.java.proyecto.servicio.MonitorServicio;
import uts.edu.java.proyecto.servicio.ProfesorServicio;
import uts.edu.java.proyecto.servicio.TutorServicio;
import uts.edu.java.proyecto.servicio.UsuarioActualServicio;

@Controller
@RequestMapping("/perfil")
public class PerfilControlador {
    
    @Autowired
    private UsuarioActualServicio usuarioActualServicio;
    
    @Autowired
    private EstudianteServicio estudianteServicio;
    
    @Autowired
    private ProfesorServicio profesorServicio;
    
    @Autowired
    private MonitorServicio monitorServicio;
    
    @Autowired
    private TutorServicio tutorServicio;
    
    @GetMapping("/editar")
    public String editarPerfil(Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para acceder a su perfil");
            return "redirect:/login";
        }
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isEstudiante = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
        boolean isProfesor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESOR"));
        boolean isMonitor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"));
        boolean isTutor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));
        
        // Determinar el tipo de usuario principal
        if (isEstudiante || isMonitor) {
            // Si es estudiante o monitor, cargar datos del estudiante
            Estudiante estudiante = usuarioActualServicio.getEstudianteActual();
            if (estudiante == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo cargar la información del estudiante");
                return "redirect:/home";
            }
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("tipoUsuario", "estudiante");
            
            // Si también es monitor, cargar información del monitor
            if (isMonitor) {
                Monitor monitor = usuarioActualServicio.getMonitorActual();
                model.addAttribute("monitor", monitor);
                model.addAttribute("esMonitor", true);
            } else {
                model.addAttribute("esMonitor", false);
            }
            
            return "perfil/editar";
        } else if (isProfesor || isTutor) {
            // Si es profesor o tutor, cargar datos del profesor
            Profesor profesor = usuarioActualServicio.getProfesorActual();
            if (profesor == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo cargar la información del profesor");
                return "redirect:/home";
            }
            model.addAttribute("profesor", profesor);
            model.addAttribute("tipoUsuario", "profesor");
            
            // Si también es tutor, cargar información del tutor
            if (isTutor) {
                Tutor tutor = usuarioActualServicio.getTutorActual();
                model.addAttribute("tutor", tutor);
                model.addAttribute("esTutor", true);
            } else {
                model.addAttribute("esTutor", false);
            }
            
            return "perfil/editar";
        } else if (isAdmin) {
            // Para admin, redirigir al home (los admins no tienen perfil de estudiante/profesor)
            redirectAttributes.addFlashAttribute("info", "Los administradores deben usar el panel de administración para gestionar usuarios");
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tipo de usuario no reconocido");
            return "redirect:/home";
        }
    }
    
    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String areaExpertise,
            @RequestParam(required = false) String estado,
            RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para actualizar su perfil");
            return "redirect:/login";
        }
        
        boolean isEstudiante = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
        boolean isProfesor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESOR"));
        boolean isMonitor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"));
        boolean isTutor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));
        
        try {
            if (isEstudiante || isMonitor) {
                // Actualizar estudiante
                Estudiante estudianteActual = usuarioActualServicio.getEstudianteActual();
                if (estudianteActual == null) {
                    redirectAttributes.addFlashAttribute("error", "No se pudo encontrar el estudiante");
                    return "redirect:/perfil/editar";
                }
                
                // Actualizar solo los campos permitidos
                if (nombre != null) {
                    estudianteActual.setNombre(nombre);
                }
                if (apellido != null) {
                    estudianteActual.setApellido(apellido);
                }
                if (telefono != null) {
                    estudianteActual.setTelefono(telefono);
                }
                
                // Si se proporciona una nueva contraseña, actualizarla
                if (password != null && !password.trim().isEmpty()) {
                    estudianteActual.setPassword(password);
                }
                
                // Mantener los campos que no se pueden modificar
                // (ID, correo, programa académico ya están en estudianteActual)
                
                estudianteServicio.update(estudianteActual);
                
                // Si es monitor y se proporcionan datos del monitor, actualizarlos
                if (isMonitor && (areaExpertise != null || estado != null)) {
                    Monitor monitorActual = usuarioActualServicio.getMonitorActual();
                    if (monitorActual != null) {
                        if (areaExpertise != null) {
                            monitorActual.setAreaExpertise(areaExpertise);
                        }
                        if (estado != null) {
                            monitorActual.setEstado(estado);
                        }
                        monitorServicio.save(monitorActual);
                    }
                }
                
                redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado exitosamente");
            } else if (isProfesor || isTutor) {
                // Actualizar profesor
                Profesor profesorActual = usuarioActualServicio.getProfesorActual();
                if (profesorActual == null) {
                    redirectAttributes.addFlashAttribute("error", "No se pudo encontrar el profesor");
                    return "redirect:/perfil/editar";
                }
                
                // Actualizar solo los campos permitidos
                if (nombre != null) {
                    profesorActual.setNombre(nombre);
                }
                if (apellido != null) {
                    profesorActual.setApellido(apellido);
                }
                if (telefono != null) {
                    profesorActual.setTelefono(telefono);
                }
                
                // Si se proporciona una nueva contraseña, actualizarla
                if (password != null && !password.trim().isEmpty()) {
                    profesorActual.setPassword(password);
                }
                
                // Mantener los campos que no se pueden modificar
                // (ID, correo, departamento ya están en profesorActual)
                
                profesorServicio.update(profesorActual);
                
                // Si es tutor y se proporcionan datos del tutor, actualizarlos
                if (isTutor && areaExpertise != null) {
                    Tutor tutorActual = usuarioActualServicio.getTutorActual();
                    if (tutorActual != null) {
                        tutorActual.setAreaExpertise(areaExpertise);
                        tutorServicio.save(tutorActual);
                    }
                }
                
                redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tipo de usuario no reconocido");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return "redirect:/perfil/editar";
    }
}

