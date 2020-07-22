package com.example.capstoneproject.utils;

import android.util.Log;

import com.example.capstoneproject.model.Stock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {
    private static String LOG_TAG = JSONUtils.class.getName();

    private final static String JSON_BEST_MATCHES = "bestMatches";
    private final static String JSON_GLOBAL_QUOTE = "Global Quote";
    private final static String JSON_SYMBOL_SEARCH = "1. symbol";
    private final static String JSON_OPEN = "02. open";
    private final static String JSON_HIGH = "03. high";
    private final static String JSON_LOW = "04. low";
    private final static String JSON_PRICE = "05. price";
    private final static String JSON_VOLUME = "06. volume";
    private final static String JSON_LATEST_TRADING_DAY = "07. latest trading day";
    private final static String JSON_PREVIOUS_CLOSE = "08. previous close";
    private final static String JSON_CHANGE = "09. change";
    private final static String JSON_CHANGE_PERCENT = "10. change percent";
    private final static String JSON_NAME = "2. name";
    private final static String JSON_TYPE = "3. type";
    private final static String JSON_REGION = "4. region";
    private final static String JSON_MARKET_OPEN = "5. marketOpen";
    private final static String JSON_MARKET_CLOSE = "6. marketClose";
    private final static String JSON_TIMEZONE = "7. timezone";
    private final static String JSON_CURRENCY = "8. currency";
    private final static String JSON_MATCH_SCORE = "9. matchScore";

    public static Stock extractStockFromJSON(String mJsonString, Stock stock) throws JSONException {
        if(mJsonString == null || mJsonString.length() == 0){
            Log.e(LOG_TAG,"JSON String is " + mJsonString);
            return null;
        }
        JSONObject jsonString = new JSONObject(mJsonString);
        JSONObject result = jsonString.getJSONObject(JSON_GLOBAL_QUOTE);

        if(result.length() == 0){
            Log.e(LOG_TAG,"Empty JSON response");
            return null;
        }
        double open = result.optDouble(JSON_OPEN);
        double high = result.optDouble(JSON_HIGH);
        double low = result.optDouble(JSON_LOW);
        double price = result.getDouble(JSON_PRICE);
        long volume = result.optLong(JSON_VOLUME);
        String latest_trading_day = result.optString(JSON_LATEST_TRADING_DAY);
        double previous_close = result.optDouble(JSON_PREVIOUS_CLOSE);
        double change = result.optDouble(JSON_CHANGE);
        String change_percent = result.getString(JSON_CHANGE_PERCENT);
        stock.setOpen(open);
        stock.setHigh(high);
        stock.setLow(low);
        stock.setPrice(price);
        stock.setVolume(volume);
        stock.setLatest_trading_day(latest_trading_day);
        stock.setPrevious_close(previous_close);
        stock.setChange(change);
        stock.setChange_percent(change_percent);
        return stock;
    }


    public static ArrayList<Stock> extractStockFromSeachString(String mJsonString) throws JSONException {
        if(mJsonString == null || mJsonString.length() == 0){
            Log.e(LOG_TAG,"JSON String is " + mJsonString);
            return null;
        }

        JSONObject jsonString = new JSONObject(mJsonString);
        JSONArray resultsArray = jsonString.getJSONArray(JSON_BEST_MATCHES);

        if(resultsArray.length() == 0){
            Log.e(LOG_TAG,"Empty JSON response");
            return null;
        }
        ArrayList<Stock> stockSearchResultsArray = new ArrayList<>();

        String symbol;
        String name;
        String type;
        String region;
        String marketOpen;
        String markedClose;
        String timezone;
        String currency;
        String matchScore;

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject currentStock = resultsArray.getJSONObject(i);
            symbol = currentStock.getString(JSON_SYMBOL_SEARCH);
            name = currentStock.getString(JSON_NAME);
            type = currentStock.getString(JSON_TYPE);
            region = currentStock.getString(JSON_REGION);
            marketOpen = currentStock.optString(JSON_MARKET_OPEN);
            markedClose = currentStock.optString(JSON_MARKET_CLOSE);
            timezone = currentStock.optString(JSON_TIMEZONE);
            currency = currentStock.getString(JSON_CURRENCY);
            matchScore = currentStock.optString(JSON_MATCH_SCORE);
            stockSearchResultsArray.add(new Stock(symbol, name, type, region, marketOpen, markedClose, timezone, currency, matchScore));
        }

        return stockSearchResultsArray;
    }
}
