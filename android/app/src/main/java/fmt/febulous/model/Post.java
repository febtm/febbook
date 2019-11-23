package fmt.febulous.model;

import java.io.Serializable;


public class Post implements Serializable {


    static int total, temp_total;


    String id;
    String image;
    String title;
    String description;
    String type;
    String timestamp;
    String username;
    String userimage;
    String usercourse;
    String usertype;


    String temp_id;
    String temp_image;
    String temp_title;
    String temp_description;
    String temp_type;
    String temp_timestamp;
    String temp_username;
    String temp_userimage;
    String temp_usercourse;
    String temp_usertype;



    public int getTotal(){return total;}

    public void setTotal(int total){this.total = total;}


    public String getId(){return id;}


    public void setDetails(String id, String image, String title, String description,
                           String type, String timestamp, String username,
                           String userimage, String usercourse, String usertype){

        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.type = type;
        this.timestamp = timestamp;
        this.username = username;
        this.userimage = userimage;
        this.usercourse = usercourse;
        this.usertype = usertype;

    }



    public int getTempTotal(){return temp_total;}

    public void setTempTotal(int temp_total){this.temp_total = temp_total;}


    public String getTempId(){return temp_id;}

    public String getTempImage(){return temp_image;}

    public String getTempTitle(){return temp_title;}

    public String getTempDescription(){return temp_description;}

    public String getTempType(){return temp_type;}

    public String getTempTimestamp(){return temp_timestamp;}

    public String getTempUsername(){return temp_username;}

    public String getTempUserimage(){return temp_userimage;}

    public String getTempUsercourse(){return temp_usercourse;}

    public String getTempUsertype(){return temp_usertype;}


    public void setTempDetails(String temp_id, String temp_image, String temp_title, String temp_description,
                           String temp_type, String temp_timestamp, String temp_username,
                           String temp_userimage, String temp_usercourse, String temp_usertype){

        this.temp_id = temp_id;
        this.temp_image = temp_image;
        this.temp_title = temp_title;
        this.temp_description = temp_description;
        this.temp_type = temp_type;
        this.temp_timestamp = temp_timestamp;
        this.temp_username = temp_username;
        this.temp_userimage = temp_userimage;
        this.temp_usercourse = temp_usercourse;
        this.temp_usertype = temp_usertype;

    }

}