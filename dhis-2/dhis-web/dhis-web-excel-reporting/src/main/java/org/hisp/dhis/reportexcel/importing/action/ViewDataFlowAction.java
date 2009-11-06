package org.hisp.dhis.reportexcel.importing.action;

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
import java.util.ArrayList;
import java.util.Collections;

import org.hisp.dhis.reportexcel.ReportLocationManager;
import org.hisp.dhis.reportexcel.excelitem.ExcelItem;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;
import org.hisp.dhis.reportexcel.excelitem.comparator.ExcelItemComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class ViewDataFlowAction implements Action {
	// --------------------------------------------------------------------
	// Dependencies
	// --------------------------------------------------------------------

	private ExcelItemService excelItemService;

	private ReportLocationManager reportLocationManager;

	// --------------------------------------------------------------------
	// Inputs && Outputs
	// --------------------------------------------------------------------

	private Integer excelItemGroupId;

	private String uploadFileName;

	private ArrayList<ExcelItem> excelItems;

	private File upload;

	private ExcelItemGroup excelItemGroup;

	// --------------------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------------------

	public void setExcelItemService(ExcelItemService excelItemService) {
		this.excelItemService = excelItemService;
	}

	public File getUpload() {
		return upload;
	}

	public void setReportLocationManager(
			ReportLocationManager reportLocationManager) {
		this.reportLocationManager = reportLocationManager;
	}

	public ExcelItemGroup getExcelItemGroup() {
		return excelItemGroup;
	}

	public void setExcelItemGroupId(Integer excelItemGroupId) {
		this.excelItemGroupId = excelItemGroupId;
	}

	public ArrayList<ExcelItem> getExcelItems() {
		return excelItems;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	// --------------------------------------------------------------------
	// Action implementation
	// --------------------------------------------------------------------

	public String execute() {
		try {
			upload = new File(reportLocationManager
					.getReportExcelTempDirectory()
					+ File.separator + uploadFileName);

			excelItemGroup = excelItemService
					.getExcelItemGroup(excelItemGroupId);

			excelItems = new ArrayList<ExcelItem>(excelItemGroup
					.getExcelItems());

			Collections.sort(excelItems, new ExcelItemComparator());

			return excelItemGroup.getType();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ERROR;
	}

}
