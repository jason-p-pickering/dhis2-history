package org.hisp.dhis.light.interpretation.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Paul Mark Castillo
 */
public class GetInterpretations
    implements Action, Comparator<Interpretation>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private InterpretationService interpretationService;

    public void setInterpretationService( InterpretationService interpretationService )
    {
        this.interpretationService = interpretationService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Interpretation> interpretations;

    public List<Interpretation> getInterpretations()
    {
        return interpretations;
    }

    public void setInterpretations( List<Interpretation> interpretations )
    {
        this.interpretations = interpretations;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        List<Interpretation> tempInterpretations = interpretationService.getInterpretations();

        List<Interpretation> finalInterpretations = new ArrayList<Interpretation>();

        Iterator<Interpretation> i = tempInterpretations.iterator();
    
        while ( i.hasNext() )
        {
            Interpretation currentInterpretation = i.next();

            if ( currentInterpretation.getType().equals( Interpretation.TYPE_CHART ) )
            {
                finalInterpretations.add( currentInterpretation );
            }
        }

        Collections.sort( finalInterpretations, this );

        setInterpretations( finalInterpretations );

        return SUCCESS;
    }

    @Override
    public int compare( Interpretation o1, Interpretation o2 )
    {
        long time1 = o1.getCreated().getTime();
        long time2 = o2.getCreated().getTime();

        if ( time1 > time2 )
        {
            return -1;
        }
        else if ( time2 < time1 )
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}