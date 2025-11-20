package uts.edu.java.proyecto.servicio;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Factura;
import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.modelo.Monitoria;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.repositorio.FacturaRepositorio;
import uts.edu.java.proyecto.repositorio.MateriaRepositorio;
import uts.edu.java.proyecto.repositorio.MonitorRepositorio;
import uts.edu.java.proyecto.repositorio.MonitoriaRepositorio;

@Service
@Transactional
public class MonitoriaServicio implements IMonitoriaServicio {
    
    @Autowired
    MonitoriaRepositorio monitoriaRepositorio;
    
    @Autowired
    EstudianteRepositorio estudianteRepositorio;
    
    @Autowired
    MonitorRepositorio monitorRepositorio;
    
    @Autowired
    MateriaRepositorio materiaRepositorio;
    
    @Autowired
    FacturaRepositorio facturaRepositorio;
    
    @Override
    public List<Monitoria> getMonitorias() {
        return monitoriaRepositorio.findAll();
    }
    
    @Override
    public Monitoria save(Monitoria monitoria) {
        // Establecer relaciones desde los IDs
        if (monitoria.getIdEstudiante() != null) {
            Estudiante estudiante = estudianteRepositorio.findById(monitoria.getIdEstudiante()).orElse(null);
            monitoria.setEstudiante(estudiante);
        }
        if (monitoria.getIdMonitor() != null) {
            Monitor monitor = monitorRepositorio.findById(monitoria.getIdMonitor()).orElse(null);
            monitoria.setMonitor(monitor);
        }
        Materia materia = null;
        if (monitoria.getIdMateria() != null) {
            materia = materiaRepositorio.findById(monitoria.getIdMateria()).orElse(null);
            monitoria.setMateria(materia);
        }
        
        // Guardar la monitoría
        Monitoria monitoriaGuardada = monitoriaRepositorio.save(monitoria);
        
        // Generar factura automáticamente
        if (materia != null && monitoria.getDuracionHoras() != null && materia.getValorPorHora() != null) {
            generarFactura(monitoriaGuardada, materia);
        }
        
        return monitoriaGuardada;
    }
    
    private void generarFactura(Monitoria monitoria, Materia materia) {
        // Generar número de factura único
        String numeroFactura = generarNumeroFactura();
        
        // Crear factura
        Factura factura = new Factura(monitoria, numeroFactura, 
                                      monitoria.getDuracionHoras(), 
                                      materia.getValorPorHora());
        
        facturaRepositorio.save(factura);
    }
    
    private String generarNumeroFactura() {
        // Formato: FACT-YYYYMMDD-XXXX
        java.time.LocalDate fecha = java.time.LocalDate.now();
        String prefijo = "FACT-" + fecha.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        
        // Buscar el último número de factura del día
        long count = facturaRepositorio.count();
        String numeroFactura = prefijo + String.format("%04d", count + 1);
        
        // Verificar que no exista
        while (facturaRepositorio.findByNumeroFactura(numeroFactura).isPresent()) {
            count++;
            numeroFactura = prefijo + String.format("%04d", count + 1);
        }
        
        return numeroFactura;
    }
    
    @Override
    public Monitoria findById(Integer id) {
        Optional<Monitoria> monitoria = monitoriaRepositorio.findById(id);
        return monitoria.orElse(null);
    }
    
    @Override
    public Monitoria update(Monitoria monitoria) {
        // Establecer relaciones desde los IDs
        if (monitoria.getIdEstudiante() != null) {
            Estudiante estudiante = estudianteRepositorio.findById(monitoria.getIdEstudiante()).orElse(null);
            monitoria.setEstudiante(estudiante);
        }
        if (monitoria.getIdMonitor() != null) {
            Monitor monitor = monitorRepositorio.findById(monitoria.getIdMonitor()).orElse(null);
            monitoria.setMonitor(monitor);
        }
        if (monitoria.getIdMateria() != null) {
            Materia materia = materiaRepositorio.findById(monitoria.getIdMateria()).orElse(null);
            monitoria.setMateria(materia);
        }
        return monitoriaRepositorio.save(monitoria);
    }
    
    @Override
    public void delete(Integer id) {
        monitoriaRepositorio.deleteById(id);
    }
}
