package uts.edu.java.proyecto.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Profesor;
import uts.edu.java.proyecto.repositorio.ProfesorRepositorio;
import uts.edu.java.proyecto.servicio.MateriaServicio;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profesor")
public class ProfesorControlador {
    
    @Autowired
    ProfesorRepositorio profesorRepositorio;
    
    @Autowired
    MateriaServicio materiaServicio;
    
    private Profesor getProfesorActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return profesorRepositorio.findByCorreo(username).orElse(null);
    }
    
    @GetMapping("/materias/")
    public String materiasAsignadas(Model model) {
        Profesor profesor = getProfesorActual();
        if (profesor == null) {
            return "redirect:/login";
        }
        
        // Profesor puede ver todas las materias
        List<Materia> materias = materiaServicio.getMaterias();
        model.addAttribute("materias", materias);
        model.addAttribute("profesor", profesor);
        return "/views/materias/lista";
    }
    
    @GetMapping("/materias/editar/{id}")
    public String editarMateria(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Profesor profesor = getProfesorActual();
        if (profesor == null) {
            return "redirect:/login";
        }
        
        Materia materia = materiaServicio.findById(id);
        if (materia == null) {
            redirectAttributes.addFlashAttribute("error", "Materia no encontrada");
            return "redirect:/profesor/materias/";
        }
        model.addAttribute("materia", materia);
        return "/views/materias/editar";
    }
    
    @PostMapping("/materias/actualizar")
    public String actualizarMateria(@ModelAttribute("materia") Materia materia, RedirectAttributes redirectAttributes) {
        try {
            Profesor profesor = getProfesorActual();
            if (profesor == null) {
                redirectAttributes.addFlashAttribute("error", "Profesor no encontrado");
                return "redirect:/login";
            }
            
            materiaServicio.update(materia);
            redirectAttributes.addFlashAttribute("mensaje", "Materia actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la materia: " + e.getMessage());
        }
        return "redirect:/profesor/materias/";
    }
}
