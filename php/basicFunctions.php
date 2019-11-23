<?php 

require_once('init.php');

$method = $_POST["method"];
$id = $_POST["id"];
$comment = $_POST["comment"];
$user_id = $_POST["user_id"];

$methodname1 = "comment";
$methodname2 = "like";
$methodname3 = "dislike";
$methodname4 = "notification";
$methodname5 = "readnotification";
$methodname6 = "readchat";
$methodname7 = "deletepost";
$methodname8 = "deletecomment";
$methodname9 = "reportpost";
$methodname10 = "reportcomment";
$methodname11 = "reportuser";


$sql_report = "SELECT * FROM report WHERE post_id = '$id' AND post_title_comment = '$comment' AND report_user_id = '$user_id'";

$res_report = mysqli_query($con, $sql_report);


require_once('fcmNotPush.php');

require_once('fcmNotFirebase.php');


$push = null; 

$sql_12 = "SELECT username FROM profile WHERE userid = $user_id";

$res_12 = mysqli_query($con, $sql_12);

$row_12 = mysqli_fetch_array($res_12);


$devicetoken = array();
	

if($method === $methodname1)
{

$sql_1 ="INSERT INTO comments (post_id, comment, comment_user_id) VALUES('$id','".addslashes($comment)."','$user_id');";

if(mysqli_query($con,$sql_1)){

$sql_1_1 = "SELECT user_id FROM posts WHERE post_id = '$id'";

$res_1_1 = mysqli_query($con, $sql_1_1);

$row_1_1 = mysqli_fetch_array($res_1_1);

$post_user_id = $row_1_1['user_id'];

if($post_user_id != $user_id){

$sql_1_2 = "INSERT INTO notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('$id', '99999999', '$post_user_id', '$user_id')";

if(mysqli_query($con,$sql_1_2)){


$push = new Push("Febulous", $row_12['username']. " has commented on your Post !", null);
	 
$mPushNotification = $push->getPush();


$sql_13 = "SELECT device_token FROM PROFILE WHERE userid = '$post_user_id'";

$res_13 = mysqli_query($con,$sql_13);

$row_13 = mysqli_fetch_array($res_13);

array_push($devicetoken, $row_13['device_token']);

	 
$firebase = new Firebase(); 
	 
$firebase->send($devicetoken, $mPushNotification);
	

echo "Your Comment has been posted !";
}

else
echo "Posting Comment Failed !";
}

else
echo "Your Comment has been posted !";
}

else
echo "Posting Comment Failed !";
}


else if($method === $methodname2)
{

$sql_2 = "INSERT INTO likes (post_id, like_user_id) VALUES ('$id', '$user_id');";

if(mysqli_query($con,$sql_2)){

$sql_2_1 = "SELECT user_id FROM posts WHERE post_id = '$id'";

$res_2_1 = mysqli_query($con, $sql_2_1);

$row_2_1 = mysqli_fetch_array($res_2_1);

$post_user_id = $row_2_1['user_id'];

if($post_user_id != $user_id){

$sql_2_2 = "INSERT INTO notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('$id', '88888888', '$post_user_id', '$user_id')";

if(mysqli_query($con,$sql_2_2)){


$push = new Push("Febulous", $row_12['username']. " has liked your Post !", null);
	 
$mPushNotification = $push->getPush();


$sql_13 = "SELECT device_token FROM PROFILE WHERE userid = '$post_user_id'";

$res_13 = mysqli_query($con,$sql_13);

$row_13 = mysqli_fetch_array($res_13);

array_push($devicetoken, $row_13['device_token']);

	 
$firebase = new Firebase(); 
	 
$firebase->send($devicetoken, $mPushNotification);
	

echo "You have liked this post !";
}

else
echo "Liking Failed !";
}

else
echo "You have liked this post !";
}

else
echo "Liking Failed !";
}


else if($method === $methodname3)
{

$sql_3 ="INSERT INTO dislikes(post_id, dislike_user_id) VALUES ('$id', '$user_id');";

if(mysqli_query($con,$sql_3)){

$sql_3_1 = "SELECT user_id FROM posts WHERE post_id = '$id'";

$res_3_1 = mysqli_query($con, $sql_3_1);

$row_3_1 = mysqli_fetch_array($res_3_1);

$post_user_id = $row_3_1['user_id'];

if($post_user_id != $user_id){

$sql_3_2 = "INSERT INTO notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('$id', '77777777', '$post_user_id', '$user_id')";

if(mysqli_query($con,$sql_3_2)){

	
$push = new Push("Febulous", $row_12['username']. " has disliked your Post !", null);
	 
$mPushNotification = $push->getPush();


$sql_13 = "SELECT device_token FROM PROFILE WHERE userid = '$post_user_id'";

$res_13 = mysqli_query($con,$sql_13);

$row_13 = mysqli_fetch_array($res_13);

array_push($devicetoken, $row_13['device_token']);

	 
$firebase = new Firebase(); 
	 
$firebase->send($devicetoken, $mPushNotification);
	

echo "You have disliked this post !";
}

else
echo "Disliking Failed !";
}

else
echo "You have disliked this post !";
}

else
echo "Disliking Failed !";
}


else if($method === $methodname4)
{

$sql_4 = "INSERT INTO notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('0', '$id', '$comment', '$user_id')";

if(mysqli_query($con,$sql_4))
echo "Notification sent successfully !";

else
echo "Notification Failed !";

}


else if($method === $methodname5)
{

if(!($comment === $user_id)){

$sql_5 = "UPDATE notifications SET post_user_read = 1 WHERE post_user_id = '$user_id' AND post_id = '$id' AND notification_user_id = '$comment';";

if(mysqli_query($con, $sql_5))
echo "Success !";
}

}


else if($method === $methodname6)
{

$sql_6 = "SELECT * FROM chat_rooms WHERE user1_id = '$user_id' AND chat_room_id = '$id'";

$res_6 = mysqli_query($con, $sql_6);


$sql_6_1 = "SELECT * FROM chat_rooms WHERE user2_id = '$user_id' AND chat_room_id = '$id'";

$res_6_1 = mysqli_query($con, $sql_6_1);

if(mysqli_num_rows($res_6)>0)
$sql_6_2 = "UPDATE chat_rooms SET user1_read = 1 WHERE chat_room_id = '$id';";

else if(mysqli_num_rows($res_6_1)>0)
$sql_6_2 = "UPDATE chat_rooms SET user2_read = 1 WHERE chat_room_id = '$id';";

else
$sql_6_2 = "SELECT * FROM likes";


if(mysqli_query($con, $sql_6_2))
echo "Success !";

}


else if($method === $methodname7)
{

$sql_7 = "SELECT post_type FROM posts WHERE post_id = '$id' AND user_id = '$user_id'";

$res_7 = mysqli_query($con, $sql_7);

$row_7 = mysqli_fetch_array($res_7);

$post_type = $row_7['post_type'];

if($post_type === "Materials"){
	
$sql_7_1 = "SELECT post_description FROM posts WHERE post_id = '$id' AND user_id = '$user_id'";

$res_7_1 = mysqli_query($con, $sql_7_1);

$row_7_1 = mysqli_fetch_array($res_7_1);

$post_description = $row_7_1['post_description'];

unlink("StudyMaterials/$post_description");
	
}


$sql_7_2 = "DELETE FROM posts WHERE post_id = '$id' AND user_id = '$user_id'";

$sql_7_3 = "DELETE FROM notifications WHERE post_id = '$id'";

$sql_7_4 = "DELETE FROM likes WHERE post_id = '$id'";

$sql_7_5 = "DELETE FROM dislikes WHERE post_id = '$id'";

$sql_7_6 = "DELETE FROM comments WHERE post_id = '$id'";


if(mysqli_query($con,$sql_7_2) && mysqli_query($con,$sql_7_3) && mysqli_query($con,$sql_7_4) && mysqli_query($con,$sql_7_5) && mysqli_query($con,$sql_7_6))
echo "Your Post has been deleted !";

else
echo "Deletion Failed !";

}


else if($method === $methodname8)
{

$sql_8 = "DELETE FROM comments WHERE post_id = '$id' AND comment = '$comment' AND comment_user_id = '$user_id'";

if(mysqli_query($con,$sql_8))
echo "Your Comment has been deleted !";

else
echo "Deletion Failed !";

}


else if($method === $methodname9)
{

if(mysqli_num_rows($res_report)>0)
echo "You have already reported this Post !";

else{

$sql_9 ="INSERT INTO report (post_id, post_title_comment, report_user_id) VALUES ('$id', '".addslashes($comment)."', '$user_id');";

if(mysqli_query($con,$sql_9))
echo "You have reported this Post !";

else
echo "Reporting Failed !";
}

}


else if($method === $methodname10)
{

if(mysqli_num_rows($res_report)>0)
echo "You have already reported this Comment !";

else{

$sql_10 ="INSERT INTO report (post_id, post_title_comment, report_user_id) VALUES ('$id', '".addslashes($comment)."', '$user_id');";

if(mysqli_query($con,$sql_10))
echo "You have reported this Comment !";

else
echo "Reporting Failed !";
}

}


else if($method === $methodname11)
{

if(mysqli_num_rows($res_report)>0)
echo "You have already reported this User !";

else{

$sql_11 ="INSERT INTO report (post_id, post_title_comment, report_user_id) values('$id', '".addslashes($comment)."', '$user_id');";

if(mysqli_query($con,$sql_11))
echo "You have reported this User !";

else
echo "Reporting Failed !";
}

}

?>