package hku.cs.sqlitedemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class MainTransactions extends AppCompatActivity {
    private TransactionsDatabase sqliteHelper;
    private TransactionsAdapter transactionsAdapter;
    private RecyclerView rvSpots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity2);
        if (sqliteHelper == null) {
            sqliteHelper = new TransactionsDatabase(this);
        }
        rvSpots = (RecyclerView) findViewById(R.id.rvSpots);
        rvSpots.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Transaction> spotList = getSpotList();
        if (spotList.size() <= 0) {
            Toast.makeText(
                    this, R.string.text_NoDataFound, Toast.LENGTH_SHORT
            ).show();
        }

        if (transactionsAdapter == null) {
            transactionsAdapter = new TransactionsAdapter(this, spotList);
            rvSpots.setAdapter(transactionsAdapter);
        } else {
            transactionsAdapter.setSpotList(spotList);
            transactionsAdapter.notifyDataSetChanged();
        }

    }

    public List<Transaction> getSpotList() {
        return sqliteHelper.getAllSpots();
    }

    public void onInsertClick(View view) {
        Intent intent = new Intent(this, NewTransaction.class);
        startActivity(intent);
    }

    private class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.SpotViewHolder> {
        Context context;
        List<Transaction> spotList;

        void setSpotList(List<Transaction> spotList) {
            this.spotList = spotList;
        }

        TransactionsAdapter(Context context, List<Transaction> spotList) {
            this.context = context;
            this.spotList = spotList;
        }

        class SpotViewHolder extends RecyclerView.ViewHolder {
            //ImageView ivSpot;
            TextView tvAmount, tvCategory, tvDescription, tvDate;

            SpotViewHolder(View itemView) {
                super(itemView);
                tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
                tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
                tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);

            }
        }

        @Override
        public int getItemCount() {
            return spotList.size();
        }

        @Override
        public SpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.transaction_view, parent, false);
            return new SpotViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SpotViewHolder holder, int position) {
            final Transaction transaction = spotList.get(position);

            // We have to implement the R string currency !
            holder.tvAmount.setText("$" + transaction.getAmount());
            //holder.tvAmount.append(R.string.currency);
            holder.tvCategory.setText(transaction.getCategory());
            holder.tvDescription.setText(transaction.getDescription());
            holder.tvDate.setText(transaction.getDate());


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
                    spotList = sqliteHelper.getAllSpots();
                    notifyDataSetChanged();
                    return true;
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqliteHelper != null) {
            sqliteHelper.close();
        }
    }

}
