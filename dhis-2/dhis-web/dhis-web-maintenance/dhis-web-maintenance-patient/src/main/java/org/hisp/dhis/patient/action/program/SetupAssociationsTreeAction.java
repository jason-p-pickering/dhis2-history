package org.hisp.dhis.patient.action.program;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;


import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: SetupAssociationsTreeAction.java 5652 2008-09-06 13:24:34Z larshelg $
 */
public class SetupAssociationsTreeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    
    private int id;

    public int getProgramId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
    
    private Program program;
    
    public Program getProgram()
    {
        return program;
    }
    
    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        program = programService.getProgram( id );
        
        selectionTreeManager.setSelectedOrganisationUnits( convert( program.getOrganisationUnits() ) );
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Set<OrganisationUnit> convert( Collection<OrganisationUnit> orgUnits )
    {
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
        
        for ( OrganisationUnit orgUnit : orgUnits )
        {        	
            organisationUnits.add( orgUnit );
        }       
        
        return organisationUnits;
    }
}
