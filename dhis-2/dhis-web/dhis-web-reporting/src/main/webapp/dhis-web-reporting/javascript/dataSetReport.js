
/**
 * This file is used by dataSetReportForm.vm and dataApprovalForm.vm.
 */
dhis2.util.namespace( 'dhis2.dsr' );

dhis2.dsr.currentPeriodOffset = 0;
dhis2.dsr.periodTypeFactory = new PeriodType();
dhis2.dsr.currentDataSetReport = null;
dhis2.dsr.permissions = null;

//------------------------------------------------------------------------------
// Get and set methods
//------------------------------------------------------------------------------

dhis2.dsr.getDataSetReport = function()
{
	var ds = $( "#dataSetId" ).val();
	
    var dataSetReport = {
        ds: ds,
        periodType: $( "#periodType" ).val(),
        pe: $( "#periodId" ).val(),
        ou: selectionTreeSelection.getSelectedUid()[0],
        selectedUnitOnly: $( "#selectedUnitOnly" ).is( ":checked" ),
        offset: dhis2.dsr.currentPeriodOffset
    };
        
    var dims = [];
    var cps = [];
    
    $( ".dimension" ).each( function( index, value ) {
    	var dim = $( this ).data( "uid" );
    	var item = $( this ).val();
    	
    	if ( dim && item && item != -1 )
    	{
    		var dimQuery = dim + ":" + item;
    		dims.push( dimQuery );
    		cps.push( item );
    	}
    } );
    
    dataSetReport.dimension = dims;
    dataSetReport.cp = cps;
    
    return dataSetReport;
}

dhis2.dsr.setDataSetReport = function( dataSetReport )
{
	$( "#dataSetId" ).val( dataSetReport.dataSet );
	$( "#periodType" ).val( dataSetReport.periodType );
	
	dhis2.dsr.currentPeriodOffset = dataSetReport.offset;
	
	dhis2.dsr.displayPeriods();
	$( "#periodId" ).val( dataSetReport.period );
	
	selectionTreeSelection.setMultipleSelectionAllowed( false );
	selectionTree.buildSelectionTree();
	
	$( "body" ).on( "oust.selected", function() 
	{
		$( "body" ).off( "oust.selected" );
		dhis2.dsr.generateDataSetReport();
	} );
}

//------------------------------------------------------------------------------
// Data set
//------------------------------------------------------------------------------

/**
 * Callback for changes to data set selection.
 */
dhis2.dsr.dataSetSelected = function()
{
	var ds = $( "#dataSetId" ).val();
	var cc = dhis2.dsr.metaData.dataSets[ds].categoryCombo;
	var cogs = dhis2.dsr.metaData.dataSets[ds].categoryOptionGroupSets;
	
	if ( cc && cc != dhis2.dsr.metaData.defaultCategoryCombo ) {
		var categoryCombo = dhis2.dsr.metaData.categoryCombos[cc];
		var categoryIds = categoryCombo.categories;
		
		dhis2.dsr.setAttributesMarkup( categoryIds );		
	}
	else {
		$( "#attributeComboDiv" ).empty().hide();
	}
	
	if ( cogs && cogs.length ) {
		dhis2.dsr.setCategoryOptionGroupSetsMarkup( cogs );
	}
	else {
		$( "#categoryOptionGroupSetDiv" ).empty().hide();
	}
}

/**
 * Sets markup for drop down boxes for the given category option group sets in the
 * selection div.
 */
dhis2.dsr.setCategoryOptionGroupSetsMarkup = function( groupSetIds )
{
	if ( !groupSetIds || groupSetIds.length == 0 ) {
		return;
	}
	
	var cogsRx = [];
	$.each( groupSetIds, function( idx, id ) {
		cogsRx.push( $.get( "../api/categoryOptionGroupSets/" + id + ".json" ) );
	} );
	
	$.when.apply( $, cogsRx ).done( function() {
		var html = '';
		var args = dhis2.util.normalizeArguments( arguments );
		
		$.each( args, function( idx, cogs ) {
			var groupSet = cogs[0];			

			html += '<div class="inputSection">';
			html += '<div><label>' + groupSet.name + '</label></div>';
			html += '<select class="dimension" data-uid="' + groupSet.id + '" style="width:330px">';
			html += '<option value="-1">[ ' + i18n_select_option_view_all + ' ]</option>';
			
			$.each( groupSet.items, function( idx, option ) {
				html += '<option value="' + option.id + '">' + option.name + '</option>';
			} );
			
			html += '</select>';
			html += '</div>';
		} );

		$( "#categoryOptionGroupSetDiv" ).show().html( html );
	} );
}

/**
* Sets markup for drop down boxes for the given categories in the selection div.
*/
dhis2.dsr.setAttributesMarkup = function( categoryIds )
{
	if ( !categoryIds || categoryIds.length == 0 ) {
		return;
	}
	
	var categoryRx = [];	
	$.each( categoryIds, function( idx, id ) {
		categoryRx.push( $.get( "../api/categories/" + id + ".json" ) );
	} );

	$.when.apply( $, categoryRx ).done( function() {
		var html = '';
		var args = dhis2.util.normalizeArguments( arguments );
		
		$.each( args, function( idx, cat ) {
			var category = cat[0];
			
			html += '<div class="inputSection">';
			html += '<div><label>' + category.name + '</label></div>';
			html += '<select class="dimension" data-uid="' + category.id + '" style="width:330px">';
			html += '<option value="-1">[ ' + i18n_select_option_view_all + ' ]</option>';
			
			$.each( category.items, function( idx, option ) {
				html += '<option value="' + option.id + '">' + option.name + '</option>';
			} );
			
			html += '</select>';
			html += '</div>';
		} );

		$( "#attributeComboDiv" ).show().html( html );
	} );
}

//------------------------------------------------------------------------------
// Period
//------------------------------------------------------------------------------

dhis2.dsr.displayPeriods = function()
{
    var periodType = $( "#periodType" ).val();
    dhis2.dsr.displayPeriodsInternal( periodType, dhis2.dsr.currentPeriodOffset );
}

dhis2.dsr.displayPeriodsInternal = function( periodType, offset )
{
	var periods = dhis2.dsr.periodTypeFactory.get( periodType ).generatePeriods( offset );
    periods = dhis2.dsr.periodTypeFactory.reverse( periods );
    periods = dhis2.dsr.periodTypeFactory.filterFuturePeriodsExceptCurrent( periods );

    $( "#periodId" ).removeAttr( "disabled" );
    clearListById( "periodId" );

    for ( i in periods )
    {
        addOptionById( "periodId", periods[i].iso, periods[i].name );
    }
}

dhis2.dsr.displayNextPeriods = function()
{
    if ( dhis2.dsr.currentPeriodOffset < 0 ) // Cannot display future periods
    {
        dhis2.dsr.currentPeriodOffset++;
        dhis2.dsr.displayPeriods();
    }
}

dhis2.dsr.displayPreviousPeriods = function()
{
    dhis2.dsr.currentPeriodOffset--;
    dhis2.dsr.displayPeriods();
}

//------------------------------------------------------------------------------
// Run report
//------------------------------------------------------------------------------

dhis2.dsr.drillDownDataSetReport = function( orgUnitId, orgUnitUid )
{
	selectionTree.clearSelectedOrganisationUnits();
	selectionTreeSelection.select( orgUnitId );
	
	var dataSetReport = dhis2.dsr.getDataSetReport();
	dataSetReport["ou"] = orgUnitUid;
	dhis2.dsr.displayDataSetReport( dataSetReport );
}

dhis2.dsr.generateDataSetReport = function()
{
	var dataSetReport = dhis2.dsr.getDataSetReport();
	dhis2.dsr.displayDataSetReport( dataSetReport );
}

dhis2.dsr.displayDataSetReport = function( dataSetReport )
{	
    if ( !dataSetReport.ds )
    {
        setHeaderMessage( i18n_select_data_set );
        return false;
    }
    if ( !dataSetReport.pe )
    {
        setHeaderMessage( i18n_select_period );
        return false;
    }
    if ( !selectionTreeSelection.isSelected() )
    {
        setHeaderMessage( i18n_select_organisation_unit );
        return false;
    }
    
    dhis2.dsr.currentDataSetReport = dataSetReport;
    
    hideHeaderMessage();
    dhis2.dsr.hideCriteria();
    dhis2.dsr.hideContent();
    showLoader();
	    
    var url = dhis2.dsr.getDataSetReportUrl( dataSetReport );
    
    $.get( url, function( data ) {
    	$( '#content' ).html( data );
    	hideLoader();
    	dhis2.dsr.showContent();
    	dhis2.dsr.showApproval();
    	setTableStyles();
    } );
}

/**
 * Generates the URL for the given data set report.
 */
dhis2.dsr.getDataSetReportUrl = function( dataSetReport )
{
    var url = "generateDataSetReport.action" +
    	"?ds=" + dataSetReport.ds + 
    	"&pe=" + dataSetReport.pe + 
    	"&ou=" + dataSetReport.ou +
    	"&selectedUnitOnly=" + dataSetReport.selectedUnitOnly;
    
    $.each( dataSetReport.dimension, function( inx, val ) {
    	url += "&dimension=" + val;
    } );
    
    return url;
}

/**
 * Generates the URL for the approval of the given data set report.
 */
dhis2.dsr.getDataApprovalUrl = function( dataSetReport )
{
    var url = "../api/dataApprovals" +
        "?ds=" + dataSetReport.ds +
        "&pe=" + dataSetReport.pe +
        "&ou=" + dataSetReport.ou;

    return url;
}

/**
 * Generates the URL for the acceptance of the given data set report approval.
 */
dhis2.dsr.getDataApprovalAcceptanceUrl = function( dataSetReport )
{
    var url = "../api/dataApprovals/acceptances" +
        "?ds=" + dataSetReport.ds +
        "&pe=" + dataSetReport.pe +
        "&ou=" + dataSetReport.ou;

    return url;
}

dhis2.dsr.exportDataSetReport = function( type )
{
	var dataSetReport = dhis2.dsr.currentDataSetReport;
	
	var url = dhis2.dsr.getDataSetReportUrl( dataSetReport ) + "&type=" + type;
	    
	window.location.href = url;
}

dhis2.dsr.setUserInfo = function( username )
{
	$( "#userInfo" ).load( "../dhis-web-commons-ajax-html/getUser.action?username=" + username, function() {
		$( "#userInfo" ).dialog( {
	        modal : true,
	        width : 350,
	        height : 350,
	        title : "User"
	    } );
	} );	
}

dhis2.dsr.showCriteria = function()
{
	$( "#criteria" ).show( "fast" );
}

dhis2.dsr.hideCriteria = function()
{
	$( "#criteria" ).hide( "fast" );
}

dhis2.dsr.showContent = function()
{
	$( "#content" ).show( "fast" );
	$( ".downloadButton" ).show();
	$( "#interpretationArea" ).autogrow();
}

dhis2.dsr.hideContent = function()
{
	$( "#content" ).hide( "fast" );
	$( ".downloadButton" ).hide();
}

dhis2.dsr.showMoreOptions = function()
{
	$( "#moreOptionsLink" ).hide();
	$( "#lessOptionsLink" ).show();
	$( "#advancedOptions" ).show();
}

dhis2.dsr.showLessOptions = function()
{
	$( "#moreOptionsLink" ).show();
	$( "#lessOptionsLink" ).hide();
	$( "#advancedOptions" ).hide();
}

dhis2.dsr.showApproval = function()
{
	var dataSetReport = dhis2.dsr.currentDataSetReport;
	
	var approval = $( "#dataSetId :selected" ).data( "approval" );

	$( "#approvalNotification" ).hide();
    $( "#approvalDiv" ).hide();

	if ( !approval ) {
		return;
	}
	
	var url = dhis2.dsr.getDataApprovalUrl( dataSetReport );
	
	$.getJSON( url, function( json ) {
		if ( !json || !json.state ) {
			return;
		}

        dhis2.dsr.permissions = json;
		
		var state = json.state;

        $( "#approveButton" ).hide();
        $( "#unapproveButton" ).hide();
        $( "#acceptButton" ).hide();
        $( "#unacceptButton" ).hide();

        switch (state)
        {
            case "UNAPPROVED_WAITING":
                $( "#approvalNotification" ).show().html( i18n_waiting_for_lower_level_approval );
                break;

            case "UNAPPROVED_READY":
                $( "#approvalNotification" ).show().html( i18n_ready_for_approval );
                if ( json.mayApprove ) {
                    $( "#approvalDiv" ).show();
                    $( "#approveButton" ).show();
                }
                break;

            case "APPROVED_HERE":
                $( "#approvalNotification" ).show().html( i18n_approved );
                if ( json.mayUnapprove ) {
                    $( "#approvalDiv" ).show();
                    $( "#unapproveButton" ).show();
                }
                if ( json.mayAccept ) {
                    $( "#approvalDiv" ).show();
                    $( "#acceptButton" ).show();
                }
                break;

            case "ACCEPTED_HERE":
                $( "#approvalNotification" ).show().html( i18n_approved );
                if ( json.mayUnapprove ) {
                    $( "#approvalDiv" ).show();
                    $( "#unapproveButton" ).show();
                }
                if ( json.mayUnccept ) {
                    $( "#approvalDiv" ).show();
                    $( "#unacceptButton" ).show();
                }
                break;
        }
	} );
}

//------------------------------------------------------------------------------
// Approval
//------------------------------------------------------------------------------

dhis2.dsr.approveData = function()
{
	if ( !confirm( i18n_confirm_approval ) ) {
		return false;
	}
	
	var dataSetReport = dhis2.dsr.currentDataSetReport;
	var url = dhis2.dsr.getDataApprovalUrl( dataSetReport );
	
	$.ajax( {
		url: url,
		type: "post",
		success: function() {
            $( "#approvalNotification" ).show().html( i18n_approved );
            $( "#approvalDiv" ).hide();
			$( "#approveButton" ).hide();
            if ( dhis2.dsr.permissions.mayUnapprove ) {
                $( "#approvalDiv" ).show();
                $( "#unapproveButton" ).show();
            }
            if ( dhis2.dsr.permissions.mayAccept ) {
                $( "#approvalDiv" ).show();
                $( "#acceptButton" ).show();
            }
		},
		error: function( xhr, status, error ) {
			alert( xhr.responseText );
		}
	} );
}

dhis2.dsr.unapproveData = function()
{
	if ( !confirm( i18n_confirm_unapproval ) ) {
		return false;
	}
	
	var dataSetReport = dhis2.dsr.currentDataSetReport;
	var url = dhis2.dsr.getDataApprovalUrl( dataSetReport );
	
	$.ajax( {
		url: url,
		type: "delete",
		success: function() {
            $( "#approvalNotification" ).show().html( i18n_ready_for_approval );
            $( "#approvalDiv" ).hide();
            $( "#unapproveButton" ).hide();
            $( "#acceptButton" ).hide();
            $( "#unacceptButton" ).hide();
            if ( dhis2.dsr.permissions.mayApprove ) {
                $( "#approvalDiv" ).show();
                $( "#approveButton" ).show();
            }
		},
		error: function( xhr, status, error ) {
			alert( xhr.responseText );
		}
	} );
}

dhis2.dsr.acceptData = function()
{
    if ( !confirm( i18n_confirm_accept ) ) {
        return false;
    }

    var dataSetReport = dhis2.dsr.currentDataSetReport;
    var url = dhis2.dsr.getDataApprovalAcceptanceUrl( dataSetReport );

    $.ajax( {
        url: url,
        type: "post",
        success: function() {
            $( "#approvalNotification" ).show().html( i18n_approved_and_accepted );
            $( "#approvalDiv" ).hide();
            $( "#acceptButton" ).hide();
            if ( dhis2.dsr.permissions.mayUnapprove ) {
                $( "#approvalDiv" ).show();
                $( "#unapproveButton" ).show();
            }
            if ( dhis2.dsr.permissions.mayUnaccept ) {
                $( "#approvalDiv" ).show();
                $( "#unacceptButton" ).show();
            }
        },
        error: function( xhr, status, error ) {
            alert( xhr.responseText );
        }
    } );
}

dhis2.dsr.unacceptData = function()
{
    if ( !confirm( i18n_confirm_unaccept ) ) {
        return false;
    }

    var dataSetReport = dhis2.dsr.currentDataSetReport;
    var url = dhis2.dsr.getDataApprovalAcceptanceUrl( dataSetReport );

    $.ajax( {
        url: url,
        type: "delete",
        success: function() {
            $( "#approvalNotification" ).show().html( i18n_approved );
            $( "#approvalDiv" ).hide();
            $( "#unacceptButton" ).hide();
            if ( dhis2.dsr.permissions.mayUnapprove ) {
                $( "#approvalDiv" ).show();
                $( "#unapproveButton" ).show();
            }
            if ( dhis2.dsr.permissions.mayAccept ) {
                $( "#approvalDiv" ).show();
                $( "#acceptButton" ).show();
            }
        },
        error: function( xhr, status, error ) {
            alert( xhr.responseText );
        }
    } );
}

//------------------------------------------------------------------------------
// Share
//------------------------------------------------------------------------------

dhis2.dsr.shareInterpretation = function()
{
	var dataSetReport = dhis2.dsr.getDataSetReport();
    var text = $( "#interpretationArea" ).val();
    
    if ( text.length && $.trim( text ).length )
    {
    	text = $.trim( text );
    	
	    var url = "../api/interpretations/dataSetReport/" + $( "#currentDataSetId" ).val() +
	    	"?pe=" + dataSetReport.pe +
	    	"&ou=" + dataSetReport.ou;
	    	    
	    $.ajax( url, {
	    	type: "POST",
	    	contentType: "text/html",
	    	data: text,
	    	success: function() {	    		
	    		$( "#interpretationArea" ).val( "" );
	    		setHeaderDelayMessage( i18n_interpretation_was_shared );
	    	}    	
	    } );
    }
}

//------------------------------------------------------------------------------
// Hooks in custom forms - must be present to avoid errors in forms
//------------------------------------------------------------------------------

function onValueSave( fn )
{
	// Do nothing
}

function onFormLoad( fn )
{
	// Do nothing
}
