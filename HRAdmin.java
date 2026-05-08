package com.companyz.ems.user;

public class HRAdmin extends User {

    public HRAdmin(int userID, String username) {
        super(userID, username);
    }

    @Override
    public String getRole() {
        return "HR_ADMIN";
    }
}
