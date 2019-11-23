package fmt.febulous.model;


public class Notification {

    private static int total;

    private String id, chat_id, post_userread, notify_userid, notify_username, notify_userimage, timestamp;


    public int getTotal(){return total;}

    public void setTotal(int total){this.total = total;}


    public String getId(){return id;}

    public String getChat_id(){return chat_id;}

    public String getPost_userread(){return post_userread;}

    public String getNotify_userid(){return notify_userid;}

    public String getNotify_username(){return notify_username;}

    public String getNotify_userimage(){return notify_userimage;}

    public String getTimestamp(){return timestamp;}


    public void setNotDetails(String id, String chat_id, String post_userread, String notify_userid, String notify_username,
                              String notify_userimage, String timestamp)
    {

        this.id = id;
        this.chat_id = chat_id;
        this.post_userread = post_userread;
        this.notify_userid = notify_userid;
        this.notify_username = notify_username;
        this.notify_userimage = notify_userimage;
        this.timestamp = timestamp;

    }

    public void deleteNot(){

        this.id = null;
        this.chat_id = null;
        this.post_userread = null;
        this.notify_userid = null;
        this.notify_username = null;
        this.notify_userimage = null;
        this.timestamp = null;

        this.setTotal(this.getTotal() - 1);

    }

}