package com.example.bts.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bts.R;
import com.example.bts.model.ChatMessageModel;
import com.example.bts.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        Log.i("onBindViewHolder", "Binding view holder at position: " + position);

        try {
            String senderId = model.getSenderId();
            String currentUserId = FirebaseUtil.currentUserId();

            if (senderId != null && currentUserId != null) {
                if (senderId.equals(currentUserId)) {
                    holder.leftChatLayout.setVisibility(View.GONE);
                    holder.rightChatLayout.setVisibility(View.VISIBLE);
                    holder.rightChatTextview.setText(model.getMessage());
                } else {
                    holder.rightChatLayout.setVisibility(View.GONE);
                    holder.leftChatLayout.setVisibility(View.VISIBLE);
                    holder.leftChatTextview.setText(model.getMessage());
                }
            } else {
                // Handle the case where senderId or currentUserId is null
                Log.e("onBindViewHolder", "Sender ID or Current User ID is null");
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.GONE);
                // Optionally, you can set a default message or handle this scenario as per your requirement
            }
        } catch (Exception e) {
            Log.e("onBindViewHolder", "Error binding view holder", e);
            // Handle the error, for instance, you can show an error message to the user
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.GONE);
        }
    }


    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
        }
    }
}