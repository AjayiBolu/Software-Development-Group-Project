package com.companyz.ems;

import com.companyz.ems.db.DatabaseConnection;
import com.companyz.ems.ui.ConsoleUI;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseConnection.getInstance();
        } catch (RuntimeException ex) {
            System.err.println("DATABASE STARTUP FAILED");
            System.err.println("------------------------");
            System.err.println(ex.getMessage());
            System.err.println();
            System.err.println("Check your db.properties file. Make sure the URL,");
            System.err.println("username, and password are correct, and that the");
            System.err.println("setup.sql script has been run against your database.");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.getInstance().close();
        }));

        new ConsoleUI().run();
    }
}
