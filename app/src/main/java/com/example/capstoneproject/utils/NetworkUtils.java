package com.example.capstoneproject.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static String LOG_TAG = NetworkUtils.class.getName();

//https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=demo
    // https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=BA&apikey=demo
    private final static String API_KEY_PARAM = "apikey";
    //private final static String API_KEY = "4WPCI0VKCTPEKSVL";
    private final static String API_KEY = "LHAVWG8KRM9KG1LH";
    private final static String BASE_URL = "https://www.alphavantage.co/query?";
    private final static String FUNCTION_PARAM = "function";
    private final static String FUNCTION = "GLOBAL_QUOTE";
    private final static String FUNCTION_SEARCH = "SYMBOL_SEARCH";
    private final static String KEYWORDS_PARAM = "keywords";
    private final static String SYMBOL_PARAM = "symbol";

    public static URL buildUrl(String symbol) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(FUNCTION_PARAM, FUNCTION)
                .appendQueryParameter(SYMBOL_PARAM, symbol)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "URL: " + url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildSearchUrl(String keyword) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(FUNCTION_PARAM, FUNCTION_SEARCH)
                .appendQueryParameter(KEYWORDS_PARAM, keyword)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "URL: " + url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}