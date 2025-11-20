package uts.edu.java.proyecto.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Configuración adicional de headers de seguridad HTTP
 * Complementa la configuración de Spring Security
 */
@Configuration
public class SecurityHeadersConfig {
    
    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityHeadersFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
    
    /**
     * Filtro para agregar headers de seguridad HTTP adicionales
     */
    public static class SecurityHeadersFilter extends OncePerRequestFilter {
        
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                        FilterChain filterChain) throws ServletException, IOException {
            
            // Content Security Policy (CSP) - Ajustar según necesidades
            response.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                "img-src 'self' data: https:; " +
                "font-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                "connect-src 'self';");
            
            // Referrer Policy
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
            // Permissions Policy (anteriormente Feature Policy)
            response.setHeader("Permissions-Policy", 
                "geolocation=(), microphone=(), camera=()");
            
            filterChain.doFilter(request, response);
        }
    }
}

