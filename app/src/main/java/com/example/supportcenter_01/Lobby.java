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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.supportcenter_01.RoomDataBase.ClockInOut;
import com.example.supportcenter_01.RoomDataBase.Shift;
import com.example.supportcenter_01.databinding.ActivityLobbyBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Lobby extends AppCompatActivity {

    private ActivityLobbyBinding binding;
    private DatabaseReference db_UserRef;
    private FirebaseAuth auth;
    private MyViewModel myViewModel;
    private String name;
    private String startWork;
    private String endWork;
    private double loacalLatitude;
    private double loacalLongitude;
    private List<String> staffWork = new ArrayList<>();
    private double latitude = 25.00548;
    private double longitude = 121.54188;
    private Shift[] shifts = {new Shift("A", 8, "09:00", "17:00"), new Shift("B", 8, "13:00", "21:00"),
            new Shift("C", 8, "17:00", "01:00"), new Shift("???", 0, "09:00", "17:00"),
            new Shift("???", 0, "09:00", "17:00"), new Shift("??????", 0, "09:00", "17:00")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLobbyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getGreet();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int mouth = calendar.get(Calendar.MONTH);
        auth = FirebaseAuth.getInstance();
        db_UserRef = FirebaseDatabase.getInstance().getReference("leaveApply");
        Query query = db_UserRef.child(String.valueOf(year)).child(String.valueOf(mouth + 1)).child("schedule").child(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String value = ds.getValue(String.class);
                    staffWork.add(value);

                }
                setWorkTime();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.lobbyClockInOut.setOnClickListener(v -> {
            if (haveInternet()) {
                //??????????????????
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
                sendClickInOut();
            } else {
                Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
            }
        });
        Resources res = getResources();
        int[] icon = {R.drawable.baseline_work_outline_24,
                R.drawable.baseline_flight_takeoff_24,
                R.drawable.baseline_calendar_month_24,
                R.drawable.baseline_work_24,
                R.drawable.baseline_fact_check_24};
        String[] strings = res.getStringArray(R.array.option_string);


        binding.lobbyRvbt.setLayoutManager(new GridLayoutManager(this, 2));
        MyAdapter adapterF = new MyAdapter(icon, strings);
        binding.lobbyRvbt.setAdapter(adapterF);
    }

    private void setWorkTime() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (!staffWork.isEmpty()) {
            for (int i = 0; i < shifts.length - 3; i++) {
                if (staffWork.get(day - 1).equals(shifts[i].getShiftName())) {
                    binding.lobbyTvWorktime.setText("???????????????" + shifts[i].getStartTime() + "???????????????" + shifts[i].getEndTime());
                    binding.tvShift.setText("?????????" + shifts[i].getShiftName() + " ???");
                    startWork = shifts[i].getStartTime();
                    endWork = shifts[i].getEndTime();
                }
            }
        } else {
            Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
        }
    }

    private void getGreet() {
        auth = FirebaseAuth.getInstance();
        name = auth.getCurrentUser().getEmail();
        int index = name.indexOf("@");
        name = name.substring(0, index);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 10) {
            binding.lobbyGreet.setText(name + " ??????");
        } else if (hour >= 10 && hour < 18) {

            binding.lobbyGreet.setText(name + " ??????");
        } else {
            binding.lobbyGreet.setText(name + " ??????");

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "??????").setIcon(android.R.drawable.ic_menu_set_as).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(binding.getRoot().getContext());//?????????BottomSheet
                View view = LayoutInflater.from(Lobby.this).inflate(R.layout.bottom_sheet_logout, null);//???????????????
                Button btCancel = view.findViewById(R.id.button_cancel);
                Button bt01 = view.findViewById(R.id.button_sheet_out);
                bottomSheetDialog.setContentView(view);//??????????????????BottomSheet???
                ViewGroup parent = (ViewGroup) view.getParent();//??????BottomSheet????????????
                parent.setBackgroundResource(android.R.color.transparent);//?????????????????????,??????????????????
                bt01.setOnClickListener((v) -> {
                    bottomSheetDialog.dismiss();
                    myViewModel.signOutUserOnline();
                    finish();
                });
                btCancel.setOnClickListener((v) -> {
                    bottomSheetDialog.dismiss();
                });
                bottomSheetDialog.show();//??????BottomSheet

                break;
        }

        return super.onOptionsItemSelected(item);
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
        //??????????????????
        this.registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        getLocal();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //????????????????????????
        this.unregisterReceiver(mConnReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLocal();//???????????????
    }

    private void getLocal() {
        //?????????????????????
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            binding.lobbyTvLocation.setText("????????????????????????");
            return;
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    showLocation(location);//???????????????

                    loacalLongitude = Double.parseDouble(String.format("%.5f", location.getLongitude()));
                    loacalLatitude = Double.parseDouble(String.format("%.5f", location.getLatitude()));
                    @SuppressLint("DefaultLocale") String address = "\n?????????????????????:" + loacalLatitude + "??????:" + loacalLongitude;
                    if (latitude == loacalLatitude && longitude == loacalLongitude) {
                        binding.lobbyTvLocation.setText("1????????? " + address);
                    } else {
                        loacalLongitude = Double.parseDouble(String.format("%.4f", location.getLongitude()));
                        loacalLatitude = Double.parseDouble(String.format("%.4f", location.getLatitude()));
                        latitude = Double.parseDouble(String.format("%.4f", latitude));
                        longitude = Double.parseDouble(String.format("%.4f", longitude));
                        if (latitude == loacalLatitude && longitude == loacalLongitude) ;
                        binding.lobbyTvLocation.setText("10???????????? " + address);
                        binding.lobbyTvLocation.setText("??????10???????????? " + address);

                    }
                } else {
                    binding.lobbyTvLocation.setText("??????????????????");
                }
            }
        });
    }

    private void sendClickInOut() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            binding.lobbyTvLocation.setText("????????????????????????");
            return;
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    showLocation(location);//???????????????

                    loacalLongitude = Double.parseDouble(String.format("%.5f", location.getLongitude()));
                    loacalLatitude = Double.parseDouble(String.format("%.5f", location.getLatitude()));
                    @SuppressLint("DefaultLocale") String address = "\n?????????????????????:" + loacalLatitude + "??????:" + loacalLongitude;
                    if (latitude == loacalLatitude && longitude == loacalLongitude) {
                        binding.lobbyTvLocation.setText("????????????,1?????????" + address);
                    } else {
                        loacalLongitude = Double.parseDouble(String.format("%.4f", location.getLongitude()));
                        loacalLatitude = Double.parseDouble(String.format("%.4f", location.getLatitude()));
                        latitude = Double.parseDouble(String.format("%.4f", latitude));
                        longitude = Double.parseDouble(String.format("%.4f", longitude));
                        if (latitude == loacalLatitude && longitude == loacalLongitude) ;
                        binding.lobbyTvLocation.setText("10???????????? " + address);

                        binding.lobbyTvLocation.setText("??????10???????????? " + address);
                        send();
                    }
                } else {
                    binding.lobbyTvLocation.setText("??????????????????");
                }
            }
        });
    }

    private void send() {
        if (!staffWork.isEmpty()) {
            auth = FirebaseAuth.getInstance();
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int mouth = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            db_UserRef = FirebaseDatabase.getInstance().getReference("clockInOut");
            Query query = db_UserRef;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    int startHour = Integer.parseInt(startWork.substring(0, 2));
                    int startMinute = Integer.parseInt(startWork.substring(3, 4));

                    int endHour = Integer.parseInt(endWork.substring(0, 2));
                    int endMinute = Integer.parseInt(endWork.substring(3, 4));

                    Calendar c = Calendar.getInstance();
                    Date now = c.getTime();
                    c.set(Calendar.HOUR_OF_DAY, startHour);
                    c.set(Calendar.MINUTE, startMinute);
                    Date startWorkDate = c.getTime();
                    c.add(Calendar.MINUTE, -30);
                    Date beforeWork = c.getTime();
                    c.add(Calendar.MINUTE, +45);
                    Date late = c.getTime();

                    Calendar c2 = Calendar.getInstance();
                    c2.set(Calendar.HOUR_OF_DAY, endHour);
                    c2.set(Calendar.MINUTE, endMinute);
                    Date endWorkDate = c2.getTime();
                    c2.add(Calendar.MINUTE, +240);
                    Date later = c2.getTime();

                    if (now.after(beforeWork) && now.before(startWorkDate)) {
                        ClockInOut clockIn = new ClockInOut();
                        clockIn.state = "??????";
                        clockIn.time = sdf.format(now.getTime());
                        db_UserRef.child(name).child(String.valueOf(year)).child(String.valueOf(mouth + 1)).child(String.valueOf(day)).child("clockIn").setValue(clockIn);
                    } else if (now.after(startWorkDate) && now.before(late)) {
                        ClockInOut clockIn = new ClockInOut();
                        clockIn.state = "??????";
                        clockIn.time = sdf.format(now.getTime());
                        db_UserRef.child(name).child(String.valueOf(year)).child(String.valueOf(mouth + 1)).child(String.valueOf(day)).child("clockIn").setValue(clockIn);
                    } else if (now.after(endWorkDate) && now.before(later)) {
                        ClockInOut clockout = new ClockInOut();
                        clockout.state = "??????";
                        clockout.time = sdf.format(now.getTime());
                        db_UserRef.child(name).child(String.valueOf(year)).child(String.valueOf(mouth + 1)).child(String.valueOf(day)).child("clockOut").setValue(clockout);
                    } else {
                        Toast.makeText(Lobby.this, "???????????????", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
        }
    }


    //??????????????????
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
        @SuppressLint("DefaultLocale") String address = "?????????????????????:" + String.format("%.5f", location.getLatitude()) +
                "??????:" + String.format("%.5f", location.getLongitude());
        binding.lobbyTvLocation.setText(address);

    }

    //???????????????????????????
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // ???????????????????????????????????????????????????

            // ????????????????????????
            if (haveInternet()) {
                // ?????????????????????????????????????????????
                binding.lobbyClockInOut.setEnabled(true);
            } else {
                // ????????????
                binding.lobbyClockInOut.setEnabled(false);
                Toast.makeText(Lobby.this, "???????????????", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //??????????????????
    private boolean haveInternet() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return info != null &&
                info.isConnected();
    }
}