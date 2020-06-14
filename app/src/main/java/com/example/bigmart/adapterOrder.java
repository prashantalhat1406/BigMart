package com.example.bigmart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.List;

public class adapterOrder extends ArrayAdapter<Orders> {

    List<Orders> orders;
    Context context;
    Long userID;
    Orders order;
    Integer type;

    public adapterOrder(@NonNull Context context, int resource, @NonNull List<Orders> objects, Long userID, Integer type) {

        super(context, resource, objects);
        orders = objects;
        this.context= context;
        this.userID = userID;
        this.type = type;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemorder, parent, false);
        }


        TextView orderID = (TextView) convertView.findViewById(R.id.txt_order_ID);
        TextView orderIDDUP = (TextView) convertView.findViewById(R.id.txt_order_ID_DUP);
        TextView orderAmount = (TextView) convertView.findViewById(R.id.txt_order_totalamount);
        TextView orderStatus = (TextView) convertView.findViewById(R.id.txt_order_status);
        TextView orderDate = (TextView) convertView.findViewById(R.id.txt_order_date);
        TextView orderdeliverytype = (TextView) convertView.findViewById(R.id.txt_order_deliverytype);
        order = orders.get(position);

        final DecimalFormat formater = new DecimalFormat("0.00");



        orderID.setText("" + order.ID.substring(order.ID.length() - 5).toUpperCase());
        orderIDDUP.setText("" + order.ID);
        if (order.deliveryType.equals("Home Delivery"))
            if (order.amount > 2000)
                orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format( order.amount + (order.amount*0.02)));
            else
                orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format( order.amount + 50));
        else
            orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format( order.amount));

        orderAmount.setTextColor(ContextCompat.getColor(this.context, R.color.colorPrimaryDark));

        orderStatus.setText("" + order.status);
        orderDate.setText("" + order.date);
        orderdeliverytype.setText("" + order.deliveryType);

        orderStatus.setTextColor(ContextCompat.getColor(this.context, R.color.completeStatus));
        switch (order.status){
            case "Complete": orderStatus.setBackground(context.getDrawable(R.drawable.status_complete));
            //convertView.setBackgroundColor(context.getColor(R.color.lightGreen));
                break;
            case "Created": orderStatus.setBackground(context.getDrawable(R.drawable.status_created));
                break;
            case "InProgress": orderStatus.setBackground(context.getDrawable(R.drawable.status_inprogress));
                break;
            case "Cancelled": orderStatus.setBackground(context.getDrawable(R.drawable.status_cancelled));
                break;
        }



        return convertView;
    }
}
