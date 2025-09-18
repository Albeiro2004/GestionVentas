package com.ventas.minipos.service;

import com.ventas.minipos.repo.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

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

        return purchaseRepository.findTotalPurchasesByDateRange(start.atStartOfDay(), now.atTime(23,59,59));
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

        return purchaseRepository.findTotalPurchasesByDateRange(start.atStartOfDay(), end.atTime(23,59,59));
    }

}
