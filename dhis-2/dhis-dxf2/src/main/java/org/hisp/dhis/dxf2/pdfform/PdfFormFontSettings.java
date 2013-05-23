package org.hisp.dhis.dxf2.pdfform;

import java.awt.Color;

import com.lowagie.text.Font;


public class PdfFormFontSettings
{
    // Font Types
    public final static int FONTTYPE_BODY = 0;

    public final static int FONTTYPE_TITLE = 1;

    public final static int FONTTYPE_DESCRIPTION = 2;

    public final static int FONTTYPE_SECTIONHEADER = 3;

    public final static int FONTTYPE_FOOTER = 4;

    // Font Default Sizes
    private final static float FONTSIZE_BODY = 10;

    private final static float FONTSIZE_TITLE = 16;

    private final static float FONTSIZE_DESCRIPTION = 11;

    private final static float FONTSIZE_SECTIONHEADER = 14;

    private final static float FONTSIZE_FOOTER = 8;

    // Font Family Default
    private final static String FONTFAMILY = "HELVETICA";

    // private variables
    private Font fontBody;

    private Font fontTitle;

    private Font fontDescription;

    private Font fontSectionHeader;

    private Font fontFooter;

    public PdfFormFontSettings()
    {
        fontBody = createFont( FONTTYPE_BODY );
        fontTitle = createFont( FONTTYPE_TITLE );
        fontDescription = createFont( FONTTYPE_DESCRIPTION );
        fontSectionHeader = createFont( FONTTYPE_SECTIONHEADER );
        fontFooter = createFont( FONTTYPE_FOOTER );
    }

    public void setFont( int fontType, Font font )
    {

        switch ( fontType )
        {
        case FONTTYPE_BODY:
            fontBody = font;
            break;
        case FONTTYPE_TITLE:
            fontTitle = font;
            break;
        case FONTTYPE_DESCRIPTION:
            fontDescription = font;
            break;
        case FONTTYPE_SECTIONHEADER:
            fontSectionHeader = font;
            break;
        case FONTTYPE_FOOTER:
            fontFooter = font;
            break;
        }

    }

    public Font getFont( int fontType )
    {
        Font font = null;

        switch ( fontType )
        {
        case FONTTYPE_BODY:
            font = fontBody;
            break;
        case FONTTYPE_TITLE:
            font = fontTitle;
            break;
        case FONTTYPE_DESCRIPTION:
            font = fontDescription;
            break;
        case FONTTYPE_SECTIONHEADER:
            font = fontSectionHeader;
            break;
        case FONTTYPE_FOOTER:
            font = fontFooter;
            break;
        }

        return font;
    }

    private Font createFont( int fontType )
    {
        Font font = new Font();
        font.setFamily( FONTFAMILY );

        switch ( fontType )
        {
        case FONTTYPE_BODY:
            font.setSize( FONTSIZE_BODY );
            break;
        case FONTTYPE_TITLE:
            font.setSize( FONTSIZE_TITLE );
            font.setStyle( java.awt.Font.BOLD );
            font.setColor( new Color( 0, 0, 128 ) ); // Navy Color
            break;
        case FONTTYPE_DESCRIPTION:
            font.setSize( FONTSIZE_DESCRIPTION );
            font.setColor( Color.DARK_GRAY );
            break;
        case FONTTYPE_SECTIONHEADER:
            font.setSize( FONTSIZE_SECTIONHEADER );
            font.setStyle( java.awt.Font.BOLD );
            font.setColor( new Color( 70, 130, 180 ) ); // Steel Blue Color
            break;
        case FONTTYPE_FOOTER:
            font.setSize( FONTSIZE_FOOTER );
            break;
        default:
            font.setSize( FONTSIZE_BODY );
            break;
        }

        return font;

    }

}
