$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });

  typeOnChange();
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUpdateAttributeForm(context) {
  location.href = 'showUpdateAttributeForm.action?id=' + context.id;
}

function showAttributeDetails(context) {
  jQuery.getJSON('getAttribute.action', { id: context.id },
    function(json) {
      setInnerHTML('nameField', json.attribute.name);
      setInnerHTML('descriptionField', json.attribute.description);
      setInnerHTML('optionSetField', json.attribute.optionSet);
      setInnerHTML('idField', json.attribute.uid);

      var unique = ( json.attribute.unique == 'true') ? i18n_yes : i18n_no;
      setInnerHTML('uniqueField', unique);

      var inherit = ( json.attribute.inherit == 'true') ? i18n_yes : i18n_no;
      setInnerHTML('inheritField', inherit);

      var confidential = ( json.attribute.confidential == 'true') ? i18n_yes : i18n_no;
      setInnerHTML('confidentialField', confidential);

      var valueType = json.attribute.valueType;
      var typeMap = attributeTypeMap();
      setInnerHTML('valueTypeField', typeMap[valueType]);

      if( json.attribute.unique == 'true' ) {
        var orgunitScope = json.attribute.orgunitScope;
        var programScope = json.attribute.programScope;
        if( orgunitScope == 'false' && programScope == 'false' ) {
          setInnerHTML('scopeField', i18n_whole_system);
        }
        else if( orgunitScope == 'true' && programScope == 'false' ) {
          setInnerHTML('scopeField', i18n_orgunit);
        }
        else if( orgunitScope == 'false' && programScope == 'true' ) {
          setInnerHTML('scopeField', i18n_program);
        }
        else {
          setInnerHTML('scopeField', i18n_program_within_orgunit);
        }
      }

      showDetails();
    });
}

function attributeTypeMap() {
  var typeMap = [];
  typeMap['NUMBER'] = i18n_number;
  typeMap['TEXT'] = i18n_text;
  typeMap['BOOLEAN'] = i18n_yes_no;
  typeMap['TRUE_ONLY'] = i18n_yes_only;
  typeMap['DATE'] = i18n_date;
  typeMap['PHONE_NUMBER'] = i18n_phone_number;
  typeMap['TRACKER_ASSOCIATE'] = i18n_tracker_associate;
  typeMap['OPTION_SET'] = i18n_option_set;
  return typeMap;
}

// -----------------------------------------------------------------------------
// Remove Attribute
// -----------------------------------------------------------------------------

function removeAttribute(context) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeAttribute.action');
}


function typeOnChange() {
  var type = getFieldValue('valueType');

  hideById("optionSetRow");
  disable("optionSetId");
  hideById("trackedEntityRow");
  disable("trackedEntityId");

  if( type == "OPTION_SET" ) {
    showById("optionSetRow");
    enable("optionSetId");
  }
  else if( type == "TRACKER_ASSOCIATE" ) {
    showById("trackedEntityRow");
    enable("trackedEntityId");
  }

  if( type == "NUMBER" || type == 'TEXT' || type == 'LETTER' || type == 'PHONE_NUMBER' ) {
    enable("unique");
  }
  else {
    disable("unique");
  }
}

function uniqueOnChange() {
  if( $('#unique').attr('checked') == "checked" ) {
    jQuery('[name=uniqueTR]').show();
    jQuery('#valueType [value=BOOLEAN]').hide();
    jQuery('#valueType [value=TRUE_ONLY]').hide();
    jQuery('#valueType [value=DATE]').hide();
    jQuery('#valueType [value=TRACKER_ASSOCIATE]').hide();
    jQuery('#valueType [value=USERNAME]').hide();
    jQuery('#valueType [value=OPTION_SET]').hide();
  }
  else {
    jQuery('[name=uniqueTR]').hide();
    jQuery('#valueType [value=BOOLEAN]').show();
    jQuery('#valueType [value=TRUE_ONLY]').show();
    jQuery('#valueType [value=DATE]').show();
    jQuery('#valueType [value=TRACKER_ASSOCIATE]').show();
    jQuery('#valueType [value=USERNAME]').show();
    jQuery('#valueType [value=OPTION_SET]').show();
  }
}
