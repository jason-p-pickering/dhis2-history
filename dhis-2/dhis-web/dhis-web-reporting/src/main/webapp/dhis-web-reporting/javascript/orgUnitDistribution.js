
var distributionDivs = [ "chartDiv", "tableDiv", "loaderDiv" ];

function displayOrgUnitDistribution()
{
    if ( inputInvalid() )
    {
        return false;
    }

    displayDiv( "loaderDiv", distributionDivs );

    var groupSetId = $( "#groupSetId" ).val();
    var url = "getOrgUnitDistribution.action?groupSetId=" + groupSetId + "&type=html&" + getDC();
    $( "#tableDiv" ).load( url, function()
    {
        displayDiv( "tableDiv", distributionDivs );
        setTableStyles();
    } );
}

function getOrgUnitDistribution( type )
{
    if ( inputInvalid() )
    {
        return false;
    }

    var groupSetId = $( "#groupSetId" ).val();
    var url = "getOrgUnitDistribution.action?groupSetId=" + groupSetId + "&type=" + type + "&" + getDC();
    window.location.href = url;
}

function displayOrgUnitDistributionChart()
{
    if ( inputInvalid() )
    {
        return false;
    }

    displayDiv( "chartDiv", distributionDivs );
    $( "#chartImg" ).attr( "src", "../images/ajax-loader-circle.gif" );
    var groupSetId = $( "#groupSetId" ).val();
    var source = "getOrgUnitDistributionChart.action?groupSetId=" + groupSetId + "&"  + getDC();
    $( "#chartImg" ).attr( "src", source );
}

function inputInvalid()
{
    var groupSetId = $( "#groupSetId" ).val();

    if ( !selectionTreeSelection.isSelected() )
    {
        setHeaderDelayMessage( i18n_select_org_unit );
        return true;
    }

    if ( groupSetId == null || groupSetId == 0 )
    {
        setHeaderDelayMessage( i18n_select_group_set );
        return true;
    }

    return false;
}
