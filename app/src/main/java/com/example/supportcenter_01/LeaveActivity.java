package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supportcenter_01.RoomDataBase.Leave;
import com.example.supportcenter_01.databinding.ActivityLeaveBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class LeaveActivity extends AppCompatActivity {
    private ActivityLeaveBinding binding;
    private DatabaseReference db_UserRef;
    private FirebaseAuth auth;
    Leave leave = new Leave(1, 4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        db_UserRef = FirebaseDatabase.getInstance().getReference("leaveApply");

        ActionBar bar = getSupportActionBar();
        bar.hide();
        int[] icon = {R.drawable.baseline_flight_takeoff_24,
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

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.optionRv.setLayoutManager(llm);
//        binding.lobbyRvbt.setLayoutManager(new GridLayoutManager(this,3));
        MyLeaveAdapter adapterF = new MyLeaveAdapter(icon, leave.reason);
        binding.optionRv.setAdapter(adapterF);

        binding.btChooseTime.setOnClickListener(v -> {
            int mYear = 0;
            int mMonth = 0;
            int mDay = 0;

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
                    new MyDatePicker()
                    , mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        binding.btApply.setOnClickListener(v -> {
            db_UserRef = FirebaseDatabase.getInstance().getReference("users");
//            Query query = db_UserRef.orderByChild("email").equalTo(email);//比對資料
//            if (!TextUtils.isEmpty(name)) {
//                query.addListenerForSingleValueEvent(new ValueEventListener() {//若有相同資料，會啟動query

//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        User user = new User();//區域變數
//                        ArrayList<User> tmp_array = new ArrayList<>();
//                        if (snapshot.exists()) {
//                            for (DataSnapshot ds : snapshot.getChildren()) {//取得底下資料
//                                user = ds.getValue(User.class);
//                                tmp_array.add(user);
//                            }
//                        }
//                        boolean dataApear = false;
//                        for (User e : tmp_array) {
//                            if (e.email.equals(email)) {
//                                dataApear = true;
//                                Toast.makeText(StaffCRUD.this, "資料已存在", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                        if (!dataApear) {
//                            String id = db_UserRef.push().getKey();
//                            User newUser = new User(id, email, GUInumber, name, sex, birthday, onBoardTime);
//                            db_UserRef.child(id).setValue(newUser);//放入java物件
//                            reAdapter();
//                            Toast.makeText(StaffCRUD.this, "user name :" + name + "新增成功", Toast.LENGTH_SHORT).show();
//                            addUserAcount(email, GUInumber);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                });
//            } else {
//                Toast.makeText(this, "請輸入名字", Toast.LENGTH_SHORT).show();
//            }
//        }


    });
}

class MyDatePicker implements DatePickerDialog.OnDateSetListener {

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Toast.makeText(LeaveActivity.this,
                year + "年" + (month + 1) + "月" + dayOfMonth + "日",
                Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd EEEE");
        String show = sdf.format(calendar.getTime());

        binding.etTime.setText(show);

    }
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