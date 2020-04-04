package com.hackcrisis.shopsaferetailer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackcrisis.shopsaferetailer.Data.CartItem;
import com.hackcrisis.shopsaferetailer.R;

import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {

    private ArrayList<CartItem> dataSet;
    private Context context;

    public int setItems(ArrayList<CartItem> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTv, costTv;

        public MyViewHolder(View itemView){
            super(itemView);
            nameTv = itemView.findViewById(R.id.name_tv);
            costTv = itemView.findViewById(R.id.cost_tv);
        }
    }

    public OrderListAdapter(Context context, ArrayList<CartItem> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CartItem item = dataSet.get(position);

        holder.nameTv.setText(item.getProductName() + " x " + item.getQty());
        holder.costTv.setText("INR. " + item.getPrice() * item.getQty());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
