<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$username= $_GET['username'];

require_once('init.php');

$sql_1 = "SELECT * FROM profile WHERE username = '".$username."'";
 
$r1 = mysqli_query($con,$sql_1);
 
$res1 = mysqli_fetch_array($r1);
 
$result = array();
 
array_push($result,array(

	 "user_id"=>$res1['userid'],
	 "username"=>$res1['username'],
	 "name"=>$res1['name'],
	 "email"=>$res1['email'],
	 "userabout"=>$res1['about'],
	 "userimage"=>$res1['picture'],
	 "usercover"=>$res1['coverphoto'],
	 "gender"=>$res1['gender'],
	 "dob"=>$res1['dob'],
	 "collegename"=>$res1['college'],
	 "course"=>$res1['course'],
	 "department"=>$res1['department'],
	 "usertype"=>$res1['usertype'],
 )
 );
 
 echo json_encode(array("result"=>$result));
 
 mysqli_close($con);
 
 }

?>