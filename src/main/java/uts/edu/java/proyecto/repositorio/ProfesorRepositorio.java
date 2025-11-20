package uts.edu.java.proyecto.repositorio;

import uts.edu.java.proyecto.modelo.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfesorRepositorio extends JpaRepository<Profesor, Integer> {
    Optional<Profesor> findByCorreo(String correo);
    Optional<Profesor> findByIdProfesor(Integer idProfesor);
}
