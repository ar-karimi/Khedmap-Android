package com.khedmap.khedmap.Order.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khedmap.khedmap.Order.DataModels.SearchResult;
import com.khedmap.khedmap.R;

import java.util.List;


public class SearchResultListAdapter extends RecyclerView.Adapter<SearchResultListAdapter.SearchResultViewHolder> {

    private Context context;
    private List<SearchResult> searchResults;
    private final OnItemClickListener listener;


    public SearchResultListAdapter(Context context, List<SearchResult> searchResults, OnItemClickListener listener) {
        this.context = context;
        this.searchResults = searchResults;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(SearchResult item);
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchResultViewHolder holder, int position) {
        final SearchResult searchResult = searchResults.get(position);


        holder.name.setText(searchResult.getName());
        holder.icon.setImageResource(searchResult.getIconRes());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onItemClick(searchResult);

            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView icon;

        SearchResultViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.result_name);
            icon = itemView.findViewById(R.id.result_icon);
        }
    }

}
