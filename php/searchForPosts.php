<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$index = $_GET['index'];

$searchText = $_GET['searchText'];

$searchType = $_GET['searchType'];

require_once('init.php');

if($searchType === 'all')
$sql = "SELECT post_id, post_title, post_type, post_image FROM posts WHERE post_title LIKE '%".addslashes($searchText)."%' OR post_description LIKE '%".addslashes($searchText)."%' ORDER BY created_at DESC LIMIT $index, 10";
 
else
$sql = "SELECT post_id, post_title, post_type, post_image FROM posts WHERE (post_title LIKE '%".addslashes($searchText)."%' OR post_description LIKE '%".addslashes($searchText)."%') AND post_type = '$searchType' ORDER BY created_at DESC LIMIT $index, 10";
	
$res = mysqli_query($con,$sql);
 
$result = array();

while($row = mysqli_fetch_array($res)){

array_push($result,array(	
	 
	 "post_id"=>$row['post_id'],
	 "post_type"=>$row['post_type'],
 	 "post_title"=>$row['post_title'],
	 "post_image"=>$row['post_image'],	
	 
 )
 );
}

echo json_encode(array("result"=>$result));
 
mysqli_close($con);

}
 
?>