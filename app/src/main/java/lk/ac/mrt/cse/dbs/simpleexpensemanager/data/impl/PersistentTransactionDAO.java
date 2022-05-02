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

public class PersistentTransactionDAO implements TransactionDAO {

    private final DBHelper dbHelper;

    public PersistentTransactionDAO(DBHelper db){
        this.dbHelper = db;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        //check for account existence
        Cursor result = dbHelper.getAccountByNo(accountNo);
        if(!result.moveToFirst()){
            System.out.println("Account Number Is Invalid");
            return;
        }

        //check for current amount
        double CurrentAmount = result.getDouble(3);
        if(ExpenseType.EXPENSE == expenseType && (CurrentAmount-amount)<0){
            System.out.println("Current Balance Exceeded");
            return;
        }
        DateFormat simpleFormat = new SimpleDateFormat("yyyy-mm-dd");
        String simpleDate = simpleFormat.format(date);
        dbHelper.addTransactionLog(accountNo,expenseType.name(),amount,simpleDate);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionsList = new ArrayList<>();
        Transaction transaction;

        Cursor cursor = dbHelper.getAllTransactions();
        //check cursor nullity
        if(!cursor.moveToFirst()){
            return transactionsList;
        }

        do {
            Date date = null;
            String stringDate = cursor.getString(1);
            try {
                date = new SimpleDateFormat("yyyy-mm-dd").parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ExpenseType expense = ExpenseType.valueOf(cursor.getString(3));
            String acc_no = cursor.getString(2);
            double amount = cursor.getDouble(4);
            transaction = new Transaction(date,acc_no,expense,amount);
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
