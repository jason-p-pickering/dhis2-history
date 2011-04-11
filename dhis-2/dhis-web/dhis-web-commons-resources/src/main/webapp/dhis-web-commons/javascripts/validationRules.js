var validationRules = {
	/* dhis-web-maintenance-user */
	"user" : {
		"username" : {
			"required" : true,
			"rangelength" : [ 2, 140 ],
			"firstletteralphabet" : true,
			"alphanumeric" : true
		},
		"firstName" : {
			"required" : true,
			"rangelength" : [ 2, 140 ]
		},
		"surname" : {
			"required" : true,
			"rangelength" : [ 2, 140 ]
		},
		"password" : {
			"required" : true,
			"password" : true,
			"notequalto" : "#username",
			"rangelength" : [ 8, 35 ]
		},
		"rawPassword" : {
			"required" : true,
			"password" : true,
			"rangelength" : [ 8, 35 ]
		},
		"retypePassword" : {
			"required" : true,
			"equalTo" : "#rawPassword"
		},
		"email" : {
			"email" : true,
			"rangelength" : [ 0, 160 ]
		},
		"phoneNumber" : {
			"rangelength" : [ 0, 80 ]
		},
		"roleValidator" : {
			"required" : true
		}
	},
	"role" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 2, 140 ]
		},
		"description" : {
			"required" : true,
			"rangelength" : [ 2, 210 ]
		}
	},
	"userGroup" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 2, 210 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"memberValidator" : {
			"required" : true
		}
	},

	/* dhis-web-maintenance-organisationunit */
	"organisationUnit" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 2, 160 ]
		},
		"shortName" : {
			"required" : true,
			"rangelength" : [ 2, 25 ]
		},
		"code" : {
			"required" : true,
			"rangelength" : [ 0, 25 ]
		},
		"openingDate" : {
			"required" : true
		},
		"url" : {
			"url" : true,
			"rangelength" : [ 0, 255 ]
		},
		"contactPerson" : {
			"rangelength" : [ 0, 255 ]
		},
		"address" : {
			"rangelength" : [ 0, 255 ]
		},
		"email" : {
			"email" : true,
			"rangelength" : [ 0, 250 ]
		},
		"phoneNumber" : {
			"rangelength" : [ 0, 255 ]
		}
	},
	"organisationUnitGroup" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 2, 160 ]
		}
	},
	"organisationUnitGroupSet" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 2, 230 ]
		},
		"description" : {
			"required" : true,
			"rangelength" : [ 2, 255 ]
		}
	},

	/* dhis-web-maintenance-dataset */
	"dataEntry" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 4, 100 ]
		}
	},
	"section" : {
		"sectionName" : {
			"required" : true,
			"rangelength" : [ 2, 160 ]
		},
		"selectedList" : {
			"required" : true
		}
	},
	"dataSet" : {
		"name" : {
			"required" : true,
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : false,
			"rangelength" : [ 4, 150 ]
		},
		"shortName" : {
			"required" : true,
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : false,
			"rangelength" : [ 2, 20 ]
		},
		"code" : {
			"alphanumericwithbasicpuncspaces" : true,
			"notOnlyDigits" : false,
			"rangelength" : [ 4, 40 ]
		},
		"frequencySelect" : {
			"required" : true
		}
	},

	/* dhis-web-maintenance-dataadmin */
	"sqlView" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 2, 50 ]
		},
		"description" : {
			"required" : true,
			"rangelength" : [ 2, 255 ]
		},
		"sqlquery" : {
			"required" : true,
			"rangelength" : [ 1, 255 ]
		}
	},
	"dataLocking" : {
		"selectedPeriods" : {
			"required" : true
		},
		"selectedDataSets" : {
			"required" : true
		}
	},
	"dataBrowser" : {
		"periodTypeId" : {
			"required" : true
		},
		"mode" : {
			"required" : true
		}
	},
	"minMax" : {
		"dataSetIds" : {
			"required" : true
		}
	},

	/* dhis-web-validationrule */
	"validationRule" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 2, 160 ]
		},
		"description" : {
			"rangelength" : [ 2, 160 ]
		},
		"periodTypeName" : {
			"required" : true
		},
		"operator" : {
			"required" : true
		},
		"leftSideExpression" : {
			"required" : true
		},
		"rightSideExpression" : {
			"required" : true
		}
	},
	"validationRuleGroup" : {
		"name" : {
			"required" : true,
			"rangelength" : [ 2, 160 ]
		},
		"description" : {
			"rangelength" : [ 2, 160 ]
		}
	},

	/* dhis-web-maintenance-datadictionary */
	"concept" : {
		"name" : {
			"rangelength" : [ 3, 10 ]
		}
	},
	"dateElementCategoryCombo" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		}
	},
	"dateElementCategory" : {
		"name" : {
			"rangelength" : [ 2, 160 ]
		}
	},
	"dataElementGroup" : {
		"name" : {
			"rangelength" : [ 3, 150 ]
		}
	},
	"dataElementGroupSet" : {
		"name" : {
			"rangelength" : [ 2, 230 ]
		}
	},
	"indicator" : {
		"name" : {
			"rangelength" : [ 3, 150 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"shortName" : {
			"rangelength" : [ 2, 20 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"alternativeName" : {
			"rangelength" : [ 3, 150 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"code" : {
			"rangelength" : [ 3, 25 ],
			"alphanumericwithbasicpuncspaces" : true,
			"notOnlyDigits" : false
		},
		"description" : {
			"rangelength" : [ 3, 250 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"url" : {
			"rangelength" : [ 0, 255 ]
		}
	},
	"indicatorGroup" : {
		"name" : {
			"rangelength" : [ 3, 150 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		}
	},
	"indicatorGroupSet" : {
		"name" : {
			"rangelength" : [ 2, 230 ]
		}
	},
	"indicatorType" : {
		"name" : {
			"rangelength" : [ 3, 150 ],
			"alphanumericwithbasicpuncspaces" : true,
			"firstletteralphabet" : true
		},
		"factor" : {
			"rangelength" : [ 1, 10 ]
		}
	}
}
