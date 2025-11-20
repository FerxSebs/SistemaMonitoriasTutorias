package uts.edu.java.proyecto.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "tutorias")
public class Tutoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tutoria")
    private Integer idTutoria;
    
    @ManyToOne
    @JoinColumn(name = "id_tutor")
    private Tutor tutor;
    
    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;
    
    @ManyToOne
    @JoinColumn(name = "id_materia")
    private Materia materia;
    
    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @Column(name = "duracion_horas")
    private Double duracionHoras;
    
    @Column(name = "estado")
    private String estado;
    
    @Column(name = "observaciones")
    private String observaciones;
    
    // Campos para el formulario (no se persisten en BD)
    @Transient
    private Integer idEstudiante;
    
    @Transient
    private Integer idTutor;
    
    @Transient
    private Integer idMateria;

    // Constructores
    public Tutoria() {
    }

    public Tutoria(Integer idTutoria, Tutor tutor, Estudiante estudiante, Materia materia, 
                   LocalDateTime fecha, Double duracionHoras, String estado, String observaciones) {
        this.idTutoria = idTutoria;
        this.tutor = tutor;
        this.estudiante = estudiante;
        this.materia = materia;
        this.fecha = fecha;
        this.duracionHoras = duracionHoras;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public Integer getIdTutoria() {
        return idTutoria;
    }

    public void setIdTutoria(Integer idTutoria) {
        this.idTutoria = idTutoria;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
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
    
    // Getters y Setters para campos del formulario
    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Integer getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(Integer idTutor) {
        this.idTutor = idTutor;
    }

    public Integer getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }
    
    // Métodos helper para obtener nombres
    public String getNombreEstudiante() {
        return estudiante != null ? estudiante.getNombreCompleto() : "N/A";
    }
    
    public String getNombreTutor() {
        return tutor != null ? tutor.getNombreProfesor() : "N/A";
    }
    
    public String getNombreMateria() {
        return materia != null ? materia.getNombreConCodigo() : "N/A";
    }
    
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
        if (estado == null || estado.isEmpty()) {
            estado = "pendiente";
        }
    }
}