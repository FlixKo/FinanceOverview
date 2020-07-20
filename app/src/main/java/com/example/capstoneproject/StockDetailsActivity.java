package com.example.capstoneproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstoneproject.database.StockDatabase;
import com.example.capstoneproject.model.Stock;
import com.example.capstoneproject.model.StockViewModel;
import com.example.capstoneproject.utils.AppExecutors;
import com.example.capstoneproject.utils.FetchStockFromNetwork;

import java.util.Objects;

public class StockDetailsActivity extends AppCompatActivity {
    private static String LOG_TAG = StockDetailsActivity.class.getName();

    Stock stock;
    TextView stock_name;
    TextView stock_symbol;
    TextView stock_price;
    EditText edit_shares;
    private StockViewModel stockViewModel;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_details_activity);
        stock_name = findViewById(R.id.stock_name);
        stock_symbol = findViewById(R.id.stock_symbol);
        stock_price = findViewById(R.id.stock_price);
        edit_shares = findViewById(R.id.number_of_shares_owned);
        stockViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(
                getApplication()).create(StockViewModel.class);

        final StockDatabase stockDatabase = StockDatabase.getInstance(getApplicationContext());

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("stock")) {
                stock = Objects.requireNonNull(intentThatStartedThisActivity.getExtras()).getParcelable("stock");
                setTitle(stock.getName());
                FetchStockFromNetwork.getStock(getApplicationContext(), stock);
                getStock(stock.getSymbol());
            }
        }

        final Button storeStocksButton = findViewById(R.id.save_store_details_button);
        storeStocksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String numberShares = edit_shares.getText().toString().trim();
                Log.d(LOG_TAG, "NumberShares input: " + numberShares);

                stock.setNumberShares(Double.parseDouble(numberShares));

                AppExecutors.getInstance().networkIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        StockDatabase stockDatabase = StockDatabase.getInstance(getApplicationContext());
                        stockDatabase.stockDao().updateStock(stock);
                    }
                });

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getStock(String symbol) {
        stockViewModel.getStock(symbol).observe(this, new Observer<Stock>() {
            @Override
            public void onChanged(Stock stock) {
                if (stock != null) {
                    stock_name.setText(stock.getLatest_trading_day());
                    stock_symbol.setText(stock.getSymbol());
                    stock_price.setText(Double.toString(stock.getPrice()));
                    edit_shares.setText(Double.toString(stock.getNumberShares()));
                }

            }
        });
    }
}
