package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.supportcenter_01.RoomDataBase.ClockInOut;
import com.example.supportcenter_01.databinding.ActivityClockInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ClockInActivity extends AppCompatActivity {
    private ActivityClockInBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference db_UserRef;
    int mYear;
    int mMonth;
    int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClockInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btChooseDate.setOnClickListener(v -> {
            mYear = 0;
            mMonth = 0;
            mDay = 0;

            if (binding.tvDate.getText().toString().equals("補打卡日期")) {
                Calendar mCalendar = Calendar.getInstance();
                mYear = mCalendar.get(Calendar.YEAR);
                mMonth = mCalendar.get(Calendar.MONTH);
                mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

            } else {
                String insertDate = binding.tvDate.getText().toString();
                List<String> dataStr = Arrays.stream(insertDate.split("\n")).collect(Collectors.toList());

                mYear = Integer.parseInt(dataStr.get(0).substring(0, 4));
                mMonth = Integer.parseInt(dataStr.get(0).substring(5, 7)) - 1;
                mDay = Integer.parseInt(dataStr.get(0).substring(8, 10));

            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new ClockInActivity.MyDatePicker()
                    , mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        binding.btChooseTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            new TimePickerDialog(ClockInActivity.this, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    binding.tvTime.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));

                }
            }, hour, minute, true).show();
        });

        binding.btApply.setOnClickListener(v -> {
            send();
        });

    }

    private void send() {
//        if (!staffWork.isEmpty()) {
        auth = FirebaseAuth.getInstance();
        db_UserRef = FirebaseDatabase.getInstance().getReference("apply");
        //        Query query = db_ArtistRef.orderByChild("artistName").equalTo(binding.editTextF.getText().toString().trim());//比對資料
        Query query = db_UserRef;
        if (!binding.tvDate.equals("補打卡日期") && !binding.tvTime.equals("補打卡時間")) {//若不是空白
//            switch (binding.spReason.getSelectedItem().toString()) {
//                case "忘記打卡":
            String userEmail = auth.getCurrentUser().getEmail();
            String id = db_UserRef.child("clockInOut").push().getKey();
            ClockInOut clockInOut = new ClockInOut();
            clockInOut.email = userEmail;
            clockInOut.state = binding.spReason.getSelectedItem().toString();
            clockInOut.date = binding.tvDate.getText().toString();
            clockInOut.remark = binding.etReason.getText().toString();
            clockInOut.time = binding.tvTime.getText().toString();
            db_UserRef.child("clockInOut").child(id).setValue(clockInOut);//放入java物件
//                    break;
//                case "公務":
//                    break;
//                case "早退":
//                    break;
//                case "其他":
//                    break;'
            Toast.makeText(this, "申請成功，請等待審核", Toast.LENGTH_SHORT).show();
        }
            Toast.makeText(this, "請選擇日期、打卡時間", Toast.LENGTH_SHORT).show();
    }

    class MyDatePicker implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            Toast.makeText(ClockInActivity.this,
                    year + "年" + (month + 1) + "月" + dayOfMonth + "日",
                    Toast.LENGTH_SHORT).show();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd EEEE");
            String show = sdf.format(calendar.getTime());

            binding.tvDate.setText(show);

        }
    }
}