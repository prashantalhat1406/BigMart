package com.example.bigmart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class adapterProduct extends ArrayAdapter<Product> {
    List<Product> products;
    Context context;
    Long userID;
    Product product;
    Integer type;

    public adapterProduct(@NonNull Context context, int resource, @NonNull List<Product> objects, Long userID, Integer type) {
        super(context, resource, objects);
        products = objects;
        this.context= context;
        this.userID = userID;
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemproduct,parent,false);
        }

        final DecimalFormat formater = new DecimalFormat("0.00");


        final Button atc = (Button) convertView.findViewById(R.id.but_product_addToCart);
        atc.setTag(position);


        final LinearLayout incrementor = (LinearLayout) convertView.findViewById(R.id.layout_product_incrementor);
        incrementor.setVisibility(View.GONE);


        product = products.get(position);

        ImageView productImage = (ImageView) convertView.findViewById(R.id.img_product_image);
        int id_ = context.getResources().getIdentifier(product.ID.toLowerCase(), "drawable", context.getPackageName());
        int noimage = context.getResources().getIdentifier("imagenotavailable", "drawable", context.getPackageName());
        int outofstock = context.getResources().getIdentifier("outofstock", "drawable", context.getPackageName());

        if (id_ == 0)
            productImage.setImageResource(noimage);
        else
            productImage.setImageResource(id_);

        if (product.Qty < product.MinStock)
        {
            atc.setEnabled(false);
            atc.setBackground(context.getDrawable(R.drawable.status_complete_disable));
            productImage.setImageResource(outofstock);
        }else {
            atc.setEnabled(true);
            atc.setBackground(context.getDrawable(R.drawable.roundbutton_green));
        }

        TextView Name = (TextView)convertView.findViewById(R.id.txt_product_product_Name);
        Name.setText(""+product.getName());
        TextView mrp = (TextView)convertView.findViewById(R.id.txt_product_product_MRP);
        mrp.setText(context.getResources().getString(R.string.Rupee) + " "+ formater.format( product.MRP));
        mrp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        TextView discount = (TextView)convertView.findViewById(R.id.txt_product_product_BMA);
        discount.setText(context.getResources().getString(R.string.Rupee) +" " + formater.format(product.MRP -  product.Discount.doubleValue()));
        final TextView savedamount = (TextView)convertView.findViewById(R.id.txt_product_product_Save);
        final TextView bmaamount = (TextView)convertView.findViewById(R.id.txt_product_product_BMA);


        if(product.QtyNos == 0) {
            savedamount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format (product.Discount * 1));
            bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format((product.MRP - product.Discount )));

        }
        else {
            savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(product.Discount * product.QtyNos));
            bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format( ((product.MRP - product.Discount ) * product.QtyNos)) );

        }

        final TextView itemNos = (TextView) convertView.findViewById(R.id.txt_product_item_number);
        itemNos.setText(""+product.getQtyNos());

        Button plus = (Button) convertView.findViewById(R.id.but_product_item_plus);
        plus.setTag(position);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bintIntent = new Intent("message_subject_intent");
                bintIntent.putExtra("position", position);
                LocalBroadcastManager.getInstance(context).sendBroadcast(bintIntent);


                final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                product = products.get((Integer) v.getTag());
                if(product.QtyNos < 5) {
                    product.setQtyNos(product.getQtyNos() + 1);
                    DatabaseReference databaseReference = database.getReference("Users/" + userID + "/TempOrder");
                    databaseReference.child("" + product.ID).setValue(product);
                    itemNos.setText("" + product.QtyNos);
                    incrementor.setVisibility(View.VISIBLE);
                    savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.Discount * product.QtyNos)));
                    bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.MRP - product.Discount) * product.QtyNos)));
                }else{

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Max Limit Reached");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(
                            "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert11 = builder1.create();
                    alert11.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            //alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                            alert11.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                        }
                    });
                    alert11.show();

                }

            }
        });

        Button minus = (Button) convertView.findViewById(R.id.but_product_item_minus);
        minus.setTag(position);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bintIntent = new Intent("message_subject_intent");
                bintIntent.putExtra("position", position);
                LocalBroadcastManager.getInstance(context).sendBroadcast(bintIntent);

                final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                product = products.get((Integer) v.getTag());
                product.setQtyNos(product.getQtyNos() - 1);
                DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder");
                databaseReference.child(""+product.ID).setValue(product);
                itemNos.setText(""+product.QtyNos);
                if (product.QtyNos == 0)
                {
                    atc.setVisibility(View.VISIBLE);
                    incrementor.setVisibility(View.GONE);
                    databaseReference.child(""+product.ID).removeValue();
                    savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format( ( product.Discount)));
                    bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.MRP - product.Discount ))));
                }
                else
                {
                    incrementor.setVisibility(View.VISIBLE);
                    savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.Discount * product.QtyNos)));
                    bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.MRP - product.Discount ) * product.QtyNos)));
                }
            }
        });


        if(product.getQtyNos() > 0) {
            atc.setVisibility(View.GONE);
            incrementor.setVisibility(View.VISIBLE);
        }
        else {
            atc.setVisibility(View.VISIBLE);
            incrementor.setVisibility(View.GONE);
        }




        atc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                product = products.get((Integer) v.getTag());
                product.setQtyNos(1);
                DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder");
                databaseReference.child(""+product.ID).setValue(product);
                itemNos.setText(""+product.QtyNos);
                v.setVisibility(View.GONE);
                incrementor.setVisibility(View.VISIBLE);
                savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.Discount * product.QtyNos)));
                bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.MRP - product.Discount ))));
            }
        });


        return convertView;
    }
}
