package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public  static final String DATABASE_NAME = "190359P.db";
    public static final String ACCOUNTS_TABLE = "accounts";
    public static final String TRANSACTIONS_TABLE = "transactions";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating the Accounts table for the first time
        String createAccountStatement = "CREATE TABLE IF NOT EXISTS " + ACCOUNTS_TABLE + "(" + "account_no TEXT PRIMARY KEY, " + "bank_name TEXT NOT NULL, " + "owner_name TEXT NOT NULL, "+ "initial_balance REAL NOT NULL " + ");";
        db.execSQL(createAccountStatement);

        //create Transactions table for the first time
        String createTransactionStatement = "CREATE TABLE IF NOT EXISTS " + TRANSACTIONS_TABLE + "(" + "transaction_id INTEGER PRIMARY KEY, " + "date TEXT NOT NULL," + "account_no TEXT NOT NULL," + "type TEXT NOT NULL CHECK (type == \"INCOME\" OR type == \"EXPENSE\")," + "amount REAL NOT NULL," + "FOREIGN KEY(account_no) REFERENCES " + ACCOUNTS_TABLE + "(accounts_no) ON DELETE CASCADE " + ");";
        db.execSQL(createTransactionStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since we don't want this function in this application,I didn't implement it
    }

    public boolean addNewAccount(String acc_no, String bank_name, String holder , double initial_balance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("account_no",acc_no);
        cv.put("bank_name",bank_name);
        cv.put("owner_name",holder);
        cv.put("initial_balance",initial_balance);

        long insert = db.insert(ACCOUNTS_TABLE, null, cv);
        if (insert==-1){
            return false;
        }else {
            return true;
        }
    }

    public boolean deleteAccount(String acc_no){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("account_no",acc_no);
        int delete = db.delete(ACCOUNTS_TABLE, "account_no=?", new String[]{acc_no});
        if (delete == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean addTransactionLog(String acc_no,String type,double amount,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_no",acc_no);
        contentValues.put("type",type);
        contentValues.put("amount",amount);
        contentValues.put("date",date);

        long insert = db.insert(TRANSACTIONS_TABLE, null, contentValues);
        if (insert==-1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getAccountByNo(String acc_no){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectAccountStatement = "SELECT * FROM " + ACCOUNTS_TABLE + " WHERE account_no='" +acc_no+"';";
        Cursor result = db.rawQuery(selectAccountStatement,null);
        return result;
    }

    public Cursor getAllAccountNo(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectAllAccountNo = "SELECT account_no FROM " + ACCOUNTS_TABLE + ";";
        Cursor result = db.rawQuery(selectAllAccountNo,null);
        return result;
    }

    public Cursor getAllAccounts(){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectAllAccounts = "SELECT * FROM " + ACCOUNTS_TABLE + ";";
        Cursor result = db.rawQuery(selectAllAccounts,null);
        return result;
    }

    public boolean updateAccountBalance(double amount,String acc_no){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_no",acc_no);

        int update = db.update(ACCOUNTS_TABLE, contentValues, "account_no=?", new String[]{acc_no});
        if (update == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getAllTransactions(){
        SQLiteDatabase db  = this.getReadableDatabase();

        String selectAllTransactions = "SELECT * FROM " + TRANSACTIONS_TABLE + ";";
        Cursor result = db.rawQuery(selectAllTransactions,null);
        return result;
    }

}
