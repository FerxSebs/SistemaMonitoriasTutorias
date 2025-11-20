package uts.edu.java.proyecto.controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uts.edu.java.proyecto.modelo.Factura;
import uts.edu.java.proyecto.modelo.Monitor;
import uts.edu.java.proyecto.servicio.FacturaServicio;
import uts.edu.java.proyecto.servicio.MonitorServicio;
import uts.edu.java.proyecto.servicio.UsuarioActualServicio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/views/facturas")
public class FacturaAdminControlador {
    
    @Autowired
    FacturaServicio facturaServicio;
    
    @Autowired
    MonitorServicio monitorServicio;
    
    @Autowired
    UsuarioActualServicio usuarioActualServicio;
    
    @GetMapping("/")
    public String verFacturas(
            @RequestParam(required = false) Integer idMonitor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {
        
        List<Factura> facturas;
        
        // Verificar el rol del usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMonitor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"));
        
        // Si es monitor, solo puede ver sus propias facturas
        if (isMonitor && !isAdmin) {
            Integer idMonitorActual = usuarioActualServicio.getIdMonitorActual();
            if (idMonitorActual != null) {
                idMonitor = idMonitorActual; // Forzar el filtro al monitor actual
            }
        }
        
        // Si se proporcionan fechas, convertirlas a LocalDateTime
        final LocalDateTime fechaInicioDateTime = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        final LocalDateTime fechaFinDateTime = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;
        
        // Aplicar filtros: solo mostrar facturas que cumplan las condiciones
        if (fechaInicioDateTime != null && fechaFinDateTime != null) {
            // Si hay fechas Y monitor
            if (idMonitor != null && idMonitor > 0) {
                facturas = facturaServicio.findByMonitorAndFechaBetween(idMonitor, fechaInicioDateTime, fechaFinDateTime);
                model.addAttribute("filtroMonitor", idMonitor);
            } else {
                // Solo fechas
                facturas = facturaServicio.findByFechaBetween(fechaInicioDateTime, fechaFinDateTime);
            }
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
        } else if (idMonitor != null && idMonitor > 0) {
            // Solo filtro por monitor sin fechas
            facturas = facturaServicio.findByMonitor(idMonitor);
            model.addAttribute("filtroMonitor", idMonitor);
        } else {
            // Sin filtros: mostrar todas las facturas (solo admin) o del monitor actual
            if (isMonitor && !isAdmin) {
                Integer idMonitorActual = usuarioActualServicio.getIdMonitorActual();
                if (idMonitorActual != null) {
                    facturas = facturaServicio.findByMonitor(idMonitorActual);
                } else {
                    facturas = java.util.Collections.emptyList();
                }
            } else {
                facturas = facturaServicio.getFacturas();
            }
        }
        
        // Obtener lista de monitores para el filtro (solo para admin)
        List<Monitor> monitores = isAdmin ? monitorServicio.getMonitores() : java.util.Collections.emptyList();
        
        // Calcular total facturado para monitores
        if (isMonitor && !isAdmin) {
            double totalFacturado = facturas.stream().mapToDouble(Factura::getTotal).sum();
            model.addAttribute("totalFacturado", totalFacturado);
        }
        
        model.addAttribute("facturas", facturas);
        model.addAttribute("monitores", monitores);
        
        return "/views/facturas/lista";
    }
    
    @GetMapping("/totalizar")
    public String totalizarFacturas(
            @RequestParam(required = false) Integer idMonitor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // Validar que se proporcionen fechas
        if (fechaInicio == null || fechaFin == null) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar fecha de inicio y fecha fin para totalizar");
            StringBuilder redirectUrl = new StringBuilder("redirect:/views/facturas/?");
            if (fechaInicio != null) {
                redirectUrl.append("fechaInicio=").append(fechaInicio);
            }
            if (fechaFin != null) {
                if (redirectUrl.length() > "redirect:/views/facturas/?".length()) {
                    redirectUrl.append("&");
                }
                redirectUrl.append("fechaFin=").append(fechaFin);
            }
            if (idMonitor != null) {
                if (redirectUrl.length() > "redirect:/views/facturas/?".length()) {
                    redirectUrl.append("&");
                }
                redirectUrl.append("idMonitor=").append(idMonitor);
            }
            return redirectUrl.toString();
        }
        
        // Convertir fechas a LocalDateTime
        final LocalDateTime fechaInicioDateTime = fechaInicio.atStartOfDay();
        final LocalDateTime fechaFinDateTime = fechaFin.atTime(23, 59, 59);
        
        // Obtener facturas según filtros
        List<Factura> facturas;
        if (idMonitor != null && idMonitor > 0) {
            facturas = facturaServicio.findByMonitorAndFechaBetween(idMonitor, fechaInicioDateTime, fechaFinDateTime);
            model.addAttribute("filtroMonitor", idMonitor);
        } else {
            facturas = facturaServicio.findByFechaBetween(fechaInicioDateTime, fechaFinDateTime);
        }
        
        // Calcular totales por monitor
        Map<Integer, Map<String, Object>> totalesPorMonitor = facturaServicio.getTotalesPorMonitor(fechaInicioDateTime, fechaFinDateTime);
        
        // Si hay filtro por monitor, filtrar los totales
        if (idMonitor != null && idMonitor > 0 && totalesPorMonitor != null && !totalesPorMonitor.isEmpty()) {
            Map<Integer, Map<String, Object>> totalesFiltrados = new java.util.HashMap<>();
            if (totalesPorMonitor.containsKey(idMonitor)) {
                totalesFiltrados.put(idMonitor, totalesPorMonitor.get(idMonitor));
            }
            totalesPorMonitor = totalesFiltrados;
        }
        
        // Calcular totales generales
        Map<String, Object> totalesGenerales = new java.util.HashMap<>();
        double totalHoras = 0.0;
        double totalSubtotal = 0.0;
        double totalIva = 0.0;
        double totalTotal = 0.0;
        int totalCantidadFacturas = 0;
        
        if (totalesPorMonitor != null && !totalesPorMonitor.isEmpty()) {
            for (Map<String, Object> totales : totalesPorMonitor.values()) {
                totalCantidadFacturas += (Integer) totales.get("cantidad");
                totalHoras += (Double) totales.get("horas");
                totalSubtotal += (Double) totales.get("subtotal");
                totalIva += (Double) totales.get("iva");
                totalTotal += (Double) totales.get("total");
            }
        }
        
        totalesGenerales.put("cantidadMonitores", totalesPorMonitor != null ? totalesPorMonitor.size() : 0);
        totalesGenerales.put("cantidadFacturas", totalCantidadFacturas);
        totalesGenerales.put("totalHoras", totalHoras);
        totalesGenerales.put("totalSubtotal", totalSubtotal);
        totalesGenerales.put("totalIva", totalIva);
        totalesGenerales.put("totalTotal", totalTotal);
        
        // Obtener lista de monitores
        List<Monitor> monitores = monitorServicio.getMonitores();
        
        model.addAttribute("facturas", facturas);
        model.addAttribute("monitores", monitores);
        model.addAttribute("totalesPorMonitor", totalesPorMonitor);
        model.addAttribute("totalesGenerales", totalesGenerales);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        
        return "/views/facturas/totalizacion";
    }
    
    @GetMapping("/detalle/{id}")
    public String verDetalleFactura(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Factura factura = facturaServicio.findById(id);
        
        if (factura == null) {
            redirectAttributes.addFlashAttribute("error", "Factura no encontrada");
            return "redirect:/views/facturas/";
        }
        
        // Verificar permisos: si es monitor, solo puede ver sus propias facturas
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isMonitor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MONITOR"));
        
        if (isMonitor && !isAdmin) {
            Integer idMonitorActual = usuarioActualServicio.getIdMonitorActual();
            if (idMonitorActual == null || factura.getMonitoria() == null || 
                factura.getMonitoria().getMonitor() == null ||
                !factura.getMonitoria().getMonitor().getIdMonitor().equals(idMonitorActual)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permiso para ver esta factura");
                return "redirect:/error/acceso-denegado";
            }
        }
        
        model.addAttribute("factura", factura);
        return "/views/facturas/detalle";
    }
}

