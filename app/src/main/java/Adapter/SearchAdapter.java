package Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.restaurant.FoodDetails;
import com.example.restaurant.OnCategoryClickListener;
import com.example.restaurant.R;

import java.util.ArrayList;
import java.util.List;

import Models.CategoryModel;
import Models.FoodModel;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {




    ArrayList<FoodModel> foodModels;


    AdapterView.OnItemClickListener onItemClickListener;

    public SearchAdapter(ArrayList<FoodModel> foodModels) {
        this.foodModels = foodModels;
    }

    public SearchAdapter(ArrayList<FoodModel> foodModels, AdapterView.OnItemClickListener onItemClickListener) {
        this.foodModels = foodModels;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_pizza,parent,false);
        return new SearchAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        FoodModel model = foodModels.get(position);
        holder.pizzatitle.setText(model.getTitle());
        holder.pizzacontent.setText(String.valueOf(model.getPrice()));


        Glide.with(holder.itemView.getContext())
                .load(model.getImageUri())
                .into(holder.pizzaimg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onItemClickListener.onItemClick(null, holder.itemView,holder.getAbsoluteAdapterPosition(),0);
            }
        });

    }



    @Override
    public int getItemCount() {
        return foodModels.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pizzatitle,pizzacontent;
        ImageView pizzaimg;
        LinearLayout pizzalayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pizzatitle=itemView.findViewById(R.id.pizzatitle);
            pizzacontent=itemView.findViewById(R.id.pizzacontent);
            pizzalayout=itemView.findViewById(R.id.pizzalayout);
            pizzaimg=itemView.findViewById(R.id.pizzaimg);



        }
    }


public void setData(ArrayList<FoodModel> newData) {
   foodModels=newData;
   notifyDataSetChanged();
}



}
