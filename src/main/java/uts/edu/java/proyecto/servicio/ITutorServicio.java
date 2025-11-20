package uts.edu.java.proyecto.servicio;

import java.util.List;

import uts.edu.java.proyecto.modelo.Tutor;

public interface ITutorServicio {
    public List<Tutor> getTutores();
    public Tutor save(Tutor tutor);
    public Tutor findById(Integer id);
    public void delete(Integer id);
}