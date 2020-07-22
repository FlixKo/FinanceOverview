package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.capstoneproject.database.StockDatabase;
import com.example.capstoneproject.model.Stock;
import com.example.capstoneproject.utils.AppExecutors;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PortfolioAdapter.PortfolioAdapterOnClickHandler{


    private static String LOG_TAG = MainActivity.class.getName();
    AnyChartView anyChartView;
    PortfolioAdapter portfolioAdapter;
    RecyclerView portfolioView;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sAnalytics = GoogleAnalytics.getInstance(this);
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

                    portfolioAdapter.setStocksList(dbStock);
                    portfolioView.setAdapter(portfolioAdapter);

                    anyChartView.post(new Runnable() {

                        @Override
                        public void run() {
                            Pie pie = AnyChart.pie();
                            List<DataEntry> elements = new ArrayList<>();
                            for (int i = 0; i < dbStock.size(); i++){
                                Log.d(LOG_TAG,"Name: " + dbStock.get(i).getName() + "Price: " + dbStock.get(i).getPrice());
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

        Tracker mTracker = this.getDefaultTracker();
        Log.i(LOG_TAG, "Setting screen name: " + MainActivity.class);
        mTracker.setScreenName("Image~" + MainActivity.class);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onClick(Stock stock) {
        Log.d(LOG_TAG,"clicked on " + stock.getName());
        Intent intent = new Intent(getApplicationContext(), StockDetailsActivity.class);
        intent.putExtra("stock", stock);
        startActivity(intent);
    }

    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}