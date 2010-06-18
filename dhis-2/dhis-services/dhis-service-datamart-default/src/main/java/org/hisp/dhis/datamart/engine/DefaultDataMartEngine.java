package org.hisp.dhis.datamart.engine;

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

import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_BOOL;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_INT;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.datamart.util.ParserUtil.getDataElementIdsInExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.datamart.calculateddataelement.CalculatedDataElementDataMart;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.datamart.dataelement.DataElementDataMart;
import org.hisp.dhis.datamart.indicator.IndicatorDataMart;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.TimeUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataMartEngine
    implements DataMartEngine
{
    private static final Log log = LogFactory.getLog( DefaultDataMartEngine.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    protected AggregationCache aggregationCache;
        
    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }
    
    private DataMartStore dataMartStore;

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }

    private DataElementDataMart dataElementDataMart;

    public void setDataElementDataMart( DataElementDataMart dataElementDataMart )
    {
        this.dataElementDataMart = dataElementDataMart;
    }

    private IndicatorDataMart indicatorDataMart;

    public void setIndicatorDataMart( IndicatorDataMart indicatorDataMart )
    {
        this.indicatorDataMart = indicatorDataMart;
    }
    
    private CalculatedDataElementDataMart calculatedDataElementDataMart;

    public void setCalculatedDataElementDataMart( CalculatedDataElementDataMart calculatedDataElementDataMart )
    {
        this.calculatedDataElementDataMart = calculatedDataElementDataMart;
    }
    
    private DataElementAggregator sumIntAggregator;

    public void setSumIntAggregator( DataElementAggregator sumIntDataElementAggregator )
    {
        this.sumIntAggregator = sumIntDataElementAggregator;
    }

    private DataElementAggregator averageIntAggregator;

    public void setAverageIntAggregator( DataElementAggregator averageIntDataElementAggregator )
    {
        this.averageIntAggregator = averageIntDataElementAggregator;
    }

    private DataElementAggregator averageIntSingleValueAggregator;
    
    public void setAverageIntSingleValueAggregator( DataElementAggregator averageIntSingleValueAggregator )
    {
        this.averageIntSingleValueAggregator = averageIntSingleValueAggregator;
    }

    private DataElementAggregator sumBoolAggregator;

    public void setSumBoolAggregator( DataElementAggregator sumBooleanDataElementAggregator )
    {
        this.sumBoolAggregator = sumBooleanDataElementAggregator;
    }

    private DataElementAggregator averageBoolAggregator;

    public void setAverageBoolAggregator( DataElementAggregator averageBooleanDataElementAggregator )
    {
        this.averageBoolAggregator = averageBooleanDataElementAggregator;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // DataMartEngine implementation
    // -------------------------------------------------------------------------

    @Transactional
    public int export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, ProcessState state )
    {   
        int count = 0;
        
        TimeUtils.start();

        state.setMessage( "deleting_existing_aggregated_data" );

        // ---------------------------------------------------------------------
        // Delete existing aggregated data
        // ---------------------------------------------------------------------

        dataMartStore.deleteAggregatedDataValues( dataElementIds, periodIds, organisationUnitIds );
        
        dataMartStore.deleteAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds );
        
        log.info( "Deleted existing aggregated data: " + TimeUtils.getHMS() );

        // ---------------------------------------------------------------------
        // Filter and get operands
        // ---------------------------------------------------------------------

        final Set<Integer> nonCalculatedDataElementIds = filterCalculatedDataElementIds( dataElementIds, false );
        final Set<Integer> calculatedDataElementIds = filterCalculatedDataElementIds( dataElementIds, true );
        
        final Set<Integer> dataElementInIndicatorIds = getDataElementIdsInIndicators( indicatorIds );
        final Set<Integer> dataElementInCalculatedDataElementIds = getDataElementIdsInCalculatedDataElements( calculatedDataElementIds );
        
        final Set<Integer> allDataElementIds = new HashSet<Integer>();
        allDataElementIds.addAll( nonCalculatedDataElementIds );
        allDataElementIds.addAll( dataElementInIndicatorIds );
        allDataElementIds.addAll( dataElementInCalculatedDataElementIds );

        final Collection<DataElementOperand> allDataElementOperands = categoryService.getOperandsByIds( allDataElementIds );
        final Collection<DataElementOperand> dataElementInIndicatorOperands = categoryService.getOperandsByIds( dataElementInIndicatorIds );
        final Collection<DataElementOperand> dataElementInCalculatedDataElementOperands = categoryService.getOperandsByIds( dataElementInCalculatedDataElementIds );
        final Collection<DataElementOperand> nonCalculatedDataElementOperands = categoryService.getOperandsByIds( nonCalculatedDataElementIds );
        
        final Collection<DataElementOperand> sumIntDataElementOperands = filterOperands( nonCalculatedDataElementOperands, VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        final Collection<DataElementOperand> averageIntDataElementOperands = filterOperands( nonCalculatedDataElementOperands, VALUE_TYPE_INT, AGGREGATION_OPERATOR_AVERAGE );
        final Collection<DataElementOperand> sumBoolDataElementOperands = filterOperands( nonCalculatedDataElementOperands, VALUE_TYPE_BOOL, AGGREGATION_OPERATOR_SUM );
        final Collection<DataElementOperand> averageBoolDataElementOperands = filterOperands( nonCalculatedDataElementOperands, VALUE_TYPE_BOOL, AGGREGATION_OPERATOR_AVERAGE );
        
        log.info( "Filtered data elements: " + TimeUtils.getHMS() );
        
        // ---------------------------------------------------------------------
        // Create and trim crosstabtable
        // ---------------------------------------------------------------------

        if ( crossTabService.validateCrossTabTable( allDataElementOperands ) != 0 )
        {
            int excess = crossTabService.validateCrossTabTable( allDataElementOperands );

            log.warn( "Cannot crosstabulate since the number of data elements exceeded maximum columns: " + excess );
            
            state.setMessage( "could_not_export_too_many_data_elements" );
            
            return 0;
        }           

        log.info( "Validated crosstab table: " + TimeUtils.getHMS() );
        
        state.setMessage( "crosstabulating_data" );

        Collection<Integer> childrenIds = organisationUnitService.getOrganisationUnitHierarchy().getChildren( organisationUnitIds );
        
        final Collection<DataElementOperand> emptyOperands = crossTabService.populateCrossTabTable( allDataElementOperands, getIntersectingIds( periodIds ), childrenIds );

        log.info( "Populated crosstab table: " + TimeUtils.getHMS() );
                
        crossTabService.trimCrossTabTable( emptyOperands );

        log.info( "Trimmed crosstab table: " + TimeUtils.getHMS() );
        
        // ---------------------------------------------------------------------
        // Data element export
        // ---------------------------------------------------------------------

        state.setMessage( "exporting_data_for_data_elements" );

        if ( sumIntDataElementOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( sumIntDataElementOperands, periodIds, organisationUnitIds, sumIntAggregator );
        
            log.info( "Exported values for data element operands with sum aggregation operator of type number (" + sumIntDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( averageIntDataElementOperands.size() > 0 )
        {            
            count += dataElementDataMart.exportDataValues( averageIntDataElementOperands, periodIds, organisationUnitIds, averageIntAggregator );
        
            log.info( "Exported values for data element operands with average aggregation operator of type number (" + averageIntDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( averageIntDataElementOperands.size() > 0 )
        {            
            count += dataElementDataMart.exportDataValues( averageIntDataElementOperands, periodIds, organisationUnitIds, averageIntSingleValueAggregator );
        
            log.info( "Exported values for data element operands with average aggregation operator with single value of type number (" + averageIntDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( sumBoolDataElementOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( sumBoolDataElementOperands, periodIds, organisationUnitIds, sumBoolAggregator );
            
            log.info( "Exported values for data element operands with sum aggregation operator of type yes/no (" + sumBoolDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( averageBoolDataElementOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( averageBoolDataElementOperands, periodIds, organisationUnitIds, averageBoolAggregator );
            
            log.info( "Exported values for data element operands with average aggregation operator of type yes/no (" + averageBoolDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        state.setMessage( "exporting_data_for_indicators" );

        // ---------------------------------------------------------------------
        // Indicator export
        // ---------------------------------------------------------------------

        if ( indicatorIds != null && indicatorIds.size() > 0 )
        {
            count += indicatorDataMart.exportIndicatorValues( indicatorIds, periodIds, organisationUnitIds, dataElementInIndicatorOperands );
            
            log.info( "Exported values for indicators (" + indicatorIds.size() + "): " + TimeUtils.getHMS() );
        }

        state.setMessage( "exporting_data_for_calculated_data_elements" );

        // ---------------------------------------------------------------------
        // Calculated data element export
        // ---------------------------------------------------------------------

        if ( calculatedDataElementIds != null && calculatedDataElementIds.size() > 0 )
        {
            count += calculatedDataElementDataMart.exportCalculatedDataElements( calculatedDataElementIds, periodIds, organisationUnitIds, dataElementInCalculatedDataElementOperands );
            
            log.info( "Exported values for calculated data elements (" + calculatedDataElementIds.size() + "): " + TimeUtils.getHMS() );
        }

        crossTabService.dropCrossTabTable();
        
        log.info( "Export process completed: " + TimeUtils.getHMS() );
        
        TimeUtils.stop();

        aggregationCache.clearCache();
        
        return count;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Sorts calculated data elements from non-calculated based on the given collection
     * of identifiers and flag.
     */
    private Set<Integer> filterCalculatedDataElementIds( final Collection<Integer> dataElementIds, boolean calculated )
    {
        final Set<Integer> identifiers = new HashSet<Integer>();
        
        for ( final Integer id : dataElementIds )
        {
            final DataElement element = dataElementService.getDataElement( id );
            
            if ( ( element instanceof CalculatedDataElement ) == calculated )
            {
                identifiers.add( id );
            }
        }
        
        return identifiers;
    }
    
    /**
     * Returns all data element identifiers included in the indicators in the given 
     * identifier collection.
     */
    private Set<Integer> getDataElementIdsInIndicators( final Collection<Integer> indicatorIds )
    {
        final Set<Integer> identifiers = new HashSet<Integer>( indicatorIds.size() );
        
        for ( final Integer id : indicatorIds )
        {
            final Indicator indicator = indicatorService.getIndicator( id );
            
            identifiers.addAll( getDataElementIdsInExpression( indicator.getNumerator() ) );
            identifiers.addAll( getDataElementIdsInExpression( indicator.getDenominator() ) );            
        }
        
        return identifiers;
    }
    
    /**
     * Returns all data element identifiers included in the calculated data elements
     * in the given identifier collection.
     */
    private Set<Integer> getDataElementIdsInCalculatedDataElements( final Collection<Integer> calculatedDataElementIds )
    {
        final Set<Integer> identifiers = new HashSet<Integer>();
        
        for ( final Integer id : calculatedDataElementIds )
        {
            final Set<DataElement> dataElements = expressionService.getDataElementsInCalculatedDataElement( id );
            
            if ( dataElements != null )
            {
                identifiers.addAll( ConversionUtils.getIdentifiers( DataElement.class, dataElements ) );
            }
        }
        
        return identifiers;
    }
    
    /**
     * Returns the identifiers of the periods in the given collection including 
     * all intersecting periods.
     */
    private Collection<Integer> getIntersectingIds( final Collection<Integer> periodIds )
    {
        final Set<Integer> identifiers = new HashSet<Integer>( periodIds.size() );
        
        for ( final Integer id : periodIds )
        {
            final Period period = periodService.getPeriod( id );
            
            final Collection<Period> periods = periodService.getIntersectingPeriods( period.getStartDate(), period.getEndDate() );
            
            identifiers.addAll( ConversionUtils.getIdentifiers( Period.class, periods ) );
        }
        
        return identifiers;
    }
    
    /**
     * Filters the data element operands based on the value type.
     */
    private Collection<DataElementOperand> filterOperands( Collection<DataElementOperand> operands, String valueType, String aggregationOperator )
    {
        final Collection<DataElementOperand> filtered = new ArrayList<DataElementOperand>();
        
        for ( DataElementOperand operand : operands )
        {
            if ( operand.getValueType().equals( valueType ) && operand.getAggregationOperator().equals( aggregationOperator ) )
            {
                filtered.add( operand );
            }
        }
        
        return filtered;
    }
}
