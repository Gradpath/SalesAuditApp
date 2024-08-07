package com.wendys.salesaudit.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wendys.salesaudit.utility.Constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DateControl {
	
	private Date businessDat;
	
	// Constructor that sets the Date from milliseconds
    public DateControl(Long milliSecBusinessDat) {
        // Construct the Business Date
        if (milliSecBusinessDat > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(milliSecBusinessDat);
            cal.set(Calendar.AM_PM, Calendar.AM);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            this.setBusinessDat(cal.getTime());
        }
    }
    
    // Constructor that sets the Date from a date string.
    public DateControl(String dateString) throws ParseException {
        SimpleDateFormat sdf = Constants.dateDisplayFormat;
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(dateString));
        this.setBusinessDat(cal.getTime());
    }

    // Copy Constructor
    public DateControl(DateControl dc) {
    	this.setBusinessDat(dc.getBusinessDat());
    }
    
    // getters
	public Date getBusinessDat() {return businessDat;}

	// setters
	public void setBusinessDat(Date businessDat) {this.businessDat = businessDat;}

	// convenience getters
	public String getBusinessDatAsDayId() {
		// dayId is in format yyyymmdd
        return Constants.dateDayIdFormat.format(businessDat);		
	}
	
	/**
	 * Return the day of the week as an Int.  Sunday = 1
	 * 
	 * @return
	 */
	public int getDayOfWeek() {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(businessDat);
	    return cal.get(Calendar.DAY_OF_WEEK);
	}

	

}
