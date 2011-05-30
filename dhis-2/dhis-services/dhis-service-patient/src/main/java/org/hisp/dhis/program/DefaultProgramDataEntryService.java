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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;

/**
 * @author Chau Thu Tran
 * @version $ DefaultProgramDataEntryService.java May 26, 2011 3:59:43 PM $
 * 
 */
public class DefaultProgramDataEntryService
    implements ProgramDataEntryService
{
    private static final String EMPTY = "";

    private static final String UNKNOW_CLINIC = "unknow_clinic";

    private static final String NOTAVAILABLE = "not_available";
    
    private static final String DATA_ELEMENT_DOES_NOT_EXIST = "<i>Data element doesn't exist.</i>";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageDataElementService programStageDataElementService;

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public String prepareDataEntryFormForEntry( String htmlCode, Collection<PatientDataValue> dataValues,
        String disabled, I18n i18n, ProgramStage programStage, ProgramStageInstance programStageInstance,
        OrganisationUnit organisationUnit )
    {
        Map<Integer, Collection<PatientDataValue>> mapDataValue = new HashMap<Integer, Collection<PatientDataValue>>();

        String result = "";

        result = populateCustomDataEntryForTextBox( htmlCode, dataValues, disabled, i18n, programStage,
            programStageInstance, organisationUnit, mapDataValue );

        result = populateCustomDataEntryForBoolean( result, dataValues, disabled, i18n, programStage,
            programStageInstance, organisationUnit, mapDataValue );

        result = populateCustomDataEntryForMutiDimentionalString( result, dataValues, disabled, i18n, programStage,
            programStageInstance, organisationUnit, mapDataValue );

        result = populateCustomDataEntryForDate( result, dataValues, disabled, i18n, programStage,
            programStageInstance, organisationUnit, mapDataValue );

        result = populateI18nStrings( result, i18n );

        return result;
    }

    public String prepareDataEntryFormForEdit( String htmlCode )
    {        
        String result = populateCustomDataEntryForTextBox( htmlCode );
        
        result = populateCustomDataEntryForCombo( result );
        
        result = populateCustomDataEntryForBoolean( result );
        
        result = populateCustomDataEntryForDate( result );
        
        return result;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String populateCustomDataEntryForTextBox( String htmlCode )
    {
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String dataElementCode = inputMatcher.group( 1 );
            
            String inputHTML = inputMatcher.group();
            
            Matcher identifierMatcher = IDENTIFIER_PATTERN_TEXTBOX.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                int programStageId = Integer.parseInt( identifierMatcher.group( 1 ) );
                ProgramStage programStage = programStageService.getProgramStage( programStageId );
                Collection<DataElement> dataElements = new ArrayList<DataElement>( programStageDataElementService.getListDataElement( programStage ) );

                int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                int optionComboId = Integer.parseInt( identifierMatcher.group( 3 ) );
                DataElementCategoryOptionCombo optionCombo = categoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                if ( !dataElements.contains( dataElement ) || optionCombo == null )
                {
                    inputMatcher.appendReplacement( sb, DATA_ELEMENT_DOES_NOT_EXIST );
                }
                else
                {
                    inputMatcher.appendReplacement( sb, inputHTML );
                }

            }
        }

        return ( sb.toString().isEmpty() ) ? htmlCode : sb.toString();
    }
    
    private String populateCustomDataEntryForBoolean( String htmlCode )
    {
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = SELECT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        while ( inputMatcher.find() )
        {
            String inputHTML = inputMatcher.group();
            
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String dataElementCode = inputMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_BOOLEAN.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------
                int programStageId = Integer.parseInt( identifierMatcher.group( 1 ) );
                ProgramStage programStage = programStageService.getProgramStage( programStageId );
                Collection<DataElement> dataElements = new ArrayList<DataElement>( programStageDataElementService.getListDataElement( programStage ) );

                int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                
                if ( !dataElements.contains( dataElement ) )
                {
                    inputMatcher.appendReplacement( sb, DATA_ELEMENT_DOES_NOT_EXIST );
                } 
                else
                {
                    inputMatcher.appendReplacement( sb, inputHTML );
                }
            }
        }
        
        return ( sb.toString().isEmpty() ) ? htmlCode : sb.toString();
    }

    private String populateCustomDataEntryForCombo( String htmlCode )
    {
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = SELECT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        while ( inputMatcher.find() )
        {
            String inputHTML = inputMatcher.group();
            
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String dataElementCode = inputMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_COMBO.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------
                int programStageId = Integer.parseInt( identifierMatcher.group( 1 ) );
                ProgramStage programStage = programStageService.getProgramStage( programStageId );
                Collection<DataElement> dataElements = new ArrayList<DataElement>( programStageDataElementService.getListDataElement( programStage ) );

                int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                
                if ( !dataElements.contains( dataElement ) )
                {
                    inputMatcher.appendReplacement( sb, DATA_ELEMENT_DOES_NOT_EXIST );
                } 
                else
                {
                    inputMatcher.appendReplacement( sb, inputHTML );
                }
            }
        }
        
        return ( sb.toString().isEmpty() ) ? htmlCode : sb.toString();
    }

    private String populateCustomDataEntryForDate( String htmlCode )
    {
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        while ( inputMatcher.find() )
        {
            String inputHTML = inputMatcher.group();
            
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String dataElementCode = inputMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_DATE.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------
               
                int programStageId = Integer.parseInt( identifierMatcher.group( 1 ) );
                ProgramStage programStage = programStageService.getProgramStage( programStageId );
                Collection<DataElement> dataElements = new ArrayList<DataElement>( programStageDataElementService.getListDataElement( programStage ) );

                int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                if ( !dataElements.contains( dataElement ) )
                {
                    inputMatcher.appendReplacement( sb, DATA_ELEMENT_DOES_NOT_EXIST );
                } else
                {
                    inputMatcher.appendReplacement( sb, inputHTML );
                }
            }
        }
        
        return ( sb.toString().isEmpty() ) ? htmlCode : sb.toString();
    }

    private String populateCustomDataEntryForTextBox( String dataEntryFormCode,
        Collection<PatientDataValue> dataValues, String disabled, I18n i18n, ProgramStage programStage,
        ProgramStageInstance programStageInstance, OrganisationUnit organisationUnit,
        Map<Integer, Collection<PatientDataValue>> mapDataValue )
    {
        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jsCodeForInputs = " $DISABLED onchange=\"saveValueCustom( this )\" data=\"{compulsory:$COMPULSORY, optionComboId:$OPTIONCOMBOID, dataElementId:$DATAELEMENTID, dataElementName:'$DATAELEMENTNAME', dataElementType:'$DATAELEMENTTYPE', programStageId:$PROGRAMSTAGEID, programStageName: '$PROGRAMSTAGENAME', orgUnitName:'$ORGUNITNAME'}\"  onkeypress=\"return keyPress(event, this)\"   ";

        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
        Matcher dataElementMatcher = INPUT_PATTERN.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        Map<Integer, DataElement> dataElementMap = getDataElementMap( programStage );

        while ( dataElementMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String compulsory = "null";
            String dataElementCode = dataElementMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_TEXTBOX.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                int programStageId = Integer.parseInt( identifierMatcher.group( 1 ) );

                int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );

                int optionComboId = Integer.parseInt( identifierMatcher.group( 3 ) );

                DataElement dataElement = null;

                String programStageName = programStage.getName();

                if ( programStageId != programStage.getId() )
                {
                    dataElement = dataElementService.getDataElement( dataElementId );

                    ProgramStage otherProgramStage = programStageService.getProgramStage( programStageId );
                    programStageName = otherProgramStage != null ? otherProgramStage.getName() : "N/A";

                }
                else
                {
                    dataElement = dataElementMap.get( dataElementId );
                    if ( dataElement == null )
                    {
                        return i18n.getString( "some_data_element_not_exist" );
                    }

                    ProgramStageDataElement psde = programStageDataElementService.get( programStage, dataElement );

                    compulsory = BooleanUtils.toStringTrueFalse( psde.isCompulsory() );
                }

                if ( dataElement == null )
                {
                    continue;
                }
                if ( !DataElement.VALUE_TYPE_INT.equals( dataElement.getType() )
                    && !DataElement.VALUE_TYPE_STRING.equals( dataElement.getType() ) )
                {
                    continue;
                }
                // -------------------------------------------------------------
                // Find type of data element
                // -------------------------------------------------------------

                String dataElementType = dataElement.getDetailedNumberType();

                // -------------------------------------------------------------
                // Find existing value of data element in data set
                // -------------------------------------------------------------

                PatientDataValue patientDataValue = null;

                String dataElementValue = EMPTY;

                if ( programStageId != programStage.getId() )
                {
                    Collection<PatientDataValue> patientDataValues = mapDataValue.get( programStageId );

                    if ( patientDataValues == null )
                    {
                        ProgramStage otherProgramStage = programStageService.getProgramStage( programStageId );
                        ProgramStageInstance otherProgramStageInstance = programStageInstanceService
                            .getProgramStageInstance( programStageInstance.getProgramInstance(), otherProgramStage );
                        patientDataValues = patientDataValueService.getPatientDataValues( otherProgramStageInstance );
                        mapDataValue.put( programStageId, patientDataValues );
                    }

                    patientDataValue = getValue( patientDataValues, dataElementId, optionComboId );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }
                else
                {
                    patientDataValue = getValue( dataValues, dataElementId );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }

                // -------------------------------------------------------------
                // Insert value of data element in output code
                // -------------------------------------------------------------

                if ( dataElementCode.contains( "value=\"\"" ) )
                {
                    dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"" + dataElementValue + "\"" );
                }
                else
                {
                    dataElementCode += "value=\"" + dataElementValue + "\"";
                }

                // -------------------------------------------------------------
                // Remove placeholder view attribute from input field
                // -------------------------------------------------------------

                dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );

                // -------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields
                // -------------------------------------------------------------

                String appendCode = dataElementCode;

                appendCode += jsCodeForInputs;

                appendCode += " />";

                // -----------------------------------------------------------
                // Check if this dataElement is from another programStage then
                // disable
                // If programStagsInstance is completed then disabled it
                // -----------------------------------------------------------

                disabled = "";
                if ( programStageId == programStage.getId() && !programStageInstance.isCompleted() )
                {
                    // -----------------------------------------------------------
                    // Add ProvidedByOtherFacility checkbox
                    // -----------------------------------------------------------

                    appendCode = addProvidedByOtherFacilityCheckbox( appendCode, patientDataValue );

                }
                else
                {
                    disabled = "disabled=\"\"";
                }

                // -----------------------------------------------------------
                // 
                // -----------------------------------------------------------

                String orgUnitName = i18n.getString( NOTAVAILABLE );
                if ( patientDataValue != null )
                {
                    if ( patientDataValue.isProvidedByAnotherFacility() )
                    {
                        orgUnitName = i18n.getString( UNKNOW_CLINIC );
                    }
                    else
                    {
                        orgUnitName = patientDataValue.getOrganisationUnit().getName();
                    }
                }

                appendCode = appendCode.replace( "$DATAELEMENTID", String.valueOf( dataElementId ) );
                appendCode = appendCode.replace( "$PROGRAMSTAGEID", String.valueOf( programStageId ) );
                appendCode = appendCode.replace( "$PROGRAMSTAGENAME", programStageName );
                appendCode = appendCode.replace( "$ORGUNITNAME", orgUnitName );
                appendCode = appendCode.replace( "$OPTIONCOMBOID", String.valueOf( optionComboId ) );
                appendCode = appendCode.replace( "$DATAELEMENTNAME", dataElement.getName() );
                appendCode = appendCode.replace( "$DATAELEMENTTYPE", dataElementType );
                appendCode = appendCode.replace( "$DISABLED", disabled );
                appendCode = appendCode.replace( "$COMPULSORY", compulsory );
                appendCode = appendCode.replace( "$SAVEMODE", "false" );

                dataElementMatcher.appendReplacement( sb, appendCode );
            }
        }

        dataElementMatcher.appendTail( sb );

        return sb.toString();
    }

    private String populateCustomDataEntryForBoolean( String dataEntryFormCode,
        Collection<PatientDataValue> dataValues, String disabled, I18n i18n, ProgramStage programStage,
        ProgramStageInstance programStageInstance, OrganisationUnit organisationUnit,
        Map<Integer, Collection<PatientDataValue>> mapDataValue )
    {

        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jsCodeForBoolean = " name=\"entryselect\" data=\"{compulsory:$COMPULSORY, dataElementId:$DATAELEMENTID, dataElementName:'$DATAELEMENTNAME', dataElementType:'$DATAELEMENTTYPE', programStageId:$PROGRAMSTAGEID, programStageName: '$PROGRAMSTAGENAME', orgUnitName:'$ORGUNITNAME'}\" $DISABLED onchange=\"saveChoiceCustom( $PROGRAMSTAGEID, $DATAELEMENTID,this)\"";

        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String metaDataCode = "<span id=\"value[$DATAELEMENTID].name\" style=\"display:none\">$DATAELEMENTNAME</span>"
            + "<span id=\"value[$DATAELEMENTID].type\" style=\"display:none\">$DATAELEMENTTYPE</span>";
        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher dataElementMatcher = SELECT_PATTERN.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        Map<Integer, DataElement> dataElementMap = getDataElementMap( programStage );

        while ( dataElementMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String compulsory = "null";
            String dataElementCode = dataElementMatcher.group( 1 );
            Matcher identifierMatcher = IDENTIFIER_PATTERN_BOOLEAN.matcher( dataElementCode );
            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                int programStageId = Integer.parseInt( identifierMatcher.group( 1 ) );

                int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );

                DataElement dataElement = null;

                String programStageName = programStage.getName();

                if ( programStageId != programStage.getId() )
                {
                    dataElement = dataElementService.getDataElement( dataElementId );

                    ProgramStage otherProgramStage = programStageService.getProgramStage( programStageId );
                    programStageName = otherProgramStage != null ? otherProgramStage.getName() : "N/A";
                }
                else
                {
                    dataElement = dataElementMap.get( dataElementId );
                    if ( dataElement == null )
                    {
                        return i18n.getString( "some_data_element_not_exist" );
                    }

                    ProgramStageDataElement psde = programStageDataElementService.get( programStage, dataElement );

                    compulsory = BooleanUtils.toStringTrueFalse( psde.isCompulsory() );
                }

                if ( dataElement == null )
                {
                    continue;
                }

                if ( !DataElement.VALUE_TYPE_BOOL.equals( dataElement.getType() ) )
                {
                    continue;
                }

                // -------------------------------------------------------------
                // Find type of data element
                // -------------------------------------------------------------

                String dataElementType = dataElement.getType();

                // -------------------------------------------------------------
                // Find existing value of data element in data set
                // -------------------------------------------------------------

                PatientDataValue patientDataValue = null;

                String dataElementValue = EMPTY;

                if ( programStageId != programStage.getId() )
                {
                    Collection<PatientDataValue> patientDataValues = mapDataValue.get( programStageId );

                    if ( patientDataValues == null )
                    {
                        ProgramStage otherProgramStage = programStageService.getProgramStage( programStageId );
                        ProgramStageInstance otherProgramStageInstance = programStageInstanceService
                            .getProgramStageInstance( programStageInstance.getProgramInstance(), otherProgramStage );
                        patientDataValues = patientDataValueService.getPatientDataValues( otherProgramStageInstance );
                        mapDataValue.put( programStageId, patientDataValues );
                    }

                    patientDataValue = getValue( patientDataValues, dataElementId );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }
                else
                {

                    patientDataValue = getValue( dataValues, dataElementId );

                    if ( patientDataValue != null )
                    {
                        dataElementValue = patientDataValue.getValue();
                    }
                }

                String appendCode = dataElementCode;
                appendCode = appendCode.replace( "name=\"entryselect\"", jsCodeForBoolean );

                // -------------------------------------------------------------
                // Insert value of data element in output code
                // -------------------------------------------------------------

                if ( patientDataValue != null )
                {

                    if ( dataElementValue.equalsIgnoreCase( "true" ) )
                    {
                        appendCode = appendCode.replace( "<option value=\"true\">", "<option value=\""
                            + i18n.getString( "true" ) + "\" selected>" );
                    }

                    if ( dataElementValue.equalsIgnoreCase( "false" ) )
                    {
                        appendCode = appendCode.replace( "<option value=\"false\">", "<option value=\""
                            + i18n.getString( "false" ) + "\" selected>" );
                    }

                }

                appendCode += "</select>";

                // -------------------------------------------------------------
                // Remove placeholder view attribute from input field
                // -------------------------------------------------------------

                dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );

                // -------------------------------------------------------------
                // Insert title information - Data element id, name, type, min,
                // max
                // -------------------------------------------------------------

                if ( dataElementCode.contains( "title=\"\"" ) )
                {
                    dataElementCode = dataElementCode.replace( "title=\"\"", "title=\"-- ID:" + dataElement.getId()
                        + " Name:" + dataElement.getShortName() + " Type:" + dataElement.getType() + "\"" );
                }
                else
                {
                    dataElementCode += "title=\"-- ID:" + dataElement.getId() + " Name:" + dataElement.getShortName()
                        + " Type:" + dataElement.getType() + "\"";
                }

                // -------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields
                // -------------------------------------------------------------

                appendCode += metaDataCode;

                // -----------------------------------------------------------
                // Check if this dataElement is from another programStage then
                // disable
                // If programStagsInstance is completed then disabled it
                // -----------------------------------------------------------

                disabled = "";
                if ( programStageId != programStage.getId() || programStageInstance.isCompleted() )
                {
                    disabled = "disabled";
                }
                else
                {
                    // -----------------------------------------------------------
                    // Add ProvidedByOtherFacility checkbox
                    // -----------------------------------------------------------
                    appendCode = addProvidedByOtherFacilityCheckbox( appendCode, patientDataValue );
                }

                // -----------------------------------------------------------
                // 
                // -----------------------------------------------------------

                String orgUnitName = i18n.getString( NOTAVAILABLE );
                if ( patientDataValue != null )
                {
                    if ( patientDataValue.isProvidedByAnotherFacility() )
                    {
                        orgUnitName = i18n.getString( UNKNOW_CLINIC );
                    }
                    else
                    {
                        orgUnitName = patientDataValue.getOrganisationUnit().getName();
                    }
                }

                appendCode = appendCode.replace( "$DATAELEMENTID", String.valueOf( dataElementId ) );
                appendCode = appendCode.replace( "$PROGRAMSTAGEID", String.valueOf( programStageId ) );
                appendCode = appendCode.replace( "$PROGRAMSTAGENAME", programStageName );
                appendCode = appendCode.replace( "$ORGUNITNAME", orgUnitName );
                appendCode = appendCode.replace( "$DATAELEMENTNAME", dataElement.getName() );
                appendCode = appendCode.replace( "$DATAELEMENTTYPE", dataElementType );
                appendCode = appendCode.replace( "$DISABLED", disabled );
                appendCode = appendCode.replace( "$COMPULSORY", compulsory );
                appendCode = appendCode.replace( "i18n_yes", i18n.getString( "yes" ) );
                appendCode = appendCode.replace( "i18n_no", i18n.getString( "no" ) );
                appendCode = appendCode.replace( "i18n_select_value", i18n.getString( "select_value" ) );
                appendCode = appendCode.replace( "$SAVEMODE", "false" );

                appendCode = appendCode.replaceAll( "\\$", "\\\\\\$" );

                dataElementMatcher.appendReplacement( sb, appendCode );
            }
        }

        dataElementMatcher.appendTail( sb );

        return sb.toString();
    }

    private String populateCustomDataEntryForMutiDimentionalString( String dataEntryFormCode,
        Collection<PatientDataValue> dataValues, String disabled, I18n i18n, ProgramStage programStage,
        ProgramStageInstance programStageInstance, OrganisationUnit organisationUnit,
        Map<Integer, Collection<PatientDataValue>> mapDataValue )
    {

        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jsCodeForCombo = " name=\"entryselect\" $DISABLED data=\"{compulsory:$COMPULSORY, dataElementId:$DATAELEMENTID, dataElementName:'$DATAELEMENTNAME', dataElementType:'$DATAELEMENTTYPE', programStageId:$PROGRAMSTAGEID, programStageName: '$PROGRAMSTAGENAME', orgUnitName:'$ORGUNITNAME'}\" onchange=\"saveChoiceCustom( $PROGRAMSTAGEID, $DATAELEMENTID,this)\"";

        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String metaDataCode = "<span id=\"value[$DATAELEMENTID].name\" style=\"display:none\">$DATAELEMENTNAME</span>"
            + "<span id=\"value[$DATAELEMENTID].type\" style=\"display:none\">$DATAELEMENTTYPE</span>";
        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Matcher dataElementMatcher = SELECT_PATTERN.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        Map<Integer, DataElement> dataElementMap = getDataElementMap( programStage );

        while ( dataElementMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String dataElementCode = dataElementMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_COMBO.matcher( dataElementCode );

            String compulsory = "null";

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {

                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------
                int programStageId = Integer.parseInt( identifierMatcher.group( 1 ) );
                int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );

                DataElement dataElement = null;

                String programStageName = programStage.getName();

                if ( programStageId != programStage.getId() )
                {
                    dataElement = dataElementService.getDataElement( dataElementId );

                    ProgramStage otherProgramStage = programStageService.getProgramStage( programStageId );
                    programStageName = otherProgramStage != null ? otherProgramStage.getName() : "N/A";
                }
                else
                {
                    dataElement = dataElementMap.get( dataElementId );
                    if ( dataElement == null )
                    {
                        return i18n.getString( "some_data_element_not_exist" );
                    }

                    ProgramStageDataElement psde = programStageDataElementService.get( programStage, dataElement );

                    compulsory = BooleanUtils.toStringTrueFalse( psde.isCompulsory() );
                }

                if ( dataElement == null )
                {
                    continue;
                }
                if ( !DataElement.VALUE_TYPE_STRING.equals( dataElement.getType() ) )
                {
                    continue;
                }

                // -------------------------------------------------------------
                // Find type of data element
                // -------------------------------------------------------------

                String dataElementType = dataElement.getType();

                // -------------------------------------------------------------
                // Find existing value of data element in data set
                // -------------------------------------------------------------

                PatientDataValue patientDataValue = null;
                String dataElementValue = EMPTY;
                if ( programStageId != programStage.getId() )
                {
                    Collection<PatientDataValue> patientDataValues = mapDataValue.get( programStageId );

                    if ( patientDataValues == null )
                    {
                        ProgramStage otherProgramStage = programStageService.getProgramStage( programStageId );
                        ProgramStageInstance otherProgramStageInstance = programStageInstanceService
                            .getProgramStageInstance( programStageInstance.getProgramInstance(), otherProgramStage );
                        patientDataValues = patientDataValueService.getPatientDataValues( otherProgramStageInstance );
                        mapDataValue.put( programStageId, patientDataValues );
                    }

                    patientDataValue = getValue( patientDataValues, dataElementId );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }
                else
                {
                    patientDataValue = getValue( dataValues, dataElementId );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }

                String appendCode = dataElementCode;
                appendCode = appendCode.replace( "name=\"entryselect\"", jsCodeForCombo );

                // -------------------------------------------------------------
                // Insert value of data element in output code
                // -------------------------------------------------------------

                if ( patientDataValue != null )
                {
                    appendCode = appendCode.replace( "id=\"combo[" + patientDataValue.getOptionCombo().getId()
                        + "].combo\"", "id=\"combo[" + patientDataValue.getOptionCombo().getId()
                        + "].combo\" selected=\"selected\"" );
                }

                appendCode += "</select>";

                // -------------------------------------------------------------
                // Remove placeholder view attribute from input field
                // -------------------------------------------------------------

                dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );

                // -------------------------------------------------------------
                // Insert title information - Data element id, name, type, min,
                // max
                // -------------------------------------------------------------

                if ( dataElementCode.contains( "title=\"\"" ) )
                {
                    dataElementCode = dataElementCode.replace( "title=\"\"", "title=\"-- ID:" + dataElement.getId()
                        + " Name:" + dataElement.getShortName() + " Type:" + dataElement.getType() + "\"" );
                }
                else
                {
                    dataElementCode += "title=\"-- ID:" + dataElement.getId() + " Name:" + dataElement.getShortName()
                        + " Type:" + dataElement.getType() + "\"";
                }

                // -------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields
                // -------------------------------------------------------------

                appendCode += metaDataCode;

                // -----------------------------------------------------------
                // Check if this dataElement is from another programStage then
                // disable
                // If programStagsInstance is completed then disabled it
                // -----------------------------------------------------------

                disabled = "";
                if ( programStageId != programStage.getId() || programStageInstance.isCompleted() )
                {
                    disabled = "disabled";
                }
                else
                {
                    // -----------------------------------------------------------
                    // Add ProvidedByOtherFacility checkbox
                    // -----------------------------------------------------------

                    appendCode = addProvidedByOtherFacilityCheckbox( appendCode, patientDataValue );
                }

                // -----------------------------------------------------------
                // 
                // -----------------------------------------------------------

                String orgUnitName = i18n.getString( NOTAVAILABLE );
                if ( patientDataValue != null )
                {
                    if ( patientDataValue.isProvidedByAnotherFacility() )
                    {
                        orgUnitName = i18n.getString( UNKNOW_CLINIC );
                    }
                    else
                    {
                        orgUnitName = patientDataValue.getOrganisationUnit().getName();
                    }
                }

                appendCode = appendCode.replace( "$DATAELEMENTID", String.valueOf( dataElementId ) );
                appendCode = appendCode.replace( "$PROGRAMSTAGEID", String.valueOf( programStageId ) );
                appendCode = appendCode.replace( "$PROGRAMSTAGENAME", programStageName );
                appendCode = appendCode.replace( "$ORGUNITNAME", orgUnitName );
                appendCode = appendCode.replace( "$DATAELEMENTNAME", dataElement.getName() );
                appendCode = appendCode.replace( "$DATAELEMENTTYPE", dataElementType );
                appendCode = appendCode.replace( "$DISABLED", disabled );
                appendCode = appendCode.replace( "$COMPULSORY", compulsory );
                appendCode = appendCode.replace( "i18n_select_value", i18n.getString( "select_value" ) );
                appendCode = appendCode.replace( "$SAVEMODE", "false" );
                appendCode = appendCode.replaceAll( "\\$", "\\\\\\$" );

                dataElementMatcher.appendReplacement( sb, appendCode );
            }
        }

        dataElementMatcher.appendTail( sb );

        return sb.toString();
    }

    private String populateCustomDataEntryForDate( String dataEntryFormCode, Collection<PatientDataValue> dataValues,
        String disabled, I18n i18n, ProgramStage programStage, ProgramStageInstance programStageInstance,
        OrganisationUnit organisationUnit, Map<Integer, Collection<PatientDataValue>> mapDataValue )
    {

        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jsCodeForDate = " name=\"entryfield\" $DISABLED onchange=\"saveDateCustom( this )\" data=\"{compulsory:$COMPULSORY, dataElementId:$DATAELEMENTID, dataElementName:'$DATAELEMENTNAME', dataElementType:'$DATAELEMENTTYPE', programStageId:$PROGRAMSTAGEID, programStageName: '$PROGRAMSTAGENAME', orgUnitName:'$ORGUNITNAME'}\"";

        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String jQueryCalendar = "<script> "
            + "datePicker(\"value\\\\\\\\[$PROGRAMSTAGEID\\\\\\\\]\\\\\\\\.date\\\\\\\\:value\\\\\\\\[$DATAELEMENTID\\\\\\\\]\\\\\\\\.date\", false)"
            + ";</script>";

        final String metaDataCode = "<span id=\"value[$DATAELEMENTID].name\" style=\"display:none\">$DATAELEMENTNAME</span>"
            + "<span id=\"value[$DATAELEMENTID].type\" style=\"display:none\">$DATAELEMENTTYPE</span>";
        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Pattern dataElementPattern = Pattern.compile( "(<input.*?)[/]?/>" );
        Matcher dataElementMatcher = dataElementPattern.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Pattern to extract data element ID from data element field
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        Map<Integer, DataElement> dataElementMap = getDataElementMap( programStageInstance.getProgramStage() );

        while ( dataElementMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String compulsory = "null";
            String dataElementCode = dataElementMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN_DATE.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                int programStageId = Integer.parseInt( identifierMatcher.group( 1 ) );
                int dataElementId = Integer.parseInt( identifierMatcher.group( 2 ) );

                DataElement dataElement = null;

                String programStageName = programStage.getName();

                if ( programStageId != programStage.getId() )
                {
                    dataElement = dataElementService.getDataElement( dataElementId );

                    ProgramStage otherProgramStage = programStageService.getProgramStage( programStageId );
                    programStageName = otherProgramStage != null ? otherProgramStage.getName() : "N/A";
                }
                else
                {
                    dataElement = dataElementMap.get( dataElementId );
                    if ( dataElement == null )
                    {
                        return i18n.getString( "some_data_element_not_exist" );
                    }

                    ProgramStageDataElement psde = programStageDataElementService.get( programStage, dataElement );

                    compulsory = BooleanUtils.toStringTrueFalse( psde.isCompulsory() );
                }

                if ( dataElement == null )
                {
                    continue;
                }
                if ( !DataElement.VALUE_TYPE_DATE.equals( dataElement.getType() ) )
                {
                    continue;
                }

                // -------------------------------------------------------------
                // Find type of data element
                // -------------------------------------------------------------

                String dataElementType = dataElement.getType();

                // -------------------------------------------------------------
                // Find existing value of data element in data set
                // -------------------------------------------------------------

                PatientDataValue patientDataValue = null;
                String dataElementValue = EMPTY;

                if ( programStageId != programStage.getId() )
                {
                    Collection<PatientDataValue> patientDataValues = mapDataValue.get( programStageId );

                    if ( patientDataValues == null )
                    {
                        ProgramStage otherProgramStage = programStageService.getProgramStage( programStageId );
                        ProgramStageInstance otherProgramStageInstance = programStageInstanceService
                            .getProgramStageInstance( programStageInstance.getProgramInstance(), otherProgramStage );
                        patientDataValues = patientDataValueService.getPatientDataValues( otherProgramStageInstance );
                        mapDataValue.put( programStageId, patientDataValues );
                    }

                    patientDataValue = getValue( patientDataValues, dataElementId );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }
                else
                {
                    patientDataValue = getValue( dataValues, dataElementId );

                    dataElementValue = patientDataValue != null ? patientDataValue.getValue() : dataElementValue;
                }

                // -------------------------------------------------------------
                // Insert value of data element in output code
                // -------------------------------------------------------------

                if ( dataElementCode.contains( "value=\"\"" ) )
                {
                    dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"" + dataElementValue + "\"" );
                }
                else
                {
                    dataElementCode += "value=\"" + dataElementValue + "\"";
                }

                // -------------------------------------------------------------
                // Remove placeholder view attribute from input field
                // -------------------------------------------------------------

                dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );

                // -------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields
                // -------------------------------------------------------------

                String appendCode = dataElementCode + "/>";
                appendCode = appendCode.replace( "name=\"entryfield\"", jsCodeForDate );

                appendCode += metaDataCode;

                // -------------------------------------------------------------
                // Check if this dataElement is from another programStage then
                // disable
                // If programStagsInstance is completed then disabled it
                // -------------------------------------------------------------

                disabled = "";
                if ( programStageId != programStage.getId() || programStageInstance.isCompleted() )
                {
                    disabled = "disabled=\"\"";
                }
                else
                {
                    appendCode += jQueryCalendar;

                    // ---------------------------------------------------------
                    // Add ProvidedByOtherFacility checkbox
                    // ---------------------------------------------------------

                    appendCode = addProvidedByOtherFacilityCheckbox( appendCode, patientDataValue );

                }

                // -------------------------------------------------------------
                // Get Org Unit name
                // -------------------------------------------------------------

                String orgUnitName = i18n.getString( NOTAVAILABLE );
                if ( patientDataValue != null )
                {
                    if ( patientDataValue.isProvidedByAnotherFacility() )
                    {
                        orgUnitName = i18n.getString( UNKNOW_CLINIC );
                    }
                    else
                    {
                        orgUnitName = patientDataValue.getOrganisationUnit().getName();
                    }
                }

                appendCode = appendCode.replace( "$DATAELEMENTID", String.valueOf( dataElementId ) );

                appendCode = appendCode.replace( "$PROGRAMSTAGEID", String.valueOf( programStageId ) );

                appendCode = appendCode.replace( "$PROGRAMSTAGENAME", programStageName );

                appendCode = appendCode.replace( "$ORGUNITNAME", orgUnitName );

                appendCode = appendCode.replace( "$DATAELEMENTNAME", dataElement.getName() );

                appendCode = appendCode.replace( "$DATAELEMENTTYPE", dataElementType );

                appendCode = appendCode.replace( "$DISABLED", disabled );

                appendCode = appendCode.replace( "$COMPULSORY", compulsory );

                appendCode = appendCode.replace( "$SAVEMODE", "false" );

                appendCode = appendCode.replaceAll( "\\$", "\\\\\\$" );

                dataElementMatcher.appendReplacement( sb, appendCode );
            }
        }

        dataElementMatcher.appendTail( sb );

        return sb.toString();
    }

    /**
     * Returns the value of the PatientDataValue in the Collection of DataValues
     * with the given data element identifier and category option combo id.
     */
    private PatientDataValue getValue( Collection<PatientDataValue> dataValues, int dataElementId,
        int categoryOptionComboId )
    {
        for ( PatientDataValue dataValue : dataValues )
        {
            if ( dataValue.getOptionCombo() != null )
            {
                if ( dataValue.getDataElement().getId() == dataElementId
                    && dataValue.getOptionCombo().getId() == categoryOptionComboId )
                {
                    return dataValue;
                }
            }
        }

        return null;
    }

    /**
     * Returns the value of the PatientDataValue in the Collection of DataValues
     * with the given data element identifier.
     */
    private PatientDataValue getValue( Collection<PatientDataValue> dataValues, int dataElementId )
    {
        for ( PatientDataValue dataValue : dataValues )
        {
            if ( dataValue.getDataElement().getId() == dataElementId )
            {
                return dataValue;
            }
        }

        return null;
    }

    /**
     * Returns a Map of all DataElements in the given ProgramStage where the key
     * is the DataElement identifier and the value is the DataElement.
     */
    private Map<Integer, DataElement> getDataElementMap( ProgramStage programStage )
    {
        Collection<DataElement> dataElements = programStageDataElementService.getListDataElement( programStage );

        if ( programStage == null )
        {
            return null;
        }
        Map<Integer, DataElement> map = new HashMap<Integer, DataElement>();

        for ( DataElement element : dataElements )
        {
            map.put( element.getId(), element );
        }

        return map;
    }

    /**
     * Append a ProvidedByOtherFacility Checkbox to the html code
     * 
     * @param appendCode: current html code
     * @param patientDataValue: currrent PatientDataValue
     * @return full html code after append the check box
     */
    private String addProvidedByOtherFacilityCheckbox( String appendCode, PatientDataValue patientDataValue )
    {
        appendCode += "<label for=\"$PROGRAMSTAGEID_$DATAELEMENTID_facility\" title=\"is provided by another Facility ?\" ></label><input name=\"providedByAnotherFacility\"  title=\"is provided by another Facility ?\"  id=\"$PROGRAMSTAGEID_$DATAELEMENTID_facility\"  type=\"checkbox\" ";

        if ( patientDataValue != null && patientDataValue.isProvidedByAnotherFacility() )
        {
            appendCode += " checked=\"checked\" ";
        }
        appendCode += "onChange=\"updateProvidingFacilityCustom( $PROGRAMSTAGEID, $DATAELEMENTID, this )\"  >";

        return appendCode;

    }

    /**
     * Replaces i18n string in the custom form code.
     * 
     * @param dataEntryFormCode the data entry form html.
     * @param i18n the I18n object.
     * @return internationalized data entry form html.
     */
    private String populateI18nStrings( String dataEntryFormCode, I18n i18n )
    {
        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match i18n strings in the HTML code
        // ---------------------------------------------------------------------

        Pattern i18nPattern = Pattern.compile( "(<i18n.*?)[/]?</i18n>", Pattern.DOTALL );
        Matcher i18nMatcher = i18nPattern.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching i18n element fields
        // ---------------------------------------------------------------------

        while ( i18nMatcher.find() )
        {
            String i18nCode = i18nMatcher.group( 1 );

            i18nCode = i18nCode.replaceAll( "<i18n>", "" );

            i18nCode = i18n.getString( i18nCode );

            i18nMatcher.appendReplacement( sb, i18nCode );
        }

        i18nMatcher.appendTail( sb );

        String result = sb.toString();

        result.replaceAll( "</i18n>", "" );

        return result;
    }

}