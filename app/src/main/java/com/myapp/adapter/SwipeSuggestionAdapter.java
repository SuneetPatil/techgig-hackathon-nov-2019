package com.myapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.R;
import com.myapp.model.Suggestions;
import com.myapp.model.SwipeSuggestion;
import com.squareup.picasso.Picasso;

import java.util.List;

// Adapter for swipe cards
public class SwipeSuggestionAdapter extends RecyclerView.Adapter<SwipeSuggestionAdapter.SwipeSuggestionViewHolder> {

    private List<SwipeSuggestion> suggestionList;

    // Define listener member variable
    private SwipeSuggestionAdapter.OnItemClickListener mListener;

    public SwipeSuggestionAdapter(List<SwipeSuggestion> suggestionList) {
        this.suggestionList = suggestionList;
    }

    @Override
    public SwipeSuggestionAdapter.SwipeSuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_text_image_view, null);
        SwipeSuggestionAdapter.SwipeSuggestionViewHolder SwipeSuggestionViewHolder = new SwipeSuggestionAdapter.SwipeSuggestionViewHolder(layoutView, mListener);
        return SwipeSuggestionViewHolder;
    }

    @Override
    public void onBindViewHolder(final SwipeSuggestionAdapter.SwipeSuggestionViewHolder holder, final int position) {
        SwipeSuggestion suggestions = suggestionList.get(position);
        holder.swipeSuggestionTxtView.setText(suggestions.getSuggestion());
        holder.swipe_description_text_view.setText(suggestions.getDescription());
        Picasso.get().load(suggestions.getUrl()).into(holder.swipeSuggestionImageView);
    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(SwipeSuggestionAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public static class SwipeSuggestionViewHolder extends RecyclerView.ViewHolder {
        public TextView swipeSuggestionTxtView, swipe_description_text_view;
        public ImageView swipeSuggestionImageView;

        public SwipeSuggestionViewHolder(View view, final SwipeSuggestionAdapter.OnItemClickListener listener) {
            super(view);
            swipeSuggestionTxtView = view.findViewById(R.id.swipe_suggestion_text_view);
            swipeSuggestionImageView = view.findViewById(R.id.swipe_suggestion_image_view);
            swipe_description_text_view = view.findViewById(R.id.swipe_description_text_view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}

