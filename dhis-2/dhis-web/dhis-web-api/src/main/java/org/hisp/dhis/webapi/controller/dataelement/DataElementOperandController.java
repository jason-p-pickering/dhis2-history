package org.hisp.dhis.webapi.controller.dataelement;

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

import com.google.common.collect.Lists;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementOperandService;
import org.hisp.dhis.schema.descriptors.DataElementOperandSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.controller.WebMetaData;
import org.hisp.dhis.webapi.controller.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = DataElementOperandSchemaDescriptor.API_ENDPOINT )
public class DataElementOperandController extends AbstractCrudController<DataElementOperand>
{
    private DataElementOperandService dataElementOperandService;

    @Autowired
    public void setDataElementOperandService( DataElementOperandService dataElementOperandService )
    {
        this.dataElementOperandService = dataElementOperandService;
    }

    protected List<DataElementOperand> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<DataElementOperand> entityList;

        if ( options.getOptions().containsKey( "query" ) )
        {
            entityList = Lists.newArrayList( manager.filter( getEntityClass(), options.getOptions().get( "query" ) ) );
        }
        else if ( options.getOptions().containsKey( "dataElementGroup" ) )
        {
            DataElementGroup dataElementGroup = manager.get( DataElementGroup.class, options.getOptions().get( "dataElementGroup" ) );

            if ( dataElementGroup == null )
            {
                entityList = new ArrayList<>();
            }
            else
            {
                entityList = new ArrayList<>( dataElementOperandService.getDataElementOperandByDataElementGroup( dataElementGroup ) );
            }
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = new ArrayList<>( dataElementOperandService.getAllDataElementOperands(
                pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<>( dataElementOperandService.getAllDataElementOperands() );
        }

        return entityList;
    }

    @RequestMapping( value = "/query/{query}", method = RequestMethod.GET )
    public String query( @PathVariable String query, @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        WebMetaData metaData = new WebMetaData();

        List<DataElementOperand> dataElementOperands = Lists.newArrayList();

        for ( DataElementOperand dataElementOperand : dataElementOperandService.getAllDataElementOperands() )
        {
            if ( dataElementOperand.getDisplayName().toLowerCase().contains( query.toLowerCase() ) )
            {
                dataElementOperands.add( dataElementOperand );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataElementOperands.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataElementOperands = PagerUtils.pageCollection( dataElementOperands, pager );
        }

        metaData.setDataElementOperands( dataElementOperands );

        String viewClass = options.getViewClass( "basic" );

        handleLinksAndAccess( options, dataElementOperands );

        postProcessEntities( dataElementOperands );
        postProcessEntities( dataElementOperands, options, parameters );

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", viewClass );

        return StringUtils.uncapitalize( getEntitySimpleName() ) + "List";
    }
}
