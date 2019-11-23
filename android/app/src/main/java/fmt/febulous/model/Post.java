package fmt.febulous.model;

import java.io.Serializable;


public class Post implements Serializable {


    private static int total;

    private String id, image, title, description, type, timestamp, username, userimage;


    public int getTotal(){return total;}

    public void setTotal(int total){Post.total = total;}


    public String getId(){return id;}

    public String getImage(){return image;}

    public String getTitle(){return title;}

    public String getDescription(){return description;}

    public String getType(){return type;}

    public String getTimestamp(){return timestamp;}

    public String getUsername(){return username;}

    public String getUserimage(){return userimage;}


    public void setDetails(String id, String image, String title, String description,
                           String type, String timestamp, String username, String userimage){

        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.type = type;
        this.timestamp = timestamp;
        this.username = username;
        this.userimage = userimage;

    }

}