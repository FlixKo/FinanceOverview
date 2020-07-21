package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.capstoneproject.database.StockDatabase;
import com.example.capstoneproject.model.Stock;
import com.example.capstoneproject.model.StockViewModel;
import com.example.capstoneproject.utils.AppExecutors;
import com.example.capstoneproject.utils.SearchStocksOnNetwork;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PortfolioAdapter.PortfolioAdapterOnClickHandler{


    private static String LOG_TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                startActivity(intent);
            }
        });

        ArrayList<Stock> emptyList = new ArrayList<>();
        final RecyclerView portfolioView = findViewById(R.id.main_portfolio_recycler_view);
        final PortfolioAdapter portfolioAdapter = new PortfolioAdapter(emptyList, this, getApplicationContext());
        portfolioView.setLayoutManager(new LinearLayoutManager(this));
        portfolioView.setAdapter(portfolioAdapter);

        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                StockDatabase stockDatabase = StockDatabase.getInstance(getApplicationContext());
                List<Stock> dbStock = stockDatabase.stockDao().loadPortfolio();
                if(dbStock != null){
                    //TextView textView = findViewById(R.id.hello_world_text_view);
                    //textView.setText("Name: " + dbStock.getName() + "Price: " + dbStock.getPrice());
                    Log.d(LOG_TAG,"Name: " + dbStock.get(0).getName() + "Price: " + dbStock.get(0).getPrice());
                    portfolioAdapter.setStocksList(dbStock);
                    portfolioView.setAdapter(portfolioAdapter);
                }
            }
        });

    }

    private void showNoNetworkErrorMessage() {
        //scrollView.setVisibility(View.INVISIBLE);
        //mErrorMessageDisplay.setText(getString(R.string.no_network));
        //mErrorMessageDisplay.setVisibility(View.VISIBLE);
        Log.e(LOG_TAG,"No Network");
    }

    @SuppressWarnings("deprecation")
    private boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    return (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) ||
                            (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(Stock stock) {
        Log.d(LOG_TAG,"clicked on " + stock.getName());
    }
}