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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Factura;
import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.modelo.Monitoria;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.modelo.Tutoria;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.repositorio.FacturaRepositorio;
import uts.edu.java.proyecto.repositorio.MonitoriaRepositorio;
import uts.edu.java.proyecto.repositorio.TutoriaRepositorio;
import uts.edu.java.proyecto.servicio.EstudianteServicio;
import uts.edu.java.proyecto.servicio.FacturaServicio;
import uts.edu.java.proyecto.servicio.MateriaServicio;
import uts.edu.java.proyecto.servicio.MonitorServicio;
import uts.edu.java.proyecto.servicio.MonitoriaServicio;
import uts.edu.java.proyecto.servicio.TutorServicio;
import uts.edu.java.proyecto.servicio.TutoriaServicio;

@Controller
@RequestMapping("/estudiante")
public class EstudianteControlador {
    
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
    EstudianteServicio estudianteServicio;
    
    @Autowired
    EstudianteRepositorio estudianteRepositorio;
    
    @Autowired
    TutoriaServicio tutoriaServicio;
    
    @Autowired
    TutoriaRepositorio tutoriaRepositorio;
    
    @Autowired
    MonitoriaServicio monitoriaServicio;
    
    @Autowired
    MonitoriaRepositorio monitoriaRepositorio;
    
    @Autowired
    TutorServicio tutorServicio;
    
    @Autowired
    MonitorServicio monitorServicio;
    
    @Autowired
    MateriaServicio materiaServicio;
    
    @Autowired
    FacturaRepositorio facturaRepositorio;
    
    @Autowired
    FacturaServicio facturaServicio;
    
    private Estudiante getEstudianteActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String username = auth.getName();
        
        // Intentar buscar por correo primero
        java.util.Optional<Estudiante> estudianteOpt = estudianteRepositorio.findByCorreo(username);
        
        // Si no se encuentra por correo, intentar por ID si es numérico
        if (!estudianteOpt.isPresent()) {
            try {
                Integer id = Integer.parseInt(username);
                estudianteOpt = estudianteRepositorio.findById(id);
            } catch (NumberFormatException e) {
                // No es un número, continuar
            }
        }
        
        return estudianteOpt.orElse(null);
    }
    
    // Tutorías
    @GetMapping("/tutorias/mis-tutorias")
    public String misTutorias(Model model) {
        Estudiante estudiante = getEstudianteActual();
        if (estudiante == null) {
            return "redirect:/login";
        }
        // Filtrar tutorías solo del estudiante autenticado por su ID
        List<Tutoria> tutorias = tutoriaRepositorio.findByIdEstudiante(estudiante.getIdEstudiante());
        model.addAttribute("tutorias", tutorias);
        model.addAttribute("estudiante", estudiante);
        return "/views/tutorias/lista";
    }
    
    @GetMapping("/tutorias/nuevo")
    public String nuevaTutoria(Model model) {
        Tutoria tutoria = new Tutoria();
        List<Tutor> tutores = tutorServicio.getTutores();
        List<Materia> materias = materiaServicio.getMaterias();
        model.addAttribute("tutoria", tutoria);
        model.addAttribute("tutores", tutores);
        model.addAttribute("materias", materias);
        return "/views/tutorias/nuevo";
    }
    
    @PostMapping("/tutorias/guardar")
    public String guardarTutoria(@ModelAttribute("tutoria") Tutoria tutoria, RedirectAttributes redirectAttributes) {
        try {
            Estudiante estudiante = getEstudianteActual();
            if (estudiante == null) {
                redirectAttributes.addFlashAttribute("error", "Estudiante no encontrado. Por favor, inicie sesión nuevamente.");
                return "redirect:/login";
            }
            // Asegurar que el ID del estudiante sea siempre el del usuario autenticado
            tutoria.setIdEstudiante(estudiante.getIdEstudiante());
            // Limpiar el ID de tutoría para crear una nueva
            tutoria.setIdTutoria(null);
            tutoriaServicio.save(tutoria);
            redirectAttributes.addFlashAttribute("mensaje", "Tutoría creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la tutoría: " + e.getMessage());
        }
        return "redirect:/estudiante/tutorias/mis-tutorias";
    }
    
    // Monitorías
    @GetMapping("/monitorias/mis-monitorias")
    public String misMonitorias(Model model) {
        Estudiante estudiante = getEstudianteActual();
        if (estudiante == null) {
            return "redirect:/login";
        }
        // Filtrar monitorías solo del estudiante autenticado por su ID
        List<Monitoria> monitorias = monitoriaRepositorio.findByIdEstudiante(estudiante.getIdEstudiante());
        model.addAttribute("monitorias", monitorias);
        model.addAttribute("estudiante", estudiante);
        return "/views/monitorias/lista";
    }
    
    @GetMapping("/monitorias/nuevo")
    public String nuevaMonitoria(Model model) {
        Monitoria monitoria = new Monitoria();
        List<Monitor> monitores = monitorServicio.getMonitores();
        List<Materia> materias = materiaServicio.getMaterias();
        model.addAttribute("monitoria", monitoria);
        model.addAttribute("monitores", monitores);
        model.addAttribute("materias", materias);
        return "/views/monitorias/nuevo";
    }
    
    @PostMapping("/monitorias/guardar")
    public String guardarMonitoria(@ModelAttribute("monitoria") Monitoria monitoria, RedirectAttributes redirectAttributes, Model model) {
        try {
            Estudiante estudiante = getEstudianteActual();
            if (estudiante == null) {
                redirectAttributes.addFlashAttribute("error", "Estudiante no encontrado. Por favor, inicie sesión nuevamente.");
                return "redirect:/login";
            }
            // Asegurar que el ID del estudiante sea siempre el del usuario autenticado
            monitoria.setIdEstudiante(estudiante.getIdEstudiante());
            // Limpiar el ID de monitoría para crear una nueva
            monitoria.setIdMonitoria(null);
            Monitoria monitoriaGuardada = monitoriaServicio.save(monitoria);
            
            // Buscar la factura generada para esta monitoría
            Factura factura = facturaRepositorio.findByMonitoriaIdMonitoria(monitoriaGuardada.getIdMonitoria()).orElse(null);
            
            if (factura != null) {
                // Redirigir a la vista de detalle de factura
                redirectAttributes.addFlashAttribute("mensaje", "Monitoría creada exitosamente. Aquí está el detalle de la factura:");
                return "redirect:/estudiante/monitorias/factura/" + factura.getIdFactura();
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Monitoría creada exitosamente");
                return "redirect:/estudiante/monitorias/mis-monitorias";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la monitoría: " + e.getMessage());
            return "redirect:/estudiante/monitorias/nuevo";
        }
    }
    
    @GetMapping("/monitorias/factura/{idFactura}")
    public String verFactura(@PathVariable Integer idFactura, Model model, RedirectAttributes redirectAttributes) {
        Factura factura = facturaServicio.findById(idFactura);
        if (factura == null) {
            redirectAttributes.addFlashAttribute("error", "Factura no encontrada");
            return "redirect:/estudiante/monitorias/mis-monitorias";
        }
        
        // Verificar que la factura pertenezca al estudiante actual
        Estudiante estudiante = getEstudianteActual();
        if (estudiante == null || factura.getMonitoria() == null || 
            !factura.getMonitoria().getIdEstudiante().equals(estudiante.getIdEstudiante())) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para ver esta factura");
            return "redirect:/estudiante/monitorias/mis-monitorias";
        }
        
        model.addAttribute("factura", factura);
        return "/views/facturas/detalle";
    }
}
