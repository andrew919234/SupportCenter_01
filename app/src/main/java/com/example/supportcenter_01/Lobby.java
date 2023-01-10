package com.example.supportcenter_01;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supportcenter_01.RoomDataBase.User;
import com.example.supportcenter_01.databinding.ActivityLobbyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Lobby extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLobbyBinding binding;
    private MyViewModel myViewModel;
    public static final String TAG = Lobby.class.getSimpleName() + "My";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLobbyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.lobbyClockInOut.setOnClickListener(v -> {
            if (haveInternet()) {
                //取得位置權限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 100);
                }
            } else {
                Toast.makeText(this, "無法取得位址", Toast.LENGTH_SHORT).show();
            }
        });

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewModel.signOutUserOnline();
                finish();
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
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.lobbyRvbt.setLayoutManager(llm);
//        binding.lobbyRvbt.setLayoutManager(new GridLayoutManager(this,3));
        MyAdapter adapterF = new MyAdapter(icon, strings);
        binding.lobbyRvbt.setAdapter(adapterF);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getAllUsers().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Toast.makeText(Lobby.this, "onChange", Toast.LENGTH_SHORT).show();
            }
        });
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mConnReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLocal();
    }

    private void getLocal() {
        /**沒有權限則返回*/
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String localProvider = "";
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /**知道位置後..*/
        Location location = manager.getLastKnownLocation(localProvider);
        if (location != null) {
            showLocation(location);//
        } else {
            Log.d(TAG, "getLocal: ");
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mListener);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mListener);
        }
    }

    /**
     * 監聽位置變化
     */
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

        }
    };

    private void showLocation(Location location) {
        String address = "我的位置：緯度-" + location.getLatitude() + "經度-" + location.getLongitude();
        binding.lobbyTvLocation.setText(address);
        binding.lobbyClockInOut.setOnClickListener(view -> {
            String url = "https://www.google.com/maps/@" + location.getLatitude() + "," + location.getLongitude() + ",15z";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

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
                Toast.makeText(Lobby.this, "網路已連線", Toast.LENGTH_SHORT).show();
            } else {
                // 沒有網路
                binding.lobbyClockInOut.setEnabled(false);
                Toast.makeText(Lobby.this, "網路未連線!", Toast.LENGTH_SHORT).show();
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