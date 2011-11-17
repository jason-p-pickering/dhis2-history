package org.hisp.dhis.visualizer.action;

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

import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.visualizer.export.SVGDocument;
import org.hisp.dhis.visualizer.export.SVGUtils;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.util.SessionUtils;
import org.hisp.dhis.util.StreamActionSupport;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */

public class ExportImageAction
    extends StreamActionSupport
{
    private static final Log log = LogFactory.getLog( ExportImageAction.class );

    private static final String SVGDOCUMENT = "SVGDOCUMENT";
    
    private static final String TYPE_PNG = "png";
    
    private static final String TYPE_PDF = "pdf";

    // -------------------------------------------------------------------------
    // Output & input
    // -------------------------------------------------------------------------
    
    private String title;

    public void setTitle( String title )
    {
        this.title = title;
    }

    private String svg;

    public void setSvg( String svg )
    {
        this.svg = svg;
    }

    private Integer width;

    public void setWidth( Integer width )
    {
        this.width = width;
    }

    private Integer height;

    public void setHeight( Integer height )
    {
        this.height = height;
    }
    
    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private SVGDocument svgDocument;

    @Override
    protected String execute( HttpServletResponse response, OutputStream out )
        throws Exception
    {
        if ( title == null || svg == null || width == null || height == null || type == null )
        {
            log.info( "Invalid parameter -> Export map from session" );

            svgDocument = (SVGDocument) SessionUtils.getSessionVar( SVGDOCUMENT );
        }
        else
        {
            svgDocument = new SVGDocument();
            
            svgDocument.setTitle( title );
            svgDocument.setSvg( svg );
            svgDocument.setWidth( width );
            svgDocument.setHeight( height );
            
            SessionUtils.setSessionVar( SVGDOCUMENT, svgDocument );
        }
        
        if ( type.equals( TYPE_PNG ) )
        {
            SVGUtils.convertToPNG( svgDocument.getSVGForImage(), out, width, height );
        }
        
        else if ( type.equals( TYPE_PDF ))
        {
            SVGUtils.convertToPDF( svgDocument.getSVGForImage(), out );
        }

        return SUCCESS;
    }

    @Override
    protected String getContentType()
    {
        return type.equals( TYPE_PDF ) ? ContextUtils.CONTENT_TYPE_PDF : ContextUtils.CONTENT_TYPE_PNG;
    }

    @Override
    protected String getFilename()
    {
        return "dhis2_dv_" + CodecUtils.filenameEncode( title ) + "." + CodecUtils.filenameEncode( type );
    }
    
    @Override
    protected boolean disallowCache()
    {
        return true;
    }
}
