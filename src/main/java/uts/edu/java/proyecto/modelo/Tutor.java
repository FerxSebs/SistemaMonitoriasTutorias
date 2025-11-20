package uts.edu.java.proyecto.modelo;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tutores")
public class Tutor {
    
    @Id
    @Column(name = "id_tutor")
    private Integer idTutor;
    
    @ManyToOne
    @JoinColumn(name = "id_profesor")
    private Profesor profesor;
    
    @Column(name = "area_expertise")
    private String areaExpertise;
    
    @OneToMany(mappedBy = "tutor")
    private List<Tutoria> tutorias;
    
    // Constructores
    public Tutor() {
    }
    
    public Tutor(Integer idTutor, Profesor profesor, String areaExpertise, List<Tutoria> tutorias) {
        this.idTutor = idTutor;
        this.profesor = profesor;
        this.areaExpertise = areaExpertise;
        this.tutorias = tutorias;
    }
    
    // Getters y Setters
    public Integer getIdTutor() {
        return idTutor;
    }
    
    public void setIdTutor(Integer idTutor) {
        this.idTutor = idTutor;
    }
    
    public Profesor getProfesor() {
        return profesor;
    }
    
    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }
    
    public String getAreaExpertise() {
        return areaExpertise;
    }
    
    public void setAreaExpertise(String areaExpertise) {
        this.areaExpertise = areaExpertise;
    }
    
    public List<Tutoria> getTutorias() {
        return tutorias;
    }
    
    public void setTutorias(List<Tutoria> tutorias) {
        this.tutorias = tutorias;
    }
    
    // Método helper para obtener el nombre del profesor
    public String getNombreProfesor() {
        return profesor != null ? profesor.getNombre() : "";
    }
}