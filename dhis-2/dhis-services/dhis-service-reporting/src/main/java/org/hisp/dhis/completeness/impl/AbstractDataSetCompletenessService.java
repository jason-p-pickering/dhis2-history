package org.hisp.dhis.completeness.impl;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import static org.hisp.dhis.options.SystemSettingManager.DEFAULT_COMPLETENESS_OFFSET;
import static org.hisp.dhis.options.SystemSettingManager.KEY_COMPLETENESS_OFFSET;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.completeness.DataSetCompletenessStore;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.jdbc.batchhandler.DataSetCompletenessResultBatchHandler;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public abstract class AbstractDataSetCompletenessService
    implements DataSetCompletenessService
{
    private static final Log log = LogFactory.getLog( AbstractDataSetCompletenessService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    protected BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    protected OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    protected DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    protected DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    protected PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    protected DataSetCompletenessStore completenessStore;

    public void setCompletenessStore( DataSetCompletenessStore completenessStore )
    {
        this.completenessStore = completenessStore;
    }
    
    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // DataSetCompletenessService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------
    
    public abstract int getRegistrations( DataSet dataSet, Collection<Integer> children, Period period );
    
    public abstract int getRegistrationsOnTime( DataSet dataSet, Collection<Integer> children, Period period, Date deadline );
    
    public abstract int getSources( DataSet dataSet, Collection<Integer> children );
    
    // -------------------------------------------------------------------------
    // DataSetCompleteness
    // -------------------------------------------------------------------------

    public void exportDataSetCompleteness( Collection<Integer> dataSetIds, RelativePeriods relatives, Collection<Integer> organisationUnitIds )
    {
        if ( relatives != null )
        {
            List<Period> periods = relatives.getRelativePeriods( 1, null, false );
            
            FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );
            
            Collection<Integer> periodIds = ConversionUtils.getIdentifiers( Period.class, periodService.reloadPeriods( periods ) );
            
            exportDataSetCompleteness( dataSetIds, periodIds, organisationUnitIds );
        }
    }
    
    public void exportDataSetCompleteness( Collection<Integer> dataSetIds, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        log.info( "Data completeness export process started" );
        
        int days = (Integer) systemSettingManager.getSystemSetting( KEY_COMPLETENESS_OFFSET, DEFAULT_COMPLETENESS_OFFSET );
        
        completenessStore.deleteDataSetCompleteness( dataSetIds, periodIds, organisationUnitIds );
        
        BatchHandler<DataSetCompletenessResult> batchHandler = batchHandlerFactory.createBatchHandler( DataSetCompletenessResultBatchHandler.class );
        
        batchHandler.init();
        
        Collection<Period> periods = periodService.getPeriods( periodIds );
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( organisationUnitIds );
        Collection<DataSet> dataSets = dataSetService.getDataSets( dataSetIds );
        
        periods = completenessStore.getPeriodsWithRegistrations( periods );
        dataSets = completenessStore.getDataSetsWithRegistrations( dataSets );
        
        OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy();
        hierarchy.prepareChildren( units );
        
        //TODO Re-implement period aggregation with sql to improve performance
        
        for ( final Period period : periods )
        {
            for ( final DataSet dataSet : dataSets )
            {
                if ( period.getPeriodType() != null && dataSet.getPeriodType() != null && period.getPeriodType().equals( dataSet.getPeriodType() ) )
                {
                    for ( final OrganisationUnit unit : units )
                    {
                        final Date deadline = getDeadline( period, days );
                                
                        final DataSetCompletenessResult result = getDataSetCompleteness( period, deadline, unit, hierarchy, dataSet );
                        
                        if ( result.getSources() > 0 )
                        {
                            batchHandler.addObject( result );
                        }
                    }
                }
            }
            
            log.info( "Exported data completeness for period " + period.getId() );
        }
        
        batchHandler.flush();
        
        log.info( "Export process done" );
    }
    
    public Collection<DataSetCompletenessResult> getDataSetCompleteness( int periodId, int organisationUnitId )
    {
        final Period period = periodService.getPeriod( periodId );
        
        int days = (Integer) systemSettingManager.getSystemSetting( KEY_COMPLETENESS_OFFSET, DEFAULT_COMPLETENESS_OFFSET );        
        Date deadline = getDeadline( period, days );
        
        final Collection<Integer> children = organisationUnitService.getOrganisationUnitHierarchy().getChildren( organisationUnitId );
        
        final Collection<DataSet> dataSets = dataSetService.getAllDataSets();
        
        final Collection<DataSetCompletenessResult> results = new ArrayList<DataSetCompletenessResult>();
        
        for ( final DataSet dataSet : dataSets )
        {
            final DataSetCompletenessResult result = new DataSetCompletenessResult();
            
            result.setSources( getSources( dataSet, children ) );
            
            if ( result.getSources() > 0 )
            {
                result.setName( dataSet.getName() );
                result.setRegistrations( getRegistrations( dataSet, children, period ) );
                result.setRegistrationsOnTime( deadline != null ? getRegistrationsOnTime( dataSet, children, period, deadline ) : 0 );
                            
                result.setDataSetId( dataSet.getId() );
                result.setPeriodId( periodId );
                result.setOrganisationUnitId( organisationUnitId );
                
                results.add( result );
            }
        }
        
        return results;
    }
    
    public Collection<DataSetCompletenessResult> getDataSetCompleteness( int periodId, int parentOrganisationUnitId, int dataSetId )
    {
        final Period period = periodService.getPeriod( periodId );

        int days = (Integer) systemSettingManager.getSystemSetting( KEY_COMPLETENESS_OFFSET, DEFAULT_COMPLETENESS_OFFSET );        
        Date deadline = getDeadline( period, days );
        
        final DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        final OrganisationUnit parent = organisationUnitService.getOrganisationUnit( parentOrganisationUnitId );
        
        final Collection<OrganisationUnit> units = parent.getChildren();
        
        final Collection<DataSetCompletenessResult> results = new ArrayList<DataSetCompletenessResult>();
        
        for ( final OrganisationUnit unit : units )
        {
            final Collection<Integer> children = organisationUnitService.getOrganisationUnitHierarchy().getChildren( unit.getId() );
            
            final DataSetCompletenessResult result = new DataSetCompletenessResult();

            result.setSources( getSources( dataSet, children ) );
            
            if ( result.getSources() > 0 )
            {
                result.setName( unit.getName() );
                result.setRegistrations( getRegistrations( dataSet, children, period ) );
                result.setRegistrationsOnTime( deadline != null ? getRegistrationsOnTime( dataSet, children, period, deadline ) : 0 );
                
                result.setDataSetId( dataSetId );
                result.setPeriodId( periodId );
                result.setOrganisationUnitId( unit.getId() );
                
                results.add( result );
            }
        }
        
        return results;
    }

    public DataSetCompletenessResult getDataSetCompleteness( Period period, Date deadline, OrganisationUnit unit, OrganisationUnitHierarchy hierarchy, DataSet dataSet )
    {
        final Collection<Integer> children = hierarchy.getChildren( unit.getId() );
        
        final DataSetCompletenessResult result = new DataSetCompletenessResult();
        
        result.setName( unit.getName() );
        result.setRegistrations( getRegistrations( dataSet, children, period ) );
        result.setRegistrationsOnTime( deadline != null ? getRegistrationsOnTime( dataSet, children, period, deadline ) : 0 );
        result.setSources( getSources( dataSet, children ) );
        
        result.setDataSetId( dataSet.getId() );
        result.setPeriodId( period.getId() );
        result.setPeriodName( period.getName() );
        result.setOrganisationUnitId( unit.getId() );
        
        return result;
    }
        
    // -------------------------------------------------------------------------
    // Index
    // -------------------------------------------------------------------------

    public void createIndex()
    {
        completenessStore.createIndex();
    }
    
    public void dropIndex()
    {
        completenessStore.dropIndex();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    protected int getSourcesAssociatedWithDataSet( DataSet dataSet, Collection<Integer> sources )
    {
        Collection<Integer> dataSetSources = getIdentifiers( OrganisationUnit.class, dataSet.getSources() );
        return CollectionUtils.intersection( dataSetSources, sources ).size();
    }

    private Date getDeadline( Period period, int days )
    {
        Calendar cal = Calendar.getInstance();
        
        Date date = null;
        
        if ( period != null )
        {
            cal.clear();                
            cal.setTime( period.getEndDate() );                                       
            cal.add( Calendar.DAY_OF_MONTH, days );
            
            date = cal.getTime();
        }
        
        return date;
    }
}
