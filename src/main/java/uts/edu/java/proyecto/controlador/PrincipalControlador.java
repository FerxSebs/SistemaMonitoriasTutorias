package uts.edu.java.proyecto.controlador;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.stream.Collectors;

@Controller
public class PrincipalControlador {

    @GetMapping("/")
    public String paginaPrincipal() {
        return "index";
    }
    
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            Collection<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            model.addAttribute("roles", roles);
            model.addAttribute("username", auth.getName());
        }
        return "home";
    }
    
    @GetMapping("/estudiantes")
    public String estudiantes() {
        return "redirect:/views/estudiantes/";
    }
    
    @GetMapping("/profesores")
    public String profesores() {
        return "redirect:/views/profesores/";
    }
    
    @GetMapping("/tutorias")
    public String tutorias() {
        return "redirect:/views/tutorias/";
    }
    
    @GetMapping("/monitorias")
    public String monitorias() {
        return "redirect:/views/monitorias/";
    }
    
    @GetMapping("/tutores")
    public String tutores() {
        return "redirect:/views/tutores/";
    }
    
    @GetMapping("/monitores")
    public String monitores() {
        return "redirect:/views/monitores/";
    }
    
    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }
}
