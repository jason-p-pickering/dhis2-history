package org.hisp.dhis.reportexcel.importing.action;

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

import java.io.File;

import org.hisp.dhis.reportexcel.ReportLocationManager;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.state.SelectionManager;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author Tran Thanh Tri
 * @version $Id
 */

public class UploadExcelFileAction
    extends ActionSupport
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private File upload;

    public void setUpload( File upload )
    {
        this.upload = upload;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        File directory = reportLocationManager.getReportExcelTempDirectory();

        File output = new File( directory, (Math.random() * 1000) + fileName );

        selectionManager.setUploadFilePath( output.getAbsolutePath() );

        StreamUtils.write( upload, output );

        return SUCCESS;
    }
}
