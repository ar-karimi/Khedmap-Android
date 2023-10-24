package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.FavoriteExpert;
import com.khedmap.khedmap.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.rmiri.skeleton.SkeletonGroup;


public class FavoriteExpertListAdapter extends RecyclerView.Adapter<FavoriteExpertListAdapter.FavoriteExpertViewHolder> {

    private Context context;
    private List<FavoriteExpert> favoriteExperts;
    private final OnRemoveItemClickListener listener;


    public FavoriteExpertListAdapter(Context context, List<FavoriteExpert> favoriteExperts, OnRemoveItemClickListener listener) {
        this.context = context;
        this.favoriteExperts = favoriteExperts;
        this.listener = listener;
    }

    public interface OnRemoveItemClickListener {
        void onRemoveItemClick(String selectedItemId);
    }

    @NonNull
    @Override
    public FavoriteExpertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_expert_list, parent, false);
        return new FavoriteExpertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteExpertViewHolder holder, int position) {
        final FavoriteExpert favoriteExpert = favoriteExperts.get(position);


        Picasso.get().load(favoriteExpert.getAvatar()).into(holder.avatar, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {
                holder.skeletonGroup.setShowSkeleton(false);
                holder.skeletonGroup.finishAnimation();
            }
        });

        holder.name.setText(favoriteExpert.getName());


        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onRemoveItemClick(favoriteExpert.getIdentification());
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteExperts.size();
    }

    class FavoriteExpertViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private SkeletonGroup skeletonGroup;
        private TextView name;
        private ImageView removeButton;


        FavoriteExpertViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            skeletonGroup = itemView.findViewById(R.id.skeletonGroup);
            name = itemView.findViewById(R.id.name);
            removeButton = itemView.findViewById(R.id.remove_button);

        }
    }

}
