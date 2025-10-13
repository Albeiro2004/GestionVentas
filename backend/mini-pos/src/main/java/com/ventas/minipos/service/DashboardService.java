package com.ventas.minipos.service;

import com.ventas.minipos.dto.DashboardStatsDTO;
import com.ventas.minipos.dto.SaleResponse;
import com.ventas.minipos.repo.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DashboardService {

    @Autowired
    private ServiceOrderRepository servicioRepository;

    public SaleResponse obtenerIngresosServicios(String period) {
        LocalDate now = LocalDate.now();

        List<String> labels;
        List<Double> actual;
        List<Double> previous;

        switch (period.toLowerCase()) {
            case "week":
            default: {
                LocalDate start = now.with(DayOfWeek.MONDAY);
                LocalDate end = now.with(DayOfWeek.SUNDAY);

                LocalDate prevStart = start.minusWeeks(1);
                LocalDate prevEnd = end.minusWeeks(1);

                labels = Arrays.asList("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom");

                actual = fillWeekData(servicioRepository.obtenerIngresosPorDia(
                        start.atStartOfDay(), end.atTime(23, 59, 59))
                );
                previous = fillWeekData(servicioRepository.obtenerIngresosPorDia(
                        prevStart.atStartOfDay(), prevEnd.atTime(23, 59, 59))
                );
                break;
            }

            case "year": {
                LocalDate start = now.withDayOfYear(1);
                LocalDate end = now.withMonth(12).withDayOfMonth(31);

                LocalDate prevStart = start.minusYears(1);
                LocalDate prevEnd = end.minusYears(1);

                labels = Arrays.asList("Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic");

                actual = fillMonthData(servicioRepository.obtenerIngresosPorMes(
                        start.atStartOfDay(), end.atTime(23, 59, 59))
                );
                previous = fillMonthData(servicioRepository.obtenerIngresosPorMes(
                        prevStart.atStartOfDay(), prevEnd.atTime(23, 59, 59))
                );
                break;
            }
        }

        return new SaleResponse(labels, actual, previous);
    }

    private List<Double> fillMonthData(List<Object[]> rawData) {
        Map<Integer, Double> dataMap = rawData.stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> ((Number) r[1]).doubleValue()
                ));

        return IntStream.rangeClosed(1, 12)
                .mapToObj(m -> dataMap.getOrDefault(m, 0.0))
                .collect(Collectors.toList());
    }

    private List<Double> fillWeekData(List<Object[]> rawData) {
        // Convertimos la lista a un mapa: fecha -> total
        Map<LocalDate, Double> dataMap = rawData.stream()
                .collect(Collectors.toMap(
                        r -> {
                            if (r[0] instanceof java.sql.Date) {
                                return ((java.sql.Date) r[0]).toLocalDate();
                            } else if (r[0] instanceof LocalDate) {
                                return (LocalDate) r[0];
                            } else {
                                throw new IllegalArgumentException("Tipo de fecha no reconocido: " + r[0]);
                            }
                        },
                        r -> ((Number) r[1]).doubleValue()
                ));

        List<Double> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate monday = now.with(DayOfWeek.MONDAY);

        // Generamos valores para los 7 días de la semana actual (Lunes a Domingo)
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            result.add(dataMap.getOrDefault(date, 0.0));
        }

        return result;
    }

    public DashboardStatsDTO getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setVentas(150);
        stats.setCompras(80);
        stats.setProductos(45);
        stats.setReportes(12);

        stats.setHistorialVentas(Arrays.asList(10, 25, 50, 75, 150));
        stats.setHistorialCompras(Arrays.asList(5, 20, 40, 60, 80));
        stats.setHistorialProductos(Arrays.asList(10, 20, 30, 40, 45));
        stats.setHistorialReportes(Arrays.asList(1, 3, 5, 8, 12));

        return stats;
    }


}
