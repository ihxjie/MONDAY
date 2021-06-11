package com.ihxjie.monday.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.ihxjie.monday.R;
import com.ihxjie.monday.activity.LoginActivity;
import com.ihxjie.monday.activity.UserInfoActivity;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.CurrentUser;
import com.ihxjie.monday.face.activity.RegisterFaceActivity;
import com.ihxjie.monday.mipush.PushApplication;
import com.ihxjie.monday.service.UserService;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
import static android.content.Context.PRINT_SERVICE;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // 在线激活所需的权限
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final int REQUEST_CODE_FACE_REGISTER = 0x002;
    private static final int REQUEST_CODE_USERINFO_UPDATE = 0x005;

    private View root;

    private Retrofit retrofit;
    private UserService userService;

    private ImageView avatar;
    private TextView username;
    private TextView group;

    private MaterialButton updateUserInfo;
    private MaterialButton login;

    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState == null) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean isEngineActive = sharedPreferences.getBoolean("isEngineActive", false);
        if (!isEngineActive){
            // Toast.makeText(requireContext(), "人脸识别引擎正在初始化，请稍等", Toast.LENGTH_SHORT).show();
            activeEngine();
        }
        avatar = root.findViewById(R.id.avatar);
        username = root.findViewById(R.id.username);
        group = root.findViewById(R.id.group);

        // fingerprintRegister = root.findViewById(R.id.fingerprint_register);
        updateUserInfo = root.findViewById(R.id.update_user_info);
        login = root.findViewById(R.id.loginActivity);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        userService = retrofit.create(UserService.class);

        // 获取并渲染用户信息
        Intent intent = requireActivity().getIntent();
        String userId = intent.getStringExtra("userId");
        getUserInfo();

        login.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        return root;
    }

    /**
     * 获取并渲染用户信息
     */
    private void getUserInfo(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PushApplication.getContext());
        String userId = sharedPreferences.getString("userId", "");
        Log.d(TAG, "getUserInfo: " + userId);

        Call<CurrentUser> call = userService.getCurrentUser(userId);
        call.enqueue(new Callback<CurrentUser>() {
            @Override
            public void onResponse(@NotNull Call<CurrentUser> call, @NotNull Response<CurrentUser> response) {
                CurrentUser currentUser = response.body();
                assert currentUser != null;
                Glide.with(root)
                        .load(currentUser.avatar)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(avatar);
                username.setText(currentUser.name);
                group.setText(currentUser.group);
                updateUserInfo.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), UserInfoActivity.class);
                    intent.putExtra("currentUser", currentUser);
                    startActivityForResult(intent, REQUEST_CODE_USERINFO_UPDATE);
                });

            }

            @Override
            public void onFailure(@NotNull Call<CurrentUser> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_FACE_REGISTER:
                if (resultCode == RESULT_OK && data != null){
                    String faceId = data.getStringExtra("faceId");
                    Log.d(TAG, "onActivityResult: " + faceId);
                }
                break;
            case REQUEST_CODE_USERINFO_UPDATE:
                if (resultCode == RESULT_OK && data != null){
                    getUserInfo();
                }
                break;
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
            allGranted &= ContextCompat.checkSelfPermission(requireContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    /**
     * 激活引擎
     */
    public void activeEngine() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
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
                            //Toast.makeText(getContext(), R.string.active_success, Toast.LENGTH_SHORT).show();
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
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
                        editor.putBoolean("isEngineActive", true);
                        editor.apply();

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

    /**
     * 快捷登录
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.quick_login_preferences, rootKey);
            SwitchPreferenceCompat fingerprintSignIn = findPreference("fingerprintSignIn");
            SwitchPreferenceCompat faceSignIn = findPreference("faceSignIn");

            // 指纹快捷登录
            assert fingerprintSignIn != null;
            fingerprintSignIn.setOnPreferenceChangeListener((preference, newValue) -> {

                boolean isOpen = (boolean) newValue;
                if (!isOpen){
                    Toast.makeText(requireContext(), "已关闭指纹快捷登录", Toast.LENGTH_SHORT).show();
                    fingerprintSignIn.setChecked(false);
                }else {
                    final KeyguardManager keyguardManager = (KeyguardManager) requireContext().getSystemService(
                            Context.KEYGUARD_SERVICE);

                    if (keyguardManager.isKeyguardSecure()) {
                        final BiometricPrompt.AuthenticationCallback authenticationCallback =
                                new BiometricPrompt.AuthenticationCallback() {
                                    @Override
                                    public void onAuthenticationSucceeded(
                                            BiometricPrompt.AuthenticationResult result) {
                                        //successRunnable.run();
                                        Log.d("TAG", "onAuthenticationSucceeded: " + result);
                                        fingerprintSignIn.setChecked(true);
                                    }

                                    @Override
                                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                                        //Do nothing
                                        Log.d("TAG", "onAuthenticationError: " + errorCode + ", str: " + errString);
                                        Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                                    }
                                };

                        final Handler handler = new Handler(Looper.getMainLooper());

                        final BiometricPrompt.Builder builder = new BiometricPrompt.Builder(requireContext())
                                .setTitle("指纹认证")
                                .setNegativeButton("取消", handler::post, (dialog, which) -> {
                                    Log.d("TAG", "showLockScreen: negative btn clicked, do nothing");
                                });

                        final BiometricPrompt bp = builder.build();
                        bp.authenticate(new CancellationSignal(), handler::post, authenticationCallback);
                    } else {
                        Log.d("TAG", "showLockScreen:  no in secure.... no password");
                        Toast.makeText(requireContext(), "未找到指纹信息,请在系统中录入指纹", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }
                return false;
            });

            assert faceSignIn != null;
            faceSignIn.setOnPreferenceChangeListener((preference, newValue) -> {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean isOpen = (boolean) newValue;
                if (!isOpen){
                    editor.putBoolean("faceActive", false);
                    editor.apply();
                    faceSignIn.setChecked(false);
                    Toast.makeText(requireContext(), "已关闭人脸识别登录", Toast.LENGTH_SHORT).show();

                }else {
                    String faceId = sharedPreferences.getString("faceId", "");

                    if (!faceId.equals("")){
                        faceSignIn.setChecked(true);
                        Toast.makeText(requireContext(), "已开启人脸识别登录", Toast.LENGTH_SHORT).show();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("未检测到人脸信息，是否先录入人脸信息？")
                                .setPositiveButton("录入", (dialog, which) -> {
                                    Intent intent = new Intent(requireContext(), RegisterFaceActivity.class);
                                    startActivityForResult(intent, REQUEST_CODE_FACE_REGISTER);
                                })
                                .setNegativeButton("取消", (dialog, which) -> {

                                });
                        builder.show();
                    }
                }

                return false;
            });
        }

    }
}