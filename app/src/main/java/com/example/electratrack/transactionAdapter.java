package com.example.electratrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public transactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.transactionAmount.setText("₹ " + transaction.getAmount());
        holder.transactionCategory.setText(transaction.getCategory());
        holder.transactionDescription.setText(transaction.getDescription());
        holder.transactionDate.setText(transaction.getDate());  // Bind date
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionAmount, transactionCategory, transactionDescription, transactionDate;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);
            transactionCategory = itemView.findViewById(R.id.transactionCategory);
            transactionDescription = itemView.findViewById(R.id.transactionDescription);
            transactionDate = itemView.findViewById(R.id.transactionDate);  // Initialize date TextView
        }
    }
}
