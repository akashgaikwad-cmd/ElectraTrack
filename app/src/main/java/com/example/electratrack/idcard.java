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
import android.widget.TextView;
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

public class idcard extends AppCompatActivity {

    private ImageView profileImageView,back1;
    private TextView nameEditText, addressEditText, name1;
    private DatabaseReference userRef;

    private FirebaseAuth auth;
    private String userID;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.election_id_card);

        back1 = findViewById(R.id.back);
        profileImageView = findViewById(R.id.user_photo);
        nameEditText = findViewById(R.id.electionid);
        name1 = findViewById(R.id.name);
        addressEditText = findViewById(R.id.address);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userID=currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);
        loadUserProfile();
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(idcard.this,MainActivity.class);
                startActivity(i);
                finish();
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

                    nameEditText.setText("User Id:"+userID);
                    name1.setText(name);
                    addressEditText.setText(address);
                    if (!TextUtils.isEmpty(photoUrl)) {
                        Picasso.get().load(photoUrl).into(profileImageView);
                    }
                } else {
                    Toast.makeText(idcard.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(idcard.this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
