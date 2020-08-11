package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.capstoneproject.utils.AppExecutors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PortfolioAdapter.PortfolioAdapterOnClickHandler{


    private static String LOG_TAG = MainActivity.class.getName();
    AnyChartView anyChartView;
    PortfolioAdapter portfolioAdapter;
    RecyclerView portfolioView;
    TextView noStocks;
    TextView portfolioSize;
    TextView portfolioCurrency;
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

        noStocks = findViewById(R.id.no_stocks_text_view);
        noStocks.setVisibility(View.GONE);
        anyChartView = findViewById(R.id.any_chart_view);
        portfolioSize = findViewById(R.id.portfolio_size_text_view);
        portfolioCurrency = findViewById(R.id.portfolio_currency_text_view);
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
                            Double portfolioSizeAccumulated = 0.0;
                            for (int i = 0; i < dbStock.size(); i++){
                                Log.d(LOG_TAG,"Name: " + dbStock.get(i).getName() + "Price: " + dbStock.get(i).getPrice());
                                Double value = dbStock.get(i).getNumberShares() * dbStock.get(i).getPrice();
                                elements.add(new ValueDataEntry(dbStock.get(i).getName(),value));
                                portfolioSizeAccumulated += value;
                            }
                            pie.data(elements);
                            portfolioSize.setText(String.format("%.2f",portfolioSizeAccumulated));
                            portfolioCurrency.setText("EUR");
                            portfolioSize.setVisibility(View.VISIBLE);
                            anyChartView.setChart(pie);
                            anyChartView.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else{
                    anyChartView.setVisibility(View.INVISIBLE);
                    portfolioSize.setVisibility(View.INVISIBLE);
                    noStocks.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onClick(Stock stock) {
        Log.d(LOG_TAG,"clicked on " + stock.getName());
        Intent intent = new Intent(getApplicationContext(), StockDetailsActivity.class);
        intent.putExtra("stock", stock);
        startActivity(intent);
    }
}