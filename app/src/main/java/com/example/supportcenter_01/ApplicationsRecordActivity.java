package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supportcenter_01.RoomDataBase.ClockInOut;
import com.example.supportcenter_01.RoomDataBase.LeaveApply;
import com.example.supportcenter_01.databinding.ActivityApplicationsRecordBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class ApplicationsRecordActivity extends AppCompatActivity {
    private ActivityApplicationsRecordBinding binding;
    private DatabaseReference db_UserRef;//資料庫參考點
    private FirebaseAuth auth;
    private LinkedList<ClockInOut> clockInOuts = new LinkedList<>();
    private LinkedList<LeaveApply> leaveApplies = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApplicationsRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }

    @Override
    protected void onResume() {
        super.onResume();
        findArtist();

    }

    private void findArtist() {
        auth = FirebaseAuth.getInstance();
        db_UserRef = FirebaseDatabase.getInstance().getReference("apply").child("clockInOut");
        Query query = db_UserRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());//比對資料
        query.addListenerForSingleValueEvent(new ValueEventListener() {//若有相同資料，會啟動query
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClockInOut apply = new ClockInOut();//區域變數
                clockInOuts.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {//取得底下資料
                        apply = ds.getValue(ClockInOut.class);
                        clockInOuts.add(apply);
                    }
                }
//                binding.rvApplications.setLayoutManager(new LinearLayoutManager(ApplicationsRecordActivity.this));
//                binding.rvApplications.setAdapter(new MyAdapter());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db_UserRef = FirebaseDatabase.getInstance().getReference("apply").child("leave");
        query = db_UserRef.orderByChild("userEmail").equalTo(auth.getCurrentUser().getEmail());//比對資料
        query.addListenerForSingleValueEvent(new ValueEventListener() {//若有相同資料，會啟動query
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LeaveApply apply = new LeaveApply();//區域變數
                leaveApplies.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {//取得底下資料
                        apply = ds.getValue(LeaveApply.class);
                        leaveApplies.add(apply);
                    }
                }
                binding.rvApplications.setLayoutManager(new LinearLayoutManager(ApplicationsRecordActivity.this));
                binding.rvApplications.setAdapter(new MyAdapter());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private class MyViewHolder extends RecyclerView.ViewHolder {
            public View itemview;
            public TextView title, reason, time, date, remark;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemview = itemView;
                title = itemView.findViewById(R.id.tv_title);
                reason = itemView.findViewById(R.id.tv_reason);
                time = itemView.findViewById(R.id.tv_time);
                date = itemView.findViewById(R.id.tv_date);
                remark = itemView.findViewById(R.id.tv_remark);

            }

        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemview = inflater.inflate(R.layout.apply_record_item, parent, false);
            MyViewHolder vh = new MyViewHolder(itemview);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            if (position < clockInOuts.size()) {
                ClockInOut clockInOut = clockInOuts.get(position);
                holder.title.setText("補打卡申請");
                holder.reason.setText(clockInOut.state);
                holder.time.setText(clockInOut.time);
                holder.date.setText(clockInOut.date);
                holder.remark.setText(clockInOut.remark);
            } else {
                LeaveApply leaveApply = leaveApplies.get(position-clockInOuts.size());
                holder.title.setText("休假申請");
                holder.reason.setText(leaveApply.getLeave());
                holder.date.setText(leaveApply.getStartDate());
                holder.time.setText(leaveApply.getEndDate());
                holder.remark.setText(leaveApply.getReason());

            }
        }

        @Override
        public int getItemCount() {
            return clockInOuts.size() + leaveApplies.size();
        }
    }

}
