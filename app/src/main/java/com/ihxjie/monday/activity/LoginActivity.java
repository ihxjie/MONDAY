package com.ihxjie.monday.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihxjie.monday.MainActivity;
import com.ihxjie.monday.R;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.MobileUser;
import com.ihxjie.monday.face.activity.FaceLoginActivity;
import com.ihxjie.monday.mipush.PushApplication;
import com.ihxjie.monday.service.UserService;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final MaterialButton loginButton = findViewById(R.id.login);
        final ImageButton faceLogin = findViewById(R.id.face_login);
        final ImageButton fingerprintLogin = findViewById(R.id.fingerprint_login);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.host)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            UserService userService = retrofit.create(UserService.class);
            MobileUser mobileUser = new MobileUser();
            mobileUser.setUserName(username);
            mobileUser.setPassword(password);
            Call<String> call = userService.login(mobileUser);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    String userId = response.body();
                    if (userId == null){
                        Toast.makeText(getApplicationContext(), "未找到此用户，请检查账户或密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Log.d("LoginActivity", "onResponse: " + response.body());
                    editor.putString("userId", userId);
                    boolean commit = editor.commit();
                    if (commit){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }else {
                        editor.commit();
                    }
                    finish();
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

                }
            });
        });

        faceLogin.setOnClickListener(v -> {

            boolean faceSignIn = sharedPreferences.getBoolean("faceSignIn", false);
            if (!faceSignIn){
                Toast.makeText(getApplicationContext(), "人脸识别未开启，无法使用人脸识别登录功能", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(getApplicationContext(), FaceLoginActivity.class);
                startActivity(intent);
            }
        });

        fingerprintLogin.setOnClickListener(v -> {
            boolean fingerprintSignIn = sharedPreferences.getBoolean("fingerprintSignIn", false);

            if (!fingerprintSignIn){
                Toast.makeText(getApplicationContext(), "指纹识别未开启，无法使用指纹识别登录功能", Toast.LENGTH_SHORT).show();
                return;
            }

            final KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(
                    Context.KEYGUARD_SERVICE);

            if (keyguardManager.isKeyguardSecure()) {
                final BiometricPrompt.AuthenticationCallback authenticationCallback =
                        new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(
                                    BiometricPrompt.AuthenticationResult result) {
                                //successRunnable.run();
                                Log.d("TAG", "onAuthenticationSucceeded: " + result);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                //Do nothing
                                Log.d("TAG", "onAuthenticationError: " + errorCode + ", str: " + errString);
                                Toast.makeText(getApplicationContext(), errString, Toast.LENGTH_SHORT).show();
                            }
                        };

                final Handler handler = new Handler(Looper.getMainLooper());

                final BiometricPrompt.Builder builder = new BiometricPrompt.Builder(getApplicationContext())
                        .setTitle("指纹认证")
                        .setNegativeButton("取消", handler::post, (dialog, which) -> {
                            Log.d("TAG", "showLockScreen: negative btn clicked, do nothing");
                        });

                final BiometricPrompt bp = builder.build();
                bp.authenticate(new CancellationSignal(), handler::post, authenticationCallback);
            } else {
                Log.d("TAG", "showLockScreen:  no in secure.... no password");
                Toast.makeText(getApplicationContext(), "指纹识别未开启，无法使用指纹识别登录功能", Toast.LENGTH_SHORT).show();
            }
        });

        // Toast.makeText(getApplicationContext(), "欢迎使用课堂签到系统", Toast.LENGTH_SHORT).show();
    }
}