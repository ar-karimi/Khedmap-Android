package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.R;

import java.util.List;


public class FavoriteLocationListAdapter extends RecyclerView.Adapter<FavoriteLocationListAdapter.FavoriteLocationViewHolder> {

    private Context context;
    private List<FavoriteLocation> favoriteLocations;
    private final OnItemClickListener listener;

    private int selectedItemPosition = -1;

    public FavoriteLocationListAdapter(Context context, List<FavoriteLocation> favoriteLocations, OnItemClickListener listener) {
        this.context = context;
        this.favoriteLocations = favoriteLocations;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(FavoriteLocation item);
    }

    @NonNull
    @Override
    public FavoriteLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_location_list, parent, false);
        return new FavoriteLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteLocationViewHolder holder, final int position) {
        final FavoriteLocation favoriteLocation = favoriteLocations.get(position);

        holder.name.setText(favoriteLocation.getName());
        holder.district.setText(favoriteLocation.getDistrict());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedItemPosition = position;
                notifyDataSetChanged();

                listener.onItemClick(favoriteLocation);
            }
        });

        if (selectedItemPosition == position) {
            holder.linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bordered_selected_favorite_location_background));
        } else {
            holder.linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bordered_favorite_location_background));
        }
    }

    @Override
    public int getItemCount() {
        return favoriteLocations.size();
    }

    class FavoriteLocationViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView district;
        private LinearLayout linearLayout;

        FavoriteLocationViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            district = itemView.findViewById(R.id.district);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }

}
