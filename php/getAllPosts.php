<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$index = $_GET['index'];

require_once('init.php');

$sql = "SELECT pr.username, pr.picture, pr.course, pr.usertype, p.* FROM posts p LEFT JOIN profile pr ON p.user_id = pr.userid ORDER BY p.created_at DESC LIMIT $index,10";
 
$res = mysqli_query($con,$sql);
 
$result = array();

while($row = mysqli_fetch_array($res)){

array_push($result,array(	
	 "post_id"=>$row['post_id'],
	 "username"=>$row['username'],
	 "userimage"=>$row['picture'],
	 "course"=>$row['course'],
	 "usertype"=>$row['usertype'],
	 "post_type"=>$row['post_type'],
 	 "post_title"=>$row['post_title'],
	 "post_image"=>$row['post_image'],	
	 "post_description"=>$row['post_description'],
	 "timestamp"=>$row['created_at'],
 )
 );
}
 
 echo json_encode(array("result"=>$result));
 
 mysqli_close($con);

}
 
?>