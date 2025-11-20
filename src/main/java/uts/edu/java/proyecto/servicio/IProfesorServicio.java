package uts.edu.java.proyecto.servicio;

import java.util.List;

import uts.edu.java.proyecto.modelo.Profesor;

public interface IProfesorServicio {
    public List<Profesor> getProfesores();
    public Profesor save(Profesor profesor);
    public Profesor findById(Integer id);
    public void delete(Integer id);
    public Profesor update(Profesor profesor);
}