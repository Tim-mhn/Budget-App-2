package hku.cs.sqlitedemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MonthPieCharts extends AppCompatActivity {

    private List<ProgressBar> progressBars = new ArrayList<>();
    private List<TextView> catTextViews = new ArrayList<>();
    private TransactionsDatabase transactionsDb;
    private TextView tvMonth;
    private int monthChart = Calendar.MONTH+1;
    private final List<Integer> colorList = new ArrayList<Integer>(Arrays.asList(
            Color.BLACK,
            Color.BLUE,
            Color.RED,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.GRAY));


    public MonthPieCharts(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_piecharts);
        if (transactionsDb == null) {
            transactionsDb= new TransactionsDatabase(this);
        }
        findViews();

    }

    public void findViews(){
        tvMonth = (TextView) findViewById(R.id.tvMonth);
    }

    @Override
    protected void onStart(){
        super.onStart();
        setPieChart();
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

    // setPieChart is the core method of the class
    // We fetch the right transactions (type / month) and separate the data into the different categories
    // Then we can do the analysis of the data: ratio of amounts per category and update the views (progress bars / textViews)

    public void setPieChart(){

        tvMonth.setText(getMonthForInt(monthChart));
        List<Transaction> transactions = transactionsDb.getTransactions("Expense", monthChart);
        List<String> categories = new ArrayList<>();
        List<Float> amounts = new ArrayList<>();

        for(Transaction t : transactions){
            String cat = t.getCategory();
            Float amount = Float.parseFloat(t.getAmount());
            int index = categories.indexOf(cat);
            if(index > -1){
                Float newAmount = amounts.get(index) + amount;
                amounts.set(index, newAmount);
            } else {
                categories.add(cat);
                amounts.add(amount);
            }
        }


        float totalAmount = 0; // totalAmount is the total of expenses
        for(int i=0; i<amounts.size(); i++){
            totalAmount += amounts.get(i);
        }

        float rotation = 0;

        for(int i=0; i<categories.size(); i++){

            // Find the progressbar and text view
            String pbId = "pb"+i;
            int pbResId = getResources().getIdentifier(pbId, "id", getPackageName());
            String tvId = "tvCat"+i;
            int tvResId = getResources().getIdentifier(tvId,"id", getPackageName());
            ProgressBar pb = (ProgressBar) findViewById(pbResId);
            progressBars.add(pb);
            TextView tv = (TextView) findViewById(tvResId);
            catTextViews.add(tv);

            // Set the progress bars and text views visible and update the colours
            pb.setVisibility(View.VISIBLE);
            pb.setProgressTintList(ColorStateList.valueOf(colorList.get(i)));
            tv.setVisibility(View.VISIBLE);
            tv.setTextColor(ColorStateList.valueOf(colorList.get(i)));


            // Set the progress bar's progress & rotation
            float progress = 100 *amounts.get(i)/totalAmount;
            int progressInt = (int) progress;
            String currency = getResources().getString(R.string.currency);
            tv.setText(categories.get(i)+"\r\n"+ progressInt + "%\r\n" + currency +amounts.get(i));
            pb.setProgress(progressInt);
            pb.setRotation(rotation);
            rotation += progress*360/100;
        }
    }

    public int getPercentage(double part, double total){
        double result = 100*part/total;
        int resultInt = (int) result;
        return resultInt;
    }

    public void onClickPreviousMonth(View v){
        if(monthChart == 0){ // We go from January to December
            monthChart = 11;
        } else {
            monthChart -= 1;
        }
        updatePieChart();
    }

    public void onClickNextMonth(View v){
        if(monthChart == 11){
            monthChart = 0;
        } else {
            monthChart += 1;
        }
        updatePieChart();
    }

    public void updatePieChart(){
        clearProgressBars();
        setPieChart();
    }

    public void clearProgressBars(){
        // We make all progress bars invisible again and clear the progressBars attribute
        for(ProgressBar pb : progressBars){
            pb.setVisibility(View.INVISIBLE);
        }
        for(TextView tv : catTextViews){
            tv.setVisibility(View.INVISIBLE);
        }
        progressBars.clear();
        catTextViews.clear();
    }


    public void backToMainActivity(View v){
        Intent i = new Intent(this,MainTransactions.class);
        startActivity(i);
    }
}
