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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SO_OrderDisplay extends ArrayAdapter<Orders> {

    List<Orders> orders;
    Context context;
    Long userID;
    Orders order;
    Integer type;


    public SO_OrderDisplay(@NonNull Context context, int resource, @NonNull List<Orders> objects, Long userID, Integer type) {

        super(context, resource, objects);
        this.orders = objects;
        this.context= context;
        this.userID = userID;
        this.type = type;
    }

    private class ViewHolder {

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
                    inflate(R.layout.itemordershopowner, parent, false);

            viewHolder.orderID = (TextView) convertView.findViewById(R.id.txt_orderN_ID);
            viewHolder.orderIDDUP = (TextView) convertView.findViewById(R.id.txt_orderN_ID_DUP);
            viewHolder.orderAmount = (TextView) convertView.findViewById(R.id.txt_orderN_totalamount);
            viewHolder.orderStatus = (TextView) convertView.findViewById(R.id.txt_orderN_status);
            viewHolder.orderDate = (TextView) convertView.findViewById(R.id.txt_orderN_date);
            viewHolder.orderdeliverytype = (TextView) convertView.findViewById(R.id.txt_orderN_type);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }



        order = orders.get(position);

        final DecimalFormat formater = new DecimalFormat("0.00");
        viewHolder.orderID.setText("" + order.getID());
        viewHolder.orderID.setPaintFlags(viewHolder.orderID.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        convertView.setBackground(context.getDrawable(R.drawable.shopownerorderhistory_homed));

        viewHolder.orderIDDUP.setText("" + order.getID());
        if (order.getDeliveryType().equals("Home Delivery")) {
            viewHolder.orderdeliverytype.setBackground(context.getDrawable(R.drawable.deliverytypehome));
            viewHolder.orderdeliverytype.setText("H");
            if (order.getAmount() > 2000)
                viewHolder.orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(Math.round(order.getAmount() + (order.getAmount() * 0.02))));
            else
                viewHolder.orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(Math.round(order.getAmount() + 50)));
        }
        else
        {
            viewHolder.orderdeliverytype.setText("P");
            viewHolder.orderdeliverytype.setBackground(context.getDrawable(R.drawable.deliverytypepickup));
            viewHolder.orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format(Math.round( order.getAmount())));
        }

        viewHolder.orderAmount.setTextColor(ContextCompat.getColor(this.context, R.color.colorPrimaryDark));
        viewHolder.orderStatus.setText("" + order.getStatus());
        viewHolder.orderDate.setText("" + order.getDate());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = sdf.parse(order.getDate());
            SimpleDateFormat sdfN=new SimpleDateFormat("dd/MM/yy");
            viewHolder.orderDate.setText("" +sdfN.format(date.getTime()));
        }catch (Exception e){

        }

        viewHolder.orderStatus.setTextColor(ContextCompat.getColor(this.context, R.color.completeStatus));
        switch (order.getStatus()){
            case "Complete":
                viewHolder.orderStatus.setBackground(context.getDrawable(R.drawable.status_complete));
                viewHolder.orderStatus.setText(" Completed ");
                break;
            case "Created":
                viewHolder.orderStatus.setBackground(context.getDrawable(R.drawable.status_created));
                viewHolder.orderStatus.setText("   Created   ");
                break;
            case "InProgress":
                viewHolder.orderStatus.setBackground(context.getDrawable(R.drawable.status_inprogress));
                break;
            case "Cancelled":
                viewHolder.orderStatus.setBackground(context.getDrawable(R.drawable.status_cancelled));
                viewHolder.orderStatus.setText("  Cancelled  ");
                break;
        }
        return convertView;
    }
}
