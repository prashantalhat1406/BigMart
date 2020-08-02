package com.asm.bigmart;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import com.asm.bigmart.adapters.CU_CartDisplay;
import com.asm.bigmart.adapters.CU_OrderDisplay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class customercartdisplay extends AppCompatActivity {
    public Integer listPosition = 0;
    private List<Product> products;
    ListView productList;
    private long userID;
    private int count = 0;
    private String orderID = "test",exitingOrderID="";
    private Boolean flag = false;
    private Double TotalPrice = 0.0;
    FirebaseDatabase database;
    LinearLayout emptyCart,list,buttons, price;
    Integer productCount;
    Double cartAmount;
    private boolean firstLogin=true;
    CU_CartDisplay productAdaper;
    int cartProducts= 0;
    Orders existingCreatedOrder;

    public void addToExistingOrder(){

        DatabaseReference orderReference = database.getReference("Orders/" + existingCreatedOrder.ID );
        orderReference.child("amount").setValue(existingCreatedOrder.amount + TotalPrice);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        orderReference.child("date").setValue(df.format(c));

        for (Product product : products) {
            DatabaseReference productReference = database.getReference("Orders/" + existingCreatedOrder.ID + "/Products");
            productReference.child(""+product.ID).setValue(product);
        }

        DatabaseReference databaseReference = database.getReference("Users/" + userID + "/TempOrder");
        databaseReference.removeValue();
        flag = false;
        Toast error = Toast.makeText(customercartdisplay.this, "Thank you. Order Amended", Toast.LENGTH_LONG);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
        View view =error.getView();
        view.getBackground().setColorFilter(getResources().getColor(R.color.darkgreenColorButton), PorterDuff.Mode.SRC_IN);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        goToHome();
        finish();
    }


    public String getOrderID(){
        String unquieOrderID = "";

        Calendar cal=Calendar.getInstance();
        //SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = new SimpleDateFormat("MMM").format(cal.getTime());
        String day = new SimpleDateFormat("dd").format(cal.getTime());
        String hh = new SimpleDateFormat("hh").format(cal.getTime());
        String mm = new SimpleDateFormat("mm").format(cal.getTime());
        String ss = new SimpleDateFormat("ss").format(cal.getTime());

        unquieOrderID = "" + month_name.toUpperCase() + day+hh+mm+ss;

        return unquieOrderID;
    }

    public void goToHome(){
        Intent homeIntent = new Intent(customercartdisplay.this, home.class);
        Bundle extras = new Bundle();
        extras.putLong("userID", userID);
        extras.putInt("productCount", productCount);
        homeIntent.putExtras(extras);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToHome();
        //super.onBackPressed();
    }



    public void showOrderConfirmDialog(){

        final Dialog dialog = new Dialog(customercartdisplay.this);
        dialog.setContentView(R.layout.logoutdialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText("CONFIRM");

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        dialogMessage.setText("Have you added all items ?");

        Button redbutton = dialog.findViewById(R.id.dialog_btn_red);
        redbutton.setText("No");
        redbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        Button greenbutton = dialog.findViewById(R.id.dialog_btn_green);
        greenbutton.setText("Yes");
        greenbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                if (existingCreatedOrder.ID.equals("NO"))
                {
                    Intent intent = new Intent(customercartdisplay.this, customerdeliverypayment.class);
                    startActivityForResult(intent, 100);
                }else{
                    String o = existingCreatedOrder.ID;
                    addToExistingOrder();
                }
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public BroadcastReceiver positionMes = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            listPosition = intent.getIntExtra("position",0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_menuicon));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        /*ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)toolbar.getLayoutParams();
        params.height = 160;
        toolbar.setLayoutParams(params);*/



        LocalBroadcastManager.getInstance(this).registerReceiver(positionMes,new IntentFilter("message_subject_intent"));


        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");

        products = new ArrayList<Product>();
        productList = findViewById(R.id.listCart);
        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        emptyCart = findViewById(R.id.layout_cart_empty);
        list = findViewById(R.id.layout_cart_list);
        buttons = findViewById(R.id.layout_cart_buttons);
        price  = findViewById(R.id.layout_cart_price);

        final Button butConfirm = findViewById(R.id.but_cart_confirm);
        butConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderConfirmDialog();
            }
        });

        Button buttonContinueShopping = findViewById(R.id.but_cart_continueShopping);
        buttonContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });


        TextView txtContinueShopping = findViewById(R.id.txt_cart_continue);
        txtContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });


        final TextView butRemoveAll = findViewById(R.id.txt_cart_removeall);
        butRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(customercartdisplay.this);
                dialog.setContentView(R.layout.logoutdialog);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                dialogTitle.setText("REMOVE ALL");

                TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                dialogMessage.setText("Are you sure to Remove All items ?");

                Button redbutton = dialog.findViewById(R.id.dialog_btn_red);
                redbutton.setText("No");
                redbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                Button greenbutton = dialog.findViewById(R.id.dialog_btn_green);
                greenbutton.setText("Yes");
                greenbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder");
                        databaseReference.removeValue();
                        //goToHome();
                        price.setVisibility(View.GONE);
                        list.setVisibility(View.GONE);
                        buttons.setVisibility(View.GONE);
                        emptyCart.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                dialog.show();




            }
        });
        productAdaper = new CU_CartDisplay(customercartdisplay.this, R.layout.itemproductcart, products, userID, 2);
        productList.setAdapter(productAdaper);

        //existingOrders = new ArrayList<>();
        existingCreatedOrder = new Orders();
        existingCreatedOrder.setID("NO");


        Query query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Toast.makeText(customercartdisplay.this, "" + productList.getLastVisiblePosition(),Toast.LENGTH_SHORT).show();
                //Integer position =  productList.getSelectedItemPosition();
                products.clear();
                TotalPrice = 0.0;
                Double TotalSavings = 0.0;
                int count = 0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    products.add(product);
                    TotalPrice = TotalPrice + (product.QtyNos * (product.MRP -  product.Discount.doubleValue()));
                    TotalSavings = TotalSavings + (product.QtyNos * product.Discount.doubleValue() );
                    count = count + product.QtyNos;
                }

                TextView totalPrice = findViewById(R.id.txt_cartdisplay_totalamount);
                DecimalFormat formater = new DecimalFormat("0.00");
                totalPrice.setText("Total : "+ customercartdisplay.this.getResources().getString(R.string.Rupee) + " " +formater.format(TotalPrice));

                if(products.size() == 0)
                {
                    butConfirm.setEnabled(false);
                    butRemoveAll.setEnabled(false);
                    butRemoveAll.setVisibility(View.INVISIBLE);
                }else {
                    butConfirm.setEnabled(true);
                    butRemoveAll.setEnabled(true);
                    butRemoveAll.setVisibility(View.VISIBLE);
                }

                    //adapterProductCart productAdaper = new adapterProductCart(customercartdisplay.this, R.layout.itemproductcart, products, userID, 2);
                if (cartProducts != products.size())
                    productAdaper.notifyDataSetChanged();
                cartProducts = products.size();
                productList.setSelection(listPosition);


                TextView totSave = findViewById(R.id.txt_cartdisplay_totalsavings);
                totSave.setText("Savings : " + customercartdisplay.this.getResources().getString(R.string.Rupee) +" "+formater.format(TotalSavings));
                if (count == 0){
                    price.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                    buttons.setVisibility(View.GONE);
                    emptyCart.setVisibility(View.VISIBLE);
                }else{
                    price.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    buttons.setVisibility(View.VISIBLE);
                    emptyCart.setVisibility(View.GONE);
                }
                        //goToHome();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        DatabaseReference productReference = database.getReference("Orders/");
        Query ordersQuery = productReference.orderByKey();
        ordersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Orders order = postSnapshot.getValue(Orders.class);
                    if (order.userID == userID)
                        if (order.status.equals("Created"))
                            existingCreatedOrder = order;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            if (data.getStringExtra("type").length() != 0) {
                DatabaseReference productReference = database.getReference("Users/" + userID).child("/TempOrder");
                Query query = productReference.orderByKey();
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (flag) {
                            List<Product> products = new ArrayList<Product>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                products.add(postSnapshot.getValue(Product.class));
                            }
                            DatabaseReference orderReference = database.getReference("Orders/");

                            //orderID = orderReference.push().getKey();
                            orderID =  getOrderID();
                            Orders orders = new Orders();
                            orders.setID(orderID);
                            orders.setUserID(userID);
                            orders.setAmount(TotalPrice);

                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c);

                            orders.setDate(formattedDate);
                            orders.setDeliveryType(data.getStringExtra("type"));
                            orders.setPaymentMode("Card");
                            orders.setStatus("Created");
                            orderReference.child("" + orderID).setValue(orders);

                            for (Product product : products) {
                                DatabaseReference productReference = database.getReference("Orders/" + orderID + "/Products");
                                productReference.child(""+product.ID).setValue(product);
                            }

                            //updateStoreQuantity(products);

                            DatabaseReference databaseReference = database.getReference("Users/" + userID + "/TempOrder");
                            databaseReference.removeValue();
                            flag = false;
                            Toast error = Toast.makeText(customercartdisplay.this, "Thank you. Order placed", Toast.LENGTH_LONG);
                            error.setGravity(Gravity.TOP, 0, 0);
                            error.show();
                            View view =error.getView();
                            //view.setBackground(getDrawable(R.drawable.roundbutton_green));
                            view.getBackground().setColorFilter(getResources().getColor(R.color.darkgreenColorButton), PorterDuff.Mode.SRC_IN);
                            TextView text = view.findViewById(android.R.id.message);
                            text.setTextColor(Color.WHITE);
                            goToHome();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        getMenuInflater().inflate(R.menu.asmmenu, menu);
        //homeMenu = menu;
        MenuItem itemCart = menu.findItem(R.id.menu_viewcart);
        final LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        Query query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer cnt =0;
                cartAmount = 0.0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    cnt = cnt + product.QtyNos;
                    cartAmount = cartAmount + ((product.MRP - product.Discount) * product.QtyNos);
                }
                setBadgeCount(getBaseContext(), icon, ""+cnt,""+cartAmount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.asmmenu, menu);
        MenuItem itemCart = menu.findItem(R.id.menu_viewcart);
        final LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        Query query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productCount =0;
                cartAmount = 0.0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productCount = productCount + product.QtyNos;
                    cartAmount = cartAmount + ((product.MRP - product.Discount) * product.QtyNos);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        setBadgeCount(getBaseContext(), icon, ""+productCount, ""+cartAmount);
        return true;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count, String amount) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count,amount);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void showLogoutAlertDialog(){

        final Dialog dialog = new Dialog(customercartdisplay.this);
        dialog.setContentView(R.layout.logoutdialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button redbutton = dialog.findViewById(R.id.dialog_btn_red);
        redbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button greeenbutton = dialog.findViewById(R.id.dialog_btn_green);
        greeenbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent logoutIntent = new Intent(customercartdisplay.this, login.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                finish();

            }
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_viewcart) {
        }

        if (id == R.id.menu_orderhistory) {
            Intent viewCartIntent = new Intent(customercartdisplay.this, customerorderhistory.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            viewCartIntent.putExtras(extras);
            startActivity(viewCartIntent);
            //finish();
            //return true;
        }

        if (id == R.id.menu_profile) {
            Intent profileIntent = new Intent(customercartdisplay.this, customerprofile.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            profileIntent.putExtras(extras);
            startActivity(profileIntent);
            //finish();
            //return true;
        }

        if (id == R.id.menu_logout) {
            showLogoutAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }
}
