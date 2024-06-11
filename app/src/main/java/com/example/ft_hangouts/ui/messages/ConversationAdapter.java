package com.example.ft_hangouts.ui.messages;

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
import com.example.ft_hangouts.ui.contacts.Contact;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    Context context;
    ArrayList<ConversationItem> conversationItems;

    public ConversationAdapter(Context context, ArrayList<ConversationItem> conversationItems) {
        this.context = context;
        this.conversationItems = conversationItems;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("onCreateViewHolder: ", "Creating conversation view holder");

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.conversation_row, parent, false);

        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Contact contact = conversationItems.get(position).getContact();

        String displayedName = contact.hasName()
                ? contact.getFullName() : contact.getPhoneNumber();
        holder.name.setText(displayedName);

        Uri uri = contact.getImageUri();
        Log.d("onBindViewHolder: ", "Image found: " + uri.toString());
        if (!uri.toString().isEmpty())
            holder.contactImage.setImageURI(uri);
        else
            holder.contactImage.setImageResource(R.drawable.default_user);

        String lastMessage = conversationItems.get(position).getLastMessage();
        if (lastMessage.length() > 20)
            holder.lastMessage.setText(String.format("%s...", lastMessage.substring(0, 30)));
        else
            holder.lastMessage.setText(lastMessage);

        holder.conversationRow.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra("contact_id", conversationItems.get(position).getContact().getContactId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return conversationItems.size(); }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        ImageView contactImage;
        LinearLayout conversationRow;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_card_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            contactImage = itemView.findViewById(R.id.contact_image_button);
            conversationRow = itemView.findViewById(R.id.conversation_main_layout);
        }
    }
}
