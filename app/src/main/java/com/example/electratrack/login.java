package com.example.electratrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerpage,forgetButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgetButton = findViewById(R.id.forget_button);
        registerpage=findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
//                            // Save login state in SharedPreferences
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putBoolean("isLoggedIn", true);
//                            editor.apply();
//
//                            // Redirect to Home Activity
                            startActivity(new Intent(login.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        registerpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, register.class));
            }
        });
        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,forget_password.class));
            }
        });
    }
}
