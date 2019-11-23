<?php

require_once('init.php');

$userid=$_POST["userid"];
$username=$_POST["username"];
$gender=$_POST["gender"];
$picture=$_POST["picture"];
$dob=$_POST["dob"];
$email=$_POST["email"];
$password=$_POST["password"];
$old_username = $_POST["old_username"];
$old_email = $_POST["old_email"];
$old_password = $_POST["old_password"];
$college = $_POST["college"];
$department = $_POST["department"];
$name = $_POST["name"];
$cover = $_POST["cover"];
$about = $_POST["about"];
$course = $_POST["course"];


$sql_query_1 = "SELECT * FROM profile WHERE username like '$username' AND '$username'!= '$old_username';";

$result_1 = mysqli_query($con,$sql_query_1);


$sql_query_2 = "SELECT * FROM profile WHERE email like '".addslashes($email)."' AND '".addslashes($email)."' != '".addslashes($old_email)."';";

$result_2 = mysqli_query($con,$sql_query_2);


$sql_query_3 = "SELECT password FROM profile WHERE email like '".addslashes($old_email)."' AND password = '".addslashes($old_password)."';";

$result_3 = mysqli_query($con,$sql_query_3);


if(mysqli_num_rows($result_1)>0)
echo "Username already exists ! Please enter a unique Username !";

else
{

if(mysqli_num_rows($result_2)>0)
echo "Another account with this Email-Id already exists ! Please enter a valid Email-Id !";

else
{

if(mysqli_num_rows($result_3)>0)
{

$sql_query_4 = "UPDATE profile SET username = '$username', email = '".addslashes($email)."', password = '".addslashes($password)."', picture = '$picture', gender = '$gender', dob = '$dob', name = '".addslashes($name)."', college = '".addslashes($college)."', department = '".addslashes($department)."', about = '".addslashes($about)."', coverphoto = '$cover', course = '$course' WHERE userid = '$userid'";

if(mysqli_query($con,$sql_query_4))
echo "Your Profile has been edited successfully !";

else
echo "Profile Edition Failed !";

}

else
echo "The Current Password that you have entered is incorrect ! Please enter the correct password !";

}
}

?>
