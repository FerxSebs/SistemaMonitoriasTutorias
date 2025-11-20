package uts.edu.java.proyecto.modelo;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "monitores")
public class Monitor {
    
    @Id
    @Column(name = "id_monitor")
    private Integer idMonitor;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;
    
    @NotBlank(message = "El área de expertise es obligatoria")
    @Size(max = 100, message = "El área de expertise no puede exceder 100 caracteres")
    @Column(name = "area_expertise")
    private String areaExpertise;
    
    @Column(name = "estado")
    private String estado = "Activo";
    
    @OneToMany(mappedBy = "monitor")
    private List<Monitoria> monitorias;
    
    // Constructores
    public Monitor() {
    }
    
    public Monitor(Integer idMonitor, Estudiante estudiante, String areaExpertise, String estado, List<Monitoria> monitorias) {
        this.idMonitor = idMonitor;
        this.estudiante = estudiante;
        this.areaExpertise = areaExpertise;
        this.estado = estado;
        this.monitorias = monitorias;
    }
    
    // Getters y Setters
    public Integer getIdMonitor() {
        return idMonitor;
    }
    
    public void setIdMonitor(Integer idMonitor) {
        this.idMonitor = idMonitor;
    }
    
    public Estudiante getEstudiante() {
        return estudiante;
    }
    
    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }
    
    public String getAreaExpertise() {
        return areaExpertise;
    }
    
    public void setAreaExpertise(String areaExpertise) {
        this.areaExpertise = areaExpertise;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public List<Monitoria> getMonitorias() {
        return monitorias;
    }
    
    public void setMonitorias(List<Monitoria> monitorias) {
        this.monitorias = monitorias;
    }
    
    // Método helper para obtener el nombre del estudiante monitor
    public String getNombreEstudiante() {
        return estudiante != null ? estudiante.getNombreCompleto() : "N/A";
    }
}