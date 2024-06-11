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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.ui.contacts.contactPage.ContactPageActivity;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<Contact> contactsListData;
    private final ActivityResultLauncher<Intent> contactPageActivity;

    ContactAdapter(Context context, ArrayList<Contact> contactsListData, ActivityResultLauncher<Intent> contactPageActivity) {
        this.context = context;
        this.contactsListData = contactsListData;
        this.contactPageActivity = contactPageActivity;
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
        Log.d("onBindViewHolder: ", "Binding contact: " + contactsListData.get(position).getContactId());

        String displayedName = contactsListData.get(position).hasName()
                ? contactsListData.get(position).getFullName() : contactsListData.get(position).getPhoneNumber();
        holder.contact_name_txt.setText(String.format("%s", displayedName));

        try {
            Uri uri = contactsListData.get(position).getImageUri();
            Log.d("onBindViewHolder: ", "Image found: " + uri.toString());
            if (!uri.toString().isEmpty())
                holder.contact_image.setImageURI(uri);
            else
                holder.contact_image.setImageResource(R.drawable.default_user);
        } catch (Exception e) {
            Log.e("onBindViewHolder: ", "No image found");
            holder.contact_image.setImageResource(R.drawable.default_user);
        }

        holder.mainLayout.setOnClickListener(v -> {
            Log.d("onBindViewHolder: ", "Clicked on contact: " + contactsListData.get(position).getContactId());

            Intent intent = new Intent(context, ContactPageActivity.class);
            intent.putExtra("id", contactsListData.get(position).getContactId());
            intent.putExtra("first_name", contactsListData.get(position).getFirstName());
            intent.putExtra("last_name", contactsListData.get(position).getLastName());
            intent.putExtra("phone_number", contactsListData.get(position).getPhoneNumber());
            intent.putExtra("address", contactsListData.get(position).getAddress());
            intent.putExtra("city", contactsListData.get(position).getCity());
            intent.putExtra("postal_code", contactsListData.get(position).getPostalCode());
            intent.putExtra("email", contactsListData.get(position).getEmail());
            intent.putExtra("image_uri", contactsListData.get(position).getImagePath());

            contactPageActivity.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contactsListData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contact_name_txt;
        ImageView contact_image;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_name_txt = itemView.findViewById(R.id.contact_card_name);
            contact_image = itemView.findViewById(R.id.contact_image_button);
            mainLayout = itemView.findViewById(R.id.conversation_main_layout);
        }
    }
}
