<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$chat_room_id = $_GET['chat_room_id'];

$index = $_GET['index'];

$type = $_GET['type'];

require_once('init.php');

if($type === "old")
$sql = "SELECT p.username as username, p.picture as userimage, c.message_id, c.user_id, c.message, c.created_at FROM profile p, chat_messages c WHERE p.userid = c.user_id AND c.chat_room_id = $chat_room_id ORDER BY c.created_at DESC LIMIT $index, 10";

else
$sql = "SELECT p.username as username, p.picture as userimage, c.message_id, c.user_id, c.message, c.created_at FROM profile p, chat_messages c WHERE p.userid = c.user_id AND c.chat_room_id = $chat_room_id ORDER BY c.created_at LIMIT $index, 10";

$res = mysqli_query($con, $sql);

$result["messages"] = array();

while($row = mysqli_fetch_array($res)){

$cmt = array();
$cmt["message"] = $row['message'];
$cmt["message_id"] = $row['message_id'];
$cmt["created_at"] = $row['created_at'];

$user = array();
$user['user_id'] = $row['user_id'];
$user['username'] = $row['username'];
$user['userimage'] = $row['userimage'];
$cmt['user'] = $user;

array_push($result["messages"], $cmt);

}

if($type === "old")
$result["messages"] = array_reverse($result["messages"]);


$sql_1 = "SELECT COUNT(*) as final_message_id FROM chat_messages WHERE chat_room_id = $chat_room_id";

$res_1 = mysqli_query($con, $sql_1);

$row_1 = mysqli_fetch_array($res_1);

$tmp['final_message_id'] = $row_1['final_message_id'];

$result['final_message_id'] = $tmp;

	
echo json_encode($result);
 
mysqli_close($con);
 
}

?>