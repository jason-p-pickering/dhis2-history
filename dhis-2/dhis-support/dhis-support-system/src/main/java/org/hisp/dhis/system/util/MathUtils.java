package org.hisp.dhis.system.util;

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

import java.util.Scanner;

import org.nfunk.jep.JEP;

/**
 * @author Lars Helge Overland
 * @version $Id: MathUtil.java 4712 2008-03-12 10:32:45Z larshelg $
 */
public class MathUtils
{
    public static final double INVALID = -1.0;
    
    /**
     * Validates whether an expression is true or false.
     * 
     * @param leftSide The left side of the expression.
     * @param operator The expression operator.
     * @param rightSide The right side of the expression.
     * @return True if the expressio is true, fals otherwise.
     */
    public static boolean expressionIsTrue( double leftSide, String operator, double rightSide )
    {
        final String expression = leftSide + operator + rightSide;
        
        final JEP parser = new JEP();
        
        parser.parseExpression( expression );
        
        return ( parser.getValue() == 1.0 );
    }
    
    /** 
     * Calculates a regular mathematical expression.
     * 
     * @param expression The expression to calculate.
     * @return The result of the operation.
     */
    public static double calculateExpression( String expression )   
    {
        final JEP parser = new JEP();
        
        parser.parseExpression( expression );
        
        double result = parser.getValue();
        
        return ( result == Double.NEGATIVE_INFINITY || result == Double.POSITIVE_INFINITY ) ? INVALID : result;       
    }
    
    /**
     * Investigates whether the expression is valid or has errors.
     * 
     * @param expression The expression to validate.
     * @return True if the expression has errors, false otherwise.
     */
    public static boolean expressionHasErrors( String expression )
    {
        final JEP parser = new JEP();
        
        parser.parseExpression( expression );
        
        return parser.hasError();
    }
    
    /**
     * Returns the error information for an invalid expression.
     * 
     * @param expression The expression to validate.
     * @return The error information for an invalid expression, null if
     *         the expression is valid.
     */
    public static String getExpressionErrorInfo( String expression )
    {
        final JEP parser = new JEP();
        
        parser.parseExpression( expression );
        
        return parser.getErrorInfo();
    }
    
    /**
     * Rounds off downwards to the next distinct value.
     * 
     * @param value The value to round off
     * @return The rounded value
     */
    public static double getFloor( double value )
    {
        return Math.floor( value );
    }
    
    /**
     * Returns a number rounded off to the given number of decimals.
     * 
     * @param value the value to round off.
     * @param decimals the number of decimals.
     * @return a number rounded off to the given number of decimals.
     */
    public static double getRounded( double value, int decimals )
    {
        final double factor = Math.pow( 10, decimals );
        
        return Math.round( value * factor ) / factor;
    }
    
    /**
     * Returns the given number if larger or equal to minimun, otherwise minimum.
     * 
     * @param number the number.
     * @param min the minimum.
     * @return the given number if larger or equal to minimun, otherwise minimum.
     */
    public static int getMin( int number, int min )
    {
        return number < min ? min : number;
    }

    /**
     * Returns the given number if smaller or equal to maximum, otherwise maximum.
     * 
     * @param number the number.
     * @param max the maximum.
     * @return the the given number if smaller or equal to maximum, otherwise maximum.
     */
    public static int getMax( int number, int max )
    {
        return number > max ? max : number;
    }
    
    /**
     * Returns true if the provided String argument can be converted to a Double,
     * false if not.
     * 
     * @param value the value.
     * @return true if the provided String argument can be converted to a Double.
     */
    public static boolean isNumeric( String value )
    {
        return new Scanner( value ).hasNextDouble();
    }
}
