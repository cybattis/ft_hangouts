package com.example.ft_hangouts.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> contact_id, contact_first_name, contact_last_name, contact_phone_number;

    CustomAdapter(Context context, ArrayList<String> contact_id, ArrayList<String> contact_first_name, ArrayList<String> contact_last_name, ArrayList<String> contact_phone_number) {
        this.context = context;
        this.contact_id = contact_id;
        this.contact_first_name = contact_first_name;
        this.contact_last_name = contact_last_name;
        this.contact_phone_number = contact_phone_number;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.contact_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        System.out.println("onBindView: " + contact_id.get(position));
        holder.contact_id_txt.setText(String.valueOf(contact_id.get(position)));
        holder.contact_name_txt.setText(String.format("%s %s", contact_first_name.get(position), contact_last_name.get(position)));
    }

    @Override
    public int getItemCount() {
        return contact_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contact_id_txt, contact_name_txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_id_txt = itemView.findViewById(R.id.contact_card_id);
            contact_name_txt = itemView.findViewById(R.id.contact_card_name);
        }
    }
}
