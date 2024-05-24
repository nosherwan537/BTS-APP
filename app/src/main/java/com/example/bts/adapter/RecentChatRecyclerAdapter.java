package com.example.bts.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bts.ChatModule.Chat;
import com.example.bts.R;
import com.example.bts.model.ChatroomModel;
import com.example.bts.model.UserModel;
import com.example.bts.utils.AndroidUtil;
import com.example.bts.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        try {
            FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                            if (otherUserModel != null) {
                                boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                                // Ensure otherUserModel.getUserId() is not null
                                String otherUserId = otherUserModel.getUserId();
                                if (otherUserId != null) {
                                    FirebaseUtil.getOtherProfilePicStorageRef(otherUserId).getDownloadUrl()
                                            .addOnCompleteListener(t -> {
                                                if (t.isSuccessful()) {
                                                    Uri uri = t.getResult();
                                                    AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                                } else {
                                                    Log.e("RecentChatAdapter", "Failed to get profile picture URL", t.getException());
                                                }
                                            });

                                    holder.usernameText.setText(otherUserModel.getUsername());
                                    if (lastMessageSentByMe)
                                        holder.lastMessageText.setText("You : " + model.getLastMessage());
                                    else
                                        holder.lastMessageText.setText(model.getLastMessage());
                                    holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                                    holder.itemView.setOnClickListener(v -> {
                                        // Navigate to chat activity
                                        Intent intent = new Intent(context, Chat.class);
                                        AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    });
                                } else {
                                    Log.e("RecentChatAdapter", "otherUserModel.getUserId() is null");
                                    setDefaultView(holder);
                                }
                            } else {
                                Log.e("RecentChatAdapter", "otherUserModel is null");
                                setDefaultView(holder);
                            }
                        } else {
                            Log.e("RecentChatAdapter", "Failed to get other user: ", task.getException());
                            setDefaultView(holder);
                        }
                    });
        } catch (Exception e) {
            Log.e("RecentChatAdapter", "Error binding view holder", e);
            // Handle the error gracefully, possibly hiding the view or showing a default state
            setDefaultView(holder);
        }
    }
    private void setDefaultView(ChatroomModelViewHolder holder) {
        holder.usernameText.setText(R.string.default_name);
        holder.lastMessageText.setText(R.string.default_message);
        holder.lastMessageTime.setText("");
        holder.profilePic.setImageResource(R.drawable.person_icon);
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}