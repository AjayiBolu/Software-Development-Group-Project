package com.companyz.ems.ui;

import com.companyz.ems.entity.Employee;
import com.companyz.ems.entity.Payroll;
import com.companyz.ems.service.EmployeeService;
import com.companyz.ems.service.ReportService;
import com.companyz.ems.user.HRAdmin;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HRAdminMenu {

    private final Scanner in;
    private final HRAdmin user;
    private final EmployeeService empService = new EmployeeService();
    private final ReportService reportService = new ReportService();

    public HRAdminMenu(Scanner in, HRAdmin user) {
        this.in = in;
        this.user = user;
    }

    public void show() {
        while (true) {
            System.out.println("\n--- HR ADMIN MENU ---");
            System.out.println(" 1. Search employees");
            System.out.println(" 2. Add new employee");
            System.out.println(" 3. Update employee");
            System.out.println(" 4. Delete employee");
            System.out.println(" 5. Raise salaries (below threshold)");
            System.out.println(" 6. Raise salaries (in range)");
            System.out.println(" 7. Reports");
            System.out.println(" 0. Logout");
            System.out.print("Select: ");
            String choice = in.nextLine().trim();

            try {
                switch (choice) {
                    case "1": searchEmployees(); break;
                    case "2": addEmployee(); break;
                    case "3": updateEmployee(); break;
                    case "4": deleteEmployee(); break;
                    case "5": raiseBelow(); break;
                    case "6": raiseInRange(); break;
                    case "7": reportsMenu(); break;
                    case "0": return;
                    default:  System.out.println("Invalid selection.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void searchEmployees() {
        System.out.println("\nSearch by:  1) empID   2) Last name   3) SSN   4) List all");
        System.out.print("Choice: ");
        String c = in.nextLine().trim();
        switch (c) {
            case "1":
                int id = readInt("empID: ");
                Employee e = empService.searchByEmpID(id);
                if (e == null) System.out.println("No matching employee.");
                else           printEmployee(e);
                break;
            case "2":
                System.out.print("Last name (partial OK): ");
                List<Employee> matches = empService.searchByLastName(in.nextLine());
                if (matches.isEmpty()) System.out.println("No matches.");
                else                   matches.forEach(this::printEmployeeShort);
                break;
            case "3":
                System.out.print("SSN (###-##-####): ");
                Employee bySsn = empService.searchBySSN(in.nextLine());
                if (bySsn == null) System.out.println("No matching employee.");
                else               printEmployee(bySsn);
                break;
            case "4":
                empService.listAll().forEach(this::printEmployeeShort);
                break;
            default:
                System.out.println("Invalid.");
        }
    }

    private void addEmployee() {
        System.out.println("\n--- Add New Employee ---");
        Employee e = new Employee();
        e.setFirstName(readNonEmpty("First name: "));
        e.setLastName(readNonEmpty("Last name: "));
        System.out.print("Email: ");           e.setEmail(in.nextLine().trim());
        e.setHireDate(readDate("Hire date (YYYY-MM-DD): "));
        e.setSalary(readDouble("Salary: "));
        System.out.print("SSN (###-##-####): "); e.setSsn(in.nextLine().trim());

        int divID  = readInt("Division ID (1=Eng, 2=Sales, 3=Ops, 4=HR, 5=Finance): ");
        int jobID  = readInt("Job Title ID (1=SWE, 2=Sr SWE, 3=Eng Mgr, 4=Sales Rep, 5=Sales Mgr, 6=HR Spec, 7=HR Mgr, 8=Ops Analyst, 9=Acct, 10=Fin Dir): ");

        System.out.println("--- Address ---");
        System.out.print("Street: ");          String street = in.nextLine().trim();
        int cityID = readInt("City ID (1=Atlanta, 2=Marietta, 3=Decatur, 4=Miami, 5=Austin, 6=Charlotte): ");
        int stateID = readInt("State ID (1=GA, 2=FL, 3=TX, 4=NC, 5=CA): ");
        System.out.print("Zip: ");             String zip = in.nextLine().trim();

        LocalDate dob   = readDate("Date of birth (YYYY-MM-DD): ");
        System.out.print("Mobile phone: ");     String mobile = in.nextLine().trim();
        System.out.print("Emergency contact name: ");  String emN = in.nextLine().trim();
        System.out.print("Emergency contact phone: "); String emP = in.nextLine().trim();

        int newID = empService.addEmployee(e, divID, jobID, street, cityID, stateID, zip, dob, mobile, emN, emP);
        System.out.println("\nEmployee added. New empID = " + newID);
    }

    private void updateEmployee() {
        int id = readInt("empID to update: ");
        Employee e = empService.searchByEmpID(id);
        if (e == null) { System.out.println("No matching employee."); return; }
        printEmployee(e);
        System.out.println("\nLeave a field blank to keep its current value.");

        System.out.print("First name [" + e.getFirstName() + "]: ");
        String s = in.nextLine().trim();
        if (!s.isEmpty()) e.setFirstName(s);

        System.out.print("Last name [" + e.getLastName() + "]: ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) e.setLastName(s);

        System.out.print("Email [" + e.getEmail() + "]: ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) e.setEmail(s);

        System.out.print("Salary [" + e.getSalary() + "]: ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) e.setSalary(Double.parseDouble(s));

        System.out.print("SSN [" + e.getSsn() + "]: ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) e.setSsn(s);

        if (empService.updateEmployee(e)) System.out.println("Updated.");
        else                              System.out.println("Update failed.");
    }

    private void deleteEmployee() {
        int id = readInt("empID to delete: ");
        Employee e = empService.searchByEmpID(id);
        if (e == null) { System.out.println("No matching employee."); return; }
        printEmployee(e);
        System.out.print("\nAre you sure you want to delete this employee? (yes/no): ");
        String confirm = in.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("Cancelled.");
            return;
        }
        if (empService.deleteEmployee(id)) System.out.println("Employee " + id + " deleted.");
        else                               System.out.println("Delete failed.");
    }

    private void raiseBelow() {
        double threshold = readDouble("Salary threshold: ");
        double pct       = readDouble("Percent increase: ");
        int n = empService.raiseSalaryBelow(threshold, pct);
        System.out.println(n + " employee(s) updated.");
    }

    private void raiseInRange() {
        double min = readDouble("Min salary: ");
        double max = readDouble("Max salary: ");
        double pct = readDouble("Percent increase: ");
        int n = empService.raiseSalaryInRange(min, max, pct);
        System.out.println(n + " employee(s) updated.");
    }

    private void reportsMenu() {
        System.out.println("\nReports:");
        System.out.println(" 1. Total pay by job title (monthly)");
        System.out.println(" 2. Total pay by division (monthly)");
        System.out.println(" 3. New hires in date range");
        System.out.println(" 4. Pay history for an employee");
        System.out.print("Select: ");
        String c = in.nextLine().trim();
        switch (c) {
            case "1": {
                int month = readInt("Month (1-12): ");
                int year  = readInt("Year (e.g., 2026): ");
                Map<String, Double> r = reportService.totalPayByJobTitle(month, year);
                System.out.printf("%n--- Total Pay by Job Title for %02d/%d ---%n", month, year);
                if (r.isEmpty()) System.out.println("No payroll data for that month.");
                r.forEach((title, total) -> System.out.printf("%-30s $%,.2f%n", title, total));
                break;
            }
            case "2": {
                int month = readInt("Month (1-12): ");
                int year  = readInt("Year (e.g., 2026): ");
                Map<String, Double> r = reportService.totalPayByDivision(month, year);
                System.out.printf("%n--- Total Pay by Division for %02d/%d ---%n", month, year);
                if (r.isEmpty()) System.out.println("No payroll data for that month.");
                r.forEach((div, total) -> System.out.printf("%-30s $%,.2f%n", div, total));
                break;
            }
            case "3": {
                LocalDate start = readDate("Start date (YYYY-MM-DD): ");
                LocalDate end   = readDate("End date (YYYY-MM-DD): ");
                List<Employee> hires = reportService.newHiresBetween(start, end);
                System.out.println("\n--- New Hires from " + start + " to " + end + " ---");
                if (hires.isEmpty()) System.out.println("No new hires in that range.");
                hires.forEach(h -> System.out.printf("%5d  %-25s  hired %s%n",
                        h.getEmpID(), h.getFullName(), h.getHireDate()));
                break;
            }
            case "4": {
                int empID = readInt("empID: ");
                Employee e = empService.searchByEmpID(empID);
                if (e == null) { System.out.println("No matching employee."); break; }
                List<Payroll> ph = reportService.payHistoryFor(empID);
                System.out.println("\n--- Pay History for " + e.getFullName() + " ---");
                if (ph.isEmpty()) System.out.println("No pay history.");
                ph.forEach(p -> System.out.printf("%s   gross $%,.2f   net $%,.2f%n",
                        p.getPayDate(), p.getEarnings(), p.getNetPay()));
                break;
            }
            default:
                System.out.println("Invalid.");
        }
    }

    private void printEmployee(Employee e) {
        System.out.println();
        System.out.println("  empID:    " + e.getEmpID());
        System.out.println("  Name:     " + e.getFullName());
        System.out.println("  Email:    " + e.getEmail());
        System.out.println("  DOB:      " + e.getDob());
        System.out.println("  Hired:    " + e.getHireDate());
        System.out.printf ("  Salary:   $%,.2f%n", e.getSalary());
        System.out.println("  SSN:      " + e.getSsn());
        System.out.println("  Division: " + e.getDivisionName());
        System.out.println("  Title:    " + e.getJobTitle());
        System.out.println("  Mobile:   " + e.getMobilePhone());
        System.out.println("  Emergency:" + e.getEmergencyContactName() + " (" + e.getEmergencyContactPhone() + ")");
    }

    private void printEmployeeShort(Employee e) {
        System.out.printf("%5d  %-25s  %-25s  $%,12.2f  %s%n",
            e.getEmpID(), e.getFullName(), e.getJobTitle() == null ? "-" : e.getJobTitle(),
            e.getSalary(), e.getDivisionName() == null ? "-" : e.getDivisionName());
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Cannot be empty.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(in.nextLine().trim()); }
            catch (NumberFormatException ex) { System.out.println("Not a number, try again."); }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Double.parseDouble(in.nextLine().trim()); }
            catch (NumberFormatException ex) { System.out.println("Not a number, try again."); }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return LocalDate.parse(in.nextLine().trim()); }
            catch (Exception ex) { System.out.println("Bad date format, use YYYY-MM-DD."); }
        }
    }
}
