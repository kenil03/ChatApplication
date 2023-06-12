package com.adaarajkaip.chattingapplicationdemo.Adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adaarajkaip.chattingapplicationdemo.Models.ChatMessage;
import com.adaarajkaip.chattingapplicationdemo.databinding.ItemContainerReceivedMessageBinding;
import com.adaarajkaip.chattingapplicationdemo.databinding.ItemContainerSentMessageBinding;
import com.google.android.gms.dynamic.IFragmentWrapper;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessage;
    private final Bitmap receiverProfileImage;
    private final String senderId;

    public ChatAdapter(List<ChatMessage> chatMessage, Bitmap receiverProfileImage, String senderId) {
        this.chatMessage = chatMessage;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessage.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public static final int VIEW_TYPE_SENT = 0;
    public static final int VIEW_TYPE_RECEIVED = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_RECEIVED) {
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new SendMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).setData(chatMessage.get(position), receiverProfileImage);
        } else {
            ((SendMessageViewHolder) holder).setData(chatMessage.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    static class SendMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SendMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.sendMessages.setText(chatMessage.message);
            binding.sendTime.setText(chatMessage.dataTime);
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverImage) {
            binding.receiverMessage.setText(chatMessage.message);
            binding.receiverTime.setText(chatMessage.dataTime);
            binding.receiverImage.setImageBitmap(receiverImage);
        }
    }


}
