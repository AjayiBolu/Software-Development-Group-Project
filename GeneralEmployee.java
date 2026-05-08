package com.companyz.ems.user;

public class GeneralEmployee extends User {

    private final int linkedEmpID;

    public GeneralEmployee(int userID, String username, int linkedEmpID) {
        super(userID, username);
        this.linkedEmpID = linkedEmpID;
    }

    public int getLinkedEmpID() {
        return linkedEmpID;
    }

    @Override
    public String getRole() {
        return "EMPLOYEE";
    }
}
