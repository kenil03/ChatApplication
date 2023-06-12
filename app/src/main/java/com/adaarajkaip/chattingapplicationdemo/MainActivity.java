package com.adaarajkaip.chattingapplicationdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.adaarajkaip.chattingapplicationdemo.Activities.UsersActivity;
import com.adaarajkaip.chattingapplicationdemo.databinding.ActivityMainBinding;
import com.adaarajkaip.chattingapplicationdemo.logins.SignIn;
import com.adaarajkaip.chattingapplicationdemo.utilities.Constants;
import com.adaarajkaip.chattingapplicationdemo.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();

        binding.signOut.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Sign Out..", Toast.LENGTH_SHORT).show();
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
            documentReference.update(updates).addOnSuccessListener(unused -> {
                preferenceManager.clear();
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                finish();
            }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Unnable to signOut", Toast.LENGTH_SHORT).show());

        });

        binding.addButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    private void loadUserDetails() {
        binding.profileName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.profilePicture.setImageBitmap(bitmap);
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token).addOnSuccessListener(unused -> Toast.makeText(this, "Token updated successfully", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(this, "Unable to update token", Toast.LENGTH_SHORT).show());
    }
}