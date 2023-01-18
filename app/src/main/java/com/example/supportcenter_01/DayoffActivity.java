package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.supportcenter_01.databinding.ActivityDayoffBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DayoffActivity extends AppCompatActivity {
    private ActivityDayoffBinding binding;
    private List<Calendar> selectedDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDayoffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSelectDayOffDate();
        binding.btRetry.setOnClickListener(v -> {
            List<Calendar> disabledDays = new ArrayList<>();
            binding.calendarView.setDisabledDays(disabledDays);
        });

        binding.btApply.setOnClickListener(v -> {
            selectedDates = binding.calendarView.getSelectedDates();
            Toast.makeText(this, selectedDates.size()+"", Toast.LENGTH_SHORT).show();
        });


    }

    private void getSelectDayOffDate() {
        Calendar setDate = Calendar.getInstance();
        int minDay = setDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        setDate.set(Calendar.DAY_OF_MONTH, minDay);
        binding.calendarView.setMinimumDate(setDate);
        int maxYear = setDate.get(Calendar.YEAR);
        int maxMonth = setDate.get(Calendar.MONTH);

        Calendar maxDate = Calendar.getInstance();
        if (minDay == 12) {
            maxDate.set(Calendar.YEAR, maxYear + 1);
            maxDate.set(Calendar.MONTH, 1);
            int maxDay = maxDate.getActualMaximum(Calendar.DAY_OF_MONTH);
            maxDate.set(Calendar.DAY_OF_MONTH, maxDay);
            binding.calendarView.setMaximumDate(maxDate);
        } else {
            maxDate.set(Calendar.YEAR, maxYear);
            maxDate.set(Calendar.MONTH, maxMonth + 1);
            int maxDay = maxDate.getActualMaximum(Calendar.DAY_OF_MONTH);
            maxDate.set(Calendar.DAY_OF_MONTH, maxDay);
            binding.calendarView.setMaximumDate(maxDate);
        }
        try {
            binding.calendarView.setDate(maxDate);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }


        binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(@NonNull EventDay eventDay) {
                String day = "";
                selectedDates = binding.calendarView.getSelectedDates();
                if (selectedDates.size() >3)  {
                    Calendar date = Calendar.getInstance();
                    int month = date.get(Calendar.MONTH);
                    date.set(Calendar.MONTH, month + 1);
                    int daysInMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH);
                    List<Calendar> disabledDays = new ArrayList<>();

                    for (int i = 0; i < daysInMonth; i++) {
                        Calendar dates = Calendar.getInstance();
                        int months = date.get(Calendar.MONTH);
                        dates.set(Calendar.MONTH, month + 1);
                        dates.set(Calendar.DAY_OF_MONTH, i + 1);
                        disabledDays.add(dates);
                    }
                    binding.calendarView.setDisabledDays(disabledDays);

                    Toast.makeText(DayoffActivity.this, "超過天數了", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}