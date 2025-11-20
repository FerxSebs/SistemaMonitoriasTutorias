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
@Table(name = "profesores")
public class Profesor {
    
    @Id
    @Column(name = "id_profesor")
    private Integer idProfesor;
    
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
    
    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    @Column(name = "departamento")
    private String departamento;
    
    @Column(name = "es_tutor")
    private Boolean esTutor = false;
    
    @Column(name = "password")
    private String password;
    
    @OneToMany(mappedBy = "profesor")
    private List<Tutor> tutores;
    
    // Constructores
    public Profesor() {
    }
    
    public Profesor(Integer idProfesor, String nombre, String apellido, String correo, String telefono, String departamento, Boolean esTutor, List<Tutor> tutores) {
        this.idProfesor = idProfesor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.departamento = departamento;
        this.esTutor = esTutor;
        this.tutores = tutores;
    }
    
    // Método helper para obtener el nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    // Getters y Setters
    public Integer getIdProfesor() {
        return idProfesor;
    }
    
    public void setIdProfesor(Integer idProfesor) {
        this.idProfesor = idProfesor;
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
    
    public String getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public Boolean getEsTutor() {
        return esTutor;
    }
    
    public void setEsTutor(Boolean esTutor) {
        this.esTutor = esTutor;
    }
    
    public List<Tutor> getTutores() {
        return tutores;
    }
    
    public void setTutores(List<Tutor> tutores) {
        this.tutores = tutores;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
