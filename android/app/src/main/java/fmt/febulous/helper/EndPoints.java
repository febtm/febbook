package fmt.febulous.helper;


public class EndPoints {


    //public static final String BASE_URL = "http://10.0.3.2/febulous/";
    public static final String BASE_URL = "http://www.febulous.org/febulous/";

    public static final String ADD_CHAT = BASE_URL + "addChat.php";
    public static final String BASIC_FUNCTIONS = BASE_URL + "basicFunctions.php";
    public static final String DELETE_CHAT = BASE_URL + "deleteChat.php";
    public static final String DELETE_NOTIFICATION = BASE_URL + "deleteNotification.php";
    public static final String FORGOT_PASSWORD = BASE_URL + "forgotPassword.php?email=";
    public static final String GET_ALL_NOTIFICATIONS = BASE_URL + "getAllNotifications.php?current_user_id=";
    public static final String GET_ALL_POSTS = BASE_URL + "getAllPosts.php?index=";
    public static final String GET_ALL_PROFILES = BASE_URL + "getAllProfiles.php";
    public static final String GET_COMMENTS = BASE_URL + "getComment.php?id=";
    public static final String GET_COUNT = BASE_URL + "getCount.php?user_id=";
    public static final String GET_CURRENT_USER = BASE_URL + "getCurrentUser.php?email=";
    public static final String GET_DISLIKES = BASE_URL + "getDislike.php?id=";
    public static final String GET_LIKES = BASE_URL + "getLike.php?id=";
    public static final String GET_POST_ID = BASE_URL + "getPostId.php?post_id=";
    public static final String GET_POST_PROFILE = BASE_URL + "getPostProfile.php?post_user_name=";
    public static final String GET_POST_TYPE = BASE_URL + "getPostType.php?post_type=";
    public static final String LOGIN = BASE_URL + "login.php";
    public static final String LOGOUT = BASE_URL + "logout.php";
    public static final String MODIFY_PROFILE = BASE_URL + "modifyProfile.php";
    public static final String MY_PROFILE = BASE_URL + "myProfile.php?username=";
    public static final String POST = BASE_URL + "post.php";
    public static final String SIGNIN = BASE_URL + "signin.php";
    public static final String STUDY_MATERIALS = BASE_URL + "studyMaterials.php";

    public static final String FILE_URL = BASE_URL + "StudyMaterials/";

    public static final String CHAT_BASE_URL = BASE_URL + "messenger/v1/";

    public static final String CHAT_LOGIN = CHAT_BASE_URL + "user/login";
    public static final String CHAT_ROOMS = CHAT_BASE_URL + "chat_rooms";
    public static final String CHAT_ROOM = CHAT_BASE_URL + "chat_room/_ID_";
    public static final String CHAT_ROOM_MESSAGE = CHAT_BASE_URL + "chat_room/_ID_/message";


}