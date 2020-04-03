package com.hackcrisis.shopsafeuser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsafeuser.Data.ProductDetails;
import com.hackcrisis.shopsafeuser.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> dataSet;
    private ArrayList<ProductDetails> productDetails;

    public int setCategories(ArrayList<String> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private RecyclerView productsList;
        private TextView categoryNameTv;

        public MyViewHolder(View itemView){
            super(itemView);
            productsList = itemView.findViewById(R.id.recycler_view);
            categoryNameTv = itemView.findViewById(R.id.feed_item_title);
        }
    }

    public CategoryAdapter(Context context, ArrayList<String> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String category = dataSet.get(position);
        holder.categoryNameTv.setText(category);

        productDetails = new ArrayList<>();

        holder.productsList.setLayoutManager(new LinearLayoutManager(context));
        final ProductAdapter productAdapter = new ProductAdapter(context, new ArrayList<ProductDetails>());
        holder.productsList.setAdapter(productAdapter);

        Query query = FirebaseDatabase.getInstance().getReference("productDetails").
                orderByChild("productCategory").equalTo(category);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    productDetails = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ProductDetails p = snapshot.getValue(ProductDetails.class);
                        productDetails.add(p);
                    }

                    if(productDetails.isEmpty()){
                        dataSet.remove(position);
                        notifyDataSetChanged();
                    }

                    productAdapter.setProducts(productDetails);
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
