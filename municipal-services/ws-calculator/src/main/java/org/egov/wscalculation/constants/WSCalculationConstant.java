
package org.egov.wscalculation.constants;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.egov.wscalculation.web.models.DemandStatus;

import com.jayway.jsonpath.JsonPath;

public class WSCalculationConstant {

	public static final String TAXPERIOD_MASTER_KEY = "TAXPERIOD";

	public static final String URL_PARAMS_SEPARATER = "?";

	public static final String TENANT_ID_FIELD_FOR_SEARCH_URL = "tenantId=";

	public static final String SEPARATER = "&";

	public static final String SERVICE_FIELD_FOR_SEARCH_URL = "service=";

	public static final String SERVICE_FIELD_VALUE_WS = "WS";
	
	public static final String ONE_TIME_FEE_SERVICE_FIELD = "WS.ONE_TIME_FEE";

	public static final String WS_CONSUMER_CODE_SEPARATOR = ":";

	public static final String FINANCIAL_YEAR_MASTER = "FinancialYear";

	public static final String FINANCIAL_YEAR_RANGE_FEILD_NAME = "finYearRange";

	public static final String MDMS_STARTDATE = "startingDate";

	public static final String MDMS_ENDDATE = "endingDate";

	public static final String MDMS_FINANCIALYEAR = "FinancialYear";

	/*
	 * Module names
	 */

	public static final String FINANCIAL_MODULE = "egf-master";

	/*
	 * billing service field names
	 */

	public static final String CONSUMER_CODE_SEARCH_FIELD_NAME = "consumerCode=";

	public static final String DEMAND_CANCELLED_STATUS = DemandStatus.CANCELLED.toString();

	public static final String MDMS_FINACIALYEAR_PATH = "$.MdmsRes.egf-master.FinancialYear[?(@.code==\"{}\")]";

	public static final String EG_WS_FINANCIAL_MASTER_NOT_FOUND = "EG_WS_FINANCIAL_MASTER_NOT_FOUND";
	public static final String EG_WS_FINANCIAL_MASTER_NOT_FOUND_MSG = "No Financial Year data is available for the given year value of : ";

	public static final String BUSINESSSERVICE_FIELD_FOR_SEARCH_URL = "businessService=";
	public static final String WATER_TAX_SERVICE_CODE = "WS";

	public static final String EMPTY_DEMAND_ERROR_CODE = "EMPTY_DEMANDS";

	public static final String EMPTY_DEMAND_ERROR_MESSAGE = "No demands found for the given bill generate criteria";

	/*
	 * exceptions
	 */
	public static final String EG_WS_INVALID_DEMAND_ERROR = "EG_WS_INVALID_DEMAND_ERROR";
	public static final String EG_WS_INVALID_DEMAND_ERROR_MSG = " Bill cannot be generated for previous assessments in a year, please use the latest assesmment to pay";

	public static final String Assessment_Year = "assessmentYear";

	/**
	 * Time Taxes Config
	 */

	public static final String WS_TIME_INTEREST = "WS_TIME_INTEREST";

	public static final String WS_TIME_PENALTY = "WS_TIME_PENALTY";
	
	public static final String PENALTY_OUTSTANDING = "outstanding";
	
	public static final String OUTSTANDING = "outstandingWithoutPenalty";


	public static final String PENALTY_CURRENT_MONTH = "currentMonth";

	public static final String FIXED = "FIXED";

	public static final String FLAT = "FLAT";


	public static final String WS_WATER_CESS = "WS_WATER_CESS";

	public static final String WS_CHARGE = "10101";
	
	public static final String ADVANCE_COLLECTION = "WS_ADVANCE_CARRYFORWARD";


	public static final String WS_CHARGE_ARREAR = "10102";

	public static final String WS_PENALTY = "10201";

	/**
	 * data fields
	 */
	public static final String FROMFY_FIELD_NAME = "fromFY";

	public static final String ENDING_DATE_APPLICABLES = "endingDay";

	public static final String STARTING_DATE_APPLICABLES = "startingDay";

	public static final String MAX_AMOUNT_FIELD_NAME = "maxAmount";

	public static final String MIN_AMOUNT_FIELD_NAME = "minAmount";

	public static final String FLAT_AMOUNT_FIELD_NAME = "flatAmount";

	public static final String RATE_FIELD_NAME = "rate";

	public static final String DAYS_APPLICABLE_NAME = "applicableAfterDays";
	
	public static final String AMOUNT_FIELD_NAME = "amount";

	public static final String TYPE_FIELD_NAME = "type";
	
	public static final String SUBTYPE_FIELD_NAME = "subType";



	/*
	 * bigdecimal values
	 */

	public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	public static final String WC_REBATE_MASTER = "Rebate";

	public static final String FINANCIALYEAR_MASTER_KEY = "2019-20";

	public static final String TAXHEADMASTER_MASTER_KEY = "WS_TAX";

	public static final String WS_Round_Off = "WS_Round_Off";
	
	public static final String WS_ONE_TIME_FEE_ROUND_OFF = "WS_FEE_ROUND_OFF";

	public static final String WS_TAX_MODULE = "ws-services-calculation";

	public static final String WS_MODULE = "ws-services-masters";

	public static final String WC_PENANLTY_MASTER = "Penalty";

	public static final String WC_WATER_CESS_MASTER = "WaterCess";

	public static final String WC_INTEREST_MASTER = "Interest";

	public static final String WC_BILLING_SLAB_MASTER = "WCBillingSlab";

	public static final List<String> WS_BILLING_SLAB_MASTERS = Collections
			.unmodifiableList(Arrays.asList(WC_BILLING_SLAB_MASTER));

	public static final List<String> TAX_APPLICABLE = Collections.unmodifiableList(Arrays.asList(WS_CHARGE,WS_CHARGE_ARREAR,WS_PENALTY));

	public static final String flatRateCalculationAttribute = "Flat";

	public static final String meteredConnectionType = "Metered";

	public static final String nonMeterdConnection = "Non_Metered";

	public static final String noOfTapsConst = "No. of taps";

	public static final String pipeSizeConst = "Pipe Size";

	public static final String BILLING_PERIOD = "billingPeriod";

	public static final String ConnectionType = "connectionType";

	public static final String JSONPATH_ROOT_FOR_BilingPeriod = "$.MdmsRes.ws-services-masters.billingPeriod";


	public static final String JSONPATH_ROOT_FOR_mdmsRes = "$.MdmsRes";

	public static final String JSONPATH_ROOT_FOR_billingService = "$.MdmsRes.BillingService";
	public static final String JSONPATH_ROOT_FOR_Allowed_PAyment = "$.MdmsRes.BillingService.PaymentService";

	public static final String JSON_PATH_ROOT_FOR_Allowed_PAyment = "$.MdmsRes.BillingService.BusinessService";

	public static final String Quaterly_Billing_Period = "quarterly";

	public static final String Monthly_Billing_Period = "monthly";

	public static final String Billing_Cycle_String = "billingCycle";

	public static final String Payment_Modes_Not_Allowed = "collectionModesNotAllowed";

	public static final String Demand_End_Date_String = "demandEndDateMillis";

	public static final String Demand_Expiry_Date_String = "demandExpiryDate";

	public static final String Demand_Generate_Date_String = "demandGenerationDateMillis";

	public static final String NOTIFICATION_LOCALE = "en_IN";

	public static final String MODULE = "mgramseva-ws";

	public static final String SMS_RECIEVER_MASTER = "SMSReceiver";

	public static final String DEMAND_SUCCESS_MESSAGE_SMS = "WATER_CONNECTION_DEMAND_SUCCESSFUL_SMS_MESSAGE";

	public static final String DEMAND_FAILURE_MESSAGE_SMS = "WATER_CONNECTION_DEMAND_FAILURE_SMS_MESSAGE";

	public static final String DEMAND_SUCCESS_MESSAGE_EMAIL = "WATER_CONNECTION_DEMAND_SUCCESSFUL_EMAIL_MESSAGE";

	public static final String DEMAND_FAILURE_MESSAGE_EMAIL = "WATER_CONNECTION_DEMAND_FAILURE_EMAIL_MESSAGE";

	public static final String WATER_CONNECTION_BILL_GENERATION_SMS_MESSAGE = "WATER_CONNECTION_BILL_GENERATION_SMS_MESSAGE";
	
	
	public static final String WATER_CONNECTION_BILL_GENERATION_APP_MESSAGE = "WATER_CONNECTION_BILL_GENERATION_APP_MESSAGE";
	
	public static final String  USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";

	public static final String  FILTER_PAYMENT_METHOD_SEARCH = "[?(@.code=='WS')]";

	public static final String  USREVENTS_EVENT_NAME = "WATER BILL GENERATION";
	
	public static final String  MONTHLY_DEMAND_GENERATED = "MONTHLY_DEMAND_GENERATED";
	
	public static final String  USREVENTS_EVENT_POSTEDBY = "SYSTEM-WS";
	
	public static final String  Billing_Period_Master = "Billing_Period_Master";
	
	public static final String WC_PLOTSLAB_MASTER = "PlotSizeSlab";
	
	public static final String WC_PROPERTYUSAGETYPE_MASTER = "PropertyUsageType";
	
	public static final String WC_FEESLAB_MASTER = "FeeSlab";
	
	public static final String WC_ROADTYPE_MASTER = "RoadType";
	
	
	
	/**
	 * Fee Estimation Configuration
	 */
	public static final String WS_FORM_FEE = "WS_FORM_FEE";

	public static final String WS_SCRUTINY_FEE = "WS_SCRUTINY_FEE";

	public static final String WS_ONE_TIME_FEE = "WS_ONE_TIME_FEE";

	public static final String WS_ROAD_CUTTING_CHARGE = "WS_ROAD_CUTTING_CHARGE";

	public static final String WS_METER_CHARGE = "WS_METER_CHARGE";
	
	public static final String WS_SECURITY_CHARGE = "WS_SECURITY_CHARGE";
	
	public static final String WS_OTHER_CHARGE = "WS_OTHER_CHARGE";
	
	public static final String WS_TAX_AND_CESS = "WS_TAX_AND_CESS";
	
	public static final String WS_ADHOC_PENALTY = "WS_ADHOC_PENALTY";

	public static final String WS_ADHOC_REBATE = "WS_ADHOC_REBATE";
	
	public static final String FORM_FEE_CONST = "formFee";

	public static final String SCRUTINY_FEE_CONST = "scrutinyFee";
	
	public static final String METER_COST_CONST = "meterCost";
	
	public static final String OTHER_CHARGE_CONST = "other";
	
	public static final String TAX_PERCENTAGE_CONST = "taxpercentage";

	public static final String UNIT_COST_CONST = "unitCost";

	public static final String CALCULATION_ATTRIBUTE_CONST = "CalculationAttribute";
   
	public static final String ATTRIBUTE = "attribute";
	
	public static final String ADHOC_PENALTY = "adhocPenalty";
	
	public static final String ADHOC_REBATE = "adhocRebate";
	
	public static final Long APPLICATION_FEE_DEMAND_END_DATE = 157784760000L;
	
	public static final Long APPLICATION_FEE_DEMAND_EXP_DATE = 220898664000L;
	
	public static final String WS_TIME_ADHOC_PENALTY = "WS_TIME_ADHOC_PENALTY";
	
	public static final String WS_TIME_ADHOC_REBATE = "WS_TIME_ADHOC_REBATE";
	
	public static final String WATER_CONNECTION_BILL_GENERATION_CONSUMER_SMS_MESSAGE = "WATER_CONNECTION_BILL_GENERATION_CONSUMER_SMS_MESSAGE";

	public static final String mGram_Consumer_NewBill = "mGram.Consumer.NewBill";

	public static final String mGram_Consumer_NewDemand = "mGram.GP.MonthlyDemandGenerated";

	public static final String NEW_BULK_DEMAND_EVENT = "NEW_BULK_DEMAND_EVENT";

	public static final String NEW_BULK_DEMAND_EVENT_NM = "NEW_BULK_DEMAND_EVENT_NM";

	public static final String NEW_BULK_DEMAND_EVENT_M = "NEW_BULK_DEMAND_EVENT_M";

	public static final String MSG_KEY = "message";

	public static final String LOCALIZATION_CODES_JSONPATH = "$.messages[0].code";
	public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages[0].message";

	public static final String TEMPLATE_KEY = "templateId";

	public static final String MONTHLY_DEMAND_FAILED = "MONTHLY_DEMAND_FAILED";

	public static final String GENERATE_DEMAND_EVENT = "GENERATE_DEMAND_EN_REMINDER";

	public static final String mGram_Consumer_Bill = "mGram.Consumer.NewBill";

	public static final String mGram_Consumer_Payment = "mGram.consumer.payment.message";

	public static final String mGram_Consumer_Bill_Payment_combine = "mGram.consumer.billAndPayment.N";

	public static final String JSONPATH_ROOT_FOR_ws_service_calculator = "$.MdmsRes.ws-services-calculation";

	public static final String JSONPATH_ROOT_FOR_Penalty = "$.MdmsRes.ws-services-calculation.Penalty";



}
