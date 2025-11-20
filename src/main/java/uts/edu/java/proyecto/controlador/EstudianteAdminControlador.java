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
import uts.edu.java.proyecto.servicio.EstudianteServicio;

@Controller
@RequestMapping("/views/estudiantes")
public class EstudianteAdminControlador {
    
    @Autowired
    EstudianteServicio estudianteServicio;
    
    @GetMapping("/")
    public String verEstudiantes(Model model) {
        List<Estudiante> estudiantes = estudianteServicio.getEstudiantes();
        model.addAttribute("estudiantes", estudiantes);
        return "/views/estudiantes/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevoEstudiante(Model model) {
        Estudiante estudiante = new Estudiante();
        model.addAttribute("estudiante", estudiante);
        return "/views/estudiantes/nuevo";
    }
    
    @PostMapping("/guardar")
    public String guardarEstudiante(@ModelAttribute("estudiante") Estudiante estudiante, RedirectAttributes redirectAttributes) {
        try {
            estudianteServicio.save(estudiante);
            redirectAttributes.addFlashAttribute("mensaje", "Estudiante creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el estudiante: " + e.getMessage());
        }
        return "redirect:/views/estudiantes/";
    }
    
    @GetMapping("/editar/{id}")
    public String editarEstudiante(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Estudiante estudiante = estudianteServicio.findById(id);
        if (estudiante == null) {
            redirectAttributes.addFlashAttribute("error", "Estudiante no encontrado");
            return "redirect:/views/estudiantes/";
        }
        model.addAttribute("estudiante", estudiante);
        return "/views/estudiantes/editar";
    }
    
    @PostMapping("/actualizar")
    public String actualizarEstudiante(@ModelAttribute("estudiante") Estudiante estudiante, RedirectAttributes redirectAttributes) {
        try {
            estudianteServicio.update(estudiante);
            redirectAttributes.addFlashAttribute("mensaje", "Estudiante actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el estudiante: " + e.getMessage());
        }
        return "redirect:/views/estudiantes/";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarEstudiante(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            estudianteServicio.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Estudiante eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el estudiante: " + e.getMessage());
        }
        return "redirect:/views/estudiantes/";
    }
}

