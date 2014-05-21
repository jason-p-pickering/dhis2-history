package org.hisp.dhis.period;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import com.google.common.collect.Lists;
import org.hisp.dhis.calendar.DateUnit;

import java.util.Date;
import java.util.List;

/**
 * PeriodType for daily Periods. A valid daily Period has equal startDate and
 * endDate.
 *
 * @author Torgeir Lorange Ostby
 * @version $Id: DailyPeriodType.java 2971 2007-03-03 18:54:56Z torgeilo $
 */
public class DailyPeriodType
    extends CalendarPeriodType
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 5371766471215556241L;

    public static final String ISO_FORMAT = "yyyyMMdd";

    /**
     * The name of the DailyPeriodType, which is "Daily".
     */
    public static final String NAME = "Daily";

    public static final int FREQUENCY_ORDER = 1;

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Period createPeriod( DateUnit dateUnit )
    {
        return new Period( this, dateUnit.toJdkDate(), dateUnit.toJdkDate() );
    }

    @Override
    public int getFrequencyOrder()
    {
        return FREQUENCY_ORDER;
    }

    // -------------------------------------------------------------------------
    // CalendarPeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public Period getNextPeriod( Period period )
    {
        DateUnit dateUnit = createLocalDateUnitInstance( period.getStartDate() );
        dateUnit = getCalendar().plusDays( dateUnit, 1 );

        Date date = getCalendar().toIso( dateUnit ).toJdkDate();

        return new Period( this, date, date );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        DateUnit dateUnit = createLocalDateUnitInstance( period.getStartDate() );
        dateUnit = getCalendar().minusDays( dateUnit, 1 );

        Date date = getCalendar().toIso( dateUnit ).toJdkDate();

        return new Period( this, date, date );
    }

    /**
     * Generates daily Periods for the whole year in which the given Period's
     * startDate exists.
     */
    @Override
    public List<Period> generatePeriods( DateUnit dateUnit )
    {
        dateUnit.setMonth( 1 );
        dateUnit.setDay( 1 );

        List<Period> periods = Lists.newArrayList();

        int year = dateUnit.getYear();

        while ( year == dateUnit.getYear() )
        {
            periods.add( createPeriod( dateUnit ) );
            dateUnit = getCalendar().plusDays( dateUnit, 1 );
        }

        return periods;
    }

    /**
     * Generates the last 365 days where the last one is the day of the given
     * date.
     */
    @Override
    public List<Period> generateRollingPeriods( DateUnit dateUnit )
    {
        dateUnit = getCalendar().minusDays( dateUnit, 364 );

        List<Period> periods = Lists.newArrayList();

        for ( int i = 0; i < 365; i++ )
        {
            periods.add( createPeriod( dateUnit ) );
            dateUnit = getCalendar().plusDays( dateUnit, 1 );
        }

        return periods;
    }

    @Override
    public String getIsoDate( DateUnit dateUnit )
    {
        return String.format( "%d%02d%02d", dateUnit.getYear(), dateUnit.getMonth(), dateUnit.getDay() );
    }

    @Override
    public String getIsoFormat()
    {
        return ISO_FORMAT;
    }

    @Override
    public Date getRewindedDate( Date date, Integer rewindedPeriods )
    {
        date = date != null ? date : new Date();
        rewindedPeriods = rewindedPeriods != null ? rewindedPeriods : 1;

        DateUnit dateUnit = createLocalDateUnitInstance( date );
        dateUnit = getCalendar().minusDays( dateUnit, rewindedPeriods );

        return getCalendar().toIso( dateUnit ).toJdkDate();
    }
}
