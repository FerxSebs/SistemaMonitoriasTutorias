package uts.edu.java.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uts.edu.java.proyecto.modelo.Tutoria;
import java.util.List;

@Repository
public interface TutoriaRepositorio extends JpaRepository<Tutoria, Integer> {
    
    @Query("SELECT DISTINCT t FROM Tutoria t LEFT JOIN FETCH t.estudiante LEFT JOIN FETCH t.tutor LEFT JOIN FETCH t.materia WHERE t.estudiante.idEstudiante = ?1")
    List<Tutoria> findByIdEstudiante(Integer idEstudiante);
    
    @Query("SELECT DISTINCT t FROM Tutoria t LEFT JOIN FETCH t.estudiante LEFT JOIN FETCH t.tutor LEFT JOIN FETCH t.materia WHERE t.tutor.idTutor = ?1")
    List<Tutoria> findByIdTutor(Integer idTutor);
}