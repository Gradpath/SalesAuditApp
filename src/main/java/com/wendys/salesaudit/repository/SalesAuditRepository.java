package com.wendys.salesaudit.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wendys.salesaudit.model.AuditEntry;
import com.wendys.salesaudit.model.DataRecord;
import com.wendys.salesaudit.model.DateControl;
import com.wendys.salesaudit.model.FiscalData;
import com.wendys.salesaudit.model.Site;
import com.wendys.salesaudit.utility.SalesAuditUtility;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Component
public class SalesAuditRepository {

    @Autowired
    SalesAuditUtility auditUtility;
    
    @Value("audit.entry.endpoint")
    String auditEntryEndpoint;
    
    @Value("polled.entry.endpoint")
    String polledEntryEndpoint;
    
    @Value("rdc.entry.endpoint")
    String rdcEntryEndpoint;
    
    @Value("site.info.endpoint")
    String siteInfoEndpoint;
    
    @Value("fiscal.call.info.endpoint")
    String fiscalCallInfoEndpoint;
    
    @Value("insert.audit.entry.endpoint")
    String insertAuditEntryEndpoint;
    
    public void insertAuditEntry(AuditEntry auditEntry, String userId, String coCode) {
    	try {
			String accessToken = auditUtility.getAccessToken();
			// Convert POJO to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonNodes = objectMapper.valueToTree(auditEntry.getAuditActual());

            // Additional fields to be added
            Map<String, Object> additionalFields = new HashMap<>();
            additionalFields.put("siteNum", auditEntry.getSiteNum());
            additionalFields.put("coCodeStr", coCode);
            additionalFields.put("busDate", auditEntry.getBusinessDat().getTime());
            additionalFields.put("userID", userId);

            // Add additional fields to the JSON
            additionalFields.forEach(jsonNodes::putPOJO);

            // Convert back to JSON string if needed
            String finalJson = objectMapper.writeValueAsString(jsonNodes);
            
			auditUtility.insertAuditEntryAPIcall(insertAuditEntryEndpoint, accessToken, finalJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void updateSSAXCPEntry(AuditEntry auditEntry, String userId, String coCode) {
        // TODO Auto-generated method stub

    }

    public void updateAuditEntry(AuditEntry auditEntry, String userId) {
        // TODO Auto-generated method stub

    }

    public void deleteAuditEntry(@Valid @NotBlank String selectedRestaurant, DateControl dateControl,
                                 boolean dbRowExists) {
        // TODO Auto-generated method stub

    }

    public void insertAuditDelete(@Valid @NotBlank String selectedRestaurant, Date busDate) {
        // TODO Auto-generated method stub

    }

    public void insertPolledDelete(@Valid @NotBlank String selectedRestaurant, Date busDate) {
        // TODO Auto-generated method stub

    }

    public void deletePolledEntry(@Valid @NotBlank String selectedRestaurant, Date busDate) {
        // TODO Auto-generated method stub

    }

    public void deleteAuditEntry(@Valid @NotBlank String selectedRestaurant, Date busDate) {
        // TODO Auto-generated method stub

    }

    public FiscalData getFiscalCalInfo(Date businessDat) {
    	FiscalData fiscalData = new FiscalData();
		try {
			String accessToken = auditUtility.getAccessToken();
			fiscalData = auditUtility.getFiscalCallInfoAPIcall(fiscalCallInfoEndpoint, accessToken, businessDat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fiscalData;
    }

	public DataRecord getAuditEntry(String siteNum, Date businessDat) {
		DataRecord dataRecord = new DataRecord(siteNum);
		try {
			String accessToken = auditUtility.getAccessToken();
			dataRecord = auditUtility.getAuditEntryAPIcall(auditEntryEndpoint, accessToken, siteNum, businessDat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataRecord;
	}

    public DataRecord getPolledEntry(String siteNum, Date businessDat) {
    	DataRecord dataRecord = new DataRecord(siteNum);
		try {
			String accessToken = auditUtility.getAccessToken();
			dataRecord = auditUtility.getAuditEntryAPIcall(polledEntryEndpoint, accessToken, siteNum, businessDat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataRecord;
    }

    public DataRecord getRdcEntry(String siteNum, Date businessDat) {
    	DataRecord dataRecord = new DataRecord(siteNum);
		try {
			String accessToken = auditUtility.getAccessToken();
			dataRecord = auditUtility.getAuditEntryAPIcall(rdcEntryEndpoint, accessToken, siteNum, businessDat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataRecord;
    }

    public Site getSiteInfo(String selectedRestaurant) {
    	Site site = new Site();
        try {
        	String accessToken = auditUtility.getAccessToken();
        	site = auditUtility.getSiteInfoAPIcall(siteInfoEndpoint, accessToken, selectedRestaurant);
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return site;
    }



}