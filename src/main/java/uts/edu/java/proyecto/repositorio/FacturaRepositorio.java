package uts.edu.java.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uts.edu.java.proyecto.modelo.Factura;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepositorio extends JpaRepository<Factura, Integer> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    Optional<Factura> findByMonitoriaIdMonitoria(Integer idMonitoria);
    
    @Query("SELECT f FROM Factura f JOIN FETCH f.monitoria m JOIN FETCH m.monitor mon WHERE mon.idMonitor = ?1 AND m.fecha BETWEEN ?2 AND ?3")
    List<Factura> findByMonitorAndFechaBetween(Integer idMonitor, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT DISTINCT f FROM Factura f JOIN FETCH f.monitoria m JOIN FETCH m.monitor mon JOIN FETCH mon.estudiante est JOIN FETCH m.estudiante estudiante JOIN FETCH m.materia mat WHERE mon.idMonitor = ?1")
    List<Factura> findByMonitor(Integer idMonitor);
    
    @Query("SELECT DISTINCT f FROM Factura f JOIN FETCH f.monitoria m JOIN FETCH m.monitor mon JOIN FETCH mon.estudiante est JOIN FETCH m.estudiante estudiante JOIN FETCH m.materia mat WHERE m.fecha BETWEEN ?1 AND ?2")
    List<Factura> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

