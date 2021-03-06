/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime.functions.extended;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// based on the examples of calculations, stddev is supposed to return sample standard deviation, not population standard deviation
public class StddevFunction
        extends BaseFEELFunction {
    public static final StddevFunction INSTANCE = new StddevFunction();

    StddevFunction() {
        super("stddev");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") List<?> list) {
        if ( list == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "list", "the list cannot be null" ) );
        }
        int n = list.size();
        BigDecimal[] numbers = new BigDecimal[n];
        for ( int i = 0; i < n; i++ ) {
            final BigDecimal val = EvalHelper.getBigDecimalOrNull( list.get( i ) );
            if ( val == null ) {
                return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "list", "an element in the list is not suitable for the stddev" ) );
            }
            numbers[i] = val;
        }
        BigDecimal total = BigDecimal.ZERO;
        for ( int i = 0; i < n; i++ ) {
            total = total.add( numbers[i] );
        }
        BigDecimal mean = total.divide( BigDecimal.valueOf( n ), MathContext.DECIMAL128 );
        total = BigDecimal.ZERO;
        for ( int i = 0; i < n; i++ ) {
            BigDecimal distanceSquared = numbers[i].subtract( mean ).pow( 2, MathContext.DECIMAL128 );
            total = total.add( distanceSquared );
        }
        mean = total.divide( BigDecimal.valueOf( n - 1 ), MathContext.DECIMAL128 );
        return FEELFnResult.ofResult( SqrtFunction.sqrt( mean ) );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") Object sole) {
        if ( sole == null ) {
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "n", "the single value list cannot be null" ) );
        }
        return FEELFnResult.ofResult( BigDecimal.ZERO );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("n") Object[] list) {
        if ( list == null ) {
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "n", "the single value list cannot be null" ) );
        }

        return invoke( Arrays.asList( list ) );
    }
}
