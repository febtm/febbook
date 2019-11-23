<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$index = $_GET['index'];

$searchText = $_GET['searchText'];

require_once('init.php');

$sql = "SELECT username, picture FROM profile WHERE username LIKE '%".addslashes($searchText)."%' OR name LIKE '%".addslashes($searchText)."%' ORDER BY created_at DESC LIMIT $index, 10";
 	
$res = mysqli_query($con,$sql);
 
$result = array();

while($row = mysqli_fetch_array($res)){

array_push($result,array(	
	 
	 "username"=>$row['username'],
 	 "userimage"=>$row['picture'],
	 
 )
 );
}

echo json_encode(array("result"=>$result));
 
mysqli_close($con);

}
 
?>