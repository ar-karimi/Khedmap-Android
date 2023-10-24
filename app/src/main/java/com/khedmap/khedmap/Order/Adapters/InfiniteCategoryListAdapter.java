package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.InfiniteCategory;
import com.khedmap.khedmap.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.rmiri.skeleton.SkeletonGroup;


public class InfiniteCategoryListAdapter extends RecyclerView.Adapter<InfiniteCategoryListAdapter.InfiniteCategoryViewHolder> {

    private Context context;
    private List<InfiniteCategory> infiniteCategories;
    private final OnItemClickListener listener;


    public InfiniteCategoryListAdapter(Context context, List<InfiniteCategory> infiniteCategories, OnItemClickListener listener) {
        this.context = context;
        this.infiniteCategories = infiniteCategories;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(InfiniteCategory item);
    }

    @NonNull
    @Override
    public InfiniteCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_infinite_category_list, parent, false);
        return new InfiniteCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InfiniteCategoryViewHolder holder, int position) {
        final InfiniteCategory infiniteCategory = infiniteCategories.get(position);

        Picasso.get().load(infiniteCategory.getIcon()).into(holder.icon, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {

                holder.skeletonGroup.setShowSkeleton(false);
                holder.skeletonGroup.finishAnimation();
            }
        });


        holder.name.setText(infiniteCategory.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(infiniteCategory);

            }
        });
    }

    @Override
    public int getItemCount() {
        return infiniteCategories.size();
    }

    class InfiniteCategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private SkeletonGroup skeletonGroup;

        InfiniteCategoryViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            name = itemView.findViewById(R.id.category_title);
            skeletonGroup = itemView.findViewById(R.id.skeletonGroup);
        }
    }

}
