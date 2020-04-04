package com.hackcrisis.shopsafeuser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackcrisis.shopsafeuser.ConfirmOrderActivity;
import com.hackcrisis.shopsafeuser.Data.ShopDetails;
import com.hackcrisis.shopsafeuser.R;

import java.util.ArrayList;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.MyViewHolder> {

    private ArrayList<ShopDetails> dataSet;
    private Context context;

    public int setShops(ArrayList<ShopDetails> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView shopName;

        public MyViewHolder(View itemView){
            super(itemView);
            shopName = itemView.findViewById(R.id.shop_name);
        }
    }

    public ShopListAdapter(Context context, ArrayList<ShopDetails> dataSet){
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_display_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ShopDetails shop = dataSet.get(position);
        holder.shopName.setText(shop.getStoreName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmOrderActivity.shopId = shop.getStoreId();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
