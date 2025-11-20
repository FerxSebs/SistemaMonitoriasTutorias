package uts.edu.java.proyecto.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uts.edu.java.proyecto.modelo.Profesor;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.repositorio.ProfesorRepositorio;
import uts.edu.java.proyecto.repositorio.TutorRepositorio;

@Service
@Transactional
public class TutorServicio implements ITutorServicio {
    
    @Autowired
    TutorRepositorio tutorRepositorio;
    
    @Autowired
    ProfesorRepositorio profesorRepositorio; // Inyectado para actualizar esTutor
    
    @Override
    public List<Tutor> getTutores() {
        return tutorRepositorio.findAll();
    }
    
    @Override
    public Tutor save(Tutor tutor) {
        return tutorRepositorio.save(tutor);
    }
    
    @Override
    public Tutor findById(Integer id) {
        Optional<Tutor> tutor = tutorRepositorio.findById(id);
        return tutor.orElse(null);
    }
    
    @Override
    public void delete(Integer id) {
        // Antes de eliminar, actualizar el esTutor del profesor relacionado
        Optional<Tutor> tutor = tutorRepositorio.findById(id);
        if (tutor.isPresent()) {
            Tutor tutorAEliminar = tutor.get();
            Profesor profesor = tutorAEliminar.getProfesor();
            if (profesor != null) {
                // Actualizar esTutor a false en el profesor
                profesor.setEsTutor(false);
                profesorRepositorio.save(profesor);
            }
        }
        
        // Eliminar el tutor
        tutorRepositorio.deleteById(id);
    }
}