package com.example.capstoneproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstoneproject.database.StockDatabase;
import com.example.capstoneproject.model.Stock;
import com.example.capstoneproject.model.StockViewModel;
import com.example.capstoneproject.utils.AppExecutors;
import com.example.capstoneproject.utils.FetchStockFromNetwork;
import com.example.capstoneproject.utils.NetworkUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailsActivity extends AppCompatActivity {
    private static String LOG_TAG = StockDetailsActivity.class.getName();

    Stock stock;
    private StockViewModel stockViewModel;
    StockDatabase stockDatabase;

    @BindView(R.id.stock_name)
    TextView stock_name;
    @BindView(R.id.stock_symbol)
    TextView stock_symbol;
    @BindView(R.id.stock_price)
    TextView stock_price;
    @BindView(R.id.number_of_shares_owned)
    EditText edit_shares;
    @BindView(R.id.pb_loading_indicator_details)
    ProgressBar progressBar;
    @BindView(R.id.tv_error_message_display_details)
    TextView errorText;
    @BindView(R.id.stock_region)
    TextView stock_region;
    @BindView(R.id.stock_currency)
    TextView stock_currency;
    @BindView(R.id.save_store_details_button)
    Button storeStocksButton;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_details_activity);
        ButterKnife.bind(this);

        stockViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(
                getApplication()).create(StockViewModel.class);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("stock")) {
                stock = Objects.requireNonNull(intentThatStartedThisActivity.getExtras()).getParcelable("stock");
                if (stock != null) {
                    setTitle(stock.getName());
                    progressBar.setVisibility(View.VISIBLE);
                    if (NetworkUtils.isConnected(getApplicationContext())) {
                        FetchStockFromNetwork.getStock(getApplicationContext(), stock);
                        getStock(stock.getSymbol());
                    } else {
                        showNoNetworkErrorMessage();
                        Log.e(LOG_TAG, "no network connection");
                    }
                } else {
                    errorText.setText(getString(R.string.error_message));
                    setErrorVisible();
                    Log.e(LOG_TAG, "Empty stock object delivered by " + SearchResultsActivity.class);
                }

            }
        }

        storeStocksButton.setOnClickListener(view -> {

            String numberShares = edit_shares.getText().toString().trim();
            Log.d(LOG_TAG, "NumberShares input: " + numberShares);

            if (stock != null) {

                AppExecutors.getInstance().networkIO().execute(() -> {
                    stockDatabase = StockDatabase.getInstance(getApplicationContext());
                    Stock dbStock = stockDatabase.stockDao().loadStockBySymbol(stock.getSymbol());
                    dbStock.setNumberShares(Double.parseDouble(numberShares));
                    //int result =
                    stockDatabase.stockDao().updateStock(dbStock);
                    //Log.d(LOG_TAG,"update result: " + result);

                });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Log.e(LOG_TAG, "Stock value is null");
            }
        });
    }

    private void showNoNetworkErrorMessage() {
        errorText.setText(getString(R.string.no_network_details));
        setErrorVisible();
        Log.e(LOG_TAG, String.valueOf(R.string.no_network_details));
    }

    private void getStock(String symbol) {
        stockViewModel.getStock(symbol).observe(this, stock -> {
            progressBar.setVisibility(View.INVISIBLE);
            if (stock != null) {
                setErrorInvisible();
                stock_name.setText(stock.getName());
                stock_symbol.setText(stock.getSymbol());
                stock_price.setText(Double.toString(stock.getPrice()).trim());
                stock_currency.setText(stock.getCurrency());
                stock_region.setText(stock.getRegion());
                Log.d(LOG_TAG, "on changed: number shares: " + stock.getNumberShares());
                edit_shares.setText(Double.toString(stock.getNumberShares()));
            } else {
                setErrorVisible();
            }

        });
    }

    private void setErrorInvisible() {
        stock_name.setVisibility(View.VISIBLE);
        stock_symbol.setVisibility(View.VISIBLE);
        stock_price.setVisibility(View.VISIBLE);
        edit_shares.setVisibility(View.VISIBLE);
        stock_region.setVisibility(View.VISIBLE);
        stock_currency.setVisibility(View.VISIBLE);
        edit_shares.setVisibility(View.VISIBLE);
        storeStocksButton.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    private void setErrorVisible() {
        stock_name.setVisibility(View.INVISIBLE);
        stock_symbol.setVisibility(View.INVISIBLE);
        stock_price.setVisibility(View.INVISIBLE);
        stock_currency.setVisibility(View.INVISIBLE);
        stock_region.setVisibility(View.INVISIBLE);
        edit_shares.setVisibility(View.INVISIBLE);
        storeStocksButton.setVisibility(View.INVISIBLE);
        errorText.setText(R.string.no_details_for_stock);
        errorText.setVisibility(View.VISIBLE);
    }
}
