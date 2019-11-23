<?php

$db_name = "u691283964_febul";
$mysql_user = "u691283964_admin";
$mysql_pass = "hugh#007";
$server_name = "mysql.hostinger.in";

define('FIREBASE_API_KEY', 'AAAAr3Wp--Y:APA91bHkD5-Evd95XrdlCTf1TMMgpo5WP48k8AZk4FEl0Xli6R-hEq3Va-vCoea1nqXnPLlO0LjZ941zD5Cs3BZ1lDJbtQvIs9cabVg9OgHf-2t0Xy04mhgkLsANdV4Vf8K6O6WYUHaS');

$con = mysqli_connect($server_name,$mysql_user,$mysql_pass,$db_name);

if(!$con)
{
//echo "Connection Error ... ".mysqli_connect_error();
}
else
{
//echo "<h3>Database Connection Success ... </h3>";
}

?>