<!DOCTYPE html>

<head>

    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
	  <title>FebBook . Contact Us . </title>
    <link rel="shortcut icon" type="image/png" href="web_febbook_assets/images/site_logo_main.png" />

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="web_febbook_assets/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="web_febbook_assets/css/style.css" type="text/css"/>

    <style type="text/css">
            
    body {
           
       height:100%;
       width:100%;
       background-image:url(web_febbook_assets/images/contactus_background.jpg);
       background-repeat:no-repeat;  
       background-size:cover;  
       filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='.web_febbook_assets/images/login_background.jpg',sizingMethod='scale');
       -ms-filter:"progid:DXImageTransform.Microsoft.AlphaImageLoader(src='web_febbook_assets/images/login_background.jpg',sizingMethod='scale')";

    }

    ul.nav a:hover { 

        color: #FFFFFF !important; 
        background-color: #2196F3 !important;

    }

    hr {
    
       background-color: #FFFFFF;
       border: 0 none;
       color: #FFFFFF;
       height: 2px;
    }

    #contactus-form {
       margin:5% auto;
       max-width:750px;
    }

    </style>

    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
    <script type="text/javascript" src="web_febbook_assets/js/bootstrap.min.js"></script>

</head>


<?php

ob_start();

session_start();

require_once 'init.php';

if (isset($_SESSION['user'])) {

    header("Location: home.php");
    exit;

}

if (isset($_POST['btn-contactus'])) {

    $user_name = $_POST["contact_name"];

    $user_email = $_POST["contact_email"];

    $user_subject = $_POST["contact_subject"];

    $user_message = $_POST["contact_message"];


    $subject = $user_name . " : " . $user_subject;

    $message1 = "Application : FebBook Website" . "\n\nName : " . $user_name . "\n\nEmail-Id : " . $user_email;
    $message2 = "\n\nSubject : " . $user_subject . "\n\nMessage : " . $user_message;
    $message = $message1 . $message2;

    $receiver_email = "fmt.febulous@gmail.com";

    $headers = 'From: FebBook <fmt.febulous@gmail.com>' . "\r\n" .
        'Reply-To: FebBook <fmt.febulous@gmail.com>' . "\r\n" .
        'X-Mailer: PHP/' . phpversion();


    if(mail($receiver_email, $subject, $message, $headers))
        echo "<script>
         $(window).load(function(){
             $('#emailSuccess').modal('show');
         });
    </script>";

    else
        echo "<script>
         $(window).load(function(){
             $('#emailFailed').modal('show');
         });
    </script>";

}

?>


<body>

<nav class = "navbar navbar-default" role="navigation" style="background-color:#FFFFFF">
    <div class = "container-fluid">

        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar_collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar" style="background-color: #2196F3"></span>
                <span class="icon-bar" style="background-color: #2196F3"></span>
                <span class="icon-bar" style="background-color: #2196F3"></span>
            </button>
            <a class="navbar-brand" href="index.php" style="background-color: #2196F3; color: #FFFFFF; font-weight: bold; width: 200px;"><center>FebBook</center></a>
        </div>

        <center>
        <div class="collapse navbar-collapse" id="navbar_collapse">
            <ul class = "nav navbar-nav navbar-right">
                <li><a href = "login.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-log-in"></i>Log In</a></li>
                <li>
                <a href = "signup.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-user"></i>Sign Up</a>
                </li>
                <li>
                <a href = "recoverpassword.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-wrench"></i>Recover Password</a>
                </li>
                <li class="active"><a href = "contactus.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-edit"></i>Contact Us</a></li>
            </ul>
        </div>
        </center>

    </div>
</nav>


<div class="container">
    <div id="contactus-form">

        <form method="post">

            <div class="col-md-12">

                <div class="form-group">
                    <hr />
                </div>

                <div class="form-group">
                    <h4 style = "color: #FFFFFF; font-weight: bold;"><center>Send us your valuable feedback describing your experience while using FebBook.</center></h4>
                </div>

                <div class="form-group">
                    <hr />
                </div> 

                <div class="form-group">
                    <h4 style = "color: #FFFFFF; font-weight: bold;"><center>We will be happy to reply and to take up every suggestion seriously thereby making each of our user's experience and even more Febulous One !</center></h4>
                </div>

                <div class="form-group">
                    <hr />
                </div>  

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-user"></span></span>
                        <input type="text" name="contact_name" class="form-control" style = "color: #2196F3; font-weight: bold;" placeholder="Full Name" value="<?=(isset($user_name) ? $user_name : "")?>" required/>
                    </div>
                </div>              

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-envelope"></span></span>
                        <input type="email" name="contact_email" class="form-control" style = "color: #2196F3; font-weight: bold;" placeholder="Email-Id" value="<?=(isset($user_email) ? $user_email : "")?>" required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-pencil"></span></span>
                        <input type="text" name="contact_subject" class="form-control" style = "color: #2196F3; font-weight: bold;" placeholder="Subject" value="<?=(isset($user_subject) ? $user_subject : "")?>" required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-edit"></span></span>
                        <textarea name="contact_message" class="form-control" style = "color: #2196F3; font-weight: bold;" placeholder="Message" value="<?=(isset($user_message) ? $user_message : "")?>" required/></textarea>
                    </div>
                </div>  

                <?php
                if (isset($errMSG)) {

                    ?>
                    <div class="form-group">
                        <div class="alert alert-danger">
                            <span class="glyphicon glyphicon-info-sign" style="background-color: #2196F3; color: #FFFFFF"></span> <?php echo $errMSG; ?>
                        </div>
                    </div>
                    <?php
                }
                ?>

                <div class="form-group">
                    <button type="submit" style = "color: #FFFFFF; background-color: #2196F3;  font-weight: bold;" class="btn btn-block btn-primary" name="btn-contactus">SEND MESSAGE</button>
                </div>

                <div class="form-group">
                    <hr />
                </div>

            </div>

        </form>

    </div>
</div>


<div class="modal fade" id="emailSuccess" tabindex="-1" role="dialog" style="margin-top: 100px;">
  <div class="modal-dialog">
    <div class="modal-content">
      
      <div class="modal-header" style="background-color: #2196F3">
          <h4 class="modal-title" style="color: #FFFFFF; font-weight: bold"><center>Contact Us</center></h4>
      </div>
      
      <div class="modal-body">
          <h5 class="modal-title" style="color: #2196F3; font-weight: bold"><center>
        Thank You for your Message, kindly wait until our Team responds to it !</center></h5>
          <center>
          <button type="button" style = "color: #FFFFFF; background-color: #2196F3;  font-weight: bold; width: 100px; margin-top: 25px" class="btn btn-block btn-primary" data-dismiss="modal">OK</button>
          </center>       
      </div> 

    </div>
  </div>
</div>


<div class="modal fade" id="emailFailed" tabindex="-1" role="dialog" style="margin-top: 100px;">
  <div class="modal-dialog">
    <div class="modal-content">
      
      <div class="modal-header" style="background-color: #2196F3">
          <h4 class="modal-title" style="color: #FFFFFF; font-weight: bold"><center>Contact Us</center></h4>
      </div>
      
      <div class="modal-body">
          <h5 class="modal-title" style="color: #2196F3; font-weight: bold"><center>Message Delivery Failed !</center></h5>
          <center>
          <button type="button" style = "color: #FFFFFF; background-color: #2196F3;  font-weight: bold; width: 100px; margin-top: 25px" class="btn btn-block btn-primary" data-dismiss="modal">OK</button>
          </center>       
      </div> 

    </div>
  </div>
</div>

</body>
</html>