package com.wendys.salesaudit.controller;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.tomcat.util.buf.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wendys.salesaudit.model.AuditEntry;
import com.wendys.salesaudit.model.DateControl;
import com.wendys.salesaudit.model.SalesAuditResponse;
import com.wendys.salesaudit.model.Site;
import com.wendys.salesaudit.model.User;
import com.wendys.salesaudit.service.SalesAuditService;
import com.wendys.salesaudit.utility.Constants;
import com.wendys.salesaudit.utility.SalesAuditUtility;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
@RequestMapping("/salesAudit")
public class SalesAuditController implements Serializable {

	private static final long serialVersionUID = -5012788994941694997L;
	private static final Logger Logger = LoggerFactory.getLogger(SalesAuditController.class);

	@Autowired
	SalesAuditUtility salesAuditUtility;

	@Autowired
	SalesAuditService salesAuditService;

	@GetMapping("/load")
	public SalesAuditResponse loadSalesAudit(
			@Valid @NotBlank @RequestParam(name = "selectedRestaurant") String selectedRestaurant,
			@Valid @NotBlank @RequestParam(name = "selectedDate") String selectedDate) throws ParseException {
		Logger.debug("SalesAuditController:loadSalesAudit()");
		Logger.debug("selectedRestaurant = " + selectedRestaurant);
		Logger.debug("selectedDate = " + selectedDate);
		String validateResponse = null;
		validateResponse = salesAuditUtility.validateSalesAuditData(selectedDate, selectedRestaurant);
		if (validateResponse.equalsIgnoreCase("T") || validateResponse.equalsIgnoreCase("F")) {
			Site site = salesAuditService.getSiteInfo(selectedRestaurant);
			String validateSiteResponse = salesAuditUtility.validateAfterSite(site);
			if (validateSiteResponse.isEmpty()) {
				AuditEntry auditEntry = salesAuditService.getAuditEntry(selectedRestaurant, new DateControl(selectedDate));
				validateResponse = salesAuditUtility.validatePostAuditEntry(selectedDate, validateResponse);
				if (validateResponse.isEmpty()) {
					return salesAuditUtility.createSuccessResponse(auditEntry, site);
				}
				Logger.debug(
						"auditEntry.getAuditActual().getDbRowExists = " + auditEntry.getAuditActual().isDbRowExists());
				return salesAuditUtility.createFailureResponseWithAuditEntry(auditEntry, validateResponse);
			}else {
				validateResponse = validateSiteResponse;
			}
		}
		return salesAuditUtility.createFailureResponse(validateResponse);
	}

	@PostMapping("/reset")
	public SalesAuditResponse resetSalesAudit(
			@Valid @NotBlank @RequestParam(name = "selectedRestaurant") String selectedRestaurant,
			@Valid @NotBlank @RequestParam(name = "selectedDate") String selectedDate) throws ParseException {
		Logger.debug("SalesAuditController:resetSalesAudit()");
		return loadSalesAudit(selectedRestaurant, selectedDate);
	}

	@PostMapping("/save")
	public SalesAuditResponse saveSalesAudit(
			@Valid @NotBlank @RequestParam(name = "selectedRestaurant") String selectedRestaurant,
			@Valid @NotBlank @RequestParam(name = "selectedDate") String selectedDate, AuditEntry auditEntry, Site site,
			User user) throws ParseException {
		Logger.debug("SalesAuditController:saveSalesAudit()");
		Logger.debug("auditEntry.getSiteNum() = " + auditEntry.getSiteNum());
		Logger.debug("auditEntry.getBusinessDat() = " + auditEntry.getBusinessDat());
		if (auditEntry.getAuditActual() != null) {
			Logger.debug(
					"auditEntry.getAuditActual().getDbRowExists() = " + auditEntry.getAuditActual().isDbRowExists());
		} else {
			Logger.debug("auditEntry.getAuditActual() == null");
		}

		Logger.debug("site = " + site);
		if (!selectedRestaurant.isEmpty() && !selectedDate.isEmpty()) {
			if (auditEntry.getAuditActual().isDbRowExists()) {
				// Updating existing data.
				salesAuditService.updateAuditEntry(auditEntry, user.getUserId().toUpperCase());
			} else {
				// Insert new record.
				salesAuditService.insertAuditEntry(auditEntry, user.getUserId().toUpperCase(), site.getCoCode());
			}
		}
		return loadSalesAudit(selectedRestaurant, selectedDate);
	}

	@PostMapping("/delete")
	public SalesAuditResponse deleteSalesAudit(
			@Valid @NotBlank @RequestParam(name = "selectedRestaurant") String selectedRestaurant,
			@Valid @NotBlank @RequestParam(name = "selectedDate") String selectedDate, AuditEntry auditEntry) throws ParseException {
		Logger.debug( "SalesAuditController:deleteSalesAudit()");
		Logger.debug( "auditEntry.getSiteNum() = " + auditEntry.getSiteNum());
		Logger.debug( "auditEntry.getBusinessDat() = " + auditEntry.getBusinessDat());
		return salesAuditService.deleteSalesAudit(selectedDate,selectedRestaurant,auditEntry);
	}

	@PostMapping("/validate")
	public SalesAuditResponse validateSalesAudit(
			@Valid @NotBlank @RequestParam(name = "selectedRestaurant") String selectedRestaurant,
			@Valid @NotBlank @RequestParam(name = "selectedDate") String selectedDate, AuditEntry auditEntry) {
		Logger.debug( "SalesAuditController:validateSalesAudit()");
		Logger.debug( "validate auditEntry =" + ((auditEntry == null) ? "null" : auditEntry));
		if ((auditEntry == null) || (auditEntry.getAuditActual() == null)) {
			Logger.warn(  "auditEntry is null, skip validate");
			return new SalesAuditResponse();
		}

		return salesAuditService.validateSalesAudit(selectedDate,selectedRestaurant,auditEntry);
	}

}