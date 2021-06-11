package com.ihxjie.monday.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihxjie.monday.R;
import com.ihxjie.monday.common.Constants;
import com.ihxjie.monday.entity.Attendance;
import com.ihxjie.monday.entity.PositionInfo;
import com.ihxjie.monday.service.RecordService;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "LocationActivity";

    private MapView mMapView = null;
    private MaterialButton button;
    private MaterialTextView textView;
    private ImageView imageView;
    private MyLocationStyle myLocationStyle;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private Retrofit retrofit;
    private RecordService recordService;

    private double longitude;
    private double latitude;
    private double distance;
    private double accuracy;

    private boolean canAtt;

    private PositionInfo positionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);       //设置沉浸式状态栏，在MIUI系统中，状态栏背景透明。原生系统中，状态栏背景半透明。
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);   //设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_location);
        canAtt = false;

        Attendance attendance = (Attendance) getIntent().getSerializableExtra("attendance");
        positionInfo = new PositionInfo();

        button = findViewById(R.id.btn_location);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.avatar);
        textView.setText("正在获取位置");
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        AMap aMap = mMapView.getMap();
        myLocationStyle = new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        aMap.showIndoorMap(true);

        longitude = Double.parseDouble(attendance.getAttLongitude());
        latitude = Double.parseDouble(attendance.getAttLatitude());
        accuracy = Double.parseDouble(attendance.getAttAccuracy());
        double radius = Double.parseDouble(attendance.getAttAccuracy());
        LatLng latLng = new LatLng(latitude, longitude);
        aMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(Color.argb(50, 1, 1, 1))
                .strokeColor(Color.argb(1, 1, 1, 1))
                .strokeWidth(15));

        initLocation();
        startLocation();

        button.setOnClickListener(v -> {
            if (canAtt){
                submitAttGps(positionInfo);
                stopLocation();
            }
            else {
                Toast.makeText(getApplicationContext(), "签到失败！未在签到范围内", Toast.LENGTH_SHORT).show();
            }
        });
        
    }

    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    AMapLocationListener locationListener = location -> {
        if (null != location) {

            positionInfo.setLatitude(String.valueOf(location.getLatitude()));
            positionInfo.setLongitude(String.valueOf(location.getLongitude()));
            positionInfo.setAccuracy(String.valueOf(location.getAccuracy()));
            positionInfo.setProvider(location.getProvider());
            positionInfo.setCountry(location.getCountry());
            positionInfo.setProvince(location.getProvince());
            positionInfo.setCity(location.getCity());
            positionInfo.setDistrict(location.getDistrict());
            positionInfo.setAddress(location.getAddress());
            positionInfo.setPoi(location.getPoiName());
            positionInfo.setGpsSatellites(String.valueOf(location.getLocationQualityReport().getGPSSatellites()));
            positionInfo.setLocationType(String.valueOf(location.getLocationType()));

            DPoint attPoint = new DPoint(latitude, longitude);
            DPoint resPoint = new DPoint(location.getLatitude(), location.getLongitude());
            distance = CoordinateConverter.calculateLineDistance(attPoint, resPoint);

            positionInfo.setDistance(String.valueOf(distance));

            Intent intent = getIntent();
            Attendance attendance = (Attendance) intent.getSerializableExtra("attendance");
            positionInfo.setAttendanceId(attendance.getAttendanceId());
            textView.setText(String.format(getResources().getString(R.string.att_location_detail),
                    positionInfo.getLongitude(), positionInfo.getLatitude(), distance));
            Log.d(TAG, "distance: " + distance);
            Log.d(TAG, "accuracy: " + accuracy);
            if (distance <= accuracy){
                imageView.setImageResource(R.drawable.confirm);
                canAtt = true;
            }else {
                imageView.setImageResource(R.drawable.error);
                canAtt = false;
            }
            // submitAttGps(positionInfo);

        } else {
            Log.i(TAG, "onLocationChanged: " + "定位失败，loc is null");
        }
    };

    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    private void stopLocation(){
        locationClient.stopLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void submitAttGps(PositionInfo positionInfo){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString("userId", "");

        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.host)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        recordService = retrofit.create(RecordService.class);
        Call<String> call = recordService.attGps(userId, positionInfo);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                showToast(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }
}