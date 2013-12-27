'use strict';

/* Services */

var trackerFactory = angular.module('trackerServices', ['ngResource'])

/* Factory to fetch programs */
.factory('ProgramFactory', function($http) {
    
    var program, programPromise;
    var programs, programsPromise;
    
    return {
        
        getProgram: function(uid){
            if( program != uid && !programPromise ){
                programPromise = $http.get('../../api/programs/' + uid + '.json?viewClass=extended&paging=false').then(function(response){
                   program = response.data.id;
                   return response.data;
                });
            }
            return programPromise;
        },       
        
        getMyPrograms: function(){ 
            if( !programs || !programsPromise ){
                programsPromise = $http.get('../../api/me/programs').then(function(response){
                   programs = response.data;
                   return programs;
                });
            }
            return programsPromise;    
        }
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($http) {  
    
    var programStage, promise;   
    return {        
        getProgramStage: function(uid){
            if( programStage !== uid ){
                promise = $http.get( '../../api/programStages/' + uid + '.json?viewClass=extended&paging=false').then(function(response){
                   programStage = response.data.id;
                   return response.data;
                });
            }
            return promise;
        }
    };    
})

.factory('CurrentUserProfile', function($http) { 
    var profile, promise;
    return {
        getProfile: function() {
            if( !promise ){
                promise = $http.get('../../api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;           
        }
    };    
})

/* Factory to enroll person in a program */
.service('EnrollmentFactory', function( $http ) {
    
    var EnrollmentFactory = {};

    EnrollmentFactory.enrollPerson = function( enrollment ) {
        return $http.post( '../../api/enrollments', enrollment );
    };

    return EnrollmentFactory;
})

/* Factory for loading Essential Interventions */
.factory('EIFactory', function($http) {
    var ei, promise;
    return {
        getEI: function() {            
            if( !ei || !promise ){
                promise = $http.get('json/EI_All.json').then(function(response){                    
                    ei = response.data;
                    return ei;
                });
            }
            return promise;            
        }
    };
})

/* Factory for loading Essential Interventions - new*/
.factory('EIFormatter', function($http) {    
    return {
        getEI: function() {            
            var ei, promise;
            if( !ei || !promise ){
                promise = $http.get('../../api/dataElements.json?viewClass=extended&paging=false').then(function(response){
                    ei = response.data;                    
                    return response.data;
                });
            }
            return promise;            
        },
        
        formatEI: function(){
            var ei, promise;
            var dataElements = [];
            if( !ei || !promise ){
                promise = $http.get('../../api/dataElements.json?viewClass=extended&paging=false').then(function(response){
                    
                    angular.forEach(response.data.dataElements, function(de) {
                        
                        var dataElement;
                        
                        dataElement = {name: de.name,
                                       code: de.code,
                                       type: de.type,
                                       id: de.id,
                                       pageOfEntry: ['ANC1_GEN', 'ANC1_DEP', 'ANCnn_GEN', 'ANCnn_DEP'],
                                       sequenceOnPage: '',
                                       optionSet: '',
                                       actions: [{
                                               value: '',
                                               task: {
                                                   dependencies: [], 
                                                   conditionsComplications: [],
                                                   summary: [],
                                                   reminders: [],
                                                   messaging: [],
                                                   scheduling: []
                                               }
                                           }],
                                       labResults: ''};
                        
                        
                        if(angular.isObject(de.optionSet)){
                            var options = [];
                            angular.forEach(de.optionSet.options, function(option){
                                option = {label: option, value: option};
                                options.push(option);
                            });                            
                            dataElement.optionSet = options;                            
                        }  
                        dataElements.push(dataElement);
                    }); 
                    return dataElements;
                });
            }
            return promise;            
        }
    };
})

/* Factory for loading OrgUnit */
.factory('OrgUnitFactory', function($http) {

    var OrgUnitFactory = {};

    OrgUnitFactory.getAllOrgUnits = function() {
        return $http.get('../../api/organisationUnits.json');
    };

    OrgUnitFactory.getOrgUnit = function(uid) {
        return $http.get('../../api/organisationUnits/' + uid);
    };

    OrgUnitFactory.getMyOrgUnits = function() {
        return $http.get('../../api/me/organisationUnits');
    };

    return OrgUnitFactory;
})

/* Factory for getting person */
.factory('PersonFactory', function($http) {

    var baseUrl = '../../api/persons';
    var PersonFactory = {};

    PersonFactory.getPerson = function(uid) {
        return $http.get(baseUrl + '/' + uid + '.json');
    };

    PersonFactory.getAllPersons = function(orgUnitUid) {
        return $http.get(baseUrl + '?orgUnit=' + orgUnitUid + '&paging=false');
    };

    PersonFactory.registerPerson = function(person) {
        return $http.post(baseUrl, person);
    };

    PersonFactory.updatePerson = function(person) {
        return $http.put(baseUrl + '/' + person.person , person);
    };    

    return PersonFactory;
})

.factory('PersonService', function($http, PersonAttributesFactory){
    var person, programAttributes, promise; 
    return {
        getPerson: function(personUid, programUid){     

            //if(!promise){
                
                promise = $http.get('../../api/persons/' + personUid + '.json').then(function(response){              
                    person = response.data;                
                    PersonAttributesFactory.getEnrollmentAttributes(programUid).then(function(data) {
                        var enrollmentAttributes = [];
                        var registrationAttributes = person.attributes;
                        programAttributes = data.personAttributeTypes;   
                        angular.forEach(person.attributes, function(personAttribute) {
                            angular.forEach(programAttributes, function(programAttribute){                           
                                if( programAttribute.id == personAttribute.type){                                    
                                    enrollmentAttributes.push(personAttribute);
                                }                                
                            });                   
                        });

                        angular.forEach(enrollmentAttributes, function(a){
                            registrationAttributes.splice(registrationAttributes.indexOf(a),1);
                        });
                        
                        person.registrationAttributes = registrationAttributes;
                        person.enrollmentAttributes = enrollmentAttributes;                    
                    });                     
                    return person;
                });
            //}      
            return promise;
        }
    };            
})

/* Factory for getting person attribute types*/
.factory('PersonAttributesFactory', function($http) { 
    var rAttributes, rPromise;
    var eAttributes, ePromise;
    
    return {
        getEnrollmentAttributes: function(uid){

            if( !rAttributes || !rPromise ){                
                rPromise = $http.get('../../api/personAttributeTypes.json?program=' + uid + '&viewClass=extended&paging=false').then(function(response){                    
                    angular.forEach(response.data.personAttributeTypes, function(registrationAttribute) {
                        if (angular.isObject(registrationAttribute.personAttributeOptions)) {
                            angular.forEach(registrationAttribute.personAttributeOptions, function(attributeOption) {
                                attributeOption.value = attributeOption.name;
                            });
                        }
                    });                     
                   rAttributes = response.data;
                   return rAttributes;
                });
            }
            return rPromise;
        },
        
        getRegistrationAttributes: function(){

            if( !eAttributes || !ePromise ){                
                ePromise = $http.get('../../api/personAttributeTypes.json?withoutPrograms=true&viewClass=extended&paging=false').then(function(response){                    
                    angular.forEach(response.data.personAttributeTypes, function(registrationAttribute) {
                        if (angular.isObject(registrationAttribute.personAttributeOptions)) {
                            angular.forEach(registrationAttribute.personAttributeOptions, function(attributeOption) {
                                attributeOption.value = attributeOption.name;
                            });
                        }
                    });                    
                   eAttributes = response.data;
                   return eAttributes;
                });
            }
            return ePromise;
        }
    };
})

/* factory for getting data elements */
.factory('DataElementFactory', function($http) {

    var dataElementGroupBaseUrl = '../../api/dataElementGroups/';
    var dataElementBaseUrl = '../../api/dataElements/';

    var DataElemmentFactory = {};

    DataElemmentFactory.getDataElement = function(uid) {
        return $http.get(dataElementBaseUrl + uid + '.json');
    };

    DataElemmentFactory.getAllDataElements = function() {
        return $http.get(dataElementBaseUrl);
    };

    DataElemmentFactory.getDataElementGroup = function(uid) {
        return $http.get(dataElementGroupBaseUrl + uid + '.json');
    };

    DataElemmentFactory.getAllDataElementGroups = function() {
        return $http.get(dataElementGroupBaseUrl);
    };

    return DataElemmentFactory;
})

.factory('DHIS2EventFactory', function($http, $filter, storage) {   
    
    return {
        
        getDHIS2Events: function(person, orgUnit, program){        

            var promise = $http.get('../../api/events.json?' + 'person=' + person + '&orgUnit=' + orgUnit + '&program=' + program + '&paging=false').then(function(response){
                var dhis2Events = response.data.eventList;
                angular.forEach(dhis2Events, function(dhis2Event){
                    var programStage = storage.get(dhis2Event.programStage);
                    dhis2Event.name = programStage.name;                    
                    dhis2Event.eventDate = Date.parse(dhis2Event.eventDate.split(" ")[0]);
                    dhis2Event.eventDate = $filter('date')(dhis2Event.eventDate, 'yyyy-MM-dd');      
                });                
                return dhis2Events;                
            });            
            return promise;
        },
        
        getDHIS2Event: function(eventUID){
            
            var promise = $http.get('../../api/events/' + eventUID + '.json').then(function(response){               
                var dhis2Event = response.data;
                var programStage = storage.get(dhis2Event.programStage);
                dhis2Event.name = programStage.name;
                dhis2Event.eventDate = Date.parse(dhis2Event.eventDate.split(" ")[0]);
                dhis2Event.eventDate = $filter('date')(dhis2Event.eventDate, 'yyyy-MM-dd');               
                return dhis2Event;
            });            
            return promise;
        },
        
        postDHIS2Event: function(dhis2Event){
            var promise = $http.post('../../api/events.json?', dhis2Event).then(function(response){
                return response.data;
            });
            return promise;            
        },
        
        updateDHIS2Event: function(dhis2Event){            
            var promise = $http.put('../../api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                return response.data;
            });
            return promise;
        }
    };    
})

.service('DHIS2EventService', function() {
    var currentEventUid;
    return {
        setCurrentEventUid: function(uid) {
            currentEventUid = uid;
        },
        getCurrentEventUid: function() {    
            return currentEventUid;
        }
    };
})

.service('DependencyPageService', function() {
    var depPage;
    return {
        setDepPage: function(page) {
            depPage = page;
        },
        getDepPage: function() {    
            return depPage;
        }
    };
})

.factory('TransferHandler', function() {

    return {
        store: function(input, code, value, output) {            
            for (var i = 0; i < input.length; i++) {
                if (input[i]) {
                    input[i] = input[i].replace(new RegExp('#' + code + '#', 'g'), value);
                    if (output.indexOf(input[i]) == -1) {
                        output.push(input[i]);
                    }
                }
            }
        }
    };
})

.factory('TrackerApp', function($http) {
    
    var configuration, configurationPromise;

    return {
        getConfiguration: function() {
            
            if(!configuration || !configurationPromise){
                configurationPromise = $http.get('manifest.webapp').then(function(response){
                   configuration = response.data;
                   return configuration;
                });
            }
            
            return configurationPromoise; 
        }
    };
})

.service('UtilityService', function() {

    return {
        searchById: function(data, k) {

            angular.forEach(data, function(d) {
                if (k.id == d.id) {
                    return data.indexOf(d);
                }
            });
            return -1;
        }       
    };
})

.service('ExpressionService', function(storage) {
    
    return {
        getDataElementExpression: function(val, dhis2Events) {
            
            console.log('Input:  ', val);
           
           
            var regex = /#[^#]*#/g,
                    match,
                    m,
                    mDe,
                    matches = [];
            
            while (match = regex.exec(val)) {					
                matches.push(match);  
                m = match.toString();
                mDe = m.substring(1,m.length-1);
                
                var de = storage.get(mDe);
                
                var valueIsThere = false;
                angular.forEach(dhis2Events, function(dhis2Event){
                    angular.forEach(dhis2Event.dataValue, function(dataValue){
                        if( de.id == dataValue.dataElement)
                        {
                            valueIsThere = true;
                            console.log( storage.get(mDe), 'needs ',  dataValue );
                        }
                    });
                });
                
                console.log('finally the value is there:  ', valueIsThere);
                //var r = new RegExp(match,'g');
                val = val.replace(new RegExp(match,'g'), 'value of match');
            }
            
            console.log('Output:  ', val);
            return val;
        }
    };
})

.service('ModalService', ['$modal', function($modal) {

        var modalDefaults = {
            backdrop: true,
            keyboard: true,
            modalFade: true,
            templateUrl: '../tracker/views/modal.html'
        };

        var modalOptions = {
            closeButtonText: 'Close',
            actionButtonText: 'OK',
            headerText: 'Proceed?',
            bodyText: 'Perform this action?'
        };

        this.showModal = function(customModalDefaults, customModalOptions) {
            if (!customModalDefaults)
                customModalDefaults = {};
            customModalDefaults.backdrop = 'static';
            return this.show(customModalDefaults, customModalOptions);
        };

        this.show = function(customModalDefaults, customModalOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempModalDefaults = {};
            var tempModalOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempModalOptions, modalOptions, customModalOptions);

            if (!tempModalDefaults.controller) {
                tempModalDefaults.controller = function($scope, $modalInstance) {
                    $scope.modalOptions = tempModalOptions;
                    $scope.modalOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };
                    $scope.modalOptions.close = function(result) {
                        $modalInstance.dismiss('cancel');
                    };
                };
            }

            return $modal.open(tempModalDefaults).result;
        };

    }])

.service('DialogService', ['$modal', function($modal) {

        var dialogDefaults = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            modalFade: true,
            templateUrl: '../tracker/views/dialog.html'
        };

        var dialogOptions = {
            closeButtonText: 'close',
            actionButtonText: 'ok',
            headerText: 'dhis2_tracker',
            bodyText: 'Perform this action?'
        };

        this.showDialog = function(customDialogDefaults, customDialogOptions) {
            if (!customDialogDefaults)
                customDialogDefaults = {};
            customDialogDefaults.backdropClick = false;
            return this.show(customDialogDefaults, customDialogOptions);
        };

        this.show = function(customDialogDefaults, customDialogOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempDialogDefaults = {};
            var tempDialogOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempDialogOptions, dialogOptions, customDialogOptions);

            if (!tempDialogDefaults.controller) {
                tempDialogDefaults.controller = function($scope, $modalInstance) {
                    $scope.dialogOptions = tempDialogOptions;
                    $scope.dialogOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };                           
                };
            }

            return $modal.open(tempDialogDefaults).result;
        };

    }])

.service('NotesDialogService', ['$modal', function($modal) {

        var dialogDefaults = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            modalFade: true,
            templateUrl: '../tracker/views/anc/note.html'
        };

        var dialogOptions = {
            closeButtonText: 'close',
            actionButtonText: 'ok',
            headerText: 'dhis2_tracker',
            bodyText: 'Perform this action?',
            note: 'note',
            created_by: 'created_by',
            date: 'date'
        };

        this.showDialog = function(customDialogDefaults, customDialogOptions) {
            if (!customDialogDefaults)
                customDialogDefaults = {};
            customDialogDefaults.backdropClick = false;
            return this.show(customDialogDefaults, customDialogOptions);
        };

        this.show = function(customDialogDefaults, customDialogOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempDialogDefaults = {};
            var tempDialogOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempDialogOptions, dialogOptions, customDialogOptions);

            if (!tempDialogDefaults.controller) {
                tempDialogDefaults.controller = function($scope, $modalInstance) {
                    $scope.dialogOptions = tempDialogOptions;
                    $scope.dialogOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };                           
                };
            }

            return $modal.open(tempDialogDefaults).result;
        };

    }]);
