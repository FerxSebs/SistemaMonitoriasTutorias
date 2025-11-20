package uts.edu.java.proyecto.servicio;

import java.util.List;
import uts.edu.java.proyecto.modelo.Monitoria;

public interface IMonitoriaServicio {
    List<Monitoria> getMonitorias();
    Monitoria save(Monitoria monitoria);
    Monitoria findById(Integer id);
    Monitoria update(Monitoria monitoria);
    void delete(Integer id);
}
