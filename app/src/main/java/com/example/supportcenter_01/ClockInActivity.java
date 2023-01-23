package com.example.supportcenter_01;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.supportcenter_01.databinding.ActivityClockInBinding;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class ClockInActivity extends AppCompatActivity {
    private ActivityClockInBinding binding;
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

            if (binding.etTime.getText().toString().equals("休假時間")) {
                Calendar mCalendar = Calendar.getInstance();
                mYear = mCalendar.get(Calendar.YEAR);
                mMonth = mCalendar.get(Calendar.MONTH);
                mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

            } else {
                String insertDate = binding.etTime.getText().toString();
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

            binding.etTime.setText(show);

        }
    }
}