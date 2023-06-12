package com.adaarajkaip.chattingapplicationdemo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adaarajkaip.chattingapplicationdemo.Adapters.UsersAdapter;
import com.adaarajkaip.chattingapplicationdemo.Models.User;
import com.adaarajkaip.chattingapplicationdemo.databinding.ActivityUsersBinding;
import com.adaarajkaip.chattingapplicationdemo.listener.UserListener;
import com.adaarajkaip.chattingapplicationdemo.utilities.Constants;
import com.adaarajkaip.chattingapplicationdemo.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener  {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUsers();
        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currendUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currendUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                            if (users.size() > 0) {
                                UsersAdapter usersAdapter = new UsersAdapter(users, this);
                                binding.recyclerViewUsers.setAdapter(usersAdapter);
                                binding.recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                binding.recyclerViewUsers.setVisibility(View.VISIBLE);
                            } else {
                                showErrorMessage();
                            }
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.ErrorMessage.setText(String.format("%s", "No User available"));
        binding.ErrorMessage.setVisibility(View.VISIBLE);
    }


    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBarUsers.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarUsers.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();    }
}