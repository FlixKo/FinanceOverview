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

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
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
    AnyChartView anyChartView;
    PortfolioAdapter portfolioAdapter;
    RecyclerView portfolioView;

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

        anyChartView = findViewById(R.id.any_chart_view);
        //setUpPieChart(dbStock);

        ArrayList<Stock> emptyList = new ArrayList<>();
        portfolioView = findViewById(R.id.main_portfolio_recycler_view);
        portfolioAdapter = new PortfolioAdapter(emptyList, this, getApplicationContext());
        portfolioView.setLayoutManager(new LinearLayoutManager(this));
        portfolioView.setAdapter(portfolioAdapter);

        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                StockDatabase stockDatabase = StockDatabase.getInstance(getApplicationContext());
                final List<Stock> dbStock = stockDatabase.stockDao().loadPortfolio();
                if(dbStock != null && dbStock.size() > 0){
                    Log.d(LOG_TAG,"Name: " + dbStock.get(0).getName() + "Price: " + dbStock.get(0).getPrice());
                    portfolioAdapter.setStocksList(dbStock);
                    portfolioView.setAdapter(portfolioAdapter);

                    anyChartView.post(new Runnable() {

                        @Override
                        public void run() {
                            Pie pie = AnyChart.pie();
                            List<DataEntry> elements = new ArrayList<>();
                            for (int i = 0; i < dbStock.size(); i++){
                                Double value = dbStock.get(i).getNumberShares() * dbStock.get(i).getPrice();
                                elements.add(new ValueDataEntry(dbStock.get(i).getName(),value));
                            }
                            pie.data(elements);
                            anyChartView.setChart(pie);
                        }
                    });
                }
            }
        });

    }

    public void setUpPieChart(List<Stock> stocks){
        Pie pie = AnyChart.pie();
        List<DataEntry> elements = new ArrayList<>();
        for (int i = 0; i < stocks.size(); i++){
            elements.add(new ValueDataEntry(stocks.get(i).getSymbol(),stocks.get(i).getNumberShares()));
        }
        pie.data(elements);
        anyChartView.setChart(pie);
    }

    private void showNoNetworkErrorMessage() {
        //scrollView.setVisibility(View.INVISIBLE);
        //mErrorMessageDisplay.setText(getString(R.string.no_network));
        //mErrorMessageDisplay.setVisibility(View.VISIBLE);
        Log.e(LOG_TAG,"No Network");
    }

    @Override
    public void onClick(Stock stock) {
        Log.d(LOG_TAG,"clicked on " + stock.getName());
        Intent intent = new Intent(getApplicationContext(), StockDetailsActivity.class);
        intent.putExtra("stock", stock);
        startActivity(intent);
    }
}