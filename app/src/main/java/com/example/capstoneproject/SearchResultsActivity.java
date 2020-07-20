package com.example.capstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        setTitle(getString(R.string.search_results));

        recyclerView = findViewById(R.id.search_results_recycler_view);
        ArrayList<Stock> emptyList = new ArrayList<>();
        searchAdapter = new SearchResultsAdapter(emptyList, this, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);

        final EditText editText = findViewById(R.id.search_symbol_input);
        final Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = editText.getText().toString();
                searchText.trim();
                Log.d(LOG_TAG, "EditText input: " + searchText);
                SearchStocksOnNetwork.searchStocks(getApplicationContext(),searchText);
                searchStocks();
            }
        });
    }

    private void searchStocks() {
        stockViewModel.getStocks().observe(this, new Observer<List<Stock>>() {
            @Override
            public void onChanged(List<Stock> stocks) {

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
        Intent intent = new Intent(getApplicationContext(), StockDetailsActivity.class);
        intent.putExtra("stock", stock);
        startActivity(intent);
    }
}
