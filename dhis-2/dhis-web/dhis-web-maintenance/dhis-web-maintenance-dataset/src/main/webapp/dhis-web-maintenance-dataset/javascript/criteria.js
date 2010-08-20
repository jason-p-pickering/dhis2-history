
// -------------------------------------------------------------------------
// Public methods
// -------------------------------------------------------------------------

function addOption( list, value, text )
{
  var option = document.createElement( "option" );
  option.value = value;
  option.text = text;
  list.add( option, null );
}

function loadOrgunitLevels()
{
  var list = document.getElementById( "level" );
  
  $.getJSON(
    "getOrganisationUnitLevels.action",
    function( json )
    {
      for ( var i=0; i<json.organisationUnitLevels.length; i++ )
      {
        var level = json.organisationUnitLevels[i].level;
        var name = json.organisationUnitLevels[i].name;
        
        addOption( list, level, name );
      }
    }
  );
}

function loadPeriodTypes()
{
  var list = document.getElementById( "periodType" );

  $.getJSON(
    "getPeriodTypes.action",
    function( json )
    {
      for ( var i=0; i<json.periodTypes.length; i++ )
      {
        var name = json.periodTypes[i].name;
        
        addOption( list, name, name );
      }
    }
  );
}

function showCriteria()
{
  $( "div#criteria" ).show( "fast" );
}

function hideCriteria()
{
  $( "div#criteria" ).hide( "fast" );
}

function showPivot()
{
  $( "div#pivot" ).show( "fast" );
}

function hidePivot()
{
  $( "div#pivot" ).hide( "fast" );
}

function hideDivs()
{
  hideCriteria();
  hidePivot();
}

