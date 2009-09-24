package org.hisp.dhis.vn.chr.object.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */


public class ListRelativeObjectAction
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormManager formManager;

    public void setFormManager( FormManager formManager )
    {
        this.formManager = formManager;
    }

    private FormService formService;

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    private ElementService elementService;

    public void setElementService( ElementService elementService )
    {
        this.elementService = elementService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer formId;

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    private String objectId;

    public void setObjectId( String objectId )
    {
        this.objectId = objectId;
    }

    private Form form;

    public Form getForm()
    {
        return form;
    }

    private ArrayList<Object> data;

    public ArrayList<Object> getData()
    {
        return data;
    }

    private Collection<Element> formLinks;

    public Collection<Element> getFormLinks()
    {
        return formLinks;
    }

    private String column;

    public void setColumn( String column )
    {
        this.column = column;
    }

    private ArrayList<String> parentObject;

    public ArrayList<String> getParentObject()
    {
        return parentObject;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        form = formService.getForm( formId.intValue() );

        formLinks = elementService.getElementsByFormLink( form );

        int numberOfRecords = Integer.parseInt( (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_CHR_NUMBER_OF_RECORDS ) );

        data = formManager.listRelativeObject( form, column, objectId, numberOfRecords );

        if ( objectId != null )
        {

            for ( Element element : form.getElements() )
            {

                if ( element.getFormLink() != null )
                {

                    Form fparent = element.getFormLink();

                    ArrayList<String> data = formManager.getObject( fparent, Integer.parseInt( objectId ) );

                    parentObject = new ArrayList<String>();

                    int k = 1;

                    for ( Egroup egroup : fparent.getEgroups() )
                    {
                        for ( Element e : egroup.getElements() )
                        {

                            if ( data.get( k ) != null )
                                parentObject.add( e.getLabel() + " : " + data.get( k ) );
                            k++;
                            if ( k == fparent.getNoColumnLink() )
                                break;
                        }// end for element

                        if ( k == fparent.getNoColumnLink() )
                            break;
                    }// end for egroup
                }
            }
        }

        return SUCCESS;
    }
}
