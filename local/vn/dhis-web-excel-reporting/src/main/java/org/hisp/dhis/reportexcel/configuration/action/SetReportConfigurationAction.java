package org.hisp.dhis.reportexcel.configuration.action;

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

import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.reportexcel.ReportLocationManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

public class SetReportConfigurationAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------------

    private String templateDirectory;

    public void setTemplateDirectory( String templateDirectory )
    {
        this.templateDirectory = templateDirectory;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {   
        File templateDir = reportLocationManager.getReportExcelTemplateDirectory();
        
        if ( templateDir != null && templateDir.exists() )
        {
            deleteDirectory ( reportLocationManager.getReportExcelTemplateDirectory() );
        }
        
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY, templateDirectory );

        return SUCCESS;
    }
}
