/*
 * Copyright (c) 2004-2011, University of Oslo
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

package org.hisp.dhis.attribute;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.BaseIdentifiableObjectXmlAdapter;
import org.hisp.dhis.common.adapter.JsonIdentifiableObjectSerializer;
import org.hisp.dhis.common.adapter.JsonIdentifiableObjectSetSerializer;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

/**
 * @author mortenoh
 */
@XmlRootElement( name = "attributeValue", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class AttributeValue
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6625127769248931066L;

    private int id;

    private Attribute attribute;

    private String value;

    public AttributeValue()
    {

    }

    public AttributeValue( String value )
    {
        this.value = value;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @XmlElement( name = "attribute" )
    @XmlJavaTypeAdapter( BaseIdentifiableObjectXmlAdapter.class )
    @JsonSerialize( using = JsonIdentifiableObjectSerializer.class )
    public Attribute getAttribute()
    {
        return attribute;
    }

    public void setAttribute( Attribute attribute )
    {
        this.attribute = attribute;
    }

    @XmlElement
    @JsonProperty
    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
}
