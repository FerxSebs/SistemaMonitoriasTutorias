package uts.edu.java.proyecto.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uts.edu.java.proyecto.modelo.Profesor;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.repositorio.ProfesorRepositorio;
import uts.edu.java.proyecto.repositorio.TutorRepositorio;

@Service
@Transactional
public class ProfesorServicio implements IProfesorServicio {
    
    @Autowired
    ProfesorRepositorio profesorRepositorio;
    
    @Autowired
    TutorRepositorio tutorRepositorio; // Inyectado
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Override
    public List<Profesor> getProfesores() {
        List<Profesor> profesores = profesorRepositorio.findAll();
        // Sincronizar esTutor para todos los profesores
        for (Profesor profesor : profesores) {
            Optional<Tutor> tutor = tutorRepositorio.findById(profesor.getIdProfesor());
            boolean existeTutor = tutor.isPresent();
            if (profesor.getEsTutor() == null || profesor.getEsTutor() != existeTutor) {
                profesor.setEsTutor(existeTutor);
                profesorRepositorio.save(profesor);
            }
        }
        return profesores;
    }
    
    @Override
    public Profesor save(Profesor profesor) {
        // Preparar la contraseña
        boolean necesitaPasswordDefault = (profesor.getPassword() == null || profesor.getPassword().isEmpty());
        
        if (!necesitaPasswordDefault) {
            // Encriptar la contraseña proporcionada
            profesor.setPassword(passwordEncoder.encode(profesor.getPassword()));
        }
        // Si necesita contraseña por defecto, la asignaremos después de guardar para obtener el ID
        
        // Guardar el profesor (primera vez para obtener el ID)
        Profesor profesorGuardado = profesorRepositorio.save(profesor);
        
        // Si no se proporcionó contraseña, asignar una por defecto basada en el ID
        if (necesitaPasswordDefault) {
            String passwordDefault = "profesor" + profesorGuardado.getIdProfesor();
            profesorGuardado.setPassword(passwordEncoder.encode(passwordDefault));
            profesorGuardado = profesorRepositorio.save(profesorGuardado);
        }
        
        // Si el profesor es tutor, crear o actualizar el registro en tutores
        if (profesorGuardado.getEsTutor() != null && profesorGuardado.getEsTutor()) {
            crearOActualizarTutor(profesorGuardado);
        } else {
            // Si ya no es tutor, eliminar el registro de tutores si existe
            eliminarTutorSiExiste(profesorGuardado.getIdProfesor());
        }
        
        return profesorGuardado;
    }
    
    private void crearOActualizarTutor(Profesor profesor) {
        // Verificar si ya existe un tutor para este profesor
        Optional<Tutor> tutorExistente = tutorRepositorio.findById(profesor.getIdProfesor());
        
        if (tutorExistente.isPresent()) {
            // Si existe, actualizar la relación con el profesor
            Tutor tutor = tutorExistente.get();
            tutor.setProfesor(profesor);
            // Mantener el área de expertise existente si ya tiene una
            if (tutor.getAreaExpertise() == null || tutor.getAreaExpertise().isEmpty()) {
                tutor.setAreaExpertise("General");
            }
            tutorRepositorio.save(tutor);
        } else {
            // Si no existe, crear uno nuevo
            Tutor tutor = new Tutor();
            tutor.setIdTutor(profesor.getIdProfesor());
            tutor.setProfesor(profesor);
            tutor.setAreaExpertise("General"); // Área por defecto
            tutorRepositorio.save(tutor);
        }
    }
    
    private void eliminarTutorSiExiste(Integer idProfesor) {
        Optional<Tutor> tutor = tutorRepositorio.findById(idProfesor);
        if (tutor.isPresent()) {
            tutorRepositorio.deleteById(idProfesor);
        }
    }
    
    @Override
    public Profesor findById(Integer id) {
        Optional<Profesor> profesor = profesorRepositorio.findById(id);
        if (profesor.isPresent()) {
            Profesor prof = profesor.get();
            // Sincronizar esTutor con la existencia de un registro en tutores
            Optional<Tutor> tutor = tutorRepositorio.findById(id);
            boolean existeTutor = tutor.isPresent();
            // Si hay discrepancia, sincronizar
            if (prof.getEsTutor() != null && prof.getEsTutor() != existeTutor) {
                prof.setEsTutor(existeTutor);
                profesorRepositorio.save(prof);
            } else if (prof.getEsTutor() == null && existeTutor) {
                prof.setEsTutor(true);
                profesorRepositorio.save(prof);
            }
            return prof;
        }
        return null;
    }
    
    @Override
    public void delete(Integer id) {
        profesorRepositorio.deleteById(id);
    }
    
    @Override
    public Profesor update(Profesor profesor) {
        // Obtener el profesor actual para comparar cambios
        Profesor profesorActual = profesorRepositorio.findById(profesor.getIdProfesor()).orElse(null);
        if (profesorActual == null) {
            // Si no existe, usar el método save
            return save(profesor);
        }
        
        // Guardar el estado anterior de esTutor
        Boolean esTutorAnterior = profesorActual.getEsTutor() != null ? profesorActual.getEsTutor() : false;
        Boolean esTutorNuevo = profesor.getEsTutor() != null ? profesor.getEsTutor() : false;
        
        // Si se está actualizando la contraseña, encriptarla
        if (profesor.getPassword() != null && !profesor.getPassword().isEmpty()) {
            // Solo encriptar si la contraseña ha cambiado (no está ya encriptada)
            // BCrypt hashes empiezan con $2a$ o $2b$
            if (!profesor.getPassword().startsWith("$2a$") && !profesor.getPassword().startsWith("$2b$")) {
                profesor.setPassword(passwordEncoder.encode(profesor.getPassword()));
            }
        } else {
            // Si no se proporciona contraseña, mantener la actual
            profesor.setPassword(profesorActual.getPassword());
        }
        
        // Guardar el profesor
        Profesor profesorActualizado = profesorRepositorio.save(profesor);
        
        // Gestionar cambios en el estado de tutor
        if (esTutorNuevo && !esTutorAnterior) {
            // Cambió de no tutor a tutor: crear registro
            crearOActualizarTutor(profesorActualizado);
        } else if (!esTutorNuevo && esTutorAnterior) {
            // Cambió de tutor a no tutor: eliminar registro
            eliminarTutorSiExiste(profesorActualizado.getIdProfesor());
        } else if (esTutorNuevo && esTutorAnterior) {
            // Sigue siendo tutor: actualizar relación si es necesario
            crearOActualizarTutor(profesorActualizado);
        }
        // Si no es tutor y no lo era antes, no hacer nada
        
        return profesorActualizado;
    }
}