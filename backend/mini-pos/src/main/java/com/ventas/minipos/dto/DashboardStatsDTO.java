// src/main/java/com/ventas/minipos/dto/DashboardStatsDTO.java
package com.ventas.minipos.dto;

import java.util.List;

public class DashboardStatsDTO {

    private int ventas;
    private int compras;
    private int productos;
    private int reportes;

    private List<Integer> historialVentas;
    private List<Integer> historialCompras;
    private List<Integer> historialProductos;
    private List<Integer> historialReportes;

    public int getVentas() { return ventas; }
    public void setVentas(int ventas) { this.ventas = ventas; }

    public int getCompras() { return compras; }
    public void setCompras(int compras) { this.compras = compras; }

    public int getProductos() { return productos; }
    public void setProductos(int productos) { this.productos = productos; }

    public int getReportes() { return reportes; }
    public void setReportes(int reportes) { this.reportes = reportes; }

    public List<Integer> getHistorialVentas() { return historialVentas; }
    public void setHistorialVentas(List<Integer> historialVentas) { this.historialVentas = historialVentas; }

    public List<Integer> getHistorialCompras() { return historialCompras; }
    public void setHistorialCompras(List<Integer> historialCompras) { this.historialCompras = historialCompras; }

    public List<Integer> getHistorialProductos() { return historialProductos; }
    public void setHistorialProductos(List<Integer> historialProductos) { this.historialProductos = historialProductos; }

    public List<Integer> getHistorialReportes() { return historialReportes; }
    public void setHistorialReportes(List<Integer> historialReportes) { this.historialReportes = historialReportes; }
}
