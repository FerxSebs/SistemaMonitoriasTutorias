package uts.edu.java.proyecto.servicio;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uts.edu.java.proyecto.modelo.Factura;
import uts.edu.java.proyecto.repositorio.FacturaRepositorio;

@Service
@Transactional
public class FacturaServicio implements IFacturaServicio {
    
    @Autowired
    FacturaRepositorio facturaRepositorio;
    
    @Override
    public List<Factura> getFacturas() {
        return facturaRepositorio.findAll();
    }
    
    @Override
    public Factura findById(Integer id) {
        // Usar una consulta con JOIN FETCH para cargar todas las relaciones
        return facturaRepositorio.findById(id)
            .map(factura -> {
                // Forzar la carga de relaciones si es necesario
                if (factura.getMonitoria() != null) {
                    factura.getMonitoria().getMonitor();
                    factura.getMonitoria().getEstudiante();
                    factura.getMonitoria().getMateria();
                }
                return factura;
            })
            .orElse(null);
    }
    
    @Override
    public List<Factura> findByMonitor(Integer idMonitor) {
        return facturaRepositorio.findByMonitor(idMonitor);
    }
    
    @Override
    public List<Factura> findByMonitorAndFechaBetween(Integer idMonitor, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepositorio.findByMonitorAndFechaBetween(idMonitor, fechaInicio, fechaFin);
    }
    
    @Override
    public List<Factura> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepositorio.findByFechaBetween(fechaInicio, fechaFin);
    }
    
    @Override
    public Map<Integer, Map<String, Object>> getTotalesPorMonitor(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Map<Integer, Map<String, Object>> totalesPorMonitor = new HashMap<>();
        
        // Obtener solo las facturas en el rango de fechas (más eficiente)
        List<Factura> facturasEnRango = facturaRepositorio.findByFechaBetween(fechaInicio, fechaFin);
        
        for (Factura factura : facturasEnRango) {
            if (factura.getMonitoria() != null && 
                factura.getMonitoria().getMonitor() != null) {
                
                Integer idMonitor = factura.getMonitoria().getMonitor().getIdMonitor();
                
                totalesPorMonitor.putIfAbsent(idMonitor, new HashMap<>());
                Map<String, Object> totales = totalesPorMonitor.get(idMonitor);
                
                // Inicializar valores si es la primera factura del monitor
                if (!totales.containsKey("cantidad")) {
                    totales.put("cantidad", 0);
                    totales.put("subtotal", 0.0);
                    totales.put("iva", 0.0);
                    totales.put("total", 0.0);
                    totales.put("horas", 0.0);
                    totales.put("nombreMonitor", factura.getMonitoria().getMonitor().getEstudiante() != null ? 
                        factura.getMonitoria().getMonitor().getEstudiante().getNombreCompleto() : "N/A");
                }
                
                // Acumular valores
                totales.put("cantidad", (Integer) totales.get("cantidad") + 1);
                totales.put("subtotal", (Double) totales.get("subtotal") + (factura.getSubtotal() != null ? factura.getSubtotal() : 0.0));
                totales.put("iva", (Double) totales.get("iva") + (factura.getIva() != null ? factura.getIva() : 0.0));
                totales.put("total", (Double) totales.get("total") + (factura.getTotal() != null ? factura.getTotal() : 0.0));
                totales.put("horas", (Double) totales.get("horas") + (factura.getHoras() != null ? factura.getHoras() : 0.0));
            }
        }
        
        return totalesPorMonitor;
    }
}

