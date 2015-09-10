package org.hisp.dhis.organisationunit;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class OrganisationUnitStoreTest
    extends DhisSpringTest
{
    @Autowired
    private OrganisationUnitLevelStore orgUnitLevelStore;
    
    @Autowired
    private OrganisationUnitStore orgUnitStore;
    
    @Autowired
    private OrganisationUnitGroupStore orgUnitGroupStore;

    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    @Test
    public void testGetOrganisationUnits()
    {
        OrganisationUnit ouA = createOrganisationUnit( 'A' );
        OrganisationUnit ouB = createOrganisationUnit( 'B', ouA );
        OrganisationUnit ouC = createOrganisationUnit( 'C', ouA );
        OrganisationUnit ouD = createOrganisationUnit( 'D', ouB );
        OrganisationUnit ouE = createOrganisationUnit( 'E', ouB );
        OrganisationUnit ouF = createOrganisationUnit( 'F', ouC );
        OrganisationUnit ouG = createOrganisationUnit( 'G', ouC );
        
        orgUnitStore.save( ouA );
        orgUnitStore.save( ouB );
        orgUnitStore.save( ouC );
        orgUnitStore.save( ouD );
        orgUnitStore.save( ouE );
        orgUnitStore.save( ouF );
        orgUnitStore.save( ouG );
        
        OrganisationUnitGroup ogA = createOrganisationUnitGroup( 'A' );
        ogA.getMembers().addAll( Sets.newHashSet( ouD, ouF ) );
        OrganisationUnitGroup ogB = createOrganisationUnitGroup( 'B' );
        ogB.getMembers().addAll( Sets.newHashSet( ouE, ouG ) );
        
        orgUnitGroupStore.save( ogA );
        orgUnitGroupStore.save( ogB );
        
        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        params.setQuery( "UnitC" );
        
        List<OrganisationUnit> ous = orgUnitStore.getOrganisationUnits( params );

        assertEquals( 1, ous.size() );
        assertTrue( ous.contains( ouC ) );
        
        params = new OrganisationUnitQueryParams();
        params.setQuery( "OrganisationUnitCodeA" );
        
        ous = orgUnitStore.getOrganisationUnits( params );
        
        assertTrue( ous.contains( ouA ) );
        assertEquals( 1, ous.size() );

        params = new OrganisationUnitQueryParams();
        params.setParents( Sets.newHashSet( ouC, ouF ) );

        ous = orgUnitStore.getOrganisationUnits( params );

        assertEquals( 3, ous.size() );
        assertTrue( ous.containsAll( Sets.newHashSet( ouC, ouF, ouG ) ) );

        params = new OrganisationUnitQueryParams();
        params.setGroups( Sets.newHashSet( ogA ) );

        ous = orgUnitStore.getOrganisationUnits( params );

        assertEquals( 2, ous.size() );
        assertTrue( ous.containsAll( Sets.newHashSet( ouD, ouF ) ) );

        params = new OrganisationUnitQueryParams();
        params.setParents( Sets.newHashSet( ouC ) );        
        params.setGroups( Sets.newHashSet( ogB ) );

        ous = orgUnitStore.getOrganisationUnits( params );

        assertEquals( 1, ous.size() );
        assertTrue( ous.containsAll( Sets.newHashSet( ouG ) ) );        
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetOrganisationUnitLevel()
    {
        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        int idA = orgUnitLevelStore.save( levelA );
        int idB = orgUnitLevelStore.save( levelB );

        assertEquals( levelA, orgUnitLevelStore.get( idA ) );
        assertEquals( levelB, orgUnitLevelStore.get( idB ) );
    }

    @Test
    public void testGetOrganisationUnitLevels()
    {
        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        orgUnitLevelStore.save( levelA );
        orgUnitLevelStore.save( levelB );

        List<OrganisationUnitLevel> actual = orgUnitLevelStore.getAll();

        assertNotNull( actual );
        assertEquals( 2, actual.size() );
        assertTrue( actual.contains( levelA ) );
        assertTrue( actual.contains( levelB ) );
    }

    @Test
    public void testRemoveOrganisationUnitLevel()
    {
        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        int idA = orgUnitLevelStore.save( levelA );
        int idB = orgUnitLevelStore.save( levelB );

        assertNotNull( orgUnitLevelStore.get( idA ) );
        assertNotNull( orgUnitLevelStore.get( idB ) );

        orgUnitLevelStore.delete( levelA );

        assertNull( orgUnitLevelStore.get( idA ) );
        assertNotNull( orgUnitLevelStore.get( idB ) );

        orgUnitLevelStore.delete( levelB );

        assertNull( orgUnitLevelStore.get( idA ) );
        assertNull( orgUnitLevelStore.get( idB ) );
    }
}