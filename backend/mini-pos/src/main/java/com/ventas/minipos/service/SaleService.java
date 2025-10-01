package com.ventas.minipos.service;

import com.ventas.minipos.domain.Inventory;
import com.ventas.minipos.domain.Product;
import com.ventas.minipos.domain.Sale;
import com.ventas.minipos.domain.SaleItem;
import com.ventas.minipos.dto.*;
import com.ventas.minipos.exception.BusinessException;
import com.ventas.minipos.exception.SaleDeletionException;
import com.ventas.minipos.repo.InventoryRepository;
import com.ventas.minipos.repo.ProductRepository;
import com.ventas.minipos.repo.SaleItemRepository;
import com.ventas.minipos.repo.SaleRepository;
import com.ventas.minipos.dto.TopProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final SaleItemRepository saleItemRepository;
    private final InventoryRepository inventoryRepository;


    @Transactional(readOnly = true)
    public List<SaleDTO> getAllSales() {
        return saleRepository.findAllWithCustomerAndUser();
    }

    @Transactional
    public void deleteSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new BusinessException("Factura no encontrada"));

        // ✅ Verificar si la factura es del día actual
        LocalDate today = LocalDate.now();
        LocalDate saleDate = sale.getSaleDate().toLocalDate();

        if (!saleDate.isEqual(today)) {
            throw new SaleDeletionException("Solo se pueden eliminar facturas del día actual. \nConsulta con el Desarrollador para Proceder con esta Acción.");
        }

        // 🔄 Devolver stock a la tabla de inventario
        sale.getItems().forEach(item -> {
            Product product = item.getProduct();
            Integer cantidadVendida = item.getCantidad();

            // 🔍 Asume que la venta ocurrió desde una ubicación por defecto, como "Almacén Principal"
            String defaultLocation = "Almacén Principal";

            // 📈 Buscar el registro de inventario para el producto y la ubicación
            Inventory inventoryItem = inventoryRepository.findByProduct(product)
                    .orElse(null); // Puedes manejar un caso en el que no se encuentre el registro de inventario

            if (inventoryItem != null) {
                // ✅ Actualizar el stock
                inventoryItem.setStock(inventoryItem.getStock() + cantidadVendida);
                inventoryRepository.save(inventoryItem);
            }
        });

        // 🗑️ Eliminar factura y sus items (en cascada si está configurado)
        saleRepository.delete(sale);
    }

    private SaleDTO toDTO(Sale sale) {
        return SaleDTO.builder()
                .id(sale.getId())
                .saleDate(sale.getSaleDate())
                .total(sale.getTotal())
                .customer(CustomerDTO.builder()
                        .documento(sale.getCustomer().getDocumento())
                        .nombre(sale.getCustomer().getNombre())
                        .build())
                .name(sale.getUser().getName())
                .build();
    }

    @Transactional(readOnly = true)
    public List<SaleItemDTO> getSaleItems(Long saleId) {
        return saleItemRepository.findItemsBySaleId(saleId);
    }

    private SaleItemDTO itemToDTO(SaleItem it) {
        return SaleItemDTO.builder()
                .id(it.getId())
                .cantidad(it.getCantidad())
                .precioUnitario(it.getPrecioUnitario())
                .descuento(it.getDescuento())
                .subtotal(it.getSubtotal())
                .productName(it.getProduct().getNombre())
                .build();
    }

    public Double sumVentasByPeriod(String period) {
        LocalDate now = LocalDate.now();
        LocalDate start;

        switch (period) {
            case "today":
                start = now;
                break;
            case "week":
                start = now.with(DayOfWeek.MONDAY);
                break;
            case "month":
                start = now.withDayOfMonth(1);
                break;
            case "quarter":
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                start = LocalDate.of(now.getYear(), (currentQuarter - 1) * 3 + 1, 1);
                break;
            case "year":
                start = now.withDayOfYear(1);
                break;
            default:
                start = now.withDayOfMonth(1);
        }

        return saleRepository.sumByFechaBetween(start.atStartOfDay(), now.atTime(23,59,59));
    }

    public Double sumVentasByPreviousPeriod(String period) {
        LocalDate now = LocalDate.now();
        LocalDate start, end;

        switch (period) {
            case "today":
                start = now.minusDays(1);
                end = start;
                break;
            case "week":
                start = now.with(DayOfWeek.MONDAY).minusWeeks(1);
                end = start.plusDays(6);
                break;
            case "month":
                start = now.minusMonths(1).withDayOfMonth(1);
                end = start.withDayOfMonth(start.lengthOfMonth());
                break;
            case "quarter":
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int prevQuarter = currentQuarter - 1;
                int year = now.getYear();
                if (prevQuarter == 0) {
                    prevQuarter = 4;
                    year--;
                }
                start = LocalDate.of(year, (prevQuarter - 1) * 3 + 1, 1);
                end = start.plusMonths(2).withDayOfMonth(start.plusMonths(2).lengthOfMonth());
                break;
            case "year":
                start = now.minusYears(1).withDayOfYear(1);
                end = start.withDayOfYear(start.lengthOfYear());
                break;
            default:
                start = now.minusMonths(1).withDayOfMonth(1);
                end = start.withDayOfMonth(start.lengthOfMonth());
        }

        return saleRepository.sumByFechaBetween(start.atStartOfDay(), end.atTime(23,59,59));
    }

    public SaleResponse getSalesByPeriod(String period) {
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

                actual = fillWeekData(saleRepository.sumVentasByDay(start.atStartOfDay(), end.atTime(23,59,59)));
                previous = fillWeekData(saleRepository.sumVentasByDay(prevStart.atStartOfDay(), prevEnd.atTime(23,59,59)));
                break;
            }

            case "year": {
                LocalDate start = now.withDayOfYear(1);
                LocalDate end = now.withMonth(12).withDayOfMonth(31);

                LocalDate prevStart = start.minusYears(1);
                LocalDate prevEnd = end.minusYears(1);

                labels = Arrays.asList("Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic");

                actual = fillMonthData(saleRepository.sumVentasByMonth(start.atStartOfDay(), end.atTime(23,59,59)));
                previous = fillMonthData(saleRepository.sumVentasByMonth(prevStart.atStartOfDay(), prevEnd.atTime(23,59,59)));
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

        // 12 meses siempre
        return IntStream.rangeClosed(1, 12)
                .mapToObj(m -> dataMap.getOrDefault(m, 0.0))
                .collect(Collectors.toList());
    }

    private List<Double> fillWeekData(List<Object[]> rawData) {
        Map<Integer, Double> dataMap = rawData.stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> ((Number) r[1]).doubleValue()
                ));

        // 7 días (Lun..Dom)
        return IntStream.rangeClosed(1, 7)
                .mapToObj(d -> {
                    int dow = (d == 7) ? 0 : d; // ajustar: Domingo=0, Lun=1..Sab=6
                    return dataMap.getOrDefault(dow, 0.0);
                })
                .collect(Collectors.toList());
    }

    public List<TopProductResponse> getTopProducts() {
        List<Object[]> rows = saleRepository.findTopProducts();

        return rows.stream()
                .map(r -> new TopProductResponse(
                        (String) r[0],
                        (String) r[1],
                        ((Number) r[2]).intValue(),
                        ((Number) r[3]).doubleValue()
                ))
                .toList();
    }



}
