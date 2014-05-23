package org.hisp.dhis.program;

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

import org.hisp.dhis.dataelement.DataElement;

import java.util.Collection;

/**
 * @author Viet Nguyen
 * @version $Id$
 */
public interface ProgramStageDataElementStore
{
    String ID = ProgramStageInstanceStore.class.getName();

    /**
     * Adds an {@link ProgramStageDataElement}
     *
     * @param programStageDataElement The to ProgramStageDataElement add.
     */
    void save( ProgramStageDataElement programStageDataElement );

    /**
     * Updates an {@link ProgramStageDataElement}.
     *
     * @param programStageDataElement the ProgramStageDataElement to update.
     */
    void update( ProgramStageDataElement programStageDataElement );

    /**
     * Deletes a {@link ProgramStageDataElement}.
     *
     * @param programStageDataElement the ProgramStageDataElement to delete.
     */
    void delete( ProgramStageDataElement programStageDataElement );

    /**
     * Retrieve ProgramStageDataElement list on a program stage and a data
     * element
     *
     * @param programStage ProgramStage
     * @param dataElement  DataElement
     * @return ProgramStageDataElement
     */
    ProgramStageDataElement get( ProgramStage programStage, DataElement dataElement );

    /**
     * Returns all {@link ProgramStageDataElement}
     *
     * @return a collection of all ProgramStageDataElement, or an empty
     * collection if there are no ProgramStageDataElements.
     */
    Collection<ProgramStageDataElement> getAll();

    /**
     * Retrieve Data element list on a program stage
     *
     * @param programStage ProgramStage
     * @return ProgramStageDataElement list
     */
    Collection<DataElement> getListDataElement( ProgramStage programStage );
}
