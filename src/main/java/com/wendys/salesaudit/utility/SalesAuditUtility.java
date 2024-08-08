package com.wendys.salesaudit.utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.wendys.salesaudit.model.AuditEntry;
import com.wendys.salesaudit.model.DataRecord;
import com.wendys.salesaudit.model.DateControl;
import com.wendys.salesaudit.model.FiscalData;
import com.wendys.salesaudit.model.SalesAuditResponse;
import com.wendys.salesaudit.model.Site;
import com.wendys.salesaudit.service.SalesAuditService;

@Component
public class SalesAuditUtility {

	@Autowired
	SalesAuditService salesAuditService;
	
	@Value("access.token.username")
	public String accessTokenUsername;
	
	@Value("access.token.password")
	public String accessTokenPassword;
	
	@Value("access.token.endpoint")
	public String accessTokenEndpoint;

	private int cutoffHour = 19;

	private static final Logger Logger = LoggerFactory.getLogger(SalesAuditUtility.class);

	public String validateSalesAuditData(String selectedDate, String selectedRestaurant) throws ParseException {
		String isPreviousPeriod = "F";

		// Setup the DateControl object using milliseconds from the date selected.
		DateControl dateControl = new DateControl(selectedDate);
		Logger.debug("dateControl=" + dateControl);

		// Get the Fiscal year and period of the current and requested dates.
		DateControl currentDc = new DateControl(new Date().getTime());
		FiscalData currentFiscalData = salesAuditService.getFiscalCalInfo(currentDc.getBusinessDat());
		FiscalData selectedFiscalData = salesAuditService.getFiscalCalInfo(dateControl.getBusinessDat());

		// Determine the relationship between today's fiscal period and the selected
		// period.
		if (currentFiscalData.getYearNum() == selectedFiscalData.getYearNum()) {
			if ((currentFiscalData.getPeriodNum() - selectedFiscalData.getPeriodNum()) == 1) {
				isPreviousPeriod = "T";
			}
		} else if ((currentFiscalData.getPeriodNum() - selectedFiscalData.getPeriodNum()) == 1) {
			if ((currentFiscalData.getPeriodNum() == 1) && (selectedFiscalData.getPeriodNum() == 12)) {
				// Current period is 1 and selected period is 12 of previous year.
				isPreviousPeriod = "T";
			}
		}

		Logger.debug("currentFiscalData = " + currentFiscalData);
		Logger.debug("selectedFiscalData = " + selectedFiscalData);
		Logger.debug("isPreviousPeriod = " + isPreviousPeriod);

		// If selected date is in the future return error.
		if (currentDc.getBusinessDat().compareTo(dateControl.getBusinessDat()) < 0) {
			isPreviousPeriod = "invalid.date.future";
		}

		return isPreviousPeriod;
	}

	public String validateAfterSite(Site site) {
		// If no site data found return error.
		if (!site.isDbRowExists()) {
			return "invalid.site.noData";
		}

		// If site is International site return error.
		if (site.getSiteStatus().equalsIgnoreCase("IS")) {
			return "invalid.site.international";
		}
		return null;
	}

	public String validatePostAuditEntry(String selectedDate, String isPreviousPeriod) throws ParseException {

		DateControl dateControl = new DateControl(selectedDate);

		//Calculate various dates and times.
		LocalDate todaysDate = new LocalDate();
		DateTime  now        = new DateTime();
		DateTime  tuesCutoff = todaysDate.withDayOfWeek(DateTimeConstants.TUESDAY).toDateTime(new LocalTime(cutoffHour,0));
		DateTime  monCutoff  = todaysDate.withDayOfWeek(DateTimeConstants.MONDAY).toDateTime(new LocalTime(cutoffHour,0));
		LocalDate thisMonday = todaysDate.withDayOfWeek(DateTimeConstants.MONDAY);
		LocalDate previousMonday = todaysDate.minusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY);
		LocalDate jodaSelectedDate = new LocalDate(dateControl.getBusinessDat());

		Logger.debug("todaysDate = " + todaysDate);
		Logger.debug("today is " + todaysDate.dayOfWeek().getAsText());
		Logger.debug("now = " + now);
		Logger.debug("thisMonday = " + thisMonday);
		Logger.debug("previousMonday = " + previousMonday);
		Logger.debug("monCutoff = " + monCutoff);
		Logger.debug("tuesCutoff = " + tuesCutoff);

		// No edits allowed for data before previous week.
		if (jodaSelectedDate.isBefore(previousMonday)) {
			return "invalid.date.previous.week";
		}

		// Previous week data not editable Tuesday after cutoffHour (Monday after
		// cutoffHour on period end).
		if (jodaSelectedDate.isBefore(thisMonday)) {
			if (now.isAfter(tuesCutoff)) {
				return "invalid.date.after.tuesday";
			} else if ((now.isAfter(monCutoff)) && (isPreviousPeriod.equals("T"))) {
				return "invalid.date.after.monday";
			}
		}

		return null;
	}

	public SalesAuditResponse createSuccessResponse(AuditEntry auditEntry, Site site2) {

		SalesAuditResponse salesAuditResponse = new SalesAuditResponse();

		salesAuditResponse.setAllowDataEdits(true);
		// If site selected is franchise, advise.
		if (site2.getSiteStatus().equalsIgnoreCase("FS")) {
			salesAuditResponse.setActionMsg("message.site.franchise");
		}
		salesAuditResponse.setSiteNum(auditEntry.getSiteNum());
		salesAuditResponse.setBusinessDat(auditEntry.getBusinessDat());
		salesAuditResponse.setAuditActual(auditEntry.getAuditActual());
		salesAuditResponse.setPolledActual(auditEntry.getPolledActual());
		salesAuditResponse.setRdcActual(auditEntry.getRdcActual());
		return salesAuditResponse;
	}

	public SalesAuditResponse createFailureResponse(String validateResponse) {
		SalesAuditResponse salesAuditResponse = new SalesAuditResponse();

		salesAuditResponse.setAllowDataEdits(false);
		salesAuditResponse.setErrorMsg(new ArrayList<>(List.of(validateResponse)));
		return salesAuditResponse;
	}

	public SalesAuditResponse createFailureResponseWithAuditEntry(AuditEntry auditEntry, String validateResponse) {
		SalesAuditResponse salesAuditResponse = new SalesAuditResponse();

		salesAuditResponse.setAllowDataEdits(false);
		salesAuditResponse.setErrorMsg(new ArrayList<>(List.of(validateResponse)));
		salesAuditResponse.setSiteNum(auditEntry.getSiteNum());
		salesAuditResponse.setBusinessDat(auditEntry.getBusinessDat());
		salesAuditResponse.setAuditActual(auditEntry.getAuditActual());
		salesAuditResponse.setPolledActual(auditEntry.getPolledActual());
		salesAuditResponse.setRdcActual(auditEntry.getRdcActual());
		return salesAuditResponse;
	}

	public SalesAuditResponse validateStringDecimal(String fieldName, String fieldValue, SalesAuditResponse salesAuditResponse) {
		Logger.debug("validateStringDecimal");
		Logger.debug("fieldName=" + fieldName);
		Logger.debug("fieldValue=" + fieldValue);
		// Check if a fieldValue was keyed
		if (fieldValue != null && !fieldValue.isEmpty()) {

			// Trim the value
			String fieldVal = fieldValue.trim();

			// Convert to validate and remove leading zeros
			Double val = 0.0;
			try {
				val = Double.parseDouble(fieldVal);
			} catch (NumberFormatException e) {
				salesAuditResponse.setErrorMsg(new ArrayList<>(List.of("field.notNumeric")));
				Logger.warn("Exception converting keyed " + fieldName + " to Double. fieldVal=" + fieldVal, e);
				return salesAuditResponse;
			}

			// Make sure fieldValue is not negative
			if (val < 0.0) {
				salesAuditResponse.setErrorMsg(new ArrayList<>(List.of("field.invalidNegative")));
				Logger.warn(fieldName + " is not >= 0 test failed. fieldVal=" + fieldVal);
				return salesAuditResponse;
			}
		} else {
			salesAuditResponse.setErrorMsg(new ArrayList<>(List.of("field.notNumeric")));
			Logger.debug("Field is null/not numeric");
		}
		return salesAuditResponse;
	}

	public String getAccessToken() throws IOException {
		RestTemplate restTemplate = new RestTemplate();

		// Set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Create Basic Auth Header
		String auth = accessTokenUsername + ":" + accessTokenPassword;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
		headers.set("Authorization", "Basic " + encodedAuth);

		// Set request parameters
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("grant_type", "client_credentials");

		// Create request entity
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

		// Make the POST request
		ResponseEntity<String> response = restTemplate.exchange(accessTokenEndpoint, HttpMethod.POST, request,
				String.class);

		// Check for successful response
		if (response.getStatusCode().is2xxSuccessful()) {
			JSONObject jsonResponse = new JSONObject(response.getBody());
			return jsonResponse.getString("access_token");
		} else {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCode());
		}

	}

	public Site getSiteInfoAPIcall(String apiUrl, String accessToken, String selectedRestaurant) throws IOException {
		RestTemplate restTemplate = new RestTemplate();

	    // Set headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(accessToken);

	    // Build the URI with the query parameter
	    String urlWithParams = UriComponentsBuilder.fromHttpUrl(apiUrl)
	            .queryParam("siteNum", selectedRestaurant)
	            .toUriString();

	    // Create request entity with headers only (no body for GET requests)
	    HttpEntity<String> request = new HttpEntity<>(headers);

	    // Make the GET request
	    ResponseEntity<Site> response = restTemplate.exchange(
	            urlWithParams,
	            HttpMethod.GET,
	            request,
	            Site.class
	    );

	    // Check for successful response
	    if (response.getStatusCode().is2xxSuccessful()) {
	        return response.getBody();
	    } else {
	        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCode());
	    }
	}

	public DataRecord getAuditEntryAPIcall(String apiUrl, String accessToken, String siteNum, Date businessDat)
			throws IOException {
		RestTemplate restTemplate = new RestTemplate();

	    // Set headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(accessToken);

	    // Format the date as a string (adjust format as needed)
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    String businessDatStr = dateFormat.format(businessDat);

	    // Build the URI with the query parameters
	    String urlWithParams = UriComponentsBuilder.fromHttpUrl(apiUrl)
	            .queryParam("siteNum", siteNum)
	            .queryParam("businessDat", businessDatStr)
	            .toUriString();

	    // Create request entity with headers only (no body for GET requests)
	    HttpEntity<String> request = new HttpEntity<>(headers);

	    // Make the GET request
	    ResponseEntity<DataRecord> response = restTemplate.exchange(
	            urlWithParams,
	            HttpMethod.GET,
	            request,
	            DataRecord.class
	    );

	    // Check for successful response
	    if (response.getStatusCode().is2xxSuccessful()) {
	        return response.getBody();
	    } else {
	        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCode());
	    }
	}

	public FiscalData getFiscalCallInfoAPIcall(String fiscalCallInfoEndpoint, String accessToken, Date businessDat)
			throws IOException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		String requestJson = "{\"businessDat\": \"" + businessDat + "\"}";
		HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
		ResponseEntity<FiscalData> response = restTemplate.exchange(fiscalCallInfoEndpoint, HttpMethod.POST, request,
				FiscalData.class);
		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody();
		} else {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCode());
		}
	}

	public void insertAuditEntryAPIcall(String insertAuditEntryEndpoint, String accessToken, String finalJson) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		// Create HttpEntity
        HttpEntity<String> request = new HttpEntity<>(finalJson, headers);

        // Send POST request
        ResponseEntity<String> response = restTemplate.exchange(
        		insertAuditEntryEndpoint,
                HttpMethod.POST,
                request,
                String.class
        );

        System.out.println("Response: " + response.getBody());
		
	}

}