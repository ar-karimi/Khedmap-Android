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

import com.khedmap.khedmap.Order.DataModels.Hour;
import com.khedmap.khedmap.R;

import java.util.List;


public class HourListAdapter extends RecyclerView.Adapter<HourListAdapter.HourViewHolder> {

    private Context context;
    private List<Hour> hours;
    private final OnItemClickListener listener;

    private int selectedItem;

    public HourListAdapter(Context context, List<Hour> hours, OnItemClickListener listener, int selectedItem) {
        this.context = context;
        this.hours = hours;
        this.listener = listener;

        this.selectedItem = selectedItem;
    }

    public interface OnItemClickListener {
        void onItemClick(String selectedItemPosition);
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hour_list, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HourViewHolder holder, final int position) {
        final Hour hour = hours.get(position);

        holder.start.setText(hour.getStart());
        holder.end.setText(hour.getEnd());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedItem = position;
                notifyDataSetChanged();

                listener.onItemClick(hour.getStart()); //return start of range
            }
        });

        if (selectedItem == position) {
            holder.linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bordered_selected_date_background));
        } else {
            holder.linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bordered_date_background));
        }
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    class HourViewHolder extends RecyclerView.ViewHolder {
        private TextView start;
        private TextView end;
        private LinearLayout linearLayout;

        HourViewHolder(View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }

}
