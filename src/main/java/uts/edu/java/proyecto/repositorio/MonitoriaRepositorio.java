package uts.edu.java.proyecto.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uts.edu.java.proyecto.modelo.Monitoria;

@Repository
public interface MonitoriaRepositorio extends JpaRepository<Monitoria, Integer> {
    
    @Query("SELECT DISTINCT m FROM Monitoria m LEFT JOIN FETCH m.estudiante LEFT JOIN FETCH m.monitor LEFT JOIN FETCH m.materia")
    @Override
    List<Monitoria> findAll();
    
    @Query("SELECT DISTINCT m FROM Monitoria m LEFT JOIN FETCH m.estudiante LEFT JOIN FETCH m.monitor LEFT JOIN FETCH m.materia WHERE m.estado = ?1")
    List<Monitoria> findByEstado(String estado);
    
    @Query("SELECT DISTINCT m FROM Monitoria m LEFT JOIN FETCH m.estudiante LEFT JOIN FETCH m.monitor LEFT JOIN FETCH m.materia WHERE m.idEstudiante = ?1")
    List<Monitoria> findByIdEstudiante(Integer idEstudiante);
    
    @Query("SELECT DISTINCT m FROM Monitoria m LEFT JOIN FETCH m.estudiante LEFT JOIN FETCH m.monitor LEFT JOIN FETCH m.materia WHERE m.idMonitor = ?1")
    List<Monitoria> findByIdMonitor(Integer idMonitor);
    
    @Query("SELECT DISTINCT m FROM Monitoria m LEFT JOIN FETCH m.estudiante LEFT JOIN FETCH m.monitor LEFT JOIN FETCH m.materia WHERE m.idMateria = ?1")
    List<Monitoria> findByIdMateria(Integer idMateria);
}
