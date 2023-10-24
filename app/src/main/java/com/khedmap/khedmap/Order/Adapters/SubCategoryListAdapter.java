package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.SubCategory;
import com.khedmap.khedmap.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.rmiri.skeleton.SkeletonGroup;


public class SubCategoryListAdapter extends RecyclerView.Adapter<SubCategoryListAdapter.SubCategoryViewHolder> {

    private Context context;
    private List<SubCategory> subCategories;
    private final OnItemClickListener listener;


    public SubCategoryListAdapter(Context context, List<SubCategory> subCategories, OnItemClickListener listener) {
        this.context = context;
        this.subCategories = subCategories;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(SubCategory item);
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sub_category_list, parent, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoryViewHolder holder, int position) {
        final SubCategory subCategory = subCategories.get(position);

        Picasso.get().load(subCategory.getIcon()).into(holder.icon, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {

                holder.skeletonGroup.setShowSkeleton(false);
                holder.skeletonGroup.finishAnimation();
            }
        });


        holder.title.setText(subCategory.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(subCategory);

            }
        });
    }

    @Override
    public int getItemCount() {
        return subCategories.size();
    }

    class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title;
        private SkeletonGroup skeletonGroup;

        SubCategoryViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            title = itemView.findViewById(R.id.category_title);
            skeletonGroup = itemView.findViewById(R.id.skeletonGroup);
        }
    }

}
