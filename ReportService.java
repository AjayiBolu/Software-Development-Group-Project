package com.companyz.ems.service;

import com.companyz.ems.dao.EmployeeDAOImpl;
import com.companyz.ems.dao.IEmployeeDAO;
import com.companyz.ems.entity.Employee;
import com.companyz.ems.entity.Payroll;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportService {

    private final IEmployeeDAO dao;

    public ReportService() {
        this(new EmployeeDAOImpl());
    }

    public ReportService(IEmployeeDAO dao) {
        this.dao = dao;
    }

    public List<Payroll> payHistoryFor(int empID) {
        if (empID <= 0) throw new IllegalArgumentException("empID must be positive");
        return dao.payHistoryFor(empID);
    }

    public Map<String, Double> totalPayByJobTitle(int month, int year) {
        validateMonthYear(month, year);
        return dao.totalPayByJobTitle(month, year);
    }

    public Map<String, Double> totalPayByDivision(int month, int year) {
        validateMonthYear(month, year);
        return dao.totalPayByDivision(month, year);
    }

    public List<Employee> newHiresBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Both dates required");
        if (start.isAfter(end))           throw new IllegalArgumentException("Start must be on or before end");
        return dao.newHiresBetween(start, end);
    }

    private void validateMonthYear(int month, int year) {
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be 1-12");
        if (year  < 2000 || year > 2100) throw new IllegalArgumentException("Year out of range");
    }
}
