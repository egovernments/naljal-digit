package org.egov.naljalcustomisation.web.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueCollectionDataResponse {
    @JsonProperty("RevenueCollectionData")
    private List<RevenueCollectionData> RevenueCollectionData;

    @JsonProperty("responseInfo")
    private ResponseInfo responseInfo = null;
}
