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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UpdateObjectives extends AppCompatActivity{

    private double expense = 0;
    private double income = 0;
    private EditText etExpense;
    private EditText etIncome;
    private TextView tvBalance;
    private TextView tvMonth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_objectives);
        findViews();
        addTextChangeListeners();
    }

    private void findViews() {
        etExpense = (EditText) findViewById(R.id.etObjExpense);
        etIncome = (EditText) findViewById(R.id.etObjIncome);
        tvBalance = (TextView) findViewById(R.id.tvBalance);
        tvMonth = (TextView) findViewById(R.id.tvMonth);

        tvBalance.setText("0");

        Calendar c = Calendar.getInstance();
        int monthInt = c.get(Calendar.MONTH);
        String month = getMonthForInt(monthInt);
        tvMonth.setText(month);


    }

    private String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
    private void addOneTextChangeListener(EditText et, boolean transactionType){

        final boolean type = transactionType;
        // transactionType = false if it's an expense; true if it's an income
        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updateBalanceView(s.toString(),type);
            }
        });
    }

    private void updateBalanceView(String amountStr, boolean transactionType){
        double amount = 0;
        if(!amountStr.equals("")){
            amount = Double.parseDouble(amountStr);
        }


        double currentBalance = Double.parseDouble(tvBalance.getText().toString());
        double newAmount = currentBalance;
        if(transactionType){
            newAmount = newAmount - income + amount;
            income = amount;
        } else {
            newAmount = newAmount + expense - amount;
            expense = amount;
        }


        tvBalance.setText(Double.toString(newAmount));
    }

    private void addTextChangeListeners(){

        addOneTextChangeListener(etExpense,false);
        addOneTextChangeListener(etIncome,true);
    }
}