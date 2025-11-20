package uts.edu.java.proyecto.servicio;

import java.util.List;

import uts.edu.java.proyecto.modelo.Estudiante;

public interface IEstudianteServicio {
    public List<Estudiante> getEstudiantes();
    public Estudiante save(Estudiante estudiante);
    public Estudiante findById(Integer id);
    public void delete(Integer id);
    public Estudiante update(Estudiante estudiante);
}