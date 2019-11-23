package fmt.febulous.model;


public class ProfileUser {


    String name, image, type;

    static int total;


    public int getTotal(){return total;}

    public void setTotal(int total){this.total = total;}


    public String getName(){return name;}

    public String getImage(){return image;}

    public String getType(){return type;}


    public void setDetails(String name, String image, String type){

        this.name = name;
        this.image = image;
        this.type = type;

    }

}