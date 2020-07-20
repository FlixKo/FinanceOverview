package com.example.capstoneproject.utils;

import android.content.Context;
import android.util.Log;

import com.example.capstoneproject.database.StockDatabase;
import com.example.capstoneproject.model.Stock;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SearchStocksOnNetwork {
    private static final String LOG_TAG = SearchStocksOnNetwork.class.getSimpleName();

    public static void searchStocks(Context context, String searchTerm) {
        URL searchStockUrl = NetworkUtils.buildSearchUrl(searchTerm);
        search(searchStockUrl, context);
    }

    private static void search(final URL url, final Context context) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                StockDatabase stockDatabase = StockDatabase.getInstance(context);
                stockDatabase.stockDao().deleteAllStocks();
                String stocksResult = null;
                try {
                    stocksResult = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (stocksResult != null) {
                    try {
                        List<Stock> stocks = JSONUtils.extractStockFromSeachString(stocksResult);
                        Log.d(LOG_TAG, "search result size: " + stocks.size());
                        for (int i = 0; i < stocks.size(); i++) {
                            Stock networkStock = stocks.get(i);
                            Log.d(LOG_TAG, "name: ["+networkStock.getName() + "]; symbol: [" + networkStock.getSymbol() + "]; region: " + networkStock.getRegion());
                            Stock dbStock = stockDatabase.stockDao().loadStockBySymbol(networkStock.getSymbol());
                            if (dbStock != null) {
                                stockDatabase.stockDao().updateStock(networkStock);
                            } else {
                                stockDatabase.stockDao().insertStock(networkStock);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
