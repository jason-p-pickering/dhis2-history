package org.hisp.dhis.jdbc.batchhandler;

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id: DataElementBatchHandlerTest.java 4999 2008-04-23 15:45:08Z larshelg $
 */
public class DataElementBatchHandlerTest
    extends DhisTest
{
    private HibernateCacheManager cacheManager;
	
    private BatchHandlerFactory batchHandlerFactory;
    
    private BatchHandler batchHandler;
    
    private DataElementCategoryCombo categoryCombo;
    
    private DataElement dataElementA;
    private DataElement dataElementB;
    private DataElement dataElementC;    

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
    	cacheManager = (HibernateCacheManager) getBean( HibernateCacheManager.ID );
    	
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        batchHandlerFactory = (BatchHandlerFactory) getBean( "batchHandlerFactory" );
        
        batchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class );
        
        categoryComboService = (DataElementCategoryComboService) getBean( DataElementCategoryComboService.ID );
        
        categoryCombo = categoryComboService.getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        
        batchHandler.init();
        
        dataElementA = createDataElement( 'A', categoryCombo );
        dataElementB = createDataElement( 'B', categoryCombo );
        dataElementC = createDataElement( 'C', categoryCombo );
    }

    @Override
    public void tearDownTest()
    {
        batchHandler.flush();
    }
    
    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddObject()
    {
        batchHandler.addObject( dataElementA );
        batchHandler.addObject( dataElementB );
        batchHandler.addObject( dataElementC );
        
        batchHandler.flush();
        
        cacheManager.clearCache();
        
        Collection<DataElement> dataElements = dataElementService.getAllDataElements();
        
        assertTrue( dataElements.contains( dataElementA ) );
        assertTrue( dataElements.contains( dataElementB ) );
        assertTrue( dataElements.contains( dataElementC ) );
    }

    @Test
    public void testInsertObject()
    {
        int idA = batchHandler.insertObject( dataElementA, true );
        int idB = batchHandler.insertObject( dataElementB, true );
        int idC = batchHandler.insertObject( dataElementC, true );

        cacheManager.clearCache();
        
        assertNotNull( dataElementService.getDataElement( idA ) );
        assertNotNull( dataElementService.getDataElement( idB ) );
        assertNotNull( dataElementService.getDataElement( idC ) );
    }

    @Test
    public void testInsertWithSpecialCharacters()
    {
        dataElementA.setDescription( "'quote'" );
        dataElementB.setDescription( "\\backslash\\" );
        dataElementC.setDescription( ";semicolon;" );
        
        batchHandler.insertObject( dataElementA, false );
        batchHandler.insertObject( dataElementB, false );
        batchHandler.insertObject( dataElementC, false );
    }

    @Test
    public void testUpdateObject()
    {
        int id = dataElementService.addDataElement( dataElementA );
        
        dataElementA.setName( "UpdatedName" );
        
        batchHandler.updateObject( dataElementA );

        cacheManager.clearCache();
        
        assertEquals( dataElementService.getDataElement( id ).getName(), "UpdatedName" );
    }

    @Test
    public void testGetObjectIdentifier()
    {
        int referenceId = dataElementService.addDataElement( dataElementA );

        int retrievedId = batchHandler.getObjectIdentifier( "DataElementA" );
        
        assertEquals( referenceId, retrievedId );
    }

    @Test
    public void testObjectExists()
    {
        dataElementService.addDataElement( dataElementA );
        
        assertTrue( batchHandler.objectExists( dataElementA ) );
        
        assertFalse( batchHandler.objectExists( dataElementB ) );
    }
}
