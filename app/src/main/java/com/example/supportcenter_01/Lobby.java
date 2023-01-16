package com.example.supportcenter_01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.supportcenter_01.databinding.ActivityLobbyBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class Lobby extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLobbyBinding binding;
    private MyViewModel myViewModel;
    private double loacalLatitude;
    private double loacalLongitude;
    //    private double latitude = 24.93101;
//    private double longitude = 121.17201;
    private double latitude = 24.92977;
    private double longitude = 121.17199;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLobbyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.lobbyClockInOut.setOnClickListener(v -> {
            if (haveInternet()) {
                //取得位置權限
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
//                    getLocal();
            } else {
                Toast.makeText(this, "無法取得位址", Toast.LENGTH_SHORT).show();
            }
        });

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(binding.getRoot().getContext());//初始化BottomSheet
                view = LayoutInflater.from(Lobby.this).inflate(R.layout.bottom_sheet_logout, null);//連結的介面
                Button btCancel = view.findViewById(R.id.button_cancel);
                Button bt01 = view.findViewById(R.id.button_sheet_out);
                bottomSheetDialog.setContentView(view);//將介面載入至BottomSheet内
                ViewGroup parent = (ViewGroup) view.getParent();//取得BottomSheet介面設定
                parent.setBackgroundResource(android.R.color.transparent);//將背景設為透明,否則預設白底
                bt01.setOnClickListener((v) -> {
                    bottomSheetDialog.dismiss();
                myViewModel.signOutUserOnline();
                    finish();
                });
                btCancel.setOnClickListener((v) -> {
                    bottomSheetDialog.dismiss();
                });
                bottomSheetDialog.show();//顯示BottomSheet
            }
        });
        Resources res = getResources();
        int[] icon = {R.drawable.baseline_work_outline_24,
                R.drawable.baseline_flight_takeoff_24,
                R.drawable.baseline_calendar_month_24,
                R.drawable.baseline_date_range_24,
                R.drawable.baseline_work_24,
                R.drawable.baseline_fact_check_24};
        String[] strings = res.getStringArray(R.array.option_string);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.HORIZONTAL);z
//        binding.lobbyRvbt.setLayoutManager(llm);
        binding.lobbyRvbt.setLayoutManager(new GridLayoutManager(this, 2));
        MyAdapter adapterF = new MyAdapter(icon, strings);
        binding.lobbyRvbt.setAdapter(adapterF);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //網路連線廣播
        this.registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        getLocal();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //關閉網路連線廣播
        this.unregisterReceiver(mConnReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        getLocal();//自定義方法
    }

    private void getLocal() {
        //沒有權限則返回
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            binding.lobbyTvLocation.setText("無法取得位址權限");
            return;
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    showLocation(location);//自定義方法

                    loacalLongitude = Double.parseDouble(String.format("%.5f", location.getLongitude()));
                    loacalLatitude = Double.parseDouble(String.format("%.5f", location.getLatitude()));
                    @SuppressLint("DefaultLocale") String address = "我的位置：緯度:" + loacalLatitude +
                            "經度:" + loacalLongitude;
                    if (latitude == loacalLatitude && longitude == loacalLongitude) {
                        binding.lobbyTvLocation.setText("打卡成功,1公里內" + address);
                    } else {
                        loacalLongitude = Double.parseDouble(String.format("%.4f", location.getLongitude()));
                        loacalLatitude = Double.parseDouble(String.format("%.4f", location.getLatitude()));
                        latitude = Double.parseDouble(String.format("%.4f", latitude));
                        longitude = Double.parseDouble(String.format("%.4f", longitude));
                        if (latitude == loacalLatitude && longitude == loacalLongitude) ;
                        binding.lobbyTvLocation.setText("10公尺以內" + address);
                        binding.lobbyTvLocation.setText("差距10公尺以上" + address);

                    }
                } else {
                    binding.lobbyTvLocation.setText("無法取得位址");
                }
            }
        });
//        Location location = fusedLocationClient.getCurrentLocation();


//        String localProvider = "";
//        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        /**知道位置後..*/
//        Location location = manager.getLastKnownLocation(localProvider);
//        if (location!=null) {
//            showLocation(location);//自定義方法
//        } else {
//            Log.d("Andrew", "getLocal: ");
////            String address = "我的位置：緯度:" + String.format("%.5f",location.getLatitude()) + "經度:" + String.format("%.5f",location.getLongitude());
//            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mListener);
//            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mListener);
//        }
    }

    //監聽位置變化
    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }
    };

    private void showLocation(Location location) {
        @SuppressLint("DefaultLocale") String address = "我的位置：緯度:" + String.format("%.5f", location.getLatitude()) +
                "經度:" + String.format("%.5f", location.getLongitude());
        binding.lobbyTvLocation.setText(address);
//        binding.lobbyClockInOut.setOnClickListener(view -> {
//            String url = "https://www.google.com/maps/@" + location.getLatitude() + "," + location.getLongitude() + ",15z";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
//        });

    }

    //廣播監聽是否有網路
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 當使用者開啟或關閉網路時會進入這邊

            // 判斷目前有無網路
            if (haveInternet()) {
                // 以連線至網路，做更新資料等事情
                binding.lobbyClockInOut.setEnabled(true);
            } else {
                // 沒有網路
                binding.lobbyClockInOut.setEnabled(false);
                Toast.makeText(Lobby.this, "網路未連線", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //網路連線檢查
    private boolean haveInternet() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return info != null &&
                info.isConnected();
    }
}