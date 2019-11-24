package com.myapp.adapter;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myapp.R;
import com.myapp.model.Chat;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.google.common.io.ByteStreams.copy;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Chat> chatList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sendTextView, sendTimeTextView;
        LinearLayout linearLayout, parentLayout;
        ImageView sentImage;

        public MyViewHolder(View view) {
            super(view);

            sendTextView = view.findViewById(R.id.sentMsg);
            sentImage = view.findViewById(R.id.leximageView);
            sendTimeTextView = view.findViewById(R.id.sentTime);
            linearLayout = view.findViewById(R.id.linear_layout);
            parentLayout = view.findViewById(R.id.parent_layout);
        }
    }


    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_chat_sent_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        if (chat.isImage()){//To support Image
            holder.sendTextView.setVisibility(View.GONE);
            holder.sentImage.setVisibility(View.VISIBLE);
            Picasso.get().load("Image path").into(holder.sentImage);
            holder.linearLayout.setBackgroundResource(R.drawable.conversational_bot_text_bubble_tx);
            holder.parentLayout.setGravity(Gravity.END);
            holder.linearLayout.setPadding(20, 10, 70, 10);
            holder.sendTimeTextView.setText(chat.getTime());

        }
       else {
            holder.sendTextView.setVisibility(View.VISIBLE);
            holder.sentImage.setVisibility(View.GONE);
            if (chat.isSent()) {
                //sent msg
                holder.sendTextView.setText(chat.getSentMsg());
                holder.linearLayout.setBackgroundResource(R.drawable.conversational_bot_text_bubble_tx);
                holder.parentLayout.setGravity(Gravity.END);
                holder.linearLayout.setPadding(20, 10, 70, 10);
                holder.sendTimeTextView.setText(chat.getTime());

            } else {
                //received msg
                holder.sendTextView.setText(chat.getSentMsg());
                holder.linearLayout.setBackgroundResource(R.drawable.conversational_bot_text_bubble_rx);
                holder.linearLayout.setPadding(70, 10, 40, 10);
                holder.parentLayout.setGravity(Gravity.START);
                holder.sendTimeTextView.setText(chat.getTime());

            }

        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}