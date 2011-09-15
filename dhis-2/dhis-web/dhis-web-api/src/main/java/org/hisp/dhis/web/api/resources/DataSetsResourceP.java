package org.hisp.dhis.web.api.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.dxf2.service.DataSetMapper;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilder;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilderImpl;
import org.hisp.dhis.web.api.UrlResourceListener;
import org.springframework.beans.factory.annotation.Required;

import com.sun.jersey.api.json.JSONWithPadding;

@Path( "jsonp" )
public class DataSetsResourceP
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    @Required
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    @Context
    private UriInfo uriInfo;
    
    // -------------------------------------------------------------------------
    // JSONP
    // -------------------------------------------------------------------------

    @GET
    @Path("dataSets")
    @Produces( { "application/x-javascript" } )
    public JSONWithPadding getDataSets( @QueryParam( "callback" ) @DefaultValue( "callback" ) String callback )
    {
        Collection<DataSet> dataSets = dataSetService.getAllDataSets();
        Map<String, Object> dataSetOutput = new HashMap<String, Object>();

        List<Map<String, Object>> dataSetsArray = new ArrayList<Map<String,Object>>();

        for(DataSet dataSet : dataSets)
        {
            Map<String, Object> dataSetMap = new HashMap<String, Object>();
            dataSetMap.put( "id", dataSet.getUuid() );
            dataSetMap.put( "href", uriInfo.getAbsolutePath().toASCIIString() + "/" + dataSet.getUuid() );
            dataSetMap.put( "name", dataSet.getName() );

            dataSetsArray.add( dataSetMap );
        }

        dataSetOutput.put( "dataSets", dataSetsArray );

        return new JSONWithPadding( dataSetOutput, callback );
    }

    @GET
    @Path("dataSets/{uuid}")
    @Produces( { "application/x-javascript" } )
    public JSONWithPadding getDataSet( @PathParam("uuid") String uuid, @QueryParam( "callback" ) @DefaultValue( "callback" ) String callback )
    {
        DataSet dataSet = dataSetService.getDataSet( uuid );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "No dataset with uuid " + uuid );
        }

        org.hisp.dhis.importexport.dxf2.model.DataSet dxfDataSet = new DataSetMapper().convert( dataSet );
        new UrlResourceListener( uriInfo ).beforeMarshal( dxfDataSet );
        
        return new JSONWithPadding( dxfDataSet, callback );
    }
}
