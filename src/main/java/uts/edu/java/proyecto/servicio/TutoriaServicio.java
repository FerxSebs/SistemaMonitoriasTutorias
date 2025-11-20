package uts.edu.java.proyecto.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uts.edu.java.proyecto.modelo.Estudiante;
import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.modelo.Tutoria;
import uts.edu.java.proyecto.repositorio.EstudianteRepositorio;
import uts.edu.java.proyecto.repositorio.MateriaRepositorio;
import uts.edu.java.proyecto.repositorio.TutorRepositorio;
import uts.edu.java.proyecto.repositorio.TutoriaRepositorio;

@Service
@Transactional
public class TutoriaServicio implements ITutoriaServicio {
    
    @Autowired
    private TutoriaRepositorio tutoriaRepositorio;
    
    @Autowired
    private EstudianteRepositorio estudianteRepositorio;
    
    @Autowired
    private TutorRepositorio tutorRepositorio;
    
    @Autowired
    private MateriaRepositorio materiaRepositorio;
    
    @Override
    public List<Tutoria> listar() {
        return tutoriaRepositorio.findAll();
    }
    
    @Override
    public Tutoria listarId(int id) {
        return tutoriaRepositorio.findById(id).orElse(null);
    }
    
    @Override
    public Tutoria save(Tutoria tutoria) {
        // Convertir IDs a objetos de relación
        if (tutoria.getIdEstudiante() != null) {
            Estudiante estudiante = estudianteRepositorio.findById(tutoria.getIdEstudiante()).orElse(null);
            tutoria.setEstudiante(estudiante);
        }
        if (tutoria.getIdTutor() != null) {
            Tutor tutor = tutorRepositorio.findById(tutoria.getIdTutor()).orElse(null);
            tutoria.setTutor(tutor);
        }
        if (tutoria.getIdMateria() != null) {
            Materia materia = materiaRepositorio.findById(tutoria.getIdMateria()).orElse(null);
            tutoria.setMateria(materia);
        }
        return tutoriaRepositorio.save(tutoria);
    }
    
    @Override
    public Tutoria update(Tutoria tutoria) {
        // Convertir IDs a objetos de relación
        if (tutoria.getIdEstudiante() != null) {
            Estudiante estudiante = estudianteRepositorio.findById(tutoria.getIdEstudiante()).orElse(null);
            tutoria.setEstudiante(estudiante);
        }
        if (tutoria.getIdTutor() != null) {
            Tutor tutor = tutorRepositorio.findById(tutoria.getIdTutor()).orElse(null);
            tutoria.setTutor(tutor);
        }
        if (tutoria.getIdMateria() != null) {
            Materia materia = materiaRepositorio.findById(tutoria.getIdMateria()).orElse(null);
            tutoria.setMateria(materia);
        }
        return tutoriaRepositorio.save(tutoria);
    }
    
    @Override
    public void delete(int id) {
        tutoriaRepositorio.deleteById(id);
    }
    
    @Override
    public List<Tutoria> buscarPorEstudiante(Integer idEstudiante) {
        List<Tutoria> todasLasTutorias = tutoriaRepositorio.findAll();
        List<Tutoria> resultado = new ArrayList<>();
        for (Tutoria t : todasLasTutorias) {
            if (t.getEstudiante() != null && t.getEstudiante().getIdEstudiante().equals(idEstudiante)) {
                resultado.add(t);
            }
        }
        return resultado;
    }
    
    @Override
    public List<Tutoria> buscarPorTutor(Integer idTutor) {
        List<Tutoria> todasLasTutorias = tutoriaRepositorio.findAll();
        List<Tutoria> resultado = new ArrayList<>();
        for (Tutoria t : todasLasTutorias) {
            if (t.getTutor() != null && t.getTutor().getIdTutor().equals(idTutor)) {
                resultado.add(t);
            }
        }
        return resultado;
    }
    
    @Override
    public List<Tutoria> buscarPorMateria(Integer idMateria) {
        List<Tutoria> todasLasTutorias = tutoriaRepositorio.findAll();
        List<Tutoria> resultado = new ArrayList<>();
        for (Tutoria t : todasLasTutorias) {
            if (t.getMateria() != null && t.getMateria().getIdMateria().equals(idMateria)) {
                resultado.add(t);
            }
        }
        return resultado;
    }
    
    @Override
    public List<Tutoria> buscarPorEstado(String estado) {
        List<Tutoria> todasLasTutorias = tutoriaRepositorio.findAll();
        List<Tutoria> resultado = new ArrayList<>();
        for (Tutoria t : todasLasTutorias) {
            if (t.getEstado() != null && t.getEstado().equals(estado)) {
                resultado.add(t);
            }
        }
        return resultado;
    }
    
    @Override
    public List<Tutor> getTutores() {
        return tutorRepositorio.findAll();
    }
    
    @Override
    public List<Materia> getMaterias() {
        return materiaRepositorio.findAll();
    }
    
    @Override
    public boolean existeEstudiante(Integer idEstudiante) {
        return estudianteRepositorio.existsById(idEstudiante);
    }
    
    // Métodos auxiliares
    public List<Tutor> obtenerTodosLosTutores() {
        return getTutores();
    }
    
    public List<Materia> obtenerTodasLasMaterias() {
        return getMaterias();
    }
    
    public Optional<Estudiante> obtenerEstudiante(Integer idEstudiante) {
        return estudianteRepositorio.findById(idEstudiante);
    }
}