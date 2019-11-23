<?php

$db_name = "id4775978_febtech";
$mysql_user = "id4775978_febin";
$mysql_pass = "hugh#007";
$server_name = "localhost";

$conn = new mysqli($server_name, $mysql_user, $mysql_pass, $db_name);

if ($conn->connect_error) {

    die("Connection Failed : " . $conn->connect_error);

}

?>