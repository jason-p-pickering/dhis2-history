package org.hisp.dhis.dxf2.events.report;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.events.event.Event;
import org.hisp.dhis.dxf2.events.event.EventService;
import org.hisp.dhis.dxf2.events.event.Events;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Abyot Asalefew Gizaw <abyota@gmail.com>
 *
 */
public class AbstractEventRowService
    implements EventRowService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private EventService eventService;
    
    @Autowired
    private IdentifiableObjectManager manager;
    
    @Autowired
    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Override
    public EventRows getOverDueEventRows( Program program, List<OrganisationUnit> organisationUnits, EventStatus status )
    {
        List<EventRow> eventRowList = new ArrayList<EventRow>();
        EventRows eventRows = new EventRows();

        Events events = eventService.getEvents( program, null, null, null, organisationUnits, null, null, null, status );

        for ( Event event : events.getEvents() )
        {
            if ( event.getTrackedEntityInstance() != null )
            {
                TrackedEntityInstance tei =  trackedEntityInstanceService.getTrackedEntityInstance( event.getTrackedEntityInstance() );
                EventRow eventRow = new EventRow();   
                eventRow.setTrackedEntityInstance( event.getTrackedEntityInstance() );
                eventRow.setAttributes( tei.getAttributes() );                
                eventRow.setEvent( event.getEvent() );                
                eventRow.setProgram( program.getUid() );
                eventRow.setProgramStage( event.getProgramStage() );
                eventRow.setEventName( manager.get( ProgramStage.class, event.getProgramStage() ).getName() );
                eventRow.setRegistrationOrgUnit( manager.get( OrganisationUnit.class, tei.getOrgUnit() ).getName() );
                eventRow.setRegistrationDate( tei.getCreated() );
                //eventRow.setOrgUnit( event.getOrgUnit() );
                eventRow.setDueDate( event.getDueDate() );
                eventRow.setFollowup( event.getFollowup() );
                eventRowList.add( eventRow );
            }
        }

        eventRows.setEventRows( eventRowList );

        return eventRows;
    }

    @Override
    public EventRows getUpcomingEventRows( Program program, List<OrganisationUnit> organisationUnits, Date startDate,
        Date endDate, EventStatus eventStatus )
    {
        // TODO Auto-generated method stub
        return null;
    }

}