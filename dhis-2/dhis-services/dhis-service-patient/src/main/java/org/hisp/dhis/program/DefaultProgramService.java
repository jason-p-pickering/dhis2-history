/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.program;

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class DefaultProgramService
    implements ProgramService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStore programStore;

    /**
     * @param programStore the programStore to set
     */
    public void setProgramStore( ProgramStore programStore )
    {
        this.programStore = programStore;
    }

    // -------------------------------------------------------------------------
    // Program
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.program.ProgramService#addProgram(org.hisp.dhis.program
     * .Program)
     */
    public int addProgram( Program program )
    {
        // TODO Auto-generated method stub
        return programStore.addProgram( program );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.program.ProgramService#deleteProgram(org.hisp.dhis.program
     * .Program)
     */
    public void deleteProgram( Program program )
    {
        // TODO Auto-generated method stub
        
        programStore.deleteProgram( program );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hisp.dhis.program.ProgramService#getAllPrograms()
     */
    public Collection<Program> getAllPrograms()
    {
        // TODO Auto-generated method stub
        return programStore.getAllPrograms();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hisp.dhis.program.ProgramService#getProgram(int)
     */
    public Program getProgram( int id )
    {
        // TODO Auto-generated method stub
        return programStore.getProgram( id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.program.ProgramService#getProgram(org.hisp.dhis.dataset
     * .DataSet)
     */
    public Collection<Program> getPrograms( DataSet dataSet )
    {
        // TODO Auto-generated method stub
        return programStore.getPrograms( dataSet );
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.hisp.dhis.program.ProgramService#getPrograms(org.hisp.dhis.
     * organisationunit.OrganisationUnit)
     */
    public Collection<Program> getPrograms( OrganisationUnit organisationUnit )
    {
        // TODO Auto-generated method stub
        return programStore.getPrograms( organisationUnit );
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.hisp.dhis.program.ProgramService#getPrograms(org.hisp.dhis.
     * organisationunit.OrganisationUnit, org.hisp.dhis.dataset.DataSet)
     */
    public Collection<Program> getPrograms( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        // TODO Auto-generated method stub
        return programStore.getPrograms( organisationUnit, dataSet );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.program.ProgramService#updateProgram(org.hisp.dhis.program
     * .Program)
     */
    public void updateProgram( Program program )
    {
        // TODO Auto-generated method stub
        programStore.updateProgram( program );
    }

}
