package uts.edu.java.proyecto.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.servicio.MonitorServicio;

@Controller
@RequestMapping("/views/monitores")
public class MonitorAdminControlador {
    
    @Autowired
    MonitorServicio monitorServicio;
    
    @Autowired
    EstudianteRepositorio estudianteRepositorio;
    
    @GetMapping("/")
    public String verMonitores(Model model) {
        List<Monitor> monitores = monitorServicio.getMonitores();
        model.addAttribute("monitores", monitores);
        return "/views/monitores/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevoMonitor(Model model) {
        Monitor monitor = new Monitor();
        List<Estudiante> estudiantes = estudianteRepositorio.findAll();
        model.addAttribute("monitor", monitor);
        model.addAttribute("estudiantes", estudiantes);
        return "/views/monitores/nuevo";
    }
    
    @PostMapping("/guardar")
    public String guardarMonitor(@ModelAttribute("monitor") Monitor monitor, RedirectAttributes redirectAttributes) {
        try {
            monitorServicio.save(monitor);
            redirectAttributes.addFlashAttribute("mensaje", "Monitor creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el monitor: " + e.getMessage());
        }
        return "redirect:/views/monitores/";
    }
    
    @GetMapping("/editar/{id}")
    public String editarMonitor(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Monitor monitor = monitorServicio.findById(id);
        if (monitor == null) {
            redirectAttributes.addFlashAttribute("error", "Monitor no encontrado");
            return "redirect:/views/monitores/";
        }
        List<Estudiante> estudiantes = estudianteRepositorio.findAll();
        model.addAttribute("monitor", monitor);
        model.addAttribute("estudiantes", estudiantes);
        return "/views/monitores/editar";
    }
    
    @PostMapping("/actualizar")
    public String actualizarMonitor(@ModelAttribute("monitor") Monitor monitor, RedirectAttributes redirectAttributes) {
        try {
            monitorServicio.save(monitor);
            redirectAttributes.addFlashAttribute("mensaje", "Monitor actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el monitor: " + e.getMessage());
        }
        return "redirect:/views/monitores/";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarMonitor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            monitorServicio.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Monitor eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el monitor: " + e.getMessage());
        }
        return "redirect:/views/monitores/";
    }
}

