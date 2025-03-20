package org.egov.naljalcustomisation.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.naljalcustomisation.config.CustomisationConfiguration;
import org.egov.naljalcustomisation.repository.builder.CustomisationQueryBuilder;
import org.egov.naljalcustomisation.repository.rowmapper.VendorReportRowMapper;
import org.egov.naljalcustomisation.util.CustomServiceUtil;
import org.egov.naljalcustomisation.web.model.VendorReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class CustomisationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CustomisationQueryBuilder queryBuilder;

    @Autowired
    private CustomServiceUtil customServiceUtil;

    @Autowired
    private CustomisationConfiguration configuration;

    @Autowired
    private VendorReportRowMapper vendorReportRowMapper;

    public List<String> getTenantId() {
        String query = queryBuilder.getDistinctTenantIds();
        log.info("Tenant Id's List Query : " + query);
        return jdbcTemplate.queryForList(query, String.class);
    }

    public List<String> getPendingCollection(String tenantId, String startDate, String endDate) {
        StringBuilder query = new StringBuilder(queryBuilder.PENDINGCOLLECTION);
        query.append(" and DMD.tenantid = '").append(tenantId).append("'")
                .append( " and taxperiodfrom  >= ").append( startDate)
                .append(" and  taxperiodto <= " ).append(endDate);
        log.info("Active pending collection query : " + query);
        return jdbcTemplate.queryForList(query.toString(), String.class);

    }

    public List<Map<String, Object>> getTodayCollection(String tenantId, String startDate, String endDate, String mode) {
        StringBuilder query = new StringBuilder();
        if(mode.equalsIgnoreCase("CASH")) {
            query = new StringBuilder(queryBuilder.PREVIOUSDAYCASHCOLLECTION);
        }else {
            query = new StringBuilder(queryBuilder.PREVIOUSDAYONLINECOLLECTION);
        }
        query.append( " and p.transactiondate  >= ").append( startDate)
                .append(" and  p.transactiondate <= " ).append(endDate).append(" and p.tenantId = '").append(tenantId).append("'");
        log.info("Previous Day collection query : " + query);
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(query.toString());
        return list;
    }

    public List<String>  getPreviousMonthExpensePayments(String tenantId, Long startDate, Long endDate) {
        StringBuilder query = new StringBuilder(queryBuilder.PREVIOUSMONTHEXPPAYMENT);
        query.append( " and PAYMTDTL.receiptdate  >= ").append( startDate)
                .append(" and  PAYMTDTL.receiptdate <= " ).append(endDate).append(" and PAYMTDTL.tenantid = '").append(tenantId).append("'");
        log.info("Previous month expense paid query : " + query);
        return jdbcTemplate.queryForList(query.toString(), String.class);
    }

    public List<String> getPreviousMonthExpenseExpenses(String tenantId, String startDate, String endDate) {
        StringBuilder query = new StringBuilder(queryBuilder.PREVIOUSMONTHEXPENSE);

        query.append(" and challan.paiddate  >= ").append(startDate).append(" and  challan.paiddate <= ")
                .append(endDate).append(" and challan.tenantid = '").append(tenantId).append("'");
        log.info("Previous month expense query : " + query);
        return jdbcTemplate.queryForList(query.toString(), String.class);
    }

    public List<String> getActiveExpenses(String tenantId) {
        StringBuilder query = new StringBuilder(queryBuilder.ACTIVEEXPENSECOUNTQUERY);
        query.append(" and tenantid = '").append(tenantId).append("'");
        log.info("Active expense query : " + query);
        return jdbcTemplate.queryForList(query.toString(), String.class);
    }

    public Integer getTotalPendingCollection(String tenantId, Long endDate) {
        StringBuilder query = new StringBuilder(queryBuilder.PENDINGCOLLECTION);
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(endDate);
        int currentMonthNumber = startDate.get(Calendar.MONTH);
        if (currentMonthNumber < 3) {
            startDate.set(Calendar.YEAR, startDate.get(Calendar.YEAR) - 1);
        }
        startDate.set(Calendar.MONTH,3);
        startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        customServiceUtil.setTimeToBeginningOfDay(startDate);
        query.append(" and  dmd.taxperiodto between " +  startDate.getTimeInMillis() +" and "+ endDate );
        query.append(" and dmd.tenantid = '").append(tenantId).append("'");
        System.out.println("Query in WS for pending collection: " + query.toString());
        return jdbcTemplate.queryForObject(query.toString(), Integer.class);
    }

    public Integer getNewDemand(String tenantId, Long startDate, Long endDate) {
        StringBuilder query = new StringBuilder(queryBuilder.NEWDEMAND);
        query.append(" and dmd.taxperiodto between " + startDate + " and " + endDate)
                .append(" and dmd.tenantId = '").append(tenantId).append("'");
        return jdbcTemplate.queryForObject(query.toString(), Integer.class);

    }

    public Integer getActualCollection(String tenantId, Long startDate, Long endDate) {
        StringBuilder query = new StringBuilder(queryBuilder.ACTUALCOLLECTION);
        query.append(" and py.transactionDate  >= ").append(startDate).append(" and py.transactionDate <= ")
                .append(endDate).append(" and py.tenantId = '").append(tenantId).append("'");
        return jdbcTemplate.queryForObject(query.toString(), Integer.class);

    }

    public List<VendorReportData> getVendorReportData(Long monthStartDateTime, String tenantId, Integer offset, Integer limit)
    {
        StringBuilder vendor_report_query=new StringBuilder(queryBuilder.VENDOR_REPORT_QUERY);

        List<Object> preparedStatement=new ArrayList<>();
        preparedStatement.add(tenantId);
        preparedStatement.add(monthStartDateTime);
//		preparedStatement.add(monthEndDateTime);


        Integer newlimit=configuration.getDefaultVendorLimit();
        Integer newoffset= configuration.getDefaultVendorOffset();

        if(limit==null && offset==null)
            newlimit=configuration.getMaxSearchLimit();
        if(limit!=null && limit<=configuration.getMaxSearchLimit())
            newlimit=limit;
        if(limit!=null && limit>=configuration.getMaxSearchLimit())
            newlimit=configuration.getMaxSearchLimit();

        if(offset!=null)
            newoffset=offset;

        if (newlimit>0){
            vendor_report_query.append(" offset ?  limit ? ;");
            preparedStatement.add(newoffset);
            preparedStatement.add(newlimit);
        }

        log.info("Query of vendor report : "+vendor_report_query.toString()+" prepared statement of vendor report "+ preparedStatement);

        List<VendorReportData> vendorReportDataList=jdbcTemplate.query(vendor_report_query.toString() , preparedStatement.toArray(), vendorReportRowMapper);

        return vendorReportDataList;
    }

}
