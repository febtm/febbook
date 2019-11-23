<?php

if($_SERVER['REQUEST_METHOD']=='GET'){

$fileName = $_GET['fileName'];

$fileName = str_replace('\'','',$fileName);

require_once('init.php');

$sql = "SELECT post_id FROM posts WHERE post_type = 'Materials' AND post_description = '".addslashes($fileName)."'";

$res = mysqli_query($con, $sql);

$result = array();

if(mysqli_num_rows($res)>0){
	
array_push($result, array("fileExists"=>"Yes"));

}

else{
	
array_push($result, array("fileExists"=>"No"));

}

echo json_encode(array("result"=>$result));
 
mysqli_close($con);
 
}

?>