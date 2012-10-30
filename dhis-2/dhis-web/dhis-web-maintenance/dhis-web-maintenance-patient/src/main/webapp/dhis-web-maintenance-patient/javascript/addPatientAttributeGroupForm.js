jQuery(document).ready(	function(){
		
		jQuery('name').focus();
		
		jQuery("#availableAttributes").dhisAjaxSelect({
			source: 'getPatientAttributeWithoutGroup.action',
			iterator: 'patientAttributes',
			connectedTo: 'selectedAttributes',
			handler: function(item){
				var option = jQuery( "<option/>" );
				option.attr( "value", item.id );
				option.text( item.name );
				
				return option;
			}
		});
		
		
		validation( 'addPatientAttributeGroupForm', function(form){
			form.submit();
		}, function(){
			selectAllById('selectedAttributes');
			if(jQuery("#selectedAttributes option").length > 0 ){
				setFieldValue('hasAttributes', 'true');
			}
		});
		
		checkValueIsExist( "name", "validatePatientAttributeGroup.action" );
	});		