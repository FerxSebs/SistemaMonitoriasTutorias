package uts.edu.java.proyecto.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.servicio.EstudianteServicio;
import uts.edu.java.proyecto.servicio.UsuarioServicio;

import javax.validation.Valid;

@Controller
public class AuthControlador {
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private EstudianteServicio estudianteServicio;
    
    @Autowired
    private EstudianteRepositorio estudianteRepositorio;
    
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "auth/login";
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        if (!model.containsAttribute("estudiante")) {
            model.addAttribute("estudiante", new Estudiante());
        }
        return "auth/registro";
    }
    
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("estudiante") Estudiante estudiante,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.estudiante", result);
            redirectAttributes.addFlashAttribute("estudiante", estudiante);
            redirectAttributes.addFlashAttribute("error", "Por favor, corrija los errores en el formulario");
            return "redirect:/registro";
        }
        
        // Validar que el ID del estudiante sea proporcionado
        if (estudiante.getIdEstudiante() == null || estudiante.getIdEstudiante() <= 0) {
            redirectAttributes.addFlashAttribute("error", "El ID de estudiante es obligatorio y debe ser un número positivo");
            redirectAttributes.addFlashAttribute("estudiante", estudiante);
            return "redirect:/registro";
        }
        
        // Validar que la contraseña sea proporcionada
        if (estudiante.getPassword() == null || estudiante.getPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria");
            redirectAttributes.addFlashAttribute("estudiante", estudiante);
            return "redirect:/registro";
        }
        
        // Validar que la contraseña tenga al menos 6 caracteres
        if (estudiante.getPassword().length() < 6) {
            redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            redirectAttributes.addFlashAttribute("estudiante", estudiante);
            return "redirect:/registro";
        }
        
        // Verificar si el ID del estudiante ya existe
        if (estudianteRepositorio.findById(estudiante.getIdEstudiante()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El ID de estudiante ya está registrado");
            redirectAttributes.addFlashAttribute("estudiante", estudiante);
            return "redirect:/registro";
        }
        
        // Verificar si el email ya existe como usuario
        if (usuarioServicio.existeEmail(estudiante.getCorreo())) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado");
            redirectAttributes.addFlashAttribute("estudiante", estudiante);
            return "redirect:/registro";
        }
        
        // Verificar si el email ya existe como estudiante
        if (estudianteRepositorio.findByCorreo(estudiante.getCorreo()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado");
            redirectAttributes.addFlashAttribute("estudiante", estudiante);
            return "redirect:/registro";
        }
        
        try {
            // Asegurar que el estudiante no sea monitor en el registro público
            estudiante.setEsMonitor(false);
            
            // Guardar el estudiante (el servicio se encarga de encriptar la contraseña)
            estudianteServicio.save(estudiante);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Registro exitoso. Por favor, inicie sesión con su correo electrónico y contraseña.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al registrar el usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("estudiante", estudiante);
            return "redirect:/registro";
        }
    }
}

