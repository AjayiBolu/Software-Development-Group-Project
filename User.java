package com.companyz.ems.user;

public abstract class User {
    protected int userID;
    protected String username;

    protected User(int userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public int getUserID() { return userID; }
    public String getUsername() { return username; }

    public abstract String getRole();
}
