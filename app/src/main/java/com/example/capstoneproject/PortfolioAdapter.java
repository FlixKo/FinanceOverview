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


public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.PortfolioHolder> {

    private List<Stock> stocks;
    private final PortfolioAdapterOnClickHandler clickHandler;

    private final Context ctx;

    public PortfolioAdapter(List<Stock> stocks, PortfolioAdapterOnClickHandler clickHandler, Context ctx) {
        this.stocks = stocks;
        this.clickHandler = clickHandler;
        this.ctx = ctx;
    }


    void setStocksList(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public interface PortfolioAdapterOnClickHandler {
        void onClick(Stock stock);
    }

    @NonNull
    @Override
    public PortfolioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(ctx);
        View myView = myInflater.inflate(R.layout.portfolio_element, parent, false);
        return new PortfolioHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioHolder holder, int position) {
        holder.nameTextView.setText(stocks.get(position).getName() + " (" + stocks.get(position).getSymbol() + ")");
        double value = stocks.get(position).getNumberShares() * stocks.get(position).getPrice();
        holder.valueTextView.setText(Double.toString(value) + " " + stocks.get(position).getCurrency());
        holder.numShares.setText(Double.toString(stocks.get(position).getNumberShares()));
        holder.currentPrice.setText(Double.toString(stocks.get(position).getPrice()));
        holder.currency.setText(stocks.get(position).getCurrency());
    }

    @Override
    public int getItemCount() {
        return stocks != null ? stocks.size() : 0;
    }

    public class PortfolioHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView nameTextView;
        final TextView valueTextView;
        final TextView numShares;
        final TextView currentPrice;
        final TextView currency;

        public PortfolioHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.portfolio_text_view);
            valueTextView = itemView.findViewById(R.id.value_text_view);
            numShares = itemView.findViewById(R.id.number_of_shares_owned_text_view);
            currentPrice = itemView.findViewById(R.id.currency_text_view);
            currency = itemView.findViewById(R.id.currency_text_view);
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
