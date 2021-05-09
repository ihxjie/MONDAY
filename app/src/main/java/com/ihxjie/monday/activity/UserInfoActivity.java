package com.ihxjie.monday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialTextInputPicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihxjie.monday.R;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.CurrentUser;
import com.ihxjie.monday.service.UserService;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInfoActivity extends AppCompatActivity {

    TextInputEditText realName;
    TextInputEditText userTel;
    TextInputEditText userEmail;
    TextInputEditText userSignature;
    MaterialButton btnUpdate;

    private Retrofit retrofit;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        realName = findViewById(R.id.real_name);
        userTel = findViewById(R.id.user_tel);
        userEmail = findViewById(R.id.user_email);
        userSignature = findViewById(R.id.user_signature);
        btnUpdate = findViewById(R.id.btn_update_user);

        CurrentUser currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        realName.setText(currentUser.getName());
        userTel.setText(currentUser.getPhone());
        userEmail.setText(currentUser.getEmail());
        userSignature.setText(currentUser.getSignature());

        btnUpdate.setOnClickListener(v -> {

            currentUser.setName(Objects.requireNonNull(realName.getText()).toString());
            currentUser.setPhone(Objects.requireNonNull(userTel.getText()).toString());
            currentUser.setEmail(Objects.requireNonNull(userEmail.getText()).toString());
            currentUser.setSignature(Objects.requireNonNull(userSignature.getText()).toString());

            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.host)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            userService = retrofit.create(UserService.class);
            Call<String> call = userService.updateUser(currentUser);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    Toast.makeText(getApplicationContext(), response.body(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

                }
            });
        });


    }
}