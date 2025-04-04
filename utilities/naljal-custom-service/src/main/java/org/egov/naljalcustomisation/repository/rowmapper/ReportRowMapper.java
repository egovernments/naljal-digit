package org.egov.naljalcustomisation.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.egov.naljalcustomisation.web.model.BillReportData;
import org.egov.naljalcustomisation.service.UserService;
import org.egov.naljalcustomisation.web.model.*;
import org.egov.naljalcustomisation.web.model.users.UserDetailResponse;
import org.egov.naljalcustomisation.web.model.users.UserSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReportRowMapper implements ResultSetExtractor<List<BillReportData>> {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserService userService;

    @Override
    public List<BillReportData> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<BillReportData> billReportDataList = new ArrayList<>();
//		BillReportData billReportData = new BillReportData();
//	    Map<String, BillReportData> reportData = new HashMap<>();
        while (rs.next()) {
            BillReportData billReportData = new BillReportData();

            billReportData.setTenantName(rs.getString("tenantId"));
            billReportData.setConnectionNo(rs.getString("connectionNo"));
            billReportData.setOldConnectionNo(rs.getString("oldConnectionNo"));
            billReportData.setUserId(rs.getString("uuid"));
            billReportData.setConsumerCreatedOnDate(rs.getString("connCreatedDate"));
            billReportData.setDemandAmount(rs.getBigDecimal("A10101_DemandAmount"));
            billReportData.setPenalty(rs.getBigDecimal("WS_TIME_PENALTY_DemandAmount"));
            billReportData.setAdvance(rs.getBigDecimal("WS_ADVANCE_CARRYFORWARD_DemandAmount"));
            billReportDataList.add(billReportData);
//					setDemandTypeValue(rs, billReportData, reportData);
        }

//		List<BillReportData> listOfValues = reportData.values().stream().collect( Collectors.toCollection(ArrayList::new));
//		billReportDataList.addAll(listOfValues);
        if(!billReportDataList.isEmpty()){
            enrichConnectionHolderDetails(billReportDataList);
        }
        return billReportDataList;
    }

    private void setDemandTypeValue(ResultSet rs, BillReportData billReportData, Map<String, BillReportData> reportData)
            throws SQLException {
        if(rs.getString("demandType").equalsIgnoreCase("10101")) {
            billReportData.setDemandAmount(rs.getBigDecimal("demandAmount"));
        }
        if(rs.getString("demandType").equalsIgnoreCase("WS_TIME_PENALTY")) {
            billReportData.setPenalty(rs.getBigDecimal("demandAmount"));
        }
        if(rs.getString("demandType").equalsIgnoreCase("WS_ADVANCE_CARRYFORWARD")) {
            billReportData.setAdvance(rs.getBigDecimal("demandAmount"));
        }
        reportData.put(rs.getString("connectionNo"), billReportData);
    }

    private void enrichConnectionHolderDetails(List<BillReportData> billReportDataList) {
        Set<String> connectionHolderIds = new HashSet<>();
        for (BillReportData billReportData : billReportDataList) {
            connectionHolderIds.add(billReportData.getUserId());
        }
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setUuid(connectionHolderIds);
        UserDetailResponse userDetailResponse = userService.getUser(userSearchRequest);
        enrichConnectionHolderInfo(userDetailResponse, billReportDataList);

    }

    private void enrichConnectionHolderInfo(UserDetailResponse userDetailResponse,
                                            List<BillReportData> billReportDataList) {
        List<OwnerInfo> connectionHolderInfos = userDetailResponse.getUser();
        Map<String, OwnerInfo> userIdToConnectionHolderMap = new HashMap<>();
        connectionHolderInfos.forEach(user -> userIdToConnectionHolderMap.put(user.getUuid(), user));
        billReportDataList.forEach(billReportData-> billReportData.setConsumerName(userIdToConnectionHolderMap.get(billReportData.getUserId()).getName()));
    }


}