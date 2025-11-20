package uts.edu.java.proyecto.controlador;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Profesor;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.modelo.Tutoria;
import uts.edu.java.proyecto.repositorio.ProfesorRepositorio;
import uts.edu.java.proyecto.repositorio.TutorRepositorio;
import uts.edu.java.proyecto.repositorio.TutoriaRepositorio;
import uts.edu.java.proyecto.servicio.EstudianteServicio;
import uts.edu.java.proyecto.servicio.MateriaServicio;
import uts.edu.java.proyecto.servicio.TutorServicio;
import uts.edu.java.proyecto.servicio.TutoriaServicio;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.beans.PropertyEditorSupport;

@Controller
@RequestMapping("/tutor")
public class TutorControlador {
    
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
    TutorRepositorio tutorRepositorio;
    
    @Autowired
    ProfesorRepositorio profesorRepositorio;
    
    @Autowired
    TutoriaRepositorio tutoriaRepositorio;
    
    @Autowired
    TutoriaServicio tutoriaServicio;
    
    @Autowired
    EstudianteServicio estudianteServicio;
    
    @Autowired
    MateriaServicio materiaServicio;
    
    @Autowired
    TutorServicio tutorServicio;
    
    private Tutor getTutorActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Profesor profesor = profesorRepositorio.findByCorreo(username).orElse(null);
        if (profesor != null) {
            return tutorRepositorio.findById(profesor.getIdProfesor()).orElse(null);
        }
        return null;
    }
    
    @GetMapping("/tutorias/")
    public String tutoriasAsignadas(Model model) {
        Tutor tutor = getTutorActual();
        if (tutor == null) {
            return "redirect:/login";
        }
        List<Tutoria> tutorias = tutoriaRepositorio.findByIdTutor(tutor.getIdTutor());
        model.addAttribute("tutorias", tutorias);
        return "/views/tutorias/lista";
    }
    
    @GetMapping("/tutorias/editar/{id}")
    public String editarTutoria(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Tutor tutor = getTutorActual();
        if (tutor == null) {
            return "redirect:/login";
        }
        
        Tutoria tutoria = tutoriaServicio.listarId(id);
        if (tutoria == null) {
            redirectAttributes.addFlashAttribute("error", "Tutoría no encontrada");
            return "redirect:/tutor/tutorias/";
        }
        
        // Verificar que la tutoría pertenezca al tutor actual
        if (tutoria.getTutor() == null || !tutoria.getTutor().getIdTutor().equals(tutor.getIdTutor())) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta tutoría");
            return "redirect:/tutor/tutorias/";
        }
        
        List<Estudiante> listaEstudiantes = estudianteServicio.getEstudiantes();
        List<Materia> listaMaterias = materiaServicio.getMaterias();
        
        // Cargar los IDs para el formulario
        if (tutoria.getEstudiante() != null) {
            tutoria.setIdEstudiante(tutoria.getEstudiante().getIdEstudiante());
        }
        if (tutoria.getTutor() != null) {
            tutoria.setIdTutor(tutoria.getTutor().getIdTutor());
        }
        if (tutoria.getMateria() != null) {
            tutoria.setIdMateria(tutoria.getMateria().getIdMateria());
        }
        
        model.addAttribute("estudiantes", listaEstudiantes);
        model.addAttribute("materias", listaMaterias);
        model.addAttribute("tutoria", tutoria);
        model.addAttribute("esTutor", true);
        // Para el tutor, solo mostrar el tutor actual (no puede cambiarlo)
        List<Tutor> listaTutores = java.util.Collections.singletonList(tutor);
        model.addAttribute("tutores", listaTutores);
        return "/views/tutorias/editar";
    }
    
    @PostMapping("/tutorias/actualizar")
    public String actualizarTutoria(@ModelAttribute("tutoria") Tutoria tutoria, RedirectAttributes redirectAttributes) {
        try {
            Tutor tutor = getTutorActual();
            if (tutor == null) {
                redirectAttributes.addFlashAttribute("error", "Tutor no encontrado");
                return "redirect:/login";
            }
            
            // Verificar que la tutoría pertenezca al tutor actual
            Tutoria tutoriaExistente = tutoriaServicio.listarId(tutoria.getIdTutoria());
            if (tutoriaExistente == null || tutoriaExistente.getTutor() == null || 
                !tutoriaExistente.getTutor().getIdTutor().equals(tutor.getIdTutor())) {
                redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta tutoría");
                return "redirect:/tutor/tutorias/";
            }
            
            // Los tutores solo pueden cambiar el estado, mantener todos los demás campos de la tutoría existente
            tutoriaExistente.setEstado(tutoria.getEstado());
            
            // Asegurar que el ID del tutor no cambie
            tutoriaExistente.setIdTutor(tutor.getIdTutor());
            
            Tutoria tutoriaActualizada = tutoriaServicio.update(tutoriaExistente);
            if (tutoriaActualizada != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Estado de la tutoría actualizado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al actualizar la tutoría");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la tutoría: " + e.getMessage());
        }
        return "redirect:/tutor/tutorias/";
    }
    
    @PostMapping("/tutorias/actualizar-estado/{id}")
    public String actualizarEstadoTutoria(@PathVariable Integer id, @RequestParam String estado, RedirectAttributes redirectAttributes) {
        try {
            Tutoria tutoria = tutoriaServicio.listarId(id);
            if (tutoria != null) {
                tutoria.setEstado(estado);
                tutoriaServicio.update(tutoria);
                redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tutoría no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el estado: " + e.getMessage());
        }
        return "redirect:/tutor/tutorias/";
    }
}
