package com.example.supportcenter_01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.res.Resources;
import android.os.Bundle;

import com.example.supportcenter_01.RoomDataBase.Leave;
import com.example.supportcenter_01.databinding.ActivityOptionBinding;

public class OptionActivity extends AppCompatActivity {
    private ActivityOptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        Resources res = getResources();
        int[] icon = {R.drawable.baseline_work_outline_24,
                R.drawable.baseline_flight_takeoff_24,
                R.drawable.favorite_48,
                R.drawable.elderly_48,
                R.drawable.personal_injury_48,
                R.drawable.airline_seat_individual_suite_48,
                R.drawable.baseline_work_24,
                R.drawable.baseline_fact_check_24,
                R.drawable.girl_48,
                R.drawable.baby_changing_station_48,
                R.drawable.pregnant_woman_48,
                R.drawable.diversity_3_48,
                R.drawable.airline_seat_flat_48,
                R.drawable.breastfeeding_48,
                R.drawable.location_away_48,};
        String[] reason = new String[15];
        Leave leave = new Leave(1,4);
        for (int i = 0; i < reason.length; i++) {
            reason[i] = leave.reason[i]
                    + "," +leave.remainingAmount[i];
        }
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.optionRv.setLayoutManager(llm);
//        binding.lobbyRvbt.setLayoutManager(new GridLayoutManager(this,3));
        MyAdapter adapterF = new MyAdapter(icon, reason);
        binding.optionRv.setAdapter(adapterF);
    }
}