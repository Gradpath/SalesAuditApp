package com.wendys.salesaudit.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class DataRecord {
	
	private String cashToCount;
	private String salesTax;
    private String localPromo;
    private String nationalPromo;
    private String upgradeKidsMeal;
    private String netSales;
    private String coupons;
    private String grossSales;
    private String discounts;
    private String mgrMeals;
    private String empMeals;
    private String gcSales;
    private String gcRedeemed;
    private String creditSales;
    private String creditRefund;
    private String debitSales;
    private String gstTax;
    private String foodSurcharge;
    private String mgrVoid;
    private String refunds;
    private String mobilePay;
    private String mobilePayRefund;
    private String gcCashBack;
    private String mcx;
    private String localPrePay;
    private String dtfaDeposit;
    private String cashOverShort;
    private String deposits;
    private String taxExempt;
    
    private boolean dbRowExists;

    
    public DataRecord(String initialValue) {
        this.setCashToCount(initialValue);
        this.setSalesTax(initialValue);
        this.setLocalPromo(initialValue);
        this.setNationalPromo(initialValue);
        this.setUpgradeKidsMeal(initialValue);
        this.setNetSales(initialValue);
        this.setCoupons(initialValue);
        this.setGrossSales(initialValue);
        this.setDiscounts(initialValue);
        this.setMgrMeals(initialValue);
        this.setEmpMeals(initialValue);
        this.setGcSales(initialValue);
        this.setGcRedeemed(initialValue);
        this.setCreditSales(initialValue);
        this.setCreditRefund(initialValue);
        this.setDebitSales(initialValue);
        this.setGstTax(initialValue);
        this.setFoodSurcharge(initialValue);
        this.setMgrVoid(initialValue);
        this.setRefunds(initialValue);
        this.setMobilePay(initialValue);
        this.setMobilePayRefund(initialValue);
        this.setGcCashBack(initialValue);
        this.setMcx(initialValue);
        this.setLocalPrePay(initialValue);
        this.setDtfaDeposit(initialValue);
        this.setCashOverShort(initialValue);
        this.setDeposits(initialValue);
        this.setTaxExempt(initialValue);
    }
    
    /**
     * EPY_CREDIT_REFUND is a negative number stored in the DB as a positive number.
     * So flip the sign before writing to the DB.
     */
    public String getCreditRefundFlipped() {
        Double val = Double.parseDouble(this.creditRefund);
        return String.valueOf(val * -1);
    }
    
    /**
     * Recalculate Credit/ePAY cash to count.
     */
    public String getCreditCashToCount() {
        Double val = Double.parseDouble(this.creditSales) + Double.parseDouble(this.creditRefund);
        return String.valueOf(val);
    }
    
    /**
     * MOBL_CSH_BCK_AMT is a negative number stored in the DB as a positive number.
     * So flip the sign before writing to the DB.
     */
    public String getMobilePayRefundFlipped() {
        Double val = Double.parseDouble(this.mobilePayRefund);
        return String.valueOf(val * -1);
    }
    
    /**
     * GIFT_CSH_BCK_AMT is a negative number stored in the DB as a positive number.
     * So flip the sign before writing to the DB.
     */
    public String getGcCashBackFlipped() {
        Double val = Double.parseDouble(this.gcCashBack);
        return String.valueOf(val * -1);
    }
    
    public String getCashToCount() {
		return cashToCount;
	}

	public void setCashToCount(String cashToCount) {
		this.cashToCount = cashToCount;
	}

	public String getSalesTax() {
		return salesTax;
	}

	public void setSalesTax(String salesTax) {
		this.salesTax = salesTax;
	}

	public String getLocalPromo() {
		return localPromo;
	}

	public void setLocalPromo(String localPromo) {
		this.localPromo = localPromo;
	}

	public String getNationalPromo() {
		return nationalPromo;
	}

	public void setNationalPromo(String nationalPromo) {
		this.nationalPromo = nationalPromo;
	}

	public String getUpgradeKidsMeal() {
		return upgradeKidsMeal;
	}

	public void setUpgradeKidsMeal(String upgradeKidsMeal) {
		this.upgradeKidsMeal = upgradeKidsMeal;
	}

	public String getNetSales() {
		return netSales;
	}

	public void setNetSales(String netSales) {
		this.netSales = netSales;
	}

	public String getCoupons() {
		return coupons;
	}

	public void setCoupons(String coupons) {
		this.coupons = coupons;
	}

	public String getGrossSales() {
		return grossSales;
	}

	public void setGrossSales(String grossSales) {
		this.grossSales = grossSales;
	}

	public String getDiscounts() {
		return discounts;
	}

	public void setDiscounts(String discounts) {
		this.discounts = discounts;
	}

	public String getMgrMeals() {
		return mgrMeals;
	}

	public void setMgrMeals(String mgrMeals) {
		this.mgrMeals = mgrMeals;
	}

	public String getEmpMeals() {
		return empMeals;
	}

	public void setEmpMeals(String empMeals) {
		this.empMeals = empMeals;
	}

	public String getGcSales() {
		return gcSales;
	}

	public void setGcSales(String gcSales) {
		this.gcSales = gcSales;
	}

	public String getGcRedeemed() {
		return gcRedeemed;
	}

	public void setGcRedeemed(String gcRedeemed) {
		this.gcRedeemed = gcRedeemed;
	}

	public String getCreditSales() {
		return creditSales;
	}

	public void setCreditSales(String creditSales) {
		this.creditSales = creditSales;
	}

	public String getCreditRefund() {
		return creditRefund;
	}

	public void setCreditRefund(String creditRefund) {
		this.creditRefund = creditRefund;
	}

	public String getDebitSales() {
		return debitSales;
	}

	public void setDebitSales(String debitSales) {
		this.debitSales = debitSales;
	}

	public String getGstTax() {
		return gstTax;
	}

	public void setGstTax(String gstTax) {
		this.gstTax = gstTax;
	}

	public String getFoodSurcharge() {
		return foodSurcharge;
	}

	public void setFoodSurcharge(String foodSurcharge) {
		this.foodSurcharge = foodSurcharge;
	}

	public String getMgrVoid() {
		return mgrVoid;
	}

	public void setMgrVoid(String mgrVoid) {
		this.mgrVoid = mgrVoid;
	}

	public String getRefunds() {
		return refunds;
	}

	public void setRefunds(String refunds) {
		this.refunds = refunds;
	}

	public String getMobilePay() {
		return mobilePay;
	}

	public void setMobilePay(String mobilePay) {
		this.mobilePay = mobilePay;
	}

	public String getMobilePayRefund() {
		return mobilePayRefund;
	}

	public void setMobilePayRefund(String mobilePayRefund) {
		this.mobilePayRefund = mobilePayRefund;
	}

	public String getGcCashBack() {
		return gcCashBack;
	}

	public void setGcCashBack(String gcCashBack) {
		this.gcCashBack = gcCashBack;
	}

	public String getMcx() {
		return mcx;
	}

	public void setMcx(String mcx) {
		this.mcx = mcx;
	}

	public String getLocalPrePay() {
		return localPrePay;
	}

	public void setLocalPrePay(String localPrePay) {
		this.localPrePay = localPrePay;
	}

	public String getDtfaDeposit() {
		return dtfaDeposit;
	}

	public void setDtfaDeposit(String dtfaDeposit) {
		this.dtfaDeposit = dtfaDeposit;
	}

	public String getCashOverShort() {
		return cashOverShort;
	}

	public void setCashOverShort(String cashOverShort) {
		this.cashOverShort = cashOverShort;
	}

	public String getDeposits() {
		return deposits;
	}

	public void setDeposits(String deposits) {
		this.deposits = deposits;
	}

	public String getTaxExempt() {
		return taxExempt;
	}

	public void setTaxExempt(String taxExempt) {
		this.taxExempt = taxExempt;
	}

	public boolean isDbRowExists() {
		return dbRowExists;
	}

	public void setDbRowExists(boolean dbRowExists) {
		this.dbRowExists = dbRowExists;
	}

    
    @Override
	public String toString() {
		return "DataRecord [cashToCount=" + cashToCount + ", salesTax=" + salesTax + ", localPromo=" + localPromo
				+ ", nationalPromo=" + nationalPromo + ", upgradeKidsMeal=" + upgradeKidsMeal + ", netSales=" + netSales
				+ ", coupons=" + coupons + ", grossSales=" + grossSales + ", discounts=" + discounts + ", mgrMeals="
				+ mgrMeals + ", empMeals=" + empMeals + ", gcSales=" + gcSales + ", gcRedeemed=" + gcRedeemed
				+ ", creditSales=" + creditSales + ", creditRefund=" + creditRefund + ", debitSales=" + debitSales
				+ ", gstTax=" + gstTax + ", foodSurcharge=" + foodSurcharge + ", mgrVoid=" + mgrVoid + ", refunds="
				+ refunds + ", mobilePay=" + mobilePay + ", mobilePayRefund=" + mobilePayRefund + ", gcCashBack="
				+ gcCashBack + ", mcx=" + mcx + ", localPrePay=" + localPrePay + ", dtfaDeposit=" + dtfaDeposit
				+ ", cashOverShort=" + cashOverShort + ", deposits=" + deposits + ", taxExempt=" + taxExempt
				+ ", dbRowExists=" + dbRowExists + "]";
	}
}


