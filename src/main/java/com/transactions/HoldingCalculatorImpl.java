package com.transactions;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.DoubleAccumulator;

public class HoldingCalculatorImpl implements HoldingCalculator{

    private static final String OUTPUT_FILENAME = "holdings.txt";

    /**
     * Reads in transaction file, parses it, creates list of holdings for each account based on the transactions in the
     * transaction file, up to the date given as parameter
     *
     * @param transactionFile File input file containing information about transactions
     * @param date LocalDate date up to which calculate holdings
     * @return Map<String, List<Holding>> records holdings (value) for accounts (key)
     */
    @Override
    public Map<String, List<Holding>> calculateHoldings(File transactionFile, LocalDate date){
        //map recording holdings for each account name, to be returned
        Map<String, List<Holding>> holdings = new HashMap<>();
        List<String> transactions = null;
        //reads in transaction file
        try{
            readTransactionFile(transactionFile);
        }catch(IOException e){
            throw new HoldingCalculationException("Error: cannot read transaction file [" + transactionFile.getName() +
                    ", terminating");
        }
        //for every line in transaction file, parses line, creates and validates transaction, if transaction details
        // invalid then ignores transaction
        for (String line : transactions){
            List<Transaction> accountTransactions = new ArrayList<>();
            String previousAccount = "";
            Transaction t=null;
            try {
                t = parseTransaction(line);
            }catch(InvalidTransactionException e){
                continue;
            }
            //groups transactions by account name making use of fact that transactions are grouped by account name in
            // input file: if the transaction is linked to the same account as the previous transaction, puts it in a
            // list of transactions, once a transaction with a different account name is encountered calculates holdings
            // for account name based on transactions in list and adds them to the holdings map with the account name as
            // a key, clears list and adds transaction with new account name
            if(t.getAccount().equals(previousAccount)){
                accountTransactions.add(t);
            }else if(previousAccount.isEmpty()){
                accountTransactions.add(t);
                previousAccount = t.getAccount();
            } else{
                List<Holding> holdingsForAccount = handleTransactions(accountTransactions, date);
                holdings.put(previousAccount,holdingsForAccount);
                accountTransactions.clear();
                accountTransactions.add(t);
                previousAccount = t.getAccount();
            }
        }
        return holdings;
    }

    /**
     * Prints holdings calculated based on transaction file for every account name
     *
     * @param holdings Map<String, List<Holding>> map containing holdings for each account
     * @throws IOException
     */

    public void printHoldings(Map<String, List<Holding>> holdings) throws IOException {

       try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILENAME))) {
           for (String account : holdings.keySet()) {
               writer.write(account);
               writer.newLine();
               for (Holding holding : holdings.get(account)) {
                   writer.write(holding.getAsset());
                   writer.write("\t");
                   writer.write(Double.toString(holding.getHolding()));
                   writer.newLine();
               }
               writer.newLine();

           }
       }
    }

    /**
     * Reads in transaction file and parses it into lines (each representing a transaction)
     *
     * @param transactionFile File file containing transactions
     * @return List<String> returns a list of Strings, each representing a transaction
     * @throws IOException
     */

    public static List<String> readTransactionFile(File transactionFile) throws IOException {
        List<String> transactions = new ArrayList<>();

        try ( BufferedReader reader = new BufferedReader(new FileReader(transactionFile))){
                String line;
                while ((line = reader.readLine()) != null) {
                    transactions.add(line);
                }
            }

        return transactions;
    }

    /**
     * Calculates holdings linked to an account name by going through list of transactions for the account and adjusting
     * cash balance and assets in an account
     *
     * @param transactions List<Transaction> list of transactions linked to an account name
     * @param date LocalDate date up to which holdings will be calculated
     * @return List<Holding> list of holdings related to an account
     */

    public List<Holding> handleTransactions(List<Transaction> transactions, LocalDate date){
        Map<String,Holding> holdingMap = new HashMap<>();
        double cash = 0;
        for (Transaction t : transactions){
            if (t.getDate().isAfter(date)){
                continue;
            }
            switch(t.getTxnType()){
                case BOT:
                    cash = handleBotTransaction(holdingMap,t,cash);
                    break;
                //if transaction(txn) type is sold, checks if account holds asset to be sold (assuming only assets can
                // be sold that are held in account), if yes, calls helper function to update holdings and cash held in
                // account, if not, ignores transaction
                case SLD:
                  if(!holdingMap.keySet().contains(t.getAsset())) continue;
                  else cash = handleSldTransaction(holdingMap,t,cash);
                    break;
                 //if transaction(txn) type is withdrawal or deposit, updates cash held in account
                case WDR:
                    cash -= t.getPrice();
                    break;
                case DEP:
                    cash += t.getPrice();
                    break;
                //if transaction(txn) type is dividend, checks if account contains asset of type dividend is given for,
                // updates cash held in account, adding dividend value
                case DIV:
                    if(holdingMap.keySet().contains(t.getAsset())){
                        cash +=t.getPrice();
                    }
                    break;
            }
        }
        //creates cash holding for account, adds it to map containing holdings related to account
        Holding cashHolding = new Holding();
        cashHolding.setAsset("CASH");
        cashHolding.setHoldings(cash);
        holdingMap.put("CASH", cashHolding);
        //returns holdings related to account as list
        return new ArrayList<>(holdingMap.values());
    }

    /**
     * Splits string containing information about transaction, creates and validates Transaction object
     *
     * @param transactionLine String contains information about one transaction, expected to have following format:
     *                        Account,Date,TxnType,Units,Price,Asset
     * @return Transaction Object containing information about validated transaction
     * @throws InvalidTransactionException
     */

    public static Transaction parseTransaction(String transactionLine) throws InvalidTransactionException {
        String[] elements = transactionLine.split(",");
        Transaction t = new Transaction(elements);
        t.validateTransaction();
        return t;

    }

    /**
     * Processes BOT transaction (buying asset): checks if map recording holdings for account contains asset, if it does,
     * updates holding of asset (increasing by amount bought), if not, adds holding to map, updates and returns rounded
     * cash value held in account (pre-transaction value accepted as parameter)
     *
     * @param holdingMap Map<String,Holding> map recording holdings (value) for each asset (key) for an account
     * @param t Transaction transaction to be processed
     * @param cash double cash held in account
     * @return double updated cash value
     */

    public double handleBotTransaction(Map<String,Holding> holdingMap, Transaction t, double cash){
        String asset = t.getAsset();
        if(holdingMap.keySet().contains(asset)){
            double currentHoldings = holdingMap.get(asset).getHolding();
            holdingMap.get(asset).setHoldings(currentHoldings+t.getUnits());
        }else{
            Holding stockHolding = new Holding();
            stockHolding.setAsset(asset);
            stockHolding.setHoldings(t.getUnits());
            holdingMap.put(asset,stockHolding);
        }
       return cash -=roundToFourPlaces(t.getUnits()*t.getPrice());

    }

    //TODO: check if account holds at least as many units of the stock it wants to sell
    /**
     * Processes SLD transaction (selling asset): updates holding of asset (decreasing by amount sold), removes asset
     * from map of holdings if holdings value is 0, updates and returns rounded cash value held in account
     * (pre-transaction value accepted as parameter)
     *
     * @param holdingMap Map<String,Holding> map recording holdings (value) for each asset (key) for an account
     * @param t Transaction transaction to be processed
     * @param cash double cash held in account
     * @return double updated cash value
     */

    public double handleSldTransaction(Map<String,Holding> holdingMap, Transaction t, double cash){
        String asset = t.getAsset();
            double currentHoldings = holdingMap.get(asset).getHolding();
            holdingMap.get(asset).setHoldings(currentHoldings-t.getUnits());

        if(Double.compare(holdingMap.get(asset).getHolding(),0.0)==0){
            holdingMap.remove(asset);
        }
        cash +=roundToFourPlaces(t.getUnits()*t.getPrice());
        return cash;
    }

    /**
     * Rounds parameter to four decimal places using HALF_UP rounding mode
     *
     * @param value double value to be rounded to four decimal places
     * @return double rounded value
     */

    public double roundToFourPlaces(double value){
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(4, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
