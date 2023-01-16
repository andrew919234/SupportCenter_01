package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.supportcenter_01.RoomDataBase.Leave;
import com.example.supportcenter_01.databinding.ActivityLeaveBinding;

public class LeaveActivity extends AppCompatActivity {
    private ActivityLeaveBinding binding;
    Leave leave = new Leave(1, 4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ActionBar bar = getSupportActionBar();
        bar.hide();
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
//        String[] reason = new String[15];

//        for (int i = 0; i < reason.length; i++) {
//            reason[i] = leave.reason[i]
//                    + "," + leave.remainingAmount[i];
//        }
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.optionRv.setLayoutManager(llm);
//        binding.lobbyRvbt.setLayoutManager(new GridLayoutManager(this,3));
        MyLeaveAdapter adapterF = new MyLeaveAdapter(icon, leave.reason);
        binding.optionRv.setAdapter(adapterF);
    }

    public class MyLeaveAdapter extends RecyclerView.Adapter<MyLeaveAdapter.MyLeaveViewHolder> {
        int[] optionIcon;
        String[] optionString;


        public MyLeaveAdapter(int[] optionIcon, String[] optionString) {
            this.optionIcon = optionIcon;
            this.optionString = optionString;
        }

        public class MyLeaveViewHolder extends RecyclerView.ViewHolder {
            public View itemview;
            public Button optionButton;


            public MyLeaveViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemview = itemView;
                optionButton = itemView.findViewById(R.id.leave_bt);
                optionButton.setOnClickListener(v -> {
                    TextView leaveText = itemView.getRootView().findViewById(R.id.lobby_leave);
                    TextView remainingAmount = itemView.getRootView().findViewById(R.id.remainingAmount);

                    String leaveSelected = optionButton.getText().toString().trim();
                    for (int i = 0; i < leave.reason.length; i++) {
                        if (leave.reason[i].equals(leaveSelected)) {
                            leaveText.setText(leave.reason[i]);
                            if (leave.remainingAmount[i] < 0)
                                remainingAmount.setText("");
                            else
                                remainingAmount.setText("可用天數：" + leave.remainingAmount[i] + " 天");
                        }
                    }

                });

            }
        }

        @NonNull
        @Override
        public MyLeaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyLeaveViewHolder vh = new MyLeaveViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.leave_item, parent, false));
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyLeaveViewHolder holder, int position) {
            int icon = optionIcon[position];
            String string = optionString[position];
            holder.optionButton.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
            holder.optionButton.setText(string);

        }

        @Override
        public int getItemCount() {
            return optionIcon.length;
        }
    }

}