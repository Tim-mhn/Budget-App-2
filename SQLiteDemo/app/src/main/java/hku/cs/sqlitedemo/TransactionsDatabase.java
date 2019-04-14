package hku.cs.sqlitedemo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TransactionsDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "Transactions";
    private static final int DB_VERSION = 8;
    private static final String TABLE_NAME = "Spot";
    private static final String COL_id = "id";
    private static final String COL_amount = "amount";
    private static final String COL_description = "description";
    private static final String COL_type = "type";
    private static final String COL_category = "category";
    private static final String COL_date = "date";
    //private static final String COL_amount = "amount";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_amount + " TEXT , " +
                    COL_description + " TEXT, " +
                    COL_type + " TEXT, " +
                    COL_category + " TEXT, " +
                    COL_date + " TEXT ); ";

    public TransactionsDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Transaction> getAllSpots() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_amount, COL_description, COL_type, COL_category, COL_date
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null,
                null);


        List<Transaction> transactionList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String amount = cursor.getString(1);
            String description = cursor.getString(2);
            String type = cursor.getString(3);
            String category = cursor.getString(4);
            String date = cursor.getString(5);
            Transaction t = new Transaction(id, amount, description, type, category, date);
            transactionList.add(t);
        }
        cursor.close();

        // Part where we create a sorter instance and sort the list from newest to oldest

        TransactionsSorter sorter = new TransactionsSorter();
        List<Transaction> sortedTransactionList = sorter.mergeSort(transactionList);
        return sortedTransactionList;
    }

    public List<Transaction> getTransactions(String type){
        if(type.equalsIgnoreCase("all")){
            return getAllSpots();
        } else {
            List<Transaction> allTransactions = getAllSpots();
            List<Transaction> filteredList = new ArrayList<>();
            for(Transaction t : allTransactions){
                if (t.getType().equalsIgnoreCase(type)){
                    filteredList.add(t);
                }
            }
            return filteredList;
        }
    }

    public List<Transaction> getTransactions(String type, int month){
        List<Transaction> allTransactions = getAllSpots();
        List<Transaction> transactions = new ArrayList<>();
        for(Transaction t : allTransactions){
            if(t.getType().equalsIgnoreCase(type) && t.getMonthInt() == month){
                transactions.add(t);
            }
        }
        return transactions;
    }

    public Transaction findById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                COL_amount, COL_description, COL_type, COL_category, COL_date
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        Transaction transaction = null;
        if (cursor.moveToNext()) {
            String amount = cursor.getString(0);
            String description = cursor.getString(1);
            String type = cursor.getString(2);
            String category = cursor.getString(3);
            String date = cursor.getString(4);
            transaction = new Transaction(id, amount, description, type, category, date);
        }
        cursor.close();
        return transaction;
    }

    public long insert(Transaction t) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_amount, t.getAmount());
        values.put(COL_description, t.getDescription());
        values.put(COL_type, t.getType());
        values.put(COL_category, t.getCategory());
        values.put(COL_date, t.getDate());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Transaction transaction) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_amount, transaction.getAmount());
        values.put(COL_description, transaction.getDescription());
        values.put(COL_type, transaction.getType());
        values.put(COL_category, transaction.getCategory());
        values.put(COL_date, transaction.getDate());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(transaction.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }
}
