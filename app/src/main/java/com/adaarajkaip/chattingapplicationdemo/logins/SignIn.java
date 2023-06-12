package com.adaarajkaip.chattingapplicationdemo.logins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.adaarajkaip.chattingapplicationdemo.MainActivity;
import com.adaarajkaip.chattingapplicationdemo.R;
import com.adaarajkaip.chattingapplicationdemo.databinding.ActivitySignInBinding;
import com.adaarajkaip.chattingapplicationdemo.utilities.Constants;
import com.adaarajkaip.chattingapplicationdemo.utilities.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.createNew.setOnClickListener(v -> { startActivity(new Intent(this,SignUp.class)); finish(); });
        binding.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.signInEmail.getText().toString().trim().length() == 0){
                    binding.signInEmail.setError("Enter Email Id");
                } else if (binding.signInPassword.getText().toString().trim().length() == 0) {
                    binding.signInPassword.setError("Enter Password");
                }else {
                    signIn();
                }
            }
        });


    }

    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.signInEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.signInPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        loading(false);
                        Toast.makeText(this, "Unable to Sign!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.signInProgressBar.setVisibility(View.VISIBLE);
            binding.signButton.setVisibility(View.GONE);
        }else {
            binding.signButton.setVisibility(View.VISIBLE);
            binding.signInProgressBar.setVisibility(View.GONE);
        }
    }

//    //For Testing
//    private  void  addDataToFitebase(){
//        FirebaseFirestore database  = FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("first_name", "Kenil");
//        data.put("last_name","Sohaliya");
//        database.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(this, "Data Inserted Successfull", Toast.LENGTH_SHORT).show();
//                })
//
//                .addOnFailureListener(exception -> {
//                    Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }

}