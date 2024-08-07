package com.wendys.salesaudit.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FiscalData {
	
	private int yearNum;
    private int periodNum;

    public int getYearNum() {
        return yearNum;
    }

    public void setYearNum(int yearNum) {
        this.yearNum = yearNum;
    }

    public int getPeriodNum() {
        return periodNum;
    }

    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }

}
