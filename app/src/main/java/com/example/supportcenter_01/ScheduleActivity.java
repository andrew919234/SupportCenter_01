package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supportcenter_01.RoomDataBase.Shift;
import com.example.supportcenter_01.databinding.ActivityScheduleBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private ActivityScheduleBinding binding;
    private DatabaseReference db_UserRef;
    private FirebaseAuth auth;
    private int editYear;
    private int editmouth;
    private int firstDayOfWeek;
    private boolean visible = true;
    private List<String> staffWork = new ArrayList<>();
    private Shift[] shifts;
    int daysInMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Calendar editDate = Calendar.getInstance();
        editYear = editDate.get(Calendar.YEAR);
        editmouth = editDate.get(Calendar.MONTH);
//        if (editmouth == 12) {
//            editYear += 1;
//            editmouth = 1;
//        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2023);
        cal.set(Calendar.MONTH, editmouth);   // 設定月份 (0-11)
        cal.set(Calendar.DAY_OF_MONTH, 1); // 設定當月第一天
        firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;

        binding.tvSchedule.setText((editmouth+1)+"月班表");

        Calendar date = Calendar.getInstance();
        date.set(Calendar.MONTH, editmouth);
        daysInMonth = date.getActualMaximum(Calendar.DAY_OF_MONTH);//當月天數
        shifts = new Shift[]{new Shift("A", 8, "9:00", "17:00"), new Shift("B", 8, "13:00", "21:00"),
                new Shift("C", 8, "17:00", "1:00"), new Shift("休", 0, "9:00", "17:00"),
                new Shift("例", 0, "9:00", "17:00"), new Shift("未定", 0, "9:00", "17:00")};
        ArrayList<ArrayList<String>> boss = new ArrayList<>();
        ArrayList<String> item = new ArrayList<>();
        item.add("A");
        item.add("B");
        ArrayList<String> item2 = new ArrayList<>();
        item2.add("A");
        item2.add("C");
        item2.add("B");
        for (int i = 0; i < daysInMonth; i++) {
            if (i % 7 < 2) {
                boss.add(item2);
            } else {
                boss.add(item);
            }
        }
        auth = FirebaseAuth.getInstance();
        String name = auth.getCurrentUser().getEmail();
        int index = name.indexOf("@");
        name = name.substring(0, index);
        db_UserRef = FirebaseDatabase.getInstance().getReference("leaveApply");
        Query query = db_UserRef.child(String.valueOf(editYear)).child(String.valueOf(editmouth + 1)).child("schedule").child(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {//取得底下資料
                    String value = ds.getValue(String.class);
                    staffWork.add(value);
                }
                binding.rvSchedule.setLayoutManager(new GridLayoutManager(ScheduleActivity.this, 7));
                MyAdapter adapter = new MyAdapter();
                binding.rvSchedule.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }//若有相同資料，會啟動query
        });


    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


        public MyAdapter() {

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public View itemview;
            public TextView date, name;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemview = itemView;
                date = itemView.findViewById(R.id.tv_title_ca);
                name = itemView.findViewById(R.id.tv_shift_ca);
                name.setOnClickListener(v -> {
                    String str = name.getText().toString().trim();
                    for (Shift s:shifts) {
                        if (s.getShiftName().equals(str)){

                            Toast.makeText(ScheduleActivity.this, "時間："+s.getStartTime()+"-"+s.getEndTime(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyAdapter.MyViewHolder vh = new MyAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.schedule_item, parent, false));
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            if(position<firstDayOfWeek){
            holder.date.setText(" ");
            holder.name.setText("");

            }else {

            holder.date.setText(position-firstDayOfWeek + 1+ " 日");
            holder.name.setText(staffWork.get(position-firstDayOfWeek));
            }

        }

        @Override
        public int getItemCount() {
            return staffWork.size()+firstDayOfWeek;
        }
    }
}