package com.hackcrisis.shopsafeuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsafeuser.Data.OrderDetails;
import com.hackcrisis.shopsafeuser.OrderViewActivity;
import com.hackcrisis.shopsafeuser.R;

import java.util.ArrayList;

public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<OrderDetails> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public int setOrders(ArrayList<OrderDetails> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView userNameTv, addressTv, totalAmountPaid;

        public MyViewHolder(View itemView){
            super(itemView);
            userNameTv = itemView.findViewById(R.id.order_placed_by);
            addressTv = itemView.findViewById(R.id.address_tv);
            totalAmountPaid = itemView.findViewById(R.id.total_amount_paid);
        }
    }

    public RecentOrderAdapter(Context context, ArrayList<OrderDetails> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_orders_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final OrderDetails order = dataSet.get(position);

        holder.totalAmountPaid.setText("Total Amount : INR. "+order.getTotalAmount());
        holder.addressTv.setText(order.getOrderDate());

        databaseReference.child("shopDetails").child(order.getShopId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.userNameTv.setText(dataSnapshot.child("storeName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, OrderViewActivity.class).putExtra("orderId", order.getOrderId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
