<?php

$db_name = "a6431137_febulou";
$mysql_user = "a6431137_febulou";
$mysql_pass = "Febu123007";
$server_name = "mysql11.000webhost.com";

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