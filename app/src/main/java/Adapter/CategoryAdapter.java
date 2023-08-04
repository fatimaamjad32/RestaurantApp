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
import com.example.restaurant.OnCategoryClickListener;
import com.example.restaurant.R;

import java.util.ArrayList;

import Models.CategoryModel;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private OnCategoryClickListener onCategoryClickListener;

    ArrayList<CategoryModel> categoryModels;

    public CategoryAdapter(ArrayList<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {

        holder.categoryName.setText(categoryModels.get(position).getTitle());
        String picUrl="";
        if(position==0){
            picUrl="pizza";
            holder.catlayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.cat_background));

        }
        else if(position==1){
            picUrl="hamburger";
            holder.catlayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.cat_background1));

        }
        else if(position==2){
            picUrl="pasta";
            holder.catlayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.cat_background2));

        }
        else if(position==3){
            picUrl="donut";
            holder.catlayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.cat_background3));

        }
        if(position==4){
            picUrl="drink";
            holder.catlayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.cat_background4));

        }

        int drawableResourceId=holder.itemView.getContext().getResources().getIdentifier(picUrl,"drawable",holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .into(holder.categoryPic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCategoryClickListener != null) {
                    onCategoryClickListener.onCategoryClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        ImageView categoryPic;
        LinearLayout catlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName=itemView.findViewById(R.id.categorytitle);
            categoryPic=itemView.findViewById(R.id.categoryimage);
            catlayout=itemView.findViewById(R.id.catlayout);



        }
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }
}
