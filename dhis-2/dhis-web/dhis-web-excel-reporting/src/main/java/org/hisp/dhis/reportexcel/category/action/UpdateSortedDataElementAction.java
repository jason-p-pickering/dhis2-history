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

package org.hisp.dhis.reportexcel.category.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.state.SelectionManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class UpdateSortedDataElementAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    private DataElementService dataElementService;

    private SelectionManager selectionManager;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private Integer id;

    private Integer reportId;

    private List<String> dataElementIds = new ArrayList<String>();

    public String message;

    public I18n i18n;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public String getMessage()
    {
        return message;
    }

    public Integer getReportId()
    {
        return reportId;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setDataElementIds( List<String> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public String execute()
        throws Exception
    {
        DataElementGroupOrder dataElementGroupOrder = reportService.getDataElementGroupOrder( id );

        List<DataElement> dataElements = new ArrayList<DataElement>();

        for ( String dataElementId : this.dataElementIds )
        {

            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );
            dataElements.add( dataElement );

        }

        dataElementGroupOrder.setDataElements( dataElements );

        this.message = i18n.getString( "update_sort_dataelement_success" );

        reportService.updateDataElementGroupOrder( dataElementGroupOrder );
        
        reportId = selectionManager.getSelectedReportId();

        return SUCCESS;
    }
}
