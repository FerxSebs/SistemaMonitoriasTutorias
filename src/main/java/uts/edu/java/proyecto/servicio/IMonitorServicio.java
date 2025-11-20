package uts.edu.java.proyecto.servicio;

import java.util.List;

import uts.edu.java.proyecto.modelo.Monitor;

public interface IMonitorServicio {
    public List<Monitor> getMonitores();
    public Monitor save(Monitor monitor);
    public Monitor findById(Integer id);
    public void delete(Integer id);
}
