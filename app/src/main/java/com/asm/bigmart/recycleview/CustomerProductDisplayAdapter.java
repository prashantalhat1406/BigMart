package com.asm.bigmart.recycleview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asm.bigmart.Product;
import com.asm.bigmart.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class CustomerProductDisplayAdapter extends RecyclerView.Adapter<CustomerProductDisplayAdapter.ViewHolder> {




    List<Product> products;
    Context context;
    Long userID;
    Product product;
    Integer type;
    FirebaseDatabase database;
    Integer productCount =0;

    public CustomerProductDisplayAdapter(Context context, List<Product> products,  Long userID, Integer type) {

        this.products = products;
        this.context = context;
        this.userID = userID;
        this.type = type;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemproduct,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final DecimalFormat formater = new DecimalFormat("0.00");
        holder.atc.setTag(position);
        holder.incrementor.setVisibility(View.GONE);
        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        try {


            product = products.get(position);


            int id_ = context.getResources().getIdentifier(product.getID().toLowerCase(), "drawable", context.getPackageName());
            int noimage = context.getResources().getIdentifier("imagenotavailable", "drawable", context.getPackageName());
            int outofstock = context.getResources().getIdentifier("outofstock", "drawable", context.getPackageName());

            if (id_ == 0)
                holder.productImage.setImageResource(noimage);
            else
                holder.productImage.setImageResource(id_);

            if (product.getQty() < product.getMinStock()) {
                holder.atc.setEnabled(false);
                holder.atc.setBackground(context.getDrawable(R.drawable.status_complete_disable));
                holder.productImage.setImageResource(outofstock);
            } else {
                holder.atc.setEnabled(true);
                holder.atc.setBackground(context.getDrawable(R.drawable.roundbutton_green));
            }


            holder.Name.setText("" + product.getName());
            holder.productQuantity.setText("" + product.getName2().toLowerCase());
            holder.mrp.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(product.getMRP()));
            holder.mrp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(product.getMRP() - product.getDiscount().doubleValue()));


            if (product.getQtyNos() == 0) {
                holder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(product.getDiscount() * 1));
                holder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.getMRP() - product.getDiscount())));

            } else {
                holder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(product.getDiscount() * product.getQtyNos()));
                holder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount()) * product.getQtyNos())));

            }

            holder.itemNos.setText("" + product.getQtyNos());

            holder.plus.setTag(position);
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*int t = getTempOrderProductCount();
                if (getTempOrderProductCount() < 90){*/
                    Intent bintIntent = new Intent("message_subject_intent");
                    bintIntent.putExtra("position", position);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(bintIntent);


                    product = products.get((Integer) v.getTag());
                    if (product.getQtyNos() < 5) {
                        product.setQtyNos(product.getQtyNos() + 1);
                        DatabaseReference databaseReference = database.getReference("Users/" + userID + "/TempOrder");
                        databaseReference.child("" + product.getID()).setValue(product);
                        holder.itemNos.setText("" + product.getQtyNos());
                        holder.incrementor.setVisibility(View.VISIBLE);
                        holder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.getDiscount() * product.getQtyNos())));
                        holder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount()) * product.getQtyNos())));
                    } else {

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
                                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                //alert11.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                            }
                        });
                        alert11.show();

                    }
                    //}

                }
            });

            holder.minus.setTag(position);
            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent bintIntent = new Intent("message_subject_intent");
                    bintIntent.putExtra("position", position);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(bintIntent);

                    //final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                    product = products.get((Integer) v.getTag());
                    product.setQtyNos(product.getQtyNos() - 1);
                    DatabaseReference databaseReference = database.getReference("Users/" + userID + "/TempOrder");
                    databaseReference.child("" + product.getID()).setValue(product);
                    holder.itemNos.setText("" + product.getQtyNos());
                    if (product.getQtyNos() == 0) {
                        holder.atc.setVisibility(View.VISIBLE);
                        holder.incrementor.setVisibility(View.GONE);
                        databaseReference.child("" + product.getID()).removeValue();
                        holder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.getDiscount())));
                        holder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount()))));
                    } else {
                        holder.incrementor.setVisibility(View.VISIBLE);
                        holder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.getDiscount() * product.getQtyNos())));
                        holder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount()) * product.getQtyNos())));
                    }
                }
            });


            if (product.getQtyNos() > 0) {
                holder.atc.setVisibility(View.GONE);
                holder.incrementor.setVisibility(View.VISIBLE);
            } else {
                holder.atc.setVisibility(View.VISIBLE);
                holder.incrementor.setVisibility(View.GONE);
            }


            holder.atc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                    product = products.get((Integer) v.getTag());
                    product.setQtyNos(1);
                    DatabaseReference databaseReference = database.getReference("Users/" + userID + "/TempOrder");
                    databaseReference.child("" + product.getID()).setValue(product);
                    holder.itemNos.setText("" + product.getQtyNos());
                    v.setVisibility(View.GONE);
                    holder.incrementor.setVisibility(View.VISIBLE);
                    holder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.getDiscount() * product.getQtyNos())));
                    holder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount()))));
                }
            });



        }catch (Exception e){
            Log.d("Ts",e.getMessage());
        }



    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView Name,productQuantity,mrp,discount,savedamount,bmaamount,itemNos;
        Button plus,minus,atc;
        LinearLayout incrementor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //
            incrementor = (LinearLayout) itemView.findViewById(R.id.layout_product_incrementor);

            atc=(Button) itemView.findViewById(R.id.but_product_addToCart);
            plus=(Button) itemView.findViewById(R.id.but_product_item_plus);
            minus=(Button) itemView.findViewById(R.id.but_product_item_minus);

            productImage= (ImageView) itemView.findViewById(R.id.img_product_image);
            Name = (TextView) itemView.findViewById(R.id.txt_product_product_Name);
            productQuantity = (TextView) itemView.findViewById(R.id.txt_product_product_QTY);
            mrp = (TextView) itemView.findViewById(R.id.txt_product_product_MRP);
            discount = (TextView) itemView.findViewById(R.id.txt_product_product_BMA);
            savedamount = (TextView) itemView.findViewById(R.id.txt_product_product_Save);
            bmaamount = (TextView) itemView.findViewById(R.id.txt_product_product_BMA);
            itemNos = (TextView) itemView.findViewById(R.id.txt_product_item_number);



        }
    }
}
