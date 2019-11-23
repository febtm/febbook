<?php

require_once('init.php');

$method = $_POST['method'];
$email = $_POST['email'];
$subject = $_POST['subject'];
$message = $_POST['message'];

$headers = 'From: Febulous <fmt.febulous@gmail.com>' . "\r\n" .
        'Reply-To: Febulous <fmt.febulous@gmail.com>' . "\r\n" .
        'X-Mailer: PHP/' . phpversion();


if($method === "ContactUs"){
	
	$receiver_email = "fmt.febulous@gmail.com";

    if(mail($receiver_email, $subject, $message, $headers))
		echo "Your Message has been sent : Kindly wait until our Team responds to your Message !";
	
	else
		echo "Sending Email Failed !";
    
}

else if($method === "RecoverPassword"){
	
	$receiver_email = $email;
	
	$sql = "SELECT username, password FROM profile WHERE email = '".addslashes($receiver_email)."'";

	$res = mysqli_query($con, $sql);

	if(mysqli_num_rows($res)>0){
		
		$row = mysqli_fetch_array($res);
		
		$receiver_username = $row['username'];
		
		$receiver_password = $row['password'];
		
		
		$message = str_replace("BATMAN", $receiver_username, $message);
		$message = str_replace("JOKER", $receiver_password, $message);
		
		
		if(mail($receiver_email, $subject, $message, $headers))
			echo "A recovery email has been sent to your Email-Id !";
		
		else
			echo "Sending Email Failed !";
		
	}
	
	else
		echo "An Account with this Email-Id does not exist !";

}
	
mysqli_close($con); 
 
?>