package org.egov.naljalcustomisation.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Component
public class CustomisationConfiguration {

    @Value("${egov.wscal.bulk.demand.schedular.topic}")
    private String bulkDemandSchedularTopic;

    @Value("${egov.user.event.notification.enabled}")
    private Boolean isUserEventsNotificationEnabled;

    @Value("${egov.usr.events.create.topic}")
    private String saveUserEventsTopic;

    @Value("${notification.sms.enabled}")
    private Boolean isSMSEnabled;

    // Localization
    @Value("${egov.localization.host}")
    private String localizationHost;

    @Value("${egov.localization.context.path}")
    private String localizationContextPath;

    @Value("${egov.localization.search.endpoint}")
    private String localizationSearchEndpoint;

    @Value("${egov.user.create.path}")
    private String userCreateEndPoint;

    @Value("${egov.user.update.path}")
    private String userUpdateEndPoint;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.ui.path}")
    private String uiPath;

    @Value("${egov.month.revenue.dashboard.link}")
    private String monthRevenueDashboardLink;

    @Value("${egov.url.shortner.host}")
    private String urlShortnerHost;

    @Value("${egov.url.shortner.endpoint}")
    private String urlShortnerEndpoint;

    // SMS
    @Value("${kafka.topics.notification.sms}")
    private String smsNotifTopic;

    @Value("${sms.pending.collection.enabled}")
    private boolean isSMSForPendingCollectionEnabled;

    @Value("${egov.pending.collection.link}")
    private String pendingCollectionLink;

    //MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    @Value("${egov.today.collection.link}")
    private String todayCollectionLink;

    @Value("${sms.todays.collection.enabled}")
    private boolean isSMSForTodaysCollectionEnabled;

    @Value("${egov.user.event.notification.enabled}")
    private Boolean isUserEventEnabled;

    @Value("${egov.monthly.summary.link}")
    private String monthlySummary;

    //USER EVENTS
    @Value("${egov.ui.app.host}")
    private String uiAppHost;

    @Value("${sms.monthy.summary.enabled}")
    private boolean isSmsForMonthlySummaryEnabled;

    @Value("${egov.new.Expenditure.link}")
    private String newExpenditureLink;

    @Value("${egov.expenditure.link}")
    private String expenditureLink;

    @Value("${sms.expenditure.enabled}")
    private boolean isSmsForExpenditureEnabled;

    @Value("${egov.mark.paid.Expenditure.link}")
    private String markPaidExpenditureLink;

    @Value("${egov.expense.bill.markpaid.link}")
    private String expenseBillMarkPaidLink;

    @Value("${sms.expenditure.mark.bill.enabled}")
    private boolean isSmsForMarkBillEnabled;

    @Value("${egov.waterservice.pagination.default.limit}")
    private Integer defaultLimit;

    @Value("${egov.waterservice.pagination.default.offset}")
    private Integer defaultOffset;

    @Value("${egov.waterservice.pagination.max.limit}")
    private Integer maxLimit;

    @Value("${egov.user.host}")
    private String userHost;

    @Value("${state.level.tenant.id}")
    private String stateLevelTenantId;

    @Value("${egov.collection.host}")
    private String collectionHost;

    @Value("${egov.collectiom.payment.search}")
    private String paymentSearch;

    @Value("${egov.ws.fuzzy.searh.is.wildcard}")
    private Boolean isSearchWildcardBased;

    @Value("${egov.ws.search.name.fuziness}")
    private String nameFuziness;

    @Value("${egov.ws.search.mobileNo.fuziness}")
    private String MobileNoFuziness;

    @Value("${egov.ws.search.tenantId.fuziness}")
    private String tenantFuziness;

    @Value("${egov.es.host}")
    private String esHost;

    @Value("${egov.waterservice.es.index}")
    private String esWSIndex;

    @Value("${egov.es.search.endpoint}")
    private String esSearchEndpoint;

    @Value("${egov.indexer.es.username}")
    private String esUsername;

    @Value("${egov.indexer.es.password}")
    private String esPassword;

    @Value("${egov.vendorregistory.default.limit}")
    private Integer defaultVendorLimit;

    @Value("${egov.vendorregistory.default.offset}")
    private Integer defaultVendorOffset;

    @Value("${egov.vendorregistory.max.limit}")
    private Integer maxSearchLimit;

    @Value("${egov.challan.default.limit}")
    private Integer defaultChallanLimit;

    @Value("${egov.challan.default.offset}")
    private Integer defaultChallanOffset;

}
