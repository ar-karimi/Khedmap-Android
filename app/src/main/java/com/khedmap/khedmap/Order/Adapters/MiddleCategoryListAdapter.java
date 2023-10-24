package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.MiddleCategory;
import com.khedmap.khedmap.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.rmiri.skeleton.SkeletonGroup;

public class MiddleCategoryListAdapter  extends RecyclerView.Adapter<MiddleCategoryListAdapter.MiddleCategoryViewHolder> {

    private Context context;
    private List<MiddleCategory> middleCategories;
    private final OnItemClickListener listener;

    public MiddleCategoryListAdapter(Context context, List<MiddleCategory> middleCategories, OnItemClickListener listener) {
        this.context = context;
        this.middleCategories = middleCategories;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MiddleCategory item);
    }

    @NonNull
    @Override
    public MiddleCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_middle_category_list, parent, false);
        return new MiddleCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MiddleCategoryViewHolder holder, int position) {
        final MiddleCategory middleCategory = middleCategories.get(position);

        Picasso.get().load(middleCategory.getIcon()).into(holder.icon, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {

                holder.skeletonGroup.setShowSkeleton(false);
                holder.skeletonGroup.finishAnimation();
            }
        });

        holder.name.setText(middleCategory.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(middleCategory);

            }
        });
    }

    @Override
    public int getItemCount() {
        return middleCategories.size();
    }

    class MiddleCategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private SkeletonGroup skeletonGroup;

        MiddleCategoryViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            name = itemView.findViewById(R.id.category_title);
            skeletonGroup = itemView.findViewById(R.id.skeletonGroup);
        }
    }

}