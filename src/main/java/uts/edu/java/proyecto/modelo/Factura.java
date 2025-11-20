package uts.edu.java.proyecto.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;

@Entity
@Table(name = "facturas")
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_monitoria")
    private Monitoria monitoria;
    
    @Column(name = "numero_factura", unique = true, nullable = false)
    private String numeroFactura;
    
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;
    
    @Column(name = "horas", nullable = false)
    private Double horas;
    
    @Column(name = "valor_por_hora", nullable = false)
    private Double valorPorHora;
    
    @Column(name = "subtotal", nullable = false)
    private Double subtotal;
    
    @Column(name = "iva")
    private Double iva = 0.0;
    
    @Column(name = "total", nullable = false)
    private Double total;
    
    @Column(name = "estado", nullable = false)
    private String estado = "Pendiente"; // Pendiente, Pagada, Cancelada
    
    // Constructores
    public Factura() {
        this.fechaEmision = LocalDateTime.now();
    }
    
    public Factura(Monitoria monitoria, String numeroFactura, Double horas, Double valorPorHora) {
        this.monitoria = monitoria;
        this.numeroFactura = numeroFactura;
        this.fechaEmision = LocalDateTime.now();
        this.horas = horas;
        this.valorPorHora = valorPorHora;
        this.subtotal = horas * valorPorHora;
        this.iva = this.subtotal * 0.19; // IVA del 19%
        this.total = this.subtotal + this.iva;
        this.estado = "Pendiente";
    }
    
    // Getters y Setters
    public Integer getIdFactura() {
        return idFactura;
    }
    
    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }
    
    public Monitoria getMonitoria() {
        return monitoria;
    }
    
    public void setMonitoria(Monitoria monitoria) {
        this.monitoria = monitoria;
    }
    
    public String getNumeroFactura() {
        return numeroFactura;
    }
    
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
    
    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public Double getHoras() {
        return horas;
    }
    
    public void setHoras(Double horas) {
        this.horas = horas;
        calcularTotales();
    }
    
    public Double getValorPorHora() {
        return valorPorHora;
    }
    
    public void setValorPorHora(Double valorPorHora) {
        this.valorPorHora = valorPorHora;
        calcularTotales();
    }
    
    public Double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
    
    public Double getIva() {
        return iva;
    }
    
    public void setIva(Double iva) {
        this.iva = iva;
    }
    
    public Double getTotal() {
        return total;
    }
    
    public void setTotal(Double total) {
        this.total = total;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    // Método para calcular totales
    private void calcularTotales() {
        if (horas != null && valorPorHora != null) {
            this.subtotal = horas * valorPorHora;
            this.iva = this.subtotal * 0.19;
            this.total = this.subtotal + this.iva;
        }
    }
}

