package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.supportcenter_01.RoomDataBase.LeaveApply;
import com.example.supportcenter_01.databinding.ActivityDayoffBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DayoffActivity extends AppCompatActivity {
    private ActivityDayoffBinding binding;
    private List<Calendar> selectedDates = new ArrayList<>();
    private DatabaseReference db_UserRef;
    private FirebaseAuth auth;
    private int dayOffDays = 4;
    private String leave = "劃休";
    private String userEmail;
    private float hours = 8 * dayOffDays;
    private List<Integer> dayOfMouth = new ArrayList<>();
    private int editYear;
    private int editmouth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDayoffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        Calendar editDate = Calendar.getInstance();
        editYear = editDate.get(Calendar.YEAR);
        editmouth = editDate.get(Calendar.MONTH) + 1;
        if (editmouth == 12) {
            editYear += 1;
            editmouth = 1;
        }

        getSelectDayOffDate();
        binding.btRetry.setOnClickListener(v -> {
            List<Calendar> disabledDays = new ArrayList<>();
            binding.calendarView.clearSelectedDays();
            binding.btApply.setEnabled(true);
            binding.calendarView.setDisabledDays(disabledDays);
        });

        binding.btApply.setOnClickListener(v -> {
            selectedDates = binding.calendarView.getSelectedDates();
            sendLeaveApply();
            lockCalendarView();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDataBase();
        setDayOffRules();
    }

    private void checkDataBase() {
        auth = FirebaseAuth.getInstance();
        userEmail = auth.getCurrentUser().getEmail();
        db_UserRef = FirebaseDatabase.getInstance().getReference("leaveApply");
        Query query = db_UserRef.child(String.valueOf(editYear)).child(String.valueOf(editmouth + 1)).orderByChild("userEmail").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {//若有相同資料，會啟動query
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedDates = binding.calendarView.getSelectedDates();

                LeaveApply la = new LeaveApply();//區域變數
                ArrayList<LeaveApply> tmp_array = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {//取得底下資料
                        la = ds.getValue(LeaveApply.class);
                        tmp_array.add(la);
                    }
                }
                boolean dataApear = false;

                for (LeaveApply e : tmp_array) {
                    if (e.getUserEmail().equals(userEmail)) {
                        dataApear = true;
                        dayOfMouth = e.getDayOfMouth();
                    }
                }
                if (dataApear) {
                    Calendar date = Calendar.getInstance();
                    int month = date.get(Calendar.MONTH);
                    date.set(Calendar.MONTH, editmouth);
                    int daysInMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH);

                    List<Calendar> disabledDays = new ArrayList<>();
                    for (int i = 0; i < daysInMonth; i++) {
                        Calendar dates = Calendar.getInstance();
                        int months = date.get(Calendar.MONTH);
                        dates.set(Calendar.YEAR, editYear);
                        dates.set(Calendar.MONTH, editmouth);
                        dates.set(Calendar.DAY_OF_MONTH, i + 1);
                        disabledDays.add(dates);
                    }

                    List<Calendar> selectedDays = new ArrayList<>();
                    for (int i = 0; i < dayOfMouth.size(); i++) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, editYear);
                        c.set(Calendar.MONTH, editmouth);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMouth.get(i));
                        selectedDays.add(c);
                    }
                    binding.calendarView.setSelectedDates(selectedDays);
                    binding.calendarView.setDisabledDays(disabledDays);
                    binding.btApply.setEnabled(false);
                    String days = "";
                    for (Calendar c : selectedDays) {
                        days += c.get(Calendar.DAY_OF_MONTH) + "/";
                    }
                    binding.tvDays.setText("已選取" + days);
                    Toast.makeText(DayoffActivity.this, "已完成申請", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDayOffRules() {
        auth = FirebaseAuth.getInstance();
        userEmail = auth.getCurrentUser().getEmail();
        db_UserRef = FirebaseDatabase.getInstance().getReference("leaveApply");
        Query query = db_UserRef.child(String.valueOf(editYear)).child(String.valueOf(editmouth + 1)).child("dayOffDays");
        query.addListenerForSingleValueEvent(new ValueEventListener() {//若有相同資料，會啟動query
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String data = snapshot.getValue(new GenericTypeIndicator<String>() {
                    });
                    dayOffDays = Integer.parseInt(data.trim());
                    binding.tvDays.setText("可劃假天數：" + dayOffDays + "天");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        query = db_UserRef.child(String.valueOf(editYear)).child(String.valueOf(editmouth + 1)).child("dayOfMouth");
        query.addListenerForSingleValueEvent(new ValueEventListener() {//若有相同資料，會啟動query
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    selectedDates = binding.calendarView.getSelectedDates();

                    ArrayList<Integer> data = snapshot.getValue(new GenericTypeIndicator<ArrayList<Integer>>() {
                    });

                    List<Calendar> selectedDays = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, editYear);
                        c.set(Calendar.MONTH, editmouth);
                        c.set(Calendar.DAY_OF_MONTH, data.get(i));
                        selectedDays.add(c);
                    }
                    binding.calendarView.setDisabledDays(selectedDays);
                    String days = "";
                    for (Calendar c : selectedDays) {
                        days += c.get(Calendar.DAY_OF_MONTH) + "/";
                    }
                    Toast.makeText(DayoffActivity.this, "已禁假" + days, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSelectDayOffDate() {
        Calendar setDate = Calendar.getInstance();
        int minDay = setDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        setDate.set(Calendar.DAY_OF_MONTH, minDay);
        binding.calendarView.setMinimumDate(setDate);

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.YEAR, editYear);
        maxDate.set(Calendar.MONTH, editmouth);
        int maxDay = maxDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        maxDate.set(Calendar.DAY_OF_MONTH, maxDay);
        binding.calendarView.setMaximumDate(maxDate);

        try {
            binding.calendarView.setDate(maxDate);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }


        binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(@NonNull EventDay eventDay) {
                selectedDates = binding.calendarView.getSelectedDates();
                if (selectedDates.size() > dayOffDays - 1) {
                    lockCalendarView();
                    Toast.makeText(DayoffActivity.this, "超過天數了", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void lockCalendarView() {
        Calendar date = Calendar.getInstance();
        int month = date.get(Calendar.MONTH);
        date.set(Calendar.MONTH, editmouth);
        int daysInMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<Calendar> disabledDays = new ArrayList<>();
        for (int i = 0; i < daysInMonth; i++) {
            Calendar dates = Calendar.getInstance();
            int months = date.get(Calendar.MONTH);
            dates.set(Calendar.YEAR, editYear);
            dates.set(Calendar.MONTH, editmouth);
            dates.set(Calendar.DAY_OF_MONTH, i + 1);
            disabledDays.add(dates);
        }
        binding.calendarView.setDisabledDays(disabledDays);
    }

    private void sendLeaveApply() {
        auth = FirebaseAuth.getInstance();
        userEmail = auth.getCurrentUser().getEmail();
        LeaveApply leaveApply = new LeaveApply(leave, userEmail, dayOfMouth, hours);
        db_UserRef = FirebaseDatabase.getInstance().getReference("leaveApply");


        Query query = db_UserRef.child(String.valueOf(editYear)).child(String.valueOf(editmouth + 1)).orderByChild("userEmail").equalTo(userEmail);//比對資料
        if (!(selectedDates.size() == 0)) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {//若有相同資料，會啟動query
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    selectedDates = binding.calendarView.getSelectedDates();
                    String id = "";

                    LeaveApply la = new LeaveApply();//區域變數
                    ArrayList<LeaveApply> tmp_array = new ArrayList<>();
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {//取得底下資料
                            id = ds.getKey();
                            la = ds.getValue(LeaveApply.class);
                            tmp_array.add(la);
                        }
                    }
                    boolean dataApear = false;
                    for (LeaveApply e : tmp_array) {
                        if (e.getUserEmail().equals(userEmail)) {
                            dataApear = true;
                        }

                    }
                    String days = "";
                    if (!dataApear) {
                        dayOfMouth = new ArrayList<>();
                        String year = String.valueOf(selectedDates.get(0).get(Calendar.YEAR));
                        String mouth = String.valueOf(selectedDates.get(0).get(Calendar.MONTH) + 1);
                        for (int i = 0; i < selectedDates.size(); i++) {
                            int date = selectedDates.get(i).get(Calendar.DAY_OF_MONTH);
                            days += String.valueOf(date) + "/";
                            dayOfMouth.add(date);
                        }
                        LeaveApply leaveApply = new LeaveApply(leave, userEmail, dayOfMouth, hours);
                        id = db_UserRef.child(year).child(mouth).push().getKey();
                        db_UserRef.child(year).child(mouth).child(id).setValue(leaveApply);
                        ;//放入java物件
                        db_UserRef.child(year).child(mouth).child(id).child("dayOfMouth").setValue(dayOfMouth);//放入java物件
                        binding.btApply.setEnabled(false);
                        Toast.makeText(DayoffActivity.this, "已選取 " + selectedDates.size() + " 天", Toast.LENGTH_SHORT).show();

                    } else {
                        dayOfMouth = new ArrayList<>();
                        String year = String.valueOf(selectedDates.get(0).get(Calendar.YEAR));
                        String mouth = String.valueOf(selectedDates.get(0).get(Calendar.MONTH) + 1);
                        for (int i = 0; i < selectedDates.size(); i++) {
                            int date = selectedDates.get(i).get(Calendar.DAY_OF_MONTH);
                            days += String.valueOf(date) + "/";
                            dayOfMouth.add(date);
                        }
                        HashMap<String, Object> new_data = new HashMap<>();
                        new_data.put("dayOfMouth", dayOfMouth);
                        db_UserRef.child(String.valueOf(editYear)).child(String.valueOf(editmouth + 1)).child(id).updateChildren(new_data);
                        binding.btApply.setEnabled(false);
                        Toast.makeText(DayoffActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                    }
                    binding.tvDays.setText(days);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            Toast.makeText(this, "請選擇日期", Toast.LENGTH_SHORT).show();
        }
    }


}