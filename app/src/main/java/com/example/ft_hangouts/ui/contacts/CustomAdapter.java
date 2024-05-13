package com.example.ft_hangouts.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.ui.addContact.AddContactActivity;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private final Context context;
    private final ContactsData contactsData;

    CustomAdapter(Context context, ContactsData contactsData) {
        this.context = context;
        this.contactsData = contactsData;
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
        System.out.println("onBindView: " + contactsData.contact_id.get(position));

        holder.contact_id_txt.setText(String.valueOf(contactsData.contact_id.get(position)));
        holder.contact_name_txt.setText(String.format("%s %s", contactsData.contact_first_name.get(position),
                                                                contactsData.contact_last_name.get(position)));

        holder.mainLayout.setOnClickListener(v -> {
            System.out.println("onBindView: " + contactsData.contact_id.get(position));
            Intent intent = new Intent(context, AddContactActivity.class);
            intent.putExtra("id", contactsData.contact_id.get(position));
            intent.putExtra("first_name", contactsData.contact_first_name.get(position));
            intent.putExtra("last_name", contactsData.contact_last_name.get(position));
            intent.putExtra("phone_number", contactsData.contact_phone_number.get(position));
            intent.putExtra("address", contactsData.contact_address.get(position));
            intent.putExtra("city", contactsData.contact_city.get(position));
            intent.putExtra("postal_code", contactsData.contact_postal_code.get(position));
            intent.putExtra("email", contactsData.contact_email.get(position));
            intent.putExtra("image_uri", contactsData.contact_image_uri.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contactsData.contact_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contact_id_txt, contact_name_txt;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_id_txt = itemView.findViewById(R.id.contact_card_id);
            contact_name_txt = itemView.findViewById(R.id.contact_card_name);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
