package com.adaarajkaip.chattingapplicationdemo.logins;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.adaarajkaip.chattingapplicationdemo.MainActivity;
import com.adaarajkaip.chattingapplicationdemo.R;
import com.adaarajkaip.chattingapplicationdemo.databinding.ActivitySignUpBinding;
import com.adaarajkaip.chattingapplicationdemo.utilities.Constants;
import com.adaarajkaip.chattingapplicationdemo.utilities.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private String encodedImage ;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        binding.alreadyHaveAccount.setOnClickListener(v -> { startActivity(new Intent(this,SignIn.class)); finish(); });
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.signUpName.getText().toString().trim().length() == 0){
                    binding.signUpName.setError("Enter Name");
                } else if (binding.signUpEmail.getText().toString().trim().length() == 0) {
                    binding.signUpEmail.setError("Enter Email");
                }else if (binding.signUpPassword.getText().toString().trim().length() == 0) {
                    binding.signUpPassword.setError("Enter Password");
                }else if (binding.signUpCPassword.getText().toString().trim().length() == 0) {
                    binding.signUpCPassword.setError("Enter Confirm Password");
                }else if (!binding.signUpPassword.getText().toString().equals(binding.signUpCPassword.getText().toString())) {
                    Toast.makeText(SignUp.this, "Passwords Does not Match.\n Please Check password again.", Toast.LENGTH_SHORT).show();
                }else {
                    signUP();
                }
            }
        });
        binding.picture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void signUP() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME,binding.signUpName.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.signUpEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.signUpPassword.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, binding.signUpName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    Toast.makeText(this, "Error : "+ exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.signUp.setVisibility(View.GONE);
        }else {
            binding.signUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK){
            if(result.getData() != null){
                Uri imageURI  = result.getData().getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageURI);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    binding.picture.setImageBitmap(bitmap);
                    binding.textAddImage.setVisibility(View.GONE);
                    encodedImage = encodeImage(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    });
}