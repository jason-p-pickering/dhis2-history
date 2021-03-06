package org.hisp.dhis.analytics.table;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsTable;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.commons.util.TextUtils;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.util.ObjectUtils;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import static org.hisp.dhis.commons.util.TextUtils.getQuotedCommaDelimitedString;
import static org.hisp.dhis.dataapproval.DataApprovalLevelService.APPROVAL_LEVEL_UNAPPROVED;

/**
 * This class manages the analytics table. The analytics table is a denormalized
 * table designed for analysis which contains raw data values. It has columns for
 * each organisation unit group set and organisation unit level. Also, columns
 * for dataelementid, periodid, organisationunitid, categoryoptioncomboid, value.
 * <p>
 * The analytics table is horizontally partitioned. The partition key is the start
 * date of the  period of the data record. The table is partitioned according to
 * time span with one partition per calendar quarter.
 * <p>
 * The data records in this table are not aggregated. Typically, queries will
 * aggregate in organisation unit hierarchy dimension, in the period/time dimension,
 * and the category dimensions, as well as organisation unit group set dimensions.
 *
 * @author Lars Helge Overland
 */
public class JdbcAnalyticsTableManager
    extends AbstractJdbcTableManager
{
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public String validState()
    {
        boolean hasData = jdbcTemplate.queryForRowSet( "select dataelementid from datavalue limit 1" ).next();

        if ( !hasData )
        {
            return "No data values exist, not updating aggregate analytics tables";
        }

        int orgUnitLevels = organisationUnitService.getMaxOfOrganisationUnitLevels();

        if ( orgUnitLevels == 0 )
        {
            return "No organisation unit levels exist, not updating aggregate analytics tables";
        }

        log.info( "Approval enabled: " + isApprovalEnabled() );

        return null;
    }

    @Override
    public String getTableName()
    {
        return ANALYTICS_TABLE_NAME;
    }

    @Override
    public void preCreateTables()
    {
        if ( isApprovalEnabled() )
        {
            resourceTableService.generateDataApprovalMinLevelTable();
        }
    }

    @Override
    public void createTable( AnalyticsTable table )
    {
        final String tableName = table.getTempTableName();

        final String dbl = statementBuilder.getDoubleColumnType();

        final String sqlDrop = "drop table " + tableName;

        executeSilently( sqlDrop );

        String sqlCreate = "create table " + tableName + " (";

        List<String[]> columns = getDimensionColumns( table );

        validateDimensionColumns( columns );

        for ( String[] col : columns )
        {
            sqlCreate += col[0] + " " + col[1] + ",";
        }

        sqlCreate += "daysxvalue " + dbl + ", daysno integer not null, value " + dbl + ", textvalue varchar(50000)) ";

        sqlCreate += statementBuilder.getTableOptions( false );

        log.info( "Creating table: " + tableName + ", columns: " + columns.size() );

        log.debug( "Create SQL: " + sqlCreate );

        executeSilently( sqlCreate );
    }

    @Override
    @Async
    public Future<?> populateTableAsync( ConcurrentLinkedQueue<AnalyticsTable> tables )
    {
        final String dbl = statementBuilder.getDoubleColumnType();

        final String approvalClause = getApprovalJoinClause();

        taskLoop:
        while ( true )
        {
            AnalyticsTable table = tables.poll();

            if ( table == null )
            {
                break taskLoop;
            }

            String intClause =
                "dv.value " + statementBuilder.getRegexpMatch() + " '" + MathUtils.NUMERIC_LENIENT_REGEXP + "' " +
                    "and ( dv.value != '0' or de.aggregationtype in ('" + AggregationType.AVERAGE + ',' + AggregationType.AVERAGE_SUM_ORG_UNIT + "') " +
                    "or de.zeroissignificant = true ) ";

            populateTable( table, "cast(dv.value as " + dbl + ")", "null", ValueType.NUMERIC_TYPES, intClause, approvalClause );

            populateTable( table, "1", "null", Lists.newArrayList( ValueType.BOOLEAN, ValueType.TRUE_ONLY ), "dv.value = 'true'", approvalClause );

            populateTable( table, "0", "null", Lists.newArrayList( ValueType.BOOLEAN ), "dv.value = 'false'", approvalClause );

            populateTable( table, "1", "null", Lists.newArrayList( ValueType.TRUE_ONLY ), "dv.value = 'true'", approvalClause );

            populateTable( table, "null", "dv.value", ValueType.TEXT_TYPES, null, approvalClause );
        }

        return null;
    }

    /**
     * Populates the given analytics table.
     *
     * @param table               analytics table to populate.
     * @param valueExpression     numeric value expression.
     * @param textValueExpression textual value expression.
     * @param valueTypes          data element value types to include data for.
     * @param whereClause         where clause to constrain data query.
     */
    private void populateTable( AnalyticsTable table, String valueExpression,
        String textValueExpression, List<ValueType> valueTypes, String whereClause, String approvalClause )
    {
        final String start = DateUtils.getMediumDateString( table.getPeriod().getStartDate() );
        final String end = DateUtils.getMediumDateString( table.getPeriod().getEndDate() );
        final String tableName = table.getTempTableName();
        final String valTypes = TextUtils.getQuotedCommaDelimitedString( ObjectUtils.asStringList( valueTypes ) );

        String sql = "insert into " + table.getTempTableName() + " (";

        List<String[]> columns = getDimensionColumns( table );

        validateDimensionColumns( columns );

        for ( String[] col : columns )
        {
            sql += col[0] + ",";
        }

        sql += "daysxvalue, daysno, value, textvalue) select ";

        for ( String[] col : columns )
        {
            sql += col[2] + ",";
        }

        sql +=
            valueExpression + " * ps.daysno as daysxvalue, " +
                "ps.daysno as daysno, " +
                valueExpression + " as value, " +
                textValueExpression + " as textvalue " +
                "from datavalue dv " +
                "left join _dataelementgroupsetstructure degs on dv.dataelementid=degs.dataelementid " +
                "left join _organisationunitgroupsetstructure ougs on dv.sourceid=ougs.organisationunitid " +
                "left join _categoryoptiongroupsetstructure cogs on dv.categoryoptioncomboid=cogs.categoryoptioncomboid " +
                "left join _categoryoptiongroupsetstructure aogs on dv.attributeoptioncomboid=aogs.categoryoptioncomboid " +
                "left join _categorystructure dcs on dv.categoryoptioncomboid=dcs.categoryoptioncomboid " +
                "left join _categorystructure acs on dv.attributeoptioncomboid=acs.categoryoptioncomboid " +
                "left join _orgunitstructure ous on dv.sourceid=ous.organisationunitid " +
                "left join _dataelementstructure des on dv.dataelementid = des.dataelementid " +
                "inner join dataelement de on dv.dataelementid=de.dataelementid " +
                "inner join categoryoptioncombo co on dv.categoryoptioncomboid=co.categoryoptioncomboid " +
                "inner join categoryoptioncombo ao on dv.attributeoptioncomboid=ao.categoryoptioncomboid " +
                "inner join _categoryoptioncomboname aon on dv.attributeoptioncomboid=aon.categoryoptioncomboid " +
                "inner join period pe on dv.periodid=pe.periodid " +
                "inner join _periodstructure ps on dv.periodid=ps.periodid " +
                "inner join organisationunit ou on dv.sourceid=ou.organisationunitid " +
                approvalClause +
                "where de.valuetype in (" + valTypes + ") " +
                "and de.domaintype = 'AGGREGATE' " +
                "and pe.startdate >= '" + start + "' " +
                "and pe.startdate <= '" + end + "' " +
                "and dv.value is not null ";

        if ( whereClause != null )
        {
            sql += "and " + whereClause;
        }

        populateAndLog( sql, tableName + ", " + valueTypes );
    }

    /**
     * Returns sub-query for approval level. First looks for approval level in
     * data element resource table which will indicate level 0 (highest) if approval
     * is not required. Then looks for highest level in dataapproval table.
     */
    private String getApprovalJoinClause()
    {
        if ( isApprovalEnabled() )
        {
            String sql =
                "left join _dataapprovalminlevel da " +
                    "on des.datasetid=da.datasetid and da.periodid=dv.periodid and da.attributeoptioncomboid=dv.attributeoptioncomboid " +
                    "and (";

            Set<OrganisationUnitLevel> levels = dataApprovalLevelService.getOrganisationUnitApprovalLevels();

            for ( OrganisationUnitLevel level : levels )
            {
                sql += "ous.idlevel" + level.getLevel() + " = da.organisationunitid or ";
            }

            return TextUtils.removeLastOr( sql ) + ") ";
        }

        return StringUtils.EMPTY;
    }

    @Override
    public List<String[]> getDimensionColumns( AnalyticsTable table )
    {
        List<String[]> columns = new ArrayList<>();

        List<DataElementGroupSet> dataElementGroupSets =
            idObjectManager.getDataDimensionsNoAcl( DataElementGroupSet.class );

        List<OrganisationUnitGroupSet> orgUnitGroupSets =
            idObjectManager.getDataDimensionsNoAcl( OrganisationUnitGroupSet.class );

        List<CategoryOptionGroupSet> disaggregationCategoryOptionGroupSets =
            categoryService.getDisaggregationCategoryOptionGroupSetsNoAcl();

        List<CategoryOptionGroupSet> attributeCategoryOptionGroupSets =
            categoryService.getAttributeCategoryOptionGroupSetsNoAcl();

        List<DataElementCategory> disaggregationCategories =
            categoryService.getDisaggregationDataDimensionCategoriesNoAcl();

        List<DataElementCategory> attributeCategories =
            categoryService.getAttributeDataDimensionCategoriesNoAcl();

        List<OrganisationUnitLevel> levels =
            organisationUnitService.getFilledOrganisationUnitLevels();

        for ( DataElementGroupSet groupSet : dataElementGroupSets )
        {
            String[] col = { quote( groupSet.getUid() ), "character(11)", "degs." + quote( groupSet.getUid() ) };
            columns.add( col );
        }

        for ( OrganisationUnitGroupSet groupSet : orgUnitGroupSets )
        {
            String[] col = { quote( groupSet.getUid() ), "character(11)", "ougs." + quote( groupSet.getUid() ) };
            columns.add( col );
        }

        for ( CategoryOptionGroupSet groupSet : disaggregationCategoryOptionGroupSets )
        {
            String[] col = { quote( groupSet.getUid() ), "character(11)", "cogs." + quote( groupSet.getUid() ) };
            columns.add( col );
        }

        for ( CategoryOptionGroupSet groupSet : attributeCategoryOptionGroupSets )
        {
            String[] col = { quote( groupSet.getUid() ), "character(11)", "aogs." + quote( groupSet.getUid() ) };
            columns.add( col );
        }

        for ( DataElementCategory category : disaggregationCategories )
        {
            String[] col = { quote( category.getUid() ), "character(11)", "dcs." + quote( category.getUid() ) };
            columns.add( col );
        }

        for ( DataElementCategory category : attributeCategories )
        {
            String[] col = { quote( category.getUid() ), "character(11)", "acs." + quote( category.getUid() ) };
            columns.add( col );
        }

        for ( OrganisationUnitLevel level : levels )
        {
            String column = quote( PREFIX_ORGUNITLEVEL + level.getLevel() );
            String[] col = { column, "character(11)", "ous." + column };
            columns.add( col );
        }

        List<PeriodType> periodTypes = PeriodType.getAvailablePeriodTypes();

        for ( PeriodType periodType : periodTypes )
        {
            String column = quote( periodType.getName().toLowerCase() );
            String[] col = { column, "character varying(15)", "ps." + column };
            columns.add( col );
        }

        String[] de = { quote( "dx" ), "character(11) not null", "de.uid" };
        String[] co = { quote( "co" ), "character(11) not null", "co.uid" };
        String[] ao = { quote( "ao" ), "character(11) not null", "ao.uid" };
        String[] ou = { quote( "ou" ), "character(11) not null", "ou.uid" };
        String[] level = { quote( "level" ), "integer", "ous.level" };

        columns.addAll( Lists.newArrayList( de, co, ao, ou, level ) );

        if ( isApprovalEnabled() )
        {
            String col = "coalesce(des.datasetapprovallevel, aon.approvallevel, da.minlevel, " + APPROVAL_LEVEL_UNAPPROVED + ")";

            String[] al = { quote( "approvallevel" ), "integer", col };
            columns.add( al );
        }

        return columns;
    }

    @Override
    public List<Integer> getDataYears( Date earliest )
    {
        String sql =
            "select distinct(extract(year from pe.startdate)) " +
                "from datavalue dv " +
                "inner join period pe on dv.periodid=pe.periodid " +
                "where pe.startdate is not null ";

        if ( earliest != null )
        {
            sql += "and pe.startdate >= '" + DateUtils.getMediumDateString( earliest ) + "'";
        }

        return jdbcTemplate.queryForList( sql, Integer.class );
    }

    @Override
    @Async
    public Future<?> applyAggregationLevels( ConcurrentLinkedQueue<AnalyticsTable> tables, Collection<String> dataElements, int aggregationLevel )
    {
        taskLoop:
        while ( true )
        {
            AnalyticsTable table = tables.poll();

            if ( table == null )
            {
                break taskLoop;
            }

            StringBuilder sql = new StringBuilder( "update " + table.getTempTableName() + " set " );

            for ( int i = 0; i < aggregationLevel; i++ )
            {
                int level = i + 1;

                String column = quote( DataQueryParams.LEVEL_PREFIX + level );

                sql.append( column + " = null," );
            }

            sql.deleteCharAt( sql.length() - ",".length() );

            sql.append( " where level > " + aggregationLevel );
            sql.append( " and dx in (" + getQuotedCommaDelimitedString( dataElements ) + ")" );

            log.debug( "Aggregation level SQL: " + sql.toString() );

            jdbcTemplate.execute( sql.toString() );
        }

        return null;
    }

    @Override
    @Async
    public Future<?> vacuumTablesAsync( ConcurrentLinkedQueue<AnalyticsTable> tables )
    {
        taskLoop:
        while ( true )
        {
            AnalyticsTable table = tables.poll();

            if ( table == null )
            {
                break taskLoop;
            }

            final String sql = statementBuilder.getVacuum( table.getTempTableName() );

            log.debug( "Vacuum SQL: " + sql );

            jdbcTemplate.execute( sql );
        }

        return null;
    }

    /**
     * Indicates whether the system should ignore data which has not been approved
     * in analytics tables.
     */
    private boolean isApprovalEnabled()
    {
        boolean setting = systemSettingManager.hideUnapprovedDataInAnalytics();
        boolean levels = !dataApprovalLevelService.getAllDataApprovalLevels().isEmpty();

        return setting && levels;
    }
}
