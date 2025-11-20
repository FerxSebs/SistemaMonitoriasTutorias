package uts.edu.java.proyecto.modelo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "materias")
public class Materia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia")
    private Integer idMateria;
    
    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(max = 100, message = "El nombre de la materia no puede exceder 100 caracteres")
    @Column(name = "nombre")
    private String nombre;
    
    @NotBlank(message = "El código de la materia es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    @Column(name = "codigo")
    private String codigo;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "valor_por_hora", nullable = false)
    private Double valorPorHora = 0.0;
    
    @OneToMany(mappedBy = "materia")
    private List<Tutoria> tutorias;
    
    // Constructores
    public Materia() {
    }
    
    public Materia(Integer idMateria, String nombre, String codigo, String descripcion, Double valorPorHora, List<Tutoria> tutorias) {
        this.idMateria = idMateria;
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.valorPorHora = valorPorHora;
        this.tutorias = tutorias;
    }
    
    // Método helper para mostrar materia con código
    public String getNombreConCodigo() {
        return codigo + " - " + nombre;
    }
    
    // Getters y Setters (en caso de que Lombok no los genere correctamente)
    public Integer getIdMateria() {
        return idMateria;
    }
    
    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<Tutoria> getTutorias() {
        return tutorias;
    }
    
    public void setTutorias(List<Tutoria> tutorias) {
        this.tutorias = tutorias;
    }
    
    public Double getValorPorHora() {
        return valorPorHora;
    }
    
    public void setValorPorHora(Double valorPorHora) {
        this.valorPorHora = valorPorHora;
    }
}
