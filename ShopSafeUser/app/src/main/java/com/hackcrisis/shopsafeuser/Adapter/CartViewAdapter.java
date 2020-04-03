package com.hackcrisis.shopsafeuser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hackcrisis.shopsafeuser.Data.CartItem;
import com.hackcrisis.shopsafeuser.R;

import java.util.ArrayList;

public class CartViewAdapter extends RecyclerView.Adapter<CartViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<CartItem> dataSet;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public int setCartItems(ArrayList<CartItem> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView productNameTv, priceTv, qtyTv;
        private Button plusBtn, minusBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            productNameTv = itemView.findViewById(R.id.cart_view_item_product_name);
            priceTv = itemView.findViewById(R.id.cart_view_item_price);
            qtyTv = itemView.findViewById(R.id.cart_view_quantity);
            plusBtn = itemView.findViewById(R.id.cart_view_item_add);
            minusBtn = itemView.findViewById(R.id.cart_view_item_subtract);
        }
    }

    public CartViewAdapter(Context context, ArrayList<CartItem> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final CartItem item = dataSet.get(position);

        holder.priceTv.setText("INR" + item.getPrice());
        holder.productNameTv.setText(item.getProductName());
        holder.qtyTv.setText(item.getQty() + "");

        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getQty()<5){
                    item.setQty(item.getQty() + 1);
                    databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item.getProductId()).child("qty").setValue(item.getQty());
                }
            }
        });

        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setQty(item.getQty() - 1);
                databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item.getProductId()).child("qty").setValue(item.getQty());
                if(item.getQty() == 0){
                    databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(item.getProductId()).removeValue();
                    dataSet.remove(position);
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
