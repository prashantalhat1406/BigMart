package com.asm.bigmart;

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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class zretireadapterReportOrderHistory extends ArrayAdapter<Orders> {

    List<Orders> orders;
    Context context;

    Orders order;

    public zretireadapterReportOrderHistory(@NonNull Context context, int resource, @NonNull List<Orders> objects) {

        super(context, resource, objects);
        orders = objects;
        this.context= context;

    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemorderreport, parent, false);
        }


        TextView orderID = (TextView) convertView.findViewById(R.id.txt_reportorder_ID);
        TextView orderIDDUP = (TextView) convertView.findViewById(R.id.txt_reportorder_ID_DUP);
        TextView orderAmount = (TextView) convertView.findViewById(R.id.txt_reportorder_totalamount);
        TextView orderDate = (TextView) convertView.findViewById(R.id.txt_reportorder_date);
        TextView orderdeliverytype = (TextView) convertView.findViewById(R.id.txt_reportorder_type);

        order = orders.get(position);

        final DecimalFormat formater = new DecimalFormat("0.00");

        orderID.setText("" + order.ID);
        orderID.setPaintFlags(orderID.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        convertView.setBackground(context.getDrawable(R.drawable.shopownerorderhistory_homed));

        orderIDDUP.setText("" + order.ID);
        if (order.deliveryType.equals("Home Delivery")) {
            orderdeliverytype.setBackground(context.getDrawable(R.drawable.deliverytypehome));
            orderdeliverytype.setText("H");
            if (order.amount > 2000)
                //orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format( order.amount + (order.amount*0.02)));
                orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(Math.round(order.amount + (order.amount * 0.02))));
            else
                orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(Math.round(order.amount + 50)));
        }
        else
        {
            orderdeliverytype.setText("P");
            orderdeliverytype.setBackground(context.getDrawable(R.drawable.deliverytypepickup));
            orderAmount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format(Math.round( order.amount)));
        }

        orderAmount.setTextColor(ContextCompat.getColor(this.context, R.color.colorPrimaryDark));

        orderDate.setText("" + order.date);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = sdf.parse(order.date);
            SimpleDateFormat sdfN=new SimpleDateFormat("dd/MM/yy");
            orderDate.setText("" +sdfN.format(date.getTime()));
        }catch (Exception e){

        }

        return convertView;
    }
}
