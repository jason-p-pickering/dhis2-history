package org.hisp.dhis.importexport.pdf.converter;

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

import java.util.Collection;

import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.PDFConverter;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.system.util.PDFUtils;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

/**
 * @author Lars Helge Overland
 * @version $Id: IndicatorConverter.java 4646 2008-02-26 14:54:29Z larshelg $
 * @modifier Dang Duy Hieu
 * @since 2010-05-19
 */
public class IndicatorConverter
    extends PDFUtils
    implements PDFConverter
{
    private IndicatorService indicatorService;

    private ExpressionService expressionService;

    public IndicatorConverter( IndicatorService indicatorService, ExpressionService expressionService )
    {
        this.indicatorService = indicatorService;
        this.expressionService = expressionService;
    }

    // -------------------------------------------------------------------------
    // PDFConverter implementation
    // -------------------------------------------------------------------------

    public void write( Document document, ExportParams params )
    {
        I18n i18n = params.getI18n();
        I18nFormat format = params.getFormat();

        PDFUtils.printIndicatorFrontPage( document, params.getIndicators(), i18n, format );

        Collection<Indicator> indicators = indicatorService.getIndicators( params.getIndicators() );

        BaseFont bf = getTrueTypeFontByDimension( BaseFont.IDENTITY_H );
        Font TEXT = new Font( bf, 9, Font.NORMAL );
        Font ITALIC = new Font( bf, 9, Font.ITALIC );
        Font HEADER3 = new Font( bf, 12, Font.BOLD );

        for ( Indicator indicator : indicators )
        {
            addTableToDocument( document, printIndicator( indicator, i18n, expressionService, HEADER3, ITALIC, TEXT,
                true, 0.40f, 0.60f ) );
        }
    }
}
