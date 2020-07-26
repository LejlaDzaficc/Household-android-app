package com.example.household;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;
    String lastClientName;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View setupMessageBubbleView(int i, View convertView, MessageViewHolder holder, Message message){
        holder.clientName = (TextView)convertView.findViewById(R.id.clientName);
        holder.messageContent = (TextView) convertView.findViewById(R.id.message_content);
        String senderName = message.getClientName();
        convertView.setTag(holder);
        if(i>0)
            lastClientName = messages.get(i-1).getClientName();
        if(!senderName.equals(lastClientName) || i==0)
            holder.clientName.setText(senderName);
        else
            holder.clientName.setVisibility(View.GONE) ;
        holder.messageContent.setText(message.getContent());
        return convertView;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);
        if (message.isBelongsToCurrentUser())
            convertView = messageInflater.inflate(R.layout.out_messages, null);
        else
            convertView = messageInflater.inflate(R.layout.in_messages, null);
        convertView = setupMessageBubbleView(i, convertView, holder, message);
        return convertView;
    }
}

class MessageViewHolder {
    public TextView clientName;
    public TextView messageContent;
}


