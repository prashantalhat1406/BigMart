package com.asm.bigmart.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
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

public class SO_Report_OrderDisplay extends ArrayAdapter<Orders> {

    List<Orders> orders;
    Context context;
    Orders order;
    private static final String TAG = "SO_Report_OrderDisplay";

    public SO_Report_OrderDisplay(@NonNull Context context, int resource, @NonNull List<Orders> objects) {

        super(context, resource, objects);
        orders = objects;
        this.context= context;
    }

    private static class ViewHolder{
        TextView orderID,orderIDDUP,orderAmount,orderDate,orderdeliverytype;
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemorderreport, parent, false);

            viewHolder.orderID = (TextView) convertView.findViewById(R.id.txt_reportorder_ID);
            viewHolder.orderIDDUP = (TextView) convertView.findViewById(R.id.txt_reportorder_ID_DUP);
            viewHolder.orderAmount = (TextView) convertView.findViewById(R.id.txt_reportorder_totalamount);
            viewHolder.orderDate = (TextView) convertView.findViewById(R.id.txt_reportorder_date);
            viewHolder.orderdeliverytype = (TextView) convertView.findViewById(R.id.txt_reportorder_type);

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

        viewHolder.orderDate.setText("" + order.getDate());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = sdf.parse(order.getDate());
            SimpleDateFormat sdfN=new SimpleDateFormat("dd/MM/yy");
            viewHolder.orderDate.setText("" +sdfN.format(date.getTime()));
        }catch (Exception e){   Log.d(TAG,e.getMessage());   }

        return convertView;
    }
}
