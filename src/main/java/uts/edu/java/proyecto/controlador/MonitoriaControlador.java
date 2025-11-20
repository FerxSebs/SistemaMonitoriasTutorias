package uts.edu.java.proyecto.controlador;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.modelo.Monitoria;
import uts.edu.java.proyecto.repositorio.MonitoriaRepositorio;
import uts.edu.java.proyecto.servicio.EstudianteServicio;
import uts.edu.java.proyecto.servicio.MateriaServicio;
import uts.edu.java.proyecto.servicio.MonitorServicio;
import uts.edu.java.proyecto.servicio.MonitoriaServicio;
import uts.edu.java.proyecto.servicio.UsuarioActualServicio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("views/monitorias")
public class MonitoriaControlador {
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null && !text.isEmpty()) {
                    try {
                        // Formato esperado: yyyy-MM-ddTHH:mm (del input datetime-local)
                        setValue(LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
                    } catch (Exception e) {
                        // Intentar con formato alternativo si el primero falla
                        try {
                            setValue(LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        } catch (Exception ex) {
                            throw new IllegalArgumentException("Formato de fecha inválido: " + text, ex);
                        }
                    }
                } else {
                    setValue(null);
                }
            }
            
            @Override
            public String getAsText() {
                LocalDateTime value = (LocalDateTime) getValue();
                return (value != null ? value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")) : "");
            }
        });
    }
    
    @Autowired
    MonitoriaServicio monitoriaServicio;
    
    @Autowired
    EstudianteServicio estudianteServicio;
    
    @Autowired
    MonitorServicio monitorServicio;
    
    @Autowired
    MateriaServicio materiaServicio;
    
    @Autowired
    UsuarioActualServicio usuarioActualServicio;
    
    @Autowired
    MonitoriaRepositorio monitoriaRepositorio;
    
    @GetMapping("/")
    public String verMonitorias(Model model) {
        List<Monitoria> listaMonitorias;
        
        // Filtrar según el rol del usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMonitor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"));
        boolean isEstudiante = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
        
        if (isAdmin) {
            // Admin ve todas las monitorías
            listaMonitorias = monitoriaServicio.getMonitorias();
        } else if (isEstudiante) {
            // Estudiante ve solo sus monitorías (las que solicitó)
            Integer idEstudiante = usuarioActualServicio.getIdEstudianteActual();
            if (idEstudiante != null) {
                listaMonitorias = monitoriaRepositorio.findByIdEstudiante(idEstudiante);
            } else {
                listaMonitorias = java.util.Collections.emptyList();
            }
        } else if (isMonitor && !isAdmin) {
            // Monitor ve solo sus monitorías asignadas (las que tiene a cargo)
            Integer idMonitor = usuarioActualServicio.getIdMonitorActual();
            if (idMonitor != null) {
                listaMonitorias = monitoriaRepositorio.findByIdMonitor(idMonitor);
            } else {
                listaMonitorias = java.util.Collections.emptyList();
            }
        } else {
            listaMonitorias = java.util.Collections.emptyList();
        }
        
        model.addAttribute("monitorias", listaMonitorias);
        return "/views/monitorias/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevaMonitoria(Model model) {
        Monitoria monitoria = new Monitoria();
        List<Estudiante> estudiantes = estudianteServicio.getEstudiantes();
        List<Monitor> monitores = monitorServicio.getMonitores();
        List<Materia> materias = materiaServicio.getMaterias();
        model.addAttribute("monitoria", monitoria);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("monitores", monitores);
        model.addAttribute("materias", materias);
        return "/views/monitorias/nuevo";
    }
    
    @PostMapping("/guardar")
    public String guardarMonitoria(@ModelAttribute("monitoria") Monitoria monitoria, RedirectAttributes redirectAttributes) {
        try {
            // Las relaciones ya están establecidas por los IDs en el modelo
            monitoriaServicio.save(monitoria);
            redirectAttributes.addFlashAttribute("mensaje", "Monitoría creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la monitoría: " + e.getMessage());
        }
        return "redirect:/views/monitorias/";
    }
    
    @GetMapping("/editar/{id}")
    public String editarMonitoria(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        // Verificar permisos según el rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMonitor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"));
        boolean isEstudiante = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
        
        // Los estudiantes no pueden editar monitorías
        if (isEstudiante && !isAdmin && !isMonitor) {
            redirectAttributes.addFlashAttribute("error", "Los estudiantes no pueden modificar monitorías");
            return "redirect:/error/acceso-denegado";
        }
        
        Monitoria monitoria = monitoriaServicio.findById(id);
        if (monitoria == null) {
            redirectAttributes.addFlashAttribute("error", "Monitoría no encontrada");
            return "redirect:/error/acceso-denegado";
        }
        
        List<Estudiante> estudiantes = estudianteServicio.getEstudiantes();
        List<Materia> materias = materiaServicio.getMaterias();
        List<Monitor> monitores;
        
        if (isMonitor && !isAdmin) {
            // Si es monitor, verificar que la monitoría pertenezca a él
            Integer idMonitorActual = usuarioActualServicio.getIdMonitorActual();
            if (idMonitorActual == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo identificar el monitor actual");
                return "redirect:/error/acceso-denegado";
            }
            if (!monitoria.getIdMonitor().equals(idMonitorActual)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta monitoría");
                return "redirect:/error/acceso-denegado";
            }
            // Para el monitor, solo mostrar el monitor actual (no puede cambiarlo)
            Monitor monitorActual = monitorServicio.findById(idMonitorActual);
            if (monitorActual == null) {
                redirectAttributes.addFlashAttribute("error", "Monitor no encontrado");
                return "redirect:/error/acceso-denegado";
            }
            monitores = java.util.Collections.singletonList(monitorActual);
            model.addAttribute("esMonitor", true);
        } else {
            // Admin puede ver todos los monitores
            monitores = monitorServicio.getMonitores();
            model.addAttribute("esMonitor", false);
        }
        
        model.addAttribute("monitoria", monitoria);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("monitores", monitores);
        model.addAttribute("materias", materias);
        return "/views/monitorias/editar";
    }
    
    @PostMapping("/actualizar")
    public String actualizarMonitoria(@ModelAttribute("monitoria") Monitoria monitoria, RedirectAttributes redirectAttributes) {
        try {
            // Verificar permisos según el rol
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isMonitor = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"));
            boolean isEstudiante = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
            
            // Los estudiantes no pueden actualizar monitorías
            if (isEstudiante && !isAdmin && !isMonitor) {
                redirectAttributes.addFlashAttribute("error", "Los estudiantes no pueden modificar monitorías");
                return "redirect:/error/acceso-denegado";
            }
            
            if (isMonitor && !isAdmin) {
                // Si es monitor, verificar que la monitoría pertenezca a él
                Monitoria monitoriaExistente = monitoriaServicio.findById(monitoria.getIdMonitoria());
                if (monitoriaExistente == null) {
                    redirectAttributes.addFlashAttribute("error", "Monitoría no encontrada");
                    return "redirect:/error/acceso-denegado";
                }
                Integer idMonitorActual = usuarioActualServicio.getIdMonitorActual();
                if (idMonitorActual == null || !monitoriaExistente.getIdMonitor().equals(idMonitorActual)) {
                    redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta monitoría");
                    return "redirect:/error/acceso-denegado";
                }
                // Asegurar que el ID del monitor no cambie
                monitoria.setIdMonitor(idMonitorActual);
            }
            
            // Las relaciones ya están establecidas por los IDs en el modelo
            monitoriaServicio.update(monitoria);
            redirectAttributes.addFlashAttribute("mensaje", "Monitoría actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la monitoría: " + e.getMessage());
        }
        return "redirect:/views/monitorias/";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarMonitoria(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            // Verificar permisos según el rol
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isMonitor = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"));
            boolean isEstudiante = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
            
            // Los estudiantes no pueden eliminar monitorías
            if (isEstudiante && !isAdmin && !isMonitor) {
                redirectAttributes.addFlashAttribute("error", "Los estudiantes no pueden eliminar monitorías");
                return "redirect:/error/acceso-denegado";
            }
            
            Monitoria monitoria = monitoriaServicio.findById(id);
            if (monitoria == null) {
                redirectAttributes.addFlashAttribute("error", "Monitoría no encontrada");
                return "redirect:/views/monitorias/";
            }
            
            // Si es monitor, verificar que la monitoría pertenezca a él
            if (isMonitor && !isAdmin) {
                Integer idMonitorActual = usuarioActualServicio.getIdMonitorActual();
                if (idMonitorActual == null || !monitoria.getIdMonitor().equals(idMonitorActual)) {
                    redirectAttributes.addFlashAttribute("error", "No tiene permiso para eliminar esta monitoría");
                    return "redirect:/error/acceso-denegado";
                }
            }
            
            monitoriaServicio.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Monitoría eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la monitoría: " + e.getMessage());
        }
        return "redirect:/views/monitorias/";
    }
}
