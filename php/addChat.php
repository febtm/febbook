<?php 

require_once('init.php');

$method = $_POST['method'];

$current_user_id = $_POST['current_user_id'];

$user_id = $_POST['user_id'];

	
$sql_1 = "SELECT * FROM chat_rooms WHERE user1_id = '$current_user_id' AND user2_id = '$user_id'";

$res1 = mysqli_query($con, $sql_1);

$sql_11 = "SELECT * FROM chat_rooms WHERE user2_id = '$current_user_id' AND user1_id = '$user_id'";

$res11 = mysqli_query($con, $sql_11);

$sql_2 = "SELECT * FROM notifications WHERE post_user_id = '$user_id' AND notification_user_id = '$current_user_id' AND chat_room_id = '55555555'";

$res2 = mysqli_query($con, $sql_2);


if($method === "chat_request")
{

if(!(mysqli_num_rows($res1)>0) && !(mysqli_num_rows($res11)>0)){

if(!(mysqli_num_rows($res2)>0)){

$sql_12 = "INSERT INTO notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('0', '55555555', '$user_id', '$current_user_id')";

if(mysqli_query($con, $sql_12))
echo "A Chat request has been sent to this User !";
}

else
echo "A Chat request has already been sent to this User !";
}

else{

$sql_4 = "SELECT chat_room_id FROM chat_rooms WHERE (user1_id = '$current_user_id' AND user2_id = '$user_id') OR (user2_id = '$current_user_id' AND user1_id = '$user_id') ";

$res4 = mysqli_query($con, $sql_4);

$row = mysqli_fetch_array($res4);

$chat_room_id = $row['chat_room_id'];

echo "$chat_room_id";
}

}


else if($method === "chat_accept")
{

if(!(mysqli_num_rows($res1)>0) && !(mysqli_num_rows($res11)>0)){

$sql1 = "INSERT INTO chat_rooms (user1_id, user2_id, user1_online) VALUES ('$current_user_id', '$user_id', '1')";

$result_1 = mysqli_query($con, $sql1);

$sql_11 = "INSERT INTO notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('0', '55555551', '$user_id', '$current_user_id')";

if(mysqli_query($con, $sql_11)){

$sql_111 = "DELETE FROM notifications WHERE chat_room_id = '55555555' AND post_user_id = '$current_user_id' AND notification_user_id = '$user_id'";

$result_111 = mysqli_query($con, $sql_111);
}
}

$sql_4 = "SELECT chat_room_id FROM chat_rooms WHERE (user1_id = '$current_user_id' AND user2_id = '$user_id') OR (user2_id = '$current_user_id' AND user1_id = '$user_id')";

$res4 = mysqli_query($con, $sql_4);

$row = mysqli_fetch_array($res4);

$chat_room_id = $row['chat_room_id'];

echo "$chat_room_id";

}


else
{

$sql_22 = "INSERT INTO notifications (post_id, chat_room_id, post_user_id, notification_user_id) VALUES ('0', '55555552', '$user_id', '$current_user_id')";

if(mysqli_query($con, $sql_22)){

$sql_22 = "DELETE FROM notifications WHERE chat_room_id = '55555555' AND post_user_id = '$current_user_id' AND notification_user_id = '$user_id'";

$result_22 = mysqli_query($con, $sql_22);

echo "The Chat request has been rejected !";
}

}

mysqli_close($con);

?>