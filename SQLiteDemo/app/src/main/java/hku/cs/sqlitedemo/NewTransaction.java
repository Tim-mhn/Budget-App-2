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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NewTransaction extends AppCompatActivity {


    private EditText etDescription;
    private RadioGroup rgType;
    private TransactionsDatabase sqliteHelper;
    private EditText etAmount;
    private Spinner spinCategory;
    private TextView tvDate;
    private Toolbar tbToolBar;

    private int year;
    private int month;
    private int day;

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

    private static final int REQUEST_TAKE_PICTURE = 0;
    static final int DATE_DIALOG_ID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_transaction);
        if (sqliteHelper == null) {
            sqliteHelper = new TransactionsDatabase(this);
        }
        findViews();
        setupCategoriesSpinner();
        setupCalendar();

    }



    private void findViews() {
        etDescription = (EditText) findViewById(R.id.etDescription);
        rgType = (RadioGroup) findViewById(R.id.rgType);
        etAmount = (EditText) findViewById(R.id.etAmount);
        spinCategory = (Spinner) findViewById(R.id.spinCategory);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tbToolBar = (Toolbar) findViewById(R.id.NewTransactionToolbar);
    }

    private void setupCategoriesSpinner(){


        // Instead of creating the category string list here, we fetch the data from the class Category
        // We just need to add the hint 'Select category' and the other option 'New Category' to the list
        List<Category> categories = Category.getCategories();
        final List<String> categoriesList = new ArrayList<>();
        categoriesList.add("New category");
        for(Category c : categories){
            categoriesList.add(c.getName());
        }
        categoriesList.add("Select category...");

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

        spinCategory.setAdapter(adapter);
        spinCategory.setSelection(adapter.getCount()); //display hint
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    // Set an EditText view to get user input
                    final EditText input = new EditText(NewTransaction.this);

                    new AlertDialog.Builder(NewTransaction.this)
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

        // get the current date
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplay();

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
                        year, month, day);
        }
        return null;
    }


    private boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }



    public void onInsertClick(View view) {
        String description = etDescription.getText().toString().trim();

        int radioButtonID = rgType.getCheckedRadioButtonId();

        String amount = etAmount.getText().toString().trim();


        String date = tvDate.getText().toString().trim();

        String category = spinCategory.getItemAtPosition(spinCategory.getSelectedItemPosition()).toString().trim();

        if (radioButtonID == -1 || amount.matches("") || date.matches("") || category.equals("Select category...")) {
            Toast.makeText(this, "Please complete all the fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rbType = (RadioButton) rgType.findViewById(radioButtonID);
        String type = rbType.getText().toString().trim();

        Transaction transaction = new Transaction(amount, description, type, category, date);


        long rowId = sqliteHelper.insert(transaction);
        if (rowId != -1) {
            Toast.makeText(this, R.string.msg_InsertSuccess,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.msg_InsertFail,
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void onCancelClick(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqliteHelper != null) {
            sqliteHelper.close();
        }
    }

}
