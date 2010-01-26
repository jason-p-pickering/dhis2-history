package org.hisp.dhis.reportexcel.filemanager.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.reportexcel.utils.FileUtils;

import com.opensymphony.xwork2.Action;

public class ExcelTemplateListAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private SystemSettingManager systemSettingManager;

    // -------------------------------------------
    // Output
    // -------------------------------------------
    private Collection<File> templateFiles = new ArrayList<File>();

    // -------------------------------------------
    // Getter && Setter
    // -------------------------------------------

    public Collection<File> getTemplateFiles()
    {
        return templateFiles;
    }

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------
    // Action implementation
    // -------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        String templateDirectory = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY );
        
        templateFiles = FileUtils.getListFile( new File( templateDirectory ) );

        return SUCCESS;
    }

}
