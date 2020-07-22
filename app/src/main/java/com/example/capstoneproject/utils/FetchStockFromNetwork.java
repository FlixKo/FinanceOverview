package com.example.capstoneproject.utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.capstoneproject.database.StockDatabase;
import com.example.capstoneproject.model.Stock;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class FetchStockFromNetwork {
    private static final String LOG_TAG = FetchStockFromNetwork.class.getSimpleName();

    public static void getStock(Context context, Stock stock) {
        URL getStockUrl = NetworkUtils.buildUrl(stock.getSymbol());
        getStockDetails(getStockUrl, context, stock);
    }

    private static void getStockDetails(final URL url, final Context context, final Stock stock) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                StockDatabase stockDatabase = StockDatabase.getInstance(context);

                String stockResult = null;
                try {
                    stockResult = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (stockResult != null) {
                    try {
                        Stock updatedStock = JSONUtils.extractStockFromJSON(stockResult, stock);
                        Log.d(LOG_TAG, updatedStock.getSymbol() + "; price: " + updatedStock.getPrice());
                        Stock dbStock = stockDatabase.stockDao().loadStockBySymbol(updatedStock.getSymbol());
                        if (dbStock != null) {
                            updatedStock.setNumberShares(dbStock.getNumberShares());
                            stockDatabase.stockDao().deleteStock(dbStock);
                        }
                        long result = stockDatabase.stockDao().insertStock(updatedStock);
                        Log.d(LOG_TAG, "insertion result: " + result);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
