package org.hisp.dhis.common;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.legend.LegendSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JacksonXmlRootElement( localName = "dimension", namespace = DxfNamespaces.DXF_2_0 )
public class BaseDimensionalObject
    extends BaseNameableObject implements DimensionalObject
{
    /**
     * The type of this dimension.
     */
    private DimensionType dimensionType;

    /**
     * The name of this dimension. For the dynamic dimensions this will be equal
     * to dimension identifier. For the period dimension, this will reflect the
     * period type. For the org unit dimension, this will reflect the level.
     */
    private String dimensionName;

    /**
     * The dimensional items for this dimension.
     */
    private List<NameableObject> items = new ArrayList<>();

    /**
     * The legend set for this dimension.
     */
    protected LegendSet legendSet;
    
    /**
     * Filter. Applicable for events. Contains operator and filter on this format:
     * <operator>:<filter>;<operator>:<filter>
     * Operator and filter pairs can be repeated any number of times.
     */
    private String filter;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public BaseDimensionalObject()
    {
    }

    public BaseDimensionalObject( String dimension )
    {
        this.uid = dimension;
    }

    public BaseDimensionalObject( String dimension, DimensionType dimensionType, List<? extends NameableObject> items )
    {
        this.uid = dimension;
        this.dimensionType = dimensionType;
        this.items = new ArrayList<>( items );
    }

    public BaseDimensionalObject( String dimension, DimensionType dimensionType, String dimensionName, String displayName, List<? extends NameableObject> items )
    {
        this.uid = dimension;
        this.dimensionType = dimensionType;
        this.dimensionName = dimensionName;
        this.displayName = displayName;
        this.items = new ArrayList<>( items );
    }

    public BaseDimensionalObject( String dimension, DimensionType dimensionType, String dimensionName, String displayName, LegendSet legendSet, String filter )
    {
        this.uid = dimension;
        this.dimensionType = dimensionType;
        this.dimensionName = dimensionName;
        this.displayName = displayName;
        this.legendSet = legendSet;
        this.filter = filter;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    @Override
    public boolean isAllItems()
    {
        return items != null && items.isEmpty();
    }

    @Override
    public boolean hasItems()
    {
        return items != null && !items.isEmpty();
    }
    
    @Override
    public boolean hasLegendSet()
    {
        return legendSet != null;
    }

    @Override
    public String getDimensionName()
    {
        return dimensionName != null ? dimensionName : uid;
    }

    @Override
    public AnalyticsType getAnalyticsType()
    {
        return
            DimensionType.TRACKED_ENTITY_ATTRIBUTE.equals( dimensionType ) ||
                DimensionType.TRACKED_ENTITY_DATAELEMENT.equals( dimensionType ) ?
                AnalyticsType.EVENT : AnalyticsType.AGGREGATE;
    }

    /**
     * Returns the items in the filter as a list. Order of items are preserved.
     * Requires that the filter has the IN operator and that at least one item
     * is specified in the filter, returns null if not.
     */
    public List<String> getFilterItemsAsList()
    {
        final String inOp = QueryOperator.IN.getValue().toLowerCase();
        final int opLen = inOp.length() + 1;

        if ( filter == null || !filter.toLowerCase().startsWith( inOp ) || filter.length() < opLen )
        {
            return null;
        }

        String filterItems = filter.substring( opLen, filter.length() );

        return new ArrayList<>( Arrays.asList( filterItems.split( DimensionalObjectUtils.OPTION_SEP ) ) );
    }

    //--------------------------------------------------------------------------
    // Getters and setters
    //--------------------------------------------------------------------------

    @Override
    @JsonProperty
    @JsonView( { DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDimension()
    {
        return uid;
    }

    public void setDimension( String dimension )
    {
        this.uid = dimension;
    }

    @Override
    @JsonProperty
    @JsonView( { DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DimensionType getDimensionType()
    {
        return dimensionType;
    }

    public void setDimensionType( DimensionType dimensionType )
    {
        this.dimensionType = dimensionType;
    }

    @Override
    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    @JsonDeserialize( contentAs = BaseNameableObject.class )
    @JsonView( { DimensionalView.class } )
    @JacksonXmlElementWrapper( localName = "items", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "item", namespace = DxfNamespaces.DXF_2_0 )
    public List<NameableObject> getItems()
    {
        return items;
    }

    public void setItems( List<NameableObject> items )
    {
        this.items = items;
    }

    @Override
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DimensionalView.class, DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LegendSet getLegendSet()
    {
        return legendSet;
    }

    public void setLegendSet( LegendSet legendSet )
    {
        this.legendSet = legendSet;
    }

    @Override
    @JsonProperty
    @JsonView( { DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getFilter()
    {
        return filter;
    }

    public void setFilter( String filter )
    {
        this.filter = filter;
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    @Override
    public void mergeWith( IdentifiableObject other, MergeStrategy strategy )
    {
        super.mergeWith( other, strategy );

        if ( other.getClass().isInstance( this ) )
        {
            DimensionalObject dimensionalObject = (DimensionalObject) other;

            if ( MergeStrategy.MERGE_ALWAYS.equals( strategy ) )
            {
                dimensionType = dimensionalObject.getDimensionType();
                dimensionName = dimensionalObject.getDimensionName();
                legendSet = dimensionalObject.getLegendSet();
                filter = dimensionalObject.getFilter();
            }
            else if ( MergeStrategy.MERGE_IF_NOT_NULL.equals( strategy ) )
            {
                dimensionType = dimensionalObject.getDimensionType() == null ? dimensionType : dimensionalObject.getDimensionType();
                dimensionName = dimensionalObject.getDimensionName() == null ? dimensionName : dimensionalObject.getDimensionName();
                legendSet = dimensionalObject.getLegendSet() == null ? legendSet : dimensionalObject.getLegendSet();
                filter = dimensionalObject.getFilter() == null ? filter : dimensionalObject.getFilter();
            }

            items.clear();
            items.addAll( dimensionalObject.getItems() );
        }
    }

    @Override
    public String toString()
    {
        return "[" + uid + ", type: " + dimensionType + ", items: " + items + ", legend set: " + legendSet + ", filter: " + filter + "]";
    }
}
