package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.Zone;
import com.khedmap.khedmap.R;

import java.util.List;


public class ZoneListAdapter extends RecyclerView.Adapter<ZoneListAdapter.ZoneViewHolder> {

    private Context context;
    private List<Zone> zones;
    private final OnItemClickListener listener;


    public ZoneListAdapter(Context context, List<Zone> zones, OnItemClickListener listener) {
        this.context = context;
        this.zones = zones;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Zone item);
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_zone_list, parent, false);
        return new ZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ZoneViewHolder holder, int position) {
        final Zone zone = zones.get(position);

        holder.zoneNumber.setText(zone.getServerId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(zone);

            }
        });
    }

    @Override
    public int getItemCount() {
        return zones.size();
    }

    class ZoneViewHolder extends RecyclerView.ViewHolder {
        private TextView zoneNumber;

        ZoneViewHolder(View itemView) {
            super(itemView);
            zoneNumber = itemView.findViewById(R.id.zone_number);
        }
    }

}
