package org.hisp.dhis.webapi.controller.event;

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

import com.google.common.collect.Lists;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.dxf2.fieldfilter.FieldFilterService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.objectfilter.ObjectFilterService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.program.ProgramStatus;
import org.hisp.dhis.schema.descriptors.TrackedEntityInstanceSchemaDescriptor;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams;
import org.hisp.dhis.webapi.controller.exception.NotFoundException;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.ContextUtils.CacheStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = TrackedEntityInstanceSchemaDescriptor.API_ENDPOINT )
@PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKED_ENTITY_INSTANCE_SEARCH')" )
public class TrackedEntityInstanceController
{
    @Autowired
    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Autowired
    private org.hisp.dhis.trackedentity.TrackedEntityInstanceService instanceService;

    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private FieldFilterService fieldFilterService;

    @Autowired
    private ObjectFilterService objectFilterService;

    @Autowired
    private ContextService contextService;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET, produces = { ContextUtils.CONTENT_TYPE_JSON, ContextUtils.CONTENT_TYPE_JAVASCRIPT } )
    public String queryTrackedEntityInstancesJson(
        @RequestParam( required = false ) String query,
        @RequestParam( required = false ) Set<String> attribute,
        @RequestParam( required = false ) Set<String> filter,
        @RequestParam( required = false ) String ou,
        @RequestParam( required = false ) OrganisationUnitSelectionMode ouMode,
        @RequestParam( required = false ) String program,
        @RequestParam( required = false ) ProgramStatus programStatus,
        @RequestParam( required = false ) Boolean followUp,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date programStartDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date programEndDate,
        @RequestParam( required = false ) String trackedEntity,
        @RequestParam( required = false ) EventStatus eventStatus,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date eventStartDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date eventEndDate,
        @RequestParam( required = false ) boolean skipMeta,
        @RequestParam( required = false ) Integer page,
        @RequestParam( required = false ) Integer pageSize,
        @RequestParam( required = false ) boolean skipPaging,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        Set<String> orgUnits = ContextUtils.getQueryParamValues( ou );
        TrackedEntityInstanceQueryParams params = instanceService.getFromUrl( query, attribute, filter, orgUnits, ouMode,
            program, programStatus, followUp, programStartDate, programEndDate, trackedEntity,
            eventStatus, eventStartDate, eventEndDate, skipMeta, page, pageSize, skipPaging );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, CacheStrategy.NO_CACHE );
        Grid grid = instanceService.getTrackedEntityInstances( params );

        model.addAttribute( "model", grid );
        model.addAttribute( "viewClass", "detailed" );
        return "grid";
    }

    @RequestMapping( method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_XML )
    public void queryTrackedEntityInstancesXml(
        @RequestParam( required = false ) String query,
        @RequestParam( required = false ) Set<String> attribute,
        @RequestParam( required = false ) Set<String> filter,
        @RequestParam( required = false ) String ou,
        @RequestParam( required = false ) OrganisationUnitSelectionMode ouMode,
        @RequestParam( required = false ) String program,
        @RequestParam( required = false ) ProgramStatus programStatus,
        @RequestParam( required = false ) Boolean followUp,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date programStartDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date programEndDate,
        @RequestParam( required = false ) String trackedEntity,
        @RequestParam( required = false ) EventStatus eventStatus,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date eventStartDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date eventEndDate,
        @RequestParam( required = false ) boolean skipMeta,
        @RequestParam( required = false ) Integer page,
        @RequestParam( required = false ) Integer pageSize,
        @RequestParam( required = false ) boolean skipPaging,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        Set<String> orgUnits = ContextUtils.getQueryParamValues( ou );
        TrackedEntityInstanceQueryParams params = instanceService.getFromUrl( query, attribute, filter, orgUnits, ouMode,
            program, programStatus, followUp, programStartDate, programEndDate, trackedEntity,
            eventStatus, eventStartDate, eventEndDate, skipMeta, page, pageSize, skipPaging );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.NO_CACHE );
        Grid grid = instanceService.getTrackedEntityInstances( params );
        GridUtils.toXml( grid, response.getOutputStream() );
    }

    @RequestMapping( method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_EXCEL )
    public void queryTrackedEntityInstancesXls(
        @RequestParam( required = false ) String query,
        @RequestParam( required = false ) Set<String> attribute,
        @RequestParam( required = false ) Set<String> filter,
        @RequestParam( required = false ) String ou,
        @RequestParam( required = false ) OrganisationUnitSelectionMode ouMode,
        @RequestParam( required = false ) String program,
        @RequestParam( required = false ) ProgramStatus programStatus,
        @RequestParam( required = false ) Boolean followUp,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date programStartDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date programEndDate,
        @RequestParam( required = false ) String trackedEntity,
        @RequestParam( required = false ) EventStatus eventStatus,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date eventStartDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date eventEndDate,
        @RequestParam( required = false ) boolean skipMeta,
        @RequestParam( required = false ) Integer page,
        @RequestParam( required = false ) Integer pageSize,
        @RequestParam( required = false ) boolean skipPaging,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        Set<String> orgUnits = ContextUtils.getQueryParamValues( ou );
        TrackedEntityInstanceQueryParams params = instanceService.getFromUrl( query, attribute, filter, orgUnits, ouMode,
            program, programStatus, followUp, programStartDate, programEndDate, trackedEntity,
            eventStatus, eventStartDate, eventEndDate, skipMeta, page, pageSize, skipPaging );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, CacheStrategy.NO_CACHE );
        Grid grid = instanceService.getTrackedEntityInstances( params );
        GridUtils.toXls( grid, response.getOutputStream() );
    }

    @RequestMapping( method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_CSV )
    public void queryTrackedEntityInstancesCsv(
        @RequestParam( required = false ) String query,
        @RequestParam( required = false ) Set<String> attribute,
        @RequestParam( required = false ) Set<String> filter,
        @RequestParam( required = false ) String ou,
        @RequestParam( required = false ) OrganisationUnitSelectionMode ouMode,
        @RequestParam( required = false ) String program,
        @RequestParam( required = false ) ProgramStatus programStatus,
        @RequestParam( required = false ) Boolean followUp,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date programStartDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date programEndDate,
        @RequestParam( required = false ) String trackedEntity,
        @RequestParam( required = false ) EventStatus eventStatus,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date eventStartDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date eventEndDate,
        @RequestParam( required = false ) boolean skipMeta,
        @RequestParam( required = false ) Integer page,
        @RequestParam( required = false ) Integer pageSize,
        @RequestParam( required = false ) boolean skipPaging,
        Model model,
        HttpServletResponse response ) throws Exception
    {
        Set<String> orgUnits = ContextUtils.getQueryParamValues( ou );
        TrackedEntityInstanceQueryParams params = instanceService.getFromUrl( query, attribute, filter, orgUnits, ouMode,
            program, programStatus, followUp, programStartDate, programEndDate, trackedEntity,
            eventStatus, eventStartDate, eventEndDate, skipMeta, page, pageSize, skipPaging );

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.NO_CACHE );
        Grid grid = instanceService.getTrackedEntityInstances( params );
        GridUtils.toCsv( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKED_ENTITY_INSTANCE_SEARCH')" )
    public @ResponseBody RootNode getTrackedEntityInstanceById( @PathVariable( "id" ) String pvId )
        throws NotFoundException
    {
        List<TrackedEntityInstance> trackedEntityInstances = Lists.newArrayList( getTrackedEntityInstance( pvId ) );
        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );

        if ( fields.isEmpty() )
        {
            fields.add( ":all" );
        }

        RootNode rootNode = new RootNode( "metadata" );
        rootNode.setDefaultNamespace( DxfNamespaces.DXF_2_0 );
        rootNode.setNamespace( DxfNamespaces.DXF_2_0 );

        rootNode.addChild( fieldFilterService.filter( TrackedEntityInstance.class, trackedEntityInstances, fields ) );

        return rootNode;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKED_ENTITY_INSTANCE_ADD')" )
    public void postTrackedEntityInstanceXml( @RequestParam( defaultValue = "CREATE" ) ImportStrategy strategy, HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        ImportSummaries importSummaries = trackedEntityInstanceService.addTrackedEntityInstanceXml( request.getInputStream(), strategy );
        response.setContentType( MediaType.APPLICATION_XML_VALUE );

        if ( importSummaries.getImportSummaries().size() > 1 )
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            JacksonUtils.toXml( response.getOutputStream(), importSummaries );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            ImportSummary importSummary;

            if ( !importSummaries.getImportSummaries().isEmpty() )
            {
                importSummary = importSummaries.getImportSummaries().get( 0 );

                if ( !importSummary.getStatus().equals( ImportStatus.ERROR ) )
                {
                    response.setHeader( "Location", getResourcePath( request, importSummary ) );
                }
            }
            else
            {
                importSummary = new ImportSummary( ImportStatus.SUCCESS, "Empty list of tracked entity instances given." );
                importSummary.setDataValueCount( null );
                importSummary.setImportCount( null );
            }

            JacksonUtils.toXml( response.getOutputStream(), importSummary );
        }
    }

    @RequestMapping( value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKED_ENTITY_INSTANCE_ADD')" )
    public void postTrackedEntityInstanceJson( @RequestParam( defaultValue = "CREATE" ) ImportStrategy strategy, HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        ImportSummaries importSummaries = trackedEntityInstanceService.addTrackedEntityInstanceJson( request.getInputStream(), strategy );
        response.setContentType( MediaType.APPLICATION_JSON_VALUE );

        if ( importSummaries.getImportSummaries().size() > 1 )
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            JacksonUtils.toJson( response.getOutputStream(), importSummaries );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_CREATED );
            ImportSummary importSummary;

            if ( !importSummaries.getImportSummaries().isEmpty() )
            {
                importSummary = importSummaries.getImportSummaries().get( 0 );

                if ( !importSummary.getStatus().equals( ImportStatus.ERROR ) )
                {
                    response.setHeader( "Location", getResourcePath( request, importSummary ) );
                }
            }
            else
            {
                importSummary = new ImportSummary( ImportStatus.SUCCESS, "Empty list of tracked entity instances given." );
                importSummary.setDataValueCount( null );
                importSummary.setImportCount( null );
            }

            JacksonUtils.toJson( response.getOutputStream(), importSummary );
        }
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKED_ENTITY_INSTANCE_ADD')" )
    public void updateTrackedEntityInstanceXml( @PathVariable String id, HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstanceXml( id, request.getInputStream() );

        response.setContentType( MediaType.APPLICATION_XML_VALUE );
        JacksonUtils.toXml( response.getOutputStream(), importSummary );
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKED_ENTITY_INSTANCE_ADD')" )
    public void updateTrackedEntityInstanceJson( @PathVariable String id, HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        ImportSummary importSummary = trackedEntityInstanceService.updateTrackedEntityInstanceJson( id, request.getInputStream() );

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJson( response.getOutputStream(), importSummary );
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{id}", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKED_ENTITY_INSTANCE_ADD')" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void deleteTrackedEntityInstance( @PathVariable String id )
        throws NotFoundException
    {
        TrackedEntityInstance trackedEntityInstance = getTrackedEntityInstance( id );
        trackedEntityInstanceService.deleteTrackedEntityInstance( trackedEntityInstance );
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private TrackedEntityInstance getTrackedEntityInstance( String id )
        throws NotFoundException
    {
        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService.getTrackedEntityInstance( id );

        if ( trackedEntityInstance == null )
        {
            throw new NotFoundException( "TrackedEntityInstance", id );
        }
        return trackedEntityInstance;
    }

    private String getResourcePath( HttpServletRequest request, ImportSummary importSummary )
    {
        return ContextUtils.getContextPath( request ) + "/api/" + "trackedEntityInstances" + "/" + importSummary.getReference();
    }

    // -------------------------------------------------------------------------
    // Exception handling
    // -------------------------------------------------------------------------

    @ExceptionHandler( IllegalQueryException.class )
    public void handleError( IllegalQueryException ex, HttpServletResponse response )
    {
        ContextUtils.conflictResponse( response, ex.getMessage() );
    }
}
