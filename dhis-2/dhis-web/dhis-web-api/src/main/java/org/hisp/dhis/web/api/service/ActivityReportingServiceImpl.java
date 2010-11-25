package org.hisp.dhis.web.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.activityplan.ActivityPlanService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientMobileSetting;
import org.hisp.dhis.patient.PatientMobileSettingService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.ActivityValue;
import org.hisp.dhis.web.api.model.Beneficiary;
import org.hisp.dhis.web.api.model.DataValue;
import org.hisp.dhis.web.api.model.PatientAttribute;
import org.hisp.dhis.web.api.model.Task;
import org.hisp.dhis.web.api.model.comparator.ActivityComparator;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

public class ActivityReportingServiceImpl
    implements ActivityReportingService
{

    private ActivityComparator activityComparator = new ActivityComparator();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ActivityPlanService activityPlanService;

    private org.hisp.dhis.program.ProgramStageInstanceService programStageInstanceService;

    private PatientAttributeValueService patientAttValueService;

    private PatientAttributeService patientAttService;

    private org.hisp.dhis.dataelement.DataElementCategoryService categoryService;

    private org.hisp.dhis.patientdatavalue.PatientDataValueService dataValueService;

    private CurrentUserService currentUserService;
    
    private PatientMobileSettingService patientMobileSettingService;
    
//    private PatientIdentifierService patientIdentifierService;

    // -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------

    public ActivityPlan getCurrentActivityPlan( OrganisationUnit unit, String localeString )
    {
        DateTime dt = new DateTime();
        DateMidnight from = dt.withDayOfMonth( 1 ).toDateMidnight();
        DateMidnight to = from.plusMonths( 1 );

        Collection<Activity> allActivities = activityPlanService.getActivitiesByProvider( unit );

        ActivityPlan plan = new ActivityPlan();

        List<org.hisp.dhis.web.api.model.Activity> items = new ArrayList<org.hisp.dhis.web.api.model.Activity>();

        int i = 0;
        for ( Activity activity : allActivities )
        {
            // there are error on db with patientattributeid 14, so I limit the
            // patient to be downloaded
            if ( i > 10 )
            {
                break;
            }

            long dueTime = activity.getDueDate().getTime();
            if ( to.isBefore( dueTime ) )
            {
                continue;
            }

            if ( from.isBefore( dueTime ) )
            {
                items.add( getActivityModel( activity ) );
                i++;
            }
            else if ( !activity.getTask().isCompleted() )
            {
                org.hisp.dhis.web.api.model.Activity a = getActivityModel( activity );
                items.add( a );
                a.setLate( true );
                i++;
            }
        }
        if ( !items.isEmpty() )
        {
            Collections.sort( items, activityComparator );
            plan.setActivitiesList( items );
        }

        return plan;

    }

    private org.hisp.dhis.web.api.model.Activity getActivityModel( org.hisp.dhis.activityplan.Activity activity )
    {
        if ( activity == null )
        {
            return null;
        }
        org.hisp.dhis.web.api.model.Activity item = new org.hisp.dhis.web.api.model.Activity();
        Patient patient = activity.getBeneficiary();

        item.setBeneficiary( getBeneficiaryModel( patient ) );
        item.setDueDate( activity.getDueDate() );
        item.setTask( getTask( activity.getTask() ) );
        return item;
    }

    public Task getTask( ProgramStageInstance stageInstance )
    {
        if ( stageInstance == null )
        {
            return null;
        }

        Task task = new Task();

        task.setCompleted( stageInstance.isCompleted() );
        task.setId( stageInstance.getId() );
        task.setProgramStageId( stageInstance.getProgramStage().getId() );

        return task;
    }

    private org.hisp.dhis.web.api.model.Beneficiary getBeneficiaryModel( Patient patient )
    {
        PatientMobileSetting setting = patientMobileSettingService.getCurrentSetting()==null?null:patientMobileSettingService.getCurrentSetting().iterator().next();
        
        Beneficiary beneficiary = new Beneficiary();

        List<PatientAttribute> patientAtts = new ArrayList<PatientAttribute>();

        beneficiary.setId( patient.getId() );
        beneficiary.setFirstName( patient.getFirstName() );
        beneficiary.setLastName( patient.getLastName() );
        beneficiary.setMiddleName( patient.getMiddleName() );
        int currentYear = new Date().getYear();
        int age = currentYear - patient.getBirthDate().getYear();
        beneficiary.setAge( age );
        
        // Set static attributes if it is required (gender, dobtype, birthdate, bloodgroup, registrationdate)
        if(setting != null){
            if(setting.getGender()){
                beneficiary.setGender( patient.getGender() );
            }
            if(setting.getDobtype()){
                beneficiary.setDobType( patient.getDobType() );
            }
            if(setting.getBirthdate()){
                beneficiary.setBirthDate( patient.getBirthDate() );
            }
            if(setting.getBloodgroup()){
                beneficiary.setBloodGroup( patient.getBloodGroup() );
            }
            if(setting.getRegistrationdate()){
                beneficiary.setRegistrationDate( patient.getRegistrationDate() );
            }
        }else{
            beneficiary.setGender(null);
            beneficiary.setBirthDate( null );
            beneficiary.setDobType( null );
            beneficiary.setBloodGroup( null );
            beneficiary.setRegistrationDate( null );
        }

        // Set attribute which is used to group beneficiary on mobile (only if
        // there is attribute which is set to be group factor)
        PatientAttribute beneficiaryAttribute = null;
        org.hisp.dhis.patient.PatientAttribute patientAttribute = patientAttService.getPatientAttributeByGroupBy( true );

        if ( patientAttribute != null )
        {
            beneficiaryAttribute = new PatientAttribute();
            beneficiaryAttribute.setName( patientAttribute.getName() );
            PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, patientAttribute );
            beneficiaryAttribute.setValue( value == null ? "Unknown" : value.getValue() );
            beneficiary.setGroupAttribute( beneficiaryAttribute );
        }
        patientAttribute = null;

        // Set all attributes
        
        List<org.hisp.dhis.patient.PatientAttribute> atts;
        if(setting != null){
            atts = setting.getPatientAttributes();
            for(org.hisp.dhis.patient.PatientAttribute each : atts){
                PatientAttributeValue value = patientAttValueService.getPatientAttributeValue( patient, each );
                if ( value != null )
                {
                    patientAtts.add( new PatientAttribute( each.getName(), value.getValue() ) );
                }
            }
        }
        
        // Set all identifier
        Set<PatientIdentifier> patientIdentifiers = patient.getIdentifiers();
        List<org.hisp.dhis.web.api.model.PatientIdentifier> identifiers = new ArrayList<org.hisp.dhis.web.api.model.PatientIdentifier>();
        if(patientIdentifiers.size() > 0){
            
            for(PatientIdentifier each : patientIdentifiers){
                identifiers.add( new org.hisp.dhis.web.api.model.PatientIdentifier(each.getIdentifierType().getName(), each.getIdentifier()) );
            }
            
            beneficiary.setIdentifiers( identifiers );
        }
        
        beneficiary.setPatientAttValues( patientAtts );
        return beneficiary;
    }

    // -------------------------------------------------------------------------
    // DataValueService
    // -------------------------------------------------------------------------

    @Override
    public String saveActivityReport( OrganisationUnit unit, ActivityValue activityValue )
    {

        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( activityValue
            .getProgramInstanceId() );

        if ( programStageInstance == null )
        {
            return "INVALID_PROGRAM_STAGE";
        }

        programStageInstance.getProgramStage();
        Collection<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>();

        for ( ProgramStageDataElement de : programStageInstance.getProgramStage().getProgramStageDataElements() )
        {
            dataElements.add( de.getDataElement() );
        }

        programStageInstance.getProgramStage().getProgramStageDataElements();
        Collection<Integer> dataElementIds = new ArrayList<Integer>( activityValue.getDataValues().size() );

        for ( DataValue dv : activityValue.getDataValues() )
        {
            dataElementIds.add( dv.getId() );
        }

        if ( dataElements.size() != dataElementIds.size() )
        {
            return "INVALID_PROGRAM_STAGE";
        }

        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = new HashMap<Integer, org.hisp.dhis.dataelement.DataElement>();
        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            if ( !dataElementIds.contains( dataElement.getId() ) )
            {
                return "INVALID_PROGRAM_STAGE";
            }
            dataElementMap.put( dataElement.getId(), dataElement );
        }

        // Set ProgramStageInstance to completed
        programStageInstance.setCompleted( true );
        programStageInstanceService.updateProgramStageInstance( programStageInstance );
        // Everything is fine, hence save
        saveDataValues( activityValue, programStageInstance, dataElementMap, unit,
            categoryService.getDefaultDataElementCategoryOptionCombo() );

        return "SUCCESS";

    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private void saveDataValues( ActivityValue activityValue, ProgramStageInstance programStageInstance,
        Map<Integer, DataElement> dataElementMap, OrganisationUnit orgUnit, DataElementCategoryOptionCombo optionCombo )
    {

        org.hisp.dhis.dataelement.DataElement dataElement;
        String value;

        for ( DataValue dv : activityValue.getDataValues() )
        {
            value = dv.getVal();
            DataElementCategoryOptionCombo cateOptCombo = categoryService.getDataElementCategoryOptionCombo( dv
                .getCategoryOptComboID() );
            if ( value != null && value.trim().length() == 0 )
            {
                value = null;
            }

            if ( value != null )
            {
                value = value.trim();
            }

            dataElement = dataElementMap.get( dv.getId() );
            PatientDataValue dataValue = dataValueService.getPatientDataValue( programStageInstance, dataElement,
                orgUnit );

            if ( dataValue == null )
            {
                if ( value != null )
                {
                    if ( programStageInstance.getExecutionDate() == null )
                    {
                        programStageInstance.setExecutionDate( new Date() );
                        programStageInstanceService.updateProgramStageInstance( programStageInstance );
                    }

                    dataValue = new PatientDataValue( programStageInstance, dataElement, cateOptCombo, orgUnit,
                        new Date(), value, false );
                    
                    dataValueService.savePatientDataValue( dataValue );
                }
            }
            else
            {
                if ( programStageInstance.getExecutionDate() == null )
                {
                    programStageInstance.setExecutionDate( new Date() );
                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }

                dataValue.setValue( value );
                dataValue.setOptionCombo( optionCombo );
                dataValue.setProvidedByAnotherFacility( false );
                dataValue.setTimestamp( new Date() );

                dataValueService.updatePatientDataValue( dataValue );
            }
        }
    }

    // Setters...

    @Required
    public void setProgramStageInstanceService(
        org.hisp.dhis.program.ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    @Required
    public void setActivityPlanService( ActivityPlanService activityPlanService )
    {
        this.activityPlanService = activityPlanService;
    }

    @Required
    public void setPatientAttValueService( PatientAttributeValueService patientAttValueService )
    {
        this.patientAttValueService = patientAttValueService;
    }

    @Required
    public void setPatientAttService( PatientAttributeService patientAttService )
    {
        this.patientAttService = patientAttService;
    }

    @Required
    public void setCategoryService( org.hisp.dhis.dataelement.DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    @Required
    public void setDataValueService( org.hisp.dhis.patientdatavalue.PatientDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    @Required
    public void setPatientMobileSettingService( PatientMobileSettingService patientMobileSettingService )
    {
        this.patientMobileSettingService = patientMobileSettingService;
    }

//    @Required
//    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
//    {
//        this.patientIdentifierService = patientIdentifierService;
//    }
    
    
    
    

}
