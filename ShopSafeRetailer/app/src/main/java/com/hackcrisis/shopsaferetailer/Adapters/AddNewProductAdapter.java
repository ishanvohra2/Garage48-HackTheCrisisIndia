package com.hackcrisis.shopsaferetailer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hackcrisis.shopsaferetailer.Data.ProductDetails;
import com.hackcrisis.shopsaferetailer.R;

import java.util.ArrayList;

public class AddNewProductAdapter extends RecyclerView.Adapter<AddNewProductAdapter.MyViewHolder> {

    private Context context;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ProductDetails> dataSet;

    public int setProducts(ArrayList<ProductDetails> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private Button addBtn;
        private TextView productNameTv, priceTv;
        private ImageView imageView;
        private LinearLayout addedBox;

        public MyViewHolder(View itemView){
            super(itemView);
            addBtn = itemView.findViewById(R.id.add_product_btn);
            productNameTv = itemView.findViewById(R.id.cart_view_item_product_name);
            priceTv = itemView.findViewById(R.id.cart_view_item_price);
            imageView = itemView.findViewById(R.id.product_image);
            addedBox = itemView.findViewById(R.id.added_box);
        }
    }

    public AddNewProductAdapter(Context context, ArrayList<ProductDetails> dataSet){
        this.context = context;
        this.dataSet = dataSet;
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

        final ProductDetails productDetails = dataSet.get(position);

        holder.addedBox.setVisibility(View.GONE);
        holder.addBtn.setVisibility(View.VISIBLE);

        Glide.with(context.getApplicationContext()).load(productDetails.getImgUrl()).into(holder.imageView);

        holder.productNameTv.setText(productDetails.getProductName());
        holder.priceTv.setText("INR. " + productDetails.getPrice());

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("inventory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(productDetails.getProductId())
                        .child("stock").setValue(1);
                holder.addBtn.setText("Added");
                holder.addBtn.setEnabled(false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
