package com.example.ft_hangouts.ui.messages;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.ui.contacts.Contact;

import java.util.ArrayList;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ME_MESSAGE = 0;
    private static final int CONTACT_MESSAGE = 1;
    private final Context context;
    private final ArrayList<Message> messages;
    Contact contact;

    MessageAdapter(Context context, ArrayList<Message> messages, Contact contact) {
        this.context = context;
        this.messages = messages;
        Log.d("ConversationActivity", "Messages: " + messages);
        this.contact = contact;
    }

    public static class MeMessageViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView message;
        TextView timestamp;
        public MeMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_me);
            date = itemView.findViewById(R.id.message_date_me);
            timestamp = itemView.findViewById(R.id.message_timestamp_me);
        }
    }

    public static class ContactBubbleViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        ImageView contactImage;
        TextView message;
        TextView timestamp;
        public ContactBubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_other);
            date = itemView.findViewById(R.id.message_date_other);
            timestamp = itemView.findViewById(R.id.message_timestamp_other);
            contactImage = itemView.findViewById(R.id.contact_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isMe ? ME_MESSAGE : CONTACT_MESSAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) throws IllegalArgumentException {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == ME_MESSAGE) {
            Log.d("MessageAdapter", "Creating me message view holder");
            View view = layoutInflater.inflate(R.layout.message_bubble_me, parent, false);
            return new MeMessageViewHolder(view);
        }
        if (viewType == CONTACT_MESSAGE) {
            Log.d("MessageAdapter", "Creating contact message view holder");
            View view = layoutInflater.inflate(R.layout.message_bubble_contact, parent, false);
            return new ContactBubbleViewHolder(view);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

        switch (holder.getItemViewType()) {
            case ME_MESSAGE:
                Log.d("MessageAdapter", "Binding me message");
                MeMessageViewHolder meMessage = (MeMessageViewHolder)holder;
                // Date
                meMessage.date.setText(dateFormat.format(messages.get(position).dateSend));
                // Message
                meMessage.message.setText(messages.get(position).message);
                // Timestamp
                meMessage.timestamp.setText(timeFormat.format(messages.get(position).dateSend));
                break;

            case CONTACT_MESSAGE:
                Log.d("MessageAdapter", "Binding contact message");
                ContactBubbleViewHolder contactMessage = (ContactBubbleViewHolder)holder;
                // Date
                contactMessage.date.setText(dateFormat.format(messages.get(position).dateSend));
                // Image
                Uri uri = contact.getImageUri();
                if (!uri.toString().isEmpty())
                    contactMessage.contactImage.setImageURI(uri);
                else
                    contactMessage.contactImage.setImageResource(R.drawable.default_user);
                // Message
                contactMessage.message.setText(messages.get(position).message);
                // Timestamp
                contactMessage.timestamp.setText(timeFormat.format(messages.get(position).dateSend));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
