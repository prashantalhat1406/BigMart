package com.asm.bigmart;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class adapterOrderDetails extends ArrayAdapter<Product> {

    List<Product> products;

    Context context;
    Product product;


    public adapterOrderDetails(@NonNull Context context, int resource, @NonNull List<Product> objects) {

        super(context, resource, objects);
        products = objects;

        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemorderdetails, parent, false);
        }

        if (position%2 == 0)
            convertView.setBackgroundColor(Color.WHITE);
        else
            convertView.setBackgroundColor(context.getColor(R.color.lighgrey));

        TextView productNo = (TextView) convertView.findViewById(R.id.txt_order_detail_productNo);
        TextView productName = (TextView) convertView.findViewById(R.id.txt_order_detail_productName);
        TextView productQty = (TextView) convertView.findViewById(R.id.txt_order_detail_productQty);
        TextView productMRP = (TextView) convertView.findViewById(R.id.txt_order_detail_productMRP);
        TextView productDiscount = (TextView) convertView.findViewById(R.id.txt_order_detail_productDiscount);
        TextView productTotal = (TextView) convertView.findViewById(R.id.txt_order_detail_productTotal);
        product = products.get(position);

        final DecimalFormat formater = new DecimalFormat("0.00");
        productNo.setText(""+(position+1));
        productName.setText(""+ product.Name);



        productQty.setText(""+ product.QtyNos);
        productMRP.setText(""+ formater.format( product.MRP));
        productDiscount.setText(""+ formater.format( product.Discount));
        productTotal.setText(""+ formater.format(((product.MRP-product.Discount) * product.QtyNos)));


        return convertView;
    }
}
