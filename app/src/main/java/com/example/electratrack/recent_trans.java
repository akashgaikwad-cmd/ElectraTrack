package com.example.electratrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class recent_trans extends AppCompatActivity {

    private RecyclerView recyclerView;
    private transactionAdapter adapter;
    private List<Transaction> transactionList;
    private FirebaseAuth auth;
    private ImageView back,myImageview,report;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_transactions);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userID = currentUser.getUid();

        recyclerView = findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        back = findViewById(R.id.back);
        myImageview=findViewById(R.id.myImageView);
        report=findViewById(R.id.report);
        transactionList = new ArrayList<>();
        adapter = new transactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        fetchRecentTransactions();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(recent_trans.this, MainActivity.class));
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(recent_trans.this,ReportActivity.class));
            }
        });
        myImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(recent_trans.this,add_expense.class));
            }
        });
    }

    private void fetchRecentTransactions() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("Transaction");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    transactionList.add(transaction);
                }
                // Sort the transactions by date in decreasing order
                sortTransactionsByDate();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void sortTransactionsByDate() {
        Collections.sort(transactionList, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    return sdf.parse(t2.getDate()).compareTo(sdf.parse(t1.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
