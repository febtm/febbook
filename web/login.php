<?php

ob_start();

session_start();

require_once 'init.php';

if (isset($_SESSION['user'])) {

    header("Location: home.php");
    exit;

}

if (isset($_POST['btn-login'])) {

    $user_email=$_POST["login_email"];

    $user_password=$_POST["login_password"];


    $sql1 = "SELECT userid FROM profile WHERE email = '".addslashes($user_email)."' AND password = '".addslashes($user_password)."';";

    $result1 = mysqli_query($conn, $sql1);

    if(mysqli_num_rows($result1)>0){

    $row = mysqli_fetch_array($result1);

    $user_id = $row['userid'];


    $sql2 = "SELECT * FROM chat_rooms WHERE user1_id = '$user_id'";

    $result2 = mysqli_query($conn, $sql2);

    $sql3 = "SELECT * FROM chat_rooms WHERE user2_id = '$user_id'";

    $result3 = mysqli_query($conn, $sql3);


    if(mysqli_num_rows($result2)>0)
    $sql4 = "UPDATE chat_rooms SET user1_online = 1 WHERE user1_id = '$user_id';";

    else if(mysqli_num_rows($result3)>0)
    $sql4 = "UPDATE chat_rooms SET user2_online = 1 WHERE user2_id = '$user_id';";

    else
    $sql4 = "SELECT * FROM likes";

    if(mysqli_query($conn, $sql4)){

    $_SESSION['user'] = $user_id;

    header("Location: home.php");
    
    exit;
    
    }

    else
    $errMSG = "Login Failed !";

    }

    else
    $errMSG = "Email-Id and Password does not match !";

}

?>


<!DOCTYPE html>

<head>

    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>FebBook . Log In . </title>
    <link rel="shortcut icon" type="image/png" href="web_febbook_assets/images/site_logo_main.png" />
    
    <link rel="stylesheet" href="web_febbook_assets/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="web_febbook_assets/css/style.css" type="text/css"/>

    <style type="text/css">
        
    body {
       
       height:100%;
       width:100%;
       background-image:url(web_febbook_assets/images/login_background.jpg);
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

    #login-form {
       margin:8% auto;
       max-width:500px;
    }

    </style>

    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
    <script type="text/javascript" src="web_febbook_assets/js/bootstrap.min.js"></script>

</head>


<body>

<nav class = "navbar navbar-default" role="navigation" style="background-color: #FFFFFF">
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
                <li class="active"><a href = "login.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-log-in"></i>Log In</a></li>
                <li>
                <a href = "signup.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-user"></i>Sign Up</a>
                </li>
                <li>
                <a href = "recoverpassword.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-wrench"></i>Recover Password</a>
                </li>
                <li><a href = "contactus.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-edit"></i>Contact Us</a></li>
            </ul>
        </div>
        </center>

    </div>
</nav>


<div class="container">
    <div id="login-form">

        <form method="post">

            <div class="col-md-12">

                <div class="form-group">
                    <hr />
                </div>

                <div class="form-group">
                    <h4 style = "color: #FFFFFF; font-weight: bold;"><center>Enter Your Credentials !</center></h4>
                </div>

                <div class="form-group">
                    <hr />
                </div>                

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-envelope"></span></span>
                        <input type="email" name="login_email" class="form-control" style = "color: #2196F3; font-weight: bold;" placeholder="Email-Id" value="<?=(isset($user_email) ? $user_email : "")?>" required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-lock"></span></span>
                        <input type="password" name="login_password" class="form-control" style = "color: #2196F3; font-weight: bold;" placeholder="Password" value="<?=(isset($user_password) ? $user_password : "")?>" required/>
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
                    <button type="submit" style = "color: #FFFFFF; background-color: #2196F3;  font-weight: bold;" class="btn btn-block btn-primary" name="btn-login">LOG IN</button>
                </div>

                <div class="form-group">
                    <hr />
                </div>

            </div>

        </form>

    </div>
</div>

</body>

</html>