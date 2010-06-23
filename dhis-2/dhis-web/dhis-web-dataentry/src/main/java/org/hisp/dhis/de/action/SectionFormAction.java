package org.hisp.dhis.de.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.customvalue.CustomValue;
import org.hisp.dhis.customvalue.CustomValueService;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.de.comments.StandardCommentsManager;
import org.hisp.dhis.de.screen.DataEntryScreenManager;
import org.hisp.dhis.de.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Tri
 * @version $Id: FormAction.java 4089 2007-11-24 13:30:44Z larshelg $
 */
public class SectionFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CustomValueService customValueService;

    public CustomValueService getCustomValueService()
    {
        return customValueService;
    }

    public void setCustomValueService( CustomValueService customValueService )
    {
        this.customValueService = customValueService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private StandardCommentsManager standardCommentsManager;

    public void setStandardCommentsManager( StandardCommentsManager standardCommentsManager )
    {
        this.standardCommentsManager = standardCommentsManager;
    }

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    private DataEntryScreenManager dataEntryScreenManager;

    public void setDataEntryScreenManager( DataEntryScreenManager dataEntryScreenManager )
    {
        this.dataEntryScreenManager = dataEntryScreenManager;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<CustomValue> customValues = new ArrayList<CustomValue>();

    public List<CustomValue> getCustomValues()
    {
        return customValues;
    }

    private List<Section> sections;

    public List<Section> getSections()
    {
        return sections;
    }

    private Map<Integer, DataValue> dataValueMap;

    public Map<Integer, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    private Map<CalculatedDataElement, Integer> calculatedValueMap;

    public Map<CalculatedDataElement, Integer> getCalculatedValueMap()
    {
        return calculatedValueMap;
    }

    private List<String> standardComments;

    public List<String> getStandardComments()
    {
        return standardComments;
    }

    private Map<String, String> dataElementValueTypeMap;

    public Map<String, String> getDataElementValueTypeMap()
    {
        return dataElementValueTypeMap;
    }

    private Map<Integer, MinMaxDataElement> minMaxMap;

    public Map<Integer, MinMaxDataElement> getMinMaxMap()
    {
        return minMaxMap;
    }

    private Integer integer = 0;

    public Integer getInteger()
    {
        return integer;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer selectedDataSetId;

    public void setSelectedDataSetId( Integer selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }

    public Integer getSelectedDataSetId()
    {
        return selectedDataSetId;
    }

    private Integer selectedPeriodIndex;

    public void setSelectedPeriodIndex( Integer selectedPeriodIndex )
    {
        this.selectedPeriodIndex = selectedPeriodIndex;
    }

    public Integer getSelectedPeriodIndex()
    {
        return selectedPeriodIndex;
    }

    private Map<Integer, Map<Integer, Collection<DataElementCategoryOption>>> orderedOptionsMap = new HashMap<Integer, Map<Integer, Collection<DataElementCategoryOption>>>();

    public Map<Integer, Map<Integer, Collection<DataElementCategoryOption>>> getOrderedOptionsMap()
    {
        return orderedOptionsMap;
    }

    private Map<Integer, Collection<DataElementCategory>> orderedCategories = new HashMap<Integer, Collection<DataElementCategory>>();

    public Map<Integer, Collection<DataElementCategory>> getOrderedCategories()
    {
        return orderedCategories;
    }

    private Map<Integer, Integer> numberOfTotalColumns = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getNumberOfTotalColumns()
    {
        return numberOfTotalColumns;
    }

    private Map<Integer, Map<Integer, Collection<Integer>>> catColRepeat = new HashMap<Integer, Map<Integer, Collection<Integer>>>();

    public Map<Integer, Map<Integer, Collection<Integer>>> getCatColRepeat()
    {
        return catColRepeat;
    }

    private Map<Integer, Collection<DataElementCategoryOptionCombo>> orderdCategoryOptionCombos = new HashMap<Integer, Collection<DataElementCategoryOptionCombo>>();

    public Map<Integer, Collection<DataElementCategoryOptionCombo>> getOrderdCategoryOptionCombos()
    {
        return orderdCategoryOptionCombos;
    }

    Collection<DataElementCategoryOptionCombo> allOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

    public Collection<DataElementCategoryOptionCombo> getAllOptionCombos()
    {
        return allOptionCombos;
    }

    private List<DataElementCategoryCombo> orderedCategoryCombos = new ArrayList<DataElementCategoryCombo>();

    public List<DataElementCategoryCombo> getOrderedCategoryCombos()
    {
        return orderedCategoryCombos;
    }

    private Map<Integer, Boolean> sectionIsMultiDimensional = new HashMap<Integer, Boolean>();

    public Map<Integer, Boolean> getSectionIsMultiDimensional()
    {
        return sectionIsMultiDimensional;
    }
    
    private Map<Integer, Integer> sectionCombos = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getSectionCombos()
    {
        return sectionCombos;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        customValues = (List<CustomValue>) customValueService.getCustomValuesByDataSet( dataSet );

        Period period = selectedStateManager.getSelectedPeriod();

        Collection<DataElement> dataElements = new ArrayList<DataElement>();

        dataElements = dataSet.getDataElements();

        if ( dataElements.size() == 0 )
        {
            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Get the min/max values
        // ---------------------------------------------------------------------

        Collection<MinMaxDataElement> minMaxDataElements = minMaxDataElementService.getMinMaxDataElements(
            organisationUnit, dataElements );

        minMaxMap = new HashMap<Integer, MinMaxDataElement>( minMaxDataElements.size() );

        for ( MinMaxDataElement minMaxDataElement : minMaxDataElements )
        {
            minMaxMap.put( minMaxDataElement.getDataElement().getId(), minMaxDataElement );
        }

        // ---------------------------------------------------------------------
        // Order the Sections
        // ---------------------------------------------------------------------

        sections = new ArrayList<Section>( dataSet.getSections() );

        Collections.sort( sections, new SectionOrderComparator() );

        // ---------------------------------------------------------------------
        // Get the category combos for the sections
        // ---------------------------------------------------------------------

        for ( Section section : sections )
        {
            if ( section.getDataElements().size() > 0 )
            {
                orderedCategoryCombos.add( section.getDataElements().iterator().next().getCategoryCombo() );
                
                sectionCombos.put( section.getId(), section.getDataElements().iterator().next().getCategoryCombo().getId() );
            }
            
            if( dataEntryScreenManager.hasMultiDimensionalDataElement( section ) )
            {
                sectionIsMultiDimensional.put( section.getId(), true );
            }
        }

        for ( DataElementCategoryCombo categoryCombo : orderedCategoryCombos )
        {
            Collection<DataElementCategoryOptionCombo> optionCombos = categoryService.sortOptionCombos( categoryCombo );

            allOptionCombos.addAll( optionCombos );

            orderdCategoryOptionCombos.put( categoryCombo.getId(), optionCombos );

            // ---------------------------------------------------------------------
            // Perform ordering of categories and their options so that they
            // could
            // be displayed as in the paper form. Note that the total number of
            // entry cells to be generated are the multiple of options from each
            // category.
            // ---------------------------------------------------------------------

            numberOfTotalColumns.put( categoryCombo.getId(), optionCombos.size() );

            orderedCategories.put( categoryCombo.getId(), categoryCombo.getCategories() );

            Map<Integer, Collection<DataElementCategoryOption>> optionsMap = new HashMap<Integer, Collection<DataElementCategoryOption>>();

            for ( DataElementCategory dec : categoryCombo.getCategories() )
            {
                optionsMap.put( dec.getId(), dec.getCategoryOptions() );
            }

            orderedOptionsMap.put( categoryCombo.getId(), optionsMap );

            // ---------------------------------------------------------------------
            // Calculating the number of times each category should be repeated
            // ---------------------------------------------------------------------

            int catColSpan = optionCombos.size();

            Map<Integer, Integer> catRepeat = new HashMap<Integer, Integer>();

            Map<Integer, Collection<Integer>> colRepeat = new HashMap<Integer, Collection<Integer>>();

            for ( DataElementCategory cat : categoryCombo.getCategories() )
            {
                if ( !cat.getCategoryOptions().isEmpty() )
                {
                    catColSpan = catColSpan / cat.getCategoryOptions().size();
                    int total = optionCombos.size() / (catColSpan * cat.getCategoryOptions().size());
                    Collection<Integer> cols = new ArrayList<Integer>( total );

                    for ( int i = 0; i < total; i++ )
                    {
                        cols.add( i );
                    }

                    /*
                     * TODO Cols are made to be a collection simply to
                     * facilitate a for loop in the velocity template - there
                     * should be a better way of "for" doing a loop.
                     */

                    colRepeat.put( cat.getId(), cols );

                    catRepeat.put( cat.getId(), catColSpan );
                }
            }

            catColRepeat.put( categoryCombo.getId(), colRepeat );
        }

        // ---------------------------------------------------------------------
        // Get the DataValues and create a map
        // ---------------------------------------------------------------------

        Collection<DataValue> dataValues = dataValueService.getDataValues( organisationUnit, period, dataElements );

        dataValueMap = new HashMap<Integer, DataValue>( dataValues.size() );

        for ( DataValue dataValue : dataValues )
        {
            dataValueMap.put( dataValue.getDataElement().getId(), dataValue );
        }

        // ---------------------------------------------------------------------
        // Prepare values for unsaved CalculatedDataElements
        // ---------------------------------------------------------------------

        CalculatedDataElement cde;

        calculatedValueMap = new HashMap<CalculatedDataElement, Integer>();

        Map<DataElement, Integer> factorMap;

        DataValue dataValue;
        int factor;
        int value = 0;

        for ( DataElement dataElement : dataElements )
        {
            if ( !(dataElement instanceof CalculatedDataElement) )
            {
                continue;
            }

            cde = (CalculatedDataElement) dataElement;

            if ( cde.isSaved() )
            {
                continue;
            }

            factorMap = dataElementService.getDataElementFactors( cde );

            for ( DataElement cdeElement : cde.getExpression().getDataElementsInExpression() )
            {
                factor = factorMap.get( cdeElement );
                dataValue = dataValueMap.get( cdeElement.getId() );

                if ( dataValue != null )
                {
                    value += Integer.parseInt( dataValue.getValue() ) * factor;
                }

            }

            calculatedValueMap.put( cde, value );

            value = 0;
        }

        // ---------------------------------------------------------------------
        // Make the standard comments available
        // ---------------------------------------------------------------------

        standardComments = standardCommentsManager.getStandardComments();

        // ---------------------------------------------------------------------
        // Make the DataElement types available
        // ---------------------------------------------------------------------

        dataElementValueTypeMap = new HashMap<String, String>();
        dataElementValueTypeMap.put( DataElement.VALUE_TYPE_BOOL, i18n.getString( "yes_no" ) );
        dataElementValueTypeMap.put( DataElement.VALUE_TYPE_INT, i18n.getString( "number" ) );
        dataElementValueTypeMap.put( DataElement.VALUE_TYPE_STRING, i18n.getString( "text" ) );

        return SUCCESS;

    }
}
