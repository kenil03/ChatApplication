package com.adaarajkaip.chattingapplicationdemo.logins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.adaarajkaip.chattingapplicationdemo.R;
import com.adaarajkaip.chattingapplicationdemo.databinding.ActivitySignInBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                    addDataToFitebase();
                }
            }
        });


    }

    private  void  addDataToFitebase(){
        FirebaseFirestore database  = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("first_name", "Kenil");
        data.put("last_name","Sohaliya");
        database.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Data Inserted Successfull", Toast.LENGTH_SHORT).show();
                })

                .addOnFailureListener(exception -> {
                    Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}