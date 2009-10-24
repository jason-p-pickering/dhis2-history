package org.hisp.dhis.mapping.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.hisp.dhis.mapping.MappingService;

import com.opensymphony.xwork2.Action;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public class AddOrUpdateMapViewAction
    implements Action
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
    // Input
    // -------------------------------------------------------------------------
    
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    private int indicatorGroupId;
    
    public void setIndicatorGroupId( int indicatorGroupId )
    {
        this.indicatorGroupId = indicatorGroupId;
    }
    
    private int indicatorId;
    
    public void setIndicatorId( int indicatorId )
    {
        this.indicatorId = indicatorId;
    }
    
    private String periodTypeId;
    
    public void setPeriodTypeId( String periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }
    
    private int periodId;
    
    public void setPeriodId( int periodId )
    {
        this.periodId = periodId;
    }
    
    private String mapSource;
    
    public void setMapSource( String mapSource )
    {
        this.mapSource = mapSource;
    }
    
    private int method;
    
    public void setMethod( int method )
    {
        this.method = method;
    }
    
    private int classes;
    
    public void setClasses( int classes )
    {
        this.classes = classes;
    }
    
    private String colorLow;
    
    public void setColorLow( String colorLow )
    {
        this.colorLow = colorLow;
    }
    
    private String colorHigh;
    
    public void setColorHigh( String colorHigh )
    {
        this.colorHigh = colorHigh;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        mappingService.addOrUpdateMapView( name, indicatorGroupId, indicatorId, periodTypeId, periodId, mapSource,
            method, classes, colorLow, colorHigh );
        
        return SUCCESS;
    }
}