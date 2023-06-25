package com.example.myapplication.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.elements.ContactItem;

import java.util.List;

public class ContactItemAdapter extends BaseAdapter{
    List<ContactItem> contactItems;

    public ContactItemAdapter(List<ContactItem> contactItems) {
        this.contactItems = contactItems;
    }

    private class ViewHolder {
        ImageView profilePic;
        TextView date;
        TextView lastMessage;
        TextView displayName;
    }

    @Override
    public int getCount() {
        return contactItems != null ? contactItems.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return contactItems != null ? contactItems.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        ContactItem contactItem = contactItems.get(position);
        if (convertView == null  || convertView.getTag() == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.date = convertView.findViewById(R.id.contact_item_date);
            viewHolder.lastMessage = convertView.findViewById(R.id.contact_item_last_message);
            viewHolder.displayName = convertView.findViewById(R.id.contact_item_display_name);
            viewHolder.profilePic = convertView.findViewById(R.id.contact_item_image);
            convertView.setTag(R.layout.contact_item, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.layout.contact_item);
        }

        viewHolder.date.setText(contactItem.getDate());
        viewHolder.lastMessage.setText(contactItem.getLastMessage());
        viewHolder.displayName.setText(contactItem.getDisplayName());
        viewHolder.profilePic.setImageBitmap(contactItem.getProfilePicBitmap());

        return convertView;
    }


}
