package org.hisp.dhis.user;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.deletion.DeletionHandler;

/**
 * @author Lars Helge Overland
 */
public class UserDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return User.class.getSimpleName();
    }

    @Override
    public void deleteUserAuthorityGroup( UserAuthorityGroup authorityGroup )
    {
        for ( UserCredentials credentials : authorityGroup.getMembers() )
        {
            credentials.getUserAuthorityGroups().remove( authorityGroup );
            userService.updateUserCredentials( credentials );
        }
    }
    
    @Override
    public void deleteOrganisationUnit( OrganisationUnit unit )
    {
        for ( User user : unit.getUsers() )
        {
            user.getOrganisationUnits().remove( unit );
            userService.updateUser( user );
        }
    }

    @Override
    public void deleteUserGroup( UserGroup group )
    {        
        for ( User user : group.getMembers() )
        {
            user.getGroups().remove( group );
            userService.updateUser( user );
        }
    }

    @Override
    public String allowDeleteUserAuthorityGroup( UserAuthorityGroup authorityGroup )
    {
        for ( UserCredentials credentials : userService.getAllUserCredentials() )
        {
            for ( UserAuthorityGroup role : credentials.getUserAuthorityGroups() )
            {
                if ( role.equals( authorityGroup ) )
                {
                    return credentials.getName();
                }
            }
        }

        return null;
    }
}
