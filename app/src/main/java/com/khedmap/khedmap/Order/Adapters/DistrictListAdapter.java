package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.District;
import com.khedmap.khedmap.Order.DataModels.Zone;
import com.khedmap.khedmap.R;

import java.util.List;


public class DistrictListAdapter extends RecyclerView.Adapter<DistrictListAdapter.DistrictViewHolder> {

    private Context context;
    private List<District> districts;
    private final OnItemClickListener listener;


    public DistrictListAdapter(Context context, List<District> districts, OnItemClickListener listener) {
        this.context = context;
        this.districts = districts;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(District item);
    }

    @NonNull
    @Override
    public DistrictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_district_list, parent, false);
        return new DistrictViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DistrictViewHolder holder, int position) {
        final District district = districts.get(position);

        holder.districtName.setText(district.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(district);

            }
        });
    }

    @Override
    public int getItemCount() {
        return districts.size();
    }

    class DistrictViewHolder extends RecyclerView.ViewHolder {
        private TextView districtName;

        DistrictViewHolder(View itemView) {
            super(itemView);
            districtName = itemView.findViewById(R.id.district_name);
        }
    }

}
