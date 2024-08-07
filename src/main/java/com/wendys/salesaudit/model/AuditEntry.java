package com.wendys.salesaudit.model;

import java.util.Date;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuditEntry {

    private String siteNum;
    private Date businessDat;
    private DataRecord auditActual;
    private DataRecord polledActual;
    private DataRecord rdcActual;

    // Getters and setters for each field
    public String getSiteNum() {
        return siteNum;
    }

    public void setSiteNum(String siteNum) {
        this.siteNum = siteNum;
    }

    public Date getBusinessDat() {
        return businessDat;
    }

    public void setBusinessDat(Date businessDat) {
        this.businessDat = businessDat;
    }

    public DataRecord getAuditActual() {
        return auditActual;
    }

    public void setAuditActual(DataRecord auditActual) {
        this.auditActual = auditActual;
    }

    public DataRecord getPolledActual() {
        return polledActual;
    }

    public void setPolledActual(DataRecord polledActual) {
        this.polledActual = polledActual;
    }

    public DataRecord getRdcActual() {
        return rdcActual;
    }

    public void setRdcActual(DataRecord rdcActual) {
        this.rdcActual = rdcActual;
    }
}

