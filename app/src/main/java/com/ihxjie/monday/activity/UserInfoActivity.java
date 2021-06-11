package com.ihxjie.monday.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialTextInputPicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihxjie.monday.R;
import com.ihxjie.monday.common.Config;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.CurrentUser;
import com.ihxjie.monday.service.UserService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";

    private static final int fromAlbum = 0x006;

    TextInputEditText realName;
    TextInputEditText userTel;
    TextInputEditText userEmail;
    TextInputEditText userSignature;
    ProgressBar progressBar;
    ShapeableImageView avatar;
    MaterialButton btnUpdate;

    private Retrofit retrofit;
    private UserService userService;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("更改用户信息");
        }
        Toast.makeText(this, "点击头像可更换头像", Toast.LENGTH_SHORT).show();

        realName = findViewById(R.id.real_name);
        userTel = findViewById(R.id.user_tel);
        userEmail = findViewById(R.id.user_email);
        userSignature = findViewById(R.id.user_signature);
        avatar = findViewById(R.id.avatar);
        btnUpdate = findViewById(R.id.btn_update_user);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        CurrentUser currentUser = (CurrentUser) getIntent().getSerializableExtra("currentUser");

        realName.setText(currentUser.getName());
        userTel.setText(currentUser.getPhone());
        userEmail.setText(currentUser.getEmail());
        userSignature.setText(currentUser.getSignature());
        Glide.with(this)
                .load(currentUser.avatar)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(avatar);

        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userService = retrofit.create(UserService.class);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = sharedPreferences.getString("userId", "");

        btnUpdate.setOnClickListener(v -> {

            currentUser.setName(Objects.requireNonNull(realName.getText()).toString());
            currentUser.setPhone(Objects.requireNonNull(userTel.getText()).toString());
            currentUser.setEmail(Objects.requireNonNull(userEmail.getText()).toString());
            currentUser.setSignature(Objects.requireNonNull(userSignature.getText()).toString());

            Call<String> call = userService.updateUser(currentUser);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    Toast.makeText(getApplicationContext(), response.body(), Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

                }
            });

        });

        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, fromAlbum);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case fromAlbum:
                if (resultCode == RESULT_OK && data != null){

                    Uri uri = data.getData();
                    try {
                        ParcelFileDescriptor fd =  getContentResolver().openFileDescriptor(data.getData(), "r");
                        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
                        avatar.setImageBitmap(bitmap);
                        fd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String filename = UUID.randomUUID().toString() + ".png";
                    // 上传至OSS
                    OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(Config.OSS_ACCESS_KEY_ID, Config.OSS_ACCESS_KEY_SECRET);

                    ClientConfiguration conf = new ClientConfiguration();
                    conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒。
                    conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒。
                    conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个。
                    conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次。
                    OSS oss = new OSSClient(getApplicationContext(), Config.OSS_ENDPOINT, credentialProvider, conf);

                    PutObjectRequest put = new PutObjectRequest(Config.BUCKET_NAME, Config.OBJECT_NAME + filename, uri);
                    put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                        @Override
                        public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setMax((int) totalSize);
                            progressBar.setProgress((int) currentSize);
                            Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                        }
                    });

                    OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                        @Override
                        public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                            Log.d("PutObject", "UploadSuccess");
                            Log.d("ETag", result.getETag());
                            Log.d("RequestId", result.getRequestId());
                            Log.d("url", oss.presignPublicObjectURL(Config.BUCKET_NAME, Config.OBJECT_NAME + filename));
                            updateAvatar(oss.presignPublicObjectURL(Config.BUCKET_NAME, Config.OBJECT_NAME + filename));
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                            // 请求异常。
                            if (clientExcepion != null) {
                                // 本地异常，如网络异常等。
                                clientExcepion.printStackTrace();
                            }
                            if (serviceException != null) {
                                // 服务异常。
                                Log.e("ErrorCode", serviceException.getErrorCode());
                                Log.e("RequestId", serviceException.getRequestId());
                                Log.e("HostId", serviceException.getHostId());
                                Log.e("RawMessage", serviceException.getRawMessage());
                            }
                        }
                    });
                    // task.cancel(); // 可以取消任务。
                    // task.waitUntilFinished(); // 等待任务完成。
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateAvatar(String avatar){
        Call<String> call = userService.updateAvatar(userId, avatar);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                Toast.makeText(getApplicationContext(), response.body(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

            }
        });
    }
}