/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.light.singleevents.action;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class PopulateSingleEventsDataAction implements Action {
    
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

	
	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------

	@Override
	public String execute() {
		
		// Create datasets
		PeriodType periodType = periodService.getPeriodTypeByName( "Daily" );
		DataSet dataSet1 = new DataSet( "Dataset1", "DD1", null, periodType );
        dataSet1.setMobile( true );
        dataSet1.setVersion( 1 );
        int datasetId1 = dataSetService.addDataSet( dataSet1 );
		dataSet1 = dataSetService.getDataSet(datasetId1);
		
		DataSet dataSet2 = new DataSet( "Dataset2", "DD2", null, periodType );
        dataSet2.setMobile( true );
        dataSet2.setVersion( 1 );
        int datasetId2 = dataSetService.addDataSet( dataSet2 );
		dataSet2 = dataSetService.getDataSet(datasetId2);
        
        // Create orgunits
		OrganisationUnit organisationUnit1 = new OrganisationUnit( "Andeby", "ab", null, new Date(), null, true, null );
		organisationUnit1.addDataSet(dataSet1);
		organisationUnit1.setHasPatients(true);
		int id1 = organisationUnitService.addOrganisationUnit( organisationUnit1 );
		
		OrganisationUnit organisationUnit2 = new OrganisationUnit( "Gaseby", "gb", null, new Date(), null, true, null );
		organisationUnit2.addDataSet(dataSet2);
		organisationUnit2.setHasPatients(true);
		int id2 = organisationUnitService.addOrganisationUnit( organisationUnit2 );
		
		organisationUnit1 = organisationUnitService.getOrganisationUnit(id1);
		organisationUnit2 = organisationUnitService.getOrganisationUnit(id2);
		
		Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
		organisationUnits.add(organisationUnit1);
		
		// Add orgunits to user
		Collection<User> users = userService.getAllUsers();
		User admin = users.iterator().next();
		
		admin.addOrganisationUnit(organisationUnit1);
		admin.addOrganisationUnit(organisationUnit2);
		userService.updateUser(admin);
		
		// Create Single-event programs
		
		Program program = new Program();
		
        program.setName( "Birth" );
        program.setDescription( "Birth" );
        program.setVersion( 1 );
        program.setDateOfEnrollmentDescription( "" );
        program.setDateOfIncidentDescription( "Date of birth" );
        program.setMaxDaysAllowedInputData( 60 );
        program.setSingleEvent( true );
        program.setOrganisationUnits(organisationUnits);

        programService.saveProgram( program );
        
        ProgramStage programStage = new ProgramStage();

        programStage.setName( "Single-Event" + " " + "Birth" );
        programStage.setDescription( "Birth" );
        programStage.setStageInProgram( program.getProgramStages().size() + 1 );
        programStage.setProgram( program );
        programStage.setMinDaysFromStart( 0 );

        programStageService.saveProgramStage( programStage );
        
		Program program2 = new Program();
		
        program2.setName( "Death");
        program2.setDescription( "Death" );
        program2.setVersion( 1 );
        program2.setDateOfEnrollmentDescription( "" );
        program2.setDateOfIncidentDescription( "Date of death" );
        program2.setMaxDaysAllowedInputData( 60 );
        program2.setSingleEvent( true );
        program2.setOrganisationUnits(organisationUnits);

        programService.saveProgram( program2 );
        
        ProgramStage programStage2 = new ProgramStage();

        programStage2.setName( "Single-Event" + " " + "Death" );
        programStage2.setDescription( "Death" );
        programStage2.setStageInProgram( program.getProgramStages().size() + 1 );
        programStage2.setProgram( program );
        programStage2.setMinDaysFromStart( 0 );
        
        ArrayList<String> patients = new ArrayList<String>();
        patients.add("Donald Duck");
        patients.add("Dolly Duck");
        patients.add("Doffen Duck");
        patients.add("Dole Duck");
        patients.add("Ole Duck");
        patients.add("Mikke Mus");
        patients.add("Langbein");
        patients.add("Petter Smart");
        patients.add("SvartePetter");
        patients.add("Onkel Skrue");
        patients.add("Darkwing Duck");
        patients.add("Minni Mus");
        patients.add("Fetter Anton");
        patients.add("Bestemor Duck");
        patients.add("Klodrik");
        patients.add("Rikerud");
        patients.add("Magica fra Tryll");
        patients.add("Klaus Knegg");
        patients.add("Pluto");
        patients.add("Politimester Fiks");
        patients.add("Anton Duck");
        patients.add("Hetti");
        patients.add("Netti");
        patients.add("Letti");
        patients.add("Nabo Jensen");
        patients.add("Klara Ku");
        patients.add("Rotor McKvakk");
        patients.add("Snipp");
        patients.add("Snapp");
        patients.add("B-Gjeng:176-167");
        patients.add("B-Gjeng:176-671");
        patients.add("B-Gjeng:176-761");
        patients.add("Bestefar B");
        
        // var to check if date sorts correct
        int i = 0;
        // var to alternate which program to enroll
        boolean alternate = true;
        
        for(String item : patients){
        	
        	alternate = (alternate) ? false : true;
        	i++;
        	
        	Program pro = (alternate) ? program : program2;
        	
	        Patient p = new Patient();
	        p.setIsDead( false );
	        p.setFirstName( item );
	        p.setBirthDate(new Date());
	        p.setGender(Patient.MALE);
	        p.setDobType(Patient.DOB_TYPE_VERIFIED);
	        p.setOrganisationUnit(organisationUnit1);
	        p.setRegistrationDate(new Date());
	        
	        patientService.savePatient(p);
	        
	        ProgramInstance programInstance = new ProgramInstance();
	        programInstance.setEnrollmentDate( new Date() );
	        Date date = new Date();
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.DAY_OF_MONTH, -i);
	        date = cal.getTime();
	        programInstance.setDateOfIncident(date);
	        programInstance.setProgram( pro );
	        programInstance.setPatient( p );
	        programInstance.setCompleted( false );
	
	        programInstanceService.addProgramInstance( programInstance );
	
	        p.getPrograms().add( pro );
	        patientService.updatePatient( p );
        }
		
		return SUCCESS;
	}
}
