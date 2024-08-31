package com.example.electratrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editCurrentPassword, editNewPassword, editConfirmNewPassword;
    private Button buttonSavePassword;
    private FirebaseAuth auth;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_chang);

        // Initialize views
        editCurrentPassword = findViewById(R.id.old);
        editNewPassword = findViewById(R.id.newpassword1);
        editConfirmNewPassword = findViewById(R.id.newpassword2);
        buttonSavePassword = findViewById(R.id.changed_button);
        back=findViewById(R.id.back);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this,MainActivity.class));
            }
        });
        // Set onClick listener for Save button
        buttonSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String currentPassword = editCurrentPassword.getText().toString();
        String newPassword = editNewPassword.getText().toString();
        String confirmNewPassword = editConfirmNewPassword.getText().toString();

        // Validate input
        if (TextUtils.isEmpty(currentPassword)) {
            editCurrentPassword.setError("Current password is required");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            editNewPassword.setError("New password is required");
            return;
        }

        if (TextUtils.isEmpty(confirmNewPassword)) {
            editConfirmNewPassword.setError("Please confirm your new password");
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            editConfirmNewPassword.setError("Passwords do not match");
            return;
        }

        // Re-authenticate the user before changing the password
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // User has been successfully re-authenticated, proceed to update the password
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Password change failed. Try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Re-authentication failed
                    Toast.makeText(ChangePasswordActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
