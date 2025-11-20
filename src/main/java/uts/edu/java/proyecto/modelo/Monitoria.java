package uts.edu.java.proyecto.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "monitorias")
public class Monitoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_monitoria")
    private Integer idMonitoria;
    
    @NotNull(message = "El ID del estudiante es obligatorio")
    @Column(name = "id_estudiante")
    private Integer idEstudiante;
    
    @NotNull(message = "El ID del monitor es obligatorio")
    @Column(name = "id_monitor")
    private Integer idMonitor;
    
    @NotNull(message = "El ID de la materia es obligatorio")
    @Column(name = "id_materia")
    private Integer idMateria;
    
    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @NotNull(message = "La duración es obligatoria")
    @Column(name = "duracion_horas")
    private Double duracionHoras;
    
    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    @Column(name = "estado")
    private String estado;
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(name = "observaciones")
    private String observaciones;
    
    // Relaciones (opcionales para simplificar)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estudiante", insertable = false, updatable = false)
    private Estudiante estudiante;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_monitor", insertable = false, updatable = false)
    private Monitor monitor;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_materia", insertable = false, updatable = false)
    private Materia materia;
    
    // Constructores
    public Monitoria() {
    }
    
    public Monitoria(Integer idMonitoria, Integer idEstudiante, Integer idMonitor, Integer idMateria, 
                    LocalDateTime fecha, Double duracionHoras, String estado, String observaciones, 
                    Estudiante estudiante, Monitor monitor, Materia materia) {
        this.idMonitoria = idMonitoria;
        this.idEstudiante = idEstudiante;
        this.idMonitor = idMonitor;
        this.idMateria = idMateria;
        this.fecha = fecha;
        this.duracionHoras = duracionHoras;
        this.estado = estado;
        this.observaciones = observaciones;
        this.estudiante = estudiante;
        this.monitor = monitor;
        this.materia = materia;
    }
    
    // Getters y Setters
    public Integer getIdMonitoria() {
        return idMonitoria;
    }
    
    public void setIdMonitoria(Integer idMonitoria) {
        this.idMonitoria = idMonitoria;
    }
    
    public Integer getIdEstudiante() {
        return idEstudiante;
    }
    
    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }
    
    public Integer getIdMonitor() {
        return idMonitor;
    }
    
    public void setIdMonitor(Integer idMonitor) {
        this.idMonitor = idMonitor;
    }
    
    public Integer getIdMateria() {
        return idMateria;
    }
    
    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public Double getDuracionHoras() {
        return duracionHoras;
    }
    
    public void setDuracionHoras(Double duracionHoras) {
        this.duracionHoras = duracionHoras;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public Estudiante getEstudiante() {
        return estudiante;
    }
    
    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }
    
    public Monitor getMonitor() {
        return monitor;
    }
    
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
    
    public Materia getMateria() {
        return materia;
    }
    
    public void setMateria(Materia materia) {
        this.materia = materia;
    }
    
    // Métodos helper
    public String getNombreEstudiante() {
        return estudiante != null ? estudiante.getNombreCompleto() : "N/A";
    }
    
    public String getNombreMonitor() {
        return monitor != null && monitor.getEstudiante() != null ? monitor.getEstudiante().getNombreCompleto() : "N/A";
    }
    
    public String getNombreMateria() {
        return materia != null ? materia.getNombreConCodigo() : "N/A";
    }
}
