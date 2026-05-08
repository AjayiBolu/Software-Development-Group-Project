package com.companyz.ems.entity;

import java.time.LocalDate;

public class Employee {
    private int empID;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate hireDate;
    private double salary;
    private String ssn;
    private Integer addressID;

    private LocalDate dob;
    private String mobilePhone;
    private String emergencyContactName;
    private String emergencyContactPhone;

    private String divisionName;
    private String jobTitle;

    public Employee() {}

    public Employee(int empID, String firstName, String lastName, String email,
                    LocalDate hireDate, double salary, String ssn, Integer addressID) {
        this.empID = empID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hireDate = hireDate;
        this.salary = salary;
        this.ssn = ssn;
        this.addressID = addressID;
    }

    public int getEmpID() { return empID; }
    public void setEmpID(int empID) { this.empID = empID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public Integer getAddressID() { return addressID; }
    public void setAddressID(Integer addressID) { this.addressID = addressID; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String n) { this.emergencyContactName = n; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String p) { this.emergencyContactPhone = p; }

    public String getDivisionName() { return divisionName; }
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    @Override
    public String toString() {
        return String.format(
            "Employee[empID=%d, name=%s %s, email=%s, hired=%s, salary=$%,.2f, ssn=%s, division=%s, title=%s]",
            empID, firstName, lastName, email, hireDate, salary, ssn,
            divisionName == null ? "-" : divisionName,
            jobTitle == null ? "-" : jobTitle);
    }
}
