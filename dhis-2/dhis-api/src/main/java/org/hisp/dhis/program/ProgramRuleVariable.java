package org.hisp.dhis.program;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author markusbekken
 */
@JacksonXmlRootElement( localName = "programRuleVariable", namespace = DxfNamespaces.DXF_2_0 )
public class ProgramRuleVariable
    extends BaseNameableObject
{
    /**
     * The program that the variable belongs to
     */
    private Program program;

    /**
     * The type of the variable, used by the rules engine to know how to quote
     * or convert the value. The allowed types are number, text, bool, date.
     */
    private ProgramRuleVariableDataType dataType;

    /**
     * The value that the variable should have initially. Usually the default
     * value is replaced by a value of the connected dataelement, or by a
     * calculation performed by rules. If not, the default value would be used
     * in evaluating rules.
     * */
    private String defaultValue;

    /**
     * The source of the variables content. Allowed values are:
     * dataelement_newest_event_program_stage Get a specific data elements value
     * from the most recent event in the current enrollment, but within one
     * program stage. dataelement_uID and programstage_uID needs to be
     * specified. dataelement_newest_event_program Get a specific data elements
     * value from the most recent event in the current enrollment, regardless of
     * program stage.datalement_uID needs to be specified.
     * dataelement_current_event Get a specific data elements value, but only
     * within the current event. dataelement_previous_event Get a specific
     * data elements value, specifically from the event preceding the current
     * event, if this exists. calculated_value Do not assign the variable a
     * hard-linked source, it will be populated by rules with “assignvariable”
     * actions(i.e. calculation rules). tei_attribute Get a specific attribute
     * from the current tracked entity. the linked attribute will be used to
     * lookup the attributes uID value.
     */
    private ProgramRuleVariableSourceType sourceType;

    /**
     * Used for sourceType tei_attribute to determine which attribute to fetch
     * into the variable.
     */
    private TrackedEntityAttribute attribute;

    /**
     * The data element that is linked to the variable. Must de defined if the
     * sourceType is one of the following:
     * dataelement_newest_event_program_stage dataelement_newest_event_program
     * dataelement_current_event
     */
    private DataElement dataElement;

    /**
     * Specification of the program stage that the variable should be fetched
     * from. Only used for source type dataelement_newest_event_program_stage
     */
    private ProgramStage programStage;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramRuleVariable()
    {
        setAutoFields();
    }

    public ProgramRuleVariable( String name, 
            String description,
            Program program, 
            ProgramRuleVariableDataType dataType,
            String defaultValue,
            ProgramRuleVariableSourceType sourceType,
            TrackedEntityAttribute attribute,
            DataElement dataElement,
            ProgramStage programStage)
    {
        this();
        this.name = name;
        this.description = description;
        this.program = program;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
        this.sourceType = sourceType;
        this.attribute = attribute;
        this.dataElement = dataElement;
        this.programStage = programStage;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public TrackedEntityAttribute getAttribute()
    {
        return attribute;
    }

    public void setAttribute( TrackedEntityAttribute attribute )
    {
        this.attribute = attribute;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue( String defaultValue )
    {
        this.defaultValue = defaultValue;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ProgramRuleVariableDataType getDataType()
    {
        return dataType;
    }

    public void setDataType( ProgramRuleVariableDataType dataType )
    {
        this.dataType = dataType;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ProgramRuleVariableSourceType getSourceType()
    {
        return sourceType;
    }

    public void setSourceType( ProgramRuleVariableSourceType sourceType )
    {
        this.sourceType = sourceType;
    }
}
