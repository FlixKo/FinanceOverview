package com.example.capstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.model.Stock;
import com.example.capstoneproject.model.StockViewModel;
import com.example.capstoneproject.utils.AppExecutors;
import com.example.capstoneproject.utils.JSONUtils;
import com.example.capstoneproject.utils.NetworkUtils;
import com.example.capstoneproject.utils.SearchStocksOnNetwork;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity implements SearchResultsAdapter.SearchResultsAdapterOnClickHandler {

    private static String LOG_TAG = SearchResultsActivity.class.getName();
    private StockViewModel stockViewModel;
    private RecyclerView recyclerView;
    private SearchResultsAdapter searchAdapter;
    private ProgressBar progressBar;
    private TextView errorText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_activity);
        stockViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(
                getApplication()).create(StockViewModel.class);

        setTitle(getString(R.string.search));

        recyclerView = findViewById(R.id.search_results_recycler_view);
        ArrayList<Stock> emptyList = new ArrayList<>();
        searchAdapter = new SearchResultsAdapter(emptyList, this, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);
        progressBar = findViewById(R.id.pb_loading_indicator_search);
        errorText = findViewById(R.id.tv_error_message_display_search);

        final EditText editText = findViewById(R.id.search_symbol_input);
        final Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String searchText = editText.getText().toString().trim();
                Log.d(LOG_TAG, "EditText input: " + searchText);

                if(NetworkUtils.isConnected(getApplicationContext())){
                    AppExecutors.getInstance().networkIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<Stock> searchResultStocks = search(searchText);

                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(searchResultStocks != null){
                                        setUpView(searchResultStocks);
                                    }else{
                                        setUpErrorView();
                                    }
                                }
                            });

                        }
                    });


                    //SearchStocksOnNetwork.searchStocks(getApplicationContext(),searchText);
                    //searchStocks();
                }else{
                    showNoNetworkErrorMessage();
                }

            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

    }

    private void setUpView(List<Stock> stocks){
        progressBar.setVisibility(View.INVISIBLE);
        setTitle(getString(R.string.search_results));
        searchAdapter.setStocksList(stocks);
        searchAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    private void setUpErrorView(){
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        errorText.setText(R.string.no_search_result);
        errorText.setVisibility(View.VISIBLE);
        Log.e(LOG_TAG, "No results found");
    }

    private void showNoNetworkErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorText.setText(getString(R.string.no_network_search));
        errorText.setVisibility(View.VISIBLE);
        Log.e(LOG_TAG, "No network connection");
    }

    private List<Stock> search(String searchText){
        List<Stock> stocks =  null;
        try{
            URL searchStockUrl = NetworkUtils.buildSearchUrl(searchText);
            stocks = JSONUtils.extractStockFromSeachString(NetworkUtils.getResponseFromHttpUrl(searchStockUrl));
        }catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stocks;
    }

    private void searchStocks() {
        stockViewModel.getStocks().observe(this, new Observer<List<Stock>>() {
            @Override
            public void onChanged(List<Stock> stocks) {

                progressBar.setVisibility(View.INVISIBLE);
                if(stocks != null){
                    setTitle(getString(R.string.search_results));
                    searchAdapter.setStocksList(stocks);
                    searchAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    errorText.setVisibility(View.INVISIBLE);
                }else{
                    recyclerView.setVisibility(View.INVISIBLE);
                    errorText.setText(R.string.no_search_result);
                    errorText.setVisibility(View.VISIBLE);
                    Log.e(LOG_TAG, "No results found");
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
