package uts.edu.java.proyecto.servicio;

import java.util.List;

import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.modelo.Tutor;
import uts.edu.java.proyecto.modelo.Tutoria;

public interface ITutoriaServicio {
    
    // Métodos CRUD básicos
    public List<Tutoria> listar();
    
    public Tutoria listarId(int id);
    
    public Tutoria save(Tutoria tutoria);
    
    public Tutoria update(Tutoria tutoria);
    
    public void delete(int id);
    
    // Métodos de búsqueda
    public List<Tutoria> buscarPorEstudiante(Integer idEstudiante);
    
    public List<Tutoria> buscarPorTutor(Integer idTutor);
    
    public List<Tutoria> buscarPorMateria(Integer idMateria);
    
    public List<Tutoria> buscarPorEstado(String estado);
    
    // Métodos auxiliares
    public List<Tutor> getTutores();
    
    public List<Materia> getMaterias();
    
    public boolean existeEstudiante(Integer idEstudiante);
    ;
}