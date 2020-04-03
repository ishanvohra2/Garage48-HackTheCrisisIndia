package com.hackcrisis.shopsaferetailer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsaferetailer.R;

import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private int quantity;

    public int setProductIds(ArrayList<String> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView priceTv, nameTv, qtyTv;
        private ImageView imageView;
        private Button addTv, minusTv;

        public MyViewHolder(View itemView){
            super(itemView);
            priceTv = itemView.findViewById(R.id.cart_view_item_price);
            nameTv = itemView.findViewById(R.id.cart_view_item_product_name);
            qtyTv = itemView.findViewById(R.id.cart_view_quantity);
            imageView = itemView.findViewById(R.id.product_image);
            addTv = itemView.findViewById(R.id.cart_view_item_add);
            minusTv = itemView.findViewById(R.id.cart_view_item_subtract);
        }
    }

    public InventoryAdapter(Context context, ArrayList<String> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final String productId = dataSet.get(position);

        databaseReference.child("productDetails").child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.nameTv.setText(dataSnapshot.child("productName").getValue(String.class));
                holder.priceTv.setText("INR. " + dataSnapshot.child("price").getValue(Float.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("inventory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        quantity = dataSnapshot.child("stock").getValue(Integer.class);
                        holder.qtyTv.setText(quantity + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.addTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = quantity + 1;
                databaseReference.child("inventory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(productId).child("stock")
                        .setValue(quantity);
            }
        });

        holder.minusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = quantity - 1;
                databaseReference.child("inventory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(productId).child("stock")
                        .setValue(quantity);

                if(quantity == 0){
                    databaseReference.child("inventory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(productId).removeValue();
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
