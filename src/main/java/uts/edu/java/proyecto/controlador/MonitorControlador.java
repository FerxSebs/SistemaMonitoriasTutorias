package uts.edu.java.proyecto.controlador;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Factura;
import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.modelo.Monitoria;
import uts.edu.java.proyecto.modelo.Tutoria;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.repositorio.FacturaRepositorio;
import uts.edu.java.proyecto.repositorio.MonitorRepositorio;
import uts.edu.java.proyecto.repositorio.MonitoriaRepositorio;
import uts.edu.java.proyecto.repositorio.TutoriaRepositorio;
import uts.edu.java.proyecto.servicio.EstudianteServicio;
import uts.edu.java.proyecto.servicio.MateriaServicio;
import uts.edu.java.proyecto.servicio.MonitorServicio;
import uts.edu.java.proyecto.servicio.MonitoriaServicio;
import uts.edu.java.proyecto.servicio.TutoriaServicio;

@Controller
@RequestMapping("/monitor")
public class MonitorControlador {
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null && !text.isEmpty()) {
                    try {
                        setValue(LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
                    } catch (Exception e) {
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
    MonitorRepositorio monitorRepositorio;
    
    @Autowired
    EstudianteRepositorio estudianteRepositorio;
    
    @Autowired
    MonitoriaRepositorio monitoriaRepositorio;
    
    @Autowired
    MonitoriaServicio monitoriaServicio;
    
    @Autowired
    TutoriaRepositorio tutoriaRepositorio;
    
    @Autowired
    TutoriaServicio tutoriaServicio;
    
    @Autowired
    FacturaRepositorio facturaRepositorio;
    
    @Autowired
    EstudianteServicio estudianteServicio;
    
    @Autowired
    MonitorServicio monitorServicio;
    
    @Autowired
    MateriaServicio materiaServicio;
    
    private Monitor getMonitorActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Estudiante estudiante = estudianteRepositorio.findByCorreo(username).orElse(null);
        if (estudiante != null) {
            return monitorRepositorio.findById(estudiante.getIdEstudiante()).orElse(null);
        }
        return null;
    }
    
    // Monitorías a cargo
    @GetMapping("/monitorias/")
    public String monitoriasACargo(Model model) {
        Monitor monitor = getMonitorActual();
        if (monitor == null) {
            return "redirect:/login";
        }
        List<Monitoria> monitorias = monitoriaRepositorio.findByIdMonitor(monitor.getIdMonitor());
        model.addAttribute("monitorias", monitorias);
        return "/views/monitorias/lista";
    }
    
    @GetMapping("/monitorias/editar/{id}")
    public String editarMonitoria(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Monitor monitor = getMonitorActual();
        if (monitor == null) {
            return "redirect:/login";
        }
        
        Monitoria monitoria = monitoriaServicio.findById(id);
        if (monitoria == null) {
            redirectAttributes.addFlashAttribute("error", "Monitoría no encontrada");
            return "redirect:/monitor/monitorias/";
        }
        
        // Verificar que la monitoría pertenezca al monitor actual
        if (!monitoria.getIdMonitor().equals(monitor.getIdMonitor())) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta monitoría");
            return "redirect:/monitor/monitorias/";
        }
        
        List<Estudiante> estudiantes = estudianteServicio.getEstudiantes();
        List<Materia> materias = materiaServicio.getMaterias();
        
        // Para el monitor, solo mostrar el monitor actual (no puede cambiarlo)
        List<Monitor> monitores = java.util.Collections.singletonList(monitor);
        
        model.addAttribute("monitoria", monitoria);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("monitores", monitores);
        model.addAttribute("materias", materias);
        model.addAttribute("esMonitor", true); // Flag para deshabilitar el campo monitor en la vista
        return "/views/monitorias/editar";
    }
    
    @PostMapping("/monitorias/actualizar")
    public String actualizarMonitoria(@ModelAttribute("monitoria") Monitoria monitoria, RedirectAttributes redirectAttributes) {
        try {
            Monitor monitor = getMonitorActual();
            if (monitor == null) {
                redirectAttributes.addFlashAttribute("error", "Monitor no encontrado");
                return "redirect:/login";
            }
            
            // Verificar que la monitoría pertenezca al monitor actual
            Monitoria monitoriaExistente = monitoriaServicio.findById(monitoria.getIdMonitoria());
            if (monitoriaExistente == null) {
                redirectAttributes.addFlashAttribute("error", "Monitoría no encontrada");
                return "redirect:/monitor/monitorias/";
            }
            
            if (!monitoriaExistente.getIdMonitor().equals(monitor.getIdMonitor())) {
                redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta monitoría");
                return "redirect:/monitor/monitorias/";
            }
            
            // Asegurar que el ID del monitor no cambie
            monitoria.setIdMonitor(monitor.getIdMonitor());
            
            // Actualizar la monitoría
            monitoriaServicio.update(monitoria);
            redirectAttributes.addFlashAttribute("mensaje", "Monitoría actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la monitoría: " + e.getMessage());
        }
        return "redirect:/monitor/monitorias/";
    }
    
    @PostMapping("/monitorias/actualizar-estado/{id}")
    public String actualizarEstadoMonitoria(@PathVariable Integer id, @RequestParam String estado, RedirectAttributes redirectAttributes) {
        try {
            Monitor monitor = getMonitorActual();
            if (monitor == null) {
                redirectAttributes.addFlashAttribute("error", "Monitor no encontrado");
                return "redirect:/login";
            }
            
            Monitoria monitoria = monitoriaServicio.findById(id);
            if (monitoria != null) {
                // Verificar que la monitoría pertenezca al monitor actual
                if (!monitoria.getIdMonitor().equals(monitor.getIdMonitor())) {
                    redirectAttributes.addFlashAttribute("error", "No tiene permiso para modificar esta monitoría");
                    return "redirect:/monitor/monitorias/";
                }
                monitoria.setEstado(estado);
                monitoriaServicio.update(monitoria);
                redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Monitoría no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el estado: " + e.getMessage());
        }
        return "redirect:/monitor/monitorias/";
    }
    
    // Tutorías solicitadas por el monitor (cuando es estudiante)
    @GetMapping("/tutorias/mis-tutorias")
    public String misTutorias(Model model) {
        Monitor monitor = getMonitorActual();
        if (monitor == null) {
            return "redirect:/login";
        }
        Estudiante estudiante = monitor.getEstudiante();
        List<Tutoria> tutorias = tutoriaRepositorio.findByIdEstudiante(estudiante.getIdEstudiante());
        model.addAttribute("tutorias", tutorias);
        return "/views/tutorias/lista";
    }
    
    // Monitorías solicitadas por el monitor (cuando es estudiante)
    @GetMapping("/monitorias/mis-monitorias")
    public String misMonitorias(Model model) {
        Monitor monitor = getMonitorActual();
        if (monitor == null) {
            return "redirect:/login";
        }
        Estudiante estudiante = monitor.getEstudiante();
        List<Monitoria> monitorias = monitoriaRepositorio.findByIdEstudiante(estudiante.getIdEstudiante());
        model.addAttribute("monitorias", monitorias);
        return "/views/monitorias/lista";
    }
    
    // Facturación
    @GetMapping("/facturacion/")
    public String facturacion(@RequestParam(required = false) String fechaInicio, 
                             @RequestParam(required = false) String fechaFin,
                             Model model) {
        Monitor monitor = getMonitorActual();
        if (monitor == null) {
            return "redirect:/login";
        }
        
        List<Factura> facturas;
        if (fechaInicio != null && !fechaInicio.isEmpty() && fechaFin != null && !fechaFin.isEmpty()) {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio + "T00:00:00");
            LocalDateTime fin = LocalDateTime.parse(fechaFin + "T23:59:59");
            facturas = facturaRepositorio.findByMonitorAndFechaBetween(monitor.getIdMonitor(), inicio, fin);
        } else {
            // Últimos 30 días por defecto
            LocalDateTime fin = LocalDateTime.now();
            LocalDateTime inicio = fin.minusDays(30);
            facturas = facturaRepositorio.findByMonitorAndFechaBetween(monitor.getIdMonitor(), inicio, fin);
        }
        
        model.addAttribute("facturas", facturas);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        
        // Calcular totales
        double totalFacturado = facturas.stream().mapToDouble(Factura::getTotal).sum();
        model.addAttribute("totalFacturado", totalFacturado);
        
        return "/views/facturas/lista";
    }
}
