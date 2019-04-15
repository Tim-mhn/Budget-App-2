package hku.cs.sqlitedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
/*
import android.support.design.widget.TabLayout;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.support.v7.widget.ToolbarWidgetWrapper;
*/
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.tabs.TabLayout;
import com.triggertrap.seekarc.SeekArc;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainTransactions extends AppCompatActivity {
    private TransactionsDatabase sqliteHelper;
    private TransactionsAdapter transactionsAdapter;
    private ObjectivesDatabase objDb;
    private RecyclerView rvSpots;
    private SeekArc seekArcExpenses;
    private SeekArc seekArcIncomes;
    private TextView progressPercentageExpenses;
    private TextView progressPercentageIncomes;
    private Spinner filterSpinner;
    private TabLayout tabs;
    private BottomAppBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity2);
        if (sqliteHelper == null) {
            sqliteHelper = new TransactionsDatabase(this);
        }
        if (objDb == null){
            objDb = new ObjectivesDatabase(this);
        }
        findViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        bottomBar.inflateMenu(R.menu.bottomappbar_menu);
        MenuItem transactionsItem = menu.findItem(R.id.app_bar_transactions);
        TextView tvTransactions= transactionsItem.getActionView().findViewById(R.id.tvTransactionsItem);
        tvTransactions.setTextColor(getResources().getColor(R.color.design_default_color_primary_dark));
        tvTransactions.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.design_default_color_primary_dark)));
        // tvTransactions.setCompoundDrawables(null, getResources().getDrawable(R.drawable.list_white_64,getApplicationContext().getTheme()),null,null);
        // transactionsItem.setIcon(R.drawable.list_white_64);
        return true;
    }

    public void findViews(){
        rvSpots = (RecyclerView) findViewById(R.id.rvSpots);
        rvSpots.setLayoutManager(new LinearLayoutManager(this));

        progressPercentageExpenses = (TextView) findViewById(R.id.intCurrentProgressExpenses);
        seekArcExpenses = (SeekArc) findViewById(R.id.seekArcExpenses);
        seekArcExpenses.setEnabled(false);

        progressPercentageIncomes = (TextView) findViewById(R.id.intCurrentProgressIncomes);
        seekArcIncomes = (SeekArc) findViewById(R.id.seekArcIncomes);
        seekArcIncomes.setEnabled(false);

        progressPercentageExpenses.setText(String.valueOf(seekArcExpenses.getProgress()).concat("%"));
        progressPercentageIncomes.setText(String.valueOf(seekArcIncomes.getProgress()).concat("%"));
        //filterSpinner = (Spinner) findViewById(R.id.filterSpinner);

        tabs = (TabLayout) findViewById(R.id.tabs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottomBar = (BottomAppBar) findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Transaction> transactionList = getTransactionsList("all");
        if (transactionList.size() <= 0) {
            Toast.makeText(
                    this, R.string.text_NoDataFound, Toast.LENGTH_SHORT
            ).show();
        }

        if (transactionsAdapter == null) {
            transactionsAdapter = new TransactionsAdapter(this, transactionList);
            rvSpots.setAdapter(transactionsAdapter);
        } else {
            transactionsAdapter.setSpotList(transactionList);
            transactionsAdapter.notifyDataSetChanged();
        }
        //setupFilterSpinner();
        addTabsListener();
        updateProgressBar();

    }

    public void addTabsListener() {
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            Context context;
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("Pressing tab " + tab.getText());
                if (tab.getText().equals("All")) {
                    transactionsAdapter.transactionList = getTransactionsList("All");
                }
                else if (tab.getText().equals("Incomes")) {
                    transactionsAdapter.transactionList = getTransactionsList("Income");
                }
                else {
                    transactionsAdapter.transactionList = getTransactionsList("Expense");
                }
                transactionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public List<Transaction> getTransactionsList(String type){
        return sqliteHelper.getTransactions(type);
    }

    // Click on the + button to open the 'new transaction' view

    public void onInsertClick(View view) {
        Intent intent = new Intent(this, NewTransaction.class);
        startActivity(intent);
    }


    private class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.SpotViewHolder> {
        Context context;
        List<Transaction> transactionList;

        void setSpotList(List<Transaction> transactionList) {
            this.transactionList = transactionList;
        }

        TransactionsAdapter(Context context, List<Transaction> transactionList) {
            this.context = context;
            this.transactionList = transactionList;
        }

        class SpotViewHolder extends RecyclerView.ViewHolder {
            ImageView ivCategory;
            TextView tvAmount, tvCategory, tvDescription, tvDate;

            SpotViewHolder(View itemView) {
                super(itemView);
                tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
                tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
                tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                ivCategory = (ImageView) itemView.findViewById(R.id.ivCategory);

            }
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        @Override
        public SpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.transaction_view, parent, false);
            return new SpotViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SpotViewHolder holder, int position) {
            final Transaction transaction = transactionList.get(position);

            // We have to implement the R string currency !
            holder.tvAmount.setText("$" + transaction.getAmount());
            holder.tvCategory.setText(transaction.getCategory());
            holder.tvDescription.setText(transaction.getDescription());
            holder.tvDate.setText(transaction.getDate());
            if(transaction.getType().equalsIgnoreCase("expense")){
                holder.tvAmount.setTextColor(getResources().getColor(R.color.OrangeRed));
            } else {
                holder.tvAmount.setTextColor(getResources().getColor(R.color.SeaGreen));
            }
            String imageName = Category.getImageName(transaction.getCategory());
            int imageSrc = context.getResources().getIdentifier("drawable/"+imageName,null,context.getPackageName());
            holder.ivCategory.setImageResource(imageSrc);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", transaction.getId()); // test line
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int count = sqliteHelper.deleteById(transaction.getId());
                    Toast.makeText(context, count + " " + getString(R.string.msg_RowDeleted),
                            Toast.LENGTH_SHORT).show();
                    transactionList = sqliteHelper.getAllSpots();
                    notifyDataSetChanged();
                    updateProgressBar();
                    return true;
                }
            });
        }
    }

    /*
    public void onFilterList(String option){
        transactionsAdapter.transactionList = getTransactionsList(option);
        transactionsAdapter.notifyDataSetChanged();
    }

    private void setupFilterSpinner(){


        // Initializing a String Array
        String[] types = new String[]{
                "All",
                "Expense",
                "Income",
        };

        final List<String> typesList = new ArrayList<>(Arrays.asList(types));

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typesList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount(); // you don't display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        filterSpinner.setAdapter(adapter);
        filterSpinner.setSelection(0); //display hint
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) filterSpinner.getSelectedItem();
                onFilterList(type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
<<<<<<< HEAD
    */

    // Menu onClick methods

    public void onObjectivesClick(View v){
        Intent i = new Intent(this,UpdateObjectives.class);
        startActivity(i);
    }

    public void onTransactionsClick(View v){
        // do nothing
    }

    public void onDetailClick(View v){
        Intent i = new Intent(this, MonthPieCharts.class);
        startActivity(i);
    }

    public void onSettingsClick(View v){
        // do nothing
    }
    // Click on the image to update the monthly objectives
    // Opens a new view to set the user's income / expense month objectives

    public void onProgressBarClick(View v){
        Intent i = new Intent(this,UpdateObjectives.class);
        startActivity(i);
    }

    // List of methods used to set and update the progress bar
    // We fetch the data from the transactions DB and objectives DB
    // We get the total amount of expenses & income of transactions of the current month

    public void updateProgressBar(){
        double monthExpenseObj = getMonthExpenseObj();
        double monthIncomeObj = getMonthIncomeObj();
        double monthTotalExpenses = getMonthTotal("expense");
        double monthTotalIncomes = getMonthTotal("income");

        int expensePercentage = getExpensePercentage(monthTotalExpenses, monthExpenseObj);
        int incomePercentage = getIncomePercentage(monthTotalIncomes, monthIncomeObj);

        seekArcExpenses.setProgress(expensePercentage);
        seekArcIncomes.setProgress(incomePercentage);
        setProgressBarColor(expensePercentage, "expenses");
        setProgressBarColor(incomePercentage, "incomes");
        progressPercentageExpenses.setText(String.valueOf(expensePercentage).concat("%"));
        progressPercentageIncomes.setText(String.valueOf(incomePercentage).concat("%"));

        TextView ratioIncomes = (TextView) findViewById(R.id.txtRatioIncomes);
        TextView ratioExpenses = (TextView) findViewById(R.id.txtRatioExpenses);
        ratioIncomes.setText("$ ".concat(String.valueOf((int)monthTotalIncomes)).concat(" / ").concat(String.valueOf((int)monthIncomeObj)));
        ratioExpenses.setText("$ ".concat(String.valueOf((int)monthTotalExpenses)).concat(" / ").concat(String.valueOf((int)monthExpenseObj)));
    }

    public void setProgressBarColor(int percentage, String type){
        int colorId;

        if (type.matches("expenses")) {
            if(percentage < 20){
                colorId = getResources().getColor(R.color.LightSkyBlue);
            } else if (percentage < 50){
                colorId = getResources().getColor(R.color.Lime);
            } else if (percentage < 80) {
                colorId = getResources().getColor(R.color.DarkOrange);
            } else if (percentage < 100) {
                colorId = getResources().getColor(R.color.Tomato);
            } else {
                colorId = getResources().getColor(R.color.DarkRed);
            }
            seekArcExpenses.setProgressColor(colorId);
        }
        else {
            if(percentage > 80){
                colorId = getResources().getColor(R.color.LightSkyBlue);
            } else if (percentage > 50){
                colorId = getResources().getColor(R.color.Lime);
            } else if (percentage > 20) {
                colorId = getResources().getColor(R.color.DarkOrange);
            } else if (percentage > 0) {
                colorId = getResources().getColor(R.color.Tomato);
            } else {
                colorId = getResources().getColor(R.color.DarkRed);
            }
            seekArcIncomes.setProgressColor(colorId);
        }
    }

    // getMonthAmount("expense") will return the current month total expenses
    // getMonthTotal("income") will return the current month total income
    public double getMonthTotal(String transactionType){
        List<Transaction> transactions = getTransactionsList(transactionType);
        double amount = 0;
        int currentYear = 2019;
        int currentMonth = Calendar.MONTH + 1;
        for(Transaction t : transactions){
            if(t.getMonthInt() == currentMonth && Integer.parseInt(t.getYear()) == currentYear){
                amount += Double.parseDouble(t.getAmount());
            }
        }
        return amount;
    }


    public double getMonthIncomeObj(){
        MonthObjective mO = objDb.getCurrentObjectives();
        return mO.getIncome();
    }

    public double getMonthExpenseObj(){
        MonthObjective mO = objDb.getCurrentObjectives();
        return mO.getExpense();
    }

    public int getIncomePercentage(double currentIncomes, double incomeObjective){
        double percentage = 100 * currentIncomes / incomeObjective;
        return (int) percentage;
    }

    public int getExpensePercentage(double currentExpenses, double expenseObjective){
        double percentage = 100 * currentExpenses / expenseObjective;
        return (int) percentage;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqliteHelper != null) {
            sqliteHelper.close();
        }
    }

}
