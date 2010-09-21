/*
 * Copyright (C) 2007-2008  Camptocamp|
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires core/GeoStat/Choropleth.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

mapfish.widgets.geostat.Choropleth = Ext.extend(Ext.FormPanel, {

    layer: null,
    
    format: null,
    
    url: null,
    
    featureSelection: true,
    
    nameAttribute: null,
    
    indicator: null,
    
    indicatorText: null,
    
    coreComp: null,
    
    classificationApplied: false,
    
    ready: false,
    
    border: false,
    
    loadMask: false,
    
    labelGenerator: null,
	
    colorInterpolation: false,
    
    newUrl: false,
	
    imageLegend: false,
	
    bounds: false,
    
    parentId: false,
    
    mapView: false,
    
    initComponent: function() {
    
        mapViewStore = new Ext.data.JsonStore({
            url: path_mapping + 'getAllMapViews' + type,
            root: 'mapViews',
            fields: ['id', 'name'],
            sortInfo: {field: 'name', direction: 'ASC'},
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (PARAMETER) {
                            Ext.Ajax.request({
                                url: path_mapping + 'getMapView' + type,
                                method: 'POST',
                                params: {id: PARAMETER},
								success: function(r) {
									PARAMETER = false;
                                    choropleth.mapView = getNumericMapView(Ext.util.JSON.decode(r.responseText).mapView[0]);
                                    MAPSOURCE = choropleth.mapView.mapSourceType;
                                    MAP.setCenter(new OpenLayers.LonLat(choropleth.mapView.longitude, choropleth.mapView.latitude), choropleth.mapView.zoom);

									Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                    Ext.getCmp('mapdatetype_cb').setValue(MAPDATETYPE);
                                    Ext.getCmp('mapview_cb').setValue(choropleth.mapView.id);
                                    VALUETYPE.polygon = choropleth.mapView.mapValueType;
                                    
                                    if (choropleth.mapView.mapLegendType == map_legend_type_automatic) {
                                        LEGEND[thematicMap].type = map_legend_type_automatic;
                                        Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_automatic);
                                        Ext.getCmp('maplegendset_cb').hideField();
                                        Ext.getCmp('method_cb').showField();
                                        Ext.getCmp('method_cb').setValue(choropleth.mapView.method);
                                        Ext.getCmp('colorA_cf').showField();
                                        Ext.getCmp('colorA_cf').setValue(choropleth.mapView.colorLow);
                                        Ext.getCmp('colorB_cf').showField();
                                        Ext.getCmp('colorB_cf').setValue(choropleth.mapView.colorHigh);
                                        
                                        if (choropleth.mapView.method == classify_with_bounds) {
                                            Ext.getCmp('numClasses_cb').hideField();
                                            Ext.getCmp('bounds_tf').showField();
                                            Ext.getCmp('bounds_tf').setValue(choropleth.mapView.bounds);
                                        }
                                        else {
                                            Ext.getCmp('bounds_tf').hideField();
                                            Ext.getCmp('numClasses_cb').showField();
                                            Ext.getCmp('numClasses_cb').setValue(choropleth.mapView.classes);
                                        }
                                    }
									else if (choropleth.mapView.mapLegendType == map_legend_type_predefined) {
                                        LEGEND[thematicMap].type = map_legend_type_predefined;
                                        Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_predefined);
                                        Ext.getCmp('method_cb').hideField();
                                        Ext.getCmp('bounds_tf').hideField();
                                        Ext.getCmp('numClasses_cb').hideField();
                                        Ext.getCmp('colorA_cf').hideField();
                                        Ext.getCmp('colorB_cf').hideField();
                                        Ext.getCmp('maplegendset_cb').showField();
                                        
                                        Ext.getCmp('maplegendset_cb').setValue(choropleth.mapView.mapLegendSetId);
										
										predefinedMapLegendSetStore.load();
									}
									
									Ext.getCmp('mapvaluetype_cb').setValue(choropleth.mapView.mapValueType);
										
									if (choropleth.mapView.mapValueType == map_value_type_indicator) {
                                        Ext.getCmp('indicator_cb').showField();
                                        Ext.getCmp('indicatorgroup_cb').showField();
                                        Ext.getCmp('dataelementgroup_cb').hideField();
                                        Ext.getCmp('dataelement_cb').hideField();

                                        Ext.getCmp('indicatorgroup_cb').setValue(choropleth.mapView.indicatorGroupId);
                                    
                                        indicatorStore.setBaseParam('indicatorGroupId', choropleth.mapView.indicatorGroupId);
                                        indicatorStore.load();
                                    }
                                    else if (choropleth.mapView.mapValueType == map_value_type_dataelement) {
                                        Ext.getCmp('indicator_cb').hideField();
                                        Ext.getCmp('indicatorgroup_cb').hideField();
                                        Ext.getCmp('dataelementgroup_cb').showField();
                                        Ext.getCmp('dataelement_cb').showField();

                                        Ext.getCmp('dataelementgroup_cb').setValue(choropleth.mapView.dataElementGroupId);
                                    
                                        dataElementStore.setBaseParam('dataElementGroupId', choropleth.mapView.dataElementGroupId);
                                        dataElementStore.load();
                                    }
                                    
                                    if (MAPDATETYPE == map_date_type_fixed) {
                                        Ext.getCmp('periodtype_cb').showField();
                                        Ext.getCmp('period_cb').showField();
                                        Ext.getCmp('startdate_df').hideField();
                                        Ext.getCmp('enddate_df').hideField();
                                    }
                                    else {
                                        Ext.getCmp('periodtype_cb').hideField();
                                        Ext.getCmp('period_cb').hideField();
                                        Ext.getCmp('startdate_df').showField();
                                        Ext.getCmp('enddate_df').showField();
                                    }
                                },
                                failure: function() {
                                  alert( i18n_status , i18n_error_while_retrieving_data );
                                }
                            });
                        }
                    }
                }
            }
        });

        indicatorGroupStore = new Ext.data.JsonStore({
            url: path_mapping + 'getAllIndicatorGroups' + type,
            root: 'indicatorGroups',
            fields: ['id', 'name'],
            idProperty: 'id',
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
        
        indicatorStore = new Ext.data.JsonStore({
            url: path_mapping + 'getIndicatorsByIndicatorGroup' + type,
            root: 'indicators',
            fields: ['id', 'name', 'shortName'],
            idProperty: 'id',
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        indicatorStore.each(
                            function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            },
                            this
                        );
                        
                        Ext.getCmp('indicator_cb').clearValue();

                        if (choropleth.mapView) {
                            Ext.getCmp('indicator_cb').setValue(choropleth.mapView.indicatorId);

                            if (choropleth.mapView.mapDateType == map_date_type_fixed) {
                                Ext.getCmp('periodtype_cb').showField();
                                Ext.getCmp('period_cb').showField();
                                Ext.getCmp('startdate_df').hideField();
                                Ext.getCmp('enddate_df').hideField();
                                
                                Ext.getCmp('periodtype_cb').setValue(choropleth.mapView.periodTypeId);
                                periodStore.setBaseParam('name', choropleth.mapView.periodTypeId);
                                periodStore.load();
                            }
                            else if (choropleth.mapView.mapDateType == map_date_type_start_end) {
                                Ext.getCmp('periodtype_cb').hideField();
                                Ext.getCmp('period_cb').hideField();
                                Ext.getCmp('startdate_df').showField();
                                Ext.getCmp('enddate_df').showField();

                                Ext.getCmp('startdate_df').setValue(new Date(choropleth.mapView.startDate));
                                Ext.getCmp('enddate_df').setValue(new Date(choropleth.mapView.endDate));
                                
                                if (MAPSOURCE == map_source_type_database) {
                                    Ext.Ajax.request({
                                        url: path_commons + 'getOrganisationUnit' + type,
                                        method: 'POST',
                                        params: {id:choropleth.mapView.mapSource},
                                        success: function(r) {
                                            var name = Ext.util.JSON.decode(r.responseText).organisationUnit.name;
                                            Ext.getCmp('map_tf').setValue(name);
                                            Ext.getCmp('map_tf').value = choropleth.mapView.mapSource;
                                            choropleth.loadFromDatabase(choropleth.mapView.mapSource);
                                        },
                                        failure: function() {
                                            alert('Error: getOrganisationUnit');
                                        }
                                    });
                                }
                                else {
                                    Ext.getCmp('map_cb').setValue(choropleth.mapView.mapSource);
                                    choropleth.loadFromFile(choropleth.mapView.mapSource);
                                }
                            }
                        }
                    }
                }
            }
        });
		
		dataElementGroupStore = new Ext.data.JsonStore({
			url: path_mapping + 'getAllDataElementGroups' + type,
            root: 'dataElementGroups',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
		
		dataElementStore = new Ext.data.JsonStore({
            url: path_mapping + 'getDataElementsByDataElementGroup' + type,
            root: 'dataElements',
            fields: ['id', 'name', 'shortName'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        dataElementStore.each(
                        function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            },  this
                        );
                        
                        Ext.getCmp('dataelement_cb').clearValue();

                        if (choropleth.mapView) {
                            Ext.getCmp('dataelement_cb').setValue(choropleth.mapView.dataElementId);
                            
                            if (choropleth.mapView.mapDateType == map_date_type_fixed) {
                                Ext.getCmp('periodtype_cb').showField();
                                Ext.getCmp('period_cb').showField();
                                Ext.getCmp('startdate_df').hideField();
                                Ext.getCmp('enddate_df').hideField();
                                
                                Ext.getCmp('periodtype_cb').setValue(choropleth.mapView.periodTypeId);
                                periodStore.setBaseParam('name', choropleth.mapView.periodTypeId);
                                periodStore.load();
                            }
                            else if (choropleth.mapView.mapDateType == map_date_type_start_end) {
                                Ext.getCmp('periodtype_cb').hideField();
                                Ext.getCmp('period_cb').hideField();
                                Ext.getCmp('startdate_df').showField();
                                Ext.getCmp('enddate_df').showField();
                                
                                Ext.getCmp('startdate_df').setValue(new Date(choropleth.mapView.startDate));
                                Ext.getCmp('enddate_df').setValue(new Date(choropleth.mapView.endDate));
                                
                                if (MAPSOURCE == map_source_type_database) {
                                    Ext.Ajax.request({
                                        url: path_commons + 'getOrganisationUnit' + type,
                                        method: 'POST',
                                        params: {id:choropleth.mapView.mapSource},
                                        success: function(r) {
                                            var name = Ext.util.JSON.decode(r.responseText).organisationUnit.name;
                                            Ext.getCmp('map_tf').setValue(name);
                                            Ext.getCmp('map_tf').value = choropleth.mapView.mapSource;
                                            choropleth.loadFromDatabase(choropleth.mapView.mapSource);
                                        },
                                        failure: function() {
                                            alert('Error: getOrganisationUnit');
                                        }
                                    });
                                }
                                else {
                                    Ext.getCmp('map_cb').setValue(choropleth.mapView.mapSource);
                                    choropleth.loadFromFile(choropleth.mapView.mapSource);
                                }
                            }
                        }
                    },
                    scope: this
                }
            }
        });
        
        periodTypeStore = new Ext.data.JsonStore({
            url: path_mapping + 'getAllPeriodTypes' + type,
            root: 'periodTypes',
            fields: ['name'],
            autoLoad: true
        });
            
        periodStore = new Ext.data.JsonStore({
            url: path_mapping + 'getPeriodsByPeriodType' + type,
            root: 'periods',
            fields: ['id', 'name'],
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        if (choropleth.mapView) {
                            Ext.getCmp('period_cb').setValue(choropleth.mapView.periodId);
                                
                            Ext.Ajax.request({
                                url: path_mapping + 'setMapUserSettings' + type,
                                method: 'POST',
                                params: {mapSourceType: choropleth.mapView.mapSourceType, mapDateType: MAPDATETYPE },
                                success: function(r) {
                                    Ext.getCmp('map_cb').getStore().load();
                                    Ext.getCmp('maps_cb').getStore().load();
                                    Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                },
                                failure: function() {
                                    alert( 'Error: setMapSourceTypeUserSetting' );
                                }
                            });
                        }
                    }
                }
            }
        });
            
        mapStore = new Ext.data.JsonStore({
            url: path_mapping + 'getAllMaps' + type,
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            idProperty: 'mapLayerPath',
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (choropleth.mapView) {
                            if (MAPSOURCE == map_source_type_database) {
                                Ext.Ajax.request({
                                    url: path_commons + 'getOrganisationUnit' + type,
                                    method: 'POST',
                                    params: {id:choropleth.mapView.mapSource},
                                    success: function(r) {
                                        var name = Ext.util.JSON.decode(r.responseText).organisationUnit.name;
                                        Ext.getCmp('map_tf').setValue(name);
                                        Ext.getCmp('map_tf').value = choropleth.mapView.mapSource;
                                        choropleth.loadFromDatabase(choropleth.mapView.mapSource);
                                    },
                                    failure: function() {
                                        alert('Error: getOrganisationUnit');
                                    }
                                });
                            }
                            else {
                                Ext.getCmp('map_cb').setValue(choropleth.mapView.mapSource);
                                choropleth.loadFromFile(choropleth.mapView.mapSource);
                            }
                        }
                    }
                }
            }
        });
		
		predefinedMapLegendSetStore = new Ext.data.JsonStore({
            url: path_mapping + 'getMapLegendSetsByType' + type,
            baseParams: {type: map_legend_type_predefined},
            root: 'mapLegendSets',
            fields: ['id', 'name'],
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
						if (choropleth.mapView) {
							Ext.Ajax.request({
								url: path_mapping + 'getMapLegendSet' + type,
								method: 'POST',
								params: {id: choropleth.mapView.mapLegendSetId},
								success: function(r) {
									var mls = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
									Ext.getCmp('maplegendset_cb').setValue(mls.id);
									choropleth.applyPredefinedLegend();
								},
								failure: function() {
									alert('Error: getMapLegendSet');
								}
							});
						}
                    }
                }
            }
        });
        
        this.items = [
         
        {
            xtype: 'combo',
            id: 'mapview_cb',
            fieldLabel: i18n_favorite,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: i18n_optional,
            selectOnFocus: true,
			labelSeparator: labelseparator,
            width: combo_width,
            store: mapViewStore,
            listeners: {
                'select': {
                    fn: function() {
                        var mId = Ext.getCmp('mapview_cb').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapView' + type,
                            method: 'POST',
                            params: { id: mId },
                            success: function(r) {
                                choropleth.mapView = getNumericMapView(Ext.util.JSON.decode(r.responseText).mapView[0]);
								MAPSOURCE = choropleth.mapView.mapSourceType;
                                MAPDATETYPE = choropleth.mapView.mapDateType;
                                Ext.getCmp('mapdatetype_cb').setValue(MAPDATETYPE);
                                
                                Ext.getCmp('mapvaluetype_cb').setValue(choropleth.mapView.mapValueType);
								VALUETYPE.polygon = choropleth.mapView.mapValueType;
                                
                                if (choropleth.mapView.mapValueType == map_value_type_indicator) {
                                    Ext.getCmp('indicatorgroup_cb').showField();
                                    Ext.getCmp('indicator_cb').showField();
                                    Ext.getCmp('dataelementgroup_cb').hideField();
                                    Ext.getCmp('dataelement_cb').hideField();
                                    
                                    Ext.getCmp('indicatorgroup_cb').setValue(choropleth.mapView.indicatorGroupId);
                                    indicatorStore.setBaseParam('indicatorGroupId', choropleth.mapView.indicatorGroupId);
                                    indicatorStore.load();
                                }
                                else if (choropleth.mapView.mapValueType == map_value_type_dataelement) {
                                    Ext.getCmp('indicatorgroup_cb').hideField();
                                    Ext.getCmp('indicator_cb').hideField();
                                    Ext.getCmp('dataelementgroup_cb').showField();
                                    Ext.getCmp('dataelement_cb').showField();
                                    
                                    Ext.getCmp('dataelementgroup_cb').setValue(choropleth.mapView.dataElementGroupId);
                                    dataElementStore.setBaseParam('dataElementGroupId', choropleth.mapView.dataElementGroupId);
                                    dataElementStore.load();
                                }                                        
								
                                if (choropleth.mapView.mapLegendType == map_legend_type_automatic) {
                                    LEGEND[thematicMap].type = map_legend_type_automatic;
									Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_automatic);
                                    Ext.getCmp('maplegendset_cb').hideField();
									Ext.getCmp('method_cb').showField();
                                    Ext.getCmp('method_cb').setValue(choropleth.mapView.method);
                                    Ext.getCmp('colorA_cf').showField();
									Ext.getCmp('colorA_cf').setValue(choropleth.mapView.colorLow);
                                    Ext.getCmp('colorB_cf').showField();
									Ext.getCmp('colorB_cf').setValue(choropleth.mapView.colorHigh);
                                    
                                    if (choropleth.mapView.method == classify_with_bounds) {
                                        Ext.getCmp('numClasses_cb').hideField();
                                        Ext.getCmp('bounds_tf').showField();
                                        Ext.getCmp('bounds_tf').setValue(choropleth.mapView.bounds);
                                    }
                                    else {
                                        Ext.getCmp('bounds_tf').hideField();
                                        Ext.getCmp('numClasses_cb').showField();
                                        Ext.getCmp('numClasses_cb').setValue(choropleth.mapView.classes);
                                    }
								}
								else if (choropleth.mapView.mapLegendType == map_legend_type_predefined) {
                                    LEGEND[thematicMap].type = map_legend_type_predefined;
									Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_predefined);
									Ext.getCmp('method_cb').hideField();
									Ext.getCmp('bounds_tf').hideField();
									Ext.getCmp('numClasses_cb').hideField();
									Ext.getCmp('colorA_cf').hideField();
									Ext.getCmp('colorB_cf').hideField();
									Ext.getCmp('maplegendset_cb').showField();
									
                                    Ext.getCmp('maplegendset_cb').setValue(choropleth.mapView.mapLegendSetId);
                                    choropleth.applyPredefinedLegend();
								}
                            },
                            failure: function() {
                              alert( i18n_status , i18n_error_while_retrieving_data );
                            } 
                        });
                    },
                    scope: this
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
			id: 'mapvaluetype_cb',
            fieldLabel: i18n_mapvaluetype,
			labelSeparator: labelseparator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            width: combo_width,
			value: map_value_type_indicator,
            store: new Ext.data.SimpleStore({
                fields: ['id', 'name'],
                data: [[map_value_type_indicator, 'Indicators'], [map_value_type_dataelement, 'Data elements']]
            }),
			listeners: {
				'select': {
					fn: function() {
						if (Ext.getCmp('mapvaluetype_cb').getValue() == map_value_type_indicator) {
							Ext.getCmp('indicatorgroup_cb').showField();
							Ext.getCmp('indicator_cb').showField();
							Ext.getCmp('dataelementgroup_cb').hideField();
							Ext.getCmp('dataelement_cb').hideField();
							VALUETYPE.polygon = map_value_type_indicator;
						}
						else if (Ext.getCmp('mapvaluetype_cb').getValue() == map_value_type_dataelement) {
							Ext.getCmp('indicatorgroup_cb').hideField();
							Ext.getCmp('indicator_cb').hideField();
							Ext.getCmp('dataelementgroup_cb').showField();
							Ext.getCmp('dataelement_cb').showField();
							VALUETYPE.polygon = map_value_type_dataelement;
						}
                        
                        choropleth.classify(false, true);
					}
				}
			}
		},
        
        {
            xtype: 'combo',
            id: 'indicatorgroup_cb',
            fieldLabel: i18n_indicator_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: indicatorGroupStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
						
						Ext.getCmp('indicator_cb').clearValue();
                        indicatorStore.setBaseParam('indicatorGroupId', this.getValue());
                        indicatorStore.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'indicator_cb',
            fieldLabel: i18n_indicator ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: indicatorStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
 
                        var iId = Ext.getCmp('indicator_cb').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapLegendSetByIndicator' + type,
                            method: 'POST',
                            params: {indicatorId: iId},
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    LEGEND[thematicMap].type = map_legend_type_predefined;
                                    Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_predefined);
                                    Ext.getCmp('maplegendset_cb').showField();
                                    Ext.getCmp('maplegendset_cb').setValue(mapLegendSet.id);
                                    Ext.getCmp('method_cb').hideField();
                                    Ext.getCmp('numClasses_cb').hideField();
                                    Ext.getCmp('colorA_cf').hideField();
                                    Ext.getCmp('colorB_cf').hideField();

                                    choropleth.applyPredefinedLegend();
                                }
                                else {
                                    if (LEGEND[thematicMap].type == map_legend_type_predefined) {
                                        LEGEND[thematicMap].type = map_legend_type_automatic;
                                        Ext.getCmp('maplegendtype_cb').setValue(LEGEND[thematicMap].type);
                                        Ext.getCmp('method_cb').showField();
                                        if (Ext.getCmp('method_cb').getValue() == classify_with_bounds) {
                                            Ext.getCmp('bounds_tf').showField();
                                            Ext.getCmp('numClasses_cb').hideField();
                                        }
                                        else {
                                            Ext.getCmp('bounds_tf').hideField();
                                            Ext.getCmp('numClasses_cb').showField();
                                        }
                                        Ext.getCmp('colorA_cf').showField();
                                        Ext.getCmp('colorB_cf').showField();
                                        Ext.getCmp('maplegendset_cb').hideField();       

                                        choropleth.classify(false, true);
                                    }
                                }
                            },
                            failure: function()
                            {
                              alert( i18n_status , i18n_error_while_retrieving_data );
                            } 
                        });
                    },
                    scope: this
                }
            }
        },
		
		{
            xtype: 'combo',
            id: 'dataelementgroup_cb',
            fieldLabel: i18n_dataelement_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: dataElementGroupStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
                        Ext.getCmp('dataelement_cb').clearValue();
						dataElementStore.setBaseParam('dataElementGroupId', this.getValue());
                        dataElementStore.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'dataelement_cb',
            fieldLabel: i18n_dataelement ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: dataElementStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
                        
                        var deId = Ext.getCmp('dataelement_cb').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapLegendSetByDataElement' + type,
                            method: 'POST',
                            params: {dataElementId: deId},
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    Ext.getCmp('maplegendtype_cb').setValue(map_legend_type_predefined);
                                    Ext.getCmp('maplegendset_cb').showField();
                                    Ext.getCmp('maplegendset_cb').setValue(mapLegendSet.id);
                                    Ext.getCmp('method_cb').hideField();
                                    Ext.getCmp('numClasses_cb').hideField();
                                    Ext.getCmp('colorA_cf').hideField();
                                    Ext.getCmp('colorB_cf').hideField();

                                    choropleth.applyPredefinedLegend();
                                }
                                else {
                                    if (LEGEND[thematicMap].type == map_legend_type_predefined) {
                                        LEGEND[thematicMap].type = map_legend_type_automatic;
                                        Ext.getCmp('maplegendtype_cb').setValue(LEGEND[thematicMap].type);
                                        Ext.getCmp('method_cb').showField();
                                        if (Ext.getCmp('method_cb').getValue() == classify_with_bounds) {
                                            Ext.getCmp('bounds_tf').showField();
                                            Ext.getCmp('numClasses_cb').hideField();
                                        }
                                        else {
                                            Ext.getCmp('bounds_tf').hideField();
                                            Ext.getCmp('numClasses_cb').showField();
                                        }
                                        Ext.getCmp('colorA_cf').showField();
                                        Ext.getCmp('colorB_cf').showField();
                                        Ext.getCmp('maplegendset_cb').hideField();       

                                        choropleth.classify(false, true);
                                    }
                                }
                            },
                            failure: function()
                            {
                              alert( i18n_status , i18n_error_while_retrieving_data );
                            } 
                        });
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'periodtype_cb',
            fieldLabel: i18n_period_type,
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: periodTypeStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
                        
                        Ext.getCmp('period_cb').clearValue();
                        Ext.getCmp('period_cb').getStore().setBaseParam('name', this.getValue());
                        Ext.getCmp('period_cb').getStore().load();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            id: 'period_cb',
            fieldLabel: i18n_period ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: periodStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue()) {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
                        choropleth.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            id: 'startdate_df',
            fieldLabel: i18n_start_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: emptytext,
			labelSeparator: labelseparator,
            width: combo_width,
            listeners: {
                'select': {
                    fn: function(df, date) {
                        Ext.getCmp('enddate_df').setMinValue(date);
                        choropleth.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            id: 'enddate_df',
            fieldLabel: i18n_end_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: emptytext,
			labelSeparator: labelseparator,
            width: combo_width,
            listeners: {
                'select': {
                    fn: function(df, date) {
                        Ext.getCmp('startdate_df').setMaxValue(date);
                        choropleth.classify(false, true);
                    }
                }
            }
        },                        
        
        {
            xtype: 'combo',
            id: 'map_cb',
            fieldLabel: i18n_map ,
            typeAhead: true,
            editable: false,
            valueField: 'mapLayerPath',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: mapStore,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
                        
                        if (Ext.getCmp('map_cb').getValue() != choropleth.newUrl) {
                            choropleth.loadFromFile(Ext.getCmp('map_cb').getValue());
                        }
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'map_tf',
            fieldLabel: i18n_parent_orgunit,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            listeners: {
                'focus': {
                    fn: function() {
                        function showTree() {
                            var value, rawvalue;
                            var w = new Ext.Window({
                                id: 'orgunit_w',
                                title: 'Select parent organisation unit',
                                closeAction: 'hide',
                                autoScroll: true,
                                width: 280,
                                autoHeight: true,
                                height: 'auto',
                                boxMaxHeight: 500,
                                items: [
                                    {
                                        xtype: 'treepanel',
                                        id: 'orgunit_tp',
                                        bodyStyle: 'padding:7px',
                                        height: getMultiSelectHeight(),
                                        autoScroll: true,
                                        loader: new Ext.tree.TreeLoader({
                                            dataUrl: path_mapping + 'getOrganisationUnitChildren' + type
                                        }),
                                        root: {
                                            id: TOPLEVELUNIT.id,
                                            text: TOPLEVELUNIT.name,
                                            hasChildrenWithCoordinates: TOPLEVELUNIT.hasChildrenWithCoordinates,
                                            nodeType: 'async',
                                            draggable: false,
                                            expanded: true
                                        },
                                        listeners: {
                                            'click': {
                                                fn: function(n) {
                                                    if (n.hasChildNodes()) {
                                                        Ext.getCmp('map_tf').setValue(n.attributes.text);
                                                        Ext.getCmp('map_tf').value = n.attributes.id;
                                                        Ext.getCmp('map_tf').node = n;
                                                    }
                                                }
                                            },
                                            'expandnode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w').syncSize();
                                                }
                                            },
                                            'collapsenode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w').syncSize();
                                                }
                                            }
                                        }
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'table',
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'Select',
                                                width: 133,
                                                handler: function() {
                                                    if (Ext.getCmp('map_tf').getValue() && Ext.getCmp('map_tf').getValue() != choropleth.parentId) {
                                                        choropleth.loadFromDatabase(Ext.getCmp('map_tf').value);
                                                    }
                                                    Ext.getCmp('orgunit_w').hide();
                                                }
                                            },
                                            {
                                                xtype: 'button',
                                                text: 'Cancel',
                                                width: 133,
                                                handler: function() {
                                                    Ext.getCmp('orgunit_w').hide();
                                                }
                                            }
                                        ]
                                    }
                                ]
                            });
                            
                            var x = Ext.getCmp('center').x + 15;
                            var y = Ext.getCmp('center').y + 41;
                            w.setPosition(x,y);
                            w.show();
                        }
                        
                        if (TOPLEVELUNIT.id) {
                            showTree();
                        }
                        else {
                            Ext.Ajax.request({
                                url: path_commons + 'getOrganisationUnits' + type,
                                params: { level: 1 },
                                method: 'POST',
                                success: function(r) {
                                    var rootNode = Ext.util.JSON.decode(r.responseText).organisationUnits[0];
                                    TOPLEVELUNIT.id = rootNode.id;
                                    TOPLEVELUNIT.name = rootNode.name;
                                    TOPLEVELUNIT.hasChildrenWithCoordinates = rootNode.hasChildrenWithCoordinates;
                                    
                                    showTree();          
                                },
                                failure: function(r) {
                                    alert('getOrganisationUnits');
                                }
                            });
                        }
                    },
                    scope: this
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_type,
            id: 'maplegendtype_cb',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            value: LEGEND[thematicMap].type,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[map_legend_type_automatic, i18n_automatic],
					[map_legend_type_predefined, i18n_predefined]
				]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('maplegendtype_cb').getValue() == map_legend_type_predefined && Ext.getCmp('maplegendtype_cb').getValue() != LEGEND[thematicMap].type ) {
							LEGEND[thematicMap].type = map_legend_type_predefined;
							Ext.getCmp('method_cb').hideField();
							Ext.getCmp('bounds_tf').hideField();
                            Ext.getCmp('numClasses_cb').hideField();
							Ext.getCmp('colorA_cf').hideField();
							Ext.getCmp('colorB_cf').hideField();
							Ext.getCmp('maplegendset_cb').showField();
							
							if (Ext.getCmp('maplegendset_cb').getValue()) {
                                choropleth.applyPredefinedLegend();
							}
                        }
                        else if (Ext.getCmp('maplegendtype_cb').getValue() == map_legend_type_automatic && Ext.getCmp('maplegendtype_cb').getValue() != LEGEND[thematicMap].type) {
							LEGEND[thematicMap].type = map_legend_type_automatic;
							Ext.getCmp('method_cb').showField();
							if (Ext.getCmp('method_cb').getValue() == classify_with_bounds) {
								Ext.getCmp('bounds_tf').showField();
								Ext.getCmp('numClasses_cb').hideField();
							}
							else {
								Ext.getCmp('bounds_tf').hideField();
								Ext.getCmp('numClasses_cb').showField();
							}
							Ext.getCmp('colorA_cf').showField();
							Ext.getCmp('colorB_cf').showField();
							Ext.getCmp('maplegendset_cb').hideField();
                            
                            choropleth.classify(false, true);
                        }
                    },
                    scope: this
                }
            }
        },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_set,
            id: 'maplegendset_cb',
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            triggerAction: 'all',
            width: combo_width,
			hidden: true,
            store: predefinedMapLegendSetStore,
            listeners: {
                'select': {
                    fn: function() {
						choropleth.applyPredefinedLegend();
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'combo',
            fieldLabel: i18n_method,
            id: 'method_cb',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            value: LEGEND[thematicMap].method,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[2, i18n_equal_intervals],
					[3, i18n_equal_group_count],
					[1, i18n_fixed_breaks]
				]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('method_cb').getValue() == classify_with_bounds && Ext.getCmp('method_cb').getValue() != LEGEND[thematicMap].method) {
							LEGEND[thematicMap].method = classify_with_bounds;
                            Ext.getCmp('bounds_tf').showField();
                            Ext.getCmp('numClasses_cb').hideField();
                        }
                        else if (Ext.getCmp('method_cb').getValue() != LEGEND[thematicMap].method) {
							LEGEND[thematicMap].method = Ext.getCmp('method_cb').getValue();
                            Ext.getCmp('bounds_tf').hideField();
                            Ext.getCmp('numClasses_cb').showField();
                            
                            this.classify(false, true);
                        }
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'bounds_tf',
            fieldLabel: i18n_bounds,
			labelSeparator: labelseparator,
            emptyText: i18n_comma_separated_values,
            isFormField: true,
            width: combo_width,
            hidden: true
        },
        
        {
            xtype: 'combo',
            fieldLabel: i18n_classes,
			labelSeparator: labelseparator,
            id: 'numClasses_cb',
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: LEGEND[thematicMap].classes,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').clearValue();
                        }
						
						if (Ext.getCmp('numClasses_cb').getValue() != LEGEND[thematicMap].classes) {
							LEGEND[thematicMap].classes = Ext.getCmp('numClasses_cb').getValue();
							this.classify(false, true);
						}
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'colorfield',
            fieldLabel: i18n_low_color,
			labelSeparator: labelseparator,
            id: 'colorA_cf',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            fieldLabel: i18n_high_color,
			labelSeparator: labelseparator,
            id: 'colorB_cf',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FF0000"
        },
        
        { html: '<br>' },

        {
            xtype: 'button',
			cls: 'aa_med',
            isFormField: true,
            fieldLabel: '',
            labelSeparator: '',
            text: i18n_refresh,
            handler: function() {
                if (choropleth.validateForm()) {
                    this.layer.setVisibility(true);
                    this.classify(true, true);
                }
                else {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
            },
            scope: this
        }

        ];
	
		mapfish.widgets.geostat.Choropleth.superclass.initComponent.apply(this);
    },
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    requestSuccess: function(request) {
        this.ready = true;

        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    requestFailure: function(request) {
        OpenLayers.Console.error( i18n_ajax_request_failed );
    },
    
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf').getValue());
        return [colorA, colorB];
    },
	
	applyPredefinedLegend: function() {
        LEGEND[thematicMap].type = map_legend_type_predefined;
		var mls = Ext.getCmp('maplegendset_cb').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: path_mapping + 'getMapLegendsByMapLegendSet' + type,
			method: 'POST',
			params: { mapLegendSetId: mls },
			success: function(r) {
				var mapLegends = Ext.util.JSON.decode(r.responseText).mapLegends;
				var colors = [];
				var bounds = [];
				for (var i = 0; i < mapLegends.length; i++) {
					if (bounds[bounds.length-1] != mapLegends[i].startValue) {
						if (bounds.length != 0) {
							colors.push(new mapfish.ColorRgb(240,240,240));
						}
						bounds.push(mapLegends[i].startValue);
					}
					colors.push(new mapfish.ColorRgb());
					colors[colors.length-1].setFromHex(mapLegends[i].color);
					bounds.push(mapLegends[i].endValue);
				}

				choropleth.colorInterpolation = colors;
				choropleth.bounds = bounds;
				choropleth.classify(false, true);
			},
			failure: function() {
				alert('Error: getMapLegendsByMapLegendSet');
			}
		});
	},
    
    loadFromDatabase: function(id, isDrillDown) {
        if (isDrillDown) {
            load();
        }
        else if (id != choropleth.parentId || choropleth.mapView) {
            if (!choropleth.mapView) {
                if (!Ext.getCmp('map_tf').node.attributes.hasChildrenWithCoordinates) {
                    Ext.message.msg(false, i18n_no_coordinates_found);
                    Ext.getCmp('map_tf').setValue(Ext.getCmp('orgunit_tp').getNodeById(choropleth.parentId).attributes.text);                    
                    Ext.getCmp('map_tf').value = choropleth.parentId;
                    Ext.getCmp('map_tf').node = Ext.getCmp('orgunit_tp').getNodeById(choropleth.parentId);
                    return;
                }
            }
            load();
        }
            
        function load() {
            MASK.msg = i18n_loading_geojson;
            MASK.show();
            
            choropleth.parentId = id;
            choropleth.setUrl(path_mapping + 'getGeoJson.action?parentId=' + choropleth.parentId);
        }
    },
    
    loadFromFile: function(url) {
        if (url != choropleth.newUrl) {
            choropleth.newUrl = url;

            if (MAPSOURCE == map_source_type_geojson) {
                choropleth.setUrl(path_mapping + 'getGeoJsonFromFile.action?name=' + url);
            }
			else if (MAPSOURCE == map_source_type_shapefile) {
				choropleth.setUrl(path_geoserver + wfs + url + output);
			}
        }
        else {
            choropleth.classify(false, true);
        }
    },
    
    displayMapLegendTypeFields: function() {
        if (LEGEND[thematicMap].type == map_legend_type_automatic) {
			Ext.getCmp('maplegendset_cb').hideField();
		}
		else if (LEGEND[thematicMap].type == map_legend_type_predefined) {
			Ext.getCmp('maplegendset_cb').showField();
		}
    },
    
    validateForm: function(exception) {
        if (Ext.getCmp('mapvaluetype_cb').getValue() == map_value_type_indicator) {
            if (!Ext.getCmp('indicator_cb').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else if (Ext.getCmp('mapvaluetype_cb').getValue() == map_value_type_dataelement) {
            if (!Ext.getCmp('dataelement_cb').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        if (MAPDATETYPE == map_date_type_fixed) {
            if (!Ext.getCmp('period_cb').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else {
            if (!Ext.getCmp('startdate_df').getValue() && (!Ext.getCmp('enddate_df').getValue())) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        var cmp = MAPSOURCE == map_source_type_database ? Ext.getCmp('map_tf') : Ext.getCmp('map_cb');
        if (!cmp.getValue()) {
            if (exception) {
                Ext.message.msg(false, i18n_form_is_not_complete);
            }
            return false;
        }
        
        return true;
    },
    
    getIndicatorOrDataElementId: function() {
        return VALUETYPE.polygon == map_value_type_indicator ?
            Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
    },
    
    applyValues: function() {
        var options = {};
        choropleth.indicator = options.indicator = 'value';
        options.method = Ext.getCmp('method_cb').getValue();
        options.numClasses = Ext.getCmp('numClasses_cb').getValue();
        options.colors = choropleth.getColors();
        
        choropleth.coreComp.updateOptions(options);
        choropleth.coreComp.applyClassification();
        choropleth.classificationApplied = true;
    
        MASK.hide();
    },

    classify: function(exception, position) {
        if (MAPSOURCE == map_source_type_database) {
            choropleth.classifyDatabase(exception, position);
        }
        else {
            choropleth.classifyFile(exception, position);
        }
    },
    
    classifyDatabase: function(exception, position) {
		choropleth.displayMapLegendTypeFields();
        if (choropleth.validateForm(exception)) {
        
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();

            MAPDATA[ACTIVEPANEL].name = Ext.getCmp('map_tf').getValue();
            MAPDATA[ACTIVEPANEL].nameColumn = 'name';
            MAPDATA[ACTIVEPANEL].longitude = BASECOORDINATE.longitude;
            MAPDATA[ACTIVEPANEL].latitude = BASECOORDINATE.latitude;
            MAPDATA[ACTIVEPANEL].zoom = 7;
            
            if (!position) {
                if (MAPDATA[ACTIVEPANEL].zoom != MAP.getZoom()) {
                    MAP.zoomTo(MAPDATA[ACTIVEPANEL].zoom);
                }
                MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude));
            }
            
            if (choropleth.mapView) {
                if (choropleth.mapView.longitude && choropleth.mapView.latitude && choropleth.mapView.zoom) {
                    MAP.setCenter(new OpenLayers.LonLat(choropleth.mapView.longitude, choropleth.mapView.latitude), choropleth.mapView.zoom);
                }
                else {
                    MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude), MAPDATA[ACTIVEPANEL].zoom);
                }
                choropleth.mapView = false;
            }
            
            FEATURE[thematicMap] = MAP.getLayersByName('Polygon layer')[0].features;
            
            var indicatorOrDataElementId = VALUETYPE.polygon == map_value_type_indicator ?
                Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
            var dataUrl = VALUETYPE.polygon == map_value_type_indicator ?
                'getIndicatorMapValuesByParentOrganisationUnit' : 'getDataMapValuesByParentOrganisationUnit';
            var params = new Object();
            if (MAPDATETYPE == map_date_type_fixed) {
                params.periodId = Ext.getCmp('period_cb').getValue();
            }
            else {
                params.startDate = new Date(Ext.getCmp('startdate_df').getValue()).format('Y-m-d');
                params.endDate = new Date(Ext.getCmp('enddate_df').getValue()).format('Y-m-d');
            }
            params.id = indicatorOrDataElementId;
            params.parentId = choropleth.parentId;

            Ext.Ajax.request({
                url: path_mapping + dataUrl + type,
                method: 'POST',
                params: params,
                success: function(r) {
                    var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                    EXPORTVALUES = getExportDataValueJSON(mapvalues);
                    
                    if (mapvalues.length == 0) {
                        Ext.message.msg(false, i18n_current_selection_no_data );
                        MASK.hide();
                        return;
                    }

                    for (var i = 0; i < mapvalues.length; i++) {
                        for (var j = 0; j < FEATURE[thematicMap].length; j++) {
                            if (mapvalues[i].orgUnitName == FEATURE[thematicMap][j].attributes.name) {
                                FEATURE[thematicMap][j].attributes.value = parseFloat(mapvalues[i].value);
                                if (!FEATURE[thematicMap][j].attributes.labelString) {
                                    FEATURE[thematicMap][j].attributes.labelString = FEATURE[thematicMap][j].attributes.name;
                                    if (Ext.isNumber(FEATURE[thematicMap][j].attributes.value)) {
                                        FEATURE[thematicMap][j].attributes.labelString += ' (' + FEATURE[thematicMap][j].attributes.value.toFixed(1) + ')';
                                    }
                                }
                                break;
                            }
                        }
                    }
                    
                    choropleth.applyValues();
                },
                failure: function(r) {
                    alert('Error: ' + dataUrl);
                }
            });
        }
    },
    
    classifyFile: function(exception, position) {
		choropleth.displayMapLegendTypeFields();
        if (choropleth.validateForm(exception)) {
        
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();
            
            Ext.Ajax.request({
                url: path_mapping + 'getMapByMapLayerPath' + type,
                method: 'POST',
                params: { mapLayerPath: choropleth.newUrl },
                success: function(r) {
                    MAPDATA[ACTIVEPANEL] = Ext.util.JSON.decode(r.responseText).map[0];
                    
                    MAPDATA[ACTIVEPANEL].organisationUnitLevel = parseFloat(MAPDATA[ACTIVEPANEL].organisationUnitLevel);
                    MAPDATA[ACTIVEPANEL].longitude = parseFloat(MAPDATA[ACTIVEPANEL].longitude);
                    MAPDATA[ACTIVEPANEL].latitude = parseFloat(MAPDATA[ACTIVEPANEL].latitude);
                    MAPDATA[ACTIVEPANEL].zoom = parseFloat(MAPDATA[ACTIVEPANEL].zoom);
                    
                    if (!position) {
                        if (MAPDATA[ACTIVEPANEL].zoom != MAP.getZoom()) {
                            MAP.zoomTo(MAPDATA[ACTIVEPANEL].zoom);
                        }
                        MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude));
                    }
                    
                    if (choropleth.mapView) {
                        if (choropleth.mapView.longitude && choropleth.mapView.latitude && choropleth.mapView.zoom) {
                            MAP.setCenter(new OpenLayers.LonLat(choropleth.mapView.longitude, choropleth.mapView.latitude), choropleth.mapView.zoom);
                        }
                        else {
                            MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude), MAPDATA[ACTIVEPANEL].zoom);
                        }
                        choropleth.mapView = false;
                    }
            
                    FEATURE[thematicMap] = MAP.getLayersByName('Polygon layer')[0].features;
            
                    var indicatorOrDataElementId = VALUETYPE.polygon == map_value_type_indicator ?
                        Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
                    var dataUrl = VALUETYPE.polygon == map_value_type_indicator ?
                        'getIndicatorMapValuesByMap' : 'getDataMapValuesByMap';
                    var periodId = Ext.getCmp('period_cb').getValue();
                    var mapLayerPath = choropleth.newUrl;
                    
                    Ext.Ajax.request({
                        url: path_mapping + dataUrl + type,
                        method: 'POST',
                        params: {id:indicatorOrDataElementId, periodId:periodId, mapLayerPath:mapLayerPath},
                        success: function(r) {
                            var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                            EXPORTVALUES = getExportDataValueJSON(mapvalues);
                            var mv = new Array();
                            var mour = new Array();
                            var nameColumn = MAPDATA[thematicMap].nameColumn;
                            var options = {};
                            
                            if (mapvalues.length == 0) {
                                Ext.message.msg(false, i18n_current_selection_no_data );
                                MASK.hide();
                                return;
                            }
                            
                            for (var i = 0; i < mapvalues.length; i++) {
                                mv[mapvalues[i].orgUnitName] = mapvalues[i].orgUnitName ? mapvalues[i].value : '';
                            }
                            
                            Ext.Ajax.request({
                                url: path_mapping + 'getAvailableMapOrganisationUnitRelations' + type,
                                method: 'POST',
                                params: { mapLayerPath: mapLayerPath },
                                success: function(r) {
                                    var relations = Ext.util.JSON.decode(r.responseText).mapOrganisationUnitRelations;
                                   
                                    for (var i = 0; i < relations.length; i++) {
                                        mour[relations[i].featureId] = relations[i].organisationUnit;
                                    }

                                    for (var j = 0; j < FEATURE[thematicMap].length; j++) {
                                        var value = mv[mour[FEATURE[thematicMap][j].attributes[nameColumn]]];
                                        FEATURE[thematicMap][j].attributes.value = value ? parseFloat(value) : '';
                                        FEATURE[thematicMap][j].data.id = FEATURE[thematicMap][j].attributes[nameColumn];
                                        FEATURE[thematicMap][j].data.name = FEATURE[thematicMap][j].attributes[nameColumn];
                                        if (!FEATURE[thematicMap][j].attributes.labelString) {
                                            FEATURE[thematicMap][j].attributes.labelString = FEATURE[thematicMap][j].attributes[nameColumn];
                                            if (Ext.isNumber(FEATURE[thematicMap][j].attributes.value)) {
                                                FEATURE[thematicMap][j].attributes.labelString += ' (' + FEATURE[thematicMap][j].attributes.value.toFixed(1) + ')';
                                            }
                                        }
                                    }
                                    
                                    choropleth.applyValues();
                                }
                            });
                        }
                    });
                }
            });
        }
    },
            
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Choropleth.superclass.onRender.apply(this, arguments);
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.bwrap,
                    this.loadMask);
            this.loadMask.show();
        }

        var coreOptions = {
            'layer': this.layer,
            'format': this.format,
            'url': this.url,
            'requestSuccess': this.requestSuccess.createDelegate(this),
            'requestFailure': this.requestFailure.createDelegate(this),
            'featureSelection': this.featureSelection,
            'nameAttribute': this.nameAttribute,
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator
        };

        this.coreComp = new mapfish.GeoStat.Choropleth(this.map, coreOptions);
    }   
});

Ext.reg('choropleth', mapfish.widgets.geostat.Choropleth);