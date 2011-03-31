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

package org.hisp.dhis.patient.startup;

import org.hisp.dhis.program.ProgramAttribute;
import org.hisp.dhis.program.ProgramAttributeService;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version ProgramAttributePopulator.java Nov 9, 2010 12:22:29 PM
 */
public class ProgramAttributePopulator
    extends AbstractStartupRoutine
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramAttributeService programAttributeService;

    public void setProgramAttributeService( ProgramAttributeService programAttributeService )
    {
        this.programAttributeService = programAttributeService;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
        throws Exception
    {
        ProgramAttribute attribute = programAttributeService.getProgramAttributeByName( ProgramAttribute.DEAD_NAME );

        if ( attribute == null )
        {
            attribute = new ProgramAttribute();
            attribute.setName( ProgramAttribute.DEAD_NAME );
            attribute.setDescription( "Date when patient unenrolls the program" );
            attribute.setValueType( ProgramAttribute.TYPE_BOOL );

            programAttributeService.saveProgramAttribute( attribute );
        }

        attribute = programAttributeService.getProgramAttributeByName( ProgramAttribute.CLOSED_DATE );

        if ( attribute == null )
        {
            attribute = new ProgramAttribute();
            attribute.setName( ProgramAttribute.CLOSED_DATE );
            attribute.setDescription( "Date when patient unenrolls the program" );
            attribute.setValueType( ProgramAttribute.TYPE_DATE );

            programAttributeService.saveProgramAttribute( attribute );
        }
    }

}
