package com.wendys.salesaudit.model;

import java.util.List;

import com.wendys.salesaudit.utility.Constants;

public class User {

    private String employeeId;
    private String userId;
    private String commonName;
    private List<String> functions;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }

    /**
     * @return true if this user has the Admin function, and false otherwise.
     */
    public Boolean isAdmin() {
        return functions.contains(Constants.ADMIN_FUNCTION);
    }

    /**
     * @return true if this user has the Editor function, and false otherwise.
     */
    public Boolean isEditor() {
        return functions.contains(Constants.EDIT_FUNCTION);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        // A User with a null employeeId is never equal to any other User object
        if (this.employeeId == null) {
            return false;
        }

        if (obj == null || !(obj instanceof User)) {
            return false;
        }

        User that = (User)obj;

        return this.employeeId.equals(that.employeeId);
    }

    @Override
    public int hashCode() {
        if (employeeId != null) {
            return employeeId.hashCode();
        } else {
            return Integer.MAX_VALUE;
        }
    }

}