package uts.edu.java.proyecto.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.repositorio.MonitorRepositorio;

@Service
@Transactional
public class MonitorServicio implements IMonitorServicio {
    
    @Autowired
    MonitorRepositorio monitorRepositorio;
    
    @Autowired
    EstudianteRepositorio estudianteRepositorio; // Inyectado para actualizar esMonitor
    
    @Override
    public List<Monitor> getMonitores() {
        return monitorRepositorio.findAll();
    }
    
    @Override
    public Monitor save(Monitor monitor) {
        return monitorRepositorio.save(monitor);
    }
    
    @Override
    public Monitor findById(Integer id) {
        Optional<Monitor> monitor = monitorRepositorio.findById(id);
        return monitor.orElse(null);
    }
    
    @Override
    public void delete(Integer id) {
        // Antes de eliminar, actualizar el esMonitor del estudiante relacionado
        Optional<Monitor> monitor = monitorRepositorio.findById(id);
        if (monitor.isPresent()) {
            Monitor monitorAEliminar = monitor.get();
            Estudiante estudiante = monitorAEliminar.getEstudiante();
            if (estudiante != null) {
                // Actualizar esMonitor a false en el estudiante
                estudiante.setEsMonitor(false);
                estudianteRepositorio.save(estudiante);
            }
        }
        
        // Eliminar el monitor
        monitorRepositorio.deleteById(id);
    }
}
