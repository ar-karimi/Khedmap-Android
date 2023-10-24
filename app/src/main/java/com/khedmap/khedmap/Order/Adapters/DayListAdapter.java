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

import com.khedmap.khedmap.Order.DataModels.Day;
import com.khedmap.khedmap.R;

import java.util.List;


public class DayListAdapter extends RecyclerView.Adapter<DayListAdapter.DayViewHolder> {

    private Context context;
    private List<Day> days;
    private final OnItemClickListener listener;

    private int selectedItem;

    public DayListAdapter(Context context, List<Day> days, OnItemClickListener listener, int selectedItem) {
        this.context = context;
        this.days = days;
        this.listener = listener;

        this.selectedItem = selectedItem;
    }

    public interface OnItemClickListener {
        void onItemClick(int selectedItemPosition);
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day_list, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DayViewHolder holder, final int position) {
        final Day day = days.get(position);

        holder.name.setText(day.getName());
        holder.number.setText(day.getNumber());
        holder.month.setText(day.getMonth());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedItem = position;
                notifyDataSetChanged();

                listener.onItemClick(selectedItem);
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
        return days.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView number;
        private TextView month;
        private LinearLayout linearLayout;

        DayViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            month = itemView.findViewById(R.id.month);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }

}
