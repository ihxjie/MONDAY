package com.ihxjie.monday.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.ihxjie.monday.MainActivity;
import com.ihxjie.monday.R;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.CurrentUser;
import com.ihxjie.monday.face.activity.RegisterFaceActivity;
import com.ihxjie.monday.service.UserService;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // 在线激活所需的权限
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final int REQUEST_CODE_FACE_REGISTER = 0x002;

    private Retrofit retrofit;
    private UserService userService;

    private ImageView avatar;
    private TextView username;
    private TextView group;

    private MaterialButton activeEngine;
    private MaterialButton faceRegister;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        avatar = root.findViewById(R.id.avatar);
        username = root.findViewById(R.id.username);
        group = root.findViewById(R.id.group);

        activeEngine = root.findViewById(R.id.engineButton);
        faceRegister = root.findViewById(R.id.faceRegister);

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        userService = retrofit.create(UserService.class);
        Call<CurrentUser> call = userService.getCurrentUser();
        call.enqueue(new Callback<CurrentUser>() {
            @Override
            public void onResponse(@NotNull Call<CurrentUser> call, @NotNull Response<CurrentUser> response) {
                CurrentUser currentUser = response.body();
                Glide.with(root)
                        .load(currentUser.avatar)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(avatar);
                username.setText(currentUser.name);
                group.setText(currentUser.group);

            }

            @Override
            public void onFailure(@NotNull Call<CurrentUser> call, @NotNull Throwable t) {

            }
        });

        activeEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeEngine();
            }
        });
        faceRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterFaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FACE_REGISTER);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_CODE_FACE_REGISTER && requestCode == RESULT_OK){
            String faceId = data.getStringExtra("faceId");
            Log.d(TAG, "onActivityResult: " + faceId);
        }
    }

    /**
     * 权限检查
     *
     * @param neededPermissions 需要的权限
     * @return 是否全部被允许
     */
    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(getContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    /**
     * 激活引擎
     */
    public void activeEngine() {

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i(TAG, "subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(
                        getContext(),
                        com.ihxjie.monday.face.common.Constants.APP_ID,
                        com.ihxjie.monday.face.common.Constants.SDK_KEY);
                Log.i(TAG, "subscribe cost: " + (System.currentTimeMillis() - start));
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            Toast.makeText(getContext(), R.string.active_success, Toast.LENGTH_SHORT).show();
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            Toast.makeText(getContext(), R.string.already_activated, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), R.string.active_failed, Toast.LENGTH_SHORT).show();
                        }

                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(getContext(), activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i(TAG, activeFileInfo.toString());
                        }

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}