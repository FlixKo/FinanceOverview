package com.example.capstoneproject.widget;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.capstoneproject.MainActivity;
import com.example.capstoneproject.R;
import com.example.capstoneproject.database.StockDao;
import com.example.capstoneproject.database.StockDatabase;
import com.example.capstoneproject.model.Stock;

import java.util.List;

public class PortfolioWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static String LOG_TAG = PortfolioWidgetRemoteViewsFactory.class.getName();
    private Context mContext;
    private StockDatabase stockDatabase;
    private StockDao stockDao;
    private List<Stock> stocks;

    public PortfolioWidgetRemoteViewsFactory(Context context) {
        this.mContext = context;
        stockDatabase = StockDatabase.getInstance(context.getApplicationContext());
        stockDao = stockDatabase.stockDao();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        stocks = stockDao.loadPortfolio();
        Log.d(LOG_TAG,"portfolio size: " + stocks.size());
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return stocks == null ? 0 : stocks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.portfolio_text_view, stocks.get(position).getName());

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return stocks.get(i).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
