package org.hisp.dhis.dataelement;

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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dimension.Dimension;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.dimension.DimensionOptionElement;

/**
 * DataElementGroupSet is a set of DataElementGroups. It is by default exclusive,
 * in the sense that a DataElement can only be a member of one or zero of the 
 * DataElementGroups in a DataElementGroupSet.
 * 
 * @author Lars Helge Overland
 */
public class DataElementGroupSet
    extends Dimension
{
    private List<DataElementGroup> members = new ArrayList<DataElementGroup>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementGroupSet()
    {   
    }
    
    public DataElementGroupSet( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Dimension
    // -------------------------------------------------------------------------

    public List<? extends DimensionOption> getDimensionOptions()
    {
        return members;
    }
    
    public DimensionOption getDimensionOption( DimensionOptionElement dimensionOptionElement )
    {
        for ( DimensionOption dimensionOption : members )
        {
            if ( dimensionOption.getDimensionOptionElements().contains( dimensionOptionElement ) )
            {
                return dimensionOption;
            }
        }
        
        return null;
    }
    
    // -------------------------------------------------------------------------
    // equals and hashCode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !( o instanceof DataElementGroupSet ) )
        {
            return false;
        }

        final DataElementGroupSet other = (DataElementGroupSet) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public List<DataElementGroup> getMembers()
    {
        return members;
    }

    public void setMembers( List<DataElementGroup> members )
    {
        this.members = members;
    }    
}
