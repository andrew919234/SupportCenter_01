package com.example.supportcenter_01;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    int[] optionIcon ;
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    EditText e = itemView.getRootView().findViewById(R.id.editText);
//                    e.setText(name.getText().toString());
//                    Spinner spinnerG = itemView.getRootView().findViewById(R.id.spinner);
//                    //依據要更新的ID，從strings.xml找到genre陣列，比對後選擇
//                    String[] res = {"Sufi", "Rock", "Folk", "Pop"};
//                    for (int i = 0; i < res.length; i++) {
//                        if (genre.equals(res[i]))
//                            spinnerG.setSelection(i);
//                    }
//                    Spinner spinnerId = itemView.getRootView().findViewById(R.id.spinner_up);
//                    String[] IdArray = {Id.getText().toString().trim()};
//                    ArrayAdapter adapter = new ArrayAdapter(itemView.getRootView().getContext(), android.R.layout.simple_spinner_item, IdArray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerId.setAdapter(adapter);

                }
            });

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
    }

    @Override
    public int getItemCount() {
        return optionIcon.length;
    }
}

