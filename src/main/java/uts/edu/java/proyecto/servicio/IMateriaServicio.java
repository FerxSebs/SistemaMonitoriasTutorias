package uts.edu.java.proyecto.servicio;

import java.util.List;

import uts.edu.java.proyecto.modelo.Materia;

public interface IMateriaServicio {
    List<Materia> getMaterias();
    Materia save(Materia materia);
    Materia findById(Integer id);
    Materia update(Materia materia);
    void delete(Integer id);
}