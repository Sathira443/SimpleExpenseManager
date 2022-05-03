package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is a persistent implementation of TransactionDAO interface. All the
 * transaction logs are stored in a SQLite database.
 */
public class PersistentTransactionDAO implements TransactionDAO {

    private final DBHelper dbHelper;

    public PersistentTransactionDAO(DBHelper db){
        this.dbHelper = db;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        //Get the account and check the existence
        Cursor result = dbHelper.getAccountByNo(accountNo);
        if(!result.moveToFirst()){
            return;
        }

        //Get the remaining value in account and Check transaction can be completed without exceeding balance
        double remainingAmount = result.getDouble(3); //
        if(ExpenseType.EXPENSE == expenseType && (remainingAmount-amount)<0){
            return;
        }

        DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
        String simpleDate = simpleFormat.format(date);

        //Add transaction to the log
        dbHelper.addTransactionLog(accountNo, expenseType.name(), amount, simpleDate);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionsList = new ArrayList<>();
        Transaction transaction;

        Cursor cursor = dbHelper.getAllTransactions();

        //Check Transaction Log is empty
        if(!cursor.moveToFirst()){
            return transactionsList;
        }

        do {
            //Getting all the Attribute values for one transaction one by one
            Date date = null;
            String stringDate = cursor.getString(1);
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ExpenseType expense = ExpenseType.valueOf(cursor.getString(3));
            String acc_no = cursor.getString(2);
            double amount = cursor.getDouble(4);
            transaction = new Transaction(date,acc_no,expense,amount);

            //add transaction to the list
            transactionsList.add(transaction);
        }
        while(cursor.moveToNext());

        return transactionsList;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactionList = getAllTransactionLogs();

        //check the limit exceed the list size
        if(limit>=transactionList.size()){
            return transactionList;
        }
        return transactionList.subList((limit-transactionList.size()),transactionList.size());
    }
}
