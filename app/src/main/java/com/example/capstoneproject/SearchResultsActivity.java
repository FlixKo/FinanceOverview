package com.example.capstoneproject;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.model.Stock;
import com.example.capstoneproject.model.StockViewModel;
import com.example.capstoneproject.utils.SearchStocksOnNetwork;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity implements SearchResultsAdapter.SearchResultsAdapterOnClickHandler {

    private static String LOG_TAG = SearchResultsActivity.class.getName();
    private StockViewModel stockViewModel;
    private RecyclerView recyclerView;
    private SearchResultsAdapter searchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_activity);
        stockViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(
                getApplication()).create(StockViewModel.class);

        recyclerView = findViewById(R.id.search_results_recycler_view);
        ArrayList<Stock> emptyList = new ArrayList<>();
        searchAdapter = new SearchResultsAdapter(emptyList, this, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);
        String searchTerm = "BMW";
        SearchStocksOnNetwork.searchStocks(getApplicationContext(),searchTerm);
        searchStocks();
    }

    private void searchStocks() {
        stockViewModel.getStocks().observe(this, new Observer<List<Stock>>() {
            @Override
            public void onChanged(List<Stock> stocks) {
                setTitle(getString(R.string.search_results));
                if(stocks != null){
                    searchAdapter.setStocksList(stocks);
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(Stock stock) {
        Log.d(LOG_TAG, stock.getName() + " clicked");
    }
}
