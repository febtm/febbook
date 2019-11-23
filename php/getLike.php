<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$id = $_GET['id'];

require_once('init.php');

$sql = "SELECT * FROM likes WHERE post_id = '".$id."'";
 
$r = mysqli_query($con, $sql);

$result = array();
 
while($res = mysqli_fetch_array($r)){

array_push($result,array(

	 "user_id"=>$res['like_user_id'],
 )
 );
 
}

echo json_encode(array("result"=>$result));
 
mysqli_close($con);
 
}

?>