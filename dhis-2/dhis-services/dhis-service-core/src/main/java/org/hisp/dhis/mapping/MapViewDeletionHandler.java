package org.hisp.dhis.mapping;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.deletion.DeletionHandler;

import java.util.Iterator;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class MapViewDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    protected String getClassName()
    {
        return MapView.class.getName();
    }

    @Override
    public String allowDeletePeriod( Period period )
    {
        for ( MapView mapView : mappingService.getAllMapViews() )
        {
            if ( mapView.getPeriods().contains( period ) )
            {
                return mapView.getName();
            }
        }

        return null;
    }

    @Override
    public void deleteIndicator( Indicator indicator )
    {
        Iterator<MapView> mapViews = mappingService.getAllMapViews().iterator();

        while ( mapViews.hasNext() )
        {
            MapView mapView = mapViews.next();

            if ( mapView.getIndicators() != null && mapView.getIndicators().contains( indicator ) )
            {
                mapViews.remove();
                mappingService.deleteMapView( mapView );
            }
        }
    }

    @Override
    public void deleteDataElement( DataElement dataElement )
    {
        Iterator<MapView> mapViews = mappingService.getAllMapViews().iterator();

        while ( mapViews.hasNext() )
        {
            MapView mapView = mapViews.next();

            if ( mapView.getDataElements() != null && mapView.getDataElements().contains( dataElement ) )
            {
                mapViews.remove();
                mappingService.deleteMapView( mapView );
            }
        }
    }

    @Override
    public void deleteOrganisationUnit( OrganisationUnit organisationUnit )
    {
        Iterator<MapView> mapViews = mappingService.getAllMapViews().iterator();

        while ( mapViews.hasNext() )
        {
            MapView mapView = mapViews.next();

            if ( mapView.getOrganisationUnits() != null && mapView.getOrganisationUnits().contains( organisationUnit ) )
            {
                mapViews.remove();
                mappingService.deleteMapView( mapView );
            }
        }
    }

    @Override
    public void deleteMapLegendSet( MapLegendSet mapLegendSet )
    {
        Iterator<MapView> mapViews = mappingService.getAllMapViews().iterator();

        while ( mapViews.hasNext() )
        {
            MapView mapView = mapViews.next();

            if ( mapView.getLegendSet() != null && mapView.getLegendSet().equals( mapLegendSet ) )
            {
                mapViews.remove();
                mappingService.deleteMapView( mapView );
            }
        }
    }

    @Override
    public String allowDeleteMapView( MapView mapView )
    {
        return mappingService.countMapViewMaps( mapView ) == 0 ? null : ERROR;
    }

    @Override
    public String allowDeleteDataSet( DataSet dataSet )
    {
        return mappingService.countDataSetCharts( dataSet ) == 0 ? null : ERROR;
    }

    @Override
    public String allowDeleteIndicator( Indicator indicator )
    {
        return mappingService.countIndicatorCharts( indicator ) == 0 ? null : ERROR;
    }

    @Override
    public String allowDeleteDataElement( DataElement dataElement )
    {
        return mappingService.countDataElementCharts( dataElement ) == 0 ? null : ERROR;
    }
}
