package com.asm.bigmart.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asm.bigmart.Product;
import com.asm.bigmart.R;

import java.text.DecimalFormat;
import java.util.List;

public class CU_OrderDetails extends ArrayAdapter<Product> {

    List<Product> products;
    Context context;
    Product product;


    public CU_OrderDetails(@NonNull Context context, int resource, @NonNull List<Product> objects) {

        super(context, resource, objects);
        products = objects;
        this.context = context;

    }

    private class ViewHolder{
        TextView productNo,productName,productQty,productMRP,productDiscount,productTotal;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemorderdetails, parent, false);
            viewHolder.productNo = (TextView) convertView.findViewById(R.id.txt_order_detail_productNo);
            viewHolder.productName = (TextView) convertView.findViewById(R.id.txt_order_detail_productName);
            viewHolder.productQty = (TextView) convertView.findViewById(R.id.txt_order_detail_productQty);
            viewHolder.productMRP = (TextView) convertView.findViewById(R.id.txt_order_detail_productMRP);
            viewHolder.productDiscount = (TextView) convertView.findViewById(R.id.txt_order_detail_productDiscount);
            viewHolder.productTotal = (TextView) convertView.findViewById(R.id.txt_order_detail_productTotal);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        product = products.get(position);

        if (position%2 == 0)
            convertView.setBackgroundColor(Color.WHITE);
        else
            convertView.setBackgroundColor(context.getColor(R.color.lighgrey));


        final DecimalFormat formater = new DecimalFormat("0.00");
        viewHolder.productNo.setText(""+(position+1));
        viewHolder.productName.setText(""+ product.getName());
        viewHolder.productQty.setText(""+ product.getQtyNos());
        viewHolder.productMRP.setText(""+ formater.format( product.getMRP()));
        viewHolder.productDiscount.setText(""+ formater.format( product.getDiscount()));
        viewHolder.productTotal.setText(""+ formater.format(((product.getMRP()-product.getDiscount()) * product.getQtyNos())));

        return convertView;
    }
}
