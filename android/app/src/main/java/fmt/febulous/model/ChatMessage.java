package fmt.febulous.model;

import java.io.Serializable;


public class ChatMessage implements Serializable {


    String message_id, message, createdAt;

    ChatUser chatUser;

    public ChatMessage() {}

    public String getId() {
        return message_id;
    }

    public void setId(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ChatUser getChatUser() {
        return chatUser;
    }

    public void setChatUser(ChatUser chatUser) {
        this.chatUser = chatUser;
    }

}