package com.example.capstoneproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.model.Stock;

import java.util.List;


public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsHolder> {

    private List<Stock> stocks;
    private final SearchResultsAdapterOnClickHandler clickHandler;
    private final String LOG_TAG = SearchResultsAdapter.class.getName();

    private final Context ctx;

    public SearchResultsAdapter(List<Stock> stocks, SearchResultsAdapterOnClickHandler clickHandler, Context ctx) {
        this.stocks = stocks;
        this.clickHandler = clickHandler;
        this.ctx = ctx;
    }


    void setStocksList(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public interface SearchResultsAdapterOnClickHandler {
        void onClick(Stock stock);
    }

    @NonNull
    @Override
    public SearchResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(ctx);
        View myView = myInflater.inflate(R.layout.search_results_element, parent, false);
        return new SearchResultsHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsHolder holder, int position) {
        holder.textView.setText(stocks.get(position).getName() + "(" + stocks.get(position).getSymbol() + ")");
    }

    @Override
    public int getItemCount() {
        return stocks != null ? stocks.size() : 0;
    }

    public class SearchResultsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView textView;

        public SearchResultsHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.search_results_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Stock currentStock = stocks.get(adapterPosition);
            clickHandler.onClick(currentStock);
        }
    }
}
