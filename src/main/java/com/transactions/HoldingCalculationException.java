package com.transactions;

/**
 * Unchecked exception class, thrown by calculateHoldings method (in HoldingCalculatorImpl) if an error is detected that
 * it cannot recover from
 */

public class HoldingCalculationException extends RuntimeException {
    HoldingCalculationException(String message){
        super(message);
    }
}
