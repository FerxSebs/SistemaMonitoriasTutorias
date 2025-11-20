package uts.edu.java.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uts.edu.java.proyecto.modelo.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}

