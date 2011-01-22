package org.hisp.dhis.paging;

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

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.system.paging.Paging;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Quang Nguyen
 */
public abstract class ActionPagingSupport<T>
    extends ActionSupport
{
    protected static final Integer DEFAULT_PAGE_SIZE = 50;

    protected Integer currentPage;

    public void setCurrentPage( Integer currentPage )
    {
        this.currentPage = currentPage;
    }

    protected Integer pageSize;

    public void setPageSize( Integer pageSize )
    {
        this.pageSize = pageSize;
    }

    protected Paging paging;

    public Paging getPaging()
    {
        return paging;
    }

    @SuppressWarnings( "unchecked" )
    private String getCurrentLink()
    {
        HttpServletRequest request = ServletActionContext.getRequest();

        String baseLink = request.getRequestURI() + "?";

        Enumeration<String> paramNames = request.getParameterNames();

        while ( paramNames.hasMoreElements() )
        {
            String paramName = paramNames.nextElement();
            if ( !paramName.equalsIgnoreCase( "pageSize" ) && !paramName.equalsIgnoreCase( "currentPage" ) )
            {
                String value = request.getParameter( paramName );
                baseLink += paramName + "=" + value + "&";
            }
        }

        return baseLink.substring( 0, baseLink.length() - 1 );
    }

    protected Paging createPaging( Integer totalRecord )
    {
        Paging resultPaging = new Paging( getCurrentLink(), pageSize == null ? DEFAULT_PAGE_SIZE : pageSize );

        resultPaging.setCurrentPage( currentPage == null ? 0 : currentPage );

        resultPaging.setTotal( totalRecord );

        return resultPaging;
    }

    protected List<T> getBlockElement( List<T> elementList, int startPos, int pageSize )
    {
        List<T> returnList;
        
        int endPos = paging.getEndPos();
        	
        returnList = elementList.subList( startPos, endPos );

        return returnList;
    }
}
