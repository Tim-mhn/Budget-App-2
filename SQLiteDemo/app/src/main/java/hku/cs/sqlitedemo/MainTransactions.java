package hku.cs.sqlitedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private SeekArc seekArc;
    private TextView progressPerentage;
    private Spinner filterSpinner;

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
        //setupFilterSpinner();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //if (getSupportActionBar() != null) {
          //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //}

    }

    public void findViews(){
        rvSpots = (RecyclerView) findViewById(R.id.rvSpots);
        rvSpots.setLayoutManager(new LinearLayoutManager(this));

        progressPerentage = (TextView) findViewById(R.id.txtProgress);
        seekArc = (SeekArc) findViewById(R.id.seekArc);
        seekArc.setEnabled(false);

        progressPerentage.setText(String.valueOf(seekArc.getProgress()).concat("%"));
        //filterSpinner = (Spinner) findViewById(R.id.filterSpinner);
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
        setupFilterSpinner();
        updateProgressBar();



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
                holder.tvAmount.setTextColor(Color.RED);
            } else {
                holder.tvAmount.setTextColor(Color.GREEN);
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

    public void onDetailClick(View v){
        Intent i = new Intent(this, MonthPieCharts.class);
        startActivity(i);
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
        int percentage = getExpensePercentage();
        seekArc.setProgress(percentage);
        setProgressBarColor(percentage);
        progressPerentage.setText(String.valueOf(percentage).concat("%"));
    }

    public void setProgressBarColor(int percentage){
        int colorId;
        if(percentage<20){
            colorId = getResources().getColor(R.color.LightSkyBlue);
        } else if (percentage<50){
            colorId = getResources().getColor(R.color.Lime);
        } else if (percentage<80) {
            colorId = getResources().getColor(R.color.DarkOrange);
        } else if (percentage < 100) {
            colorId = getResources().getColor(R.color.Tomato);
        } else {
            colorId = getResources().getColor(R.color.DarkRed);
        }
        seekArc.setProgressColor(colorId);
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

    public int getIncomePercentage(){
        double current = getMonthTotal("income");
        double objective = getMonthIncomeObj();
        double result = 100*current/objective;
        int percentage = (int) result;
        return percentage;
    }

    public int getExpensePercentage(){
        double current = getMonthTotal("expense");
        double objective = getMonthExpenseObj();
        double result = 100*current/objective;
        int percentage = (int) result;
        //int percentage = (int) current;
        return percentage;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqliteHelper != null) {
            sqliteHelper.close();
        }
    }

}
