package com.transactions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Transaction {

    public static enum txn{
        BOT, SLD, WDR, DEP, DIV
    }
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    String account;
    LocalDate date;
    txn txnType;
    double units;
    double price;
    String asset;


    Transaction(String[] elements) throws InvalidTransactionException {
        if(elements.length != 6){
            //exception caught in next level (to skip line)
            throw new InvalidTransactionException("Error: transaction input lines of incorrect format, expected format: Account,Date,TxnType,Units,Price,Asset");
        }

        this.account = elements[0];
        try {
            this.date = LocalDate.parse(elements[1], formatter);
        }catch(Exception e){
            throw new InvalidTransactionException("Error: date of incorrect format, expected format yyyMMdd, actual date [" + elements[1] +"]");
        }
        try {
            this.txnType = txn.valueOf(elements[2]);
        } catch(Exception e){
            throw new InvalidTransactionException("Error: txn type expected to be BOT, SLD, WDR, DEP or DIV, actual txn type [" + elements[2] +"]");
        }
        try {
            this.units = Double.valueOf(elements[3]);
        } catch(Exception e){
            throw new InvalidTransactionException("Error: units expected to be of type double, actual units [" + elements[3] +"]");
        }
        try {
            this.price = Double.valueOf(elements[4]);
        } catch(Exception e){
            throw new InvalidTransactionException("Error: price expected to be of type double, actual price [" + elements[4] +"]");
        }

        this.asset = elements[5];


    }

    public void validateTransaction() throws InvalidTransactionException {
        if((this.txnType.equals(txn.WDR) || this.txnType.equals(txn.DEP)) && !this.asset.equals("CASH")){
            throw new InvalidTransactionException("Error: for deposit and withdrawal transaction type asset is expected to be CASH, actual asset: [" + this.asset + "]");
        }
        if(this.units <= 0 || this.price <= 0){
            throw new InvalidTransactionException("Error: units and price expected to be positive");
        }
        //check that account not ''
        if(this.account.isEmpty()){
            throw new InvalidTransactionException("Error: account name expected to be non empty");
        }

        if(this.asset.isEmpty()){
            throw new InvalidTransactionException("Error: Asset not specified");
        }
        if (this.asset.equals("CASH")&& this.price!= 1.0){
            throw new InvalidTransactionException("Error: for cash transactions the price is expected to be 1, actual price [ " + this.price + "]");
        }

    }


    public String getAccount(){
        return this.account;
    }

    public LocalDate getDate(){
        return this.date;
    }

    public txn getTxnType(){
        return this.txnType;
    }

    public double getUnits(){
        return this.units;
    }

    public double getPrice(){
        return this.price;
    }

    public String getAsset(){
        return this.asset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.getUnits(), getUnits()) == 0 &&
                Double.compare(that.getPrice(), getPrice()) == 0 &&
                Objects.equals(getAccount(), that.getAccount()) &&
                Objects.equals(getDate(), that.getDate()) &&
                txnType == that.txnType &&
                Objects.equals(getAsset(), that.getAsset());
    }

    @Override
    public int hashCode() {
        int result = 17;
        long lu = Double.doubleToLongBits(getUnits());
        result = 37 * result + (int)(lu ^ (lu >>> 32));
        long lp = Double.doubleToLongBits(getPrice());
        result = 37 * result + (int)(lp ^ (lp >>> 32));
        result = 37* result + getAccount().hashCode();
        result = 37 * result + getDate().hashCode();
        result = 37 * result + getTxnType().hashCode();
        result = 37 * result + getAccount().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "account='" + account + '\'' +
                ", date=" + date +
                ", txnType=" + txnType +
                ", units=" + units +
                ", price=" + price +
                ", asset='" + asset + '\'' +
                '}';
    }
}