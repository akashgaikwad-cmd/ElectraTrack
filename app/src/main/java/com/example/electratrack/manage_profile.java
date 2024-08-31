package com.example.electratrack;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class manage_profile extends AppCompatActivity {

    private ImageView profileImageView,back1;
    private EditText nameEditText, addressEditText;
    private Button saveButton,changeButton;
    private Uri imageUri;

    private DatabaseReference userRef;

    private FirebaseAuth auth;
    private String userID;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_profile);

        profileImageView = findViewById(R.id.profile_photo);
        changeButton = findViewById(R.id.button_change_photo);
        back1 = findViewById(R.id.back);
        nameEditText = findViewById(R.id.edit_name);
        addressEditText = findViewById(R.id.edit_address);
        saveButton = findViewById(R.id.button_save);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userID=currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        uploadImageToFirebase();
                    }
                }
        );
        loadUserProfile();
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(manage_profile.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void loadUserProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String address = snapshot.child("Address").getValue(String.class);
                    String photoUrl = snapshot.child("photo").getValue(String.class);

                    nameEditText.setText(name);
                    addressEditText.setText(address);
                    if (!TextUtils.isEmpty(photoUrl)) {
                        Picasso.get().load(photoUrl).into(profileImageView);
                    }
                } else {
                    Toast.makeText(manage_profile.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(manage_profile.this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
    }
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Get Firebase Storage instance
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            // Create a unique file name for the image using the user's ID and a timestamp
            StorageReference fileReference = storageReference.child("profile_images/" + userID + "/" + System.currentTimeMillis() + ".jpg");

            // Upload the image
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // On success, get the download URL
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Get the download URL for the uploaded image
                                    String downloadUrl = uri.toString();

                                    // Save the URL to the user's profile in the database
                                    userRef.child("photo").setValue(downloadUrl);

                                    // Update the ImageView with the new image
                                    Picasso.get().load(downloadUrl).into(profileImageView);

                                    Toast.makeText(manage_profile.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed uploads
                            Toast.makeText(manage_profile.this, "Image upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveUserProfile() {
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Name and Address are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.child("name").setValue(name);
        userRef.child("Address").setValue(address);
        // Update photo if needed

        Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
    }
}
