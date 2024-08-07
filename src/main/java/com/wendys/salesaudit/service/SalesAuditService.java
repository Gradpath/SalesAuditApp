package com.wendys.salesaudit.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wendys.salesaudit.model.AuditEntry;
import com.wendys.salesaudit.model.DataRecord;
import com.wendys.salesaudit.model.DateControl;
import com.wendys.salesaudit.model.FiscalData;
import com.wendys.salesaudit.model.SalesAuditResponse;
import com.wendys.salesaudit.model.Site;
import com.wendys.salesaudit.repository.SalesAuditRepository;
import com.wendys.salesaudit.utility.Constants;
import com.wendys.salesaudit.utility.SalesAuditUtility;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Service
public class SalesAuditService {

	private static final Logger Logger = LoggerFactory.getLogger(SalesAuditService.class);

	@Autowired
	SalesAuditRepository salesAuditRepository;

	@Autowired
	SalesAuditUtility salesAuditUtility;

	@Value("label_suffix_char")
	String label_suffix_char;

	@Value("cash.to.count.field.label")
	String cash_to_count_field_label;

	@Value("sales.tax.field.label")
	String sales_tax_field_label;

	@Value("localpromo.field.label")
	String localpromo_field_label;

	@Value("${nationalpromo.field.label}")
	private String nationalPromoLabel;

	@Value("${kmupgrade.field.label}")
	private String kmUpgradeLabel;

	@Value("${coupons.field.label}")
	private String couponsLabel;

	@Value("${discounts.field.label}")
	private String discountsLabel;

	@Value("${manager.meals.field.label}")
	private String managerMealsLabel;

	@Value("${employee.meals.field.label}")
	private String employeeMealsLabel;

	@Value("${gift.sales.field.label}")
	private String giftSalesLabel;

	@Value("${gift.redeemed.field.label}")
	private String giftRedeemedLabel;

	@Value("${credit.sales.field.label}")
	private String creditSalesLabel;

	@Value("${credit.refund.field.label}")
	private String creditRefundLabel;

	@Value("${debt.sales.field.label}")
	private String debtSalesLabel;

	@Value("${food.surcharge.field.label}")
	private String foodSurchargeLabel;

	@Value("${manager.void.field.label}")
	private String managerVoidLabel;

	@Value("${refunds.field.label}")
	private String refundsLabel;

	@Value("${mobile.pay.field.label}")
	private String mobilePayLabel;

	@Value("${mobile.pay.refund.field.label}")
	private String mobilePayRefundLabel;

	@Value("${gift.card.cash.back.field.label}")
	private String giftCardCashBackLabel;

	@Value("${mcx.field.label}")
	private String mcxLabel;

	@Value("${local.prepay.field.label}")
	private String localPrePayLabel;

	@Value("${dtfadeposits.field.label}")
	private String dtfaDepositsLabel;

	@Value("${tax.exempt.field.label}")
	private String taxExemptLabel;

	public FiscalData getFiscalCalInfo(Date businessDat) {
		return salesAuditRepository.getFiscalCalInfo(businessDat);
	}

	public Site getSiteInfo(String selectedRestaurant) {
		return salesAuditRepository.getSiteInfo(selectedRestaurant);
	}

	public AuditEntry getAuditEntry(String siteNum, DateControl dateControl) {
		AuditEntry auditEntry = new AuditEntry();
		auditEntry.setSiteNum(siteNum);
		auditEntry.setBusinessDat(dateControl.getBusinessDat());

		auditEntry.setAuditActual(salesAuditRepository.getAuditEntry(siteNum, dateControl.getBusinessDat()));
		auditEntry.setPolledActual(salesAuditRepository.getPolledEntry(siteNum, dateControl.getBusinessDat()));
		auditEntry.setRdcActual(salesAuditRepository.getRdcEntry(siteNum, dateControl.getBusinessDat()));
		return auditEntry;
	}

	public void updateAuditEntry(AuditEntry auditEntry, String userId) {
		DataRecord dr = auditEntry.getAuditActual();

		// Calculate Adjusted Net Sales.
		Double sum = this.getAdjustedNetSales(dr);
		dr.setNetSales(sum.toString());

		// Calculate Gross Sales.
		sum = this.getGrossSales(dr);
		dr.setGrossSales(sum.toString());

		// Calculate Cash Over Short.
		sum = this.getCashOverShort(dr);
		dr.setCashOverShort(sum.toString());

		salesAuditRepository.updateAuditEntry(auditEntry, userId);
	}

	public void insertAuditEntry(AuditEntry auditEntry, String userId, String coCode) {
		DataRecord dr = auditEntry.getAuditActual();

		// Calculate Adjusted Net Sales.
		Double sum = this.getAdjustedNetSales(dr);
		dr.setNetSales(sum.toString());

		// Calculate Gross Sales.
		sum = this.getGrossSales(dr);
		dr.setGrossSales(sum.toString());

		// Calculate Cash Over Short.
		sum = this.getCashOverShort(dr);
		dr.setCashOverShort(sum.toString());

		salesAuditRepository.insertAuditEntry(auditEntry, userId, coCode);
		salesAuditRepository.updateSSAXCPEntry(auditEntry, userId, coCode);
	}

	/**
	 * For the given DataRecord, calculate the Cash Over Short value.
	 *
	 * @param dr
	 * @return Double
	 */
	private Double getCashOverShort(DataRecord dr) {
		Double sum = Double.valueOf(dr.getDeposits()) - Double.valueOf(dr.getCashToCount());

		return sum;
	}

	/**
	 * For the given DataRecord, calculate the AdjustedNetSales value.
	 *
	 * @param dr
	 * @return
	 */
	private Double getAdjustedNetSales(DataRecord dr) {
		Double sum = Double.valueOf(dr.getCashToCount()) + Double.valueOf(dr.getCreditSales())
				+ Double.valueOf(dr.getDebitSales()) + Double.valueOf(dr.getGcRedeemed()) + Double.valueOf(dr.getMcx())
				+ Double.valueOf(dr.getMobilePay()) - Double.valueOf(dr.getSalesTax())
				- Double.valueOf(dr.getUpgradeKidsMeal()) - Double.valueOf(dr.getGcSales())
				- Double.valueOf(dr.getDtfaDeposit()) - Double.valueOf(dr.getLocalPromo())
				- Double.valueOf(dr.getNationalPromo()) - Double.valueOf(dr.getFoodSurcharge());

		return sum;
	}

	/**
	 * For the given DataRecord, calculate the Gross Sales.
	 *
	 * @param dr
	 * @return Double
	 */
	private Double getGrossSales(DataRecord dr) {
		Double sum = Double.valueOf(dr.getNetSales()) + Double.valueOf(dr.getMgrMeals())
				+ Double.valueOf(dr.getEmpMeals()) + Double.valueOf(dr.getCoupons())
				+ Double.valueOf(dr.getLocalPromo()) + Double.valueOf(dr.getNationalPromo())
				+ Double.valueOf(dr.getDiscounts()) + Double.valueOf(dr.getSalesTax())
				+ Double.valueOf(dr.getUpgradeKidsMeal()) + Double.valueOf(dr.getGcSales())
				+ Double.valueOf(dr.getFoodSurcharge());

		return sum;
	}

	public SalesAuditResponse deleteSalesAudit(@Valid @NotBlank String selectedDate,
											   @Valid @NotBlank String selectedRestaurant, AuditEntry auditEntry) throws ParseException {
		SalesAuditResponse salesAuditResponse = new SalesAuditResponse();
		if (auditEntry.getAuditActual() != null) {
			Logger.debug(
					"auditEntry.getAuditActual().getDbRowExists() = " + auditEntry.getAuditActual().isDbRowExists());
		} else {
			Logger.debug("auditEntry.getAuditActual() == null");
		}
		if (auditEntry.getPolledActual() != null) {
			Logger.debug(
					"auditEntry.getPolledActual().getDbRowExists() = " + auditEntry.getPolledActual().isDbRowExists());
		} else {
			Logger.debug("auditEntry.getPolledActual() == null");
		}
		if (!selectedRestaurant.isEmpty() && !selectedDate.isEmpty()) {
			// No audit record, do not try and delete. Return and display message.
			if (!auditEntry.getAuditActual().isDbRowExists()) {
				salesAuditResponse.setErrorMsg(new ArrayList<>(List.of("\"audit.data.none\"")));
				return salesAuditResponse;
			}

			// Delete associated record from the database..
			DateControl dateControl = new DateControl(selectedDate);
			Date busDate = dateControl.getBusinessDat();

			salesAuditRepository.insertAuditDelete(selectedRestaurant, busDate);

			if (auditEntry.getPolledActual().isDbRowExists()) {
				salesAuditRepository.insertPolledDelete(selectedRestaurant, busDate);
				salesAuditRepository.deletePolledEntry(selectedRestaurant, busDate);
			}

			salesAuditRepository.deleteAuditEntry(selectedRestaurant, busDate);
			salesAuditResponse.setActionMsg("message.data.deleted");
		}
		return salesAuditResponse;
	}

	public SalesAuditResponse validateSalesAudit(@Valid @NotBlank String selectedDate,
												 @Valid @NotBlank String selectedRestaurant, AuditEntry auditEntry) {
		String labelSuffix = label_suffix_char;
		SalesAuditResponse salesAuditResponse = new SalesAuditResponse();
		if (!selectedRestaurant.isEmpty() && !selectedDate.isEmpty()) {
			// Validate dollar amounts.
			Map<String, String> auditFields = new HashMap<>();
			auditFields.put(cash_to_count_field_label, auditEntry.getAuditActual().getCashToCount());
			auditFields.put(sales_tax_field_label, auditEntry.getAuditActual().getSalesTax());
			auditFields.put(localpromo_field_label, auditEntry.getAuditActual().getLocalPromo());
			auditFields.put(nationalPromoLabel, auditEntry.getAuditActual().getNationalPromo());
			auditFields.put(kmUpgradeLabel,  auditEntry.getAuditActual().getUpgradeKidsMeal());
			auditFields.put(couponsLabel,  auditEntry.getAuditActual().getCoupons());
			auditFields.put(discountsLabel, auditEntry.getAuditActual().getDiscounts());
			auditFields.put(managerMealsLabel, auditEntry.getAuditActual().getMgrMeals());
			auditFields.put(employeeMealsLabel, auditEntry.getAuditActual().getEmpMeals());
			auditFields.put(giftSalesLabel, auditEntry.getAuditActual().getGcSales());
			auditFields.put(giftRedeemedLabel, auditEntry.getAuditActual().getGcRedeemed());
			auditFields.put(creditSalesLabel, auditEntry.getAuditActual().getCreditSales());
			auditFields.put(creditRefundLabel, auditEntry.getAuditActual().getCreditRefund());
			auditFields.put(debtSalesLabel, auditEntry.getAuditActual().getDebitSales());
			auditFields.put(foodSurchargeLabel, auditEntry.getAuditActual().getFoodSurcharge());
			auditFields.put(managerVoidLabel, auditEntry.getAuditActual().getMgrVoid());
			auditFields.put(refundsLabel, auditEntry.getAuditActual().getRefunds());
			auditFields.put(mobilePayLabel, auditEntry.getAuditActual().getMobilePay());
			auditFields.put(mobilePayRefundLabel, auditEntry.getAuditActual().getMobilePayRefund());
			auditFields.put(giftCardCashBackLabel, auditEntry.getAuditActual().getGcCashBack());
			auditFields.put(mcxLabel, auditEntry.getAuditActual().getMcx());
			auditFields.put(localPrePayLabel, auditEntry.getAuditActual().getLocalPrePay());
			auditFields.put(dtfaDepositsLabel, auditEntry.getAuditActual().getDtfaDeposit());
			auditFields.put(taxExemptLabel, auditEntry.getAuditActual().getTaxExempt());

			auditFields.forEach((label, valueSupplier) -> {
				String fieldLabel = StringUtils.removeEnd(label, labelSuffix);
				salesAuditUtility.validateStringDecimal(fieldLabel, valueSupplier, salesAuditResponse);
			});

			// Validate date entered.
			DateTime dt = Constants.dateFormatter.parseDateTime(selectedDate);
			Logger.debug("selectedDate = " + selectedDate);
			Logger.debug("dt.getDayOfYear() = " + dt.getDayOfYear());
			Logger.debug("dt.getYear() = " + dt.getYear());

		} else {
			Logger.debug("Nothing to validate in DailySalesEntry...");
		}
		return salesAuditResponse;
	}

}