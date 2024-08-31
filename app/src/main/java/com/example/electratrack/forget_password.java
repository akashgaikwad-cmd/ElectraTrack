package com.example.electratrack;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electratrack.R;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class forget_password extends AppCompatActivity {

    private EditText emailEditText;
    private Button sendPasswordButton;
    private FirebaseAuth mAuth;
    private static final String TAG = "ForgetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.email);
        sendPasswordButton = findViewById(R.id.sendemail);
        mAuth = FirebaseAuth.getInstance();

        sendPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(forget_password.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                return;
            }else{
                sendPasswordResetEmail(email);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://electratrack.page.link/resetPassword?email=" + email) // The link the user clicks
                .setHandleCodeInApp(true) // Whether to handle the code in the app
                .setAndroidPackageName(
                        "com.example.electratrack",  // Your Android package name
                        true, // Install if not available
                        "12"   // The minimum app version required
                )
                .build();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "Password reset email sent.");
                    } else {
                        Log.e("TAG", "Failed to send reset email.", task.getException());
                    }
                });
    }
}
