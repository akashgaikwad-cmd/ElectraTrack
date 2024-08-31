package com.example.electratrack;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.Date;

public class add_expense extends AppCompatActivity {

    private ImageView phonePeButton,googlePayButton,back,report,recent;
    private EditText amount,description;
    private DatabaseReference userRef;
    private Spinner mySpinner;
    private FirebaseAuth auth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);
        String[] items = {"Food Expense", "Travel Expense", "College Expense", "Shopping Expense","Grocery Expense","Other Expense"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mySpinner = findViewById(R.id.categorySpinner);
        back=findViewById(R.id.back);
        report=findViewById(R.id.report);
        recent=findViewById(R.id.recent);
        amount = findViewById(R.id.amountInput);
        mySpinner.setAdapter(adapter);
        description = findViewById(R.id.descriptionInput);
        phonePeButton = findViewById(R.id.ppay);
        googlePayButton= findViewById(R.id.gpay);


        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userID=currentUser.getUid();
        Button saveBtn=findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountString = amount.getText().toString().trim();
                if (TextUtils.isEmpty(amountString)) {
                    amount.setError("Amount is required"); // Set error on the correct EditText
                    return;
                }

                int amount_1;
                try {
                    amount_1 = Integer.parseInt(amountString);
                } catch (NumberFormatException e) {
                    amount.setError("Invalid amount format");
                    return;
                }

                String desc = description.getText().toString();
                String cate = mySpinner.getSelectedItem().toString();
                LocalDate currentDate = LocalDate.now();

                userRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("Transaction");
                Transaction t1 = new Transaction(amount_1, desc, cate, currentDate.toString());
                DatabaseReference newExpenseRef = userRef.push();
                newExpenseRef.setValue(t1)
                        .addOnSuccessListener(aVoid -> {
                            // Data saved successfully
                            Toast.makeText(add_expense.this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Failed to save data
                            Toast.makeText(add_expense.this, "Failed to add expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(add_expense.this,MainActivity.class));
            }
        });
        // On Click Listener for PhonePe QR Scanner
        phonePeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("phonepe://upi");
                Intent callIntent = new Intent(Intent.ACTION_VIEW, number);
                if (callIntent != null) {
                    startActivity(callIntent);
                } else {
                    Toast.makeText(add_expense.this, "PhonePe is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });



        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(add_expense.this,ReportActivity.class));
            }
        });
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(add_expense.this,recent_trans.class));
            }
        });

// On Click Listener for Google Pay QR Scanner
        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("upi://pay"); // UPI URI scheme
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.nbu.paisa.user"); // PhonePe package name

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "PhonePe app not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}