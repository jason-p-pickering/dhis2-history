
function saveDocument()
{
    var name = document.getElementById( "name" );
    
    var url = "validateDocument.action?name=" + name;
    
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( saveDocumentReceived );
    request.send( url );
}

function saveDocumentReceived( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == "input" )
    {
        setMessage( message );
        
        return false;
    }
    else if ( type == "success" )
    {
        document.getElementById( "documentForm" ).submit();
    }
}

function removeDocument( id )
{
    var dialog = window.confirm( i18n_confirm_remove_report );
    
    if ( dialog )
    {
        window.location.href = "removeDocument.action?id=" + id;
    }
}

function addDocumentToDashboard( id )
{
    var dialog = window.confirm( i18n_confirm_add_to_dashboard );
    
    if ( dialog )
    {
        var request = new Request(); 
        request.send( "addDocumentToDashboard.action?id=" + id );
    }
}
