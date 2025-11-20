package uts.edu.java.proyecto.repositorio;

import uts.edu.java.proyecto.modelo.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstudianteRepositorio extends JpaRepository<Estudiante, Integer> {
    Optional<Estudiante> findByCorreo(String correo);
    Optional<Estudiante> findByIdEstudiante(Integer idEstudiante);
}
