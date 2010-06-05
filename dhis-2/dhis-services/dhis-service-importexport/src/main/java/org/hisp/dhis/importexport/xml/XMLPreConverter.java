package org.hisp.dhis.importexport.xml;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

/**
 *
 * @author bobj
 * @version created 31-May-2010
 */
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.framework.XMLPipe;
import org.amplecode.staxwax.reader.DefaultXMLEventReader;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.transformer.TransformerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.importexport.ImportException;
import org.hisp.dhis.importexport.ImportParams;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GenericXMLConvertor transforms imported foreign XML to dxf
 *
 * @author bobj
 */
@Component("preConverter")
public class XMLPreConverter
{

    private final Log log = LogFactory.getLog( XMLPreConverter.class );

    public static final String DXF_ROOT = "dxf";

    public static final int BUFFER_SIZE = 2000;

    public static final String TRANSFORMERS_CONFIG = "transform/transforms.xml";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    protected XSLTLocator xsltLocator;

    @Autowired
    protected URIResolver resolver;

    /**
     * This method is called for an anonymous xml stream ie. we don't yet know if or how to transform it
     * @param xmlDataStream
     * @param reader
     * @param params
     * @param state
     * @return
     * @throws ImportException
     */
    public XMLReader transform( InputStream xmlDataStream, ImportParams params, ProcessState state ) throws ImportException
    {
        XMLReader dxfReader = null;

        String xsltIdentifierTag = null;

        Map<String, String> xsltParams = null;

        QName rootName = null;


        BufferedInputStream bufin = new BufferedInputStream( xmlDataStream );
        Map<QName, String> attributes = new HashMap<QName, String>();


        try
        {
             XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory.newInstance();
             XMLStreamReader2 streamReader = (XMLStreamReader2) factory.createXMLStreamReader( xmlDataStream );

            // buffer enough space to read root element
            bufin.mark( BUFFER_SIZE );

            // move to document root
            streamReader.nextTag();
            rootName = streamReader.getName();
            int attributeCount = streamReader.getAttributeCount();
            for ( int i = 0; i < attributeCount; ++i )
            {
                QName attribute = streamReader.getAttributeName( i );
                String value = streamReader.getAttributeValue( i );
                attributes.put( attribute, value );
            }
            bufin.reset();


            log.info( "Importing " + rootName.toString() );

            // first test if its a dxf stream
            if ( rootName.getLocalPart().equals( DXF_ROOT ) )
            {
                // Native DXF stream - no transform required

                // -----------------------------------------------------------------
                // Retrieve namespace and minor version from root element and set on
                // import params. Use default if not found.
                // -----------------------------------------------------------------

                params.setNamespace( defaultIfEmpty( rootName.getNamespaceURI(), ImportParams.NAMESPACE_10 ) );
                String version = attributes.get( new QName( ImportParams.ATTRIBUTE_MINOR_VERSION ) );
                params.setMinorVersion( version != null ? version : ImportParams.MINOR_VERSION_10 );

                log.info( "Using DXF namespace '" + params.getNamespace() + "' version '" + params.getMinorVersion() + "'" );

                // no transform required
                dxfReader = XMLFactory.getXMLReader( streamReader );

            } else
            {        // use the stringified form of the qname as an id
                xsltIdentifierTag = rootName.toString();
                log.debug( "Tag for transformer: " + xsltIdentifierTag );

                dxfReader = this.transform( streamReader, params, state, xsltParams, xsltIdentifierTag );
            }
        } catch ( Exception ex )
        {
            log.info( ex );
            throw new ImportException( "Failed to transform xml stream" );
        }
        return dxfReader;
    }

    /**
     *
     * @param streamReader
     * @param params
     * @param state
     * @param xsltParams
     * @param xsltTag
     * @return
     * @throws Exception
     */
    public DefaultXMLEventReader transform( XMLStreamReader2 streamReader, ImportParams params, ProcessState state,
        Map<String, String> xsltParams, String xsltTag )
        throws Exception
    {
        DefaultXMLEventReader dxfReader;
        InputStream sheetStream = xsltLocator.getTransformerByTag( xsltTag );
        Source sheet = new StreamSource( sheetStream );
        TransformerTask tt = new TransformerTask( sheet, xsltParams );

        Source source = new StAXSource( streamReader );

        XMLPipe pipe = new XMLPipe(); // Make a pipe to capture output of transform
        XMLEventWriter pipeinput = pipe.getInput();
        XMLEventReader2 pipeoutput = pipe.getOutput();

        StAXResult result = new StAXResult( pipeinput ); // Set result of transform to input of pipe
        //tt.transform( source, result, resolver );
        tt.transform( source, result, null );
        log.info( "Transform successful" );

        // Set dxfReader to output of pipe
        dxfReader = new DefaultXMLEventReader( (XMLEventReader2) pipeoutput );

        streamReader.close();

        return dxfReader;
    }
}