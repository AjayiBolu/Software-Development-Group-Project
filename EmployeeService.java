package com.companyz.ems.service;

import com.companyz.ems.dao.EmployeeDAOImpl;
import com.companyz.ems.dao.IEmployeeDAO;
import com.companyz.ems.entity.Employee;

import java.time.LocalDate;
import java.util.List;

public class EmployeeService {

    private final IEmployeeDAO dao;

    public EmployeeService() {
        this(new EmployeeDAOImpl());
    }

    public EmployeeService(IEmployeeDAO dao) {
        this.dao = dao;
    }

    public Employee searchByEmpID(int empID) {
        if (empID <= 0) throw new IllegalArgumentException("empID must be positive");
        return dao.findById(empID);
    }

    public List<Employee> searchByLastName(String last) {
        if (last == null || last.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        return dao.findByLastName(last.trim());
    }

    public Employee searchBySSN(String ssn) {
        if (ssn == null || ssn.trim().isEmpty()) {
            throw new IllegalArgumentException("SSN cannot be empty");
        }
        return dao.findBySSN(ssn.trim());
    }

    public List<Employee> listAll() {
        return dao.findAll();
    }

    public boolean updateEmployee(Employee e) {
        validateEmployee(e);
        if (e.getEmpID() <= 0) {
            throw new IllegalArgumentException("Cannot update without an empID");
        }
        return dao.update(e);
    }

    public boolean deleteEmployee(int empID) {
        if (empID <= 0) throw new IllegalArgumentException("empID must be positive");
        return dao.delete(empID);
    }

    public int raiseSalaryBelow(double threshold, double pct) {
        if (threshold <= 0) throw new IllegalArgumentException("Threshold must be positive");
        if (pct <= 0)        throw new IllegalArgumentException("Percent increase must be greater than 0");
        return dao.updateSalaryBelowThreshold(threshold, pct);
    }

    public int raiseSalaryInRange(double minSal, double maxSal, double pct) {
        if (minSal < 0 || maxSal <= 0) throw new IllegalArgumentException("Salaries must be positive");
        if (minSal >= maxSal)          throw new IllegalArgumentException("min must be less than max");
        if (pct <= 0)                  throw new IllegalArgumentException("Percent increase must be greater than 0");
        return dao.updateSalaryInRange(minSal, maxSal, pct);
    }

    public int addEmployee(Employee e, int divisionID, int jobTitleID,
                           String street, int cityID, int stateID, String zip,
                           LocalDate dob, String mobile,
                           String emergencyName, String emergencyPhone) {
        validateEmployee(e);
        if (divisionID <= 0 || jobTitleID <= 0) {
            throw new IllegalArgumentException("Division and Job Title are required");
        }
        return dao.insert(e, divisionID, jobTitleID, street, cityID, stateID, zip,
                          dob, mobile, emergencyName, emergencyPhone);
    }

    private void validateEmployee(Employee e) {
        if (e == null) throw new IllegalArgumentException("Employee cannot be null");
        require(e.getFirstName(), "first name");
        require(e.getLastName(),  "last name");
        if (e.getSalary() < 0) throw new IllegalArgumentException("Salary cannot be negative");
        if (e.getEmail() != null && !e.getEmail().isEmpty() && !e.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email is not formatted correctly");
        }
        if (e.getSsn() != null && !e.getSsn().isEmpty()
                && !e.getSsn().matches("\\d{3}-\\d{2}-\\d{4}")) {
            throw new IllegalArgumentException("SSN must be formatted as ###-##-####");
        }
    }

    private void require(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " cannot be empty");
        }
    }
}
