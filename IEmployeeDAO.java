package com.companyz.ems.dao;

import com.companyz.ems.entity.Employee;
import com.companyz.ems.entity.Payroll;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IEmployeeDAO {

    Employee findById(int empID);
    List<Employee> findByLastName(String last);
    Employee findBySSN(String ssn);
    List<Employee> findAll();
    int insert(Employee e, int divisionID, int jobTitleID,
               String street, int cityID, int stateID, String zip,
               LocalDate dob, String mobile, String emergencyName, String emergencyPhone);
    boolean update(Employee e);
    boolean delete(int empID);

    int updateSalaryBelowThreshold(double threshold, double pctIncrease);
    int updateSalaryInRange(double minSalary, double maxSalary, double pctIncrease);

    List<Payroll> payHistoryFor(int empID);
    Map<String, Double> totalPayByJobTitle(int month, int year);
    Map<String, Double> totalPayByDivision(int month, int year);
    List<Employee> newHiresBetween(LocalDate start, LocalDate end);
}
