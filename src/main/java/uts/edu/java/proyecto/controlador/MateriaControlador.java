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

import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.servicio.MateriaServicio;
import uts.edu.java.proyecto.servicio.UsuarioActualServicio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("views/materias")
public class MateriaControlador {
    
    @Autowired
    MateriaServicio materiaServicio;
    
    @Autowired
    UsuarioActualServicio usuarioActualServicio;
    
    @GetMapping("/")
    public String verMaterias(Model model) {
        List<Materia> listaMaterias;
        
        // Filtrar según el rol del usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isProfesor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESOR"));
        
        if (isAdmin || isProfesor) {
            // Admin y Profesor ven todas las materias
            listaMaterias = materiaServicio.getMaterias();
        } else {
            // Otros roles no deberían acceder aquí
            listaMaterias = materiaServicio.getMaterias();
        }
        
        model.addAttribute("materias", listaMaterias);
        return "/views/materias/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevaMateria(Model model) {
        Materia materia = new Materia();
        model.addAttribute("materia", materia);
        return "/views/materias/nuevo";
    }
    
    @PostMapping("/guardar")
    public String guardarMateria(@ModelAttribute("materia") Materia materia, RedirectAttributes redirectAttributes) {
        try {
            materiaServicio.save(materia);
            redirectAttributes.addFlashAttribute("mensaje", "Materia creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la materia: " + e.getMessage());
        }
        return "redirect:/views/materias/";
    }
    
    @GetMapping("/editar/{id}")
    public String editarMateria(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Materia materia = materiaServicio.findById(id);
        if (materia == null) {
            redirectAttributes.addFlashAttribute("error", "Materia no encontrada");
            return "redirect:/views/materias/";
        }
        model.addAttribute("materia", materia);
        return "/views/materias/editar";
    }
    
    @PostMapping("/actualizar")
    public String actualizarMateria(@ModelAttribute("materia") Materia materia, RedirectAttributes redirectAttributes) {
        try {
            materiaServicio.update(materia);
            redirectAttributes.addFlashAttribute("mensaje", "Materia actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la materia: " + e.getMessage());
        }
        return "redirect:/views/materias/";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarMateria(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            materiaServicio.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Materia eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la materia: " + e.getMessage());
        }
        return "redirect:/views/materias/";
    }
}

