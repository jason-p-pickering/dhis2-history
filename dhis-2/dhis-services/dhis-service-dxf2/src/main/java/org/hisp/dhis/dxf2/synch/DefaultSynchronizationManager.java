package org.hisp.dhis.dxf2.synch;

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

import static org.apache.commons.lang.StringUtils.trimToNull;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.configuration.Configuration;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.CodecUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Lars Helge Overland
 */
public class DefaultSynchronizationManager
    implements SynchronizationManager
{
    private static final Log log = LogFactory.getLog( DefaultSynchronizationManager.class );
    
    private static final String KEY_LAST_SUCCESSFUL_SYNC = "keyLastSuccessfulSynch";
    
    private static CronTrigger CRON = new CronTrigger( "5 * * * * *" ); // Every 5 minutes
    
    private static final String PING_PATH = "/api/system/ping";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    
    @Autowired
    private DataValueSetService dataValueSetService;
    
    @Autowired
    private DataValueService dataValueService;
    
    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private SystemSettingManager systemSettingManager;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private TaskScheduler taskScheduler;

    // -------------------------------------------------------------------------
    // SynchronizatonManager implementation
    // -------------------------------------------------------------------------

    public AvailabilityStatus isRemoteServerAvailable()
    {
        Configuration config = configurationService.getConfiguration();
        
        if ( !isRemoteServerConfigured( config ) )
        {
            return new AvailabilityStatus( false, "Remote server is not configured" );
        }        
        
        String url = config.getRemoteServerUrl() + PING_PATH;
        
        log.info( "Remote server ping URL: " + url + ", username: " + config.getRemoteServerUsername() );
        
        HttpEntity<String> request = getBasicAuthRequestEntity( config.getRemoteServerUsername(), config.getRemoteServerPassword() );
        
        ResponseEntity<String> response = null;
        HttpStatus sc = null;
        AvailabilityStatus status = null;
        
        try
        {
            response = restTemplate.exchange( url, HttpMethod.GET, request, String.class );
            sc = response.getStatusCode();
        }
        catch ( HttpClientErrorException ex )
        {
            sc = ex.getStatusCode();
        }
        catch ( HttpServerErrorException ex )
        {
            sc = ex.getStatusCode();
        }
        catch( ResourceAccessException ex )
        {
            return new AvailabilityStatus( false, "Network is unreachable" );
        }
        
        log.info( "Response: " + response + ", status code: " + sc );
        
        if ( HttpStatus.FOUND.equals( sc ) )
        {
            status = new AvailabilityStatus( false, "Server is available but no authentication was provided" );
        }
        else if ( HttpStatus.UNAUTHORIZED.equals( sc ) )
        {
            status = new AvailabilityStatus( false, "Server is available but authentication failed" );
        }
        else if ( HttpStatus.INTERNAL_SERVER_ERROR.equals( sc ) )
        {
            status = new AvailabilityStatus( false, "Server is available but experienced an internal error" );
        }        
        else if ( HttpStatus.OK.equals( sc ) )
        {
            status = new AvailabilityStatus( true, "Server is available and authentication was successful" );
        }
        else
        {
            status = new AvailabilityStatus( false, "Server is not available for unknown reason: " + sc );
        }
        
        log.info( status );
        
        return status;        
    }
    
    public void enableDataSynch()
    {        
    }
    
    public void disableDataSynch()
    {        
    }
    
    public boolean isDataSynchEnabled()
    {
        return false;
    }
    
    public boolean executeDataSynch()
    {
        AvailabilityStatus availability = isRemoteServerAvailable();
        
        if ( !availability.isAvailable() )
        {
            log.info( "Aborting synch, server not available" );
            return false;
        }

        Date date = getLastSynchSuccess();
        
        int lastUpdatedCount = dataValueService.getDataValueCountLastUpdatedAfter( date );
        
        if ( lastUpdatedCount == 0 )
        {
            log.info( "Aborting synch, no new or updated data values" );
            return false;
        }
        
        // Synch
        
        setLastSynchSuccess();
        
        return true;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Gets the time of the last successful synchronization operation. If not set,
     * the current time is returned.
     */
    private Date getLastSynchSuccess()
    {
        Date date = (Date) systemSettingManager.getSystemSetting( KEY_LAST_SUCCESSFUL_SYNC );
        
        return date != null ? date : new Date();
    }

    /**
     * Sets the time of the last successful synchronization operation.
     */
    private void setLastSynchSuccess()
    {
        systemSettingManager.saveSystemSetting( KEY_LAST_SUCCESSFUL_SYNC, new Date() );
    }

    /**
     * Indicates whether a remote server has been properly configured.
     */
    private boolean isRemoteServerConfigured( Configuration config )
    {
        if ( trimToNull( config.getRemoteServerUrl() ) == null )
        {
            log.info( "Remote server URL not set" );
            return false;
        }
        
        if ( trimToNull( config.getRemoteServerUsername() ) == null || trimToNull( config.getRemoteServerPassword() ) == null )
        {
            log.info( "Remote server username or password not set" );
            return false;
        }
        
        return true;
    }
    
    /**
     * Creates an HTTP entity for requests with appropriate header for basic 
     * authentication.
     */
    private <T> HttpEntity<T> getBasicAuthRequestEntity( String username, String password )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set( HEADER_AUTHORIZATION, CodecUtils.getBasicAuthString( username, password ) ); 
        return new HttpEntity<T>( headers );
    }
}