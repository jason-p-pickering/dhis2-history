package org.hisp.dhis.dxf2.pdfform;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hisp.dhis.dxf2.datavalueset.DataValueSet;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Document;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.AcroFields.Item;
import com.lowagie.text.pdf.PdfReader;


public class PdfDataEntryFormUtil {

    // public Static Values
    public static final int DATATYPE_DATASET = 0;

    public static final int DATATYPE_PROGRAMSTAGE = 1;

    // Label Names
    public static final String LABELCODE_TEXTFIELD = "TXFD_";

    public static final String LABELCODE_BUTTON = "BTNFD_";

    public static final String LABELCODE_ORGID = LABELCODE_TEXTFIELD + "OrgID";

    public static final String LABELCODE_PERIODID = LABELCODE_TEXTFIELD + "PeriodID";

    public static final String LABELCODE_BUTTON_SAVEAS = LABELCODE_BUTTON + "SaveAs";

    public static final String LABELCODE_DATADATETEXTFIELD = "TXFDDT_";

    public static final String LABELCODE_DATAENTRYTEXTFIELD = "TXFDDV_";

    public static final String LABELCODE_PROGRAMSTAGEIDTEXTBOX = "TXPSTGID_";

    
    // private static values
    private static final String DATAVALUE_IMPORT_STOREBY = "admin";

    private static final String DATAVALUE_IMPORT_COMMENT = "Imported by PDF Data Entry Form";

    private static final String DATAVALUE_IMPORT_TIMESTAMP_DATEFORMAT = "yyyy-MM-dd";

    private static final String FOOTERTEXT_DEFAULT = "PDF Template generated from DHIS %s on %s";

    private static final String DATEFORMAT_FOOTER_DEFAULT = "MMMM dd, yyyy";
    
    
    // -------------------------------------------------------------------------
    // METHODS
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // --- Document Setting Related [START]

    public static void setFooterOnDocument(Document document, String footerText, Font font)
    {
      boolean isNumbered = true;

      HeaderFooter footer = new HeaderFooter( new Phrase( footerText, font ), isNumbered );
      footer.setBorder( Rectangle.NO_BORDER );
      footer.setAlignment( Element.ALIGN_RIGHT );
      document.setFooter( footer );
                
    }
    
    // Set DefaultFooter
    public static void setDefaultFooterOnDocument( Document document, String serverName, Font font )
    {
        // Set Footer
        String strFooterText = String.format( FOOTERTEXT_DEFAULT, serverName, (new SimpleDateFormat(
            DATEFORMAT_FOOTER_DEFAULT )).format( new Date() ) );

        setFooterOnDocument( document, strFooterText, font );

    }
    
    public static Rectangle getDefaultPageSize(int typeId)
    {

        if ( typeId == PdfDataEntryFormUtil.DATATYPE_PROGRAMSTAGE )
        {
            return new Rectangle( PageSize.A4.getLeft(),
                PageSize.A4.getBottom(), PageSize.A4.getTop(), PageSize.A4.getRight() );
        }        
        else
        {
            return PageSize.A4;
        }
        
    }
            
    // --- Document Setting Related [END]
    // -------------------------------------------------------------------------
    
    
    
    // Retreive DataValue Informations from PDF inputStream.
    public static DataValueSet getDataValueSet( InputStream in )
        throws RuntimeException
    {
        PdfReader reader = null;

        DataValueSet dataValueSet = new DataValueSet();

        List<org.hisp.dhis.dxf2.datavalue.DataValue> dataValueList = new ArrayList<org.hisp.dhis.dxf2.datavalue.DataValue>();

        try
        {

            reader = new PdfReader( in ); // new PdfReader(in, null);

            AcroFields form = reader.getAcroFields();

            if ( form != null )
            {

                // TODO: MOVE THESE STATIC NAME VALUES TO inside of service
                // class or PDFForm Class <-- should be in PDFForm Class.
                String strOrgUID = form.getField( PdfDataEntryFormUtil.LABELCODE_ORGID );
                String strPeriodID = form.getField( PdfDataEntryFormUtil.LABELCODE_PERIODID );

                Period period = PeriodType.createPeriodExternalId( strPeriodID );

                // Loop Through the Fields and get data.
                HashMap<String, AcroFields.Item> fields = form.getFields();
                Set<Entry<String, Item>> entrySet = fields.entrySet();

                Set<String> fldNames = form.getFields().keySet();

                for ( String fldName : fldNames )
                {

                    if ( fldName.startsWith( PdfDataEntryFormUtil.LABELCODE_DATAENTRYTEXTFIELD ) )
                    {

                        String[] strArrFldName = fldName.split( "_" );

                        // Create DataValues to be put in a DataValueSet
                        org.hisp.dhis.dxf2.datavalue.DataValue dataValue = new org.hisp.dhis.dxf2.datavalue.DataValue();

                        dataValue.setDataElement( strArrFldName[1] );
                        dataValue.setCategoryOptionCombo( strArrFldName[2] );
                        dataValue.setOrgUnit( strOrgUID );
                        dataValue.setPeriod( period.getIsoDate() );

                        dataValue.setValue( form.getField( fldName ) );

                        dataValue.setStoredBy( DATAVALUE_IMPORT_STOREBY );
                        dataValue.setComment( DATAVALUE_IMPORT_COMMENT );
                        dataValue.setFollowup( false );
                        dataValue.setTimestamp( new SimpleDateFormat( DATAVALUE_IMPORT_TIMESTAMP_DATEFORMAT )
                            .format( new Date() ) );

                        dataValueList.add( dataValue );

                    }
                }

                dataValueSet.setDataValues( dataValueList );

            }
            else
            {
                throw new RuntimeException( "Could not generate PDF AcroFields form from the file." );
            }

        }
        catch ( Exception e )
        {
            throw new RuntimeException( e.getMessage() );
        }
        finally
        {
            reader.close();
        }

        return dataValueSet;
    }


    // -----------------------------------------------------------------------------
    // --- For Import - ProgramStage [START]

    
    // --- For Import - ProgramStage [END]
    // -----------------------------------------------------------------------------


}
