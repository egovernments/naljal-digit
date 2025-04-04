package org.egov.vendor.web.model;

import jakarta.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Validated
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VendorRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;

	@Valid
	@JsonProperty("vendor")
	private Vendor vendor = null;
	
	 

}
