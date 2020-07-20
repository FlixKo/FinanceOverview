package com.example.capstoneproject.utils;

import android.content.Context;
import android.util.Log;

import com.example.capstoneproject.database.StockDatabase;
import com.example.capstoneproject.model.Stock;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class FetchStockFromNetwork {
    private static final String LOG_TAG = FetchStockFromNetwork.class.getSimpleName();

    public static void getStock(Context context, Stock stock, String stockSymbol) {
        URL getStockUrl = NetworkUtils.buildUrl(stockSymbol);
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
                        JSONUtils.extractStockFromJSON(stockResult, stock);
                            Log.d(LOG_TAG, stock.getName() + "; symbol: " + stock.getSymbol());
                             Stock dbStock = stockDatabase.stockDao().loadStockBySymbol(stock.getSymbol());
                            if (dbStock != null) {
                                //networkMovie.setFavorite(dbMovie.isFavorite());
                                //networkMovie.setFavorite(false);
                                stockDatabase.stockDao().updateStock(stock);
                            } else {
                                stockDatabase.stockDao().insertStock(stock);
                            }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
