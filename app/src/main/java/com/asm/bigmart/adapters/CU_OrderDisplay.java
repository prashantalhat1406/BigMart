package com.asm.bigmart.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.asm.bigmart.Orders;
import com.asm.bigmart.R;

import java.text.DecimalFormat;
import java.util.List;

public class CU_OrderDisplay extends ArrayAdapter<Orders> {

    List<Orders> orders;
    Context context;
    Long userID;
    Orders order;
    Integer type;

    public CU_OrderDisplay(@NonNull Context context, int resource, @NonNull List<Orders> objects, Long userID, Integer type) {

        super(context, resource, objects);
        orders = objects;
        this.context= context;
        this.userID = userID;
        this.type = type;
    }

    private class ViewHolder{
        TextView orderID,orderIDDUP,orderAmount,orderStatus,orderDate,orderdeliverytype;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemorder, parent, false);

            viewHolder.orderID = (TextView) convertView.findViewById(R.id.txt_order_ID);
            viewHolder.orderIDDUP = (TextView) convertView.findViewById(R.id.txt_order_ID_DUP);
            viewHolder.orderAmount = (TextView) convertView.findViewById(R.id.txt_order_totalamount);
            viewHolder.orderStatus = (TextView) convertView.findViewById(R.id.txt_order_status);
            viewHolder.orderDate = (TextView) convertView.findViewById(R.id.txt_order_date);
            viewHolder.orderdeliverytype = (TextView) convertView.findViewById(R.id.txt_order_deliverytype);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        order = orders.get(position);

        if (position%2 == 0)
            convertView.setBackground(context.getDrawable(R.drawable.orderhistory));
        else
            convertView.setBackground(context.getDrawable(R.drawable.orderhistory_grey));

        final DecimalFormat formater = new DecimalFormat("0.00");
        viewHolder.orderID.setText("" + order.getID());
        viewHolder.orderID.setPaintFlags(viewHolder.orderID.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        viewHolder.orderIDDUP.setText("" + order.getID());
        if (order.getDeliveryType().equals("Home Delivery"))
            if (order.getAmount() > 2000)
                viewHolder.orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(Math.round( order.getAmount() + (order.getAmount()*0.02))));
            else
                viewHolder.orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format(Math.round( order.getAmount() + 50)));
        else
            viewHolder.orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format(Math.round( order.getAmount())));

        viewHolder.orderAmount.setTextColor(ContextCompat.getColor(this.context, R.color.colorPrimaryDark));

        viewHolder.orderStatus.setText("" + order.getStatus());
        viewHolder.orderDate.setText("" + order.getDate());
        viewHolder.orderdeliverytype.setText("" + order.getDeliveryType());

        viewHolder.orderStatus.setTextColor(ContextCompat.getColor(this.context, R.color.completeStatus));
        switch (order.getStatus()){
            case "Complete":
                viewHolder.orderStatus.setBackground(context.getDrawable(R.drawable.status_complete));
                viewHolder.orderStatus.setText(" Completed ");
                break;
            case "Created":
                viewHolder.orderStatus.setBackground(context.getDrawable(R.drawable.status_created));
                //viewHolder.orderStatus.setText("   Created   ");
                viewHolder.orderStatus.setText(R.string.statusCreated);
                break;
            case "InProgress":
                viewHolder.orderStatus.setBackground(context.getDrawable(R.drawable.status_inprogress));
                break;
            case "Cancelled":
                viewHolder.orderStatus.setBackground(context.getDrawable(R.drawable.status_cancelled));
                viewHolder.orderStatus.setText("   Cancelled   ");
                break;
        }

        return convertView;
    }
}
