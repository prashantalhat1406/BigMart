package com.asm.bigmart.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.asm.bigmart.Product;
import com.asm.bigmart.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.List;

public class CU_CartDisplay extends ArrayAdapter<Product> {
    List<Product> products;
    Context context;
    Long userID;
    Product product;
    Integer type;
    int lastPosition = -1;
    boolean firstLogin;

    public CU_CartDisplay(@NonNull Context context, int resource, @NonNull List<Product> objects, Long userID, Integer type) {
        super(context, resource, objects);
        products = objects;
        this.context= context;
        this.userID = userID;
        this.type = type;

    }

    private static class ViewHolder{
        Button atc,plus,minus;
        LinearLayout incrementor;
        ImageView productImage;
        TextView Name,productQuantity,mrp,savedamount,bmaamount,itemNos,remove;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.itemproductcart,parent,false);

            viewHolder.atc = (Button) convertView.findViewById(R.id.but_addToCart);
            viewHolder.plus = (Button) convertView.findViewById(R.id.but_item_plus);
            viewHolder.minus = (Button) convertView.findViewById(R.id.but_item_minus);
            viewHolder.incrementor = (LinearLayout) convertView.findViewById(R.id.layout_incrementor);
            viewHolder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            viewHolder.Name = (TextView) convertView.findViewById(R.id.product_Name);
            viewHolder.productQuantity = (TextView) convertView.findViewById(R.id.txt_product_productcart_QTY);
            viewHolder.mrp = (TextView) convertView.findViewById(R.id.product_MRP);
            viewHolder.savedamount = (TextView) convertView.findViewById(R.id.product_Save);
            viewHolder.bmaamount = (TextView) convertView.findViewById(R.id.product_BMA);
            viewHolder.itemNos = (TextView) convertView.findViewById(R.id.txt_item_number);
            viewHolder.remove = (TextView)convertView.findViewById(R.id.txt_item_remove);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final DecimalFormat formater = new DecimalFormat("0.00");
        viewHolder.atc.setTag(position);

        viewHolder.incrementor = (LinearLayout) convertView.findViewById(R.id.layout_incrementor);

        viewHolder.remove.setTag(position);
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder");
                product = products.get((Integer) v.getTag());
                databaseReference.child(""+product.getID()).removeValue();
            }
        });

        viewHolder.remove.setVisibility(View.VISIBLE);
        viewHolder.incrementor.setVisibility(View.VISIBLE);


        product = products.get(position);

        viewHolder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
        int id_ = context.getResources().getIdentifier(product.getID().toLowerCase(), "drawable", context.getPackageName());
        int noimage = context.getResources().getIdentifier("imagenotavailable", "drawable", context.getPackageName());

        /*if (id_ == 0)
            viewHolder.productImage.setImageResource(noimage);
        else
            viewHolder.productImage.setImageResource(id_);*/

        String imageName = product.getName().replace(" ","_") +"_"+ product.getName2().toLowerCase() + ".png";


            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("Products").child(imageName);

            storageReference.getBytes(1024 * 512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    viewHolder.productImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int noimage = context.getResources().getIdentifier("imagenotavailable", "drawable", context.getPackageName());
                    viewHolder.productImage.setImageResource(noimage);
                }
            });


            int outofstock = context.getResources().getIdentifier("outofstock", "drawable", context.getPackageName());
            if (product.getQty() < product.getMinStock())
            {
                viewHolder.atc.setEnabled(false);
                viewHolder.atc.setBackground(context.getDrawable(R.drawable.status_complete_disable));
                viewHolder.productImage.setImageResource(outofstock);
            }else {
                viewHolder.atc.setEnabled(true);
                viewHolder.atc.setBackground(context.getDrawable(R.drawable.roundbutton_green));
            }



        viewHolder.Name.setText(""+product.getName());
        viewHolder.productQuantity.setText(""+product.getName2().toLowerCase());
        viewHolder.mrp.setText(context.getResources().getString(R.string.Rupee) + " "+ formater.format( product.getMRP()));
        viewHolder.mrp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.bmaamount.setText(context.getResources().getString(R.string.Rupee) +" " + formater.format(product.getMRP() -  product.getDiscount().doubleValue()));



        if(product.getQtyNos() == 0) {
            viewHolder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format (product.getDiscount() ));
            viewHolder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " +  formater.format((product.getMRP() - product.getDiscount() )));

        }
        else {
            viewHolder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(product.getDiscount() * product.getQtyNos()));
            viewHolder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format( ((product.getMRP() - product.getDiscount() ) * product.getQtyNos())) );

        }

        viewHolder.itemNos.setText(""+product.getQtyNos());

        viewHolder.plus.setTag(position);
        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bintIntent = new Intent("message_subject_intent");
                bintIntent.putExtra("position", position);
                LocalBroadcastManager.getInstance(context).sendBroadcast(bintIntent);



                final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                product = products.get((Integer) v.getTag());
                if(product.getQtyNos() < 5) {
                    product.setQtyNos(product.getQtyNos() + 1);
                    DatabaseReference databaseReference = database.getReference("Users/" + userID + "/TempOrder");
                    databaseReference.child("" + product.getID()).setValue(product);
                    viewHolder.itemNos.setText("" + product.getQtyNos());
                    viewHolder.incrementor.setVisibility(View.VISIBLE);
                    viewHolder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.getDiscount() * product.getQtyNos())));
                    viewHolder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount()) * product.getQtyNos())));
                    viewHolder.bmaamount.setText("ASM " + viewHolder.bmaamount.getText());
                    viewHolder.savedamount.setText("Save " + viewHolder.savedamount.getText());
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
                            alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                            alert11.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK);
                        }
                    });
                    alert11.show();

                }


            }
        });

        viewHolder.minus.setTag(position);
        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bintIntent = new Intent("message_subject_intent");
                bintIntent.putExtra("position", position);
                LocalBroadcastManager.getInstance(context).sendBroadcast(bintIntent);

                final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                product = products.get((Integer) v.getTag());
                product.setQtyNos(product.getQtyNos() - 1);
                DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder");
                viewHolder.itemNos.setText(""+product.getQtyNos());
                if (product.getQtyNos() == 0)
                {
                    viewHolder.atc.setVisibility(View.VISIBLE);
                    viewHolder.incrementor.setVisibility(View.GONE);
                    databaseReference.child(""+product.getID()).removeValue();
                    viewHolder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format( ( product.getDiscount())));
                    viewHolder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount() ))));
                }
                else
                {
                    databaseReference.child(""+product.getID()).setValue(product);
                    viewHolder.incrementor.setVisibility(View.VISIBLE);
                    viewHolder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.getDiscount() * product.getQtyNos())));
                    viewHolder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount() ) * product.getQtyNos())));
                }
                viewHolder.bmaamount.setText("ASM " + viewHolder.bmaamount.getText());
                viewHolder.savedamount.setText("Save " + viewHolder.savedamount.getText());
            }
        });


        if(product.getQtyNos() > 0) {
            viewHolder.atc.setVisibility(View.GONE);
            viewHolder.incrementor.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.atc.setVisibility(View.VISIBLE);
            viewHolder.incrementor.setVisibility(View.GONE);
        }

        viewHolder.atc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
                product = products.get((Integer) v.getTag());
                product.setQtyNos(1);
                DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder");
                databaseReference.child(""+product.getID()).setValue(product);
                viewHolder.itemNos.setText(""+product.getQtyNos());
                v.setVisibility(View.GONE);
                viewHolder.incrementor.setVisibility(View.VISIBLE);
                viewHolder.savedamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format((product.getDiscount() * product.getQtyNos())));
                viewHolder.bmaamount.setText(context.getResources().getString(R.string.Rupee) + " " + formater.format(((product.getMRP() - product.getDiscount() ))));
                viewHolder.bmaamount.setText("ASM " + viewHolder.bmaamount.getText());
                viewHolder.savedamount.setText("Save " + viewHolder.savedamount.getText());
            }
        });

        viewHolder.bmaamount.setText("ASM " + viewHolder.bmaamount.getText());
        viewHolder.savedamount.setText("Save " + viewHolder.savedamount.getText());
        viewHolder.mrp.setText("MRP " + viewHolder.mrp.getText());

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.loaddownanim : R.anim.loadupanim);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }
}
