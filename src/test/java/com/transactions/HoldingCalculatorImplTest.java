package com.transactions;

import java.time.LocalDate;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HoldingCalculatorImplTest {

    public static double delta = 0.00001;

    @Test
    public void parseTransaction_shouldCreateExpectedTransaction() throws InvalidTransactionException {
        String transactionString = "NEAA0000,20100228,BOT,100.231,23,TEST";
        Transaction t = HoldingCalculatorImpl.parseTransaction(transactionString);
        assertEquals(t.getAccount(),"NEAA0000");
        assertEquals(t.getAsset(),"TEST");
        assertEquals(t.getDate(),LocalDate.parse("20100228", Transaction.formatter));
        assertEquals(t.getPrice(), (double) Double.valueOf("23"),delta);
        assertEquals(t.getUnits(), (double) Double.valueOf("100.231"),delta);
        assertEquals(t.getTxnType(), Transaction.txn.valueOf("TEST"));
    }

    @Test(expected = InvalidTransactionException.class)
    public void parseTransaction_shouldThrowExceptionWhenEmptyString() throws InvalidTransactionException {
        String transactionString = "";
        Transaction t = HoldingCalculatorImpl.parseTransaction(transactionString);

    }

    @Test(expected = InvalidTransactionException.class)
    public void parseTransaction_shouldThrowExceptionWhenInvalidString() throws InvalidTransactionException {
        String transactionString = "NEAA0000;20100228;BOT;100.231;23;TEST";
        Transaction t = HoldingCalculatorImpl.parseTransaction(transactionString);

    }

}
