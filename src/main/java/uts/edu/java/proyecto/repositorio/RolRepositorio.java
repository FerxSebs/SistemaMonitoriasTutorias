package uts.edu.java.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uts.edu.java.proyecto.modelo.Rol;

import java.util.Optional;

@Repository
public interface RolRepositorio extends JpaRepository<Rol, Integer> {
    
    Optional<Rol> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
}

