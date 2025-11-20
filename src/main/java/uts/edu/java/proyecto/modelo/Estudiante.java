package uts.edu.java.proyecto.modelo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "estudiantes")
public class Estudiante {
    
    @Id
    @Column(name = "id_estudiante")
    private Integer idEstudiante;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    @Column(name = "apellido")
    private String apellido;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
    @Column(name = "correo")
    private String correo;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono")
    private String telefono;
    
    @Size(max = 100, message = "El programa académico no puede exceder 100 caracteres")
    @Column(name = "programa_academico")
    private String programaAcademico;
    
    @Column(name = "es_monitor")
    private Boolean esMonitor = false;
    
    @Column(name = "password")
    private String password;
    
    @OneToMany(mappedBy = "estudiante")
    private List<Tutoria> tutorias;
    
    @OneToMany(mappedBy = "estudiante")
    private List<Monitor> monitores;
    
    // Constructores
    public Estudiante() {
    }
    
    public Estudiante(Integer idEstudiante, String nombre, String apellido, String correo, String telefono, String programaAcademico, Boolean esMonitor, List<Tutoria> tutorias, List<Monitor> monitores) {
        this.idEstudiante = idEstudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.programaAcademico = programaAcademico;
        this.esMonitor = esMonitor;
        this.tutorias = tutorias;
        this.monitores = monitores;
    }
    
    // Método helper para obtener el nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    // Getters y Setters manuales para asegurar compatibilidad
    public Integer getIdEstudiante() {
        return idEstudiante;
    }
    
    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getProgramaAcademico() {
        return programaAcademico;
    }
    
    public void setProgramaAcademico(String programaAcademico) {
        this.programaAcademico = programaAcademico;
    }
    
    public Boolean getEsMonitor() {
        return esMonitor;
    }
    
    public void setEsMonitor(Boolean esMonitor) {
        this.esMonitor = esMonitor;
    }
    
    public List<Tutoria> getTutorias() {
        return tutorias;
    }
    
    public void setTutorias(List<Tutoria> tutorias) {
        this.tutorias = tutorias;
    }
    
    public List<Monitor> getMonitores() {
        return monitores;
    }
    
    public void setMonitores(List<Monitor> monitores) {
        this.monitores = monitores;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}