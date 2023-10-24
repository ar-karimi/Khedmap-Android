package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.Teammate;
import com.khedmap.khedmap.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.rmiri.skeleton.SkeletonGroup;


public class TeammateListAdapter extends RecyclerView.Adapter<TeammateListAdapter.TeammateViewHolder> {

    private Context context;
    private List<Teammate> teammates;


    public TeammateListAdapter(Context context, List<Teammate> teammates) {
        this.context = context;
        this.teammates = teammates;
    }

    @NonNull
    @Override
    public TeammateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_teammate_list, parent, false);
        return new TeammateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TeammateViewHolder holder, int position) {
        final Teammate teammate = teammates.get(position);

        Picasso.get().load(teammate.getAvatar()).into(holder.avatar, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {

                holder.skeletonGroup.setShowSkeleton(false);
                holder.skeletonGroup.finishAnimation();
            }
        });


        holder.name.setText(teammate.getName());
    }

    @Override
    public int getItemCount() {
        return teammates.size();
    }

    class TeammateViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private SkeletonGroup skeletonGroup;

        TeammateViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.teammate_avatar);
            name = itemView.findViewById(R.id.teammate_name);
            skeletonGroup = itemView.findViewById(R.id.skeletonGroup);
        }
    }

}
