package com.hackcrisis.shopsafeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsafeuser.Adapter.ShopListAdapter;
import com.hackcrisis.shopsafeuser.Adapter.SlotListAdapter;
import com.hackcrisis.shopsafeuser.Data.CartItem;
import com.hackcrisis.shopsafeuser.Data.OrderDetails;
import com.hackcrisis.shopsafeuser.Data.ShopDetails;
import com.hackcrisis.shopsafeuser.Data.SlotItem;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConfirmOrderActivity extends AppCompatActivity implements PaymentResultListener {

    private ArrayList<ShopDetails> shopDetails = new ArrayList<>();
    private Double userLat, userLng;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Location userLoc = new Location("");

    public static String shopId = "", slotId = "", modeOfDelivery = "";

    double totalAmount = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView shopList = findViewById(R.id.shop_recycler);
        shopList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        final ShopListAdapter shopListAdapter = new ShopListAdapter(this, new ArrayList<ShopDetails>());
        shopList.setAdapter(shopListAdapter);

        Query query = FirebaseDatabase.getInstance().getReference("shopDetails").orderByChild("storePinCode").equalTo(MainActivity.pinCode);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    shopDetails = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if(shopDetails.size()<3){
                            ShopDetails s = snapshot.getValue(ShopDetails.class);
                            shopDetails.add(s);
//                            Location shopLoc = new Location("s");
//                            shopLoc.setLongitude(s.getLongitude());
//                            shopLoc.setLatitude(s.getLatitude());
//                            float distance = userLoc.distanceTo(shopLoc);
//                            if(distance<2000){
//                                shopDetails.add(s);
//                            }
                        }
                    }
                    shopListAdapter.setShops(shopDetails);
                    shopListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RecyclerView slotList = findViewById(R.id.recycler_view);
        slotList.setLayoutManager(new LinearLayoutManager(this));

        final SlotListAdapter adapter = new SlotListAdapter(this, new ArrayList<SlotItem>());
        slotList.setAdapter(adapter);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 2000);
                if(!shopId.isEmpty()){
                    FirebaseDatabase.getInstance().getReference().child("slots").child(shopId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                ArrayList<SlotItem> slotIds = new ArrayList<>();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    SlotItem s = new SlotItem();
                                    s.setSlotId(snapshot.getKey());
                                    s.setSlotTime(snapshot.getValue(String.class));
                                    slotIds.add(s);
                                }
                                adapter.setSlots(slotIds);
                                adapter.notifyDataSetChanged();
                            }
                            else{
                                adapter.setSlots(new ArrayList<SlotItem>());
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        handler.postDelayed(r, 0);

        Button placeOrderbtn = findViewById(R.id.cart_btn);
        placeOrderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!shopId.isEmpty() && !slotId.isEmpty()){
                    Dialog dialog = onCreateDialog();
                    dialog.show();
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    dialog.getWindow().setLayout(width, height);
                    dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                }
                else{
                    Toast.makeText(ConfirmOrderActivity.this, "Please select appropriate shop and slot", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Dialog onCreateDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.mode_of_delivery_dialog);

        ImageView homeDelBtn = dialog.findViewById(R.id.home_del);
        ImageView takeAwayBtn = dialog.findViewById(R.id.take_away);

        homeDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfDelivery = "Home Delivery";
                startThePayment();
                dialog.dismiss();
            }
        });

        takeAwayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfDelivery = "Take Away";
                startThePayment();
                dialog.dismiss();
            }
        });

        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private void startThePayment() {

        databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CartItem item = snapshot.getValue(CartItem.class);
                    totalAmount = totalAmount + (item.getPrice()*item.getQty());
                }
                Toast.makeText(ConfirmOrderActivity.this, "INR " + totalAmount, Toast.LENGTH_SHORT).show();
                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_test_hm9uR6b7qPqtH6");
                checkout.setImage(R.mipmap.ic_launcher);

                Activity activity = ConfirmOrderActivity.this;

                try {
                    JSONObject options = new JSONObject();
                    options.put("currency", "INR");
                    options.put("amount", totalAmount * 100);
                    checkout.open(activity, options);

                } catch (JSONException e) {
                    Log.d("ErrorPayment",  e.getMessage());
                    Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment done!", Toast.LENGTH_SHORT).show();
        final OrderDetails orderDetails = new OrderDetails();
        orderDetails.setModeOfDelivery(modeOfDelivery);
        orderDetails.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        orderDetails.setOrderId(databaseReference.push().getKey());
        orderDetails.setShopId(shopId);
        orderDetails.setSlotId(slotId);
        orderDetails.setStatus("Placed");
        orderDetails.setTotalAmount(totalAmount);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        orderDetails.setOrderDate(formatter.format(date));

        final ArrayList<CartItem> cart = new ArrayList<>();

        databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    cart.add(snapshot.getValue(CartItem.class));
                }
                orderDetails.setCart(cart);
                databaseReference.child("orderDetails").child(orderDetails.getOrderId()).setValue(orderDetails);
                databaseReference.child("cartDetails").child(orderDetails.getUserId()).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        startActivity(new Intent(ConfirmOrderActivity.this, OrderViewActivity.class).putExtra("orderId", orderDetails.getOrderId()));
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {

    }
}
