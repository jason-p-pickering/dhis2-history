package org.hisp.dhis.common;

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

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "pager", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Pager
{
    public static final int DEFAULT_PAGE_SIZE = 50;

    private int page = 1;

    private int total = 0;

    private int pageSize = Pager.DEFAULT_PAGE_SIZE;

    public Pager()
    {

    }

    public Pager( int page, int total )
    {
        this.page = page;
        this.total = total;

        if ( this.page > getPageCount() )
        {
            this.page = getPageCount();
        }

        if ( this.page < 1 )
        {
            this.page = 1;
        }
    }

    public Pager( int page, int total, int pageSize )
    {
        this.page = page;
        this.total = total;
        this.pageSize = pageSize;

        if ( this.page > getPageCount() )
        {
            this.page = getPageCount();
        }

        if ( this.page < 1 )
        {
            this.page = 1;
        }
    }

    @XmlElement
    @JsonProperty
    public int getPage()
    {
        return page;
    }

    @XmlElement
    @JsonProperty
    public int getTotal()
    {
        return total;
    }

    @XmlElement
    @JsonProperty
    public int getPageSize()
    {
        return pageSize;
    }

    @XmlElement
    @JsonProperty
    public int getPageCount()
    {
        int pageCount = 1;
        int totalTmp = total;

        while ( totalTmp > pageSize )
        {
            totalTmp -= pageSize;
            pageCount++;
        }

        return pageCount;
    }

    public int getOffset()
    {
        return (page * pageSize) - pageSize;
    }
}
