package uts.edu.java.proyecto.controlador;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/error")
public class ErrorControlador {
    
    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mensaje = "No tiene permiso para realizar esta acción.";
        String titulo = "Acceso Denegado";
        
        if (auth != null && auth.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            Collection<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            
            // Determinar el rol principal
            String rolPrincipal = "Usuario";
            if (roles.contains("ROLE_ADMIN")) {
                rolPrincipal = "Administrador";
            } else if (roles.contains("ROLE_ESTUDIANTE")) {
                rolPrincipal = "Estudiante";
            } else if (roles.contains("ROLE_MONITOR")) {
                rolPrincipal = "Monitor";
            } else if (roles.contains("ROLE_PROFESOR")) {
                rolPrincipal = "Profesor";
            } else if (roles.contains("ROLE_TUTOR")) {
                rolPrincipal = "Tutor";
            }
            
            model.addAttribute("rolPrincipal", rolPrincipal);
            model.addAttribute("username", auth.getName());
        }
        
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("titulo", titulo);
        return "/error/acceso-denegado";
    }
}

