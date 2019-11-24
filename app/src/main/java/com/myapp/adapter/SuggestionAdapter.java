package com.myapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myapp.R;
import com.myapp.model.Suggestions;

import java.util.List;


public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private List<Suggestions> suggestionList;

    // Define listener member variable
    private OnItemClickListener mListener;

    public SuggestionAdapter(List<Suggestions> suggestionList) {
        this.suggestionList = suggestionList;
    }

    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_textview_layout, null);
        SuggestionViewHolder suggestionViewHolder = new SuggestionViewHolder(layoutView, mListener);
        return suggestionViewHolder;
    }

    @Override
    public void onBindViewHolder(final SuggestionViewHolder holder, final int position) {
        Suggestions suggestions = suggestionList.get(position);
        holder.suggestionTxtView.setText(suggestions.getSuggestion());
    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        public TextView suggestionTxtView;

        public SuggestionViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            suggestionTxtView = (TextView) view.findViewById(R.id.suggestion_text_view);
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

