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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.modelo.Tutoria;
import uts.edu.java.proyecto.repositorio.TutoriaRepositorio;
import uts.edu.java.proyecto.servicio.EstudianteServicio;
import uts.edu.java.proyecto.servicio.MateriaServicio;
import uts.edu.java.proyecto.servicio.TutorServicio;
import uts.edu.java.proyecto.servicio.TutoriaServicio;
import uts.edu.java.proyecto.servicio.UsuarioActualServicio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("views/tutorias")
public class TutoriaControlador {
    
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
    TutoriaServicio tutoriaServicio;
    @Autowired
    TutorServicio tutorServicio;
    @Autowired
    EstudianteServicio estudianteServicio;
    @Autowired
    MateriaServicio materiaServicio;
    @Autowired
    UsuarioActualServicio usuarioActualServicio;
    @Autowired
    TutoriaRepositorio tutoriaRepositorio;
    
    // Direccionar a la página de gestión de las tutorías
    @RequestMapping("/")
    public String verTutorias(Model model) {
        List<Tutoria> listaTutorias;
        
        // Filtrar según el rol del usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isTutor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));
        boolean isEstudiante = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
        
        if (isAdmin) {
            // Admin ve todas las tutorías
            listaTutorias = tutoriaServicio.listar();
        } else if (isTutor) {
            // Tutor ve solo sus tutorías asignadas
            Integer idTutor = usuarioActualServicio.getIdTutorActual();
            if (idTutor != null) {
                listaTutorias = tutoriaRepositorio.findByIdTutor(idTutor);
            } else {
                listaTutorias = java.util.Collections.emptyList();
            }
        } else if (isEstudiante) {
            // Estudiante ve solo sus tutorías
            Integer idEstudiante = usuarioActualServicio.getIdEstudianteActual();
            if (idEstudiante != null) {
                listaTutorias = tutoriaRepositorio.findByIdEstudiante(idEstudiante);
            } else {
                listaTutorias = java.util.Collections.emptyList();
            }
        } else {
            listaTutorias = java.util.Collections.emptyList();
        }
        
        model.addAttribute("tutorias", listaTutorias);
        return "/views/tutorias/lista";
    }
    
    // Direccionar al formulario para crear la Tutoría
    @GetMapping("/nuevo")
    public String mostrarPaginaNuevaTutoria(Model model, RedirectAttributes redirectAttributes) {
        // Verificar permisos según el rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isTutor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));
        
        // Los tutores no pueden crear tutorías
        if (isTutor && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Los tutores no pueden crear tutorías. Solo pueden gestionar las tutorías que les han sido asignadas.");
            return "redirect:/views/tutorias/";
        }
        
        Tutoria tutoria = new Tutoria();
        List<Tutor> listaTutores = tutorServicio.getTutores();
        List<Estudiante> listaEstudiantes = estudianteServicio.getEstudiantes();
        List<Materia> listaMaterias = materiaServicio.getMaterias();
        
        model.addAttribute("tutores", listaTutores);
        model.addAttribute("estudiantes", listaEstudiantes);
        model.addAttribute("materias", listaMaterias);
        model.addAttribute("tutoria", tutoria);
        return "/views/tutorias/nuevo";
    }
    
    @PostMapping("/guardar")
    public String guardarTutoria(@ModelAttribute("tutoria") Tutoria tutoria, RedirectAttributes redirectAttributes) {
        // Verificar permisos según el rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isTutor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));
        
        // Los tutores no pueden crear tutorías
        if (isTutor && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Los tutores no pueden crear tutorías. Solo pueden gestionar las tutorías que les han sido asignadas.");
            return "redirect:/views/tutorias/";
        }
        
        try {
            tutoria.setIdTutoria(null); // Asegurar que sea nuevo
            tutoriaServicio.save(tutoria);
            redirectAttributes.addFlashAttribute("mensaje", "Tutoría creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la tutoría: " + e.getMessage());
        }
        return "redirect:/views/tutorias/";
    }
    
    @PostMapping("/actualizar")
    public String actualizarTutoria(@ModelAttribute("tutoria") Tutoria tutoria, RedirectAttributes redirectAttributes) {
        try {
            // Verificar permisos según el rol
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isTutor = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));
            boolean isEstudiante = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
            
            // Los estudiantes no pueden actualizar tutorías
            if (isEstudiante && !isAdmin && !isTutor) {
                redirectAttributes.addFlashAttribute("error", "Los estudiantes no pueden modificar tutorías");
                return "redirect:/error/acceso-denegado";
            }
            
            if (isTutor && !isAdmin) {
                // Si es tutor, verificar que la tutoría pertenezca a él
                Tutoria tutoriaExistente = tutoriaServicio.listarId(tutoria.getIdTutoria());
                if (tutoriaExistente == null) {
                    redirectAttributes.addFlashAttribute("error", "Tutoría no encontrada");
                    return "redirect:/error/acceso-denegado";
                }
                
                Integer idTutorActual = usuarioActualServicio.getIdTutorActual();
                if (idTutorActual == null) {
                    redirectAttributes.addFlashAttribute("error", "No se pudo identificar el tutor actual. Asegúrese de que su perfil de tutor esté correctamente configurado.");
                    return "redirect:/error/acceso-denegado";
                }
                
                // Obtener el ID del tutor de la tutoría (puede venir de la relación o del campo transiente)
                Integer idTutorTutoria = null;
                if (tutoriaExistente.getTutor() != null) {
                    idTutorTutoria = tutoriaExistente.getTutor().getIdTutor();
                } else if (tutoriaExistente.getIdTutor() != null) {
                    idTutorTutoria = tutoriaExistente.getIdTutor();
                }
                
                if (idTutorTutoria == null || !idTutorTutoria.equals(idTutorActual)) {
                    redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta tutoría. Solo puede editar las tutorías asignadas a usted.");
                    return "redirect:/error/acceso-denegado";
                }
                
                // Los tutores solo pueden cambiar el estado, mantener todos los demás campos de la tutoría existente
                tutoria.setIdTutor(idTutorActual);
                tutoria.setIdEstudiante(tutoriaExistente.getEstudiante() != null ? tutoriaExistente.getEstudiante().getIdEstudiante() : tutoriaExistente.getIdEstudiante());
                tutoria.setIdMateria(tutoriaExistente.getMateria() != null ? tutoriaExistente.getMateria().getIdMateria() : tutoriaExistente.getIdMateria());
                tutoria.setFecha(tutoriaExistente.getFecha());
                tutoria.setDuracionHoras(tutoriaExistente.getDuracionHoras());
                tutoria.setObservaciones(tutoriaExistente.getObservaciones());
                // Solo el estado se actualiza desde el formulario
            }
            
            Tutoria tutoriaActualizada = tutoriaServicio.update(tutoria);
            if (tutoriaActualizada != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Tutoría actualizada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al actualizar la tutoría");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la tutoría: " + e.getMessage());
        }
        return "redirect:/views/tutorias/";
    }
    
    // Direccionar al formulario para editar la Tutoría
    @GetMapping("/editar/{idTutoria}")
    public String editarTutoria(@PathVariable int idTutoria, Model model, RedirectAttributes redirectAttributes) {
        // Verificar permisos según el rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isTutor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));
        boolean isEstudiante = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
        
        // Los estudiantes no pueden editar tutorías
        if (isEstudiante && !isAdmin && !isTutor) {
            redirectAttributes.addFlashAttribute("error", "Los estudiantes no pueden modificar tutorías");
            return "redirect:/error/acceso-denegado";
        }
        
        Tutoria tutoria = tutoriaServicio.listarId(idTutoria);
        
        if (tutoria == null) {
            redirectAttributes.addFlashAttribute("error", "Tutoría no encontrada");
            return "redirect:/views/tutorias/";
        }
        
        // Si es tutor, verificar que la tutoría pertenezca a él
        if (isTutor && !isAdmin) {
            Integer idTutorActual = usuarioActualServicio.getIdTutorActual();
            if (idTutorActual == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo identificar el tutor actual. Asegúrese de que su perfil de tutor esté correctamente configurado.");
                return "redirect:/error/acceso-denegado";
            }
            
            // Obtener el ID del tutor de la tutoría (puede venir de la relación o del campo transiente)
            Integer idTutorTutoria = null;
            if (tutoria.getTutor() != null) {
                idTutorTutoria = tutoria.getTutor().getIdTutor();
            } else if (tutoria.getIdTutor() != null) {
                idTutorTutoria = tutoria.getIdTutor();
            }
            
            if (idTutorTutoria == null || !idTutorTutoria.equals(idTutorActual)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta tutoría. Solo puede editar las tutorías asignadas a usted.");
                return "redirect:/error/acceso-denegado";
            }
            
            // Para el tutor, solo mostrar el tutor actual (no puede cambiarlo)
            Tutor tutorActual = tutorServicio.findById(idTutorActual);
            if (tutorActual == null) {
                redirectAttributes.addFlashAttribute("error", "Tutor no encontrado");
                return "redirect:/error/acceso-denegado";
            }
            List<Tutor> listaTutores = java.util.Collections.singletonList(tutorActual);
            model.addAttribute("tutores", listaTutores);
            model.addAttribute("esTutor", true);
        } else {
            // Admin puede ver todos los tutores
            List<Tutor> listaTutores = tutorServicio.getTutores();
            model.addAttribute("tutores", listaTutores);
            model.addAttribute("esTutor", false);
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
        return "/views/tutorias/editar";
    }
    
    // Eliminar una tutoría
    @GetMapping("/eliminar/{idTutoria}")
    public String deleteTutoria(@PathVariable int idTutoria, RedirectAttributes redirectAttributes) {
        try {
            // Verificar permisos según el rol
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isTutor = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));
            boolean isEstudiante = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
            
            // Los estudiantes no pueden eliminar tutorías
            if (isEstudiante && !isAdmin && !isTutor) {
                redirectAttributes.addFlashAttribute("error", "Los estudiantes no pueden eliminar tutorías");
                return "redirect:/error/acceso-denegado";
            }
            
            Tutoria tutoria = tutoriaServicio.listarId(idTutoria);
            if (tutoria == null) {
                redirectAttributes.addFlashAttribute("error", "Tutoría no encontrada");
                return "redirect:/views/tutorias/";
            }
            
            // Si es tutor, verificar que la tutoría pertenezca a él
            if (isTutor && !isAdmin) {
                Integer idTutorActual = usuarioActualServicio.getIdTutorActual();
                if (idTutorActual == null) {
                    redirectAttributes.addFlashAttribute("error", "No se pudo identificar el tutor actual. Asegúrese de que su perfil de tutor esté correctamente configurado.");
                    return "redirect:/error/acceso-denegado";
                }
                
                // Obtener el ID del tutor de la tutoría (puede venir de la relación o del campo transiente)
                Integer idTutorTutoria = null;
                if (tutoria.getTutor() != null) {
                    idTutorTutoria = tutoria.getTutor().getIdTutor();
                } else if (tutoria.getIdTutor() != null) {
                    idTutorTutoria = tutoria.getIdTutor();
                }
                
                if (idTutorTutoria == null || !idTutorTutoria.equals(idTutorActual)) {
                    redirectAttributes.addFlashAttribute("error", "No tiene permiso para eliminar esta tutoría. Solo puede eliminar las tutorías asignadas a usted.");
                    return "redirect:/error/acceso-denegado";
                }
            }
            
            tutoriaServicio.delete(idTutoria);
            redirectAttributes.addFlashAttribute("mensaje", "Tutoría eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la tutoría: " + e.getMessage());
        }
        return "redirect:/views/tutorias/";
    }
    
    // Buscar tutorías por estudiante
    @RequestMapping("/buscarPorEstudiante/{idEstudiante}")
    public String buscarPorEstudiante(@PathVariable Integer idEstudiante, Model model) {
        List<Tutoria> listaTutorias = tutoriaServicio.buscarPorEstudiante(idEstudiante);
        model.addAttribute("tutorias", listaTutorias);
        model.addAttribute("idEstudianteFiltro", idEstudiante);
        return "/views/tutorias/lista";
    }
    
    // Buscar tutorías por tutor
    @RequestMapping("/buscarPorTutor/{idTutor}")
    public String buscarPorTutor(@PathVariable Integer idTutor, Model model) {
        List<Tutoria> listaTutorias = tutoriaServicio.buscarPorTutor(idTutor);
        model.addAttribute("tutorias", listaTutorias);
        model.addAttribute("idTutorFiltro", idTutor);
        return "/views/tutorias/lista";
    }
    
    // Buscar tutorías por materia
    @RequestMapping("/buscarPorMateria/{idMateria}")
    public String buscarPorMateria(@PathVariable Integer idMateria, Model model) {
        List<Tutoria> listaTutorias = tutoriaServicio.buscarPorMateria(idMateria);
        model.addAttribute("tutorias", listaTutorias);
        model.addAttribute("idMateriaFiltro", idMateria);
        return "/views/tutorias/lista";
    }
 // Agregar al TutoriaControlador.java
    @GetMapping("/api/verificar-estudiante/{idEstudiante}")
    @ResponseBody
    public boolean verificarEstudiante(@PathVariable Integer idEstudiante) {
        Estudiante estudiante = estudianteServicio.findById(idEstudiante);
        return estudiante != null;
    }

    @GetMapping("/api/estudiante/{idEstudiante}")
    @ResponseBody
    public Estudiante obtenerEstudiante(@PathVariable Integer idEstudiante) {
        return estudianteServicio.findById(idEstudiante);
    }
}
