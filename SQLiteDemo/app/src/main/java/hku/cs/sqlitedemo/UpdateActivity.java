package hku.cs.sqlitedemo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {
    private EditText etDescription;
    private RadioGroup rgType;
    private TransactionsDatabase transactionsDb;
    private EditText etAmount;
    private Spinner spinCategory;
    private TextView tvDate;

    private int year;
    private int month;
    private int day;


    private Transaction transaction; // Transaction which we want to update
    private static final int DATE_DIALOG_ID = 0;

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int thisYear,
                                      int monthOfYear, int dayOfMonth) {
                    year = thisYear;
                    month = monthOfYear;
                    day = dayOfMonth;
                    updateDateDisplay();
                }
            };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_transaction);
        if (transactionsDb == null) {
            transactionsDb = new TransactionsDatabase(this);
        }
        findViews();
        setupCategoriesSpinner();
        setupCalendar();

    }

    private void findViews() { // get the transaction with the intent and update the Views with the transaction's attributes
        etDescription = (EditText) findViewById(R.id.etDescription);
        rgType = (RadioGroup) findViewById(R.id.rgType);
        etAmount = (EditText) findViewById(R.id.etAmount);
        spinCategory = (Spinner) findViewById(R.id.spinCategory);
        tvDate = (TextView) findViewById(R.id.tvDate);

        int id = getIntent().getExtras().getInt("id");
        transaction = transactionsDb.findById(id); // check that line
        if (transaction == null) {
            Toast.makeText(this, R.string.msg_NoDataFound,
                    Toast.LENGTH_SHORT).show();
            return;
        }


        etDescription.setText(transaction.getDescription());
        etAmount.setText(transaction.getAmount());
        tvDate.setText(transaction.getDate());

        setupTypeRadioGroup();


    }


    private void setupTypeRadioGroup(){

        int index = 0;
        if(transaction.getType().equals("Income")){
            index = 1;
        }
        ((RadioButton) rgType.getChildAt(index)).setChecked(true); // we check one of the buttons in the type button group
    }
    private void setupCategoriesSpinner(){

        // Initializing a String Array
        String[] categories = new String[]{
                "New category",
                "Nightlife",
                "Dinner",
                "Housing",
                "Health",
                "Select category..."
        };

        final List<String> categoriesList = new ArrayList<>(Arrays.asList(categories));

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoriesList) {

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
                return super.getCount() - 1; // you don't display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String category = transaction.getCategory();

        int counter = categoriesList.indexOf(category);
        if(counter == -1) {
            counter = 2;
        }
        spinCategory.setAdapter(adapter);
        spinCategory.setSelection(counter); //display initially chosen category
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    // Set an EditText view to get user input
                    final EditText input = new EditText(UpdateActivity.this);

                    new AlertDialog.Builder(UpdateActivity.this)
                            .setMessage("New category")
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Editable editable = input.getText();

                                    categoriesList.add(1, editable.toString());
                                    adapter.notifyDataSetChanged();
                                    spinCategory.setSelection(1);

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Do nothing.
                                }
                            }).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupCalendar(){

        tvDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });



        year = Integer.parseInt(transaction.getYear());
        month = transaction.getMonthInt()+1;
        day = Integer.parseInt(transaction.getDay());

    }

    private void updateDateDisplay(){
        this.tvDate.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        //.append(mMonth + 1).append("-")
                        .append(getMonthForInt(month)).append(" ")
                        .append(day).append(", ")
                        .append(year).append(" "));
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


    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        year, month-1, day);
        }
        return null;
    }


    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    public void onUpdateClick(View view) {
        String description = etDescription.getText().toString().trim();

        int radioButtonID = rgType.getCheckedRadioButtonId();
        RadioButton rbType = (RadioButton) rgType.findViewById(radioButtonID);
        String type = rbType.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();
        String date = tvDate.getText().toString().trim();
        String category = spinCategory.getItemAtPosition(spinCategory.getSelectedItemPosition()).toString().trim();



        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setCategory(category);
        transaction.setDescription(description);

        int count = transactionsDb.update(transaction);
        Toast.makeText(this, count + " " + getString(R.string.msg_RowUpdated),
                Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onCancelClick(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transactionsDb != null) {
            transactionsDb.close();
        }
    }


}
