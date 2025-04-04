package org.egov.wscalculation.web.controller;


import java.util.List;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.egov.wscalculation.web.models.*;
import org.egov.wscalculation.service.DemandService;
import org.egov.wscalculation.service.WSCalculationService;
import org.egov.wscalculation.service.WSCalculationServiceImpl;
import org.egov.wscalculation.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Slf4j
@Getter
@Setter
@Builder
@RestController
@RequestMapping("/waterCalculator")
public class CalculatorController {
	
	
	@Autowired
	private DemandService demandService;
	
	@Autowired
	private WSCalculationService wSCalculationService;
	
	@Autowired
	private WSCalculationServiceImpl wSCalculationServiceImpl;
	
	@Autowired
	private final ResponseInfoFactory responseInfoFactory;
	
	@PostMapping("/_estimate")
	public ResponseEntity<CalculationRes> getTaxEstimation(@RequestBody @Valid CalculationReq calculationReq) {
		List<Calculation> calculations = wSCalculationServiceImpl.getEstimation(calculationReq);
		CalculationRes response = CalculationRes.builder().calculation(calculations)
				.responseInfo(
						responseInfoFactory.createResponseInfoFromRequestInfo(calculationReq.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_calculate")
	public ResponseEntity<CalculationRes> calculate(@RequestBody @Valid CalculationReq calculationReq) {
		List<Calculation> calculations = wSCalculationService.getCalculation(calculationReq);
		CalculationRes response = CalculationRes.builder().calculation(calculations)
				.responseInfo(
						responseInfoFactory.createResponseInfoFromRequestInfo(calculationReq.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_updateDemand")
	public ResponseEntity<DemandResponse> updateDemands(@RequestBody @Valid RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute @Valid GetBillCriteria getBillCriteria) {
		List<Demand> demands = demandService.updateDemands(getBillCriteria, requestInfoWrapper);
		DemandResponse response = DemandResponse.builder().demands(demands)
				.responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_bulkDemand")
	public ResponseEntity<BulkDemandResponse> bulkDemand(@Valid @RequestBody BulkDemand bulkDemand) {
		wSCalculationService.generateBulkDemandForTenant(bulkDemand);
		BulkDemandResponse response = BulkDemandResponse.builder().message("Bulk demand generation process stated, you will be notified shortly!")
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(bulkDemand.getRequestInfo(), true)).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
//	@PostMapping("/_jobscheduler/{isSendMessage}")
//	public void jobscheduler(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @PathVariable boolean isSendMessage) {
//		wSCalculationService.generateDemandBasedOnTimePeriod(requestInfoWrapper.getRequestInfo(), isSendMessage);
//	}
	
	@PostMapping("/_applyAdhocTax")
	public ResponseEntity<CalculationRes> applyAdhocTax(@Valid @RequestBody AdhocTaxReq adhocTaxReq) {
		List<Calculation> calculations = wSCalculationServiceImpl.applyAdhocTax(adhocTaxReq);
		CalculationRes response = CalculationRes.builder().calculation(calculations)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(adhocTaxReq.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	@PostMapping("/_getPenaltyDetails")
	public ResponseEntity<DemandPenaltyResponse> getPenaltyDetails(@RequestBody @Valid RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute @Valid GetBillCriteria getBillCriteria) {
		DemandPenaltyResponse demandPenaltyResponse = demandService.getPenaltyDetails(getBillCriteria, requestInfoWrapper);
		demandPenaltyResponse = DemandPenaltyResponse.builder().demands(demandPenaltyResponse.getDemands()).
				totalApplicablePenalty(demandPenaltyResponse.getTotalApplicablePenalty()).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true)).build();
		return new ResponseEntity<>(demandPenaltyResponse, HttpStatus.OK);
	}

	@PostMapping("/_addPenalty")
	public ResponseEntity<HttpStatus> addPenalty(@RequestBody PenaltyRequest penaltyRequest) {
		return demandService.addPenalty(penaltyRequest.getRequestInfo(),penaltyRequest.getAddPenaltyCriteria());

	}

	@PostMapping("/_rollOutDashboardSearch")
	public ResponseEntity<RollOutDashboardResponse> rollOutDashboardSearch(@RequestBody RollOutDashboardRequest rollOutDashboardRequest)
	{
		log.info("Roll out dashboard request"+rollOutDashboardRequest.getRollOutDashboard());
		RollOutDashboard sendDataForRollOut=wSCalculationService.sendDataForRollOut(rollOutDashboardRequest);
		RollOutDashboardResponse response = RollOutDashboardResponse.builder().
				rollOutDashboard(sendDataForRollOut).build();

		return new ResponseEntity<>(response,HttpStatus.OK);
	}

}
