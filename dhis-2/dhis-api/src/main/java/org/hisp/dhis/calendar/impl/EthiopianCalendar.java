package org.hisp.dhis.calendar.impl;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import org.hisp.dhis.calendar.Calendar;
import org.hisp.dhis.calendar.ChronologyBasedCalendar;
import org.hisp.dhis.calendar.DateTimeUnit;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.EthiopicChronology;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class EthiopianCalendar extends ChronologyBasedCalendar
{
    private static final Calendar self = new EthiopianCalendar();

    public static Calendar getInstance()
    {
        return self;
    }

    protected EthiopianCalendar()
    {
        super( EthiopicChronology.getInstance( DateTimeZone.getDefault() ) );
    }

    @Override
    public String name()
    {
        return "ethiopian";
    }

    @Override
    public DateTimeUnit toIso( DateTimeUnit dateTimeUnit )
    {
        if ( dateTimeUnit.getMonth() > 12 )
        {
            throw new RuntimeException( "Illegal month, must be between 1 and 12, was given " + dateTimeUnit.getMonth() );
        }

        return super.toIso( dateTimeUnit );
    }

    @Override
    public DateTimeUnit fromIso( Date date )
    {
        DateTimeUnit dateTimeUnit = super.fromIso( date );

        if ( dateTimeUnit.getMonth() > 12 )
        {
            throw new RuntimeException( "Illegal month, must be between 1 and 12, was given " + dateTimeUnit.getMonth() );
        }

        return dateTimeUnit;
    }

    @Override
    public DateTimeUnit fromIso( DateTimeUnit dateTimeUnit )
    {
        return super.fromIso( dateTimeUnit );
    }

    @Override
    public DateTimeUnit plusDays( DateTimeUnit dateTimeUnit, int days )
    {
        dateTimeUnit = super.plusDays( dateTimeUnit, days );

        if ( dateTimeUnit.getMonth() > 12 )
        {
            dateTimeUnit.setYear( dateTimeUnit.getYear() + 1 );
            dateTimeUnit.setMonth( 1 );
            dateTimeUnit.setDay( 1 );
        }

        return dateTimeUnit;
    }

    @Override
    public DateTimeUnit plusMonths( DateTimeUnit dateTimeUnit, int months )
    {
        dateTimeUnit = super.plusMonths( dateTimeUnit, months );

        if ( dateTimeUnit.getMonth() > 12 )
        {
            dateTimeUnit.setYear( dateTimeUnit.getYear() + 1 );
            dateTimeUnit.setMonth( 1 );
        }

        return dateTimeUnit;
    }

    @Override
    public int daysInYear( int year )
    {
        return 12 * 30;
    }

    @Override
    public int daysInMonth( int year, int month )
    {
        if ( month > 12 )
        {
            throw new RuntimeException( "Illegal month, must be between 1 and 12, was given " + month );
        }

        return 30;
    }
}
