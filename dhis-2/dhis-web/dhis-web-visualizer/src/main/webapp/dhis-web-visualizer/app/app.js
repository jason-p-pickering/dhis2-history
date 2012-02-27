DV.conf = {
    init: {
		example: {
			series: ['Series 1', 'Series 2', 'Series 3', 'Series 4'],
			category: ['Category 1', 'Category 2', 'Category 3'],
			filter: [DV.i18n.example_chart],
			values: [84, 77, 87, 82, 91, 69, 82, 78, 83, 76, 73, 85],
			setState: function() {
				DV.state.type = DV.conf.finals.chart.column;
				DV.state.series.dimension = DV.conf.finals.dimension.data.value;
				DV.state.series.names = DV.conf.init.example.series;
				DV.state.category.dimension = DV.conf.finals.dimension.period.value;
				DV.state.category.names = DV.conf.init.example.category;
				DV.state.filter.dimension = DV.conf.finals.dimension.organisationunit.value;
				DV.state.filter.names = DV.conf.init.example.filter;
				DV.state.targetLineValue = 80;
				DV.state.targetLineLabel = 'Target line label';
				DV.state.rangeAxisLabel = 'Range axis label';
				DV.state.domainAxisLabel = 'Domain axis label';
			},
			setValues: function() {
				var obj1 = {}, obj2 = {}, obj3 = {}, obj4 = {}, obj5 = {}, obj6 = {}, obj7 = {}, obj8 = {}, obj9 = {}, obj10 = {}, obj11 = {}, obj12 = {};
				obj1[DV.state.series.dimension] = DV.conf.init.example.series[0];
				obj1[DV.state.category.dimension] = DV.conf.init.example.category[0];
				obj1.v = DV.conf.init.example.values[0];
				obj2[DV.state.series.dimension] = DV.conf.init.example.series[1];
				obj2[DV.state.category.dimension] = DV.conf.init.example.category[0];
				obj2.v = DV.conf.init.example.values[1];
				obj3[DV.state.series.dimension] = DV.conf.init.example.series[2];
				obj3[DV.state.category.dimension] = DV.conf.init.example.category[0];
				obj3.v = DV.conf.init.example.values[2];
				obj4[DV.state.series.dimension] = DV.conf.init.example.series[3];
				obj4[DV.state.category.dimension] = DV.conf.init.example.category[0];
				obj4.v = DV.conf.init.example.values[3];
				obj5[DV.state.series.dimension] = DV.conf.init.example.series[0];
				obj5[DV.state.category.dimension] = DV.conf.init.example.category[1];
				obj5.v = DV.conf.init.example.values[4];
				obj6[DV.state.series.dimension] = DV.conf.init.example.series[1];
				obj6[DV.state.category.dimension] = DV.conf.init.example.category[1];
				obj6.v = DV.conf.init.example.values[5];
				obj7[DV.state.series.dimension] = DV.conf.init.example.series[2];
				obj7[DV.state.category.dimension] = DV.conf.init.example.category[1];
				obj7.v = DV.conf.init.example.values[6];
				obj8[DV.state.series.dimension] = DV.conf.init.example.series[3];
				obj8[DV.state.category.dimension] = DV.conf.init.example.category[1];
				obj8.v = DV.conf.init.example.values[7];
				obj9[DV.state.series.dimension] = DV.conf.init.example.series[0];
				obj9[DV.state.category.dimension] = DV.conf.init.example.category[2];
				obj9.v = DV.conf.init.example.values[8];
				obj10[DV.state.series.dimension] = DV.conf.init.example.series[1];
				obj10[DV.state.category.dimension] = DV.conf.init.example.category[2];
				obj10.v = DV.conf.init.example.values[9];
				obj11[DV.state.series.dimension] = DV.conf.init.example.series[2];
				obj11[DV.state.category.dimension] = DV.conf.init.example.category[2];
				obj11.v = DV.conf.init.example.values[10];
				obj12[DV.state.series.dimension] = DV.conf.init.example.series[3];
				obj12[DV.state.category.dimension] = DV.conf.init.example.category[2];
				obj12.v = DV.conf.init.example.values[11];
				DV.value.values = [obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12];
			}
		},
		ajax: {
			jsonfy: function(r) {
				r = Ext.JSON.decode(r.responseText);
				var obj = {system: {rootNode: {id: r.rn[0], name: r.rn[1], level: 1}, periods: {}, user: {id: r.user.id, isAdmin: r.user.isAdmin, organisationUnit: {id: r.user.ou[0], name: r.user.ou[1]}}}};
				for (var relative in r.p) {
					obj.system.periods[relative] = [];
					for (var i = 0; i < r.p[relative].length; i++) {
						obj.system.periods[relative].push({id: r.p[relative][i][0], name: r.p[relative][i][1]});
					}
				}
				return obj;
			}
		}
    },
    finals: {
        ajax: {
            path_visualizer: '../',
            path_commons: '../../dhis-web-commons-ajax-json/',
            path_api: '../../api/',
            path_portal: '../../dhis-web-portal/',
            path_images: 'images/',
            initialize: 'initialize.action',
            redirect: 'redirect.action',
            data_get: 'getAggregatedValues.action',
            indicator_get: 'getIndicatorsMinified.action',
            indicatorgroup_get: 'getIndicatorGroupsMinified.action',
            dataelement_get: 'getDataElementsMinified.action',
            dataelementgroup_get: 'getDataElementGroupsMinified.action',
            dataelement_get: 'getDataElementsMinified.action',
            dataset_get: 'getDataSetsMinified.action',
            organisationunitchildren_get: 'getOrganisationUnitChildren.action',
            favorite_addorupdate: 'addOrUpdateChart.action',
            favorite_addorupdatesystem: 'addOrUpdateSystemChart.action',            
            favorite_get: 'charts/',
            favorite_getall: 'getSystemAndCurrentUserCharts.action',
            favorite_delete: 'deleteCharts.action'
        },
        dimension: {
            data: {
                value: 'data',
                rawvalue: DV.i18n.data,
                warning: {
					filter: DV.i18n.wm_multiple_filter_ind_de
				}
            },
            indicator: {
                value: 'indicator',
                rawvalue: DV.i18n.indicator
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: DV.i18n.dataelement
            },
            dataset: {
				value: 'dataset',
                rawvalue: DV.i18n.dataset
			},
            period: {
                value: 'period',
                rawvalue: DV.i18n.period,
                warning: {
					filter: DV.i18n.wm_multiple_filter_period
				}
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: DV.i18n.organisation_unit,
                warning: {
					filter: DV.i18n.wm_multiple_filter_orgunit
				}
            }
        },        
        chart: {
            series: 'series',
            category: 'category',
            filter: 'filter',
            column: 'column',
            stackedcolumn: 'stackedcolumn',
            bar: 'bar',
            stackedbar: 'stackedbar',
            line: 'line',
            area: 'area',
            pie: 'pie'
        },
        data: {
			domain: 'domain_',
			targetline: 'targetline_',
			trendline: 'trendline_'
		},
        image: {
            png: 'png',
            pdf: 'pdf'
        },
        cmd: {
            init: 'init_',
			urlparam: 'id'
        }
    },
    chart: {
        style: {
            inset: 30,
            font: 'arial,sans-serif,ubuntu,consolas'
        },
        theme: {
            dv1: ['#94ae0a', '#0c4375', '#a61120', '#ff8809', '#7c7474', '#a61187', '#ffd13e', '#24ad9a', '#a66111', '#414141', '#4500c4', '#1d5700']
        }
    },
    statusbar: {
		icon: {
			error: 'error_s.png',
			warning: 'warning.png',
			ok: 'ok.png'
		}
	},
    layout: {
        west_width: 424,
        west_fieldset_width: 402,
        west_fill_height: 440,
        center_tbar_height: 31,
        east_tbar_height: 31,
        east_gridcolumn_height: 30,
        form_label_width: 55,
        window_favorite_ypos: 100,
        window_confirm_width: 250,
        grid_favorite_width: 420
    }
};

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'lib/ext-ux');
Ext.require('Ext.ux.form.MultiSelect');

Ext.onReady( function() {
    Ext.override(Ext.form.FieldSet,{setExpanded:function(a){var b=this,c=b.checkboxCmp,d=b.toggleCmp,e;a=!!a;if(c){c.setValue(a)}if(d){d.setType(a?"up":"down")}if(a){e="expand";b.removeCls(b.baseCls+"-collapsed")}else{e="collapse";b.addCls(b.baseCls+"-collapsed")}b.collapsed=!a;b.doComponentLayout();b.fireEvent(e,b);return b}});
    Ext.QuickTips.init();
    document.body.oncontextmenu = function(){return false;}; 
    
    Ext.Ajax.request({
        url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.initialize,
        success: function(r) {
            
    DV.init = DV.conf.init.ajax.jsonfy(r);
    
    DV.init.initialize = function() {
        DV.util.combobox.filter.category();
        
        DV.conf.init.example.setState();
        DV.conf.init.example.setValues();
        
        DV.init.cmd = DV.util.getUrlParam(DV.conf.finals.cmd.urlparam) || DV.conf.finals.cmd.init;
        DV.exe.execute(true, DV.init.cmd);
    };
    
    DV.cmp = {
        region: {},
        charttype: [],
        settings: {},
        fieldset: {},
        dimension: {
            indicator: {},
            dataelement: {},
            dataset: {},
            period: [],
            organisationunit: {}
        },
        toolbar: {
            menuitem: {}
        },
        statusbar: {},
        favorite: {
            rename: {}
        }
    };
    
    DV.util = {
        getCmp: function(q) {
            return DV.viewport.query(q)[0];
        },
        getUrlParam: function(s) {
            var output = '';
            var href = window.location.href;
            if (href.indexOf('?') > -1 ) {
                var query = href.substr(href.indexOf('?') + 1);
                var query = query.split('&');
                for (var i = 0; i < query.length; i++) {
                    if (query[i].indexOf('=') > -1) {
                        var a = query[i].split('=');
                        if (a[0].toLowerCase() === s) {
							output = a[1];
							break;
						}
                    }
                }
            }
            return unescape(output);
        },
        viewport: {
            getSize: function() {
                return {x: DV.cmp.region.center.getWidth(), y: DV.cmp.region.center.getHeight()};
            },
            getXY: function() {
                return {x: DV.cmp.region.center.x + 15, y: DV.cmp.region.center.y + 43};
            },
            getPageCenterX: function(cmp) {
                return ((screen.width/2)-(cmp.width/2));
            },
            getPageCenterY: function(cmp) {
                return ((screen.height/2)-((cmp.height/2)-100));
            }
        },
        multiselect: {
            select: function(a, s) {
                var selected = a.getValue();
                if (selected.length) {
                    var array = [];
                    Ext.Array.each(selected, function(item) {
                        array.push({id: item, s: a.store.getAt(a.store.findExact('id', item)).data.s});
                    });
                    s.store.add(array);
                }
                this.filterAvailable(a, s);
            },
            selectAll: function(a, s) {
                var array = [];
                a.store.each( function(r) {
                    array.push({id: r.data.id, s: r.data.s});
                });
                s.store.add(array);
                this.filterAvailable(a, s);
            },            
            unselect: function(a, s) {
                var selected = s.getValue();
                if (selected.length) {
                    Ext.Array.each(selected, function(item) {
                        s.store.remove(s.store.getAt(s.store.findExact('id', item)));
                    });                    
                    this.filterAvailable(a, s);
                }
            },
            unselectAll: function(a, s) {
                s.store.removeAll();
                a.store.clearFilter();
            },
            filterAvailable: function(a, s) {
                a.store.filterBy( function(r) {
                    var filter = true;
                    s.store.each( function(r2) {
                        if (r.data.id === r2.data.id) {
                            filter = false;
                        }
                    });
                    return filter;
                });
                a.store.sort('s', 'ASC');
            }
        },
        fieldset: {
            toggleIndicator: function() {
                DV.cmp.fieldset.indicator.toggle();
            },
            toggleDataElement: function() {
                DV.cmp.fieldset.dataelement.toggle();
            },
            toggleDataSet: function() {
                DV.cmp.fieldset.dataset.toggle();
            },
            togglePeriod: function() {
                DV.cmp.fieldset.period.toggle();
            },
            toggleOrganisationUnit: function() {
                DV.cmp.fieldset.organisationunit.toggle();
            },
            toggleOptions: function() {
                DV.cmp.fieldset.options.toggle();
            },
            collapseFieldsets: function(fieldsets) {
                for (var i = 0; i < fieldsets.length; i++) {
                    fieldsets[i].collapse();
                }
            }
        },
        button: {
            type: {
                getValue: function() {
                    for (var i = 0; i < DV.cmp.charttype.length; i++) {
                        if (DV.cmp.charttype[i].pressed) {
                            return DV.cmp.charttype[i].name;
                        }
                    }
                },
                setValue: function(type) {
                    for (var i = 0; i < DV.cmp.charttype.length; i++) {
                        DV.cmp.charttype[i].toggle(DV.cmp.charttype[i].name === type);
                    }
                },
                toggleHandler: function(b) {
                    if (!b.pressed) {
                        b.toggle();
                    }
                }
            }
        },
        store: {
            addToStorage: function(s, records) {
                s.each( function(r) {
                    if (!s.storage[r.data.id]) {
                        s.storage[r.data.id] = {id: r.data.id, s: r.data.s, name: r.data.s, parent: s.parent};
                    }
                });
                if (records) {
                    Ext.Array.each(records, function(r) {
                        if (!s.storage[r.data.id]) {
                            s.storage[r.data.id] = {id: r.data.id, s: r.data.s, name: r.data.s, parent: s.parent};
                        }
                    });
                }                        
            },
            loadFromStorage: function(s) {
                var items = [];
                s.removeAll();
                for (var obj in s.storage) {
                    if (s.storage[obj].parent === s.parent) {
                        items.push(s.storage[obj]);
                    }
                }
                s.add(items);
                s.sort('s', 'ASC');
            },
            containsParent: function(s) {
                for (var obj in s.storage) {
                    if (s.storage[obj].parent === s.parent) {
                        return true;
                    }
                }
                return false;
            }
        },
        dimension: {
            indicator: {
                getIds: function(exception) {
                    var a = [];
                    DV.cmp.dimension.indicator.selected.store.each( function(r) {
                        a.push(r.data.id);
                    });
                    if (exception && !a.length) {
                        alert(DV.i18n.no_indicators_selected);
                    }
                    return a;
                }
            },
            dataelement: {
                getIds: function(exception) {
                    if (DV.cmp.dimension.dataelement.selected.store) {
                        var a = [];
                        DV.cmp.dimension.dataelement.selected.store.each( function(r) {
                            a.push(r.data.id);
                        });
                        if (exception && !a.length) {
                            alert(DV.i18n.no_data_elements_selected);
                        }
                        return a;
                    }
                    else {
                        alert(DV.i18n.data_element_store_does_not_exist);
                    }
                }
            },
            data: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DV.state.indicatorIds, function(r) {
                        a.push('indicatorIds=' + r);
                    });
                    Ext.Array.each(DV.state.dataelementIds, function(r) {
                        a.push('dataElementIds=' + r);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception, isFilter) {
                    var a = [];
                    DV.cmp.dimension.indicator.selected.store.each( function(r) {
                        a.push(DV.util.string.getEncodedString(r.data.s));
                    });
                    if (DV.cmp.dimension.dataelement.selected.store) {
                        DV.cmp.dimension.dataelement.selected.store.each( function(r) {
                            a.push(DV.util.string.getEncodedString(r.data.s));
                        });
                    }
                    if (exception && !a.length) {
						DV.util.notification.error(DV.i18n.et_no_indicators_dataelements, DV.i18n.em_no_indicators_dataelements);
                    }
                    if (exception && isFilter && a.length > 1) {
						DV.exe.warnings.push(DV.i18n.wm_first_filter_unit);
					}
					return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                }                    
            },
            period: {
                getUrl: function(isFilter) {
                    var a = [];
                    for (var r in DV.state.relativePeriods) {
                        if (DV.state.relativePeriods[r]) {
                            Ext.Array.each(DV.init.system.periods[r], function(item) {
                                a.push('periodIds=' + item.id);
                            });
                        }
                    }
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception, isFilter) {
                    var a = [],
                        cmp = DV.cmp.dimension.period;
                    Ext.Array.each(cmp, function(item) {
                        if (item.getValue()) {
                            Ext.Array.each(DV.init.system.periods[item.paramName], function(item) {
                                a.push(DV.util.string.getEncodedString(item.name));
                            });
                        }
                    });
                    if (exception && !a.length) {
						DV.util.notification.error(DV.i18n.et_no_periods, DV.i18n.em_no_periods);
                    }
                    if (exception && isFilter && a.length > 1) {
						DV.exe.warnings.push(DV.i18n.wm_first_filter_unit);
					}
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNamesByRelativePeriodsObject: function(rp) {
                    var relatives = [],
                        names = [];
                    for (var r in rp) {
                        if (rp[r]) {
                            relatives.push(r);
                        }
                    }
                    for (var i = 0; i < relatives.length; i++) {
                        var r = DV.init.system.periods[relatives[i]] || [];
                        for (var j = 0; j < r.length; j++) {
                            names.push(r[j].name);
                        }
                    }
                    return names;
                },                        
                getNameById: function(id) {
                    for (var obj in DV.init.system.periods) {
                        var a = DV.init.system.periods[obj];
                        for (var i = 0; i < a.length; i++) {
                            if (a[i].id == id) {
                                return a[i].name;
                            }
                        };
                    }
                },
                getIds: function(exception) {
                    var a = [],
                        cmp = DV.cmp.dimension.period;
                    Ext.Array.each(cmp, function(item) {
                        if (item.getValue()) {
                            a.push(item.paramName);
                        }
                    });
                    if (exception && !a.length) {
						DV.util.notification.error(DV.i18n.et_no_periods, DV.i18n.em_no_periods);
                    }
                    return a;
                },
                getRelativePeriodObject: function(exception) {
                    var a = {},
                        cmp = DV.cmp.dimension.period,
                        valid = false;
                    Ext.Array.each(cmp, function(item) {
                        a[item.paramName] = item.getValue();
                        valid = item.getValue() ? true : valid;
                    });
                    if (exception && !valid) {
						DV.util.notification.error(DV.i18n.et_no_periods, DV.i18n.em_no_periods);
                    }
                    return a;
                }   
            },
            organisationunit: {
                getUrl: function(isFilter) {
                    var a = [];
					Ext.Array.each(DV.state.organisationunitIds, function(item) {
						a.push('organisationUnitIds=' + item);
					});
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception, isFilter) {
                    var a = [],
                        tp = DV.cmp.dimension.organisationunit.treepanel,
                        selection = tp.getSelectionModel().getSelection();
					if (!selection.length) {
						selection = [tp.getRootNode()];
						tp.selectRoot();
					}
					Ext.Array.each(selection, function(r) {
						a.push(DV.util.string.getEncodedString(r.data.text));
					});
					if (exception && !a.length) {
						DV.util.notification.error(DV.i18n.et_no_orgunits, DV.i18n.em_no_orgunits);
					}
                    if (exception && isFilter && a.length > 1 && !DV.state.userOrganisationUnit) {
						DV.exe.warnings.push(DV.i18n.wm_first_filter_unit);
					}
                    return DV.state.userOrganisationUnit ? [DV.init.system.user.organisationUnit.name] : (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getIds: function(exception) {
                    var a = [],
                        tp = DV.cmp.dimension.organisationunit.treepanel,
                        selection = tp.getSelectionModel().getSelection();
					if (!selection.length) {
						selection = [tp.getRootNode()];
						tp.selectRoot();
					}
					Ext.Array.each(selection, function(r) {
						a.push(r.data.id);
					});
					if (exception && !a.length) {
						DV.util.notification.error(DV.i18n.et_no_orgunits, DV.i18n.em_no_orgunits);
					}
                    return DV.state.userOrganisationUnit ? [DV.init.system.user.organisationUnit.id] : a;
                }                    
            }
        },
        notification: {
			error: function(title, text) {
				title = title || '';
				text = text || '';
				Ext.create('Ext.window.Window', {
					title: title,
					cls: 'dv-messagebox',
					iconCls: 'dv-window-title-messagebox',
					modal: true,
					width: 300,
					items: [
						{
							xtype: 'label',
							width: 40,
							text: text
						}
					]
				}).show();
				DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
				DV.cmp.statusbar.panel.update('<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.error + '" style="padding:0 5px 0 0"/>' + text);
			},
			warning: function(text) {
				text = text || '';
				DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
				DV.cmp.statusbar.panel.update('<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.warning + '" style="padding:0 5px 0 0"/>' + text);
			},
			ok: function() {
				DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
				DV.cmp.statusbar.panel.update('<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.ok + '" style="padding:0 5px 0 0"/>&nbsp;&nbsp;');
			}				
		},
        mask: {
            showMask: function(cmp, str) {
                if (DV.mask) {
                    DV.mask.destroy();
                }
                DV.mask = new Ext.LoadMask(cmp, {msg: str});
                DV.mask.show();
            },
            hideMask: function() {
				if (DV.mask) {
					DV.mask.hide();
				}
			}
        },
        chart: {
			default: {
				getChart: function(axes, series) {
					return Ext.create('Ext.chart.Chart', {
						animate: true,
						store: DV.store.chart,
						insetPadding: DV.conf.chart.style.inset,
						items: DV.state.hideSubtitle ? false : DV.util.chart.default.getTitle(),
						legend: DV.state.hideLegend ? false : DV.util.chart.default.getLegend(),
						axes: axes,
						series: series,
						theme: 'dv1'
					});
				},
				getLegend: function(len) {
					len = len ? len : DV.store.chart.range.length;
					return {
						position: len > 5 ? 'right' : 'top',
						labelFont: '15px ' + DV.conf.chart.style.font,
						boxStroke: '#ffffff',
						boxStrokeWidth: 0,
						padding: 0
					};
				},
				getTitle: function() {
					return {
						type: 'text',
						text: DV.state.filter.names[0],
						font: 'bold 15px ' + DV.conf.chart.style.font,
						fill: '#222',
						width: 300,
						height: 20,
						x: 28,
						y: 16
					};
				},
				label: {
					getCategory: function() {
						return {
							font: '14px ' + DV.conf.chart.style.font,
							rotate: {
								degrees: 330
							}
						};
					},
					getNumeric: function() {
						return {
							font: '13px ' + DV.conf.chart.style.font,
							renderer: Ext.util.Format.numberRenderer(DV.util.number.getChartAxisFormatRenderer())
						};
					}
				},
				axis: {
					getNumeric: function(stacked) {
						var axis = {
							type: 'Numeric',
							position: 'left',
							title: DV.state.rangeAxisLabel || false,
							labelTitle: {
								font: '17px ' + DV.conf.chart.style.font
							},
							minimum: 0,
							fields: stacked ? DV.state.series.names : DV.store.chart.range,
							label: DV.util.chart.default.label.getNumeric(),
							grid: {
								odd: {
									opacity: 1,
									fill: '#fefefe',
									stroke: '#aaa',
									'stroke-width': 0.1
								},									
								even: {
									opacity: 1,
									fill: '#f1f1f1',
									stroke: '#aaa',
									'stroke-width': 0.1
								}
							}
						};
						if (DV.init.cmd === DV.conf.finals.cmd.init) {
							axis.maximum = 100;
							axis.majorTickSteps = 10;
						}
						return axis;
					},
					getCategory: function() {
						return {
							type: 'Category',
							position: 'bottom',
							title: DV.state.domainAxisLabel || false,
							labelTitle: {
								font: '17px ' + DV.conf.chart.style.font
							},
							fields: DV.conf.finals.data.domain,
							label: DV.util.chart.default.label.getCategory()
						};
					}
				},
				series: {
					getTips: function() {
						return {
							trackMouse: true,
							cls: 'dv-chart-tips',
							renderer: function(si, item) {
								this.update('' + item.value[1]);
							}
						};
					},
					getTargetLine: function() {
						var title = DV.state.targetLineLabel || DV.i18n.target;
						title += ' (' + DV.state.targetLineValue + ')';
						return {
							type: 'line',
							axis: 'left',
							xField: DV.conf.finals.data.domain,
							yField: DV.conf.finals.data.targetline,
							style: {
								opacity: 1,
								lineWidth: 3,
								stroke: '#051a2e'
							},
							markerConfig: {
								type: 'circle',
								radius: 0
							},
							title: title
						};
					},
					getTrendLineArray: function() {
						var a = [];
						for (var i = 0; i < DV.chart.trendLine.length; i++) {
							a.push({
								type: 'line',
								axis: 'left',
								xField: DV.conf.finals.data.domain,
								yField: DV.chart.trendLine[i].key,
								style: {
									opacity: 0.8,
									lineWidth: 3
								},
								markerConfig: {
									type: 'circle',
									radius: 4
								},
								tips: DV.util.chart.default.series.getTips(),
								title: DV.chart.trendLine[i].name
							});
						}
						return a;
					},
					setTheme: function() {
						var colors = DV.conf.chart.theme.dv1.slice(0, DV.state.series.names.length);						
						if (DV.state.targetLineValue) {
							colors.push('#051a2e', '#051a2e');
						}						
						Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
							constructor: function(config) {
								Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
									seriesThemes: colors,
									colors: colors
								}, config));
							}
						});
					}
				}
			},
            bar: {
				label: {
					getCategory: function() {
						return {
							font: '14px ' + DV.conf.chart.style.font
						};
					}
				},
				axis: {
					getNumeric: function() {
						var num = DV.util.chart.default.axis.getNumeric();
						num.position = 'bottom';
						return num;
					},
					getCategory: function() {
						var cat = DV.util.chart.default.axis.getCategory();
						cat.position = 'left';
						cat.label = DV.util.chart.bar.label.getCategory();
						return cat;
					}
				},
				series: {
					getTips: function() {
						return {
							trackMouse: true,
							cls: 'dv-chart-tips',
							renderer: function(si, item) {
								this.update('' + item.value[0]);
							}
						};
					},
					getTargetLine: function() {
						var tl = DV.util.chart.default.series.getTargetLine();
						tl.axis = 'bottom';
						tl.xField = DV.conf.finals.data.targetline;
						tl.yField = DV.conf.finals.data.domain;
						return tl;
					},
					getTrendLineArray: function() {
						var a = [];
						for (var i = 0; i < DV.chart.trendLine.length; i++) {
							a.push({
								type: 'line',
								axis: 'bottom',
								xField: DV.chart.trendLine[i].key,
								yField: DV.conf.finals.data.domain,
								style: {
									opacity: 0.8,
									lineWidth: 3
								},
								markerConfig: {
									type: 'circle',
									radius: 4
								},
								tips: DV.util.chart.bar.series.getTips(),
								title: DV.chart.trendLine[i].name
							});
						}
						return a;
					}
				}
            },
            line: {
				series: {
					getArray: function() {
						var a = [];
						for (var i = 0; i < DV.state.series.names.length; i++) {
							a.push({
								type: 'line',
								axis: 'left',
								xField: DV.conf.finals.data.domain,
								yField: DV.state.series.names[i],
								style: {
									opacity: 0.8,
									lineWidth: 3
								},
								markerConfig: {
									type: 'circle',
									radius: 4
								},
								tips: DV.util.chart.default.series.getTips()
							});
						}
						return a;
					},
					setTheme: function() {
						var colors = DV.conf.chart.theme.dv1.slice(0, DV.state.series.names.length);
						if (DV.state.trendLine) {
							colors = colors.concat(colors);
						}
						if (DV.state.targetLineValue) {
							colors.push('#051a2e');
						}						
						Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
							constructor: function(config) {
								Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
									seriesThemes: colors,
									colors: colors
								}, config));
							}
						});
					}
				}
            },
            pie: {
                getTitle: function() {
                    return [
                        {
                            type: 'text',
                            text: DV.state.filter.names[0],
                            font: 'bold 15px ' + DV.conf.chart.style.font,
                            fill: '#222',
                            width: 300,
                            height: 20,
                            x: 28,
                            y: 16
                        },
                        {
                            type: 'text',
                            text: DV.state.series.names[0],
                            font: '13px ' + DV.conf.chart.style.font,
                            fill: '#444',
                            width: 300,
                            height: 20,
                            x: 28,
                            y: 36
                        }
                    ];                        
                },
                series: {
					getTips: function() {
						return {
							trackMouse: true,
							cls: 'dv-chart-tips-pie',
							renderer: function(item) {
								this.update(item.data[DV.conf.finals.data.domain] + '<br/><b>' + item.data[DV.state.series.names[0]] + '</b>');
							}
						};
					},
					setTheme: function() {
						var colors = DV.conf.chart.theme.dv1.slice(0, DV.state.category.names.length);
						Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
							constructor: function(config) {
								Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
									seriesThemes: colors,
									colors: colors
								}, config));
							}
						});
					}
				}
            }
        },
        combobox: {
            filter: {
                clearValue: function(v, cb, i, d) {
                    if (v === cb.getValue()) {
                        cb.clearValue();
                    }
                },
                category: function() {
                    var cbs = DV.cmp.settings.series,
                        cbc = DV.cmp.settings.category,
                        cbf = DV.cmp.settings.filter,
                        v = cbs.getValue(),
                        d = DV.conf.finals.dimension.data.value,
                        p = DV.conf.finals.dimension.period.value,
                        o = DV.conf.finals.dimension.organisationunit.value,
                        index = 0;
                        
                    this.clearValue(v, cbc);
                    this.clearValue(v, cbf);
                    
                    cbc.filterArray = [!(v === d), !(v === p), !(v === o)];
                    cbc.store.filterBy( function(r) {
                        return cbc.filterArray[index++];
                    });
                    
                    this.filter();
                },                
                filter: function() {
                    var cbc = DV.cmp.settings.category,
                        cbf = DV.cmp.settings.filter,
                        v = cbc.getValue(),
                        d = DV.conf.finals.dimension.data.value,
                        p = DV.conf.finals.dimension.period.value,
                        o = DV.conf.finals.dimension.organisationunit.value,
                        index = 0;
                        
                    this.clearValue(v, cbf);
                        
                    cbf.filterArray = Ext.Array.clone(cbc.filterArray);
                    cbf.filterArray[0] = cbf.filterArray[0] ? !(v === d) : false;
                    cbf.filterArray[1] = cbf.filterArray[1] ? !(v === p) : false;
                    cbf.filterArray[2] = cbf.filterArray[2] ? !(v === o) : false;
                    
                    cbf.store.filterBy( function(r) {
                        return cbf.filterArray[index++];
                    });
                }
            }
        },
        checkbox: {
            setRelativePeriods: function(rp) {
                for (var r in rp) {
                    var cmp = DV.util.getCmp('checkbox[paramName="' + r + '"]');
                    if (cmp) {
                        cmp.setValue(rp[r]);
                    }
                }
            }
        },                
        number: {
            isInteger: function(n) {
                var str = new String(n);
                if (str.indexOf('.') > -1) {
                    var d = str.substr(str.indexOf('.') + 1);
                    return (d.length === 1 && d == '0');
                }
                return false;
            },
            allValuesAreIntegers: function(values) {
                for (var i = 0; i < values.length; i++) {
                    if (!this.isInteger(values[i].v)) {
                        return false;
                    }
                }
                return true;
            },
            getChartAxisFormatRenderer: function() {
                return this.allValuesAreIntegers(DV.value.values) ? '0' : '0.0';
            }
        },
       /*FIXME:This is probably not going to work as intended with UNICODE?*/
        string: {
            getEncodedString: function(text) {
                return text.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'');
            }
        },
        value: {
            jsonfy: function(r) {
                r = Ext.JSON.decode(r.responseText),
                values = [];
                for (var i = 0; i < r.length; i++) {
                    values.push({d: r[i][0], p: r[i][1], o: r[i][2], v: r[i][3]});
                }
                return values;
            }
        },
        crud: {
            favorite: {
                create: function(fn, isUpdate) {
                    DV.util.mask.showMask(DV.cmp.favorite.window, DV.i18n.saving + '...');
                    
                    var params = DV.state.getParams();
                    params.name = DV.cmp.favorite.name.getValue();
                    
                    if (isUpdate) {
                        params.uid = DV.store.favorite.getAt(DV.store.favorite.findExact('name', params.name)).data.id;
                    }
					
                    var url = DV.cmp.favorite.system.getValue() ? DV.conf.finals.ajax.favorite_addorupdatesystem : DV.conf.finals.ajax.favorite_addorupdate;                    
                    Ext.Ajax.request({
                        url: DV.conf.finals.ajax.path_visualizer + url,
                        params: params,
                        success: function() {
                            DV.store.favorite.load({callback: function() {
                                DV.util.mask.hideMask();
                                if (fn) {
                                    fn();
                                }
                            }});
                        }
                    });
                },
                update: function(fn) {
                    DV.util.crud.favorite.create(fn, true);
                },
                updateName: function(name) {
                    if (DV.store.favorite.findExact('name', name) != -1) {
                        return;
                    }
                    DV.util.mask.showMask(DV.cmp.favorite.window, DV.i18n.renaming + '...');
                    var r = DV.cmp.favorite.grid.getSelectionModel().getSelection()[0];
                    var url = DV.cmp.favorite.system.getValue() ? DV.conf.finals.ajax.favorite_addorupdatesystem : DV.conf.finals.ajax.favorite_addorupdate;
                    Ext.Ajax.request({
                        url: DV.conf.finals.ajax.path_visualizer + url,
                        params: {uid: r.data.id, name: name},
                        success: function() {
                            DV.store.favorite.load({callback: function() {
                                DV.cmp.favorite.rename.window.close();
                                DV.util.mask.hideMask();
                                DV.cmp.favorite.grid.getSelectionModel().select(DV.store.favorite.getAt(DV.store.favorite.findExact('name', name)));
                                DV.cmp.favorite.name.setValue(name);
                            }});
                        }
                    });
                },
                del: function(fn) {
                    DV.util.mask.showMask(DV.cmp.favorite.window, DV.i18n.deleting + '...');
                    var baseurl = DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.favorite_delete,
                        selection = DV.cmp.favorite.grid.getSelectionModel().getSelection();
                    Ext.Array.each(selection, function(item) {
                        baseurl = Ext.String.urlAppend(baseurl, 'uids=' + item.data.id);
                    });
                    Ext.Ajax.request({
                        url: baseurl,
                        success: function() {
                            DV.store.favorite.load({callback: function() {
                                DV.util.mask.hideMask();
                                if (fn) {
                                    fn();
                                }
                            }});
                        }
                    }); 
                }
            }
        }
    };
    
    DV.store = {
        dimension: function() {
            return Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: [
                    {id: DV.conf.finals.dimension.data.value, name: DV.conf.finals.dimension.data.rawvalue},
                    {id: DV.conf.finals.dimension.period.value, name: DV.conf.finals.dimension.period.rawvalue},
                    {id: DV.conf.finals.dimension.organisationunit.value, name: DV.conf.finals.dimension.organisationunit.rawvalue}
                ]
            });
        },
        indicator: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.indicator_get,
                    reader: {
                        type: 'json',
                        root: 'indicators'
                    }
                },
                storage: {},
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                data: []
            })
        },
        dataelement: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.dataelement_get,
                    reader: {
                        type: 'json',
                        root: 'dataElements'
                    }
                },
                storage: {},
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                data: []
            })
        },
        dataset: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.dataset_get,
                    reader: {
                        type: 'json',
                        root: 'dataSets'
                    }
                },
                storage: {},
                isLoaded: false,
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                data: []
            })
        },
        chart: null,
        getChartStore: function(exe) {
            var keys = [];
            Ext.Array.each(DV.chart.data, function(item) {
				keys = Ext.Array.merge(keys, Ext.Object.getKeys(item));
            });
            this.chart = Ext.create('Ext.data.Store', {
                fields: keys,
                data: DV.chart.data
            });
            
            this.chart.range = keys.slice(0);
            for (var i = 0; i < this.chart.range.length; i++) {
                if (this.chart.range[i] === DV.conf.finals.data.domain) {
                    this.chart.range.splice(i, 1);
                }
            }
            
            if (exe) {
                DV.chart.getChart(true);
            }
            else {
                return this.chart;
            }
        },
        datatable: null,
        getDataTableStore: function(exe) {
            this.datatable = Ext.create('Ext.data.Store', {
                fields: [
                    DV.conf.finals.dimension.data.value,
                    DV.conf.finals.dimension.period.value,
                    DV.conf.finals.dimension.organisationunit.value,
                    'v'
                ],
                data: DV.value.values
            });
            
            if (exe) {
                DV.datatable.getDataTable(true);
            }
            else {
                return this.datatable;
            }
            
        },
        favorite: Ext.create('Ext.data.Store', {
            fields: ['id', 'name', 'lastUpdated', 'userId'],
            proxy: {
                type: 'ajax',
                url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.favorite_getall,
                reader: {
                    type: 'json',
                    root: 'charts'
                }
            },
            isLoaded: false,
            sorting: {
                field: 'name',
                direction: 'ASC'
            },
            sortStore: function() {
                this.sort(this.sorting.field, this.sorting.direction);
            },
            listeners: {
                load: function(s) {
					s.isLoaded = !s.isLoaded ? true : false;
					
                    s.sortStore();
                    s.each(function(r) {
                        r.data.lastUpdated = r.data.lastUpdated.substr(0,16);
                        r.data.icon = '<img src="' + DV.conf.finals.ajax.path_images + 'favorite.png" />';
                        r.commit();
                    });
                }
            }
        })            
    };
    
    DV.state = {
        type: DV.conf.finals.chart.column,
        series: {
            dimension: null,
            names: []
        },
        category: {
            dimension: null,
            names: []
        },
        filter: {
            dimension: null,
            names: []
        },
        indicatorIds: [],
        dataelementIds: [],
        relativePeriods: {},
        organisationunitIds: [],
        hideSubtitle: false,
        hideLegend: false,
        domainAxisLabel: null,
        rangeAxisLabel: null,
        targetLineValue: 70,
        targetLineLabel: null,
        trendLine: null,
        userOrganisationUnit: false,
        isRendered: false,
        getState: function(exe) {
            this.type = DV.util.button.type.getValue();
            
            this.getOptions();
            
            this.series.dimension = DV.cmp.settings.series.getValue();
            this.category.dimension = DV.cmp.settings.category.getValue();
            this.filter.dimension = DV.cmp.settings.filter.getValue();
            
            if (!this.validation.dimensions.call(this)) {
				return;
			}
            
            this.series.names = DV.util.dimension[this.series.dimension].getNames();
            this.category.names = DV.util.dimension[this.category.dimension].getNames();
            this.filter.names = DV.util.dimension[this.filter.dimension].getNames();

            if (!this.validation.names.call(this)) {
				return;
			}
			
            this.validation.filter.call(this);
            
            this.indicatorIds = DV.util.dimension.indicator.getIds();
            this.dataelementIds = DV.util.dimension.dataelement.getIds();
            this.relativePeriods = DV.util.dimension.period.getRelativePeriodObject();
            this.organisationunitIds = DV.util.dimension.organisationunit.getIds();
            
            this.validation.trendline.call(this);
            
            this.validation.targetline.call(this);
            
            this.validation.render.call(this);
            
            if (exe) {
                DV.value.getValues(true);
            }
        },
        getOptions: function() {            
            this.hideSubtitle = DV.cmp.favorite.hidesubtitle.getValue();
            this.hideLegend = DV.cmp.favorite.hidelegend.getValue();
            this.trendLine = DV.cmp.favorite.trendline.getValue();
            this.userOrganisationUnit = DV.cmp.favorite.userorganisationunit.getValue();
            this.domainAxisLabel = DV.cmp.favorite.domainaxislabel.getValue();
            this.rangeAxisLabel = DV.cmp.favorite.rangeaxislabel.getValue();
            this.targetLineValue = parseFloat(DV.cmp.favorite.targetlinevalue.getValue());
            this.targetLineLabel = DV.cmp.favorite.targetlinelabel.getValue();
		},
        getParams: function() {
            var obj = {};
            obj.type = this.type.toUpperCase();
            
			obj.hideSubtitle = DV.cmp.favorite.hidesubtitle.getValue();
			obj.hideLegend = DV.cmp.favorite.hidelegend.getValue();
			obj.trendLine = DV.cmp.favorite.trendline.getValue();
			obj.userOrganisationUnit = DV.cmp.favorite.userorganisationunit.getValue();
			obj.domainAxisLabel = DV.cmp.favorite.domainaxislabel.getValue();
			obj.rangeAxisLabel = DV.cmp.favorite.rangeaxislabel.getValue();
			obj.targetLineValue = DV.cmp.favorite.targetlinevalue.getValue();
			obj.targetLineLabel = (obj.targetLineValue && !DV.cmp.favorite.targetlinelabel.isDisabled()) ? DV.cmp.favorite.targetlinelabel.getValue() : null;
			
            obj.series = this.series.dimension.toUpperCase();
            obj.category = this.category.dimension.toUpperCase();
            obj.filter = this.filter.dimension.toUpperCase();
			
            obj.indicatorIds = this.indicatorIds;
            obj.dataElementIds = this.dataelementIds;
            obj = Ext.Object.merge(obj, this.relativePeriods);
            obj.organisationUnitIds = this.organisationunitIds;
            
            return obj;            
        },
        setFavorite: function(exe, uid) {
            if (uid) {
                Ext.Ajax.request({
                    url: DV.conf.finals.ajax.path_api + DV.conf.finals.ajax.favorite_get + uid + '.json?links=false',
                    scope: this,
                    success: function(r) {
						if (!this.validation.response.call(this, r)) {
							return;
						}
						
                        var f = Ext.JSON.decode(r.responseText),
							indiment = [],
							irec = [],
							drec = [];	
                            
						if (!this.validation.favorite(f)) {
							return;
						}
                        
                        f.type = f.type.toLowerCase();
                        f.series = f.series.toLowerCase();
                        f.category = f.category.toLowerCase();
                        f.filter = f.filter.toLowerCase();        
                                        
                        f.names = {
                            data: [],
                            period: [],
                            organisationunit: []
                        };
                        
                        this.type = f.type;
						
                        this.hideSubtitle = f.hideSubtitle;
                        this.hideLegend = f.hideLegend;                        
                        this.trendLine = f.regression;
                        this.userOrganisationUnit = f.userOrganisationUnit;
                        this.domainAxisLabel = f.domainAxisLabel;
                        this.rangeAxisLabel = f.rangeAxisLabel;
                        this.targetLineValue = f.targetLineValue ? parseFloat(f.targetLineValue) : null;
                        this.targetLineLabel = f.targetLineLabel ? f.targetLineLabel : null;
                        
                        this.series.dimension = f.series;
                        this.category.dimension = f.category;
                        this.filter.dimension = f.filter;
                        
                        if (!this.validation.dimensions.call(this)) {
							return;
						}
						
						if (!this.validation.categories.call(this)) {
							return;
						}
						
						this.validation.trendline.call(this);
						
						this.validation.targetline.call(this);
						
                        if (f.indicators) {
                            for (var i = 0; i < f.indicators.length; i++) {
                                indiment.push(f.indicators[i]);
                                this.indicatorIds.push(f.indicators[i].internalId);
                                irec.push({id: f.indicators[i].internalId, s: DV.util.string.getEncodedString(f.indicators[i].shortName), name: DV.util.string.getEncodedString(f.indicators[i].shortName), parent: null});
                            }
                        }                        
                        if (f.dataElements) {
                            for (var i = 0; i < f.dataElements.length; i++) {
                                indiment.push(f.dataElements[i]);
                                this.dataelementIds.push(f.dataElements[i].internalId);
                                drec.push({id: f.dataElements[i].internalId, s: DV.util.string.getEncodedString(f.dataElements[i].shortName), name: DV.util.string.getEncodedString(f.dataElements[i].shortName), parent: null});
                            }
                        }
                        for (var i = 0; i < indiment.length; i++) {
                            f.names.data.push(DV.util.string.getEncodedString(indiment[i].shortName));
                        }
                        
                        this.relativePeriods = f.relativePeriods;
                        f.names.period = DV.util.dimension.period.getNamesByRelativePeriodsObject(this.relativePeriods);
                        
                        for (var i = 0; i < f.organisationUnits.length; i++) {
                            this.organisationunitIds.push(f.organisationUnits[i].internalId);
                            f.names.organisationunit.push(DV.util.string.getEncodedString(f.organisationUnits[i].name));
                            DV.cmp.dimension.organisationunit.treepanel.storage[f.organisationUnits[i].internalId] = DV.util.string.getEncodedString(f.organisationUnits[i].name);
                        }
                        
                        this.series.names = f.names[this.series.dimension];
                        this.category.names = f.names[this.category.dimension];
                        this.filter.names = f.names[this.filter.dimension];
                        
						if (!this.validation.names.call(this)) {
							return;
						}
			
						this.validation.filter.call(this);
						
						this.validation.render.call(this);
						
						this.setUI(f, irec, drec);
                        
                        if (exe) {
                            DV.value.getValues(true);
                        }
                    }
                });
            }
        },
        setUI: function(f, irec, drec) {
			DV.util.button.type.setValue(f.type);
			
			DV.cmp.favorite.hidesubtitle.setValue(f.hideSubtitle);
			DV.cmp.favorite.hidelegend.setValue(f.hideLegend);
			DV.cmp.favorite.trendline.setValue(f.regression);
			DV.cmp.favorite.userorganisationunit.setValue(f.userOrganisationUnit);
			DV.cmp.favorite.domainaxislabel.setValue(f.domainAxisLabel);
			DV.cmp.favorite.rangeaxislabel.setValue(f.rangeAxisLabel);
			DV.cmp.favorite.targetlinevalue.setValue(f.targetLineValue);
			DV.cmp.favorite.targetlinelabel.xable();
			DV.cmp.favorite.targetlinelabel.setValue(f.targetLineLabel);

			DV.cmp.settings.series.setValue(DV.conf.finals.dimension[f.series].value);
			DV.util.combobox.filter.category();                        
			DV.cmp.settings.category.setValue(DV.conf.finals.dimension[f.category].value);
			DV.util.combobox.filter.filter();                        
			DV.cmp.settings.filter.setValue(DV.conf.finals.dimension[f.filter].value);
									
			DV.store.indicator.selected.removeAll();
			if (f.indicators) {
				DV.store.indicator.selected.add(irec);
				DV.util.store.addToStorage(DV.store.indicator.available, irec);
				DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
			}
									
			DV.store.dataelement.selected.removeAll();
			if (f.dataElements) {
				DV.store.dataelement.selected.add(drec);
				DV.util.store.addToStorage(DV.store.dataelement.available, drec);
				DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
			}

			DV.util.checkbox.setRelativePeriods(f.relativePeriods);
		},						
        resetState: function() {
            this.type = DV.conf.finals.chart.column;
            this.series.dimension = null;
            this.series.names = [];
            this.category.dimension = null;
            this.category.names = [];
            this.filter.dimension = null;
            this.filter.names = [];
            this.indicatorIds = [];
            this.dataelementIds = [];
            this.relativePeriods = {};
            this.organisationunitIds = [];
			this.hideSubtitle = false,
            this.hideLegend = false;
			this.trendLine = null;
			this.userOrganisationUnit = false;
			this.domainAxisLabel = null;
			this.rangeAxisLabel = null;
			this.targetLineValue = null;
			this.targetLineLabel = null;
        },
        validation: {
			dimensions: function() {
				if (!this.series.dimension || !this.category.dimension || !this.filter.dimension) {
					DV.util.notification.error(DV.i18n.et_invalid_dimension_setup, DV.i18n.em_invalid_dimension_setup);
					return false;
				}
				return true;
			},
			names: function() {            
				if (!this.series.names.length) {
					DV.util.notification.error(DV.i18n.et_no_indicators_dataelements, DV.i18n.em_no_indicators_dataelements);
					return false;
				}           
				if (!this.category.names.length) {
					DV.util.notification.error(DV.i18n.et_no_periods, DV.i18n.em_no_periods);
					return false;
				}           
				if (!this.filter.names.length) {
					DV.util.notification.error(DV.i18n.et_no_orgunits, DV.i18n.em_no_orgunits);
					return false;
				}
				return true;
			},
			filter: function() {
				if (this.filter.names.length > 1) {
					this.filter.names = this.filter.names.slice(0,1);
					DV.exe.warnings.push(DV.conf.finals.dimension[this.filter.dimension].warning.filter + ' ' + DV.i18n.wm_first_filter_used);
				}
			},
			categories: function() {
				if (this.category.names.length < 2 && (this.type === DV.conf.finals.chart.line || this.type === DV.conf.finals.chart.area)) {
					DV.util.notification.error(DV.i18n.et_line_area_categories, DV.i18n.em_line_area_categories);
					return false;
				}
				return true;
			},
			trendline: function() {
				if (this.trendLine) {
					var reasons = [];
					if (this.type === DV.conf.finals.chart.stackedcolumn || this.type === DV.conf.finals.chart.stackedbar || this.type === DV.conf.finals.chart.area) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_stacked_chart);
						this.trendLine = false;
					}
					else if (this.type === DV.conf.finals.chart.pie) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_pie_chart);
						this.trendLine = false;
					}
					
					if (this.category.names.length < 2) {
						reasons.push(DV.i18n.wm_required_categories);
						this.trendLine = false;
					}
					
					if (reasons.length) {
						var text = DV.i18n.wm_trendline_deactivated + ' (';
						for (var i = 0; i < reasons.length; i++) {
							text += i > 0 ? ' + ' : '';
							text += reasons[i];
						}
						text += ').';
						DV.exe.warnings.push(text);
					}
				}
			},
			targetline: function() {			
				if (this.targetLineValue) {
					var reasons = [];
					if (this.type === DV.conf.finals.chart.stackedcolumn || this.type === DV.conf.finals.chart.stackedbar || this.type === DV.conf.finals.chart.area) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_stacked_chart);
						this.targetLineValue = null;
					}
					else if (this.type === DV.conf.finals.chart.pie) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_pie_chart);
						this.targetLineValue = null;
					}
					
					if (this.category.names.length < 2) {
						reasons.push(DV.i18n.wm_required_categories);
						this.targetLineValue = null;
					}
					
					if (reasons.length) {
						var text = DV.i18n.wm_targetline_deactivated + ' (';
						for (var i = 0; i < reasons.length; i++) {
							text += i > 0 ? ' + ' : '';
							text += reasons[i];
						}
						text += ').';
						DV.exe.warnings.push(text);
					}
				}
			},
			render: function() {
				if (!this.isRendered) {
					DV.cmp.toolbar.datatable.enable();
					this.isRendered = true;
				}
			},
			response: function(r) {
				if (!r.responseText) {
					DV.util.mask.hideMask();
					DV.util.notification.error(DV.i18n.et_invalid_uid, DV.i18n.em_invalid_uid);
					return false;
				}
				return true;
			},
			favorite: function(f) {				
				if (!f.organisationUnits || !f.organisationUnits.length) {
					alert(DV.i18n.favorite_no_orgunits);
					return false;
				}
				return true;
			}
		}
    };
    
    DV.value = {
        values: [],
        getValues: function(exe) {
            DV.util.mask.showMask(DV.cmp.region.center, DV.i18n.loading);
            
            var params = [];
            params = params.concat(DV.util.dimension[DV.state.series.dimension].getUrl());
            params = params.concat(DV.util.dimension[DV.state.category.dimension].getUrl());
            params = params.concat(DV.util.dimension[DV.state.filter.dimension].getUrl(true));
            
            var baseurl = DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.data_get;
            Ext.Array.each(params, function(item) {
                baseurl = Ext.String.urlAppend(baseurl, item);
            });
            
            Ext.Ajax.request({
                url: baseurl,
                success: function(r) {                    
                    DV.value.values = DV.util.value.jsonfy(r);
                    
                    if (!DV.value.values.length) {
						DV.util.mask.hideMask();
                        DV.util.notification.error(DV.i18n.et_no_data, DV.i18n.em_no_data);
                        return;
                    }
                    
                    var storage = Ext.Object.merge(DV.store[DV.conf.finals.dimension.indicator.value].available.storage, DV.store[DV.conf.finals.dimension.dataelement.value].available.storage);
                    Ext.Array.each(DV.value.values, function(item) {
                        item[DV.conf.finals.dimension.data.value] = DV.util.string.getEncodedString(storage[item.d].name);
                        item[DV.conf.finals.dimension.period.value] = DV.util.string.getEncodedString(DV.util.dimension.period.getNameById(item.p));
                        item[DV.conf.finals.dimension.organisationunit.value] = DV.util.string.getEncodedString(DV.cmp.dimension.organisationunit.treepanel.findNameById(item.o));
                        item.v = parseFloat(item.v);
                    });
                    
                    if (exe) {
                        DV.chart.getData(true);
                    }
                    else {
                        return DV.value.values;
                    }
                }
            });
        }
    };
    
    DV.chart = {
        data: [],
        getData: function(exe) {
            this.data = [];
            
            Ext.Array.each(DV.state.category.names, function(item) {
                var obj = {};
                obj[DV.conf.finals.data.domain] = item;
                DV.chart.data.push(obj);
            });
            
            Ext.Array.each(DV.chart.data, function(item) {
                for (var i = 0; i < DV.state.series.names.length; i++) {
                    item[DV.state.series.names[i]] = 0;
                }
            });

            Ext.Array.each(DV.chart.data, function(item) {
                for (var i = 0; i < DV.state.series.names.length; i++) {
                    for (var j = 0; j < DV.value.values.length; j++) {
                        if (DV.value.values[j][DV.state.category.dimension] === item[DV.conf.finals.data.domain] && DV.value.values[j][DV.state.series.dimension] === DV.state.series.names[i]) {
                            item[DV.value.values[j][DV.state.series.dimension]] = DV.value.values[j].v;
                            break;
                        }
                    }
                }
            });
            
			if (DV.state.trendLine) {
				this.trendLine = [];
				for (var i = 0; i < DV.state.series.names.length; i++) {
					var s = DV.state.series.names[i],
						reg = new SimpleRegression();
					for (var j = 0; j < DV.chart.data.length; j++) {
						reg.addData(j, DV.chart.data[j][s]);
					}
					var key = DV.conf.finals.data.trendline + s;
					for (var j = 0; j < DV.chart.data.length; j++) {
						var n = reg.predict(j);
						DV.chart.data[j][key] = parseFloat(reg.predict(j).toFixed(1));
					}
					this.trendLine.push({
						key: key,
						name: DV.i18n.trend + ' (' + s + ')'
					});
				}
			}

			if (DV.state.targetLineValue) {
				Ext.Array.each(DV.chart.data, function(item) {
					item[DV.conf.finals.data.targetline] = DV.state.targetLineValue;
				});
			}
            
            if (exe) {
                DV.store.getChartStore(true);
            }
            else {
                return this.data;
            }
        },
        chart: null,
        getChart: function(exe) {
            this[DV.state.type]();
            if (exe) {
                this.reload();
            }
            else {
                return this.chart;
            }
        },
        column: function(stacked) {
			var series = [];
			if (DV.state.trendLine) {
				var a = DV.util.chart.default.series.getTrendLineArray();
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}
			series.push({
				type: 'column',
				axis: 'left',
				xField: DV.conf.finals.data.domain,
				yField: DV.state.series.names,
				stacked: stacked,
				style: {
					opacity: 0.8,
					stroke: '#333'
				},				
				tips: DV.util.chart.default.series.getTips()
			});
			if (DV.state.targetLineValue) {
				series.push(DV.util.chart.default.series.getTargetLine());
			}
			
			var axes = [];
			var numeric = DV.util.chart.default.axis.getNumeric(stacked);
			axes.push(numeric);
			axes.push(DV.util.chart.default.axis.getCategory());
			
			DV.util.chart.default.series.setTheme();
			this.chart = DV.util.chart.default.getChart(axes, series);
        },
        stackedcolumn: function() {
            this.column(true);
        },
        bar: function(stacked) {
			var series = [];
			if (DV.state.trendLine) {
				var a = DV.util.chart.bar.series.getTrendLineArray();
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}
			series.push({
				type: 'bar',
				axis: 'bottom',
				xField: DV.conf.finals.data.domain,
				yField: DV.state.series.names,
				stacked: stacked,
				style: {
					opacity: 0.8,
					stroke: '#333'
				},
				tips: DV.util.chart.default.series.getTips()
			});
			if (DV.state.targetLineValue) {
				series.push(DV.util.chart.bar.series.getTargetLine());
			}
			
			var axes = [];
			var numeric = DV.util.chart.bar.axis.getNumeric(stacked);
			axes.push(numeric);
			axes.push(DV.util.chart.bar.axis.getCategory());
			
			DV.util.chart.default.series.setTheme();			
			this.chart = DV.util.chart.default.getChart(axes, series);
        },
        stackedbar: function() {
            this.bar(true);
        },
        line: function() {
			var series = [];
			if (DV.state.trendLine) {
				var a = DV.util.chart.default.series.getTrendLineArray();
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}
			series = series.concat(DV.util.chart.line.series.getArray());
			if (DV.state.targetLineValue) {
				series.push(DV.util.chart.default.series.getTargetLine());
			}
			
			var axes = [];
			var numeric = DV.util.chart.default.axis.getNumeric();
			axes.push(numeric);
			axes.push(DV.util.chart.default.axis.getCategory());
			
			DV.util.chart.line.series.setTheme();
			this.chart = DV.util.chart.default.getChart(axes, series);
        },
        area: function() {
			var series = [];
			series.push({
				type: 'area',
				axis: 'left',
				xField: DV.conf.finals.data.domain,
				yField: DV.state.series.names,
				style: {
					opacity: 0.65,
					stroke: '#555'
				}
			});
			
			var axes = [];
			var numeric = DV.util.chart.default.axis.getNumeric();
			axes.push(numeric);
			axes.push(DV.util.chart.default.axis.getCategory());
			
			DV.util.chart.line.series.setTheme();
			this.chart = DV.util.chart.default.getChart(axes, series);
        },
        pie: function() {
			DV.util.chart.pie.series.setTheme();
            this.chart = Ext.create('Ext.chart.Chart', {
                animate: true,
                shadow: true,
                store: DV.store.chart,
                insetPadding: 60,
                items: DV.state.hideSubtitle ? false : DV.util.chart.pie.getTitle(),
                legend: DV.state.hideLegend ? false : DV.util.chart.default.getLegend(DV.state.category.names.length),
                series: [{
                    type: 'pie',
                    field: DV.state.series.names[0],
                    showInLegend: true,
                    label: {
                        field: DV.conf.finals.data.domain
                    },
                    highlight: {
                        segment: {
                            margin: 10
                        }
                    },
                    style: {
                        opacity: 0.9,
						stroke: '#555'
                    },
                    tips: DV.util.chart.pie.series.getTips()
                }],
                theme: 'dv1'
            });
        },
        reload: function() {
			DV.state.getOptions();
            DV.cmp.region.center.removeAll(true);
            DV.cmp.region.center.add(this.chart);            
            DV.exe.finalize();
            
            if (DV.init.cmd !== DV.conf.finals.cmd.init) {
                DV.store.getDataTableStore(true);
            }
        }
    };
    
    DV.datatable = {
        datatable: null,
        getDataTable: function(exe) {
            this.datatable = Ext.create('Ext.grid.Panel', {
                height: DV.util.viewport.getSize().y - 1,
                scroll: 'vertical',
                cls: 'dv-datatable',
                columns: [
                    {
                        text: DV.conf.finals.dimension.data.rawvalue,
                        dataIndex: DV.conf.finals.dimension.data.value,
                        width: 150,
                        height: DV.conf.layout.east_gridcolumn_height
                    },
                    {
                        text: DV.conf.finals.dimension.period.rawvalue,
                        dataIndex: DV.conf.finals.dimension.period.value,
                        width: 100,
                        height: DV.conf.layout.east_gridcolumn_height,
                        sortable: false
                    },
                    {
                        text: DV.conf.finals.dimension.organisationunit.rawvalue,
                        dataIndex: DV.conf.finals.dimension.organisationunit.value,
                        width: 150,
                        height: DV.conf.layout.east_gridcolumn_height
                    },
                    {
                        text: DV.i18n.value,
                        dataIndex: 'v',
                        width: 80,
                        height: DV.conf.layout.east_gridcolumn_height
                    }
                ],
                store: DV.store.datatable
            });
            
            if (exe) {
                this.reload();
            }
            else {
                return this.datatable;
            }
        },
        reload: function() {
            DV.cmp.region.east.removeAll(true);
            DV.cmp.region.east.add(this.datatable);
        }            
    };
    
    DV.exe = {
        execute: function(exe, cmd) {
			DV.exe.warnings = [];			
            if (cmd) {
                if (cmd === DV.conf.finals.cmd.init) {
                    DV.chart.getData(exe);
                }
                else if (cmd === DV.conf.finals.cmd.datatable) {
					DV.store.getDataTableStore(exe);
				}					
                else {
					DV.state.resetState();
                    DV.state.setFavorite(true, cmd);
                }
            }
            else {
				DV.state.resetState();
                DV.state.getState(exe);
            }
        },
        finalize: function() {
			DV.init.cmd = false;
			DV.util.mask.hideMask();
            
            if (DV.exe.warnings.length) {
				DV.util.notification.warning(DV.exe.getWarnings());
			}
			else {
				DV.util.notification.ok();
			}
        },
        warnings: [],
        getWarnings: function() {
			var t = '';
			for (var i = 0; i < this.warnings.length; i++) {
				if (i > 0) {
					t += '<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.warning + '" style="padding:0 5px 0 8px" />';
				}
				t += this.warnings[i] + ' ';
			}
			return t;
		}				
    };
        
    DV.viewport = Ext.create('Ext.container.Viewport', {
        layout: 'border',
        renderTo: Ext.getBody(),
        items: [
            {
                region: 'west',
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
                items: [
                    {
                        xtype: 'toolbar',
                        height: 45,
                        style: 'padding-top:1px; border-style:none',
                        defaults: {
                            height: 40,
                            toggleGroup: 'chartsettings',
                            handler: DV.util.button.type.toggleHandler,
                            listeners: {
                                afterrender: function(b) {
                                    if (b.xtype === 'button') {
                                        DV.cmp.charttype.push(b);
                                    }
                                }
                            }
                        },
                        items: [
                            {
                                xtype: 'label',
                                text: DV.i18n.chart_type,
                                style: 'font-size:11px; font-weight:bold; padding:13px 8px 0 10px'
                            },
                            {
								xtype: 'button',
                                icon: 'images/column.png',
                                name: DV.conf.finals.chart.column,
                                tooltip: DV.i18n.column_chart,
								width: 40,
                                pressed: true
                            },
                            {
								xtype: 'button',
                                icon: 'images/column-stacked.png',
                                name: DV.conf.finals.chart.stackedcolumn,
                                tooltip: DV.i18n.stacked_column_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/bar.png',
                                name: DV.conf.finals.chart.bar,
                                tooltip: DV.i18n.bar_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/bar-stacked.png',
                                name: DV.conf.finals.chart.stackedbar,
                                tooltip: DV.i18n.stacked_bar_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/line.png',
                                name: DV.conf.finals.chart.line,
                                tooltip: DV.i18n.line_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/area.png',
                                name: DV.conf.finals.chart.area,
                                tooltip: DV.i18n.area_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/pie.png',
                                name: DV.conf.finals.chart.pie,
                                tooltip: DV.i18n.pie_chart,
								width: 40
                            }
                        ]
                    },
                    {
                        xtype: 'toolbar',
                        id: 'chartsettings_tb',
                        height: 48,
                        items: [
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: DV.i18n.series,
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.series,
                                        emptyText: DV.i18n.series,
                                        queryMode: 'local',
                                        editable: false,
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.data.value,
                                        listeners: {
                                            afterrender: function() {
                                                DV.cmp.settings.series = this;
                                            },
                                            select: function() {
                                                DV.util.combobox.filter.category();
                                            }
                                        }
                                    }
                                ]
                            },                            
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: DV.i18n.category,
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.category,
                                        emptyText: DV.i18n.category,
                                        queryMode: 'local',
                                        editable: false,
                                        lastQuery: '',
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.period.value,
                                        listeners: {
                                            afterrender: function(cb) {
                                                DV.cmp.settings.category = this;
                                            },
                                            select: function(cb) {
                                                DV.util.combobox.filter.filter();
                                            }
                                        }
                                    }
                                ]
                            },                            
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Filter',
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.filter,
                                        emptyText: DV.i18n.filter,
                                        queryMode: 'local',
                                        editable: false,
                                        lastQuery: '',
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.organisationunit.value,
                                        listeners: {
                                            afterrender: function(cb) {
                                                DV.cmp.settings.filter = this;
                                            }
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        bodyStyle: 'border-style:none; border-top:2px groove #eee; padding:10px 10px 0 10px;',
                        items: [
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
                                name: DV.conf.finals.dimension.indicator.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleIndicator();" class="dv-fieldset-title-link">' + DV.i18n.indicators + '</a>',
                                collapsible: true,
                                width: DV.conf.layout.west_fieldset_width,
                                items: [
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        style: 'margin-bottom:8px',
                                        width: DV.conf.layout.west_fieldset_width - 22,
                                        valueField: 'id',
                                        displayField: 'name',
                                        fieldLabel: DV.i18n.select_group,
                                        labelStyle: 'padding-left:7px;',
                                        labelWidth: 90,
                                        editable: false,
                                        queryMode: 'remote',
                                        store: Ext.create('Ext.data.Store', {
                                            fields: ['id', 'name', 'index'],
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.indicatorgroup_get,
                                                reader: {
                                                    type: 'json',
                                                    root: 'indicatorGroups'
                                                }
                                            },
                                            listeners: {
                                                load: function(s) {
                                                    s.add({id: 0, name: DV.i18n.all_indicator_groups, index: -1});
                                                    s.sort('index', 'ASC');
                                                }
                                            }
                                        }),
                                        listeners: {
                                            select: function(cb) {
                                                var store = DV.store.indicator.available;
                                                store.parent = cb.getValue();
                                                
                                                if (DV.util.store.containsParent(store)) {
                                                    DV.util.store.loadFromStorage(store);
                                                    DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                }
                                                else {
                                                    store.load({params: {id: cb.getValue()}});
                                                }
                                            }
                                        }
                                    },                                    
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            {
                                                xtype: 'multiselect',
                                                id: 'availableIndicators',
                                                name: 'availableIndicators',
                                                cls: 'dv-toolbar-multiselect-left',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
                                                valueField: 'id',
                                                queryMode: 'remote',
                                                store: DV.store.indicator.available,
                                                tbar: [
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.available,
                                                        cls: 'dv-toolbar-multiselect-left-label'
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowright.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowrightdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                        }
                                                    },
                                                    ' '
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.indicator.available = this;
                                                    },
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.select(this, DV.cmp.dimension.indicator.selected);
                                                        }, this);
                                                    }
                                                }
                                            },                                            
                                            {
                                                xtype: 'multiselect',
                                                id: 'selectedIndicators',
                                                name: 'selectedIndicators',
                                                cls: 'dv-toolbar-multiselect-right',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
                                                valueField: 'id',
                                                ddReorder: true,
                                                queryMode: 'local',
                                                store: DV.store.indicator.selected,
                                                tbar: [
                                                    ' ',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleftdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselectAll(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleft.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                        }
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.selected,
                                                        cls: 'dv-toolbar-multiselect-right-label'
                                                    }
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.indicator.selected = this;
                                                    },
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.indicator.available, this);
                                                        }, this);
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.indicator = this;
                                    },
                                    expand: function() {
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.dataelement, DV.cmp.fieldset.dataset, DV.cmp.fieldset.period, DV.cmp.fieldset.organisationunit]);
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
                                name: DV.conf.finals.dimension.dataelement.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleDataElement();" class="dv-fieldset-title-link">' + DV.i18n.data_elements + '</a>',
                                collapsed: true,
                                collapsible: true,
                                items: [
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        style: 'margin-bottom:8px',
                                        width: DV.conf.layout.west_fieldset_width - 22,
                                        valueField: 'id',
                                        displayField: 'name',
                                        fieldLabel: 'Select group',
                                        labelStyle: 'padding-left:7px;',
                                        labelWidth: 90,
                                        editable: false,
                                        queryMode: 'remote',
                                        store: Ext.create('Ext.data.Store', {
                                            fields: ['id', 'name', 'index'],
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.dataelementgroup_get,
                                                reader: {
                                                    type: 'json',
                                                    root: 'dataElementGroups'
                                                }
                                            },
                                            listeners: {
                                                load: function(s) {
                                                    s.add({id: 0, name: '[ All data element groups ]', index: -1});
                                                    s.sort('index', 'ASC');
                                                }
                                            }
                                        }),
                                        listeners: {
                                            select: function(cb) {
                                                var store = DV.store.dataelement.available;
                                                store.parent = cb.getValue();
                                                
                                                if (DV.util.store.containsParent(store)) {
                                                    DV.util.store.loadFromStorage(store);
                                                    DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                }
                                                else {
                                                    store.load({params: {id: cb.getValue()}});
                                                }
                                            }
                                        }
                                    },                                    
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            Ext.create('Ext.ux.form.MultiSelect', {
                                                id: 'availableDataElements',
                                                name: 'availableDataElements',
                                                cls: 'dv-toolbar-multiselect-left',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
                                                valueField: 'id',
                                                queryMode: 'remote',
                                                store: DV.store.dataelement.available,
                                                tbar: [
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.available,
                                                        cls: 'dv-toolbar-multiselect-left-label'
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowright.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowrightdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                        }
                                                    },
                                                    ' '
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.dataelement.available = this;
                                                    },                                                                
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.select(this, DV.cmp.dimension.dataelement.selected);
                                                        }, this);
                                                    }
                                                }
                                            }),                                            
                                            {
                                                xtype: 'multiselect',
                                                id: 'selectedDataElements',
                                                name: 'selectedDataElements',
                                                cls: 'dv-toolbar-multiselect-right',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
                                                valueField: 'id',
                                                ddReorder: true,
                                                queryMode: 'remote',
                                                store: DV.store.dataelement.selected,
                                                tbar: [
                                                    ' ',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleftdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselectAll(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleft.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                        }
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.selected,
                                                        cls: 'dv-toolbar-multiselect-right-label'
                                                    }
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.dataelement.selected = this;
                                                    },          
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.dataelement.available, this);
                                                        }, this);
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.dataelement = this;
                                    },
                                    expand: function() {
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.indicator, DV.cmp.fieldset.dataset, DV.cmp.fieldset.period, DV.cmp.fieldset.organisationunit]);
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
                                name: DV.conf.finals.dimension.dataset.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleDataSet();" class="dv-fieldset-title-link">' + DV.i18n.datasets + '</a>',
                                collapsed: true,
                                collapsible: true,
                                items: [
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            Ext.create('Ext.ux.form.MultiSelect', {
                                                id: 'availableDataSets',
                                                name: 'availableDataSets',
                                                cls: 'dv-toolbar-multiselect-left',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
                                                valueField: 'id',
                                                queryMode: 'remote',
                                                store: DV.store.dataset.available,
                                                tbar: [
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.available,
                                                        cls: 'dv-toolbar-multiselect-left-label'
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowright.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowrightdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
                                                        }
                                                    },
                                                    ' '
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.dataset.available = this;
                                                    },                                                                
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.select(this, DV.cmp.dimension.dataset.selected);
                                                        }, this);
                                                    }
                                                }
                                            }),                                            
                                            {
                                                xtype: 'multiselect',
                                                id: 'selectedDataSets',
                                                name: 'selectedDataSets',
                                                cls: 'dv-toolbar-multiselect-right',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
                                                valueField: 'id',
                                                ddReorder: true,
                                                queryMode: 'remote',
                                                store: DV.store.dataset.selected,
                                                tbar: [
                                                    ' ',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleftdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselectAll(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleft.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
                                                        }
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.selected,
                                                        cls: 'dv-toolbar-multiselect-right-label'
                                                    }
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.dataset.selected = this;
                                                    },          
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.dataset.available, this);
                                                        }, this);
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.dataset = this;
                                    },
                                    expand: function() {
										if (!DV.store.dataset.available.isLoaded) {
											DV.store.dataset.available.load();
										}
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.indicator, DV.cmp.fieldset.dataelement, DV.cmp.fieldset.period, DV.cmp.fieldset.organisationunit]);
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
                                name: DV.conf.finals.dimension.period.value,
                                title: '<a href="javascript:DV.util.fieldset.togglePeriod();" class="dv-fieldset-title-link">' + DV.i18n.periods +'</a>',
                                collapsed: true,
                                collapsible: true,
                                cmp: [],
                                items: [
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding:0 0 0 10px',
                                                defaults: {
                                                    labelSeparator: '',
                                                    listeners: {
                                                        added: function(chb) {
                                                            if (chb.xtype === 'checkbox') {
                                                                DV.cmp.dimension.period.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.months,
                                                        cls: 'dv-label-period-heading'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastMonth',
                                                        boxLabel: DV.i18n.last_month
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'last12Months',
                                                        boxLabel: DV.i18n.last_12_months,
                                                        checked: true
                                                    }
                                                ]
                                            },
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding:0 0 0 32px',
                                                defaults: {
                                                    labelSeparator: '',
                                                    listeners: {
                                                        added: function(chb) {
                                                            if (chb.xtype === 'checkbox') {
                                                                DV.cmp.dimension.period.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.quarters,
                                                        cls: 'dv-label-period-heading'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastQuarter',
                                                        boxLabel: DV.i18n.last_quarter
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'last4Quarters',
                                                        boxLabel: DV.i18n.last_4_quarters
                                                    }
                                                ]
                                            },
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding:0 0 0 32px',
                                                defaults: {
                                                    labelSeparator: '',
                                                    listeners: {
                                                        added: function(chb) {
                                                            if (chb.xtype === 'checkbox') {
                                                                DV.cmp.dimension.period.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.six_months,
                                                        cls: 'dv-label-period-heading'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastSixMonth',
                                                        boxLabel: DV.i18n.last_six_month
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'last2SixMonths',
                                                        boxLabel: DV.i18n.last_two_six_month
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding:5px 0 0 10px',
                                                defaults: {
                                                    labelSeparator: '',
                                                    listeners: {
                                                        added: function(chb) {
                                                            if (chb.xtype === 'checkbox') {
                                                                DV.cmp.dimension.period.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: DV.i18n.years,
                                                        cls: 'dv-label-period-heading'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'thisYear',
                                                        boxLabel: DV.i18n.this_year
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastYear',
                                                        boxLabel: DV.i18n.last_year
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'last5Years',
                                                        boxLabel: DV.i18n.last_5_years
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.period = this;
                                    },
                                    expand: function() {
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.indicator, DV.cmp.fieldset.dataelement, DV.cmp.fieldset.dataset, DV.cmp.fieldset.organisationunit]);
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
                                name: DV.conf.finals.dimension.organisationunit.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleOrganisationUnit();" class="dv-fieldset-title-link">' + DV.i18n.organisation_units + '</a>',
                                collapsed: true,
                                collapsible: true,
                                items: [
                                    {
                                        xtype: 'treepanel',
                                        cls: 'dv-tree',
                                        height: 260,
                                        width: DV.conf.layout.west_fieldset_width - 22,
                                        autoScroll: true,
                                        multiSelect: true,
                                        isRendered: false,
                                        storage: {},
                                        selectRoot: function() {
                                            if (this.isRendered) {
                                                if (!this.getSelectionModel().getSelection().length) {
                                                    this.getSelectionModel().select(this.getRootNode());
                                                }
                                            }
                                        },
                                        findNameById: function(id) {
                                            var n = this.store.getNodeById(id) ? this.store.getNodeById(id).data.text : null;                                            
                                            if (!n) {
                                                for (var k in this.storage) {
                                                    if (k === id) {
                                                        n = this.storage[k];
                                                    }
                                                }
                                            }
                                            return n;
                                        },
                                        store: Ext.create('Ext.data.TreeStore', {
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.organisationunitchildren_get
                                            },
                                            root: {
                                                id: DV.init.system.rootNode.id,
                                                text: DV.init.system.rootNode.name,
                                                expanded: false
                                            }
                                        }),
                                        listeners: {
                                            added: function() {
                                                DV.cmp.dimension.organisationunit.treepanel = this;
                                            },
                                            itemcontextmenu: function(v, r, h, i, e) {
                                                if (v.menu) {
                                                    v.menu.destroy();
                                                }
                                                v.menu = Ext.create('Ext.menu.Menu', {
                                                    id: 'treepanel-contextmenu',
                                                    showSeparator: false
                                                });
                                                if (!r.data.leaf) {
                                                    v.menu.add({
                                                        id: 'treepanel-contextmenu-item',
                                                        text: DV.i18n.select_all_children,
                                                        icon: 'images/node-select-child.png',
                                                        handler: function() {
                                                            r.expand(false, function() {
                                                                v.getSelectionModel().select(r.childNodes, true);
                                                                v.getSelectionModel().deselect(r);
                                                            });
                                                        }
                                                    });
                                                }
                                                else {
                                                    return;
                                                }
                                                
                                                v.menu.showAt(e.xy);
                                            }
                                        }
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.organisationunit = this;
                                    },
                                    collapse: function(fs) {
										if (DV.cmp.fieldset.organisationunit) {
											DV.cmp.fieldset.organisationunit.setHeight(25);
										}
									},
                                    expand: function(fs) {
										var h = DV.cmp.region.west.getHeight() - DV.conf.layout.west_fill_height;
										DV.cmp.fieldset.organisationunit.setHeight(h);
										DV.cmp.dimension.organisationunit.treepanel.setHeight(h - 40);

                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.indicator, DV.cmp.fieldset.dataelement, DV.cmp.fieldset.dataset, DV.cmp.fieldset.period]);
                                        var tp = DV.cmp.dimension.organisationunit.treepanel;
                                        if (!tp.isRendered) {
                                            tp.isRendered = true;
                                            tp.getRootNode().expand();
                                            tp.selectRoot();
                                        }
                                    }
                                }
                            }
                        ]
                    },
					{
						xtype: 'toolbar',
						id: 'chartoptions_tb',
						layout: 'fit',
						items: [
							{
								xtype: 'panel',
								bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
								items: [
									{
										bodyStyle: 'border-style:none; background-color:transparent; padding:0 0 10px 3px; font-size:11px; font-weight:bold',
										html: DV.i18n.chart_options
									},
									{
										xtype: 'panel',
										layout: 'column',
										bodyStyle: 'border-style:none; background-color:transparent; padding-bottom:15px',
										items: [
											{
												xtype: 'checkbox',
												cls: 'dv-checkbox-alt1',
												style: 'margin-right:26px',
												boxLabel: DV.i18n.hide_subtitle,
												labelWidth: DV.conf.layout.form_label_width,
												listeners: {
													added: function() {
														DV.cmp.favorite.hidesubtitle = this;
													}
												}
											},
											{
												xtype: 'checkbox',
												cls: 'dv-checkbox-alt1',
												style: 'margin-right:25px',
												boxLabel: DV.i18n.hide_legend,
												labelWidth: DV.conf.layout.form_label_width,
												listeners: {
													added: function() {
														DV.cmp.favorite.hidelegend = this;
													}
												}
											},
											{
												xtype: 'checkbox',
												cls: 'dv-checkbox-alt1',
												style: 'margin-right:26px',
												boxLabel: DV.i18n.trend_line,
												labelWidth: DV.conf.layout.form_label_width,
												listeners: {
													added: function() {
														DV.cmp.favorite.trendline = this;
													}
												}
											},
											{
												xtype: 'checkbox',
												cls: 'dv-checkbox-alt1',
												boxLabel: DV.i18n.user_orgunit,
												labelWidth: DV.conf.layout.form_label_width,
												listeners: {
													added: function() {
														DV.cmp.favorite.userorganisationunit = this;
													}
												}
											}
										]
									},
									{
										xtype: 'panel',
										layout: 'column',
										bodyStyle: 'border:0 none; background-color:transparent; padding-bottom:8px',
										items: [
											{
												xtype: 'textfield',
												cls: 'dv-textfield-alt1',
												style: 'margin-right:4px',
												fieldLabel: DV.i18n.domain_axis_label,
												labelAlign: 'top',
												labelSeparator: '',
												maxLength: 100,
												enforceMaxLength: true,
												labelWidth: DV.conf.layout.form_label_width,
												width: 199,
												listeners: {
													added: function() {
														DV.cmp.favorite.domainaxislabel = this;
													}
												}
											},
											{
												xtype: 'textfield',
												cls: 'dv-textfield-alt1',
												fieldLabel: DV.i18n.range_axis_label,
												labelAlign: 'top',
												labelSeparator: '',
												maxLength: 100,
												enforceMaxLength: true,
												labelWidth: DV.conf.layout.form_label_width,
												width: 199,
												listeners: {
													added: function() {
														DV.cmp.favorite.rangeaxislabel = this;
													}
												}
											}
										]
									},
									{
										xtype: 'panel',
										layout: 'column',
										bodyStyle: 'border:0 none; background-color:transparent; padding-bottom:5px',
										items: [
											{
												xtype: 'numberfield',
												cls: 'dv-textfield-alt1',
												style: 'margin-right:5px',
												hideTrigger: true,
												fieldLabel: DV.i18n.target_line_value,
												labelAlign: 'top',
												labelSeparator: '',
												maxLength: 100,
												enforceMaxLength: true,
												width: 199,
												spinUpEnabled: true,
												spinDownEnabled: true,
												listeners: {
													added: function() {
														DV.cmp.favorite.targetlinevalue = this;
													},
													change: function() {
														DV.cmp.favorite.targetlinelabel.xable();
													}
												}
											},
											{
												xtype: 'textfield',
												cls: 'dv-textfield-alt1',
												fieldLabel: DV.i18n.target_line_label,
												labelAlign: 'top',
												labelSeparator: '',
												maxLength: 100,
												enforceMaxLength: true,
												width: 199,
												disabled: true,
												xable: function() {
													if (DV.cmp.favorite.targetlinevalue.getValue()) {
														this.enable();
													}
													else {
														this.disable();
													}
												},
												listeners: {
													added: function() {
														DV.cmp.favorite.targetlinelabel = this;
													}
												}
											}
										]
									}
								]
							}
						]
					}
				],				
                listeners: {
                    afterrender: function() {
                        DV.cmp.region.west = this;
                    },
                    collapse: function() {                    
                        this.collapsed = true;
                        DV.cmp.toolbar.resizewest.setText('>>>');
                    },
                    expand: function() {
                        this.collapsed = false;
                        DV.cmp.toolbar.resizewest.setText('<<<');
                    }
                }
            },
            {
                id: 'center',
                region: 'center',
                layout: 'fit',
                bodyStyle: 'padding:10px',
                tbar: {
                    xtype: 'toolbar',
                    cls: 'dv-toolbar',
                    height: DV.conf.layout.center_tbar_height,
                    defaults: {
                        height: 26
                    },
                    items: [
                        {
                            xtype: 'button',
                            name: 'resizewest',
							cls: 'dv-toolbar-btn-2',
                            text: '<<<',
                            tooltip: DV.i18n.show_hide_chart_settings,
                            handler: function() {
                                var p = DV.cmp.region.west;
                                if (p.collapsed) {
                                    p.expand();
                                }
                                else {
                                    p.collapse();
                                }
                            },
                            listeners: {
                                added: function() {
                                    DV.cmp.toolbar.resizewest = this;
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-1',
                            text: DV.i18n.update,
                            handler: function() {
                                DV.exe.execute(true, DV.init.cmd);
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: DV.i18n.favorites + '..',
                            listeners: {
                                afterrender: function(b) {
                                    this.menu = Ext.create('Ext.menu.Menu', {
                                        margin: '2 0 0 0',
                                        shadow: false,
                                        showSeparator: false,
                                        items: [
                                            {
                                                text: DV.i18n.manage_favorites,
                                                iconCls: 'dv-menu-item-edit',
                                                handler: function() {
                                                    if (DV.cmp.favorite.window) {
                                                        DV.cmp.favorite.window.show();
                                                    }
                                                    else {
                                                        DV.cmp.favorite.window = Ext.create('Ext.window.Window', {
                                                            title: DV.i18n.manage_favorites,
                                                            iconCls: 'dv-window-title-favorite',
                                                            bodyStyle: 'padding:8px; background-color:#fff',
															width: DV.conf.layout.grid_favorite_width,
                                                            closeAction: 'hide',
                                                            resizable: false,
                                                            modal: true,
                                                            resetForm: function() {
                                                                DV.cmp.favorite.name.setValue('');
                                                                DV.cmp.favorite.system.setValue(false);
                                                            },
                                                            items: [
                                                                {
                                                                    xtype: 'form',
                                                                    bodyStyle: 'border-style:none',
                                                                    items: [
                                                                        {
                                                                            xtype: 'textfield',
                                                                            cls: 'dv-textfield',
                                                                            fieldLabel: 'Name',
                                                                            maxLength: 160,
                                                                            enforceMaxLength: true,
                                                                            labelWidth: DV.conf.layout.form_label_width,
                                                                            width: DV.conf.layout.grid_favorite_width - 28,
                                                                            listeners: {
                                                                                added: function() {
                                                                                    DV.cmp.favorite.name = this;
                                                                                },
                                                                                change: function() {
                                                                                    DV.cmp.favorite.system.check();
                                                                                    DV.cmp.favorite.save.xable();
                                                                                }
                                                                            }
                                                                        },
                                                                        {
                                                                            xtype: 'checkbox',
                                                                            cls: 'dv-checkbox',
                                                                            style: 'padding-bottom:2px',
                                                                            fieldLabel: DV.i18n.system,
                                                                            labelWidth: DV.conf.layout.form_label_width,
                                                                            disabled: !DV.init.system.user.isAdmin,
                                                                            check: function() {
                                                                                if (!DV.init.system.user.isAdmin) {
                                                                                    if (DV.store.favorite.findExact('name', DV.cmp.favorite.name.getValue()) === -1) {
                                                                                        this.setValue(false);
                                                                                    }
                                                                                }
                                                                            },
                                                                            listeners: {
                                                                                added: function() {
                                                                                    DV.cmp.favorite.system = this;
                                                                                }
                                                                            }
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    xtype: 'grid',
                                                                    width: DV.conf.layout.grid_favorite_width - 28,
                                                                    scroll: 'vertical',
                                                                    multiSelect: true,
                                                                    columns: [
                                                                        {
                                                                            dataIndex: 'name',
                                                                            width: DV.conf.layout.grid_favorite_width - 139,
                                                                            style: 'display:none'
                                                                        },
                                                                        {
                                                                            dataIndex: 'lastUpdated',
                                                                            width: 111,
                                                                            style: 'display:none'
                                                                        }
                                                                    ],
                                                                    setHeightInWindow: function(store) {
                                                                        var h = (store.getCount() * 23) + 30,
                                                                            sh = DV.util.viewport.getSize().y * 0.6;
                                                                        this.setHeight(h > sh ? sh : h);
                                                                        this.doLayout();
                                                                        this.up('window').doLayout();
                                                                    },
                                                                    store: DV.store.favorite,
                                                                    tbar: {
                                                                        id: 'favorite_t',
                                                                        cls: 'dv-toolbar',
                                                                        height: 30,
                                                                        defaults: {
                                                                            height: 24
                                                                        },
                                                                        items: [
                                                                            {
                                                                                text: DV.i18n.sort_by + '..',
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.sortby = this;
                                                                                    },
                                                                                    afterrender: function(b) {
                                                                                        this.addCls('dv-menu-togglegroup');
                                                                                        this.menu = Ext.create('Ext.menu.Menu', {
                                                                                            shadowOffset: 1,
                                                                                            showSeparator: false,
                                                                                            width: 109,
                                                                                            height: 70,
                                                                                            items: [
                                                                                                {
                                                                                                    xtype: 'radiogroup',
                                                                                                    cls: 'dv-radiogroup',
                                                                                                    columns: 1,
                                                                                                    vertical: true,
                                                                                                    items: [
                                                                                                        {
                                                                                                            boxLabel: DV.i18n.name,
                                                                                                            name: 'sortby',
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'name';
                                                                                                                    store.sorting.direction = 'ASC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        },
                                                                                                        {
                                                                                                            boxLabel: DV.i18n.system,
                                                                                                            name: 'sortby',
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'userId';
                                                                                                                    store.sorting.direction = 'ASC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        },
                                                                                                        {
                                                                                                            boxLabel:  DV.i18n.last_updated,
                                                                                                            name: 'sortby',
                                                                                                            checked: true,
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'lastUpdated';
                                                                                                                    store.sorting.direction = 'DESC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    ]
                                                                                                }
                                                                                            ]
                                                                                        });
                                                                                    }
                                                                                }
                                                                            },
                                                                            '->',
                                                                            {
                                                                                text: DV.i18n.rename,
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                disabled: true,
                                                                                xable: function() {
                                                                                    if (DV.cmp.favorite.grid.getSelectionModel().getSelection().length == 1) {
                                                                                        DV.cmp.favorite.rename.button.enable();
                                                                                    }
                                                                                    else {
                                                                                        DV.cmp.favorite.rename.button.disable();
                                                                                    }
                                                                                },
                                                                                handler: function() {
                                                                                    var selected = DV.cmp.favorite.grid.getSelectionModel().getSelection()[0];
                                                                                    var w = Ext.create('Ext.window.Window', {
                                                                                        title: DV.i18n.rename_favorite,
                                                                                        layout: 'fit',
                                                                                        width: DV.conf.layout.window_confirm_width,
                                                                                        bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                        modal: true,
                                                                                        cmp: {},
                                                                                        items: [
                                                                                            {
                                                                                                xtype: 'textfield',
                                                                                                cls: 'dv-textfield',
                                                                                                maxLength: 160,
                                                                                                enforceMaxLength: true,
                                                                                                value: selected.data.name,
                                                                                                listeners: {
                                                                                                    added: function() {
                                                                                                        this.up('window').cmp.name = this;
                                                                                                    },
                                                                                                    change: function() {
                                                                                                        this.up('window').cmp.rename.xable();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        ],
                                                                                        bbar: [
																							{
																								xtype: 'label',
																								style: 'padding-left:2px; line-height:22px; font-size:10px; color:#666; width:50%',
																								listeners: {
																									added: function() {
																										DV.cmp.favorite.rename.label = this;
																									}
																								}
																							},
																							'->',
                                                                                            {
                                                                                                text: DV.i18n.cancel,
                                                                                                handler: function() {
                                                                                                    this.up('window').close();
                                                                                                }
                                                                                            },
                                                                                            {
                                                                                                text: DV.i18n.rename,
                                                                                                disabled: true,
                                                                                                xable: function() {
                                                                                                    var value = this.up('window').cmp.name.getValue();
                                                                                                    if (value) {
																										if (DV.store.favorite.findExact('name', value) == -1) {
																											this.enable();
																											DV.cmp.favorite.rename.label.setText('');
																											return;
																										}
																										else {
																											DV.cmp.favorite.rename.label.setText(DV.i18n.name_already_in_use);
																										}
																									}
																									this.disable();
                                                                                                },
                                                                                                handler: function() {
                                                                                                    DV.util.crud.favorite.updateName(this.up('window').cmp.name.getValue());
                                                                                                },
                                                                                                listeners: {
                                                                                                    afterrender: function() {
                                                                                                        this.up('window').cmp.rename = this;
                                                                                                    },
                                                                                                    change: function() {
                                                                                                        this.xable();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        ],
                                                                                        listeners: {
                                                                                            afterrender: function() {
                                                                                                DV.cmp.favorite.rename.window = this;
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                    w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                    w.show();
                                                                                },
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.rename.button = this;
                                                                                    }
                                                                                }
                                                                            },
                                                                            {
                                                                                text: DV.i18n.delete,
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                disabled: true,
                                                                                xable: function() {
                                                                                    if (DV.cmp.favorite.grid.getSelectionModel().getSelection().length) {
                                                                                        DV.cmp.favorite.del.enable();
                                                                                    }
                                                                                    else {
                                                                                        DV.cmp.favorite.del.disable();
                                                                                    }
                                                                                },
                                                                                handler: function() {
                                                                                    var sel = DV.cmp.favorite.grid.getSelectionModel().getSelection();
                                                                                    if (sel.length) {
                                                                                        var str = '';
                                                                                        for (var i = 0; i < sel.length; i++) {
                                                                                            var out = sel[i].data.name.length > 35 ? (sel[i].data.name.substr(0,35) + '...') : sel[i].data.name;
                                                                                            str += '<br/>' + out;
                                                                                        }
                                                                                        var w = Ext.create('Ext.window.Window', {
                                                                                            title: DV.i18n.delete_favorite,
                                                                                            width: DV.conf.layout.window_confirm_width,
                                                                                            bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                            modal: true,
                                                                                            items: [
                                                                                                {
                                                                                                    html: DV.i18n.are_you_sure,
                                                                                                    bodyStyle: 'border-style:none'
                                                                                                },
                                                                                                {
                                                                                                    html: str,
                                                                                                    cls: 'dv-window-confirm-list'
                                                                                                }                                                                                                    
                                                                                            ],
                                                                                            bbar: [
                                                                                                {
                                                                                                    text: DV.i18n.cancel,
                                                                                                    handler: function() {
                                                                                                        this.up('window').close();
                                                                                                    }
                                                                                                },
                                                                                                '->',
                                                                                                {
                                                                                                    text: DV.i18n.delete,
                                                                                                    handler: function() {
                                                                                                        this.up('window').close();
                                                                                                        DV.util.crud.favorite.del(function() {
                                                                                                            DV.cmp.favorite.name.setValue('');
                                                                                                            DV.cmp.favorite.window.down('grid').setHeightInWindow(DV.store.favorite);
                                                                                                        });                                                                                                        
                                                                                                    }
                                                                                                }
                                                                                            ]
                                                                                        });
                                                                                        w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                        w.show();
                                                                                    }
                                                                                },
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.del = this;
                                                                                    }
                                                                                }
                                                                            }
                                                                        ]
                                                                    },
                                                                    listeners: {
                                                                        added: function() {
                                                                            DV.cmp.favorite.grid = this;
                                                                        },
                                                                        itemclick: function(g, r) {
                                                                            DV.cmp.favorite.name.setValue(r.data.name);
                                                                            DV.cmp.favorite.system.setValue(r.data.userId ? false : true);
                                                                            DV.cmp.favorite.rename.button.xable();
                                                                            DV.cmp.favorite.del.xable();
                                                                        },
                                                                        itemdblclick: function() {
                                                                            if (DV.cmp.favorite.save.xable()) {
                                                                                DV.cmp.favorite.save.handler();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            ],
                                                            bbar: [
                                                                {
                                                                    xtype: 'label',
                                                                    style: 'padding-left:2px; line-height:22px; font-size:10px; color:#666; width:70%',
                                                                    listeners: {
                                                                        added: function() {
                                                                            DV.cmp.favorite.label = this;
                                                                        }
                                                                    }
                                                                },																
                                                                '->',
                                                                {
                                                                    text: DV.i18n.save,
                                                                    disabled: true,
                                                                    xable: function() {
                                                                        if (DV.state.isRendered) {
                                                                            if (DV.cmp.favorite.name.getValue()) {
                                                                                var index = DV.store.favorite.findExact('name', DV.cmp.favorite.name.getValue());
                                                                                if (index != -1) {
                                                                                    if (DV.store.favorite.getAt(index).data.userId || DV.init.system.user.isAdmin) {
                                                                                        this.enable();
                                                                                        DV.cmp.favorite.label.setText('');
                                                                                        return true;
                                                                                    }
                                                                                    else {
                                                                                        DV.cmp.favorite.label.setText(DV.i18n.system_favorite_overwrite_not_allowed);
                                                                                    }
                                                                                }
                                                                                else {
                                                                                    this.enable();
                                                                                    DV.cmp.favorite.label.setText('');
                                                                                    return true;
                                                                                }
                                                                            }
                                                                            else {
                                                                                DV.cmp.favorite.label.setText('');
                                                                            }
                                                                        }
                                                                        else {
                                                                            if (DV.cmp.favorite.name.getValue()) {
                                                                                DV.cmp.favorite.label.setText(DV.i18n.example_chart_cannot_be_saved);
                                                                            }
                                                                            else {
                                                                                DV.cmp.favorite.label.setText('');
                                                                            }																				
                                                                        }
                                                                        this.disable();
                                                                        return false;
                                                                    },
                                                                    handler: function() {
                                                                        if (this.xable()) {
                                                                            var value = DV.cmp.favorite.name.getValue();
                                                                            if (DV.store.favorite.findExact('name', value) != -1) {
                                                                                var item = value.length > 40 ? (value.substr(0,40) + '...') : value;
                                                                                var w = Ext.create('Ext.window.Window', {
                                                                                    title: DV.i18n.save_favorite,
                                                                                    width: DV.conf.layout.window_confirm_width,
                                                                                    bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                    modal: true,
                                                                                    items: [
                                                                                        {
                                                                                            html: DV.i18n.are_you_sure,
                                                                                            bodyStyle: 'border-style:none'
                                                                                        },
                                                                                        {
                                                                                            html: '<br/>' + item,
                                                                                            cls: 'dv-window-confirm-list'
                                                                                        }
                                                                                    ],
                                                                                    bbar: [
                                                                                        {
                                                                                            text: DV.i18n.cancel,
                                                                                            handler: function() {
                                                                                                this.up('window').close();
                                                                                            }
                                                                                        },
                                                                                        '->',
                                                                                        {
                                                                                            text: DV.i18n.overwrite,
                                                                                            handler: function() {
                                                                                                this.up('window').close();
                                                                                                DV.util.crud.favorite.update(function() {
                                                                                                    DV.cmp.favorite.window.resetForm();
                                                                                                });
                                                                                                
                                                                                            }
                                                                                        }
                                                                                    ]
                                                                                });
                                                                                w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                w.show();
                                                                            }
                                                                            else {
                                                                                DV.util.crud.favorite.create(function() {
                                                                                    DV.cmp.favorite.window.resetForm();
                                                                                    DV.cmp.favorite.window.down('grid').setHeightInWindow(DV.store.favorite);
                                                                                });
                                                                            }                                                                                    
                                                                        }
                                                                    },
                                                                    listeners: {
                                                                        added: function() {
                                                                            DV.cmp.favorite.save = this;
                                                                        }
                                                                    }
                                                                }
                                                            ],
                                                            listeners: {
                                                                show: function() {                                               
                                                                    DV.cmp.favorite.save.xable();
                                                                    this.down('grid').setHeightInWindow(DV.store.favorite);
                                                                }
                                                            }
                                                        });
                                                        var w = DV.cmp.favorite.window;
                                                        w.setPosition((screen.width/2)-(DV.conf.layout.grid_favorite_width/2), DV.conf.layout.window_favorite_ypos, true);
                                                        w.show();
                                                    }
                                                },
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.toolbar.menuitem.datatable = this;
                                                    }
                                                }
                                            },
                                            '-',
                                            {
                                                xtype: 'grid',
                                                cls: 'dv-menugrid',
                                                width: 420,
                                                scroll: 'vertical',
                                                columns: [
                                                    {
                                                        dataIndex: 'icon',
                                                        width: 25,
                                                        style: 'display:none'
                                                    },
                                                    {
                                                        dataIndex: 'name',
                                                        width: 285,
                                                        style: 'display:none'
                                                    },
                                                    {
                                                        dataIndex: 'lastUpdated',
                                                        width: 110,
                                                        style: 'display:none'
                                                    }
                                                ],
                                                setHeightInMenu: function(store) {
                                                    var h = store.getCount() * 26,
                                                        sh = DV.util.viewport.getSize().y * 0.6;
                                                    this.setHeight(h > sh ? sh : h);
                                                    this.doLayout();
                                                    this.up('menu').doLayout();
                                                },
                                                store: DV.store.favorite,
                                                listeners: {
                                                    itemclick: function(g, r) {
                                                        g.getSelectionModel().select([], false);
                                                        this.up('menu').hide();
                                                        DV.exe.execute(true, r.data.id);
                                                    }
                                                }
                                            }
                                        ],
                                        listeners: {
                                            show: function() {
                                                if (!DV.store.favorite.isLoaded) {
                                                    DV.store.favorite.load({scope: this, callback: function() {
                                                        this.down('grid').setHeightInMenu(DV.store.favorite);
                                                    }});
                                                }
                                                else {
                                                    this.down('grid').setHeightInMenu(DV.store.favorite);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: DV.i18n.download + '..',
                            execute: function(type) {
                                var svg = document.getElementsByTagName('svg');
                                
                                if (svg.length < 1) {
									DV.util.notification.error(DV.i18n.et_svg_browser, DV.i18n.em_svg_browser);
                                    return;
                                }
                                
                                document.getElementById('titleField').value = DV.state.filter.names[0] || 'Example chart';
                                document.getElementById('svgField').value = svg[0].parentNode.innerHTML;
                                document.getElementById('typeField').value = type;
                                
                                var exportForm = document.getElementById('exportForm');
                                exportForm.action = '../exportImage.action';
                                
                                if (svg[0].parentNode.innerHTML && type) {
                                    exportForm.submit();
                                }
                                else {
                                    alert(DV.i18n.no_svg_format);
                                }
                            },
                            listeners: {
                                afterrender: function(b) {
                                    this.menu = Ext.create('Ext.menu.Menu', {
                                        margin: '2 0 0 0',
                                        shadow: false,
                                        showSeparator: false,
                                        items: [
                                            {
                                                text: DV.i18n.image_png,
                                                iconCls: 'dv-menu-item-png',
                                                minWidth: 105,
                                                handler: function() {
                                                    b.execute(DV.conf.finals.image.png);
                                                }
                                            },
                                            {
                                                text: 'PDF',
                                                iconCls: 'dv-menu-item-pdf',
                                                minWidth: 105,
                                                handler: function() {
                                                    b.execute(DV.conf.finals.image.pdf);
                                                }
                                            }
                                        ]                                            
                                    });
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: DV.i18n.data_table,
                            disabled: true,
                            handler: function() {
                                var p = DV.cmp.region.east;
                                if (p.collapsed && p.items.length) {
                                    p.expand();
                                    DV.cmp.toolbar.resizeeast.show();
                                    DV.exe.execute(true, DV.conf.finals.cmd.datatable);
                                }
                                else {
                                    p.collapse();
                                    DV.cmp.toolbar.resizeeast.hide();
                                }
                            },
                            listeners: {
                                added: function() {
                                    DV.cmp.toolbar.datatable = this;
                                }
                            }
                        },
                        '->',
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: 'Exit',
                            handler: function() {
                                window.location.href = DV.conf.finals.ajax.path_portal + DV.conf.finals.ajax.redirect;
                            }
                        },
                        {
                            xtype: 'button',
                            name: 'resizeeast',
							cls: 'dv-toolbar-btn-2',
                            text: '>>>',
                            tooltip: DV.i18n.hide_data_table,
                            hidden: true,
                            handler: function() {
                                DV.cmp.region.east.collapse();
                                this.hide();
                            },
                            listeners: {
                                added: function() {
                                    DV.cmp.toolbar.resizeeast = this;
                                }
                            }
                        }
                    
                    ]
                },
                bbar: {
					items: [
						{
							xtype: 'panel',
							cls: 'dv-statusbar',
							height: 24,
							listeners: {
								added: function() {
									DV.cmp.statusbar.panel = this;
								}
							}
						}
					]
				},					
                listeners: {
                    added: function() {
                        DV.cmp.region.center = this;
                    },
                    resize: function() {
						if (DV.state.isRendered && DV.cmp.statusbar.panel) {
							DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
						}
					}
                }
            },
            {
                region: 'east',
                preventHeader: true,
                collapsible: true,
                collapsed: true,
                collapseMode: 'mini',
                width: 498,
                listeners: {
                    afterrender: function() {
                        DV.cmp.region.east = this;
                    }
                }
            }
        ],
        listeners: {
            afterrender: function() {
                DV.init.initialize();
            },
            resize: function(vp) {
                DV.cmp.region.west.setWidth(DV.conf.layout.west_width);
                
                if (DV.datatable.datatable) {
                    DV.datatable.datatable.setHeight(DV.util.viewport.getSize().y - DV.conf.layout.east_tbar_height);
                }
            }
        }
    });
    
    }});
});
