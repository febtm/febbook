<!DOCTYPE html>

<head>

    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>FebBook . Recover Password . </title>
    <link rel="shortcut icon" type="image/png" href="web_febbook_assets/images/site_logo_main.png" />

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="web_febbook_assets/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="web_febbook_assets/css/style.css" type="text/css"/>

    <style type="text/css">
        
    body {
       
       height:100%;
       width:100%;
       background-image:url(web_febbook_assets/images/recoverpassword_background.jpg);
       background-repeat:no-repeat;  
       background-size:cover;  
       filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='.web_febbook_assets/images/recoverpassword_background.jpg',sizingMethod='scale');
       -ms-filter:"progid:DXImageTransform.Microsoft.AlphaImageLoader(src='web_febbook_assets/images/recoverpassword_background.jpg',sizingMethod='scale')";

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

    #recoverpassword-form {
       margin:10% auto;
       max-width:500px;
    }

    </style>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

</head>


<?php

ob_start();

session_start();

require_once 'init.php';

if (isset($_SESSION['user'])) {

    header("Location: home.php");
    exit;

}

if (isset($_POST['btn-recoverpassword'])) {

    $user_email = $_POST["recover_email"];


    $sql = "SELECT username, password FROM profile WHERE email='".addslashes($user_email)."'";
     
    $r = mysqli_query($conn, $sql);

    if(!(mysqli_num_rows($r)>0))
    $errMSG = "An Account with this Email-Id does not exist !";

    else {
     
    $res = mysqli_fetch_array($r);
     
    $user_name = $res['username'];

    $user_password = $res['password'];


    $subject = "Did you forget your password ?";

    $message1 = "Greetings " . $user_name . ",\n\n";
    $message2 = "We have received a request from your account stating that you have forgotten your password. Kindly find your password below -> ";
    $message3 = "\n\n" . "Your Password : " . $user_password . "\n\n";
    $message4 = "If you did not initiate this password request, please contact us at : fmt.febulous@gmail.com to report the issue.";
    $message5 = "\n\n" . "Regards, \nThe FebBook Team";

    $message = $message1 . $message2 . $message3 . $message4 . $message5;

    $headers = 'From: FebBook <fmt.febulous@gmail.com>' . "\r\n" .
        'Reply-To: FebBook <fmt.febulous@gmail.com>' . "\r\n" .
        'X-Mailer: PHP/' . phpversion();

    if(mail($user_email, $subject, $message, $headers))
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
            <a class="navbar-brand" href="index.php" style="background-color: #2196F3; color: #FFFFFF; font-weight: bold; width: 200px"><center>FebBook</center></a>
        </div>

        <center>
        <div class="collapse navbar-collapse" id="navbar_collapse">
            <ul class = "nav navbar-nav navbar-right">
                <li><a href = "login.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-log-in"></i>Log In</a></li>
                <li>
                <a href = "signup.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-user"></i>Sign Up</a>
                </li>
                <li class="active">
                <a href = "recoverpassword.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-wrench"></i>Recover Password</a>
                </li>
                <li><a href = "contactus.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-edit"></i>Contact Us</a></li>
            </ul>
        </div>
        </center>
        
    </div>
</nav>


<div class="container">
    <div id="recoverpassword-form">

        <form method="post">

            <div class="col-md-12">

                <div class="form-group">
                    <hr/>
                </div>

                <div class="form-group">
                    <h4 style = "color: #FFFFFF; font-weight: bold;"><center>Enter Your Email-Id !</center></h4>
                </div>

                <div class="form-group">
                    <hr/>
                </div>                

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF;"><span class="glyphicon glyphicon-envelope"></span></span>
                        <input type="email" name="recover_email" class="form-control" style = "color: #2196F3; font-weight: bold;" placeholder="Email-Id" value="<?=(isset($user_email) ? $user_email : "")?>" required/>
                    </div>
                </div>

                <?php
                if (isset($errMSG)) {

                    ?>
                    <div class="form-group">
                        <div class="alert alert-danger">
                            <span class="glyphicon glyphicon-info-sign"></span> <?php echo $errMSG; ?>
                        </div>
                    </div>
                    <?php
                }
                ?>

                <div class="form-group">
                    <button type="submit" style = "color: #FFFFFF; background-color: #2196F3;  font-weight: bold;" class="btn btn-block btn-primary" name="btn-recoverpassword">SEND RECOVERY EMAIL</button>
                </div>

                <div class="form-group">
                    <hr/>
                </div>

            </div>

        </form>

    </div>
</div>


<div class="modal fade" id="emailSuccess" tabindex="-1" role="dialog" style="margin-top: 100px;">
  <div class="modal-dialog">
    <div class="modal-content">
      
      <div class="modal-header" style="background-color: #2196F3">
          <h4 class="modal-title" style="color: #FFFFFF; font-weight: bold"><center>Recover Password</center></h4>
      </div>
      
      <div class="modal-body">
          <h5 class="modal-title" style="color: #2196F3; font-weight: bold"><center>A recovery email has been sent to your Email-Id !</center></h5>
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
          <h4 class="modal-title" style="color: #FFFFFF; font-weight: bold"><center>Recover Password</center></h4>
      </div>
      
      <div class="modal-body">
          <h5 class="modal-title" style="color: #2196F3; font-weight: bold"><center>Password Recovery Failed !</center></h5>
          <center>
          <button type="button" style = "color: #FFFFFF; background-color: #2196F3; font-weight: bold; width: 100px; margin-top: 25px" class="btn btn-block btn-primary" data-dismiss="modal">OK</button>
          </center>       
      </div> 

    </div>
  </div>
</div>

</body>

</html>