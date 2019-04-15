package hku.cs.sqlitedemo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
/*import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;*/
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ObjectivesDatabase extends SQLiteOpenHelper {

    // ObjectivesDatabase will be used to store the user's current month spendings objectives.
    // For the moment, this database only stores the expense & income objectives for the current month.
    // The DB has thus only 1 row. We could potentially upgrade it, storing the other months data.
    private static final String DB_NAME = "Objectives";
    private static final int DB_VERSION = 4;
    private static final String TABLE_NAME = "Objective";
    private static final String COL_id = "id";
    private static final String COL_income = "incomeObj";
    private static final String COL_expense = "expenseObj";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_id + " INT PRIMARY KEY , " +
                    COL_income + " REAL , " +
                    COL_expense + " REAL ); ";

    public ObjectivesDatabase(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        //insert(new MonthObjective(0,0));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        //insert(new MonthObjective(0,0));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public MonthObjective getCurrentObjectives() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_income, COL_expense
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null,
                null);

        MonthObjective mO = new MonthObjective(0,0);
        if(cursor.moveToNext()){
            double incomeObj = cursor.getDouble(1);
            double expenseObj = cursor.getDouble(2);
            mO = new MonthObjective(incomeObj, expenseObj);
        }

        cursor.close();
        return mO;
    }


    public long insert(MonthObjective mO) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_id, 0);
        values.put(COL_income, mO.getIncome());
        values.put(COL_expense, mO.getExpense());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(MonthObjective mO) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_income, mO.getIncome());
        values.put(COL_expense, mO.getExpense());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(0)};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

}