package fmt.febulous.model;

import java.io.Serializable;


public class ChatUser implements Serializable {


    String user_id, username, userimage;


    public ChatUser(String user_id, String username, String userimage) {

        this.user_id = user_id;
        this.username = username;
        this.userimage = userimage;

    }

    public String getId() {
        return user_id;
    }

    public String getUser_name() {
        return username;
    }

    public String getUser_image() {
        return userimage;
    }

}