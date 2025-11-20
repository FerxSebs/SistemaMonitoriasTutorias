package uts.edu.java.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uts.edu.java.proyecto.modelo.Monitor;
import java.util.List;

@Repository
public interface MonitorRepositorio extends JpaRepository<Monitor, Integer> {
    
    @Query("SELECT m FROM Monitor m LEFT JOIN FETCH m.estudiante")
    List<Monitor> findAll();
}
