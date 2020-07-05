package com.example.bigmart;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class adapterShopOwnerUser extends ArrayAdapter<User> {
    List<User> users;
    Context context;
    User user;


    public adapterShopOwnerUser(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        users = objects;
        this.context= context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemshopowneruser,parent,false);
        }

        user = users.get(position);

        TextView no = (TextView)convertView.findViewById(R.id.txt_shopowner_userno);
        no.setText(""+(position+1));

        if (position%2 == 0)
            convertView.setBackgroundColor(Color.WHITE);
        else
            convertView.setBackgroundColor(context.getColor(R.color.lighgrey));

        try {
            TextView name = (TextView) convertView.findViewById(R.id.txt_shopowner_userName);
            name.setText("" + CryptUtil.decrypt(user.getName()));
            TextView mobile = (TextView) convertView.findViewById(R.id.txt_shopowner_userMobile);
            mobile.setText(" " + user.getMobile());
            TextView userID = (TextView) convertView.findViewById(R.id.txt_shopowner_userID);
            userID.setText("" + user.getID());
        }catch (Exception e){}

        return convertView;
    }
}
