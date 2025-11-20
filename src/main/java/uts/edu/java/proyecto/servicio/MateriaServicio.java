package uts.edu.java.proyecto.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uts.edu.java.proyecto.modelo.Materia;
import uts.edu.java.proyecto.repositorio.MateriaRepositorio;

@Service
@Transactional
public class MateriaServicio implements IMateriaServicio {
    
    @Autowired
    MateriaRepositorio materiaRepositorio;
    
    @Override
    public List<Materia> getMaterias() {
        return materiaRepositorio.findAll();
    }
    
    @Override
    public Materia save(Materia materia) {
        if (materia.getValorPorHora() == null) {
            materia.setValorPorHora(0.0);
        }
        return materiaRepositorio.save(materia);
    }
    
    @Override
    public Materia findById(Integer id) {
        Optional<Materia> materia = materiaRepositorio.findById(id);
        return materia.orElse(null);
    }
    
    @Override
    public Materia update(Materia materia) {
        if (materia.getValorPorHora() == null) {
            materia.setValorPorHora(0.0);
        }
        return materiaRepositorio.save(materia);
    }
    
    @Override
    public void delete(Integer id) {
        materiaRepositorio.deleteById(id);
    }
}