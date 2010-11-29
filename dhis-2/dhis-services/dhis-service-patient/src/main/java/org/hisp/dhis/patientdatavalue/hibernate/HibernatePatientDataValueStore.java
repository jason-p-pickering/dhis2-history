/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patientdatavalue.hibernate;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueStore;
import org.hisp.dhis.program.ProgramStageInstance;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernatePatientDataValueStore
    extends HibernateGenericStore<PatientDataValue>
    implements PatientDataValueStore
{
    public void saveVoid( PatientDataValue patientDataValue )
    {
        sessionFactory.getCurrentSession().save( patientDataValue );
    }

    public int delete( ProgramStageInstance programStageInstance )
    {
        Query query = getQuery( "delete from PatientDataValue where programStageInstance = :programStageInstance" );
        query.setEntity( "programStageInstance", programStageInstance );
        return query.executeUpdate();
    }

    public int delete( DataElement dataElement )
    {
        Query query = getQuery( "delete from PatientDataValue where dataElement = :dataElement" );
        query.setEntity( "dataElement", dataElement );
        return query.executeUpdate();
    }

    public int delete( DataElementCategoryOptionCombo optionCombo )
    {
        Query query = getQuery( "delete from PatientDataValue where optionCombo = :optionCombo" );
        query.setEntity( "optionCombo", optionCombo );
        return query.executeUpdate();
    }

    public PatientDataValue get( ProgramStageInstance programStageInstance, DataElement dataElement,
        OrganisationUnit organisationUnit )
    {
        return (PatientDataValue) getCriteria( Restrictions.eq( "programStageInstance", programStageInstance ),
            Restrictions.eq( "dataElement", dataElement ), Restrictions.eq( "organisationUnit", organisationUnit ) )
            .uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( ProgramStageInstance programStageInstance )
    {
        return getCriteria( Restrictions.eq( "programStageInstance", programStageInstance ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( Collection<ProgramStageInstance> programStageInstances )
    {
        return getCriteria( Restrictions.in( "programStageInstance", programStageInstances ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "dataElement", dataElement ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( DataElement dataElement, DataElementCategoryOptionCombo optionCombo )
    {
        return getCriteria( Restrictions.eq( "dataElement", dataElement ), Restrictions.eq( "optionCombo", optionCombo ) )
            .list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( DataElementCategoryOptionCombo optionCombo )
    {
        return getCriteria( Restrictions.eq( "optionCombo", optionCombo ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit )
    {
        return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit,
        ProgramStageInstance programStageInstance )
    {
        return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ),
            Restrictions.eq( "programStageInstance", programStageInstance ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit,
        Collection<ProgramStageInstance> programStageInstances )
    {
        return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ),
            Restrictions.in( "programStageInstance", programStageInstances ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ),
            Restrictions.eq( "dataElement", dataElement ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit,
        DataElementCategoryOptionCombo optionCombo )
    {
        return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ),
            Restrictions.eq( "optionCombo", optionCombo ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( boolean providedByAnotherFacility )
    {
        return getCriteria( Restrictions.eq( "providedByAnotherFacility", providedByAnotherFacility ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( OrganisationUnit organisationUnit, boolean providedByAnotherFacility )
    {
        return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ),
            Restrictions.eq( "providedByAnotherFacility", providedByAnotherFacility ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( ProgramStageInstance programStageInstance,
        boolean providedByAnotherFacility )
    {
        return getCriteria( Restrictions.eq( "programStageInstance", programStageInstance ),
            Restrictions.eq( "providedByAnotherFacility", providedByAnotherFacility ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( DataElement dataElement, boolean providedByAnotherFacility )
    {
        return getCriteria( Restrictions.eq( "dataElement", dataElement ),
            Restrictions.eq( "providedByAnotherFacility", providedByAnotherFacility ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataValue> get( Patient patient, Collection<DataElement> dataElements, Date startDate,
        Date endDate )
    {
        String hql = "From PatientDataValue pdv where pdv.dataElement in ( :dataElements ) "
            + "AND pdv.programStageInstance.programInstance.patient = :patient "
            + "AND pdv.programStageInstance.executionDate >= :startDate AND pdv.programStageInstance.executionDate <= :endDate ";

        return getQuery( hql ).setParameterList( "dataElements", dataElements ).setEntity( "patient", patient )
            .setDate( "startDate", startDate ).setDate( "endDate", endDate ).list();
    }
}
