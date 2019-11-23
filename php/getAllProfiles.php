<?php

require_once('init.php');

$sql = "SELECT username, usertype, picture from profile";
 
$res = mysqli_query($con,$sql);
 
$result = array();

while($row = mysqli_fetch_array($res)){

array_push($result,array(	
	 "usertype"=>$row['usertype'],
	 "username"=>$row['username'],
	 "userimage"=>$row['picture'],
 )
 );
}
 
 echo json_encode(array("result"=>$result));
 
 mysqli_close($con);
 
?>