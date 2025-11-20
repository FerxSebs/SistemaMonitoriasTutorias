package uts.edu.java.proyecto.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.repositorio.MonitorRepositorio;

@Service
@Transactional
public class EstudianteServicio implements IEstudianteServicio {
    
    @Autowired
    EstudianteRepositorio estudianteRepositorio;
    
    @Autowired
    MonitorRepositorio monitorRepositorio;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Override
    public List<Estudiante> getEstudiantes() {
        List<Estudiante> estudiantes = estudianteRepositorio.findAll();
        // Sincronizar esMonitor para todos los estudiantes
        for (Estudiante estudiante : estudiantes) {
            Optional<Monitor> monitor = monitorRepositorio.findById(estudiante.getIdEstudiante());
            boolean existeMonitor = monitor.isPresent();
            if (estudiante.getEsMonitor() == null || estudiante.getEsMonitor() != existeMonitor) {
                estudiante.setEsMonitor(existeMonitor);
                estudianteRepositorio.save(estudiante);
            }
        }
        return estudiantes;
    }
    
    @Override
    public Estudiante save(Estudiante estudiante) {
        // Preparar la contraseña
        boolean necesitaPasswordDefault = (estudiante.getPassword() == null || estudiante.getPassword().isEmpty());
        
        if (!necesitaPasswordDefault) {
            // Encriptar la contraseña proporcionada
            estudiante.setPassword(passwordEncoder.encode(estudiante.getPassword()));
        }
        // Si necesita contraseña por defecto, la asignaremos después de guardar para obtener el ID
        
        // Guardar el estudiante (primera vez para obtener el ID)
        Estudiante estudianteGuardado = estudianteRepositorio.save(estudiante);
        
        // Si no se proporcionó contraseña, asignar una por defecto basada en el ID
        if (necesitaPasswordDefault) {
            String passwordDefault = "estudiante" + estudianteGuardado.getIdEstudiante();
            estudianteGuardado.setPassword(passwordEncoder.encode(passwordDefault));
            estudianteGuardado = estudianteRepositorio.save(estudianteGuardado);
        }
        
        // Si el estudiante es monitor, crear o actualizar el registro en monitores
        if (estudianteGuardado.getEsMonitor() != null && estudianteGuardado.getEsMonitor()) {
            crearOActualizarMonitor(estudianteGuardado);
        } else {
            // Si ya no es monitor, eliminar el registro de monitores si existe
            eliminarMonitorSiExiste(estudianteGuardado.getIdEstudiante());
        }
        
        return estudianteGuardado;
    }
    
    private void crearOActualizarMonitor(Estudiante estudiante) {
        // Verificar si ya existe un monitor para este estudiante
        Optional<Monitor> monitorExistente = monitorRepositorio.findById(estudiante.getIdEstudiante());
        
        if (monitorExistente.isPresent()) {
            // Si existe, actualizar la relación con el estudiante
            Monitor monitor = monitorExistente.get();
            monitor.setEstudiante(estudiante);
            // Mantener el área de expertise existente si ya tiene una
            if (monitor.getAreaExpertise() == null || monitor.getAreaExpertise().isEmpty()) {
                monitor.setAreaExpertise("General");
            }
            // Asegurar que el estado esté activo si no tiene uno
            if (monitor.getEstado() == null || monitor.getEstado().isEmpty()) {
                monitor.setEstado("Activo");
            }
            monitorRepositorio.save(monitor);
        } else {
            // Si no existe, crear uno nuevo
            Monitor monitor = new Monitor();
            monitor.setIdMonitor(estudiante.getIdEstudiante());
            monitor.setEstudiante(estudiante);
            monitor.setAreaExpertise("General"); // Área por defecto
            monitor.setEstado("Activo");
            monitorRepositorio.save(monitor);
        }
    }
    
    private void eliminarMonitorSiExiste(Integer idEstudiante) {
        Optional<Monitor> monitor = monitorRepositorio.findById(idEstudiante);
        if (monitor.isPresent()) {
            monitorRepositorio.deleteById(idEstudiante);
        }
    }
    
    @Override
    public Estudiante findById(Integer id) {
        Optional<Estudiante> estudiante = estudianteRepositorio.findById(id);
        if (estudiante.isPresent()) {
            Estudiante est = estudiante.get();
            // Sincronizar esMonitor con la existencia de un registro en monitores
            Optional<Monitor> monitor = monitorRepositorio.findById(id);
            boolean existeMonitor = monitor.isPresent();
            // Si hay discrepancia, sincronizar
            if (est.getEsMonitor() != null && est.getEsMonitor() != existeMonitor) {
                est.setEsMonitor(existeMonitor);
                estudianteRepositorio.save(est);
            } else if (est.getEsMonitor() == null && existeMonitor) {
                est.setEsMonitor(true);
                estudianteRepositorio.save(est);
            }
            return est;
        }
        return null;
    }
    
    @Override
    public void delete(Integer id) {
        estudianteRepositorio.deleteById(id);
    }
    
    @Override
    public Estudiante update(Estudiante estudiante) {
        // Obtener el estudiante actual para comparar cambios
        Estudiante estudianteActual = estudianteRepositorio.findById(estudiante.getIdEstudiante()).orElse(null);
        if (estudianteActual == null) {
            // Si no existe, usar el método save
            return save(estudiante);
        }
        
        // Guardar el estado anterior de esMonitor
        Boolean esMonitorAnterior = estudianteActual.getEsMonitor() != null ? estudianteActual.getEsMonitor() : false;
        Boolean esMonitorNuevo = estudiante.getEsMonitor() != null ? estudiante.getEsMonitor() : false;
        
        // Si se está actualizando la contraseña, encriptarla
        if (estudiante.getPassword() != null && !estudiante.getPassword().isEmpty()) {
            // Solo encriptar si la contraseña ha cambiado (no está ya encriptada)
            // BCrypt hashes empiezan con $2a$ o $2b$
            if (!estudiante.getPassword().startsWith("$2a$") && !estudiante.getPassword().startsWith("$2b$")) {
                estudiante.setPassword(passwordEncoder.encode(estudiante.getPassword()));
            }
        } else {
            // Si no se proporciona contraseña, mantener la actual
            estudiante.setPassword(estudianteActual.getPassword());
        }
        
        // Guardar el estudiante
        Estudiante estudianteActualizado = estudianteRepositorio.save(estudiante);
        
        // Gestionar cambios en el estado de monitor
        if (esMonitorNuevo && !esMonitorAnterior) {
            // Cambió de no monitor a monitor: crear registro
            crearOActualizarMonitor(estudianteActualizado);
        } else if (!esMonitorNuevo && esMonitorAnterior) {
            // Cambió de monitor a no monitor: eliminar registro
            eliminarMonitorSiExiste(estudianteActualizado.getIdEstudiante());
        } else if (esMonitorNuevo && esMonitorAnterior) {
            // Sigue siendo monitor: actualizar relación si es necesario
            crearOActualizarMonitor(estudianteActualizado);
        }
        // Si no es monitor y no lo era antes, no hacer nada
        
        return estudianteActualizado;
    }
}