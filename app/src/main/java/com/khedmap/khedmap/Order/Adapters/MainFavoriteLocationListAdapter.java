package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.R;

import java.util.List;


public class MainFavoriteLocationListAdapter extends RecyclerView.Adapter<MainFavoriteLocationListAdapter.MainFavoriteLocationViewHolder> {

    private Context context;
    private List<FavoriteLocation> favoriteLocations;
    private final OnEditItemClickListener editListener;
    private final OnRemoveItemClickListener removeListener;


    public MainFavoriteLocationListAdapter(Context context, List<FavoriteLocation> favoriteLocations, OnEditItemClickListener editListener
            , OnRemoveItemClickListener removeListener) {
        this.context = context;
        this.favoriteLocations = favoriteLocations;
        this.editListener = editListener;
        this.removeListener = removeListener;
    }

    public interface OnEditItemClickListener {
        void onEditItemClick(String identification);
    }

    public interface OnRemoveItemClickListener {
        void onRemoveItemClick(String identification);
    }

    @NonNull
    @Override
    public MainFavoriteLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main_favorite_location_list, parent, false);
        return new MainFavoriteLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFavoriteLocationViewHolder holder, int position) {
        final FavoriteLocation favoriteLocation = favoriteLocations.get(position);


        holder.name.setText(favoriteLocation.getName());
        holder.district.setText(favoriteLocation.getDistrict());


        // to define listener on buttons
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editListener.onEditItemClick(favoriteLocation.getIdentification());
            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeListener.onRemoveItemClick(favoriteLocation.getIdentification());
            }
        });

    }

    @Override
    public int getItemCount() {
        return favoriteLocations.size();
    }

    class MainFavoriteLocationViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView district;
        private TextView editButton;
        private TextView removeButton;

        MainFavoriteLocationViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            district = itemView.findViewById(R.id.district);
            editButton = itemView.findViewById(R.id.edit_button);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }

}
