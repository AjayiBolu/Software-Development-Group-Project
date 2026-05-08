package com.companyz.ems.ui;

import com.companyz.ems.service.AuthService;
import com.companyz.ems.user.GeneralEmployee;
import com.companyz.ems.user.HRAdmin;
import com.companyz.ems.user.User;

import java.util.Scanner;

public class ConsoleUI {

    private final Scanner in = new Scanner(System.in);
    private final AuthService auth = new AuthService();

    public void run() {
        printBanner();
        while (true) {
            User user = login();
            if (user == null) {
                System.out.println("Invalid credentials. Try again, or type 'q' at username to quit.");
                continue;
            }
            System.out.println("\nWelcome, " + user.getUsername() + " (" + user.getRole() + ")");
            if (user instanceof HRAdmin) {
                new HRAdminMenu(in, (HRAdmin) user).show();
            } else {
                new EmployeeMenu(in, (GeneralEmployee) user).show();
            }
            System.out.println("\nLogged out.\n");
        }
    }

    private User login() {
        System.out.println("=== LOGIN ===");
        System.out.print("Username: ");
        String u = in.nextLine().trim();
        if (u.equalsIgnoreCase("q")) {
            System.out.println("Goodbye.");
            System.exit(0);
        }
        System.out.print("Password: ");
        String p = in.nextLine();
        return auth.authenticate(u, p);
    }

    private void printBanner() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("   Company Z - Employee Management System");
        System.out.println("   CSc3350 Group Team Project, Spring 2026");
        System.out.println("============================================================");
        System.out.println();
    }
}
