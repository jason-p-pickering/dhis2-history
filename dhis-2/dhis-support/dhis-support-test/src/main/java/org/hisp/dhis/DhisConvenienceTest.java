package org.hisp.dhis;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.datadictionary.ExtendedDataElement;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dbms.DbmsManager;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportObjectStatus;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.olap.OlapURL;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.resourcetable.ResourceTableService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.source.SourceStore;
import org.hisp.dhis.user.User;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public abstract class DhisConvenienceTest
{
    private static final String BASE_UUID = "C3C2E28D-9686-4634-93FD-BE3133935EC";

    private static final String EXT_TEST_DIR = System.getProperty( "user.home" ) + File.separator + "dhis2_test_dir";

    private static Date date;

    // -------------------------------------------------------------------------
    // Service references
    // -------------------------------------------------------------------------

    protected DataElementService dataElementService;

    protected DataElementCategoryService categoryService;

    protected DataDictionaryService dataDictionaryService;

    protected IndicatorService indicatorService;

    protected DataSetService dataSetService;

    protected CompleteDataSetRegistrationService completeDataSetRegistrationService;

    protected SourceStore sourceStore;

    protected OrganisationUnitService organisationUnitService;

    protected OrganisationUnitGroupService organisationUnitGroupService;

    protected PeriodService periodService;

    protected ValidationRuleService validationRuleService;

    protected ExpressionService expressionService;

    protected DataValueService dataValueService;

    protected ResourceTableService resourceTableService;

    protected MappingService mappingService;

    protected DbmsManager dbmsManager;

    protected LocationManager locationManager;
    
    protected ProgramStageService programStageService;

    static
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set( 1970, Calendar.JANUARY, 1 );

        date = calendar.getTime();
    }

    // -------------------------------------------------------------------------
    // Convenience methods
    // -------------------------------------------------------------------------

    /**
     * Creates a date.
     * 
     * @param year the year.
     * @param month the month.
     * @param day the day of month.
     * @return a date.
     */
    public static Date getDate( int year, int month, int day )
    {
        final Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set( year, month - 1, day );

        return calendar.getTime();
    }

    /**
     * Creates a date.
     * 
     * @param day the day of the year.
     * @return a date.
     */
    public Date getDay( int day )
    {
        final Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set( Calendar.DAY_OF_YEAR, day );

        return calendar.getTime();
    }

    /**
     * Compares two collections for equality. This method does not check for the
     * implementation type of the collection in contrast to the native equals
     * method. This is useful for black-box testing where one will not know the
     * implementation type of the returned collection for a method.
     * 
     * @param actual the actual collection to check.
     * @param reference the reference objects to check against.
     * @return true if the collections are equal, false otherwise.
     */
    public static boolean equals( Collection<?> actual, Object... reference )
    {
        final Collection<Object> collection = new HashSet<Object>();

        for ( Object object : reference )
        {
            collection.add( object );
        }

        if ( actual == collection )
        {
            return true;
        }

        if ( actual == null || collection == null )
        {
            return false;
        }

        if ( actual.size() != collection.size() )
        {
            return false;
        }

        for ( Object object : actual )
        {
            if ( !collection.contains( object ) )
            {
                return false;
            }
        }

        for ( Object object : collection )
        {
            if ( !actual.contains( object ) )
            {
                return false;
            }
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Dependency injection methods
    // -------------------------------------------------------------------------

    /**
     * Sets a dependency on the target service. This method can be used to set
     * mock implementations of dependencies on services for testing purposes.
     * The advantage of using this method over setting the services directly is
     * that the test can still be executed against the interface type of the
     * service; making the test unaware of the implementation and thus
     * re-usable. A weakness is that the field name of the dependency must be
     * assumed.
     * 
     * @param targetService the target service.
     * @param fieldName the name of the dependency field in the target service.
     * @param dependency the dependency.
     */
    protected void setDependency( Object targetService, String fieldName, Object dependency )
    {
        Class<?> clazz = dependency.getClass().getInterfaces()[0];

        setDependency( targetService, fieldName, dependency, clazz );
    }

    /**
     * Sets a dependency on the target service. This method can be used to set
     * mock implementations of dependencies on services for testing purposes.
     * The advantage of using this method over setting the services directly is
     * that the test can still be executed against the interface type of the
     * service; making the test unaware of the implementation and thus
     * re-usable. A weakness is that the field name of the dependency must be
     * assumed.
     * 
     * @param targetService the target service.
     * @param fieldName the name of the dependency field in the target service.
     * @param dependency the dependency.
     * @param clazz the class type of the dependency.
     */
    protected void setDependency( Object targetService, String fieldName, Object dependency, Class<?> clazz )
    {
        try
        {
            String setMethodName = "set" + fieldName.substring( 0, 1 ).toUpperCase()
                + fieldName.substring( 1, fieldName.length() );

            Class<?>[] argumentClass = new Class<?>[] { clazz };

            Method method = targetService.getClass().getMethod( setMethodName, argumentClass );

            method.invoke( targetService, dependency );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to set dependency on service: " + fieldName, ex );
        }
    }

    // -------------------------------------------------------------------------
    // Create object methods
    // -------------------------------------------------------------------------

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataElement createDataElement( char uniqueCharacter )
    {
        DataElement dataElement = new DataElement();

        dataElement.setUuid( BASE_UUID + uniqueCharacter );
        dataElement.setName( "DataElement" + uniqueCharacter );
        dataElement.setAlternativeName( "AlternativeName" + uniqueCharacter );
        dataElement.setShortName( "ShortName" + uniqueCharacter );
        dataElement.setCode( "Code" + uniqueCharacter );
        dataElement.setDescription( "Description" + uniqueCharacter );
        dataElement.setActive( true );
        dataElement.setType( DataElement.VALUE_TYPE_INT );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        dataElement.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );

        return dataElement;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param categoryCombo The category combo.
     */
    public static DataElement createDataElement( char uniqueCharacter, DataElementCategoryCombo categoryCombo )
    {
        DataElement dataElement = createDataElement( uniqueCharacter );

        dataElement.setCategoryCombo( categoryCombo );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );        

        return dataElement;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param valueType The value type.
     * @param aggregationOperator The aggregation operator.
     */
    public static DataElement createDataElement( char uniqueCharacter, String type, String aggregationOperator )
    {
        DataElement dataElement = createDataElement( uniqueCharacter );        
        dataElement.setType( type );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        dataElement.setAggregationOperator( aggregationOperator );

        return dataElement;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param valueType The value type.
     * @param aggregationOperator The aggregation operator.
     * @param categoryCombo The category combo.
     */
    public static DataElement createDataElement( char uniqueCharacter, String type, String aggregationOperator, DataElementCategoryCombo categoryCombo )
    {
        DataElement dataElement = createDataElement( uniqueCharacter );        
        dataElement.setType( type );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        dataElement.setAggregationOperator( aggregationOperator );
        dataElement.setCategoryCombo( categoryCombo );

        return dataElement;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param expression The Expression.
     */
    public static CalculatedDataElement createCalculatedDataElement( char uniqueCharacter, String type, String aggregationOperator, DataElementCategoryCombo categoryCombo, Expression expression )
    {
        CalculatedDataElement dataElement = new CalculatedDataElement();

        dataElement.setUuid( BASE_UUID + uniqueCharacter );
        dataElement.setName( "DataElement" + uniqueCharacter );
        dataElement.setAlternativeName( "AlternativeName" + uniqueCharacter );
        dataElement.setShortName( "ShortName" + uniqueCharacter );
        dataElement.setCode( "Code" + uniqueCharacter );
        dataElement.setDescription( "Description" + uniqueCharacter );
        dataElement.setActive( true );
        dataElement.setType( type );
        dataElement.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        dataElement.setAggregationOperator( aggregationOperator );
        dataElement.setCategoryCombo( categoryCombo );
        dataElement.setSaved( false );
        dataElement.setExpression( expression );

        return dataElement;
    }

    /**
     * @param categoryComboUniqueIdentifier A unique character to identify the
     *        category combo.
     * @param categoryOptionUniqueIdentifiers Unique characters to identify the
     *        category options.
     * @return
     */
    public static DataElementCategoryOptionCombo createCategoryOptionCombo( char categoryComboUniqueIdentifier,
        char... categoryOptionUniqueIdentifiers )
    {
        DataElementCategoryOptionCombo categoryOptionCombo = new DataElementCategoryOptionCombo();

        categoryOptionCombo.setCategoryCombo( new DataElementCategoryCombo( "CategoryCombo"
            + categoryComboUniqueIdentifier ) );

        for ( char identifier : categoryOptionUniqueIdentifiers )
        {
            categoryOptionCombo.getCategoryOptions().add( new DataElementCategoryOption( "CategoryOption" + identifier ) );
        }

        return categoryOptionCombo;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataElementGroup createDataElementGroup( char uniqueCharacter )
    {
        DataElementGroup group = new DataElementGroup();

        group.setUuid( BASE_UUID + uniqueCharacter );
        group.setName( "DataElementGroup" + uniqueCharacter );

        return group;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataElementGroupSet createDataElementGroupSet( char uniqueCharacter )
    {
        DataElementGroupSet groupSet = new DataElementGroupSet();
        
        groupSet.setUuid( BASE_UUID + uniqueCharacter );
        groupSet.setName( "DataElementGroupSet" + uniqueCharacter );
        
        return groupSet;
    }    

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataDictionary createDataDictionary( char uniqueCharacter )
    {
        DataDictionary dictionary = new DataDictionary();

        dictionary.setName( "DataDictionary" + uniqueCharacter );
        dictionary.setDescription( "Description" + uniqueCharacter );
        dictionary.setRegion( "Region" + uniqueCharacter );

        return dictionary;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static IndicatorType createIndicatorType( char uniqueCharacter )
    {
        IndicatorType type = new IndicatorType();

        type.setName( "IndicatorType" + uniqueCharacter );
        type.setFactor( 100 );

        return type;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param type The type.
     */
    public static Indicator createIndicator( char uniqueCharacter, IndicatorType type )
    {
        Indicator indicator = new Indicator();

        indicator.setUuid( BASE_UUID + uniqueCharacter );
        indicator.setName( "Indicator" + uniqueCharacter );
        indicator.setAlternativeName( "AlternativeName" + uniqueCharacter );
        indicator.setShortName( "ShortName" + uniqueCharacter );
        indicator.setCode( "Code" + uniqueCharacter );
        indicator.setDescription( "Description" + uniqueCharacter );
        indicator.setAnnualized( false );
        indicator.setIndicatorType( type );
        indicator.setNumerator( "Numerator" );
        indicator.setNumeratorDescription( "NumeratorDescription" );
        indicator.setNumeratorAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        indicator.setDenominator( "Denominator" );
        indicator.setDenominatorDescription( "DenominatorDescription" );
        indicator.setDenominatorAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );

        return indicator;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static IndicatorGroup createIndicatorGroup( char uniqueCharacter )
    {
        IndicatorGroup group = new IndicatorGroup();

        group.setUuid( BASE_UUID + uniqueCharacter );
        group.setName( "IndicatorGroup" + uniqueCharacter );

        return group;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static IndicatorGroupSet createIndicatorGroupSet( char uniqueCharacter )
    {
        IndicatorGroupSet groupSet = new IndicatorGroupSet();
        
        groupSet.setUuid( BASE_UUID + uniqueCharacter );
        groupSet.setName( "IndicatorGroupSet" + uniqueCharacter );
        
        return groupSet;
    }
    
    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param periodType The period type.
     */
    public static DataSet createDataSet( char uniqueCharacter, PeriodType periodType )
    {
        DataSet dataSet = new DataSet();

        dataSet.setName( "DataSet" + uniqueCharacter );
        dataSet.setShortName( "ShortName" + uniqueCharacter );
        dataSet.setCode( "Code" + uniqueCharacter );
        dataSet.setPeriodType( periodType );

        return dataSet;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static OrganisationUnit createOrganisationUnit( char uniqueCharacter )
    {
        OrganisationUnit unit = new OrganisationUnit();

        unit.setUuid( BASE_UUID + uniqueCharacter );
        unit.setName( "OrganisationUnit" + uniqueCharacter );
        unit.setShortName( "ShortName" + uniqueCharacter );
        unit.setCode( "Code" + uniqueCharacter );
        unit.setOpeningDate( date );
        unit.setClosedDate( date );
        unit.setActive( true );
        unit.setComment( "Comment" + uniqueCharacter );
        unit.setGeoCode( "GeoCode" );
        unit.setLatitude( "Latitude" );
        unit.setLongitude( "Longitude" );

        return unit;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param parent The parent.
     */
    public static OrganisationUnit createOrganisationUnit( char uniqueCharacter, OrganisationUnit parent )
    {
        OrganisationUnit unit = createOrganisationUnit( uniqueCharacter );

        unit.setParent( parent );

        return unit;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static OrganisationUnitGroup createOrganisationUnitGroup( char uniqueCharacter )
    {
        OrganisationUnitGroup group = new OrganisationUnitGroup();

        group.setUuid( BASE_UUID + uniqueCharacter );
        group.setName( "OrganisationUnitGroup" + uniqueCharacter );

        return group;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static OrganisationUnitGroupSet createOrganisationUnitGroupSet( char uniqueCharacter )
    {
        OrganisationUnitGroupSet groupSet = new OrganisationUnitGroupSet();

        groupSet.setName( "OrganisationUnitGroupSet" + uniqueCharacter );
        groupSet.setDescription( "Description" + uniqueCharacter );
        groupSet.setCompulsory( true );
        groupSet.setExclusive( true );

        return groupSet;
    }

    /**
     * @param type The PeriodType.
     * @param startDate The start date.
     * @param endDate The end date.
     */
    public static Period createPeriod( PeriodType type, Date startDate, Date endDate )
    {
        Period period = new Period();

        period.setPeriodType( type );
        period.setStartDate( startDate );
        period.setEndDate( endDate );

        return period;
    }

    /**
     * @param startDate The start date.
     * @param endDate The end date.
     */
    public static Period createPeriod( Date startDate, Date endDate )
    {
        Period period = new Period();

        period.setPeriodType( new MonthlyPeriodType() );
        period.setStartDate( startDate );
        period.setEndDate( endDate );

        return period;
    }

    /**
     * @param dataElement The data element.
     * @param period The period.
     * @param source The source.
     * @param value The value.
     * @param categoryOptionCombo The data element category option combo.
     */
    public static DataValue createDataValue( DataElement dataElement, Period period, Source source, String value,
        DataElementCategoryOptionCombo categoryOptionCombo )
    {
        DataValue dataValue = new DataValue();

        dataValue.setDataElement( dataElement );
        dataValue.setPeriod( period );
        dataValue.setSource( source );
        dataValue.setValue( value );
        dataValue.setComment( "Comment" );
        dataValue.setStoredBy( "StoredBy" );
        dataValue.setTimestamp( date );
        dataValue.setOptionCombo( categoryOptionCombo );

        return dataValue;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param operator The operator.
     * @param leftSide The left side expression.
     * @param rightSide The right side expression.
     */
    public static ValidationRule createValidationRule( char uniqueCharacter, String operator, Expression leftSide,
        Expression rightSide )
    {
        ValidationRule validationRule = new ValidationRule();

        validationRule.setName( "ValidationRule" + uniqueCharacter );
        validationRule.setDescription( "Description" + uniqueCharacter );
        validationRule.setType( ValidationRule.TYPE_ABSOLUTE );
        validationRule.setOperator( operator );
        validationRule.setLeftSide( leftSide );
        validationRule.setRightSide( rightSide );

        return validationRule;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @return
     */
    public static ValidationRuleGroup createValidationRuleGroup( char uniqueCharacter )
    {
        ValidationRuleGroup group = new ValidationRuleGroup();

        group.setName( "ValidationRuleGroup" + uniqueCharacter );
        group.setDescription( "Description" + uniqueCharacter );

        return group;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param expressionString The expression string.
     * @param dataElementsInExpression A collection of the data elements
     *        entering into the expression.
     */
    public static Expression createExpression( char uniqueCharacter, String expressionString,
        Set<DataElement> dataElementsInExpression )
    {
        Expression expression = new Expression();

        expression.setExpression( expressionString );
        expression.setDescription( "Description" + uniqueCharacter );
        expression.setDataElementsInExpression( dataElementsInExpression );

        return expression;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     */
    public static DataElement createExtendedDataElement( char uniqueCharacter )
    {
        DataElement dataElement = createDataElement( uniqueCharacter );

        ExtendedDataElement extended = createExtendedElement( uniqueCharacter );

        dataElement.setExtended( extended );

        return dataElement;
    }

    /**
     * @param uniqueCharacter A unique character to identify the object.
     * @param type The type.
     */
    public static Indicator createExtendedIndicator( char uniqueCharacter, IndicatorType type )
    {
        Indicator indicator = createIndicator( uniqueCharacter, type );

        ExtendedDataElement extended = createExtendedElement( uniqueCharacter );

        indicator.setExtended( extended );

        return indicator;
    }

    /**
     * @param dataElementId The data element identifier.
     * @param categoryOptionComboId The data element category option combo
     *        identifier.
     * @param periodId The period identifier.
     * @param sourceId The source identifier.
     * @param status The status.
     */
    public static ImportDataValue createImportDataValue( int dataElementId, int categoryOptionComboId, int periodId,
        int sourceId, ImportObjectStatus status )
    {
        ImportDataValue importDataValue = new ImportDataValue();

        importDataValue.setDataElementId( dataElementId );
        importDataValue.setCategoryOptionComboId( categoryOptionComboId );
        importDataValue.setPeriodId( periodId );
        importDataValue.setSourceId( sourceId );
        importDataValue.setValue( String.valueOf( 10 ) );
        importDataValue.setStoredBy( "StoredBy" );
        importDataValue.setTimestamp( new Date() );
        importDataValue.setComment( "Comment" );
        importDataValue.setStatus( status.name() );

        return importDataValue;
    }

    public static Map createMap( char uniqueCharacter, OrganisationUnit unit, OrganisationUnitLevel level )
    {
        Map map = new Map();

        map.setName( "Map" + uniqueCharacter );
        map.setOrganisationUnit( unit );
        map.setOrganisationUnitLevel( level );
        map.setMapLayerPath( "MapLayerPath" + uniqueCharacter );
        map.setNameColumn( "NameColumn" + uniqueCharacter );
        map.setLongitude( "Longitude" + uniqueCharacter );
        map.setLatitude( "Latitude" + uniqueCharacter );
        map.setZoom( 1 );
        map.setStaticMapLayerPaths( new HashSet<String>() );

        return map;
    }

    public static MapLegendSet createMapLegendSet( char uniqueCharacter, Indicator... indicators )
    {
        MapLegendSet legendSet = new MapLegendSet();

        legendSet.setName( "MapLegendSet" + uniqueCharacter );
        legendSet.setColorLow( "ColorLow" + uniqueCharacter );
        legendSet.setColorHigh( "ColorHigh" + uniqueCharacter );

        for ( Indicator indicator : indicators )
        {
            legendSet.getIndicators().add( indicator );
        }

        return legendSet;
    }

    public static Chart createChart( char uniqueCharacter, List<Indicator> indicators, List<Period> periods,
        List<OrganisationUnit> units )
    {
        Chart chart = new Chart();

        chart.setTitle( "Chart" + uniqueCharacter );
        chart.setDimension( Chart.DIMENSION_PERIOD );
        chart.setIndicators( indicators );
        chart.setPeriods( periods );
        chart.setOrganisationUnits( units );

        return chart;
    }

    public static OlapURL createOlapURL( char uniqueCharacter )
    {
        OlapURL olapURL = new OlapURL();

        olapURL.setName( "OlapURL" + uniqueCharacter );
        olapURL.setUrl( "URL" + uniqueCharacter );

        return olapURL;
    }

    public static User createUser( char uniqueCharacter )
    {
        User user = new User();

        user.setFirstName( "FirstName" + uniqueCharacter );
        user.setSurname( "Surname" + uniqueCharacter );
        user.setEmail( "Email" + uniqueCharacter );
        user.setPhoneNumber( "PhoneNumber" + uniqueCharacter );

        return user;
    }

    public static ProgramStage createProrgamStage( char uniqueCharacter, int stage, int minDays )
    {
        ProgramStage programStage = new ProgramStage();

        programStage.setName( "name" + uniqueCharacter );
        programStage.setDescription( "description" + uniqueCharacter );
        programStage.setStageInProgram( stage );
        programStage.setMinDaysFromStart( minDays );        

        return programStage;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static ExtendedDataElement createExtendedElement( char uniqueCharacter )
    {
        ExtendedDataElement extended = new ExtendedDataElement();

        extended.setMnemonic( "Mnemonic" + uniqueCharacter );
        extended.setVersion( "Version" + uniqueCharacter );
        extended.setContext( "Context" + uniqueCharacter );
        extended.setSynonyms( "Synonyms" + uniqueCharacter );
        extended.setHononyms( "Hononyms" + uniqueCharacter );
        extended.setKeywords( "Keywords" + uniqueCharacter );
        extended.setStatus( ExtendedDataElement.STATUS_CURRENT );
        extended.setStatusDate( date );
        extended.setDataElementType( ExtendedDataElement.TYPE_DATAELEMENT );

        extended.setDataType( ExtendedDataElement.DATATYPE_ALPHABETIC );
        extended.setRepresentationalForm( ExtendedDataElement.REPRESENTATIONAL_FORM_CODE );
        extended.setRepresentationalLayout( "RepresentationalLayout" + uniqueCharacter );
        extended.setMinimumSize( 0 );
        extended.setMaximumSize( 10 );
        extended.setDataDomain( "DataDomain" + uniqueCharacter );
        extended.setValidationRules( "ValidationRules" + uniqueCharacter );
        extended.setRelatedDataReferences( "RelatedDataReferences" + uniqueCharacter );
        extended.setGuideForUse( "GuideForUse" + uniqueCharacter );
        extended.setCollectionMethods( "CollectionMethods" + uniqueCharacter );

        extended.setResponsibleAuthority( "ResponsibleAuthority" + uniqueCharacter );
        extended.setUpdateRules( "UpdateRules" + uniqueCharacter );
        extended.setAccessAuthority( "AccessAuthority" + uniqueCharacter );
        extended.setUpdateFrequency( "UpdateFrequency" + uniqueCharacter );
        extended.setLocation( "Location" + uniqueCharacter );
        extended.setReportingMethods( "ReportingMethods" + uniqueCharacter );
        extended.setVersionStatus( "VersionStatus" + uniqueCharacter );
        extended.setPreviousVersionReferences( "PreviousVersionReferences" + uniqueCharacter );
        extended.setSourceDocument( "SourceDocument" + uniqueCharacter );
        extended.setSourceOrganisation( "SourceOrganisation" + uniqueCharacter );
        extended.setComment( "Comment" + uniqueCharacter );
        extended.setSaved( date );
        extended.setLastUpdated( date );

        return extended;
    }

    /**
     * Injects the externalDir property of LocationManager to
     * user.home/dhis2_test_dir. LocationManager dependency must be retrieved
     * from the context up front.
     * 
     * @param locationManager The LocationManager to be injected with the
     *        external directory.
     */
    public void setExternalTestDir( LocationManager locationManager )
    {
        this.locationManager = locationManager;

        setDependency( locationManager, "externalDir", EXT_TEST_DIR, String.class );
    }

    /**
     * Attempts to remove the external test directory.
     */
    public void removeExternalTestDir()
    {
        deleteDir( new File( EXT_TEST_DIR ) );
    }

    private boolean deleteDir( File dir )
    {
        if ( dir.isDirectory() )
        {
            String[] children = dir.list();

            for ( int i = 0; i < children.length; i++ )
            {
                boolean success = deleteDir( new File( dir, children[i] ) );

                if ( !success )
                {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}
