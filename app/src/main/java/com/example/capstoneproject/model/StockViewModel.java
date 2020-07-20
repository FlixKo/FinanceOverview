package com.example.capstoneproject.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.capstoneproject.database.StockDatabase;

import java.util.List;

public class StockViewModel extends AndroidViewModel {
    private static final String LOG_TAG = StockViewModel.class.getSimpleName();
    private LiveData<List<Stock>> stocks;
    private StockDatabase stockDatabase;

    public StockViewModel(@NonNull Application application) {
        super(application);
        stockDatabase = StockDatabase.getInstance(this.getApplication());
        Log.d(LOG_TAG, "Getting the database instance");
        stocks = stockDatabase.stockDao().loadAllStocks();

    }

    public LiveData<List<Stock>> getStocks() {
        return stocks;
    }

    public LiveData<Stock> getStock(String symbol) {
        return stockDatabase.stockDao().loadLiveStockBySymbol(symbol);
    }
}
