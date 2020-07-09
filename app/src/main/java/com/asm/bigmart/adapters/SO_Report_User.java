package com.asm.bigmart.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asm.bigmart.CryptUtil;
import com.asm.bigmart.R;
import com.asm.bigmart.User;

import java.util.List;

public class SO_Report_User extends ArrayAdapter<User> {
    List<User> users;
    Context context;
    User user;
    private static final String TAG = "SO_Report_User";


    public SO_Report_User(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        users = objects;
        this.context= context;
    }

    private static class ViewHolder{
        TextView no,name,mobile,userID;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemshopowneruser,parent,false);

            viewHolder.no = (TextView)convertView.findViewById(R.id.txt_shopowner_userno);
            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_shopowner_userName);
            viewHolder.mobile = (TextView) convertView.findViewById(R.id.txt_shopowner_userMobile);
            viewHolder.userID = (TextView) convertView.findViewById(R.id.txt_shopowner_userID);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        user = users.get(position);

        viewHolder.no.setText(""+(position+1));

        if (position%2 == 0)
            convertView.setBackgroundColor(Color.WHITE);
        else
            convertView.setBackgroundColor(context.getColor(R.color.lighgrey));

        try {
            viewHolder.name.setText("" + CryptUtil.decrypt(user.getName()));
            viewHolder.mobile.setText(" " + user.getMobile());
            viewHolder.userID.setText("" + user.getID());
        }catch (Exception e){Log.d(TAG,e.getMessage());}

        return convertView;
    }
}
