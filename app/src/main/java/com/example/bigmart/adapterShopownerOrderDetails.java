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

import java.text.DecimalFormat;
import java.util.List;

public class adapterShopownerOrderDetails extends ArrayAdapter<Product> {

    List<Product> products;
    List<Product> databaseProducts;
    Context context;
    Product product;


    public adapterShopownerOrderDetails(@NonNull Context context, int resource, @NonNull List<Product> objects, @NonNull List<Product> databaseobjects) {

        super(context, resource, objects);
        products = objects;
        databaseProducts = databaseobjects;
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

        productQty.setTextColor(getContext().getColor(R.color.colorWhite));

        for (Product databaseProduct : databaseProducts) {
            if (databaseProduct.ID.equals(product.ID))
            {
                if (databaseProduct.Qty < product.QtyNos)
                {
                    productQty.setBackgroundColor(getContext().getColor(R.color.redColorButton));
                }

                else
                {
                    productQty.setBackgroundColor(getContext().getColor(R.color.greenColorButton));
                }

                break;
            }
        }

        productQty.setText(""+ product.QtyNos);
        productQty.setTextSize(16);
        productMRP.setText(""+ formater.format( product.MRP));
        productDiscount.setText(""+ formater.format( product.Discount));
        productTotal.setText(""+ formater.format(((product.MRP-product.Discount) * product.QtyNos)));


        return convertView;
    }
}
