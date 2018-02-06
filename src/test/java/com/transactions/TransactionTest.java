package com.transactions;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class TransactionTest {

public static double delta = 0.00001;

@Test
    public void createTransaction_shouldReturnCorrectTransaction() throws InvalidTransactionException {
        String [] input = {"NEAA0000","20170101","DEP","100","1","CASH"};
        Transaction t = new Transaction(input);
        assertEquals(t.getAccount(),"NEAA0000");
        assertEquals(t.getDate(), LocalDate.parse("20170101", Transaction.formatter));
        assertEquals(t.getTxnType(), Transaction.txn.valueOf("DEP"));
        assertEquals(t.getUnits(), (double) Double.valueOf("100"), delta);
        assertEquals(t.getPrice(), (double) Double.valueOf("1"), delta);
        assertEquals(t.getAsset(),"CASH");
    }

    @Test(expected = InvalidTransactionException.class)
    public void createTransaction_shouldThrowExceptionIfIncorrectNumberOfArguments() throws InvalidTransactionException {
        String [] input = {"NEAA000","20080512","DEP","1","CASH"};
        Transaction t = new Transaction(input);
    }

    @Test(expected = InvalidTransactionException.class)
    public void createTransaction_shouldThrowExceptionIfInvalidDate() throws InvalidTransactionException {
        String [] input = {"NEAA0000","12081993","DEP","100","1","CASH"};
        Transaction t = new Transaction(input);
    }


    @Test(expected = InvalidTransactionException.class)
    public void createTransaction_shouldThrowExceptionIfInvalidTransactionType() throws InvalidTransactionException {
        String [] input = {"NEAA0000","20080512","ACW","100","1","CASH"};
        Transaction t = new Transaction(input);
    }

    @Test(expected = InvalidTransactionException.class)
    public void createTransaction_shouldThrowExceptionIfPriceNotNumerical() throws InvalidTransactionException {
        String [] input = {"NEAA0000","20080512","ACW","A","1","CASH"};
        Transaction t = new Transaction(input);
    }

    @Test(expected = InvalidTransactionException.class)
    public void createTransaction_shouldThrowExceptionIfUnitsNotDouble() throws InvalidTransactionException {
        String [] input = {"NEAA0000","20080512","ACW","100","32.232..","CASH"};
        Transaction t = new Transaction(input);
    }

    @Test
    public void validateTransaction_shouldNotThrowErrorIfValid() throws InvalidTransactionException {
        String [] input = {"NEAA0000","20170101","WDR","100","1","CASH"};
        Transaction t = new Transaction(input);
        t.validateTransaction();
    }

    @Test(expected = InvalidTransactionException.class)
    public void validateTransaction_shouldThrowExceptionIfInvalidTxnTypeAndTransaction() throws InvalidTransactionException{
        String [] input = {"NEAA0000","20170101","WDR","100","1","VISA"};
        Transaction t = new Transaction(input);
        t.validateTransaction();
    }

    @Test(expected = InvalidTransactionException.class)
    public void validateTransaction_shouldThrowExceptionIfNegativePrice() throws InvalidTransactionException{
        String [] input = {"NEAA0000","20170101","DIV","100","-1.9","VISA"};
        Transaction t = new Transaction(input);
        t.validateTransaction();
    }

    @Test(expected = InvalidTransactionException.class)
    public void validateTransaction_shouldThrowExceptionIfNegativeUnits() throws InvalidTransactionException{
        String [] input = {"NEAA0000","20170101","BOT","-112.0","1.012","VUKE"};
        Transaction t = new Transaction(input);
        t.validateTransaction();
    }

    @Test(expected = InvalidTransactionException.class)
    public void validateTransaction_shouldThrowExceptionIfEmptyAccountName() throws InvalidTransactionException{
        String [] input = {"","20170101","SLD","100","1","VIHA"};
        Transaction t = new Transaction(input);
        t.validateTransaction();
    }

    @Test(expected = InvalidTransactionException.class)
    public void validateTransaction_shouldThrowExceptionIfEmptyAsset() throws InvalidTransactionException{
        String [] input = {"NEAA0000","20170101","SLD","100","1",""};
        Transaction t = new Transaction(input);
        t.validateTransaction();
    }

}