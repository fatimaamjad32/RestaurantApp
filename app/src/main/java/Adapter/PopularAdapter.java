package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.restaurant.R;

import java.util.ArrayList;

import Models.CategoryModel;
import Models.FoodModel;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    ArrayList<FoodModel> foodModels;

    public PopularAdapter(ArrayList<FoodModel> foodModels) {
        this.foodModels = foodModels;
    }

    @NonNull
    @Override
    public PopularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.ViewHolder holder, int position) {

        holder.tpopulartitle.setText(foodModels.get(position).getTitle());
        holder.tpopularprice.setText(String.valueOf(foodModels.get(position).getPrice()));


       int drawableResourceId=holder.itemView.getContext().getResources().getIdentifier(foodModels.get(position).getImageUri(),"drawable",holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .into(holder.ipopularimg);

    }

    @Override
    public int getItemCount() {
        return foodModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tpopulartitle,tpopularprice;
        ImageView ipopularimg;
        LinearLayout popularlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tpopulartitle=itemView.findViewById(R.id.tpopulartitle);
            tpopularprice=itemView.findViewById(R.id.tpopularprice);
            popularlayout=itemView.findViewById(R.id.popularlayout);
            ipopularimg=itemView.findViewById(R.id.ipopularimg);



        }
    }
}
