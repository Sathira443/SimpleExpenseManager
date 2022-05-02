package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private DBHelper dbHelper;
    private Context context;

    public PersistentAccountDAO(DBHelper dbHelper,Context context){
        this.dbHelper = dbHelper;
        this.context = context;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> acc_noList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllAccountNo();

        //check whether there are no accounts
        if(!cursor.moveToFirst()){
            return acc_noList;
        }

        do {
            acc_noList.add(cursor.getString(0));
        }
        while (cursor.moveToNext());

        return acc_noList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountList = new ArrayList<Account>();
        Cursor cursor = dbHelper.getAllAccounts();


        //check whether there are no accounts
        if(!cursor.moveToFirst()){
            return accountList;
        }

        do {
            String acc_no = cursor.getString(0);
            String bank = cursor.getString(1);
            String owner = cursor.getString(2);
            double amount = cursor.getDouble(3);

            Account account = new Account(acc_no,bank,owner,amount);
            accountList.add(account);
        }
        while (cursor.moveToNext());

        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = dbHelper.getAccountByNo(accountNo);

        if(cursor.moveToFirst()){
            String acc_no = cursor.getString(0);
            String bank = cursor.getString(1);
            String owner = cursor.getString(2);
            double amount = cursor.getDouble(3);

            Account account = new Account(acc_no,bank,owner,amount);
            return account;
        }
        String err = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(err);

    }

    @Override
    public void addAccount(Account account)  {
        Cursor cursor = dbHelper.getAccountByNo(account.getAccountNo());

        if(cursor.moveToFirst()) {
            Toast.makeText(context, "Account already exists.", Toast.LENGTH_SHORT).show();
            return;
        }
        dbHelper.addNewAccount(account.getAccountNo(),account.getBankName(),account.getAccountHolderName(),account.getBalance());
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = dbHelper.getAccountByNo(accountNo);
        if(!cursor.moveToFirst()) {
            String err = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(err);
        }

        dbHelper.deleteAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Cursor cursor = dbHelper.getAccountByNo(accountNo);
        if(!cursor.moveToFirst()) {
            String err = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(err);
        }

        double currentBalance = cursor.getDouble(3);
        double finalBalance = 0;

        if(ExpenseType.EXPENSE == expenseType){
            finalBalance = currentBalance - amount;
        }
        else if(ExpenseType.INCOME == expenseType){
            finalBalance = currentBalance + amount;
        }

        if(finalBalance<0){
            String err = "Balance is not Enough!";
            throw new InvalidAccountException(err);
        }
        dbHelper.updateAccountBalance(amount,accountNo);
    }
}
