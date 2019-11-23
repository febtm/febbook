<?php

require_once('init.php');

$userid=$_POST["userid"];
$username=$_POST["username"];
$gender=$_POST["gender"];
$picture=$_POST["picture"];
$dob=$_POST["dob"];
$email=$_POST["email"];
$password=$_POST["password"];
$oldusername = $_POST["oldusername"];
$oldemail = $_POST["oldemail"];
$oldpassword = $_POST["oldpassword"];
$college = $_POST["college"];
$department = $_POST["department"];
$fullname = $_POST["fullname"];
$cover = $_POST["cover"];
$about = $_POST["about"];
$course = $_POST["course"];
$usertype = $_POST["usertype"];


$sql_query_1 = "SELECT * FROM profile WHERE username like '$username' AND '$username'!= '$oldusername';";

$result_1 = mysqli_query($con,$sql_query_1);


$sql_query_2 = "SELECT * FROM profile WHERE email like '".addslashes($email)."' AND '".addslashes($email)."' != '".addslashes($oldemail)."';";

$result_2 = mysqli_query($con,$sql_query_2);


$sql_query_3 = "SELECT password FROM profile WHERE email like '".addslashes($oldemail)."' AND password = '".addslashes($oldpassword)."';";

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

$sql_query_4 = "UPDATE profile SET username = '$username', email = '".addslashes($email)."', password = '".addslashes($password)."', picture = '$picture', gender = '$gender', dob = '$dob', name = '".addslashes($fullname)."', college = '".addslashes($college)."', usertype = '$usertype', department = '".addslashes($department)."', about = '".addslashes($about)."', coverphoto = '$cover', course = '$course' WHERE userid = '$userid'";

if(mysqli_query($con,$sql_query_4))
echo "Your Profile has been edited Successfully !";

else
echo "Profile Edition Failed !";

}

else
echo "The Current Password that you have entered is incorrect ! Please enter the correct password !";

}
}

?>
