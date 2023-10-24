package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.BottomSheetCategory;
import com.khedmap.khedmap.R;

import java.util.List;

public class BottomSheetCategoryListAdapter extends RecyclerView.Adapter<BottomSheetCategoryListAdapter.BottomSheetCategoryViewHolder> {

    private Context context;
    private List<BottomSheetCategory> bottomSheetCategories;
    private final OnItemClickListener listener;

    private int selectedItem;

    public BottomSheetCategoryListAdapter(Context context, List<BottomSheetCategory> bottomSheetCategories, OnItemClickListener listener, int selectedItem) {
        this.context = context;
        this.bottomSheetCategories = bottomSheetCategories;
        this.listener = listener;

        this.selectedItem = selectedItem;
    }

    public interface OnItemClickListener {
        boolean onItemClick(BottomSheetCategory item);
    }

    @NonNull
    @Override
    public BottomSheetCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bottomsheet_category_list, parent, false);
        return new BottomSheetCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetCategoryViewHolder holder, final int position) {
        final BottomSheetCategory bottomSheetCategory = bottomSheetCategories.get(position);

        holder.name.setText(bottomSheetCategory.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isResponseReceived = listener.onItemClick(bottomSheetCategory);

                if (isResponseReceived) {
                    selectedItem = position;
                    notifyDataSetChanged();
                }

            }
        });

        if (selectedItem == position) {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.round_button_without_shadow_selected));
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.selectedButtonTextColor));
        } else {
            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.round_button_without_shadow));
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.noSelectedButtonTextColor));
        }
    }

    @Override
    public int getItemCount() {
        return bottomSheetCategories.size();
    }

    class BottomSheetCategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        BottomSheetCategoryViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_title);
        }
    }

}
