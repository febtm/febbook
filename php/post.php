<?php 

require_once('init.php');

$type = $_POST['type'];
$title = $_POST['title'];
$desc = $_POST['desc'];
$image = $_POST['image'];
	
$uid = $_POST['uid'];

$facebook_share = $_POST['facebook_share'];
$twitter_share = $_POST['twitter_share'];
				
if($type === "Events"){

$sql = "INSERT INTO posts(post_type, post_title, post_description, post_image, user_id, facebook_share, twitter_share) VALUES ('$type', '".addslashes($title)."', '".addslashes($desc)."', '$image', '$uid', '$facebook_share', '$twitter_share')";
	
if(mysqli_query($con,$sql)){


$sql_1 = "SELECT post_id FROM posts WHERE post_title = '$title' AND post_description = '$desc' AND user_id = '$uid' ORDER BY created_at DESC";

$res_1 = mysqli_query($con,$sql_1);

$row_1 = mysqli_fetch_array($res_1);

$id = $row_1['post_id'];


$sql_2 = "INSERT INTO notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('$id', '66666666', '$uid', '$uid')";

if(mysqli_query($con,$sql_2))
echo "Your Post has been published !";

else
echo "Posting Failed !";

}

else
echo "Posting Failed !";

}


else{

$sql = "INSERT INTO posts(post_type, post_title, post_description, post_image, user_id, facebook_share, twitter_share) VALUES ('$type', '".addslashes($title)."', '".addslashes($desc)."', '$image', '$uid', '$facebook_share', '$twitter_share')";
	
if(mysqli_query($con,$sql))
echo "Your Post has been published !";

else
echo "Posting Failed !";

}
		
mysqli_close($con);

?>