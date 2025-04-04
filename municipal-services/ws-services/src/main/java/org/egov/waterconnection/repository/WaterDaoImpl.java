package org.egov.waterconnection.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.egov.waterconnection.config.WSConfiguration;
import org.egov.waterconnection.constants.WCConstants;
import org.egov.waterconnection.producer.WaterConnectionProducer;
import org.egov.waterconnection.repository.builder.WsQueryBuilder;
import org.egov.waterconnection.repository.rowmapper.BillingCycleRowMapper;
import org.egov.waterconnection.repository.rowmapper.FeedbackRowMapper;
import org.egov.waterconnection.repository.rowmapper.OpenWaterRowMapper;
import org.egov.waterconnection.repository.rowmapper.WaterRowMapper;
import org.egov.waterconnection.web.models.BillingCycle;
import org.egov.waterconnection.web.models.Feedback;
import org.egov.waterconnection.web.models.FeedbackSearchCriteria;
import org.egov.waterconnection.web.models.SearchCriteria;
import org.egov.waterconnection.web.models.WaterConnection;
import org.egov.waterconnection.web.models.WaterConnectionRequest;
import org.egov.waterconnection.web.models.WaterConnectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WaterDaoImpl implements WaterDao {

	@Autowired
	private WaterConnectionProducer waterConnectionProducer;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private WsQueryBuilder wsQueryBuilder;

	@Autowired
	private WaterRowMapper waterRowMapper;

	@Autowired
	private OpenWaterRowMapper openWaterRowMapper;
	
	@Autowired
	private WSConfiguration wsConfiguration;

	@Value("${egov.waterservice.createwaterconnection.topic}")
	private String createWaterConnection;

	@Value("${egov.waterservice.updatewaterconnection.topic}")
	private String updateWaterConnection;
	
	@Autowired
	private BillingCycleRowMapper billingCycleRowMapper;

	@Autowired
    private FeedbackRowMapper feedbackRowMapper;
	
	@Override
	public void saveWaterConnection(WaterConnectionRequest waterConnectionRequest) {
		waterConnectionProducer.push(createWaterConnection, waterConnectionRequest);
	}

	@Override
	public WaterConnectionResponse getWaterConnectionList(SearchCriteria criteria, RequestInfo requestInfo) {

		List<WaterConnection> waterConnectionList = new ArrayList<>();
		List<Object> preparedStatement = new ArrayList<>();
		Map<String, Long> collectionDataCount = null;
		List<Map<String, Object>> countData = null;
		Boolean flag = null;
		Set<String> consumerCodeSet = null;
		
		String query = wsQueryBuilder.getSearchQueryString(criteria, preparedStatement, requestInfo);

		if (query == null)
			return null;
		
		if(criteria.getIsCollectionCount() != null && criteria.getIsCollectionCount()) {
			List<Object> preparedStmntforCollectionDataCount = new ArrayList<>();
			StringBuilder collectionDataCountQuery = new StringBuilder(wsQueryBuilder.COLLECTION_DATA_COUNT);
			criteria.setIsCollectionDataCount(Boolean.TRUE);
			collectionDataCountQuery = wsQueryBuilder.applyFilters(collectionDataCountQuery, preparedStmntforCollectionDataCount, criteria);
//			collectionDataCountQuery.append(" ORDER BY wc.appCreatedDate  DESC");
		    countData = jdbcTemplate.queryForList(collectionDataCountQuery.toString(), preparedStmntforCollectionDataCount.toArray());
		    if(criteria.getIsBillPaid() != null)
		    	flag = criteria.getIsBillPaid();
		}

		Boolean isOpenSearch = isSearchOpen(requestInfo.getUserInfo());
		WaterConnectionResponse connectionResponse = new WaterConnectionResponse();
		if (isOpenSearch) {
			waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(), openWaterRowMapper);
			connectionResponse = WaterConnectionResponse.builder().waterConnection(waterConnectionList)
					.totalCount(openWaterRowMapper.getFull_count()).build();
		} else {
                log.info("HouseHold Register Query",query);
				log.info("Prepared Statement of household register ",preparedStatement.toArray());
			waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(), waterRowMapper);
			Map<String, Object> counter = new HashMap();
			if (criteria.getIsPropertyCount()!= null && criteria.getIsPropertyCount()) {
				List<Object> preparedStmnt = new ArrayList<>();
				StringBuilder propertyQuery = new StringBuilder(wsQueryBuilder.PROPERTY_COUNT);
				propertyQuery = wsQueryBuilder.applyFilters(propertyQuery, preparedStmnt, criteria);
				propertyQuery.append("GROUP BY additionaldetails->>'propertyType'");
				List<Map<String, Object>> data = jdbcTemplate.queryForList(propertyQuery.toString(),
						preparedStmnt.toArray());
				for (Map<String, Object> map : data) {
					if(map.get("propertytype")!=null) {
						counter.put(map.get("propertytype").toString(), map.get("count").toString()) ;
					}
				}
			}
			collectionDataCount =  getCollectionDataCounter(countData, flag);
			connectionResponse = WaterConnectionResponse.builder().waterConnection(waterConnectionList)
					.totalCount(waterRowMapper.getFull_count()).collectionDataCount(collectionDataCount).propertyCount(counter).build();
		}
		return connectionResponse;
	}

	@Override
	public void updateWaterConnection(WaterConnectionRequest waterConnectionRequest, boolean isStateUpdatable) {
		if (isStateUpdatable) {
			waterConnectionProducer.push(updateWaterConnection, waterConnectionRequest);
		} else {
			waterConnectionProducer.push(wsConfiguration.getWorkFlowUpdateTopic(), waterConnectionRequest);
		}
	}

	/**
	 * push object to create meter reading
	 * 
	 * @param waterConnectionRequest
	 */
	public void postForMeterReading(WaterConnectionRequest waterConnectionRequest) {
		log.info("Posting request to kafka topic - " + wsConfiguration.getCreateMeterReading());
		waterConnectionProducer.push(wsConfiguration.getCreateMeterReading(), waterConnectionRequest);
	}

	/**
	 * push object for edit notification
	 * 
	 * @param waterConnectionRequest
	 */
	public void pushForEditNotification(WaterConnectionRequest waterConnectionRequest) {
		if (!WCConstants.EDIT_NOTIFICATION_STATE
				.contains(waterConnectionRequest.getWaterConnection().getProcessInstance().getAction())) {
			waterConnectionProducer.push(wsConfiguration.getEditNotificationTopic(), waterConnectionRequest);
		}
	}
	
	/**
	 * Enrich file store Id's
	 * 
	 * @param waterConnectionRequest
	 */
	public void enrichFileStoreIds(WaterConnectionRequest waterConnectionRequest) {
		try {
			log.info("ACTION "+waterConnectionRequest.getWaterConnection().getProcessInstance().getAction());
			log.info("ApplicationStatus "+waterConnectionRequest.getWaterConnection().getApplicationStatus());
			if (waterConnectionRequest.getWaterConnection().getApplicationStatus()
					.equalsIgnoreCase(WCConstants.PENDING_APPROVAL_FOR_CONNECTION_CODE)
					|| waterConnectionRequest.getWaterConnection().getProcessInstance().getAction()
					.equalsIgnoreCase(WCConstants.ACTION_PAY)) {
				waterConnectionProducer.push(wsConfiguration.getFileStoreIdsTopic(), waterConnectionRequest);
			}
		} catch (Exception ex) {
			log.debug(ex.toString());
		}
	}
	
	/**
	 * Save file store Id's
	 * 
	 * @param waterConnectionRequest
	 */
	public void saveFileStoreIds(WaterConnectionRequest waterConnectionRequest) {
		waterConnectionProducer.push(wsConfiguration.getSaveFileStoreIdsTopic(), waterConnectionRequest);
	}

	public Boolean isSearchOpen(User userInfo) {

		return userInfo.getType().equalsIgnoreCase("SYSTEM")
				&& userInfo.getRoles().stream().map(Role::getCode).collect(Collectors.toSet()).contains("ANONYMOUS");
	}
	
	public BillingCycle getBillingCycle(String paymentId) {

		String query = WsQueryBuilder.GET_BILLING_CYCLE;

		List<Object> prepareStatementList = new ArrayList<Object>();

		prepareStatementList.add(paymentId);

		List<BillingCycle> billingCycleList = jdbcTemplate.query(query, prepareStatementList.toArray(),
				billingCycleRowMapper);

		return billingCycleList.get(0);
	}

	public List<Feedback> getFeebback(FeedbackSearchCriteria feedbackSearchCriteria) {

		List<Object> preparedStamentValues = new ArrayList<Object>();
		String query = wsQueryBuilder.getFeedback(feedbackSearchCriteria, preparedStamentValues);
		List<Feedback> feedBackList = jdbcTemplate.query(query, preparedStamentValues.toArray(), feedbackRowMapper);
		return feedBackList;
	}
	
	public Map<String, Long> getCollectionDataCounter(List<Map<String, Object>> countDataMap, Boolean flag) {
		Map<String, Long> collectionDataCountMap = new HashMap<>();
		Long paidCount = 0L;
		Long pendingCount = 0L;
		
		if(!CollectionUtils.isEmpty(countDataMap)) {
			for(Map<String, Object> wc : countDataMap) {
				BigDecimal collectionPendingAmount = (BigDecimal)wc.get("pendingamount");
				if(collectionPendingAmount != null ) {
					if(collectionPendingAmount.compareTo(BigDecimal.ZERO) <= 0) {
						++paidCount;
					}
					else {
						++pendingCount;
					}
				}else {
					++paidCount;
				}
			}
			if(flag != null) {
				if(flag) 
					collectionDataCountMap.put("collectionPaid", paidCount);
				else if(!flag) 
					collectionDataCountMap.put("collectionPending", pendingCount);
			}else {
				collectionDataCountMap.put("collectionPaid", paidCount);
				collectionDataCountMap.put("collectionPending", pendingCount);
			}
		}
		return collectionDataCountMap;
	}
	
	@Override
	public List<String> getWCListFuzzySearch(SearchCriteria criteria) {
		List<Object> preparedStatementList = new ArrayList<>();

		String query = wsQueryBuilder.getIds(criteria, preparedStatementList);
		
		try {
			return jdbcTemplate.query(query, preparedStatementList.toArray(), new SingleColumnRowMapper<>());
		}catch (Exception e) {
			log.error("error while getting ids from db: "+e.getMessage());
			throw new CustomException("EG_WC_QUERY_EXCEPTION", "error while getting ids from db");
		}
		
	}
	
	@Override
	public WaterConnectionResponse getWaterConnectionListForPlaneSearch(SearchCriteria criteria, RequestInfo requestInfo) {

		List<WaterConnection> waterConnectionList = new ArrayList<>();
		List<Object> preparedStatement = new ArrayList<>();
		
		Set<String> ids = new HashSet<String>();
		List<String> connectionIds = null;
		if (criteria.getIds() != null && !criteria.getIds().isEmpty())
			ids = criteria.getIds();
		else
			connectionIds = fetchWaterConIds(criteria);

		if(connectionIds!=null && connectionIds.size()>0) {
//		for (String id : connectionIds) {
			ids.addAll(connectionIds);
//		}
		}
		if (ids.isEmpty())
			return new WaterConnectionResponse();

		criteria.setIds(ids);
		
		String query = wsQueryBuilder.getSearchQueryStringForPlaneSearch(criteria, preparedStatement, requestInfo);

		if (query == null)
			return null;
		
		Boolean isOpenSearch = isSearchOpen(requestInfo.getUserInfo());
		WaterConnectionResponse connectionResponse = new WaterConnectionResponse();
		if (isOpenSearch) {
			waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(), openWaterRowMapper);
			connectionResponse = WaterConnectionResponse.builder().waterConnection(waterConnectionList)
					.totalCount(openWaterRowMapper.getFull_count()).build();
		} else {
			waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(), waterRowMapper);
			connectionResponse = WaterConnectionResponse.builder().waterConnection(waterConnectionList)
					.totalCount(waterRowMapper.getFull_count()).build();
		}
		return connectionResponse;
	}
	
	

	public List<String> fetchWaterConIds(SearchCriteria criteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		preparedStmtList.add(criteria.getOffset());
		preparedStmtList.add(criteria.getLimit());

		List<String> ids = jdbcTemplate.query("SELECT id from eg_ws_connection ORDER BY createdtime offset " +
						" ? " +
						"limit ? ",
				preparedStmtList.toArray(),
				new SingleColumnRowMapper<>(String.class));
		return ids;
	}


	public List<WaterConnection> getWSPlainSearch(SearchCriteria criteria, RequestInfo requestInfo) {
		if(criteria.getIds() == null || criteria.getIds().isEmpty())
			throw new CustomException("PLAIN_SEARCH_ERROR", "Search only allowed by ids!");

		List<WaterConnection> waterConnectionList = new ArrayList<>();

		List<Object> preparedStmtList = new ArrayList<>();
		String query = wsQueryBuilder.getSearchQueryStringForPlaneSearch(criteria, preparedStmtList, requestInfo);
		log.info("Query: "+query);
		log.info("PS: "+preparedStmtList);
		Boolean isOpenSearch = isSearchOpen(requestInfo.getUserInfo());
		WaterConnectionResponse connectionResponse = new WaterConnectionResponse();
		if (isOpenSearch) {
			waterConnectionList = jdbcTemplate.query(query, preparedStmtList.toArray(), openWaterRowMapper);
			connectionResponse = WaterConnectionResponse.builder().waterConnection(waterConnectionList)
					.totalCount(openWaterRowMapper.getFull_count()).build();
		} else {
			waterConnectionList = jdbcTemplate.query(query, preparedStmtList.toArray(), waterRowMapper);
			connectionResponse = WaterConnectionResponse.builder().waterConnection(waterConnectionList)
					.totalCount(waterRowMapper.getFull_count()).build();
		}
		
		return connectionResponse.getWaterConnection();
		
	}
	

	
	public Integer getPendingCollectionAmountTillDate(@Valid SearchCriteria criteria) {
		StringBuilder query = new StringBuilder(wsQueryBuilder.PENDINGCOLLECTIONTILLDATE);
		query.append(" and dmd.taxperiodto <= " +  criteria.getToDate())
				.append(" and dmd.tenantId = '").append(criteria.getTenantId()).append("'");
		log.info("Active pending collection query : " + query);
		return jdbcTemplate.queryForObject(query.toString(), Integer.class);

	}
	
	public BigDecimal getArrearsAmount(@Valid SearchCriteria criteria) {
		StringBuilder query = new StringBuilder(wsQueryBuilder.PENDINGCOLLECTION);
		long prevMonthEndDate =  criteria.getFromDate()-1;
		query.append(" and dmd.taxperiodto <= " + prevMonthEndDate)
				.append(" and dmd.tenantId = '").append(criteria.getTenantId()).append("'");
		log.info("Arrears Amount Final Query : " + query);
		return jdbcTemplate.queryForObject(query.toString(), BigDecimal.class);

	}
}
