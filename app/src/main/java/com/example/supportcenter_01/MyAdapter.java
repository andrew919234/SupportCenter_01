package com.example.supportcenter_01;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    int[] optionIcon;
    String[] optionString;


    public MyAdapter(int[] optionIcon, String[] optionString) {
        this.optionIcon = optionIcon;
        this.optionString = optionString;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View itemview;
        public Button optionButton;
        public TextView Id, name, genre;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemview = itemView;
            optionButton = itemView.findViewById(R.id.option_bt);
//            itemView.getRootView().setOnClickListener(v -> {
//                Button personalLeave = itemView.getRootView().findViewById(R.id.)
//            });

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder vh = new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.option_item, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int icon = optionIcon[position];
        String string = optionString[position];
        holder.optionButton.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
        holder.optionButton.setText(string);
        if(string.equals("休假申請")){
        holder.optionButton.setOnClickListener(v -> {
            holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), LeaveActivity.class));
        });

        }
    }

    @Override
    public int getItemCount() {
        return optionIcon.length;
    }
}

