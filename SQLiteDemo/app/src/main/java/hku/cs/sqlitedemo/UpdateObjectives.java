package hku.cs.sqlitedemo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
/*import android.support.v7.app.AppCompatActivity;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;*/
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
//import android.support.v7.widget.Toolbar;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;

import java.util.Calendar;


public class UpdateObjectives extends AppCompatActivity {

    private double expense = 0;
    private double income = 0;
    private EditText etExpense;
    private EditText etIncome;
    private TextView tvBalance;
    private TextView tvMonth;
    private Toolbar tbToolBar;
    private BottomAppBar bottomAppBar;

    // firstSet is true if it's the first time that we set our objective.
    // In that case we will insert a row in the DB insert of updating the (single) row

    private boolean firstSet = false;

    private ObjectivesDatabase objDb;
    private static final int DATE_DIALOG_ID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_objectives);
        if (objDb == null) {
            objDb = new ObjectivesDatabase(this);
        }
        findViews();
        addTextChangeListeners();
        updateBalanceView();
    }

    private void findViews() {
        etExpense = (EditText) findViewById(R.id.etObjExpense);
        etIncome = (EditText) findViewById(R.id.etObjIncome);
        tvBalance = (TextView) findViewById(R.id.tvBalance);
        //tvMonth = (TextView) findViewById(R.id.tvMonth);
        tbToolBar = (Toolbar) findViewById(R.id.tbObjectivesToolbar);
        bottomAppBar = (BottomAppBar) findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);

        MonthObjective mO = objDb.getCurrentObjectives(); // check that line
        if (mO.getIncome() == 0 && mO.getExpense() == 0) {
            Toast.makeText(this, R.string.msg_NoDataFound,
                    Toast.LENGTH_SHORT).show();
            firstSet = true; // if there is no data in the DB, we will have to insert a row and not update it
            return;
        }

        income = mO.getIncome();
        expense = mO.getExpense();
        etExpense.setText(String.valueOf(expense));
        etIncome.setText(String.valueOf(income));
        Calendar c = Calendar.getInstance();
        int monthInt = c.get(Calendar.MONTH);
        String month = getMonthForInt(monthInt);
        //tvMonth.setText(month);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        bottomAppBar.inflateMenu(R.menu.bottomappbar_menu);
        MenuItem transactionsItem = menu.findItem(R.id.app_bar_objective);
        TextView tvTransactions= transactionsItem.getActionView().findViewById(R.id.tvObjectiveItem);
        tvTransactions.setTextColor(getResources().getColor(R.color.design_default_color_primary_dark));
        tvTransactions.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary_dark)));
        return true;
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
        Double currentBalance = 0.;
        if(!tvBalance.getText().toString().equals("")){
            currentBalance = Double.parseDouble(tvBalance.getText().toString());
        }

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

    private void updateBalanceView(){
        double balance = income-expense;
        tvBalance.setText(Double.toString(balance));
    }

    private void addTextChangeListeners(){

        addOneTextChangeListener(etExpense,false);
        addOneTextChangeListener(etIncome,true);
    }

    public void onDetailClick(View v) {
        Intent i = new Intent(this,MonthPieCharts.class);
        startActivity(i);
    }

    public void onNewTransactionClick(View v){
        Intent i = new Intent(this,NewTransaction.class);
        startActivity(i);
    }

    public void backToMainActivity(View view){
        Intent i = new Intent(this,MainTransactions.class);
        startActivity(i);
    }

    public void onSetObjectives(View view){
        long count;
        if(firstSet){
            count = objDb.insert(new MonthObjective(income, expense));
        } else {
          count = objDb.update(new MonthObjective(income, expense));
        }

        Toast.makeText(this, count + " " + getString(R.string.msg_RowUpdated),
                Toast.LENGTH_SHORT).show();
        backToMainActivity(view);
    }
}
