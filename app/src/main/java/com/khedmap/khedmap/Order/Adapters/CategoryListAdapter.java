package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.Category;
import com.khedmap.khedmap.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.rmiri.skeleton.SkeletonGroup;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private final OnItemClickListener listener;


    public CategoryListAdapter(Context context, List<Category> categories, OnItemClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Category item);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position) {
        final Category category = categories.get(position);

        Picasso.get().load(category.getIcon()).into(holder.icon, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {

                holder.skeletonGroup.setShowSkeleton(false);
                holder.skeletonGroup.finishAnimation();
            }
        });


        holder.name.setText(category.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(category);

            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private SkeletonGroup skeletonGroup;

        CategoryViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            name = itemView.findViewById(R.id.category_title);
            skeletonGroup = itemView.findViewById(R.id.skeletonGroup);
        }
    }

}
