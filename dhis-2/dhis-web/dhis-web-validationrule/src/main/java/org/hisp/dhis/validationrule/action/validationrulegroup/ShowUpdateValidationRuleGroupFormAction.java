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

package org.hisp.dhis.validationrule.action.validationrulegroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
import org.hisp.dhis.validation.comparator.ValidationRuleNameComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ ShowUpdateValidationRuleGroupFormAction.java May 31, 2011 11:23:59
 *          AM $
 * 
 */
public class ShowUpdateValidationRuleGroupFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private ValidationRuleGroup validationRuleGroup;

    public ValidationRuleGroup getValidationRuleGroup()
    {
        return validationRuleGroup;
    }

    private List<ValidationRule> groupMembers = new ArrayList<ValidationRule>();

    public List<ValidationRule> getGroupMembers()
    {
        return groupMembers;
    }

    private List<ValidationRule> availableValidationRules = new ArrayList<ValidationRule>();

    public List<ValidationRule> getAvailableValidationRules()
    {
        return availableValidationRules;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Get selected group
        // ---------------------------------------------------------------------

        validationRuleGroup = validationRuleService.getValidationRuleGroup( id );

        groupMembers = new ArrayList<ValidationRule>( validationRuleGroup.getMembers() );

        Collections.sort( groupMembers, new ValidationRuleNameComparator() );

        return SUCCESS;
    }

}
