<?php

require_once('init.php');

$user_username=$_POST["signin_username"];
$user_email=$_POST["signin_email"];
$user_password=$_POST["signin_password"];
$user_usertype=$_POST["signin_usertype"];
$user_course=$_POST["signin_course"];


$sql_query_1 = "SELECT * FROM profile WHERE email = '$user_email';";

$result_1 = mysqli_query($con,$sql_query_1);


$sql_query_2 = "SELECT * FROM profile WHERE username = '$user_username';";

$result_2 = mysqli_query($con,$sql_query_2);


if(mysqli_num_rows($result_1)>0)
echo "An Account with this Email-Id already exists !";


else
{

if(mysqli_num_rows($result_2)>0)
echo "Username already exists ! Please enter a unique Username !";

else
{

$sql_query_3 ="INSERT INTO profile (username, picture, email, password, course, usertype) values('$user_username', '', '".addslashes($user_email)."', '".addslashes($user_password)."', '$user_course', '$user_usertype');";

if(mysqli_query($con,$sql_query_3))
{

echo "Sign In Successful !";

$sql_query_4 = "SELECT userid FROM profile WHERE username = '$user_username' AND email = '$user_email' AND password = '$user_password'";

$result_4 = mysqli_query($con, $sql_query_4);

$r4 = mysqli_fetch_array($result_4);

$userid = $r4['userid'];

}

else
echo "Sign In Failed !";
}

}

?>