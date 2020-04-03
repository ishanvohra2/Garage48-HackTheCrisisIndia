package com.hackcrisis.shopsafeuser.Adapter;

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
import com.hackcrisis.shopsafeuser.Data.ProductDetails;
import com.hackcrisis.shopsafeuser.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ProductDetails> dataSet;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public int setProducts(ArrayList<ProductDetails> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView productNameTv, priceTv;
        private ImageView productImage;
        private Button addToCartBtn;

        public MyViewHolder(View itemView){
            super(itemView);
            productNameTv = itemView.findViewById(R.id.feed_item_title);
            priceTv = itemView.findViewById(R.id.feed_item_cost);
            productImage = itemView.findViewById(R.id.feed_item_image);
            addToCartBtn = itemView.findViewById(R.id.add_btn);
        }
    }

    public ProductAdapter(Context context, ArrayList<ProductDetails> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ProductDetails product = dataSet.get(position);

        holder.productNameTv.setText(product.getProductName());
        holder.priceTv.setText("INR "+product.getPrice());

        databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(product.getProductId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    holder.addToCartBtn.setText("Added");
                    holder.addToCartBtn.setEnabled(false);
                }
                else{
                    holder.addToCartBtn.setText(R.string.add_to_Cart);
                    holder.addToCartBtn.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(product.getProductId()).setValue(product);
                databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(product.getProductId()).child("qty").setValue(1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
