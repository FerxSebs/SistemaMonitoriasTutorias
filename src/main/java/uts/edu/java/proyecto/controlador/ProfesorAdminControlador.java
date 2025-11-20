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

import uts.edu.java.proyecto.modelo.Profesor;
import uts.edu.java.proyecto.servicio.ProfesorServicio;

@Controller
@RequestMapping("/views/profesores")
public class ProfesorAdminControlador {
    
    @Autowired
    ProfesorServicio profesorServicio;
    
    @GetMapping("/")
    public String verProfesores(Model model) {
        List<Profesor> profesores = profesorServicio.getProfesores();
        model.addAttribute("profesores", profesores);
        return "/views/profesores/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevoProfesor(Model model) {
        Profesor profesor = new Profesor();
        model.addAttribute("profesor", profesor);
        return "/views/profesores/nuevo";
    }
    
    @PostMapping("/guardar")
    public String guardarProfesor(@ModelAttribute("profesor") Profesor profesor, RedirectAttributes redirectAttributes) {
        try {
            profesorServicio.save(profesor);
            redirectAttributes.addFlashAttribute("mensaje", "Profesor creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el profesor: " + e.getMessage());
        }
        return "redirect:/views/profesores/";
    }
    
    @GetMapping("/editar/{id}")
    public String editarProfesor(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Profesor profesor = profesorServicio.findById(id);
        if (profesor == null) {
            redirectAttributes.addFlashAttribute("error", "Profesor no encontrado");
            return "redirect:/views/profesores/";
        }
        model.addAttribute("profesor", profesor);
        return "/views/profesores/editar";
    }
    
    @PostMapping("/actualizar")
    public String actualizarProfesor(@ModelAttribute("profesor") Profesor profesor, RedirectAttributes redirectAttributes) {
        try {
            profesorServicio.update(profesor);
            redirectAttributes.addFlashAttribute("mensaje", "Profesor actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el profesor: " + e.getMessage());
        }
        return "redirect:/views/profesores/";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarProfesor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            profesorServicio.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Profesor eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el profesor: " + e.getMessage());
        }
        return "redirect:/views/profesores/";
    }
}

