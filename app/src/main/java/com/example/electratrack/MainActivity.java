package com.example.electratrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.internal.ManufacturerUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userRef,userRef1;
    private String userID;
    private ImageView profile,food_expense,travel_expense,shopping_expense,college_expense,grocery_expense;
    private TextView total;
    private TextView food,travel,grocery,clg,shopping,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        food_expense=findViewById(R.id.food_expense);
        travel_expense=findViewById(R.id.travel_expense);
        college_expense=findViewById(R.id.college_expense);
        shopping_expense=findViewById(R.id.shopping_expense);
        grocery_expense=findViewById(R.id.grocery_expense);
        total = findViewById(R.id.total);
        food = findViewById(R.id.food);
        travel= findViewById(R.id.travel);
        grocery = findViewById(R.id.grocery);
        clg = findViewById(R.id.clg);
        shopping= findViewById(R.id.shopping);
        username= findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();
        profile= findViewById(R.id.profile);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("Transaction");
            userRef1 = FirebaseDatabase.getInstance().getReference("users").child(userID);
            loadUsername();
            fetchAndDisplayTotalExpense();//Total
            fetchAndDisplayMessExpense();//Food
            fetchAndDisplayTravelExpense();//Travel
            fetchAndDisplayClgExpense();//Clg
            fetchAndDisplayGroceryExpense();//Grocery
            fetchAndDisplayShoppingExpense();//Shopping
        } else{
            startActivity(new Intent(MainActivity.this, login.class));
        }

        ImageView myImageView = findViewById(R.id.myImageView);
        ImageView myrecent = findViewById(R.id.recent);
        ImageView greport = findViewById(R.id.report);

        food_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                intent.putExtra("category", "Food Expense"); // Change "Food" to the desired category
                startActivity(intent);
            }
        });
        travel_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                intent.putExtra("category", "Travel Expense"); // Change "Food" to the desired category
                startActivity(intent);
            }
        });
        college_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                intent.putExtra("category", "College Expense"); // Change "Food" to the desired category
                startActivity(intent);
            }
        });
        shopping_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                intent.putExtra("category", "Shopping Expense"); // Change "Food" to the desired category
                startActivity(intent);
            }
        });
        grocery_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryExpenses.class);
                intent.putExtra("category", "Grocery Expense"); // Change "Food" to the desired category
                startActivity(intent);
            }
        });
        greport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ReportActivity.class));
            }
        });
            myImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   startActivity(new Intent(getApplicationContext(), add_expense.class));
                }
            });

        myrecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), recent_trans.class));
            }
        });

        ImageView profileIcon = findViewById(R.id.profile);

        profileIcon.setOnClickListener(v -> {
            showPopupMenu(v);
        });

    }
    private void fetchAndDisplayTotalExpense() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // Start of current month
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // End of current month

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double tarvelExpense = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        LocalDate transactionDate = LocalDate.parse(transaction.getDate(), formatter);
                        if (transaction.getCategory().equals("Other Expense") && // Check if category is "Mess"
                                (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                                (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate))) {
                            tarvelExpense += transaction.getAmount();
                        }
                    }
                }
                updateTotalExpense(tarvelExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void loadUsername() {
        userRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String name = snapshot.child("name").getValue(String.class);
                    String photoUrl = snapshot.child("photo").getValue(String.class);
                    username.setText(name);
                    if (!TextUtils.isEmpty(photoUrl)) {
                        Picasso.get().load(photoUrl).into(profile);
                    }
                } else {
                    Log.d("DEBUG", "Snapshot does not exist");
                    username.setText("Friend");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }

    private void fetchAndDisplayMessExpense() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // Start of current month
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // End of current month

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double messExpense = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        LocalDate transactionDate = LocalDate.parse(transaction.getDate(), formatter);
                        if (transaction.getCategory().equals("Food Expense") && // Check if category is "Mess"
                                (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                                (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate))) {
                            messExpense += transaction.getAmount();
                        }
                    }
                }
                updateMessExpense(messExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
    private void fetchAndDisplayTravelExpense() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // Start of current month
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // End of current month

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double tarvelExpense = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        LocalDate transactionDate = LocalDate.parse(transaction.getDate(), formatter);
                        if (transaction.getCategory().equals("Travel Expense") && // Check if category is "Mess"
                                (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                                (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate))) {
                            tarvelExpense += transaction.getAmount();
                        }
                    }
                }
                updateTravelExpense(tarvelExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
    private void fetchAndDisplayShoppingExpense() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // Start of current month
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // End of current month

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double shoppingExpense = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        LocalDate transactionDate = LocalDate.parse(transaction.getDate(), formatter);
                        if (transaction.getCategory().equals("Shopping Expense") && // Check if category is "Mess"
                                (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                                (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate))) {
                            shoppingExpense += transaction.getAmount();
                        }
                    }
                }
                updateShoppingExpense(shoppingExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
    private void fetchAndDisplayClgExpense() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // Start of current month
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // End of current month

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double clgExpense = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        LocalDate transactionDate = LocalDate.parse(transaction.getDate(), formatter);
                        if (transaction.getCategory().equals("College Expense") && // Check if category is "Mess"
                                (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                                (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate))) {
                            clgExpense += transaction.getAmount();
                        }
                    }
                }
                updateClgExpense(clgExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
    private void fetchAndDisplayGroceryExpense() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // Start of current month
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // End of current month

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double groceryExpense = 0.0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        LocalDate transactionDate = LocalDate.parse(transaction.getDate(), formatter);
                        if (transaction.getCategory().equals("Grocery Expense") && // Check if category is "Mess"
                                (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                                (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate))) {
                            groceryExpense += transaction.getAmount();
                        }
                    }
                }
                updateGroceryExpense(groceryExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
    private void updateTotalExpense(double totalExpense) {
        total.setText(String.format("%.2f\n", totalExpense));
    }
    private void updateGroceryExpense(double groceryExpense) {
        grocery.setText(String.format("%.2f\n", groceryExpense));
    }
    private void updateTravelExpense(double travelExpense) {
        travel.setText(String.format("%.2f\n", travelExpense));
    }
    private void updateMessExpense(double messExpense) {
        food.setText(String.format("%.2f\n", messExpense));
    }
    private void updateShoppingExpense(double shoppingExpense) {
        shopping.setText(String.format("%.2f\n", shoppingExpense));
    }
    private void updateClgExpense(double clgExpense) {
        clg.setText(String.format("%.2f\n", clgExpense));
    }
    private void showPopupMenu(View view) {
        // Create a PopupMenu, passing the context and the view to which it should be anchored
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.profile_menu, popupMenu.getMenu());

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_manage_profile) {
                // Handle "Manage Profile" click
                startActivity(new Intent(MainActivity.this,manage_profile.class));
            } else if (id == R.id.action_logout) {
                logout();
                return true;
            } else if(id==R.id.id_card){
                startActivity(new Intent(MainActivity.this,idcard.class));
            } else if(id==R.id.cpassword){
                startActivity(new Intent(MainActivity.this,ChangePasswordActivity.class));
            }

            return false;
        });


        // Show the popup menu
        popupMenu.show();
    }
    private void logout() {
        mAuth.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("hi", currentUser.toString());
        } else {
            Log.d("hi", "No user is currently signed in.");
        }
        startActivity(new Intent(MainActivity.this, login.class));
        finish();
    }



}