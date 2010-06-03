package org.hisp.dhis.importexport.dxf.converter;

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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.DataElementGroupSetImporter;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementGroupSetConverter
    extends DataElementGroupSetImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "dataElementGroupSets";
    public static final String ELEMENT_NAME = "dataElementGroupSet";
    
    private static final String FIELD_ID = "id";    
    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_NAME = "name";

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataElementGroupSetConverter( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public DataElementGroupSetConverter( BatchHandler<DataElementGroupSet> batchHandler, 
        ImportObjectService importObjectService,
        DataElementService dataElementService )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    @Override
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElementGroupSet> groupSets = dataElementService.getDataElementGroupSets( params.getDataElementGroupSets() );
        
        if ( groupSets != null && groupSets.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataElementGroupSet groupSet : groupSets )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( groupSet.getId() ) );
                writer.writeElement( FIELD_UUID, groupSet.getUuid() );
                writer.writeElement( FIELD_NAME, groupSet.getName() );
    
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }

    @Override
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final DataElementGroupSet groupSet = new DataElementGroupSet();

            groupSet.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            groupSet.setUuid( values.get( FIELD_UUID ) );
            groupSet.setName( values.get( FIELD_NAME ) );
            
            importObject( groupSet, params );
        }
    }
}
