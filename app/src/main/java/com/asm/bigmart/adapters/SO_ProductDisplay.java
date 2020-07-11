package com.asm.bigmart.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asm.bigmart.Product;
import com.asm.bigmart.R;

import java.util.List;

public class SO_ProductDisplay extends ArrayAdapter<Product> {
    List<Product> products;
    Context context;
    Product product;
    int lastPosition =-1;


    public SO_ProductDisplay(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        products = objects;
        this.context= context;
    }

    private class ViewHolder{
        TextView name,mrp,stock,productID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemshopownerproduct,parent,false);
            viewHolder.name = (TextView)convertView.findViewById(R.id.txt_shopowner_productname);
            viewHolder.mrp = (TextView)convertView.findViewById(R.id.txt_shopowner_productMRP);
            viewHolder.stock = (TextView)convertView.findViewById(R.id.txt_shopowner_productStock);
            viewHolder.productID = (TextView)convertView.findViewById(R.id.txt_shopowner_productID);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        product = products.get(position);

        TextView no = (TextView)convertView.findViewById(R.id.txt_shopowner_no);
        no.setText(""+(position+1));

        if (position%2 == 0)
            convertView.setBackgroundColor(Color.WHITE);
        else
            convertView.setBackgroundColor(context.getColor(R.color.lighgrey));

        viewHolder.name.setText(""+product.getName());
        viewHolder.mrp.setText(context.getResources().getString(R.string.Rupee) + " "+product.getMRP().toString());
        viewHolder.stock.setText(""+product.getQty());
        viewHolder.productID.setText(""+product.getID());

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.loaddownanim : R.anim.loadupanim);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }
}
