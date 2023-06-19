package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.elements.Message;

import java.util.List;


public class MessageAdapter extends BaseAdapter {
    List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    private class ViewHolder {
        TextView date;
        TextView content;
    }

    @Override
    public int getCount() {
        return messages != null ? messages.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return messages != null ? messages.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Message message = messages.get(position);
        int layoutResId = message.getMe() ? R.layout.my_message : R.layout.other_message;

        if (convertView == null || convertView.getTag() == null || convertView.getTag().equals(layoutResId)) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(layoutResId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.date = convertView.findViewById(R.id.message_date);
            viewHolder.content = convertView.findViewById(R.id.message_text);
            convertView.setTag(layoutResId, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(layoutResId);
        }

        viewHolder.date.setText(message.getDate());
        viewHolder.content.setText(message.getContent());

        return convertView;
    }

}

