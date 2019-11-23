<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$email = $_GET['email'];

require_once('init.php');


$sql = "SELECT userid, username, picture, email, password FROM profile WHERE email = '".$email."'";
 
$r = mysqli_query($con,$sql);
 
$res = mysqli_fetch_array($r);

$result = array();
 
array_push($result,array(

	 "user_id"=>$res['userid'],
	 "username"=>$res['username'],
	 "email"=>$res['email'],
	 "userimage"=>$res['picture'],
	 "password"=>$res['password'],

 )
 );
 
 echo json_encode(array("result"=>$result));
 
 mysqli_close($con);
 
 }

?>
