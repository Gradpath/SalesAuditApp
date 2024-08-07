package com.wendys.salesaudit.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Site {
	
	private String siteNum;
    private String area;
    private String address;
    private String city;
    private String state;
    private String coCode;
    private String storeStatus;
    private String siteStatus;
    private boolean dbRowExists;

    public String getSiteNum() {
        return siteNum;
    }

    public void setSiteNum(String siteNum) {
        this.siteNum = siteNum;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCoCode() {
        return coCode;
    }

    public void setCoCode(String coCode) {
        this.coCode = coCode;
    }

    public String getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(String storeStatus) {
        this.storeStatus = storeStatus;
    }
    
    public String getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(String siteStatus) {
        this.siteStatus = siteStatus;
    }

    public boolean isDbRowExists() {
        return dbRowExists;
    }

    public void setDbRowExists(boolean dbRowExists) {
        this.dbRowExists = dbRowExists;
    }
}
