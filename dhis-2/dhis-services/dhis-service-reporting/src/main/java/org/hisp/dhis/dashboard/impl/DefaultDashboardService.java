package org.hisp.dhis.dashboard.impl;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.dashboard.DashboardItem.TYPE_CHART;
import static org.hisp.dhis.dashboard.DashboardItem.TYPE_MAP;
import static org.hisp.dhis.dashboard.DashboardItem.TYPE_REPORTS;
import static org.hisp.dhis.dashboard.DashboardItem.TYPE_REPORT_TABLES;
import static org.hisp.dhis.dashboard.DashboardItem.TYPE_RESOURCES;
import static org.hisp.dhis.dashboard.DashboardItem.TYPE_USERS;

import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.dashboard.DashboardItem;
import org.hisp.dhis.dashboard.DashboardSearchResult;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Note: The remove associations methods must be altered if caching is introduced.
 * 
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultDashboardService
    implements DashboardService
{
    private static final int MAX_PER_OBJECT = 5;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateIdentifiableObjectStore<Dashboard> dashboardStore;
    
    public void setDashboardStore( HibernateIdentifiableObjectStore<Dashboard> dashboardStore )
    {
        this.dashboardStore = dashboardStore;
    }

    @Autowired
    private IdentifiableObjectManager objectManager;
    
    // -------------------------------------------------------------------------
    // DashboardService implementation
    // -------------------------------------------------------------------------

    @Override
    public DashboardSearchResult search( String query )
    {
        DashboardSearchResult result = new DashboardSearchResult();
        
        result.setUsers( objectManager.getBetweenByName( User.class, query, 0, MAX_PER_OBJECT ) );
        result.setCharts( objectManager.getBetweenByName( Chart.class, query, 0, MAX_PER_OBJECT ) );
        result.setMaps( objectManager.getBetweenByName( Map.class, query, 0, MAX_PER_OBJECT ) );
        result.setReportTables( objectManager.getBetweenByName( ReportTable.class, query, 0, MAX_PER_OBJECT ) );
        result.setReports( objectManager.getBetweenByName( Report.class, query, 0, MAX_PER_OBJECT ) );
        result.setResources( objectManager.getBetweenByName( Document.class, query, 0, MAX_PER_OBJECT ) );
        
        return result;
    }

    @Override
    public void addItemContent( String dashboardUid, String type, String contentUid )
    {
        Dashboard dashboard = getDashboard( dashboardUid );               
        
        if ( TYPE_CHART.equals( type ) )
        {
            DashboardItem item = new DashboardItem();
            item.setChart( objectManager.get( Chart.class, contentUid ) );
            dashboard.getItems().add( 0, item );
        }
        else if ( TYPE_MAP.equals( type ) )
        {
            DashboardItem item = new DashboardItem();
            item.setMap( objectManager.get( Map.class, contentUid ) );
            dashboard.getItems().add( 0, item );
        }
        else // Link item
        {
            DashboardItem availableItem = dashboard.getAvailableItemByType( type );
            
            DashboardItem item = availableItem == null ? new DashboardItem() : availableItem;
            
            if ( TYPE_USERS.equals( type ) )
            {
                item.getUsers().add( objectManager.get( User.class, contentUid ) );
            }
            else if ( TYPE_REPORT_TABLES.equals( type ) )
            {
                item.getReportTables().add( objectManager.get( ReportTable.class, contentUid ) );
            }
            else if ( TYPE_REPORTS.equals( type ) )
            {
                item.getReports().add( objectManager.get( Report.class, contentUid ) );
            }
            else if ( TYPE_RESOURCES.equals( type ) )
            {
                item.getResources().add( objectManager.get( Document.class, contentUid ) );
            }
            
            if ( availableItem == null )
            {
                dashboard.getItems().add( 0, item );
            }
        }
        
        updateDashboard( dashboard );
    }
    
    public void mergeDashboard( Dashboard dashboard )
    {
        if ( dashboard.getItems() != null )
        {
            for ( DashboardItem item : dashboard.getItems() )
            {
                mergeDashboardItem( item );
            }
        }
    }
    
    public void mergeDashboardItem( DashboardItem item )
    {
        if ( item.getChart() != null )
        {
            item.setChart( objectManager.get( Chart.class, item.getChart().getUid() ) );
        }
        
        if ( item.getMap() != null )
        {
            item.setMap( objectManager.get( Map.class, item.getMap().getUid() ) );
        }
        
        if ( item.getUsers() != null )
        {
            item.setUsers( objectManager.getByUid( User.class, getUids( item.getUsers() ) ) );
        }
        
        if ( item.getReportTables() != null )
        {
            item.setReportTables( objectManager.getByUid( ReportTable.class, getUids( item.getReportTables() ) ) );
        }
        
        if ( item.getReports() != null )
        {
            item.setReports( objectManager.getByUid( Report.class, getUids( item.getReports() ) ) );
        }
        
        if ( item.getResources() != null )
        {
            item.setResources( objectManager.getByUid( Document.class, getUids( item.getResources() ) ) );
        }
    }

    @Override
    public int saveDashboard( Dashboard dashboard )
    {
        return dashboardStore.save( dashboard );
    }

    @Override
    public void updateDashboard( Dashboard dashboard )
    {
        dashboardStore.update( dashboard );
    }

    @Override
    public void deleteDashboard( Dashboard dashboard )
    {
        dashboardStore.delete( dashboard );
    }

    @Override
    public Dashboard getDashboard( int id )
    {
        return dashboardStore.get( id );
    }

    @Override
    public Dashboard getDashboard( String uid )
    {
        return dashboardStore.getByUid( uid );
    }

    @Override
    public List<Dashboard> getByUser( User user )
    {
        return dashboardStore.getByUser( user );
    }
}
