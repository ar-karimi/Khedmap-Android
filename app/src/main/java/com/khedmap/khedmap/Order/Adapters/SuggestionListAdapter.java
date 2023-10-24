package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.Suggestion;
import com.khedmap.khedmap.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.rmiri.skeleton.SkeletonGroup;


public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListAdapter.SuggestionViewHolder> {

    private Context context;
    private List<Suggestion> suggestions;
    private final OnItemClickListener listener;


    public SuggestionListAdapter(Context context, List<Suggestion> suggestions, OnItemClickListener listener) {
        this.context = context;
        this.suggestions = suggestions;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Suggestion item);
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggestion_list, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SuggestionViewHolder holder, int position) {
        final Suggestion suggestion = suggestions.get(position);

        Picasso.get().load(suggestion.getExpertAvatar()).into(holder.expertAvatar, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {

                holder.skeletonGroup.setShowSkeleton(false);
                holder.skeletonGroup.finishAnimation();
            }
        });


        holder.expertName.setText(suggestion.getExpertName());
        holder.basePrice.setText(String.valueOf(suggestion.getBasePrice()));


        if (suggestion.getExpertRate() == (int) suggestion.getExpertRate()) //to not show .0
            holder.expertRate.setText(String.valueOf((int) suggestion.getExpertRate()));
        else
            holder.expertRate.setText(String.valueOf(suggestion.getExpertRate()));


        if (!suggestion.isAds() && !suggestion.isFavorite())
            holder.specificPropertiesLayout.setVisibility(View.GONE);
        else if (!suggestion.isAds())
            holder.isAds.setVisibility(View.GONE);
        else if (!suggestion.isFavorite())
            holder.isFavorite.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(suggestion);

            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView expertAvatar;
        private SkeletonGroup skeletonGroup;
        private TextView expertName;
        private TextView basePrice;
        private TextView expertRate;
        private LinearLayout specificPropertiesLayout;
        private TextView isAds;
        private ImageView isFavorite;


        SuggestionViewHolder(View itemView) {
            super(itemView);
            expertAvatar = itemView.findViewById(R.id.expert_avatar);
            skeletonGroup = itemView.findViewById(R.id.skeletonGroup);
            expertName = itemView.findViewById(R.id.expert_name);
            basePrice = itemView.findViewById(R.id.base_price);
            expertRate = itemView.findViewById(R.id.expert_rate);
            specificPropertiesLayout = itemView.findViewById(R.id.specific_properties_layout);
            isAds = itemView.findViewById(R.id.is_ads);
            isFavorite = itemView.findViewById(R.id.is_favorite);

        }
    }

}
