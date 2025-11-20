package uts.edu.java.proyecto.servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import uts.edu.java.proyecto.modelo.Factura;

public interface IFacturaServicio {
    
    List<Factura> getFacturas();
    
    Factura findById(Integer id);
    
    List<Factura> findByMonitor(Integer idMonitor);
    
    List<Factura> findByMonitorAndFechaBetween(Integer idMonitor, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Factura> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Map<Integer, Map<String, Object>> getTotalesPorMonitor(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

