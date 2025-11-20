package uts.edu.java.proyecto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import uts.edu.java.proyecto.servicio.UsuarioServicio;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private final UsuarioServicio usuarioServicio;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public SecurityConfig(@Lazy UsuarioServicio usuarioServicio, PasswordEncoder passwordEncoder) {
        this.usuarioServicio = usuarioServicio;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioServicio);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeRequests()
                // Rutas públicas
                .antMatchers("/", "/index", "/contacto", "/login", "/registro", "/css/**", "/js/**", "/img/**", "/error/**").permitAll()
                
                // Ruta de error personalizada (acceso autenticado)
                .antMatchers("/error/acceso-denegado").authenticated()
                
                // Rutas de administrador (acceso completo)
                .antMatchers("/views/estudiantes/**", "/views/profesores/**", "/views/tutores/**", 
                            "/views/monitores/**").hasRole("ADMIN")
                
                // Rutas de facturas (admin y monitor pueden ver)
                .antMatchers("/views/facturas/**").hasAnyRole("ADMIN", "MONITOR")
                
                // Rutas de materias (admin y profesor pueden ver)
                .antMatchers("/views/materias/**").hasAnyRole("ADMIN", "PROFESOR")
                
                // Rutas de estudiantes
                .antMatchers("/estudiante/**").hasAnyRole("ESTUDIANTE", "ADMIN")
                
                // Rutas de monitores
                .antMatchers("/monitor/**").hasAnyRole("MONITOR", "ADMIN")
                
                // Rutas de profesores
                .antMatchers("/profesor/**").hasAnyRole("PROFESOR", "ADMIN")
                
                // Rutas de tutores
                .antMatchers("/tutor/**").hasAnyRole("TUTOR", "ADMIN")
                
                // Rutas generales (acceso según rol)
                .antMatchers("/views/tutorias/**").hasAnyRole("ESTUDIANTE", "TUTOR", "MONITOR", "ADMIN")
                .antMatchers("/views/monitorias/**").hasAnyRole("ESTUDIANTE", "MONITOR", "ADMIN")
                .antMatchers("/views/materias/**").hasAnyRole("PROFESOR", "ADMIN")
                
                // Home y rutas autenticadas
                .antMatchers("/home").authenticated()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
            .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            .and()
            .exceptionHandling()
                .accessDeniedPage("/error/403")
            .and()
            // Configuración de CSRF Protection
            .csrf()
                .csrfTokenRepository(csrfTokenRepository())
                // Permitir endpoints públicos sin CSRF (solo para desarrollo)
                .ignoringAntMatchers("/api/public/**")
            .and()
            // Headers de Seguridad HTTP (configuración básica)
            .headers()
                .contentTypeOptions().and()
                .frameOptions().deny();
        
        return http.build();
    }
    
    /**
     * Configuración del repositorio de tokens CSRF
     * Usa cookies HTTP-only para mayor seguridad
     */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieHttpOnly(true);
        repository.setSecure(false); // Cambiar a true en producción con HTTPS
        return repository;
    }
}

