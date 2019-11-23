<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$id = $_GET['id'];

require_once('init.php');


$sql = "SELECT c.comment, c.created_at, p.username, p.picture FROM comments c LEFT JOIN profile p ON c.comment_user_id = p.userid WHERE c.post_id = '".$id."' ORDER BY c.created_at ASC";
 
$r = mysqli_query($con,$sql);

$result = array();
 
while($res = mysqli_fetch_array($r)){

array_push($result,array(

	 "username"=>$res['username'],
	 "userimage"=>$res['picture'],
	 "comment"=>$res['comment'],
	 "timestamp"=>$res['created_at'],
 )
 );
 
}

echo json_encode(array("result"=>$result));
 
mysqli_close($con);
 
}

?>
