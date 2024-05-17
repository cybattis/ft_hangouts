package com.example.ft_hangouts.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.ui.contactPage.ContactPageActivity;

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
        Log.d("onBindViewHolder: ", contactsData.contact_id.get(position));

        holder.contact_name_txt.setText(String.format("%s %s", contactsData.contact_first_name.get(position),
                                                                contactsData.contact_last_name.get(position)));
        try {
            Uri uri = Uri.parse(contactsData.contact_image_uri.get(position));
            holder.contact_image.setImageURI(uri);
        } catch (Exception e) {
            Log.d("onBindViewHolder: ", "No image found");
            holder.contact_image.setImageResource(R.drawable.default_user);
        }
        holder.mainLayout.setOnClickListener(v -> {
            System.out.println("onBindView: " + contactsData.contact_id.get(position));
            Intent intent = new Intent(context, ContactPageActivity.class);
            intent.putExtra("id", contactsData.contact_id.get(position));
            intent.putExtra("first_name", contactsData.contact_first_name.get(position));
            intent.putExtra("last_name", contactsData.contact_last_name.get(position));
            intent.putExtra("phone_number", contactsData.contact_phone_number.get(position));
            intent.putExtra("address", contactsData.contact_address.get(position));
            intent.putExtra("city", contactsData.contact_city.get(position));
            intent.putExtra("postal_code", contactsData.contact_postal_code.get(position));
            intent.putExtra("email", contactsData.contact_email.get(position));
            if (contactsData.contact_image_uri.get(position) != null)
                intent.putExtra("image_uri", contactsData.contact_image_uri.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contactsData.contact_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contact_name_txt;
        ImageView contact_image;

        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_name_txt = itemView.findViewById(R.id.contact_card_name);
            contact_image = itemView.findViewById(R.id.contact_row_image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
