package com.example.bigmart;

import android.content.Context;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class adapterShopOwnerProduct extends ArrayAdapter<Product> {
    List<Product> products;
    Context context;
    Product product;


    public adapterShopOwnerProduct(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        products = objects;
        this.context= context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemshopownerproduct,parent,false);
        }

        product = products.get(position);

        TextView no = (TextView)convertView.findViewById(R.id.txt_shopowner_no);
        no.setText(""+(position+1));

        TextView name = (TextView)convertView.findViewById(R.id.txt_shopowner_productname);
        name.setText(""+product.getName());
        TextView mrp = (TextView)convertView.findViewById(R.id.txt_shopowner_productMRP);
        mrp.setText(context.getResources().getString(R.string.Rupee) + " "+product.MRP.toString());
        TextView stock = (TextView)convertView.findViewById(R.id.txt_shopowner_productStock);
        stock.setText(""+product.getQty());
        TextView productID = (TextView)convertView.findViewById(R.id.txt_shopowner_productID);
        productID.setText(""+product.getID());

        return convertView;
    }
}
