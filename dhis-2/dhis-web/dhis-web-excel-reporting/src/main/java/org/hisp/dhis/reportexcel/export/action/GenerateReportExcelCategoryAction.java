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
package org.hisp.dhis.reportexcel.export.action;

import java.util.Collection;

import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.hisp.dhis.reportexcel.ReportExcelCategory;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class GenerateReportExcelCategoryAction
    extends GenerateReportExcelSupport
{

    public String execute()
        throws Exception
    {
        statementManager.initialise();

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        Period period = selectionManager.getSelectedPeriod();
        this.installExcelFormat();
        this.installPeriod( period );

        ReportExcelCategory reportExcel = (ReportExcelCategory) reportService.getReportExcel( selectionManager
            .getSelectedReportExcelId() );

        this.installReadTemplateFile( reportExcel, period, organisationUnit );

        for ( Integer sheetNo : reportService.getSheets( selectionManager.getSelectedReportExcelId() ) )
        {
            WritableSheet sheet = outputReportWorkbook.getSheet( sheetNo - 1 );

            Collection<ReportExcelItem> reportExcelItems = reportService.getReportExcelItem( sheetNo, selectionManager
                .getSelectedReportExcelId() );

            this.generateOutPutFile( organisationUnit, reportExcelItems, reportExcel, sheet );

        }
     
        this.complete();

        statementManager.destroy();

        return SUCCESS;
    }

    private void generateOutPutFile( OrganisationUnit organisationUnit, Collection<ReportExcelItem> reportExcelItems,
        ReportExcelCategory reportExcel, WritableSheet sheet )
        throws RowsExceededException, WriteException
    {

        for ( ReportExcelItem reportItem : reportExcelItems )
        {
            int rowBegin = reportItem.getRow();

            for ( DataElementGroupOrder dataElementGroup : reportExcel.getDataElementOrders() )
            {

                int beginChapter = rowBegin;
                
                if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT_NAME ) )
                {
                    ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), dataElementGroup.getName(),
                        ExcelUtils.TEXT, sheet, this.textChapterLeft );
                }
                else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT_CODE ) )
                {
                    ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), dataElementGroup.getCode(),
                        ExcelUtils.TEXT, sheet, this.textChapterLeft );
                }

                rowBegin++;
                
                int serial = 1;
                
                for ( DataElement dataElement : dataElementGroup.getDataElements() )
                {

                    if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT_NAME ) )
                    {
                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), dataElement.getName(),
                            ExcelUtils.TEXT, sheet, this.textLeft );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT_CODE ) )
                    {
                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), dataElement.getCode(),
                            ExcelUtils.TEXT, sheet, this.textLeft );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.SERIAL ) )
                    {
                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), String.valueOf( serial ),
                            ExcelUtils.NUMBER, sheet, this.number );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.FORMULA_EXCEL ) )
                    {
                        ExcelUtils.writeFormula( rowBegin, reportItem.getColumn(), reportItem.getExpression(), sheet,
                            this.number );
                    }
                    else
                    {
                        ReportExcelItem newReportItem = new ReportExcelItem();
                        newReportItem.setColumn( reportItem.getColumn() );
                        newReportItem.setRow( reportItem.getRow() );
                        newReportItem.setPeriodType( reportItem.getPeriodType() );
                        newReportItem.setName( reportItem.getName() );
                        newReportItem.setSheetNo( reportItem.getSheetNo() );
                        newReportItem.setItemType( reportItem.getItemType() );

                        String expression = reportItem.getExpression();
                        expression = expression.replace( "*", String.valueOf( dataElement.getId() ) );
                        newReportItem.setExpression( expression );

                        double value = this.getDataValue( newReportItem, organisationUnit );

                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, this.number );
                    }
                    rowBegin++;
                    serial++;
                }

                if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT ) )
                {
                    String columnName = ExcelUtils.convertColNumberToColName( reportItem.getColumn() );
                    String formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName + (rowBegin - 1) + ")";
                    ExcelUtils.writeFormula( beginChapter, reportItem.getColumn(), formula, sheet, this.number );
                }
            }
        }
    }

}
