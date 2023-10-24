package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.Order;
import com.khedmap.khedmap.R;

import java.util.List;


public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private final OnItemClickListener listener;


    public OrderListAdapter(Context context, List<Order> orders, OnItemClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Order item);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_list, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, int position) {
        final Order order = orders.get(position);

        holder.subcategory.setText(order.getSubcategory());
        holder.create.setText(order.getCreate());
        holder.serviceTime.setText(order.getServiceTime());
        holder.address.setText(order.getAddress());
        holder.numberOfSuggestions.setText(order.getNumberOfSuggestions());
        holder.status.setText(order.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(order);

            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView subcategory;
        private TextView create;
        private TextView serviceTime;
        private TextView address;
        private TextView numberOfSuggestions;
        private TextView status;

        OrderViewHolder(View itemView) {
            super(itemView);
            subcategory = itemView.findViewById(R.id.subcategory);
            create = itemView.findViewById(R.id.create);
            serviceTime = itemView.findViewById(R.id.service_time);
            address = itemView.findViewById(R.id.address);
            numberOfSuggestions = itemView.findViewById(R.id.number_of_suggestions);
            status = itemView.findViewById(R.id.status);
        }
    }

}
