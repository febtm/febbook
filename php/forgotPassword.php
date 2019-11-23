<?php

require_once('init.php');

if($_SERVER['REQUEST_METHOD']=='GET'){
 
$email=$_GET['email'];

$sql = "SELECT username, password FROM profile WHERE email='".$email."'";
 
$r = mysqli_query($con,$sql);

if(!(mysqli_num_rows($r)>0))
echo "An Account with this Email-Id does not exist !";

else{
 
$res = mysqli_fetch_array($r);
 
$result = array();
 
array_push($result,array(
 "username"=>$res['username'],
 "password"=>$res['password']
 )
 );
 
echo json_encode(array("result"=>$result));

}
 
mysqli_close($con);
 
}
 
?>