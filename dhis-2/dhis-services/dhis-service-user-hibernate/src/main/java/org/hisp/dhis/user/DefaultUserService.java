package org.hisp.dhis.user;

import java.util.Collection;

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

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class DefaultUserService
    implements UserService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    public boolean isSuperUser( UserCredentials userCredentials )
    {
        if ( userCredentials == null )
        {
            return false;
        }

        for ( UserAuthorityGroup group : userCredentials.getUserAuthorityGroups() )
        {
            if ( group.getAuthorities().contains( "ALL" ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean isLastSuperUser( UserCredentials userCredentials )
    {
        Collection<UserCredentials> users = userStore.getAllUserCredentials();
        
        for ( UserCredentials user : users )
        {
            if ( isSuperUser( user ) && user.getId() != userCredentials.getId() )
            {
                return false;
            }
        }

        return true;
    }

    public boolean isSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        if ( userAuthorityGroup == null )
        {
            return false;
        }

        return (userAuthorityGroup.getAuthorities().contains( "ALL" )) ? true : false;
    }

    public boolean isLastSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        Collection<UserAuthorityGroup> groups = userStore.getAllUserAuthorityGroups();

        for ( UserAuthorityGroup group : groups )
        {
            if ( isSuperRole( group ) && group.getId() != userAuthorityGroup.getId() )
            {
                return false;
            }
        }

        return true;
    }
}
