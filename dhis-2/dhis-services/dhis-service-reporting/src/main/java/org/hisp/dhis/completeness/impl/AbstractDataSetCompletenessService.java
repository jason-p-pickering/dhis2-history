package org.hisp.dhis.completeness.impl;

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

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.commons.collection.CachingMap;
import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.completeness.DataSetCompletenessStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.jdbc.batchhandler.DataSetCompletenessResultBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

/**
 * @author Lars Helge Overland
 */
public abstract class AbstractDataSetCompletenessService
    implements DataSetCompletenessService
{
    private static final Log log = LogFactory.getLog( AbstractDataSetCompletenessService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    protected DataSetCompletenessStore completenessStore;

    public void setCompletenessStore( DataSetCompletenessStore completenessStore )
    {
        this.completenessStore = completenessStore;
    }

    // -------------------------------------------------------------------------
    // DataSetCompletenessService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract int getRegistrations( DataSet dataSet, Collection<Integer> relevantSources, Collection<Integer> periods );

    protected abstract int getRegistrationsOnTime( DataSet dataSet, Collection<Integer> relevantSources, Collection<Integer> periods );

    protected abstract int getSources( DataSet dataSet, Collection<Integer> relevantSources, Period period );

    // -------------------------------------------------------------------------
    // DataSetCompleteness
    // -------------------------------------------------------------------------

    @Override
    @Async
    public Future<?> exportDataSetCompleteness( Collection<DataSet> dataSets, Collection<Period> periods,
        Collection<OrganisationUnit> units )
    {
        BatchHandler<DataSetCompletenessResult> batchHandler = batchHandlerFactory
            .createBatchHandler( DataSetCompletenessResultBatchHandler.class ).init();

        OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy();
        hierarchy.prepareChildren( units );
        
        final CachingMap<String, List<Integer>> periodCache = new CachingMap<>();
        
        for ( final DataSet dataSet : dataSets )
        {
            int dataSetFrequencyOrder = dataSet.getPeriodType().getFrequencyOrder();

            for ( final OrganisationUnit unit : units )
            {
                Set<Integer> sources = hierarchy.getChildren( unit.getId() );

                Set<Integer> relevantSources = getRelevantSources( dataSet, sources, null );

                for ( final Period period : periods )
                {
                    if ( period.getPeriodType() != null && period.getPeriodType().getFrequencyOrder() >= dataSetFrequencyOrder )
                    {
                        final String periodKey = dataSet.getPeriodType().getName() + period.getStartDate().toString() + period.getEndDate().toString();
                        
                        final List<Integer> periodsBetweenDates = periodCache.get( periodKey, 
                            () -> getIdentifiers( periodService.getPeriodsBetweenDates( dataSet.getPeriodType(), period.getStartDate(), period.getEndDate() ) ) );
                        
                        final DataSetCompletenessResult result = getDataSetCompleteness( period, periodsBetweenDates, unit, relevantSources, dataSet );

                        if ( result.getSources() > 0 )
                        {
                            batchHandler.addObject( result );
                        }
                    }
                }
            }
        }

        batchHandler.flush();

        log.info( "Completeness export task done" );

        return null;
    }

    @Override
    @Transactional
    public List<DataSetCompletenessResult> getDataSetCompleteness( int periodId, int organisationUnitId, Set<Integer> groupIds )
    {
        final Period period = periodService.getPeriod( periodId );

        final Set<Integer> children = organisationUnitService.getOrganisationUnitHierarchy().getChildren(
            organisationUnitId );

        final List<DataSet> dataSets = dataSetService.getAllDataSets();

        final List<DataSetCompletenessResult> results = new ArrayList<>();

        for ( final DataSet dataSet : dataSets )
        {
            final List<Integer> periodsBetweenDates = getIdentifiers( 
                periodService.getPeriodsBetweenDates( dataSet.getPeriodType(), period.getStartDate(), period.getEndDate() ) );

            final Set<Integer> relevantSources = getRelevantSources( dataSet, children, groupIds );

            final DataSetCompletenessResult result = new DataSetCompletenessResult();

            result.setSources( getSources( dataSet, relevantSources, period ) );

            if ( result.getSources() > 0 )
            {
                result.setName( dataSet.getName() );
                result.setRegistrations( getRegistrations( dataSet, relevantSources, periodsBetweenDates ) );
                result.setRegistrationsOnTime( getRegistrationsOnTime( dataSet, relevantSources, periodsBetweenDates ) );

                result.setDataSetId( dataSet.getId() );
                result.setPeriodId( periodId );
                result.setOrganisationUnitId( organisationUnitId );

                results.add( result );
            }
        }

        return results;
    }

    @Override
    @Transactional
    public List<DataSetCompletenessResult> getDataSetCompleteness( int periodId,
        Collection<Integer> organisationUnitIds, int dataSetId, Set<Integer> groupIds )
    {
        final DataSet dataSet = dataSetService.getDataSet( dataSetId );

        final Period period = periodService.getPeriod( periodId );

        final List<Integer> periodsBetweenDates = getIdentifiers( 
            periodService.getPeriodsBetweenDates( dataSet.getPeriodType(), period.getStartDate(), period.getEndDate() ) );

        final List<DataSetCompletenessResult> results = new ArrayList<>();

        for ( final Integer unitId : organisationUnitIds )
        {
            final OrganisationUnit unit = organisationUnitService.getOrganisationUnit( unitId );

            final Set<Integer> children = organisationUnitService.getOrganisationUnitHierarchy().getChildren(
                unit.getId() );

            final Set<Integer> relevantSources = getRelevantSources( dataSet, children, groupIds );

            final DataSetCompletenessResult result = getDataSetCompleteness( period, periodsBetweenDates, unit, relevantSources, dataSet );

            if ( result.getSources() > 0 )
            {
                results.add( result );
            }
        }

        return results;
    }

    @Override
    @Transactional
    public void deleteDataSetCompleteness()
    {
        completenessStore.deleteDataSetCompleteness();
    }

    // -------------------------------------------------------------------------
    // Index
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void createIndex()
    {
        completenessStore.createIndex();
    }

    @Override
    @Transactional
    public void dropIndex()
    {
        completenessStore.dropIndex();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private DataSetCompletenessResult getDataSetCompleteness( Period period, Collection<Integer> periodsBetweenDates, OrganisationUnit unit,
        Collection<Integer> relevantSources, DataSet dataSet )
    {
        final DataSetCompletenessResult result = new DataSetCompletenessResult();

        result.setName( unit.getName() );
        result.setSources( getSources( dataSet, relevantSources, period ) );

        if ( result.getSources() > 0 )
        {
            result.setRegistrations( getRegistrations( dataSet, relevantSources, periodsBetweenDates ) );
            result.setRegistrationsOnTime( getRegistrationsOnTime( dataSet, relevantSources, periodsBetweenDates ) );

            result.setDataSetId( dataSet.getId() );
            result.setPeriodId( period.getId() );
            result.setPeriodName( period.getName() );
            result.setOrganisationUnitId( unit.getId() );
        }

        return result;
    }

    private Set<Integer> getRelevantSources( DataSet dataSet, Set<Integer> sources, Set<Integer> groupIds )
    {
        Set<Integer> dataSetSources = new HashSet<>( getIdentifiers( dataSet.getSources() ) );

        if ( groupIds != null )
        {
            for ( Integer groupId : groupIds )
            {
                OrganisationUnitGroup group = organisationUnitGroupService.getOrganisationUnitGroup( groupId );
                List<Integer> ids = getIdentifiers( group.getMembers() );
                dataSetSources.retainAll( ids );
            }
        }

        return Sets.intersection( dataSetSources, sources );
    }
}
