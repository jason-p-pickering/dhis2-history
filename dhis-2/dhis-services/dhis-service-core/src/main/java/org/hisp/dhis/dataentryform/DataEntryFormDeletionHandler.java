package org.hisp.dhis.dataentryform;

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

import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormAssociation;
import org.hisp.dhis.dataentryform.DataEntryFormAssociationService;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.system.deletion.DeletionHandler;
import org.hisp.dhis.system.deletion.DeletionManager;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataEntryFormDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    private DeletionManager deletionManager;

    public void setDeletionManager( DeletionManager deletionManager )
    {
        this.deletionManager = deletionManager;
    }

    private DataEntryFormAssociationService dataEntryFormAssociationService;

    public void setDataEntryFormAssociationService( DataEntryFormAssociationService dataEntryFormAssociationService )
    {
        this.dataEntryFormAssociationService = dataEntryFormAssociationService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return DataEntryForm.class.getSimpleName();
    }

    @Override
    public void deleteDataSet( DataSet dataSet )
    {
        DataEntryFormAssociation dataEntryFormAssociation = dataEntryFormAssociationService
            .getDataEntryFormAssociation( DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSet.getId() );

        if ( dataEntryFormAssociation != null )
        {
            DataEntryForm dataEntryForm = dataEntryFormAssociation.getDataEntryForm();

            if ( dataEntryForm != null )
            {                
                deletionManager.execute( dataEntryForm );

                dataEntryFormService.deleteDataEntryForm( dataEntryForm );
            }
        }
    }
}
