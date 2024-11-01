package org.konnect.crm.sfdc;

import lombok.extern.slf4j.Slf4j;
import org.konnect.crm.request.DataFetchRequest;
import org.konnect.crm.request.TimeFilter;
import org.konnect.crm.schema.CrmDataType;
import org.konnect.utils.CollectionUtils;
import org.konnect.utils.string.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class QueryBuilder {

    private static final String SFDC_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static String buildSoql(DataFetchRequest request) {
        final String objectName = getObjectName(request.getDataType());

        Set<String> selectFields = new HashSet<>(request.getSelectedAttributes());
        if (request.getAssociationCriteria() != null) {
            selectFields.addAll(buildJoinedFields(request.getAssociationCriteria().getJoinedAttributes()));
            selectFields.addAll(buildEmbeddedQueries(request.getAssociationCriteria().getEmbeddedAttributes()));
        }

        String filter = "";
        if (request.getFilterCriteria() != null) {
            DateFormat df = new SimpleDateFormat(SFDC_DATE_TIME_FORMAT);
            if (request.getFilterCriteria().getCreatedTimeFilter() != null) {
                TimeFilter createdFilter = request.getFilterCriteria().getCreatedTimeFilter();
                String startTime = getDateTimeFromTimestamp(createdFilter.getStartTime(), df);
                String endTime = getDateTimeFromTimestamp(createdFilter.getEndTime(), df);
                filter = String.format(" WHERE CreatedDate >= %s AND CreatedDate <= %s", startTime, endTime);
            }
        }

        String selectClause = createBasicQuery(objectName, new ArrayList<>(selectFields));
        String query = String.format("%s %s", selectClause, filter);
        log.info("Generated SOQL query: {}", query);
        return query;
    }

    private static String getDateTimeFromTimestamp(long timestamp, DateFormat df) {
        return df.format(new Date(timestamp));
    }

    public static List<String> buildJoinedFields(Map<String, List<String>> joinedAttributes) {
        if (CollectionUtils.isEmpty(joinedAttributes)) {
            return Collections.emptyList();
        }
        List<String> joinedFields = new ArrayList<>();
        joinedAttributes.forEach((key, value) -> {
            if (StringUtils.isNotBlank(key) && CollectionUtils.isNotEmpty(value)) {
                value.forEach(field -> {
                    if (StringUtils.isNotBlank(field)) {
                        joinedFields.add(String.format("%s.%s", key, field));
                    }
                });
            }
        });
        return joinedFields;
    }

    public static List<String> buildEmbeddedQueries(Map<String, List<String>> embeddings) {
        if (CollectionUtils.isEmpty(embeddings)) {
            return Collections.emptyList();
        }

        List<String> embeddedQueries = new ArrayList<>();
        embeddings.forEach((key, value) -> {
            if (StringUtils.isNotBlank(key) && CollectionUtils.isNotEmpty(value)) {
                embeddedQueries.add("(" + createBasicQuery(key, value) + ")");
            }
        });
        return embeddedQueries;
    }

    public static String createBasicQuery(String table, List<String> fields) {
        return String.format("SELECT %s from %s", String.join(",", fields), table);
    }

    private static String getObjectName(String objectName) {
        CrmDataType dataType = CrmDataType.fromString(objectName);
        if (dataType == null) {
            // non-standard object
            return objectName;
        }
        switch (dataType) {
            case ACCOUNT:
                return "Account";
            case ANNUAL_RECURRING_REVENUE:
                return "AnnualRecurringRevenue";
            case CAMPAIGN:
                return "Campaign";
            case CONTACT:
                return "Contact";
            case CURRENCY_TYPE:
                return "CurrencyType";
            case DATED_CONVERSION_RATE:
                return "DatedConversionRate";
            case FORECASTING_QUOTA:
                return "ForecastingQuota";
            case LEAD:
                return "Lead";
            case OPPORTUNITY:
                return "Opportunity";
            case OPPORTUNITY_SPLIT:
                return "OpportunitySplit";
            case OPPORTUNITY_HISTORY:
                return "OpportunityHistory";
            case OPPORTUNITY_FIELD_HISTORY:
                return "OpportunityFieldHistory";
            case PERIOD:
                return "Period";
            case TASK:
                return "Task";
            case USER:
                return "User";
            default:
                throw new IllegalArgumentException("Missing Salesforce object name for crm data type " + objectName);
        }
    }
}
