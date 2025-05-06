package com.eucl.rw.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface ReportsService {
    Map<String, Object> getTokenGenerationReport(LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getRevenueReportByPeriod(LocalDate startDate, LocalDate endDate);
    Map<String, Long> getUserActivityReport(Long userId);
    List<Map<String, Object>> getExpiredTokenReport();
    Map<String, Long> getNotificationStatistics();
}
