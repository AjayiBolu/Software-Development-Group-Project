package com.companyz.ems.ui;

import com.companyz.ems.entity.Employee;
import com.companyz.ems.entity.Payroll;
import com.companyz.ems.service.EmployeeService;
import com.companyz.ems.service.ReportService;
import com.companyz.ems.user.GeneralEmployee;

import java.util.List;
import java.util.Scanner;

public class EmployeeMenu {

    private final Scanner in;
    private final GeneralEmployee user;
    private final EmployeeService empService = new EmployeeService();
    private final ReportService  reportService = new ReportService();

    public EmployeeMenu(Scanner in, GeneralEmployee user) {
        this.in = in;
        this.user = user;
    }

    public void show() {
        while (true) {
            System.out.println("\n--- EMPLOYEE MENU ---");
            System.out.println(" 1. View my information");
            System.out.println(" 2. View my pay statement history");
            System.out.println(" 0. Logout");
            System.out.print("Select: ");
            String c = in.nextLine().trim();
            try {
                switch (c) {
                    case "1": showMyInfo(); break;
                    case "2": showPayHistory(); break;
                    case "0": return;
                    default:  System.out.println("Invalid selection.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void showMyInfo() {
        Employee e = empService.searchByEmpID(user.getLinkedEmpID());
        if (e == null) {
            System.out.println("Your record is not available. Please contact HR.");
            return;
        }
        System.out.println();
        System.out.println("  empID:    " + e.getEmpID());
        System.out.println("  Name:     " + e.getFullName());
        System.out.println("  Email:    " + e.getEmail());
        System.out.println("  DOB:      " + e.getDob());
        System.out.println("  Hired:    " + e.getHireDate());
        System.out.printf ("  Salary:   $%,.2f%n", e.getSalary());
        System.out.println("  Division: " + e.getDivisionName());
        System.out.println("  Title:    " + e.getJobTitle());
        System.out.println("  Mobile:   " + e.getMobilePhone());
        System.out.println("  Emergency:" + e.getEmergencyContactName()
            + " (" + e.getEmergencyContactPhone() + ")");
    }

    private void showPayHistory() {
        List<Payroll> ph = reportService.payHistoryFor(user.getLinkedEmpID());
        System.out.println("\n--- My Pay Statement History (most recent first) ---");
        if (ph.isEmpty()) {
            System.out.println("No pay statements on file yet.");
            return;
        }
        System.out.printf("%-12s  %12s  %12s  %12s  %12s%n",
            "Pay Date", "Earnings", "Fed Tax", "State Tax", "Net Pay");
        System.out.println("---------------------------------------------------------------------");
        for (Payroll p : ph) {
            System.out.printf("%-12s  $%,11.2f  $%,11.2f  $%,11.2f  $%,11.2f%n",
                p.getPayDate(), p.getEarnings(), p.getFedTax(),
                p.getStateTax(), p.getNetPay());
        }
    }
}
