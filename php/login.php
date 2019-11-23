<?php

require_once('init.php');

$user_email=$_POST["login_email"];
$user_password=$_POST["login_password"];


$sql1 = "SELECT userid FROM profile WHERE email = '$user_email' AND password = '$user_password';";

$result1 = mysqli_query($con, $sql1);

if(mysqli_num_rows($result1)>0){

$row = mysqli_fetch_array($result1);

$user_id = $row['userid'];


$sql2 = "SELECT * FROM chat_rooms WHERE user1_id = '$user_id'";

$result2 = mysqli_query($con, $sql2);

$sql3 = "SELECT * FROM chat_rooms WHERE user2_id = '$user_id'";

$result3 = mysqli_query($con, $sql3);


if(mysqli_num_rows($result2)>0)
$sql4 = "UPDATE chat_rooms SET user1_online = 1 WHERE user1_id = '$user_id';";

else if(mysqli_num_rows($result3)>0)
$sql4 = "UPDATE chat_rooms SET user2_online = 1 WHERE user2_id = '$user_id';";

else
$sql4 = "SELECT * FROM likes";

if(mysqli_query($con, $sql4))
echo "Login Successful !";

else
echo "Login Failed !";

}

else
echo "Email-Id and Password does not match !";

?>