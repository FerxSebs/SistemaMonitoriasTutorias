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
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.repositorio.ProfesorRepositorio;
import uts.edu.java.proyecto.servicio.TutorServicio;

@Controller
@RequestMapping("/views/tutores")
public class TutorAdminControlador {
    
    @Autowired
    TutorServicio tutorServicio;
    
    @Autowired
    ProfesorRepositorio profesorRepositorio;
    
    @GetMapping("/")
    public String verTutores(Model model) {
        List<Tutor> tutores = tutorServicio.getTutores();
        model.addAttribute("tutores", tutores);
        return "/views/tutores/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevoTutor(Model model) {
        Tutor tutor = new Tutor();
        List<Profesor> profesores = profesorRepositorio.findAll();
        model.addAttribute("tutor", tutor);
        model.addAttribute("profesores", profesores);
        return "/views/tutores/nuevo";
    }
    
    @PostMapping("/guardar")
    public String guardarTutor(@ModelAttribute("tutor") Tutor tutor, RedirectAttributes redirectAttributes) {
        try {
            tutorServicio.save(tutor);
            redirectAttributes.addFlashAttribute("mensaje", "Tutor creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el tutor: " + e.getMessage());
        }
        return "redirect:/views/tutores/";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarTutor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            tutorServicio.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Tutor eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el tutor: " + e.getMessage());
        }
        return "redirect:/views/tutores/";
    }
}

