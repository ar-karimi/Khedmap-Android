package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.Disproof;
import com.khedmap.khedmap.R;

import java.util.List;


public class DisproofListAdapter extends RecyclerView.Adapter<DisproofListAdapter.DisproofViewHolder> {

    private Context context;
    private List<Disproof> disproofs;
    private final OnItemClickListener listener;


    public DisproofListAdapter(Context context, List<Disproof> disproofs, OnItemClickListener listener) {
        this.context = context;
        this.disproofs = disproofs;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Disproof item);
    }

    @NonNull
    @Override
    public DisproofViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_disproof_list, parent, false);
        return new DisproofViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DisproofViewHolder holder, int position) {
        final Disproof disproof = disproofs.get(position);

        holder.title.setText(disproof.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(disproof);

            }
        });
    }

    @Override
    public int getItemCount() {
        return disproofs.size();
    }

    class DisproofViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        DisproofViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }

}
