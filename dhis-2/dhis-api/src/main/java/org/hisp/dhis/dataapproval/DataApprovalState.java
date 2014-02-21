package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

/**
 * Current status of data approval for a selected combination of data set, period,
 * organisation unit, and category options or category group options.
 *
 * @author Jim Grace
 * @version $Id$
 */

public enum DataApprovalState
{
    /**
     * Data approval does not apply to this selection. (Data is neither
     * "approved" nor "unapproved".)
     */
    UNAPPROVABLE ( false, false, false, false ),

    /**
     * Data is unapproved, and is ready to be approved for this selection.
     */
    UNAPPROVED_READY ( false, true, true, true ),

    /**
     * Data is unapproved, and is waiting for some lower-level approval.
     */
    UNAPPROVED_WAITING ( false, true, true, false ),

    /**
     * Data is unapproved, and is waiting for approval somewhere else
     * (not approvable here.)
     */
    UNAPPROVED_ELSEWHERE ( false, true, false, false ),

    /**
     * Data is approved, and was approved here (so could be unapproved here.)
     */
    APPROVED_HERE ( true, false, true, false ),

    /**
     * Data is approved, but was not approved here (so cannot be unapproved here.)
     * This covers the case where data was approved at a higher level. It also
     * covers the case where the data is selected for a period type that is
     * longer than the data set period type, and the data was approved for
     * every constituent data set period.
     */
    APPROVED_ELSEWHERE( true, false, false, false );

    /**
     * Is this data approved (and therefore locked)?
     */
    private boolean approved;

    /**
     * Is this data unapproved (could be approved but is not)?
     */
    private boolean unapproved;

    /**
     * Is this data approvable for this selection?
     */
    private boolean approvable;

    /**
     * Is this data ready to be approved in this combination of data set, etc.?
     */
    private boolean ready;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    DataApprovalState( boolean approved, boolean unapproved, boolean approvable, boolean ready )
    {
        this.approved = approved;
        this.unapproved = unapproved;
        this.approvable = approvable;
        this.ready = ready;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public boolean isApproved()
    {
        return approved;
    }

    public boolean isUnapproved()
    {
        return unapproved;
    }

    public boolean isApprovable()
    {
        return approvable;
    }

    public boolean isReady()
    {
        return ready;
    }
}
