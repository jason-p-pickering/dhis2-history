
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showIndicatorTypeDetails( indicatorTypeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'indicatorType' );
    request.setCallbackSuccess( indicatorTypeReceived );
    request.send( 'getIndicatorType.action?id=' + indicatorTypeId );
}

function indicatorTypeReceived( indicatorTypeElement )
{
    setFieldValue( 'nameField', getElementValue( indicatorTypeElement, 'name' ) );
    setFieldValue( 'factorField', getElementValue( indicatorTypeElement, 'factor' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove indicator type
// -----------------------------------------------------------------------------

function removeIndicatorType( indicatorTypeId, indicatorTypeName )
{
	removeItem( indicatorTypeId, indicatorTypeName, i18n_confirm_delete, 'removeIndicatorType.action' );
}

// -----------------------------------------------------------------------------
// Add indicator type
// -----------------------------------------------------------------------------

function validateAddIndicatorType()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateIndicatorType.action?name=' + getFieldValue( 'name' ) +
        '&factor=' + getFieldValue( 'factor' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addIndicatorTypeForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_indicator_type_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update indicator type
// -----------------------------------------------------------------------------

function validateUpdateIndicatorType()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateIndicatorType.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) +
        '&factor=' + getFieldValue( 'factor' ) );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'updateIndicatorTypeForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_indicator_type_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
