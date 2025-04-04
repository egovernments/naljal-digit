package org.egov.naljalcustomisation.service;

import com.jayway.jsonpath.JsonPath;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.naljalcustomisation.config.CustomisationConfiguration;
import org.egov.naljalcustomisation.constants.CustomConstants;
import org.egov.naljalcustomisation.producer.CustomisationProducer;
import org.egov.naljalcustomisation.repository.CustomisationRepository;
import org.egov.naljalcustomisation.repository.ElasticSearchRepository;
import org.egov.naljalcustomisation.repository.ServiceRequestRepository;
import org.egov.naljalcustomisation.util.MdmsUtil;
import org.egov.naljalcustomisation.util.NotificationUtil;
import org.egov.naljalcustomisation.validator.CustomisationServiceValidator;
import org.egov.naljalcustomisation.web.model.*;
import org.egov.naljalcustomisation.web.model.users.UserDetailResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomisationServiceImpl implements CustomisationService {

    @Autowired
    private CustomisationServiceDaoImpl customisationServiceDaoImpl;

    @Autowired
    private CustomisationRepository repository;

    @Autowired
    private CustomisationProducer producer;

    @Autowired
    private CustomisationConfiguration config;

    @Autowired
    private EditNotificationService notificationService;

    @Autowired
    private NotificationUtil util;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MdmsUtil mdmsUtils;

    @Autowired
    private EnrichmentService enrichmentService;

    @Autowired
    private CustomisationServiceValidator customisationServiceValidator;

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    public void generateDemandBasedOnTimePeriod(RequestInfo requestInfo, boolean isSendMessage) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.now();
        log.info("Time schedule start for water demand generation on : " + date.format(dateTimeFormatter));
        List<String> tenantIds = repository.getTenantId();
        if (tenantIds.isEmpty())
            return;
        log.info("Tenant Ids : " + tenantIds.toString());
        tenantIds.forEach(tenantId -> {
            HashMap<Object, Object> demandData = new HashMap<Object, Object>();
            demandData.put("requestInfo", requestInfo);
            demandData.put("tenantId", tenantId);
            demandData.put("isSendMessage", isSendMessage);
            log.info("demand data : " + demandData);
            producer.push(config.getBulkDemandSchedularTopic(), demandData);
//			demandService.generateDemandForTenantId(tenantId, requestInfo);
        });
    }

    public void sendPendingCollectionEvent(RequestInfo requestInfo) {
        LocalDate dayofmonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime scheduleTimeFirst = LocalDateTime.of(dayofmonth.getYear(), dayofmonth.getMonth(),
                dayofmonth.getDayOfMonth(), 10, 0, 0);
        LocalDateTime scheduleTimeSecond = LocalDateTime.of(dayofmonth.getYear(), dayofmonth.getMonth(), 15, 10, 0, 0);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentTime = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter),
                dateTimeFormatter);

        List<String> tenantIds = repository.getTenantId();
        tenantIds.forEach(tenantId -> {
            if (tenantId.split("\\.").length >= 2) {
                if (null != config.getIsUserEventsNotificationEnabled()) {
                    if (config.getIsUserEventsNotificationEnabled()) {
                        EventRequest eventRequest = sendPendingCollectionNotification(requestInfo, tenantId);
                        if (null != eventRequest)
                            notificationService.sendEventNotification(eventRequest);
                    }
                }

                if (null != config.getIsSMSEnabled()) {
                    if (config.getIsSMSEnabled()) {
                        HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo,
                                CustomConstants.PENDING_COLLECTION_SMS, tenantId);
                        HashMap<String, String> gpwscMap = util.getLocalizationMessage(requestInfo, tenantId, tenantId);

                        UserDetailResponse userDetailResponse = userService.getUserByRoleCodes(requestInfo, tenantId,
                                Arrays.asList("GP_ADMIN", "SARPANNCH"));

                        String penColLink = config.getUiPath() + config.getMonthRevenueDashboardLink();
                        Map<String, String> mobileNumberIdMap = new LinkedHashMap<>();

                        for (OwnerInfo userInfo : userDetailResponse.getUser())
                            if (userInfo.getName() != null) {
                                mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getName());
                            } else {
                                mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getUserName());
                            }
                        mobileNumberIdMap.entrySet().stream().forEach(map -> {
                            if (messageMap != null && !StringUtils.isEmpty(messageMap.get(NotificationUtil.MSG_KEY))) {

                                String uuidUsername = map.getValue();
                                String message = formatPendingCollectionMessage(requestInfo, tenantId,
                                        messageMap.get(NotificationUtil.MSG_KEY), new HashMap<>());
                                message = message.replace("{PENDING_COL_LINK}", getShortenedUrl(penColLink));
                                message = message.replace("{GPWSC}",
                                        (gpwscMap != null
                                                && !StringUtils.isEmpty(gpwscMap.get(NotificationUtil.MSG_KEY)))
                                                ? gpwscMap.get(NotificationUtil.MSG_KEY)
                                                : tenantId);
                                message = message.replace("{ownername}", uuidUsername);
                                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                Date today = new Date();
                                String formattedDate = format.format(today);
                                message = message.replace("{Date}", formattedDate);

                                message = message.replace("{Date}", LocalDate.now().toString());
                                log.info("PENDING Coll SMS::" + message);
                                SMSRequest smsRequest = SMSRequest.builder().mobileNumber(map.getKey()).message(message)
                                        .templateId(messageMap.get(NotificationUtil.TEMPLATE_KEY)).tenantId(tenantId)
                                        .users(new String[]{uuidUsername}).build();
                                if (config.isSMSForPendingCollectionEnabled()) {
                                    producer.push(config.getSmsNotifTopic(), smsRequest);
                                }

                            }
                        });
                    }
                }
            }
        });
    }

    public EventRequest sendPendingCollectionNotification(RequestInfo requestInfo, String tenantId) {
        List<ActionItem> items = new ArrayList<>();
        String actionLink = config.getPendingCollectionLink();
        ActionItem item = ActionItem.builder().actionUrl(actionLink).build();
        items.add(item);
        Action action = Action.builder().actionUrls(items).build();
        List<Event> events = new ArrayList<>();
        log.info("Action Link::" + actionLink);

        Map<String, Object> additionalDetailsMap = new HashMap<String, Object>();
        additionalDetailsMap.put("localizationCode", CustomConstants.PENDING_COLLECTION_EVENT);

        HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo, CustomConstants.PENDING_COLLECTION_EVENT,
                tenantId);
        Map<String, Object> financialYear = getFinancialYear(requestInfo, tenantId);
        List<String> pendingCollection = repository.getPendingCollection(tenantId,
                financialYear.get("startingDate").toString(), financialYear.get("endingDate").toString());
        if (null != pendingCollection && pendingCollection.size() > 0 && pendingCollection.get(0) != null && Double.parseDouble(pendingCollection.get(0)) > 0) {
            events.add(Event.builder().tenantId(tenantId)
                    .description(
                            formatPendingCollectionMessage(requestInfo, tenantId, messageMap.get(NotificationUtil.MSG_KEY), additionalDetailsMap))
                    .eventType(CustomConstants.USREVENTS_EVENT_TYPE).name(CustomConstants.PENDING_COLLECTION_USEREVENT).postedBy(CustomConstants.USREVENTS_EVENT_POSTEDBY)
                    .recepient(getRecepient(requestInfo, tenantId)).source(Source.WEBAPP)
                    .recepient(getRecepient(requestInfo, tenantId)).eventDetails(null).actions(action).additionalDetails(additionalDetailsMap).build());
        }
        if (!CollectionUtils.isEmpty(events)) {
            return EventRequest.builder().requestInfo(requestInfo).events(events).build();
        } else {
            return null;
        }

    }

    public String formatPendingCollectionMessage(RequestInfo requestInfo, String tenantId, String message, Map<String, Object> additionalDetailsMap) {

        Map<String, String> attributes = new HashMap<String, String>();

        Map<String, Object> financialYear = getFinancialYear(requestInfo, tenantId);
        List<String> pendingCollection = repository.getPendingCollection(tenantId,
                financialYear.get("startingDate").toString(), financialYear.get("endingDate").toString());
        if (null != pendingCollection && pendingCollection.size() > 0)
            if (message.contains("{PENDING_COLLECTION}")) {
                if (pendingCollection.get(0) != null) {
                    message = message.replace(" {PENDING_COLLECTION} ", pendingCollection.get(0));
                    attributes.put("{PENDING_COLLECTION}", pendingCollection.get(0));
                } else {
                    message = message.replace(" {PENDING_COLLECTION} ", "0");
                    attributes.put("{PENDING_COLLECTION}", "0");
                }
                log.info("Final EVENT MEssage is :" + message);
            } else if (message.contains("{amount}")) {
                if (pendingCollection.get(0) != null) {
                    message = message.replace("{amount}", pendingCollection.get(0));
                    attributes.put("{amount}", pendingCollection.get(0));
                } else {
                    message = message.replace("{amount}", "0");
                    attributes.put("{amount}", "0");
                }
                log.info("Final SMS MEssage is :" + message);
            }
        if (message.contains("{TODAY_DATE}")) {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date today = new Date();
            String formattedDate = format.format(today);
            message = message.replace("{TODAY_DATE}", formattedDate);
            attributes.put("{TODAY_DATE}", formattedDate);
        }
        log.info("Final message is :" + message);

        additionalDetailsMap.put("attributes", attributes);


        return message;
    }

    public Map<String, Object> getFinancialYear(RequestInfo requestInfo, String tenantId) {
        Set<String> financeYears = new HashSet<>(1);
        String financeYear = prepareFinanceYear();
        MdmsCriteriaReq mdmsCriteriaReq = getFinancialYearRequest(requestInfo, financeYear, tenantId);
        StringBuilder url = mdmsUtils.getMdmsSearchUrl();
        Object res = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
        Map<String, Object> financialYearProperties;
        String jsonPath = CustomConstants.MDMS_EGFFINACIALYEAR_PATH.replace("{}", financeYear);
        try {
            List<Map<String, Object>> jsonOutput = JsonPath.read(res, jsonPath);
            financialYearProperties = jsonOutput.get(0);

        } catch (IndexOutOfBoundsException e) {
            throw new CustomException("EXP_FIN_NOT_FOUND", "Financial year not found: " + financeYears);
        }

        System.out.println("Financial year" + financialYearProperties);
        return financialYearProperties;
    }

    public String prepareFinanceYear() {
        LocalDateTime localDateTime = LocalDateTime.now();
        int currentMonth = localDateTime.getMonthValue();
        String financialYear;
        if (currentMonth >= Month.APRIL.getValue()) {
            financialYear = YearMonth.now().getYear() + "-";
            financialYear = financialYear
                    + (Integer.toString(YearMonth.now().getYear() + 1).substring(2, financialYear.length() - 1));
        } else {
            financialYear = YearMonth.now().getYear() - 1 + "-";
            financialYear = financialYear
                    + (Integer.toString(YearMonth.now().getYear()).substring(2, financialYear.length() - 1));

        }
        return financialYear;
    }

    public MdmsCriteriaReq getFinancialYearRequest(RequestInfo requestInfo, String financeYearsStr, String tenantId) {
        MasterDetail masterDetail = MasterDetail.builder().name("FinancialYear")
                .filter("[?(@." + "finYearRange" + " IN [" + financeYearsStr + "]" + " && @.module== '" + "WS" + "')]")
                .build();
        ModuleDetail moduleDetail = ModuleDetail.builder().moduleName("egf-master")
                .masterDetails(Arrays.asList(masterDetail)).build();
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId(tenantId)
                .build();
        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
    }

    private CharSequence getShortenedUrl(String url) {
        String res = null;
        HashMap<String, String> body = new HashMap<>();
        body.put("url", url);
        StringBuilder builder = new StringBuilder(config.getUrlShortnerHost());
        builder.append(config.getUrlShortnerEndpoint());
        try {
            res = restTemplate.postForObject(builder.toString(), body, String.class);

        } catch (Exception e) {
            log.error("Error while shortening the url: " + url, e);

        }
        if (StringUtils.isEmpty(res)) {
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
            ;
            return url;
        } else
            return res;
    }

    private Recepient getRecepient(RequestInfo requestInfo, String tenantId) {
        Recepient recepient = null;
        UserDetailResponse userDetailResponse = userService.getUserByRoleCodes(requestInfo, tenantId,
                Arrays.asList("GP_ADMIN"));
        if (userDetailResponse.getUser().isEmpty())
            log.error("Recepient is absent");
        else {
            List<String> toUsers = userDetailResponse.getUser().stream().map(OwnerInfo::getUuid)
                    .collect(Collectors.toList());

            recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
        }
        return recepient;
    }

    public void sendTodaysCollection(RequestInfo requestInfo) {

        LocalDate date = LocalDate.now();
        LocalDateTime scheduleTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 11, 59,
                59);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentTime = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter),
                dateTimeFormatter);
        List<String> tenantIds = repository.getTenantId();

        tenantIds.forEach(tenantId -> {
            if (tenantId.split("\\.").length >= 2) {
                if (null != config.getIsUserEventsNotificationEnabled()) {
                    if (config.getIsUserEventsNotificationEnabled()) {
                        EventRequest eventRequest = sendDayCollectionNotification(requestInfo, tenantId);
                        if (null != eventRequest)
                            notificationService.sendEventNotification(eventRequest);
                    }
                }

                if (null != config.getIsSMSEnabled()) {
                    if (config.getIsSMSEnabled()) {
                        List<String> messages = new ArrayList<String>();
                        HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo,
                                CustomConstants.TODAY_CASH_COLLECTION_SMS, tenantId);
                        HashMap<String, String> gpwscMap = util.getLocalizationMessage(requestInfo, tenantId, tenantId);

                        String mode = "cash";
                        String message = formatTodayCollectionMessage(requestInfo, tenantId,
                                messageMap.get(NotificationUtil.MSG_KEY), mode, new HashMap<>());
                        HashMap<String, String> onlineMessageMap = util.getLocalizationMessage(requestInfo,
                                CustomConstants.TODAY_ONLINE_COLLECTION_SMS, tenantId);
                        if (message != null) {

                            messages.add(message);
                            mode = "online";
//							String onlineMessage = formatTodayCollectionMessage(requestInfo, tenantId,
//									onlineMessageMap.get(NotificationUtil.MSG_KEY), mode);
//							messages.add(onlineMessage);
                            UserDetailResponse userDetailResponse = userService.getUserByRoleCodes(requestInfo,
                                    tenantId, Arrays.asList("COLLECTION_OPERATOR", "REVENUE_COLLECTOR"));
                            Map<String, String> mobileNumberIdMap = new LinkedHashMap<>();

                            for (OwnerInfo userInfo : userDetailResponse.getUser()) {
                                log.info("TODAY Coll User Info::" + userInfo);
                                if (userInfo.getName() != null) {
                                    mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getName());
                                } else {
                                    mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getUserName());
                                }
                            }
                            mobileNumberIdMap.entrySet().stream().forEach(map -> {
                                if (!messages.isEmpty()) {
                                    String uuidUsername = map.getValue();

                                    messages.forEach(msg -> {
                                        msg = msg.replace("{ownername}", uuidUsername);
                                        msg = msg.replace("{GPWSC}",
                                                (gpwscMap != null
                                                        && !StringUtils.isEmpty(gpwscMap.get(NotificationUtil.MSG_KEY)))
                                                        ? gpwscMap.get(NotificationUtil.MSG_KEY)
                                                        : tenantId);
                                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                        Date today = new Date();
                                        String formattedDate = format.format(today);
                                        msg = msg.replace("{date}", formattedDate);
                                        log.info("TODAY Coll SMS::" + msg);
                                        SMSRequest smsRequest = SMSRequest.builder().mobileNumber(map.getKey())
                                                .message(msg).templateId(messageMap.get(NotificationUtil.TEMPLATE_KEY)).tenantId(tenantId)
                                                .users(new String[]{uuidUsername}).build();
                                        if (config.isSMSForTodaysCollectionEnabled()) {
                                            producer.push(config.getSmsNotifTopic(), smsRequest);
                                        }
                                    });
                                }
                            });
                        }

                    }
                }
            }
            return;
        });
    }

    @SuppressWarnings("null")
    private EventRequest sendDayCollectionNotification(RequestInfo requestInfo, String tenantId) {
        // TODO Auto-generated method stub

        List<ActionItem> items = new ArrayList<>();
        String actionLink = config.getTodayCollectionLink();
        ActionItem item = ActionItem.builder().actionUrl(actionLink).build();
        items.add(item);
        Action action = Action.builder().actionUrls(items).build();
        log.info("Action Link::" + actionLink);

        Map<String, Object> additionalDetailsMap = new HashMap<String, Object>();
        additionalDetailsMap.put("localizationCode", CustomConstants.TODAY_CASH_COLLECTION);

        List<Event> events = new ArrayList<>();
        List<String> messages = new ArrayList<String>();
        HashMap<String, String> cashMessageMap = util.getLocalizationMessage(requestInfo, CustomConstants.TODAY_CASH_COLLECTION,
                tenantId);
        String mode = "cash";
        String message = formatTodayCollectionMessage(requestInfo, tenantId,
                cashMessageMap.get(NotificationUtil.MSG_KEY), mode, additionalDetailsMap);
        HashMap<String, String> onlineMessageMap = util.getLocalizationMessage(requestInfo, CustomConstants.TODAY_ONLINE_COLLECTION,
                tenantId);
        if (message != null) {
            messages.add(message);
            for (String msg : messages) {
                events.add(Event.builder().tenantId(tenantId).description(msg).eventType(CustomConstants.USREVENTS_EVENT_TYPE)
                        .name(CustomConstants.TODAY_COLLECTION).postedBy(CustomConstants.USREVENTS_EVENT_POSTEDBY)
                        .recepient(getRecepient(requestInfo, tenantId)).source(Source.WEBAPP).eventDetails(null)
                        .additionalDetails(additionalDetailsMap).actions(action).build());
            }
        }
//		mode = "online";
//		String onlineMessage = formatTodayCollectionMessage(requestInfo, tenantId,
//				onlineMessageMap.get(NotificationUtil.MSG_KEY), mode);
//		messages.add(onlineMessage);

        if (!CollectionUtils.isEmpty(events)) {
            return EventRequest.builder().requestInfo(requestInfo).events(events).build();
        } else {
            return null;
        }

    }

    private String formatTodayCollectionMessage(RequestInfo requestInfo, String tenantId, String message, String mode, Map<String, Object> additionalDetailsMap) {
        // TODO Auto-generated method stub
        Map<String, String> attributes = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStartDateTime = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 0,
                0, 0);
        LocalDateTime todayEndDateTime = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23,
                59, 59);
        List<Map<String, Object>> todayCollection = repository.getTodayCollection(tenantId,
                ((Long) todayStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).toString(),
                ((Long) todayEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).toString(), mode);

        if (null != todayCollection && todayCollection.size() > 0) {
            for (Map<String, Object> map : todayCollection) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (key.equalsIgnoreCase("sum")) {
                        if (value != null) {
                            message = message.replace("{amount}", value.toString());
                            attributes.put("{amount}", value.toString());
                        } else {
                            message = null;
                            return message;
                        }
                    }
                    if (key.equalsIgnoreCase("count")) {
                        if (message.contains("{no}")) {
                            if (value != null) {
                                message = message.replace("{no}", value.toString());
                                attributes.put("{no}", value.toString());
                            } else {
                                message = message.replace("{no}", "0");
                                attributes.put("{no}", "0");
                            }
                        } else if (message.contains("{number}")) {
                            if (value != null) {
                                message = message.replace("{number}", value.toString());
                                attributes.put("{number}", value.toString());
                            } else {
                                message = message.replace("{number}", "0");
                                attributes.put("{number}", "0");
                            }

                        }
                    }
                }
                log.info("Final message is :" + message);
            }
            additionalDetailsMap.put("attributes", attributes);
        }
        return message;
    }


    public EventRequest sendMonthSummaryNotification(RequestInfo requestInfo, String tenantId) {

        List<ActionItem> items = new ArrayList<>();
        String actionLink = config.getMonthlySummary();
        ActionItem item = ActionItem.builder().actionUrl(actionLink).build();
        items.add(item);
        Action action = Action.builder().actionUrls(items).build();
        log.info("ActionLink::" + actionLink);

        Map<String, Object> additionalDetailsMap = new HashMap<String, Object>();
        additionalDetailsMap.put("localizationCode", CustomConstants.MONTHLY_SUMMARY_EVENT);

        List<Event> events = new ArrayList<>();
        HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo, CustomConstants.MONTHLY_SUMMARY_EVENT, tenantId);
        events.add(Event.builder().tenantId(tenantId)
                .description(formatMonthSummaryMessage(requestInfo, tenantId, messageMap.get(NotificationUtil.MSG_KEY), additionalDetailsMap))
                .eventType(CustomConstants.USREVENTS_EVENT_TYPE).name(CustomConstants.MONTHLY_SUMMARY).postedBy(CustomConstants.USREVENTS_EVENT_POSTEDBY)
                .recepient(getRecepient(requestInfo, tenantId)).source(Source.WEBAPP).eventDetails(null).actions(action)
                .additionalDetails(additionalDetailsMap)
                .build());

        if (!CollectionUtils.isEmpty(events)) {
            return EventRequest.builder().requestInfo(requestInfo).events(events).build();
        } else {
            return null;
        }
    }

    public void sendMonthSummaryEvent(RequestInfo requestInfo) {
        LocalDate dayofmonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime scheduleTime = LocalDateTime.of(dayofmonth.getYear(), dayofmonth.getMonth(),
                dayofmonth.getDayOfMonth(), 10, 0, 0);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentTime = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter),
                dateTimeFormatter);

        List<String> tenantIds = repository.getTenantId();
        tenantIds.forEach(tenantId -> {
            if (tenantId.split("\\.").length >= 2) {
                if (null != config.getIsUserEventEnabled()) {
                    if (config.getIsUserEventEnabled()) {
                        EventRequest eventRequest = sendMonthSummaryNotification(requestInfo, tenantId);
                        if (null != eventRequest)
                            notificationService.sendEventNotification(eventRequest);
                    }
                }

                if (null != config.getIsSMSEnabled()) {
                    if (config.getIsSMSEnabled()) {
                        HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo,
                                CustomConstants.MONTHLY_SUMMARY_SMS, tenantId);
                        HashMap<String, String> gpwscMap = util.getLocalizationMessage(requestInfo, tenantId, tenantId);

                        UserDetailResponse userDetailResponse = userService.getUserByRoleCodes(requestInfo, tenantId,
                                Arrays.asList("EXPENSE_PROCESSING", "SECRETARY"));

                        String revenueLink = config.getUiAppHost() + config.getMonthRevenueDashboardLink();

                        Map<String, String> mobileNumberIdMap = new LinkedHashMap<>();
                        for (OwnerInfo userInfo : userDetailResponse.getUser())
                            if (userInfo.getName() != null) {
                                mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getName());
                            } else {
                                mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getUserName());
                            }
                        mobileNumberIdMap.entrySet().stream().forEach(map -> {
                            if (messageMap != null && !StringUtils.isEmpty(messageMap.get(NotificationUtil.MSG_KEY))) {
                                String uuidUsername = (String) map.getValue();
                                String message = formatMonthSummaryMessage(requestInfo, tenantId,
                                        messageMap.get(NotificationUtil.MSG_KEY), new HashMap<>());
                                message = message.replace("{LINK}", getShortenedUrl(revenueLink));
                                message = message.replace("{GPWSC}", (gpwscMap != null
                                        && !StringUtils.isEmpty(gpwscMap.get(NotificationUtil.MSG_KEY)))
                                        ? gpwscMap.get(NotificationUtil.MSG_KEY)
                                        : tenantId); // TODO Replace
                                // <GPWSC> with
                                // value
                                message = message.replace("{user}", uuidUsername);
                                log.info("SMS Notification::" + message);
                                SMSRequest smsRequest = SMSRequest.builder().mobileNumber(map.getKey()).message(message)
                                        .tenantId(tenantId)
                                        .templateId(messageMap.get(NotificationUtil.TEMPLATE_KEY))
                                        .users(new String[]{uuidUsername}).build();
                                if (config.isSmsForMonthlySummaryEnabled()) {
                                    producer.push(config.getSmsNotifTopic(), smsRequest);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public String formatMonthSummaryMessage(RequestInfo requestInfo, String tenantId, String message, Map<String, Object> additionalDetailsMap) {
        Map<String, String> attributes = new HashMap<>();
        LocalDate prviousMonthStart = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate prviousMonthEnd = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime previousMonthStartDateTime = LocalDateTime.of(prviousMonthStart.getYear(),
                prviousMonthStart.getMonth(), prviousMonthStart.getDayOfMonth(), 0, 0, 0);
        LocalDateTime previousMonthEndDateTime = LocalDateTime.of(prviousMonthEnd.getYear(), prviousMonthEnd.getMonth(),
                prviousMonthEnd.getDayOfMonth(), 23, 59, 59, 999000000);

        List<String> previousMonthCollection = repository.getPreviousMonthExpensePayments(tenantId,
                ((Long) previousMonthStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()),
                ((Long) previousMonthEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        if (null != previousMonthCollection && previousMonthCollection.size() > 0) {
            message = message.replace("{PREVIOUS_MONTH_COLLECTION}", previousMonthCollection.get(0));
            attributes.put("{PREVIOUS_MONTH_COLLECTION}", previousMonthCollection.get(0));
        }
        message = message.replace("{PREVIOUS_MONTH}", LocalDate.now().minusMonths(1).getMonth().toString());
        attributes.put("{PREVIOUS_MONTH}", LocalDate.now().minusMonths(1).getMonth().toString());
        List<String> previousMonthExpense = repository.getPreviousMonthExpenseExpenses(tenantId,
                ((Long) previousMonthStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .toString(),
                ((Long) previousMonthEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).toString());
        if (null != previousMonthExpense && previousMonthExpense.size() > 0) {
            message = message.replace("{PREVIOUS_MONTH_EXPENSE}", previousMonthExpense.get(0));
            attributes.put("{PREVIOUS_MONTH_EXPENSE}", previousMonthExpense.get(0));
        }
        log.info("Final message::" + message);
        additionalDetailsMap.put("attributes", attributes);
        return message;
    }

    public void sendNewExpenditureEvent(RequestInfo requestInfo) {

        LocalDate dayofmonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime scheduleTimeFirst = LocalDateTime.of(dayofmonth.getYear(), dayofmonth.getMonth(),
                dayofmonth.getDayOfMonth(), 10, 0, 0);
        LocalDateTime scheduleTimeSecond = LocalDateTime.of(dayofmonth.getYear(), dayofmonth.getMonth(), 15, 10, 0, 0);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentTime = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter),
                dateTimeFormatter);
        List<String> tenantIds = repository.getTenantId();

        tenantIds.forEach(tenantId -> {
            if (tenantId.split("\\.").length >= 2) {

                if (null != config.getIsUserEventEnabled()) {
                    if (config.getIsUserEventEnabled()) {
                        EventRequest eventRequest = sendNewExpenditureNotification(requestInfo, tenantId);
                        if (null != eventRequest)
                            notificationService.sendEventNotification(eventRequest);
                    }
                }

                if (null != config.getIsSMSEnabled()) {
                    if (config.getIsSMSEnabled()) {

                        UserDetailResponse userDetailResponse = userService.getUserByRoleCodes(requestInfo, tenantId,
                                Arrays.asList("EXPENSE_PROCESSING", "SECRETARY"));
                        Map<String, String> mobileNumberIdMap = new LinkedHashMap<>();

                        HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo,
                                CustomConstants.NEW_EXPENDITURE_SMS, tenantId);

                        HashMap<String, String> gpwscMap = util.getLocalizationMessage(requestInfo,
                                tenantId, tenantId);

                        String addExpense = config.getUiAppHost() + config.getExpenditureLink();
                        log.info("ADD Expense Link :: " + addExpense);
                        for (OwnerInfo userInfo : userDetailResponse.getUser())
                            if (userInfo.getName() != null) {
                                mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getName());
                            } else {
                                mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getUserName());
                            }
                        mobileNumberIdMap.entrySet().stream().forEach(map -> {
                            if (messageMap != null && !StringUtils.isEmpty(messageMap.get(NotificationUtil.MSG_KEY))) {
                                String message = messageMap.get(NotificationUtil.MSG_KEY);

                                message = message.replace("{NEW_EXP_LINK}", getShortenedUrl(addExpense));
                                message = message.replace("{GPWSC}", (gpwscMap != null
                                        && !StringUtils.isEmpty(gpwscMap.get(NotificationUtil.MSG_KEY)))
                                        ? gpwscMap.get(NotificationUtil.MSG_KEY)
                                        : tenantId);
                                log.info("New Expenditure SMS :: " + message);

                                SMSRequest smsRequest = SMSRequest.builder().mobileNumber(map.getKey()).message(message)
                                        .tenantId(tenantId)
                                        .templateId(messageMap.get(NotificationUtil.TEMPLATE_KEY))
                                        .users(new String[]{map.getValue()}).build();
                                if (config.isSmsForExpenditureEnabled()) {
                                    producer.push(config.getSmsNotifTopic(), smsRequest);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public EventRequest sendNewExpenditureNotification(RequestInfo requestInfo, String tenantId) {

        List<ActionItem> items = new ArrayList<>();
        String actionLink = config.getNewExpenditureLink();
        ActionItem item = ActionItem.builder().actionUrl(actionLink).build();
        items.add(item);
        Action action = Action.builder().actionUrls(items).build();
        Map<String, Object> additionalDetailsMap = new HashMap<String, Object>();
        List<Event> events = new ArrayList<>();
        additionalDetailsMap.put("localizationCode", CustomConstants.NEW_EXPENDITURE_EVENT);
        log.info("Action Link::" + actionLink);
        if (tenantId.split("\\.").length >= 2) {
            HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo, CustomConstants.NEW_EXPENDITURE_EVENT,
                    tenantId);
            log.info("Final Message ::" + messageMap.get(NotificationUtil.MSG_KEY));
            events.add(Event.builder().tenantId(tenantId).description(messageMap.get(NotificationUtil.MSG_KEY))
                    .eventType(CustomConstants.USREVENTS_EVENT_TYPE).name(CustomConstants.NEW_EXPENSE_ENTRY).postedBy(CustomConstants.USREVENTS_EVENT_POSTEDBY)
                    .recepient(getRecepient(requestInfo, tenantId)).source(Source.WEBAPP).eventDetails(null)
                    .actions(action)
                    .additionalDetails(additionalDetailsMap)
                    .build());
        }

        if (!CollectionUtils.isEmpty(events)) {
            return EventRequest.builder().requestInfo(requestInfo).events(events).build();
        } else {
            return null;
        }

    }

    public void sendMarkExpensebillEvent(RequestInfo requestInfo) {
        LocalDate dayofmonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime scheduleTimeFirst = LocalDateTime.of(dayofmonth.getYear(), dayofmonth.getMonth(), 7, 10, 0, 0);
        LocalDateTime scheduleTimeSecond = LocalDateTime.of(dayofmonth.getYear(), dayofmonth.getMonth(), 21, 10, 0, 0);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentTime = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter),
                dateTimeFormatter);

        List<String> tenantIds = repository.getTenantId();

        tenantIds.forEach(tenantId -> {
            if (tenantId.split("\\.").length >= 2) {
                if (null != config.getIsUserEventEnabled()) {
                    if (config.getIsUserEventEnabled()) {
                        EventRequest eventRequest = sendMarkExpensebillNotification(requestInfo, tenantId);
                        if (null != eventRequest)
                            notificationService.sendEventNotification(eventRequest);
                    }
                }

                if (null != config.getIsSMSEnabled()) {
                    if (config.getIsSMSEnabled()) {

                        UserDetailResponse userDetailResponse = userService.getUserByRoleCodes(requestInfo, tenantId,
                                Arrays.asList("EXPENSE_PROCESSING", "SECRETARY"));
                        Map<String, String> mobileNumberIdMap = new LinkedHashMap<>();

                        for (OwnerInfo userInfo : userDetailResponse.getUser())
                            if (userInfo.getName() != null) {
                                mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getName());
                            } else {
                                mobileNumberIdMap.put(userInfo.getMobileNumber(), userInfo.getUserName());
                            }

                        String addExpense = config.getUiAppHost() + config.getExpenseBillMarkPaidLink();
                        log.info("ADD Expense Link :: " + addExpense);

                        HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo,
                                CustomConstants.MARK_PAID_BILL_SMS, tenantId);

                        HashMap<String, String> gpwscMap = util.getLocalizationMessage(requestInfo,
                                tenantId, tenantId);

                        mobileNumberIdMap.entrySet().stream().forEach(map -> {
                            if (messageMap != null && !StringUtils.isEmpty(messageMap.get(NotificationUtil.MSG_KEY))) {
                                String message = messageMap.get(NotificationUtil.MSG_KEY);
                                message = message.replace("{EXP_MRK_LINK}", getShortenedUrl(addExpense));

                                message = message.replace("{GPWSC}", (gpwscMap != null
                                        && !StringUtils.isEmpty(gpwscMap.get(NotificationUtil.MSG_KEY)))
                                        ? gpwscMap.get(NotificationUtil.MSG_KEY)
                                        : tenantId); // TODO Replace
                                // <GPWSC> with
                                // value.
                                log.info("Mark expense bills SMS::" + message);
                                SMSRequest smsRequest = SMSRequest.builder().mobileNumber(map.getKey()).message(message)
                                        .tenantId(tenantId)
                                        .templateId(messageMap.get(NotificationUtil.TEMPLATE_KEY))
                                        .users(new String[]{map.getValue()}).build();
                                if (config.isSmsForMarkBillEnabled()) {
                                    producer.push(config.getSmsNotifTopic(), smsRequest);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public EventRequest sendMarkExpensebillNotification(RequestInfo requestInfo, String tenantId) {

        List<ActionItem> items = new ArrayList<>();
        String actionLink = config.getMarkPaidExpenditureLink();
        ActionItem item = ActionItem.builder().actionUrl(actionLink).build();
        items.add(item);
        Action action = Action.builder().actionUrls(items).build();
        log.info("ActionLink::" + actionLink);
        Map<String, Object> additionalDetailsMap = new HashMap<String, Object>();
        additionalDetailsMap.put("localizationCode", CustomConstants.MARK_PAID_BILL_EVENT);
        List<Event> events = new ArrayList<>();
        List<String> activeExpenseCount = repository.getActiveExpenses(tenantId);
        if (null != activeExpenseCount && activeExpenseCount.size() > 0 && activeExpenseCount.get(0) != null
                && Integer.parseInt(activeExpenseCount.get(0)) > 0) {
            log.info("Active expense bill Count" + activeExpenseCount.get(0));
            HashMap<String, String> messageMap = util.getLocalizationMessage(requestInfo, CustomConstants.MARK_PAID_BILL_EVENT, tenantId);
            events.add(Event.builder().tenantId(tenantId)
                    .description(formatMarkExpenseMessage(tenantId, messageMap.get(NotificationUtil.MSG_KEY), additionalDetailsMap))
                    .eventType(CustomConstants.USREVENTS_EVENT_TYPE).name(CustomConstants.EXPENSE_PAYMENT).postedBy(CustomConstants.USREVENTS_EVENT_POSTEDBY)
                    .recepient(getRecepient(requestInfo, tenantId)).source(Source.WEBAPP).eventDetails(null).actions(action)
                    .additionalDetails(additionalDetailsMap)
                    .build());
        }

        if (!CollectionUtils.isEmpty(events)) {
            return EventRequest.builder().requestInfo(requestInfo).events(events).build();
        } else {
            return null;
        }

    }

    public String formatMarkExpenseMessage(String tenantId, String message, Map<String, Object> additionalDetailsMap) {
        Map<String, String> attributes = new HashMap<>();
        List<String> activeExpenseCount = repository.getActiveExpenses(tenantId);
        if (null != activeExpenseCount && activeExpenseCount.size() > 0) {
            message = message.replace("{BILL_COUNT_AWAIT}", activeExpenseCount.get(0));
            attributes.put("{BILL_COUNT_AWAIT}", activeExpenseCount.get(0));
            additionalDetailsMap.put("attributes", attributes);
        }
        log.info("Final message for Mark Expense::" + message);
        return message;
    }

    @Override
    public List<BillReportData> billReport(@Valid String demandStartDate, @Valid String demandEndDate, String tenantId, @Valid Integer offset, @Valid Integer limit, @Valid String sortOrder, RequestInfo requestInfo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate demStartDate = LocalDate.parse(demandStartDate, formatter);
        LocalDate demEndDate = LocalDate.parse(demandEndDate, formatter);

        Long demStartDateTime = LocalDateTime.of(demStartDate.getYear(), demStartDate.getMonth(), demStartDate.getDayOfMonth(), 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        ZoneId istZone = ZoneId.of("Asia/Kolkata"); // IST Time Zone
        LocalDateTime startDateTime = demStartDate.atStartOfDay();
        LocalDateTime endDateTime = demEndDate.atTime(LocalTime.MAX);
        log.info("Local Start DateTime: " + startDateTime);
        log.info("Local End DateTime: " + endDateTime);
        long demStartDateTimeIST = startDateTime.atZone(istZone).toInstant().toEpochMilli();
        long demEndDateTimeIST = endDateTime.atZone(istZone).toInstant().toEpochMilli();

        log.info("demStartDateTimeC:"+demStartDateTimeIST);
        log.info("demEndDateTimeD:"+demEndDateTimeIST);
        Long demEndDateTime = LocalDateTime.of(demEndDate, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<BillReportData> billReportData = customisationServiceDaoImpl.getBillReportData(demStartDateTimeIST, demEndDateTimeIST, tenantId, offset, limit, sortOrder);
        return billReportData;
    }

    @Override
    public List<CollectionReportData> collectionReport(String paymentStartDate, String paymentEndDate, String tenantId, @Valid Integer offset, @Valid Integer limit, @Valid String sortOrder,
                                                       RequestInfo requestInfo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate payStartDate = LocalDate.parse(paymentStartDate, formatter);
        LocalDate payEndDate = LocalDate.parse(paymentEndDate, formatter);

        Long payStartDateTime = LocalDateTime.of(payStartDate.getYear(), payStartDate.getMonth(), payStartDate.getDayOfMonth(), 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long payEndDateTime = LocalDateTime.of(payEndDate, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<CollectionReportData> collectionReportData = customisationServiceDaoImpl.getCollectionReportData(payStartDateTime, payEndDateTime, tenantId, offset, limit, sortOrder);
        return collectionReportData;
    }

    @Override
    public List<InactiveConsumerReportData> inactiveConsumerReport(String monthStartDate, String monthEndDate, String tenantId, @Valid Integer offset, @Valid Integer limit, RequestInfo requestInfo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(monthStartDate, formatter);
        LocalDate endDate = LocalDate.parse(monthEndDate, formatter);

        Long monthStartDateTime = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long mothEndDateTime = LocalDateTime.of(endDate, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<InactiveConsumerReportData> inactiveConsumerReport = customisationServiceDaoImpl.getInactiveConsumerReport(monthStartDateTime, mothEndDateTime, tenantId, offset, limit);
        return inactiveConsumerReport;
    }

    @Override
    public WaterConnectionByDemandGenerationDateResponse countWCbyDemandGennerationDate(SearchCriteria criteria, RequestInfo requestInfo) {
        return getWCbyDemandGennerationDate(criteria, requestInfo);
    }

    public WaterConnectionByDemandGenerationDateResponse getWCbyDemandGennerationDate(SearchCriteria criteria, RequestInfo requestInfo) {
        return customisationServiceDaoImpl.getWaterConnectionByDemandDate(criteria, requestInfo);
    }

    @Override
    public WaterConnectionResponse getConsumersWithDemandNotGenerated(String previousMeterReading, String tenantId, RequestInfo requestInfo) {
        Long previousReadingEpoch;
        try {
            previousReadingEpoch = Long.parseLong(previousMeterReading);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for previousMeterReading. Expected a timestamp in milliseconds.", e);
        }

        List<ConsumersDemandNotGenerated> list = customisationServiceDaoImpl.getConsumersByPreviousMeterReading(previousReadingEpoch, tenantId);
        Set<String> connectionNo = new HashSet<>();
        for (ConsumersDemandNotGenerated connection : list) {
            connectionNo.add(connection.getConsumerCode());
        }
        SearchCriteria criteria = SearchCriteria.builder().connectionNoSet(connectionNo).tenantId(tenantId).build();
        return search(criteria, requestInfo);
    }

    public WaterConnectionResponse search(SearchCriteria criteria, RequestInfo requestInfo) {
        WaterConnectionResponse waterConnection = getWaterConnectionsList(criteria, requestInfo);
        if (!org.springframework.util.StringUtils.isEmpty(criteria.getSearchType())
                && criteria.getSearchType().equals(CustomConstants.SEARCH_TYPE_CONNECTION)) {
            waterConnection
                    .setWaterConnection(enrichmentService.filterConnections(waterConnection.getWaterConnection()));
            if (criteria.getIsPropertyDetailsRequired()) {
                waterConnection.setWaterConnection(enrichmentService
                        .enrichPropertyDetails(waterConnection.getWaterConnection(), criteria, requestInfo));

            }
        }
        customisationServiceValidator.validatePropertyForConnection(waterConnection.getWaterConnection());
        enrichmentService.enrichConnectionHolderDeatils(waterConnection.getWaterConnection(), criteria, requestInfo);
        return waterConnection;
    }

    public WaterConnectionResponse getWaterConnectionsList(SearchCriteria criteria, RequestInfo requestInfo) {
        return customisationServiceDaoImpl.getWaterConnectionList(criteria, requestInfo);
    }

    public WaterConnectionResponse getWCListFuzzySearch(SearchCriteria criteria, RequestInfo requestInfo) {

        if (criteria != null && criteria.getTextSearch() != null) {
            criteria.setTextSearch(criteria.getTextSearch().trim());
        }
        if (criteria != null && criteria.getName() != null) {
            criteria.setName(criteria.getName().trim());
        }

        List<String> idsfromDB = customisationServiceDaoImpl.getWCListFuzzySearch(criteria);

        if (CollectionUtils.isEmpty(idsfromDB))
            WaterConnectionResponse.builder().waterConnection(new LinkedList<>());

        validateFuzzySearchCriteria(criteria);

        Object esResponse = elasticSearchRepository.fuzzySearchProperties(criteria, idsfromDB);

        List<Map<String, Object>> data;

        if (!org.springframework.util.StringUtils.isEmpty(criteria.getTextSearch())) {
            data = waterConnectionSearch(criteria, esResponse);
        } else {
            data = waterConnectionFuzzySearch(criteria, esResponse);
        }

        return WaterConnectionResponse.builder().waterConnectionData(data).totalCount(data.size()).build();
    }

    @Override
    public List<Map<String, Object>> ledgerReport(String consumercode, String tenantId, Integer offset, Integer limit, String year, RequestInfoWrapper requestInfoWrapper) {
        List<Map<String, Object>> list = customisationServiceDaoImpl.getLedgerReport(consumercode, tenantId, offset, limit, year, requestInfoWrapper);
        return list;
    }

    @Override
    public List<MonthReport> monthReport(String startDate, String endDate, String tenantId, Integer offset, Integer limit,String sortOrder)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate monthStartDate = LocalDate.parse(startDate, formatter);
        LocalDate monthEndDate = LocalDate.parse(endDate, formatter);

        Long monthStartDateTime = LocalDateTime.of(monthStartDate.getYear(), monthStartDate.getMonth(), monthStartDate.getDayOfMonth(), 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long monthEndDateTime = LocalDateTime.of(monthEndDate,LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return customisationServiceDaoImpl.getMonthReport(monthStartDateTime,monthEndDateTime,tenantId,offset,limit,sortOrder);
    }

    @Override
    public LastMonthSummary getLastMonthSummary(SearchCriteria criteria, RequestInfo requestInfo) {

        LastMonthSummary lastMonthSummary = new LastMonthSummary();
        String tenantId = criteria.getTenantId();
        LocalDate currentMonthDate = LocalDate.now();
        if (criteria.getCurrentDate() != null) {
            Calendar currentDate = Calendar.getInstance();
            currentDate.setTimeInMillis(criteria.getCurrentDate());
            currentMonthDate = LocalDate.of(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH)+1,
                    currentDate.get(Calendar.DAY_OF_MONTH));
        }
        LocalDate prviousMonthStart = currentMonthDate.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate prviousMonthEnd = currentMonthDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime previousMonthStartDateTime = LocalDateTime.of(prviousMonthStart.getYear(),
                prviousMonthStart.getMonth(), prviousMonthStart.getDayOfMonth(), 0, 0, 0);
        LocalDateTime previousMonthEndDateTime = LocalDateTime.of(prviousMonthEnd.getYear(), prviousMonthEnd.getMonth(),
                prviousMonthEnd.getDayOfMonth(), 23, 59, 59, 999000000);

        // pending ws collectioni
        Integer cumulativePendingCollection = repository.getTotalPendingCollection(tenantId,
                ((Long) previousMonthEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        if (null != cumulativePendingCollection)
            lastMonthSummary.setCumulativePendingCollection(cumulativePendingCollection.toString());

        // ws demands in period
        Integer newDemand = repository.getNewDemand(tenantId,
                ((Long) previousMonthStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()),
                ((Long) previousMonthEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        if (null != newDemand)
            lastMonthSummary.setNewDemand(newDemand.toString());

        // actuall ws collection
        Integer actualCollection = repository.getActualCollection(tenantId,
                ((Long) previousMonthStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()),
                ((Long) previousMonthEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        if (null != actualCollection)
            lastMonthSummary.setActualCollection(actualCollection.toString());

        lastMonthSummary.setPreviousMonthYear(getMonthYear());

        return lastMonthSummary;

    }

    public String getMonthYear() {
        LocalDateTime localDateTime = LocalDateTime.now();
        int currentMonth = localDateTime.getMonthValue();
        String monthYear;
        if (currentMonth >= Month.APRIL.getValue()) {
            monthYear = YearMonth.now().getYear() + "-";
            monthYear = monthYear
                    + (Integer.toString(YearMonth.now().getYear() + 1).substring(2, monthYear.length() - 1));
        } else {
            monthYear = YearMonth.now().getYear() - 1 + "-";
            monthYear = monthYear + (Integer.toString(YearMonth.now().getYear()).substring(2, monthYear.length() - 1));

        }
        StringBuilder monthYearBuilder = new StringBuilder(localDateTime.minusMonths(1).getMonth().toString()).append(" ")
                .append(monthYear);

        return monthYearBuilder.toString();
    }

    @Override
    public RevenueDashboard getRevenueDashboardData(@Valid SearchCriteria criteria, RequestInfo requestInfo) {
        RevenueDashboard dashboardData = new RevenueDashboard();
        String tenantId = criteria.getTenantId();
        BigDecimal demand = customisationServiceDaoImpl.getTotalDemandAmount(criteria);
        if (null != demand) {
            dashboardData.setDemand(demand.setScale(0, RoundingMode.HALF_UP).toString());
        }
        BigDecimal paidAmount = customisationServiceDaoImpl.getActualCollectionAmount(criteria);
        if (null != paidAmount) {
            dashboardData.setActualCollection(paidAmount.setScale(0, RoundingMode.HALF_UP).toString());
        }
        BigDecimal unpaidAmount = customisationServiceDaoImpl.getPendingCollectionAmount(criteria);
        if (null != unpaidAmount) {
            dashboardData.setPendingCollection(unpaidAmount.setScale(0, RoundingMode.HALF_UP).toString());
        }
        Integer residentialCollection = customisationServiceDaoImpl.getResidentialCollectionAmount(criteria);
        if (null != residentialCollection) {
            dashboardData.setResidetialColllection(residentialCollection.toString());
        }
        Integer commeritialCollection = customisationServiceDaoImpl.getCommercialCollectionAmount(criteria);
        if (null != commeritialCollection) {
            dashboardData.setComercialCollection(commeritialCollection.toString());
        }
        Integer othersCollection = customisationServiceDaoImpl.getOthersCollectionAmount(criteria);
        if (null != othersCollection) {
            dashboardData.setOthersCollection(othersCollection.toString());
        }
        Map<String, Object> residentialsPaid = customisationServiceDaoImpl.getResidentialPaid(criteria);
        if (null != residentialsPaid) {
            dashboardData.setResidentialsCount(residentialsPaid);
        }
        Map<String, Object> comercialsPaid = customisationServiceDaoImpl.getCommercialPaid(criteria);
        if (null != comercialsPaid) {
            dashboardData.setComercialsCount(comercialsPaid);
        }
        Map<String, Object> totalApplicationsPaid = customisationServiceDaoImpl.getAllPaid(criteria);
        if (null != totalApplicationsPaid) {
            dashboardData.setTotalApplicationsCount(totalApplicationsPaid);
        }
        BigDecimal advanceAdjusted = customisationServiceDaoImpl.getTotalAdvanceAdjustedAmount(criteria);
        if (null != advanceAdjusted) {
            dashboardData.setAdvanceAdjusted(advanceAdjusted.setScale(0, RoundingMode.HALF_UP).toString());
        }
        BigDecimal pendingPenalty = customisationServiceDaoImpl.getTotalPendingPenaltyAmount(criteria);
        if (null != pendingPenalty) {
            dashboardData.setPendingPenalty(pendingPenalty.setScale(0, RoundingMode.HALF_UP).toString());
        }
        BigDecimal advanceCollection = customisationServiceDaoImpl.getAdvanceCollectionAmount(criteria);
        if (null != advanceCollection) {
            dashboardData.setAdvanceCollection(advanceCollection.setScale(0, RoundingMode.HALF_UP).toString());
        }
        BigDecimal penaltyCollection = customisationServiceDaoImpl.getPenaltyCollectionAmount(criteria);
        if (null != penaltyCollection) {
            dashboardData.setPenaltyCollection(penaltyCollection.setScale(0, RoundingMode.HALF_UP).toString());
        }

        return dashboardData;
    }

    @Override
    public List<RevenueCollectionData> getRevenueCollectionData(@Valid SearchCriteria criteria,
                                                                RequestInfo requestInfo) {

        long endDate = criteria.getToDate();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        LocalDate currentMonthDate = LocalDate.now();

        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int actualMonthnum = currentDate.get(Calendar.MONTH);

        currentDate.setTimeInMillis(criteria.getFromDate());
        int actualYear = currentDate.get(Calendar.YEAR);

        int currentMonthNumber = currentDate.get(Calendar.MONTH);

        int totalMonthsTillDate;
        LocalDate finYearStarting;

        if (currentYear != actualYear && actualYear < currentYear) {
            totalMonthsTillDate = 11;
            currentMonthDate = LocalDate.of(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1,
                    currentDate.get(Calendar.DAY_OF_MONTH));
            finYearStarting = currentMonthDate;
        } else {
            totalMonthsTillDate = actualMonthnum - currentMonthNumber;
            currentMonthDate = LocalDate.of(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1,
                    currentDate.get(Calendar.DAY_OF_MONTH));
            finYearStarting = currentMonthDate;

        }
        ArrayList<RevenueCollectionData> data = new ArrayList<RevenueCollectionData>();
        for (int i = 0; i <= totalMonthsTillDate; i++) {
            LocalDate monthStart = currentMonthDate.minusMonths(0).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate monthEnd = currentMonthDate.minusMonths(0).with(TemporalAdjusters.lastDayOfMonth());

            LocalDateTime monthStartDateTime = LocalDateTime.of(monthStart.getYear(), monthStart.getMonth(),
                    monthStart.getDayOfMonth(), 0, 0, 0);
            LocalDateTime monthEndDateTime = LocalDateTime.of(monthEnd.getYear(), monthEnd.getMonth(),
                    monthEnd.getDayOfMonth(), 23, 59, 59, 999000000);
            criteria.setFromDate((Long) monthStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            criteria.setToDate((Long) monthEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

            String tenantId = criteria.getTenantId();
            BigDecimal demand = customisationServiceDaoImpl.getTotalDemandAmount(criteria);
            RevenueCollectionData collectionData = new RevenueCollectionData();

            if (null != demand) {
                collectionData.setDemand(demand.setScale(0, RoundingMode.HALF_UP).toString());
            }
            BigDecimal paidAmount = customisationServiceDaoImpl.getActualCollectionAmount(criteria);
            if (null != paidAmount) {
                collectionData.setActualCollection(paidAmount.setScale(0, RoundingMode.HALF_UP).toString());
            }
            BigDecimal unpaidAmount = customisationServiceDaoImpl.getPendingCollectionAmount(criteria);
            if (null != unpaidAmount) {
                collectionData.setPendingCollection(unpaidAmount.setScale(0, RoundingMode.HALF_UP).toString());
            }
            BigDecimal arrears = customisationServiceDaoImpl.getArrearsAmount(criteria);
            if (null != arrears) {
                collectionData.setArrears(arrears.setScale(0, RoundingMode.HALF_UP).toString());
            }
            BigDecimal advanceAdjusted = customisationServiceDaoImpl.getTotalAdvanceAdjustedAmount(criteria);
            if (null != advanceAdjusted) {
                collectionData.setAdvanceAdjusted(advanceAdjusted.setScale(0, RoundingMode.HALF_UP).toString());
            }
            BigDecimal pendingPenalty = customisationServiceDaoImpl.getTotalPendingPenaltyAmount(criteria);
            if (null != pendingPenalty) {
                collectionData.setPendingPenalty(pendingPenalty.setScale(0, RoundingMode.HALF_UP).toString());
            }
            BigDecimal advanceCollection = customisationServiceDaoImpl.getAdvanceCollectionAmount(criteria);
            if (null != advanceCollection) {
                collectionData.setAdvanceCollection(advanceCollection.setScale(0, RoundingMode.HALF_UP).toString());
            }
            BigDecimal penaltyCollection = customisationServiceDaoImpl.getPenaltyCollectionAmount(criteria);
            if (null != penaltyCollection) {
                collectionData.setPenaltyCollection(penaltyCollection.setScale(0, RoundingMode.HALF_UP).toString());
            }

            collectionData.setMonth(criteria.getFromDate());
            data.add(i, collectionData);
            System.out.println("Month:: " + criteria.getFromDate());

            currentMonthDate = currentMonthDate.plusMonths(1);
        }
        System.out.println("datadatadatadata" + data);
        return data;
    }

    private void validateFuzzySearchCriteria(SearchCriteria criteria) {
        if (org.apache.commons.lang3.StringUtils.isBlank(criteria.getTextSearch())
                && org.apache.commons.lang3.StringUtils.isBlank(criteria.getName())
                && org.apache.commons.lang3.StringUtils.isBlank(criteria.getMobileNumber()))
            throw new CustomException("EG_WC_SEARCH_ERROR", " No criteria given for the water connection search");
    }

    private List<Map<String, Object>> waterConnectionSearch(SearchCriteria criteria, Object esResponse) {
        List<Map<String, Object>> data;
        try {
            data = wsDataResponse(esResponse);
        } catch (Exception e) {
            throw new CustomException("INVALID_SEARCH_USER_PROP_NOT_FOUND",
                    "Could not find user or water connection details !");
        }
        return data;
    }

    private List<Map<String, Object>> wsDataResponse(Object esResponse) {

        List<Map<String, Object>> data;
        try {
            data = JsonPath.read(esResponse, CustomConstants.ES_DATA_PATH);
        } catch (Exception e) {
            throw new CustomException("PARSING_ERROR", "Failed to extract data from es response");
        }

        return data;
    }

    private List<Map<String, Object>> waterConnectionFuzzySearch(SearchCriteria criteria, Object esResponse) {
        List<Map<String, Object>> data;
        try {
            data = wsDataResponse(esResponse);
            if (data.isEmpty()) {
                throw new CustomException("INVALID_SEARCH_USER_PROP_NOT_FOUND",
                        "Could not find user or water connection details !");

            }
        } catch (Exception e) {
            throw new CustomException("INVALID_SEARCH_USER_PROP_NOT_FOUND",
                    "Could not find user or water connection details !");
        }
        return data;
    }

    public List<VendorReportData> vendorReport(String monthStartDate, String tenantId, Integer offset, Integer limit, RequestInfo requestInfo)
    {

        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate= LocalDate.parse(monthStartDate,formatter);
//		LocalDate endDate=LocalDate.parse(monthEndDate,formatter);

        Long monthStartDateTime= LocalDateTime.of(startDate.getYear(),startDate.getMonth(),startDate.getDayOfMonth(),
                0,0,0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//		Long monthEndDateTime=LocalDateTime.of(endDate, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<VendorReportData> vendorReportData=repository.getVendorReportData(monthStartDateTime,tenantId,offset,limit);
        return vendorReportData;

    }

    public List<ExpenseBillReportData> expenseBillReport(RequestInfo requestInfo, String monthstartDate,String monthendDate, String tenantId, Integer offset, Integer limit)
    {
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate=LocalDate.parse(monthstartDate,dtf);
        LocalDate endDate=LocalDate.parse(monthendDate,dtf);

        Long monthStartDateTime=LocalDateTime.of(startDate.getYear(),startDate.getMonth(),startDate.getDayOfMonth(),
                0,0,0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long monthEndDateTime=LocalDateTime.of(endDate, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<ExpenseBillReportData> expenseBillReport=repository.getExpenseBillReport(monthStartDateTime,monthEndDateTime,tenantId,offset,limit);
        return expenseBillReport;

    }
}
