﻿/*
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
 * @requires core/GeoStat/Symbol.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

mapfish.widgets.geostat.Symbol = Ext.extend(Ext.FormPanel, {

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

    legend: false,

	imageLegend: false,

	bounds: false,

    parentId: false,

    mapView: false,

    mapData: false,
    
    labels: false,
    
    valueType: false,
    
    stores: false,
    
    selectFeatures: false,
    
    initComponent: function() {
        
        this.legend = {
            type: GLOBALS.config.map_legend_type_automatic,
            method: GLOBALS.config.classify_by_equal_intervals,
            classes: 5
        };
        
        this.valueType = GLOBALS.config.map_value_type_indicator;
        
        this.createStores();
        
        this.createItems();
        
        this.createSelectFeatures();
        
		mapfish.widgets.geostat.Symbol.superclass.initComponent.apply(this);
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
        OpenLayers.Console.error(i18n_ajax_request_failed);
    },
    
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf2').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf2').getValue());
        return [colorA, colorB];
    },
    
    createStores: function() {
        var mapViewStore = new Ext.data.JsonStore({
            url: GLOBALS.config.path_mapping + 'getAllMapViews' + GLOBALS.config.type,
            root: 'mapViews',
            fields: ['id', 'name'],
            sortInfo: {field: 'name', direction: 'ASC'},
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                }
            }
        });

        var indicatorGroupStore = new Ext.data.JsonStore({
            url: GLOBALS.config.path_mapping + 'getAllIndicatorGroups' + GLOBALS.config.type,
            root: 'indicatorGroups',
            fields: ['id', 'name'],
            idProperty: 'id',
            sortInfo: {field: 'name', direction: 'ASC'},
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                }
            }
        });
        
        var indicatorStore = new Ext.data.JsonStore({
            url: GLOBALS.config.path_mapping + 'getIndicatorsByIndicatorGroup' + GLOBALS.config.type,
            root: 'indicators',
            fields: ['id', 'name', 'shortName'],
            idProperty: 'id',
            sortInfo: {field: 'name', direction: 'ASC'},
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                    store.each(
                        function fn(record) {
                            var name = record.get('name');
                            name = name.replace('&lt;', '<').replace('&gt;', '>');
                            record.set('name', name);
                        }
                    );
                }
            }
        });
		
		var dataElementGroupStore = new Ext.data.JsonStore({
			url: GLOBALS.config.path_mapping + 'getAllDataElementGroups' + GLOBALS.config.type,
            root: 'dataElementGroups',
            fields: ['id', 'name'],
            sortInfo: {field: 'name', direction: 'ASC'},
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                }
            }
        });
		
		var dataElementStore = new Ext.data.JsonStore({
            url: GLOBALS.config.path_mapping + 'getDataElementsByDataElementGroup' + GLOBALS.config.type,
            root: 'dataElements',
            fields: ['id', 'name', 'shortName'],
            sortInfo: {field: 'name', direction: 'ASC'},
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                    store.each(
                        function fn(record) {
                            var name = record.get('name');
                            name = name.replace('&lt;', '<').replace('&gt;', '>');
                            record.set('name', name);
                        }
                    );
                }
            }
        });
        
        var periodTypeStore = new Ext.data.JsonStore({
            url: GLOBALS.config.path_mapping + 'getAllPeriodTypes' + GLOBALS.config.type,
            root: 'periodTypes',
            fields: ['name'],
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                }
            }
        });
            
        var periodStore = new Ext.data.JsonStore({
            url: GLOBALS.config.path_mapping + 'getPeriodsByPeriodType' + GLOBALS.config.type,
            root: 'periods',
            fields: ['id', 'name'],
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                }
            }
        });
            
        var mapStore = new Ext.data.JsonStore({
            url: GLOBALS.config.path_mapping + 'getAllMaps' + GLOBALS.config.type,
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            idProperty: 'mapLayerPath',
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                }
            }
        });
		
		var predefinedMapLegendSetStore = new Ext.data.JsonStore({
            url: GLOBALS.config.path_mapping + 'getMapLegendSetsByType' + GLOBALS.config.type,
            baseParams: {type: GLOBALS.config.map_legend_type_predefined},
            root: 'mapLegendSets',
            fields: ['id', 'name'],
            autoLoad: false,
            isLoaded: false,
            listeners: {
                'load': function(store) {
                    store.isLoaded = true;
                }
            }
        });
        
        this.stores = {
            mapView: mapViewStore,
            indicatorGroup: indicatorGroupStore,
            indicator: indicatorStore,
            dataElementGroup: dataElementGroupStore,
            dataElement: dataElementStore,
            periodType: periodTypeStore,
            period: periodStore,
            map: mapStore,
            predefinedMapLegendSet: predefinedMapLegendSetStore
        };
    },
    
    createItems: function() {
        this.items = [
        {
            xtype: 'combo',
            id: 'mapview_cb2',
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
			labelSeparator: GLOBALS.config.labelseparator,
            width: GLOBALS.config.combo_width,
            store: this.stores.mapView,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.Ajax.request({
                            url: GLOBALS.config.path_mapping + 'getMapView' + GLOBALS.config.type,
                            method: 'POST',
                            params: {id: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                this.mapView = GLOBALS.util.getNumericMapView(Ext.util.JSON.decode(r.responseText).mapView[0]);
                                this.legend.type = this.mapView.mapLegendType;
                                this.legend.method = this.mapView.method || this.legend.method;
                                this.legend.classes = this.mapView.classes || this.legend.classes;

                                MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);

                                Ext.getCmp('mapdatetype_cb').setValue(MAPDATETYPE);
                                Ext.getCmp('mapview_cb2').setValue(this.mapView.id);

                                this.valueType = this.mapView.mapValueType;
                                Ext.getCmp('mapvaluetype_cb2').setValue(this.valueType);

                                this.setMapView();
                            }
                        });
                    }
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
			id: 'mapvaluetype_cb2',
            fieldLabel: i18n_mapvaluetype,
			labelSeparator: GLOBALS.config.labelseparator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            width: GLOBALS.config.combo_width,
			value: GLOBALS.config.map_value_type_indicator,
            store: new Ext.data.SimpleStore({
                fields: ['id', 'name'],
                data: [
                    [GLOBALS.config.map_value_type_indicator, 'Indicators'],
                    [GLOBALS.config.map_value_type_dataelement, 'Data elements']
                ]
            }),
			listeners: {
				'select': {
                    scope: this,
					fn: function(cb) {
                        this.valueType = cb.getValue();
                        this.prepareMapViewValueType();
                        this.classify(false, true);
					}
				}
			}
		},
        
        {
            xtype: 'combo',
            id: 'indicatorgroup_cb2',
            fieldLabel: i18n_indicator_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.config.combo_width,
            store: this.stores.indicatorGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb2').clearValue();						
						Ext.getCmp('indicator_cb2').clearValue();
                        this.stores.indicator.setBaseParam('indicatorGroupId', cb.getValue());
                        this.stores.indicator.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'indicator_cb2',
            fieldLabel: i18n_indicator ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.config.combo_width,
            store: this.stores.indicator,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb2').clearValue();
 
                        Ext.Ajax.request({
                            url: GLOBALS.config.path_mapping + 'getMapLegendSetByIndicator' + GLOBALS.config.type,
                            method: 'POST',
                            params: {indicatorId: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    this.legend.type = GLOBALS.config.map_legend_type_predefined;
                                    this.prepareMapViewLegend();
                                    
                                    function load(scope) {
                                        Ext.getCmp('maplegendset_cb2').setValue(mapLegendSet.id);
                                        scope.applyPredefinedLegend();
                                    }
                                    
                                    if (!this.stores.predefinedMapLegendSet.isLoaded) {
                                        this.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            load(this);
                                        }});
                                    }
                                    else {
                                        load(this);
                                    }
                                }
                                else {
                                    this.legend.type = GLOBALS.config.map_legend_type_automatic;
                                    this.prepareMapViewLegend();
                                    this.classify(false, true);
                                }
                            }
                        });
                    }
                }
            }
        },
		
		{
            xtype: 'combo',
            id: 'dataelementgroup_cb2',
            fieldLabel: i18n_dataelement_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.config.combo_width,
            store: this.stores.dataElementGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb2').clearValue();
                        Ext.getCmp('dataelement_cb2').clearValue();
						this.stores.dataElement.setBaseParam('dataElementGroupId', cb.getValue());
                        this.stores.dataElement.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'dataelement_cb2',
            fieldLabel: i18n_dataelement,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.config.combo_width,
            store: this.stores.dataElement,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb2').clearValue();
 
                        Ext.Ajax.request({
                            url: GLOBALS.config.path_mapping + 'getMapLegendSetByDataElement' + GLOBALS.config.type,
                            method: 'POST',
                            params: {dataElementId: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    this.legend.type = GLOBALS.config.map_legend_type_predefined;
                                    this.prepareMapViewLegend();
                                    
                                    function load(scope) {
                                        Ext.getCmp('maplegendset_cb2').setValue(mapLegendSet.id);
                                        scope.applyPredefinedLegend();
                                    }
                                    
                                    if (!this.stores.predefinedMapLegendSet.isLoaded) {
                                        this.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            load(this);
                                        }});
                                    }
                                    else {
                                        load(this);
                                    }
                                }
                                else {
                                    this.legend.type = GLOBALS.config.map_legend_type_automatic;
                                    this.prepareMapViewLegend();
                                    this.classify(false, true);
                                }
                            }
                        });
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'periodtype_cb2',
            fieldLabel: i18n_period_type,
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.config.combo_width,
            store: this.stores.periodType,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb2').clearValue();                        
                        Ext.getCmp('period_cb2').clearValue();
                        this.stores.period.setBaseParam('name', cb.getValue());
                        this.stores.period.load();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            id: 'period_cb2',
            fieldLabel: i18n_period ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.config.combo_width,
            store: this.stores.period,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        Ext.getCmp('mapview_cb2').clearValue();
                        this.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            id: 'startdate_df2',
            fieldLabel: i18n_start_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            width: GLOBALS.config.combo_width,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(df, date) {
                        Ext.getCmp('mapview_cb2').clearValue();
                        Ext.getCmp('enddate_df2').setMinValue(date);
                        this.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            id: 'enddate_df2',
            fieldLabel: i18n_end_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            width: GLOBALS.config.combo_width,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(df, date) {
                        Ext.getCmp('mapview_cb2').clearValue();
                        Ext.getCmp('startdate_df2').setMaxValue(date);
                        this.classify(false, true);
                    }
                }
            }
        },                        
        
        {
            xtype: 'combo',
            id: 'map_cb2',
            fieldLabel: i18n_map ,
            typeAhead: true,
            editable: false,
            valueField: 'mapLayerPath',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.config.combo_width,
            store: this.stores.map,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb2').clearValue();
                        
                        if (cb.getValue() != this.newUrl) {
                            this.loadFromFile(cb.getValue());
                        }
                    }
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'map_tf2',
            fieldLabel: i18n_parent_orgunit,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.config.combo_width,
            listeners: {
                'focus': {
                    scope: this,
                    fn: function(tf) {
                        function showTree(scope) {
                            var value, rawvalue;
                            var w = new Ext.Window({
                                id: 'orgunit_w2',
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
                                        id: 'orgunit_tp2',
                                        bodyStyle: 'padding:7px',
                                        height: GLOBALS.util.getMultiSelectHeight(),
                                        autoScroll: true,
                                        loader: new Ext.tree.TreeLoader({
                                            dataUrl: GLOBALS.config.path_mapping + 'getOrganisationUnitChildren' + GLOBALS.config.type
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
                                                        tf.setValue(n.attributes.text);
                                                        tf.value = n.attributes.id;
                                                        tf.node = n;
                                                    }
                                                }
                                            },
                                            'expandnode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w2').syncSize();
                                                }
                                            },
                                            'collapsenode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w2').syncSize();
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
                                                scope: scope,
                                                handler: function() {
                                                    if (tf.getValue() && tf.getValue() != this.parentId) {
                                                        this.loadFromDatabase(tf.value);
                                                    }
                                                    Ext.getCmp('orgunit_w2').hide();
                                                }
                                            },
                                            {
                                                xtype: 'button',
                                                text: 'Cancel',
                                                width: 133,
                                                handler: function() {
                                                    Ext.getCmp('orgunit_w2').hide();
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

                        if (TOPLEVELUNIT) {
                            showTree(this);
                        }
                        else {
                            Ext.Ajax.request({
                                url: GLOBALS.config.path_commons + 'getOrganisationUnits' + GLOBALS.config.type,
                                params: {level: 1},
                                method: 'POST',
                                scope: this,
                                success: function(r) {
                                    var rootNode = Ext.util.JSON.decode(r.responseText).organisationUnits[0];
                                    TOPLEVELUNIT = {
                                        id: rootNode.id,
                                        name: rootNode.name,
                                        hasChildrenWithCoordinates: rootNode.hasChildrenWithCoordinates
                                    };
                                    showTree(this);
                                }
                            });
                        }
                    }
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_type,
            id: 'maplegendtype_cb2',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            value: this.legend.type,
            triggerAction: 'all',
            width: GLOBALS.config.combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[GLOBALS.config.map_legend_type_automatic, i18n_automatic],
					[GLOBALS.config.map_legend_type_predefined, i18n_predefined]
				]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (cb.getValue() == GLOBALS.config.map_legend_type_predefined && cb.getValue() != this.legend.type) {
							this.legend.type = GLOBALS.config.map_legend_type_predefined;
                            this.prepareMapViewLegend();
							
							if (Ext.getCmp('maplegendset_cb2').getValue()) {
                                this.applyPredefinedLegend();
							}
                        }
                        else if (cb.getValue() == GLOBALS.config.map_legend_type_automatic && cb.getValue() != this.legend.type) {
							this.legend.type = GLOBALS.config.map_legend_type_automatic;
							this.prepareMapViewLegend();                            
                            this.classify(false, true);
                        }
                    }
                }
            }
        },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_set,
            id: 'maplegendset_cb2',
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            triggerAction: 'all',
            width: GLOBALS.config.combo_width,
			hidden: true,
            store: this.stores.predefinedMapLegendSet,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
						this.applyPredefinedLegend();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            fieldLabel: i18n_method,
            id: 'method_cb2',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: GLOBALS.config.emptytext,
			labelSeparator: GLOBALS.config.labelseparator,
            value: this.legend.method,
            triggerAction: 'all',
            width: GLOBALS.config.combo_width,
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
                    scope: this,
                    fn: function(cb) {
                        if (cb.getValue() == GLOBALS.config.classify_with_bounds && cb.getValue() != this.legend.method) {
							this.legend.method = GLOBALS.config.classify_with_bounds;
                            this.prepareMapViewLegend();
                        }
                        else if (cb.getValue() != this.legend.method) {
							this.legend.method = cb.getValue();
                            this.prepareMapViewLegend();
                            this.classify(false, true);
                        }
                    }
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'bounds_tf2',
            fieldLabel: i18n_bounds,
			labelSeparator: GLOBALS.config.labelseparator,
            emptyText: i18n_comma_separated_values,
            isFormField: true,
            width: GLOBALS.config.combo_width,
            hidden: true
        },
        
        {
            xtype: 'combo',
            id: 'numClasses_cb2',
            fieldLabel: i18n_classes,
			labelSeparator: GLOBALS.config.labelseparator,
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: this.legend.classes,
            triggerAction: 'all',
            width: GLOBALS.config.combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb2').clearValue();
						
						if (cb.getValue() != this.legend.classes) {
							this.legend.classes = cb.getValue();
							this.classify(false, true);
						}
                    }
                }
            }
        },

        {
            xtype: 'colorfield',
            id: 'colorA_cf2',
            fieldLabel: i18n_low_color,
			labelSeparator: GLOBALS.config.labelseparator,
            allowBlank: false,
            isFormField: true,
            width: GLOBALS.config.combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            id: 'colorB_cf2',
            fieldLabel: i18n_high_color,
			labelSeparator: GLOBALS.config.labelseparator,
            allowBlank: false,
            isFormField: true,
            width: GLOBALS.config.combo_width,
            value: "#FF0000"
        },
        
        { html: '<br>' },

        {
            xtype: 'button',
            text: i18n_refresh,
			cls: 'aa_med',
            isFormField: true,
            fieldLabel: '',
            labelSeparator: '',
            scope: this,
            handler: function() {
                if (this.validateForm()) {
                    this.layer.setVisibility(true);
                    this.classify(true, true);
                }
                else {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
            }
        }

        ];
    },
    
    createSelectFeatures: function() {
        this.selectFeatures = {
            onHoverSelect: function onHoverSelect(feature) {
                Ext.getCmp('featureinfo_l').setText('<div style="color:black">' + feature.attributes[proportionalSymbol.mapData.nameColumn] + '</div><div style="color:#555">' + feature.attributes.value + '</div>', false);
            },
            onHoverUnselect: function onHoverUnselect(feature) {
                Ext.getCmp('featureinfo_l').setText('<span style="color:#666">' + i18n_no_feature_selected + '</span>', false);
            },
            onClickSelect: function onClickSelect(feature) {
                var east_panel = Ext.getCmp('east');
                var x = east_panel.x - 210;
                var y = east_panel.y + 41;
                
                if (MAPSOURCE == GLOBALS.config.map_source_type_database) {
                    if (feature.attributes.hasChildrenWithCoordinates) {
                        if (lfw) {
                            lfw.destroy();
                        }
                        
                        Ext.getCmp('map_tf2').setValue(feature.data.name);
                        Ext.getCmp('map_tf2').value = feature.attributes.id;
                        proportionalSymbol.loadFromDatabase(feature.attributes.id, true);
                    }
                    else {
                        Ext.message.msg(false, i18n_no_coordinates_found);
                    }
                }
            }
        }
        
        var sf = new OpenLayers.Control.newSelectFeature(
            this.layer, {
                onHoverSelect: this.selectFeatures.onHoverSelect,
                onHoverUnselect: this.selectFeatures.onHoverUnselect,
                onClickSelect: this.selectFeatures.onClickSelect,
            }
        );
        
        MAP.addControl(sf);
        sf.activate();
    },
    
    prepareMapViewValueType: function() {
        var obj = {};
        if (this.valueType == GLOBALS.config.map_value_type_indicator) {
            Ext.getCmp('indicatorgroup_cb2').showField();
            Ext.getCmp('indicator_cb2').showField();
            Ext.getCmp('dataelementgroup_cb2').hideField();
            Ext.getCmp('dataelement_cb2').hideField();
            obj.components = {
                valueTypeGroup: Ext.getCmp('indicatorgroup_cb2'),
                valueType: Ext.getCmp('indicator_cb2')
            };
            obj.stores = {
                valueTypeGroup: this.stores.indicatorGroup,
                valueType: this.stores.indicator
            };
            obj.mapView = {
                valueTypeGroup: 'indicatorGroupId',
                valueType: 'indicatorId'
            };
        }
        else if (this.valueType == GLOBALS.config.map_value_type_dataelement) {
            Ext.getCmp('indicatorgroup_cb2').hideField();
            Ext.getCmp('indicator_cb2').hideField();
            Ext.getCmp('dataelementgroup_cb2').showField();
            Ext.getCmp('dataelement_cb2').showField();
            obj.components = {
                valueTypeGroup: Ext.getCmp('dataelementgroup_cb2'),
                valueType: Ext.getCmp('dataelement_cb2')
            };
            obj.stores = {
                valueTypeGroup: this.stores.dataElementGroup,
                valueType: this.stores.dataElement
            };
            obj.mapView = {
                valueTypeGroup: 'dataElementGroupId',
                valueType: 'dataElementId'
            };
        }
        return obj;
    },
    
    prepareMapViewDateType: function() {
        var obj = {};
        if (MAPDATETYPE == GLOBALS.config.map_date_type_fixed) {
            Ext.getCmp('periodtype_cb2').showField();
            Ext.getCmp('period_cb2').showField();
            Ext.getCmp('startdate_df2').hideField();
            Ext.getCmp('enddate_df2').hideField();
            obj.components = {
                c1: Ext.getCmp('periodtype_cb2'),
                c2: Ext.getCmp('period_cb2')
            };
            obj.stores = {
                c1: this.stores.periodType,
                c2: this.stores.period
            };
            obj.mapView = {
                c1: 'periodTypeId',
                c2: 'periodId'
            };
        }
        else if (MAPDATETYPE == GLOBALS.config.map_date_type_start_end) {
            Ext.getCmp('periodtype_cb2').hideField();
            Ext.getCmp('period_cb2').hideField();
            Ext.getCmp('startdate_df2').showField();
            Ext.getCmp('enddate_df2').showField();
            obj.components = {
                c1: Ext.getCmp('startdate_df2'),
                c2: Ext.getCmp('enddate_df2')
            };
            obj.mapView = {
                c1: 'startDate',
                c2: 'endDate'
            };
        }
        return obj;
    },
    
    prepareMapViewLegend: function() {
        Ext.getCmp('maplegendtype_cb2').setValue(this.legend.type);
        
        if (this.legend.type == GLOBALS.config.map_legend_type_automatic) {
            Ext.getCmp('method_cb2').showField();
            Ext.getCmp('colorA_cf2').showField();
            Ext.getCmp('colorB_cf2').showField();
            Ext.getCmp('maplegendset_cb2').hideField();
            
            if (this.legend.method == GLOBALS.config.classify_with_bounds) {
                Ext.getCmp('numClasses_cb2').hideField();
                Ext.getCmp('bounds_tf2').showField();
            }
            else {
                Ext.getCmp('numClasses_cb2').showField();
                Ext.getCmp('bounds_tf2').hideField();
            }                
        }
        else if (this.legend.type == GLOBALS.config.map_legend_type_predefined) {
            Ext.getCmp('method_cb2').hideField();
            Ext.getCmp('numClasses_cb2').hideField();
            Ext.getCmp('bounds_tf2').hideField();
            Ext.getCmp('colorA_cf2').hideField();
            Ext.getCmp('colorB_cf2').hideField();
            Ext.getCmp('maplegendset_cb2').showField();
        }
    },
    
    prepareMapViewMap: function() {
        if (MAPSOURCE == GLOBALS.config.map_source_type_database) {
            Ext.getCmp('map_cb2').hideField();
            Ext.getCmp('map_tf2').showField();
        }
        else {
            Ext.getCmp('map_cb2').showField();
            Ext.getCmp('map_tf2').hideField();
        }
    },
    
    setMapView: function() {
        obj = this.prepareMapViewValueType();
        
        function valueTypeGroupStoreCallback(scope) {
            obj.components.valueTypeGroup.setValue(scope.mapView[obj.mapView.valueTypeGroup]);
            
            obj.stores.valueType.setBaseParam(obj.mapView.valueTypeGroup, obj.components.valueTypeGroup.getValue());
            obj.stores.valueType.load({scope: scope, callback: function() {
                obj.components.valueType.setValue(scope.mapView[obj.mapView.valueType]);
                
                obj = scope.prepareMapViewDateType();
                if (MAPDATETYPE == GLOBALS.config.map_date_type_fixed) {
                    if (obj.stores.c1.isLoaded) {
                        dateTypeGroupStoreCallback(scope);
                    }
                    else {
                        obj.stores.c1.load({scope: scope, callback: function() {
                            dateTypeGroupStoreCallback(scope);
                        }});
                    }
                }
                else if (MAPDATETYPE == GLOBALS.config.map_date_type_start_end) {
                    obj.components.c1.setValue(new Date(scope.mapView[obj.mapView.c1]));
                    obj.components.c2.setValue(new Date(scope.mapView[obj.mapView.c2]));
                    
                    scope.setMapViewLegend();
                }                
            }});
        }
        
        function dateTypeGroupStoreCallback(scope) {
            obj.components.c1.setValue(scope.mapView[obj.mapView.c1]);
            
            obj.stores.c2.setBaseParam('name', scope.mapView[obj.mapView.c1]);
            obj.stores.c2.load({scope: scope, callback: function() {
                obj.components.c2.setValue(scope.mapView[obj.mapView.c2]);
                
                scope.setMapViewLegend();
            }});
        }
        
        if (obj.stores.valueTypeGroup.isLoaded) {
            valueTypeGroupStoreCallback(this);
        }
        else {
            obj.stores.valueTypeGroup.load({scope: this, callback: function() {
                valueTypeGroupStoreCallback(this);
            }});
        }
    },
    
    setMapViewLegend: function() {
        this.prepareMapViewLegend();
        
        function predefinedMapLegendSetStoreCallback(scope) {
            Ext.getCmp('maplegendset_cb2').setValue(scope.mapView.mapLegendSetId);
            scope.applyPredefinedLegend(true);
        }
        
        if (this.legend.type == GLOBALS.config.map_legend_type_automatic) {
            Ext.getCmp('method_cb2').setValue(this.mapView.method);
            Ext.getCmp('colorA_cf2').setValue(this.mapView.colorLow);
            Ext.getCmp('colorB_cf2').setValue(this.mapView.colorHigh);
            
            if (this.legend.method == GLOBALS.config.classify_with_bounds) {
                Ext.getCmp('bounds_tf2').setValue(this.mapView.bounds);
            }
            else {
                Ext.getCmp('numClasses_cb2').setValue(this.mapView.classes);
            }
            
            this.setMapViewMap();
        }
        else if (this.legend.type == GLOBALS.config.map_legend_type_predefined) {
            if (this.stores.isLoaded) {
                predefinedMapLegendSetStoreCallback(this);
            }
            else {
                this.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                    predefinedMapLegendSetStoreCallback(this);
                }});
            }
        }            
    },
    
    setMapViewMap: function() {
        this.prepareMapViewMap();

        if (MAPSOURCE == GLOBALS.config.map_source_type_database) {
            Ext.getCmp('map_tf2').setValue(this.mapView.parentOrganisationUnitName);
            Ext.getCmp('map_tf2').value = this.mapView.mapSource;
            this.loadFromDatabase(this.mapView.mapSource);
        }
        else {
            Ext.getCmp('map_cb2').setValue(this.mapView.mapSource);
            this.loadFromFile(this.mapView.mapSource);
        }
    },
	
	applyPredefinedLegend: function(isMapView) {
        this.legend.type = GLOBALS.config.map_legend_type_predefined;
		var mls = Ext.getCmp('maplegendset_cb2').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: GLOBALS.config.path_mapping + 'getMapLegendsByMapLegendSet' + GLOBALS.config.type,
			method: 'POST',
			params: {mapLegendSetId: mls},
            scope: this,
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

				this.colorInterpolation = colors;
				this.bounds = bounds;
                
                if (isMapView) {
                    this.setMapViewMap();
                }
                else {
                    this.classify(false, true);
                }                   
			}
		});
	},
    
    loadFromDatabase: function(id, isDrillDown) {
        if (isDrillDown) {
            load(this);
        }
        else if (id != this.parentId || this.mapView) {
            if (!this.mapView) {
                if (!Ext.getCmp('map_tf2').node.attributes.hasChildrenWithCoordinates) {
                    Ext.message.msg(false, i18n_no_coordinates_found);
                    Ext.getCmp('map_tf2').setValue(Ext.getCmp('orgunit_tp2').getNodeById(this.parentId).attributes.text);                    
                    Ext.getCmp('map_tf2').value = this.parentId;
                    Ext.getCmp('map_tf2').node = Ext.getCmp('orgunit_tp2').getNodeById(this.parentId);
                    return;
                }
            }
            load(this);
        }
            
        function load(scope) {
            MASK.msg = i18n_loading_geojson;
            MASK.show();
            
            scope.parentId = id;
            scope.setUrl(GLOBALS.config.path_mapping + 'getGeoJson.action?parentId=' + scope.parentId);
        }
    },
    
    loadFromFile: function(url) {
        if (url != this.newUrl) {
            this.newUrl = url;

            if (MAPSOURCE == GLOBALS.config.map_source_type_geojson) {
                this.setUrl(GLOBALS.config.path_mapping + 'getGeoJsonFromFile.action?name=' + url);
            }
			else if (MAPSOURCE == GLOBALS.config.map_source_type_shapefile) {
				this.setUrl(GLOBALS.config.path_geoserver + GLOBALS.config.wfs + url + GLOBALS.config.output);
			}
        }
        else {
            this.classify(false, true);
        }
    },
    
    validateForm: function(exception) {
        if (Ext.getCmp('mapvaluetype_cb2').getValue() == GLOBALS.config.map_value_type_indicator) {
            if (!Ext.getCmp('indicator_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else if (Ext.getCmp('mapvaluetype_cb2').getValue() == GLOBALS.config.map_value_type_dataelement) {
            if (!Ext.getCmp('dataelement_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        if (MAPDATETYPE == GLOBALS.config.map_date_type_fixed) {
            if (!Ext.getCmp('period_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else {
            if (!Ext.getCmp('startdate_df2').getValue() || !Ext.getCmp('enddate_df2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        var cmp = MAPSOURCE == GLOBALS.config.map_source_type_database ? Ext.getCmp('map_tf2') : Ext.getCmp('map_cb2');
        if (!cmp.getValue()) {
            if (exception) {
                Ext.message.msg(false, i18n_form_is_not_complete);
            }
            return false;
        }
        
        if (Ext.getCmp('maplegendtype_cb2').getValue() == GLOBALS.config.map_legend_type_automatic) {
            if (Ext.getCmp('method_cb2').getValue() == GLOBALS.config.classify_with_bounds) {
                if (!Ext.getCmp('bounds_tf2').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                    }
                    return false;
                }
            }
        }
        else if (Ext.getCmp('maplegendtype_cb2').getValue() == GLOBALS.config.map_legend_type_predefined) {
            if (!Ext.getCmp('maplegendset_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }            
        
        return true;
    },
    
    getFormValues: function() {
        return {
            mapValueType: Ext.getCmp('mapvaluetype_cb2').getValue(),
            indicatorGroupId: Ext.getCmp('indicatorgroup_cb2').getValue() || '',
            indicatorId: Ext.getCmp('indicator_cb2').getValue() || '',
            dataElementGroupId: Ext.getCmp('dataelementgroup_cb2').getValue() || '',
            dataElementId: Ext.getCmp('dataelement_cb2').getValue() || '',
            periodTypeId: Ext.getCmp('periodtype_cb2').getValue() || '',
            periodId: Ext.getCmp('period_cb2').getValue() || '',
            startDate: Ext.getCmp('startdate_df2').getValue() || '',
            endDate: Ext.getCmp('enddate_df2').getValue() || '',
            mapSource: MAPSOURCE == GLOBALS.config.map_source_type_database ?
                Ext.getCmp('map_tf2').value : Ext.getCmp('map_cb2').getValue(),
            mapLegendType: Ext.getCmp('maplegendtype_cb2').getValue(),
            method: this.legend.type == GLOBALS.config.map_legend_type_automatic ? Ext.getCmp('method_cb2').getValue() : '',
            classes: this.legend.type == GLOBALS.config.map_legend_type_automatic ? Ext.getCmp('numClasses_cb2').getValue() : '',
            bounds: this.legend.type == GLOBALS.config.map_legend_type_automatic && this.legend.method == GLOBALS.config.classify_with_bounds ? Ext.getCmp('bounds_tf2').getValue() : '',
            colorLow: this.legend.type == GLOBALS.config.map_legend_type_automatic ? Ext.getCmp('colorA_cf2').getValue() : '',
            colorHigh: this.legend.type == GLOBALS.config.map_legend_type_automatic ? Ext.getCmp('colorB_cf2').getValue() : '',
            mapLegendSetId: Ext.getCmp('maplegendset_cb2').getValue() || '',
            longitude: MAP.getCenter().lon,
            latitude: MAP.getCenter().lon,
            zoom: parseInt(MAP.getZoom())
        };
    },
    
    applyValues: function() {
        var options = {};
        this.indicator = 'value';
        options.indicator = this.indicator;
        options.method = Ext.getCmp('method_cb2').getValue();
        options.numClasses = Ext.getCmp('numClasses_cb2').getValue();
        options.colors = this.getColors();
        
        this.coreComp.updateOptions(options);
        this.coreComp.applyClassification();
        this.classificationApplied = true;
        
        MASK.hide();
    },

    classify: function(exception, position) {
        if (MAPSOURCE == GLOBALS.config.map_source_type_database) {
            this.classifyDatabase(exception, position);
        }
        else {
            this.classifyFile(exception, position);
        }
    },
    
    classifyDatabase: function(exception, position) {
        if (this.validateForm(exception)) {
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();
            
            if (!position) {
                MAP.zoomToExtent(this.layer.getDataExtent());
            }
            
            this.mapData = {
                nameColumn: 'name'
            };
            
            if (this.mapView) {
                if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                    MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                }
                else {
                    MAP.zoomToExtent(this.layer.getDataExtent());
                }
                this.mapView = false;
            }
            
            var dataUrl = this.valueType == GLOBALS.config.map_value_type_indicator ?
                'getIndicatorMapValuesByParentOrganisationUnit' : 'getDataMapValuesByParentOrganisationUnit';
            
            var params = {
                id: this.valueType == GLOBALS.config.map_value_type_indicator ? Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue(),
                periodId: MAPDATETYPE == GLOBALS.config.map_date_type_fixed ? Ext.getCmp('period_cb2').getValue() : null,
                startDate: MAPDATETYPE == GLOBALS.config.map_date_type_start_end ? new Date(Ext.getCmp('startdate_df2').getValue()).format('Y-m-d') : null,
                endDate: MAPDATETYPE == GLOBALS.config.map_date_type_start_end ? new Date(Ext.getCmp('enddate_df2').getValue()).format('Y-m-d') : null,
                parentId: this.parentId
            };
                
            Ext.Ajax.request({
                url: GLOBALS.config.path_mapping + dataUrl + GLOBALS.config.type,
                method: 'POST',
                params: params,
                scope: this,
                success: function(r) {
                    var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                    EXPORTVALUES = GLOBALS.util.getExportDataValueJSON(mapvalues);
                    
                    if (mapvalues.length == 0) {
                        Ext.message.msg(false, i18n_current_selection_no_data);
                        MASK.hide();
                        return;
                    }

                    for (var i = 0; i < mapvalues.length; i++) {
                        for (var j = 0; j < this.layer.features.length; j++) {
                            if (mapvalues[i].orgUnitName == this.layer.features[j].attributes.name) {
                                this.layer.features[j].attributes.value = parseFloat(mapvalues[i].value);
                                this.layer.features[j].attributes.labelString = this.layer.features[j].attributes.name + ' (' + this.layer.features[j].attributes.value + ')';
                                break;
                            }
                        }
                    }
                    
                    this.applyValues();
                }
            });
        }
    },
    
    classifyFile: function(exception, position) {
        if (this.validateForm(exception)) {
        
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();
            
            Ext.Ajax.request({
                url: GLOBALS.config.path_mapping + 'getMapByMapLayerPath' + GLOBALS.config.type,
                method: 'POST',
                params: {mapLayerPath: this.newUrl},
                scope: this,
                success: function(r) {
                    this.mapData = Ext.util.JSON.decode(r.responseText).map[0];
                    
                    this.mapData.organisationUnitLevel = parseFloat(this.mapData.organisationUnitLevel);
                    this.mapData.longitude = parseFloat(this.mapData.longitude);
                    this.mapData.latitude = parseFloat(this.mapData.latitude);
                    this.mapData.zoom = parseFloat(this.mapData.zoom);
                    
                    if (!position) {
                        if (this.mapData.zoom != MAP.getZoom()) {
                            MAP.zoomTo(this.mapData.zoom);
                        }
                        MAP.setCenter(new OpenLayers.LonLat(this.mapData.longitude, this.mapData.latitude));
                    }
                    
                    if (this.mapView) {
                        if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                            MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                        }
                        else {
                            MAP.setCenter(new OpenLayers.LonLat(this.mapData.longitude, this.mapData.latitude), this.mapData.zoom);
                        }
                        this.mapView = false;
                    }
                    
                    var params = {
                        id: this.valueType == GLOBALS.config.map_value_type_indicator ? Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue()
                    };
                        
            
                    var indicatorOrDataElementId = this.valueType == GLOBALS.config.map_value_type_indicator ?
                        Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
                    var dataUrl = this.valueType == GLOBALS.config.map_value_type_indicator ?
                        'getIndicatorMapValuesByMap' : 'getDataMapValuesByMap';
                    var periodId = Ext.getCmp('period_cb2').getValue();
                    var mapLayerPath = this.newUrl;
                    
                    Ext.Ajax.request({
                        url: GLOBALS.config.path_mapping + dataUrl + GLOBALS.config.type,
                        method: 'POST',
                        params: {id:indicatorOrDataElementId, periodId:periodId, mapLayerPath:mapLayerPath},
                        scope: this,
                        success: function(r) {
                            var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                            EXPORTVALUES = GLOBALS.util.getExportDataValueJSON(mapvalues);
                            var mv = new Array();
                            var mour = new Array();
                            var nameColumn = this.mapData.nameColumn;
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
                                url: GLOBALS.config.path_mapping + 'getAvailableMapOrganisationUnitRelations' + GLOBALS.config.type,
                                method: 'POST',
                                params: {mapLayerPath: mapLayerPath},
                                scope: this,
                                success: function(r) {
                                    var relations = Ext.util.JSON.decode(r.responseText).mapOrganisationUnitRelations;
                                   
                                    for (var i = 0; i < relations.length; i++) {
                                        mour[relations[i].featureId] = relations[i].organisationUnit;
                                    }

                                    for (var j = 0; j < this.layer.features.length; j++) {
                                        var value = mv[mour[this.layer.features[j].attributes[nameColumn]]];
                                        this.layer.features[j].attributes.value = value ? parseFloat(value) : '';
                                        this.layer.features[j].data.id = this.layer.features[j].attributes[nameColumn];
                                        this.layer.features[j].data.name = this.layer.features[j].attributes[nameColumn];
                                        this.layer.features[j].attributes.labelString = this.layer.features[j].attributes[nameColumn] + ' (' + this.layer.features[j].attributes.value + ')';
                                    }
                                    
                                    this.applyValues();
                                }
                            });
                        }
                    });
                }
            });
        }
    },
    
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Symbol.superclass.onRender.apply(this, arguments);
        if (this.loadMask) {
            this.loadMask = new Ext.LoadMask(this.bwrap, this.loadMask);
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

        this.coreComp = new mapfish.GeoStat.Symbol(this.map, coreOptions);
    }   
});

Ext.reg('proportionalSymbol', mapfish.widgets.geostat.Symbol);