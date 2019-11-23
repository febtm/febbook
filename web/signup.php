<?php

ob_start();

session_start();

include_once 'init.php';

if (isset($_SESSION['user']) != "") {

    header("Location: home.php");
    exit;

}

$user_usertype = "";
$user_course = "";

if (isset($_POST['btn-signup'])) {

    $user_username=$_POST["signup_username"];
    $user_email=$_POST["signup_email"];
    $user_password=$_POST["signup_password"];
    $user_course=$_POST["signup_course"];

    if($user_course == 'Course Type') {

    $errTyp = "warning";
    $errMSG = "Kindly select your Course Type !";

    }

    else if(!isset($_POST['user_tos'])) {

    $errTyp = "warning";
    $errMSG = "You must agree to our Terms and Conditions !";

    }

    else {


    $sql_query_1 = "SELECT * FROM profile WHERE username = '$user_username';";

    $result_1 = mysqli_query($conn,$sql_query_1);


    $sql_query_2 = "SELECT * FROM profile WHERE email = '".addslashes($user_email)."';";

    $result_2 = mysqli_query($conn,$sql_query_2);


    if(mysqli_num_rows($result_1)>0) {

    $errTyp = "warning";
    $errMSG = "Username already exists ! Please enter a unique Username !";

    }

    else {

    if(mysqli_num_rows($result_2)>0) {
    
    $errTyp = "warning";
    $errMSG = "An Account with this Email-Id already exists !";

    }

    else {

    $sql_query_3 ="INSERT INTO profile (username, picture, email, password, course) values('$user_username', '', '".addslashes($user_email)."', '".addslashes($user_password)."', '$user_course');";

    if(mysqli_query($conn,$sql_query_3)) {

    $sql_query_4 = "SELECT userid FROM profile WHERE username = '$user_username' AND email = '".addslashes($user_email)."' AND password = '".addslashes($user_password)."'";

    $result_4 = mysqli_query($conn, $sql_query_4);

    $r4 = mysqli_fetch_array($result_4);

    $userid = $r4['userid'];

    $_SESSION['user'] = $userid;

    if (isset($_SESSION['user'])) {
        print_r($_SESSION);
        header("Location: home.php");
        exit;
    }}

    else {

    $errTyp = "danger";
    $errMSG = "Sign In Failed !";

    }

}}}}

?>


<!DOCTYPE html>

<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    
    <title>FebBook . Sign Up . </title>
    <link rel="shortcut icon" type="image/png" href="web_febbook_assets/images/site_logo_main.png" />
    
    <link rel="stylesheet" href="web_febbook_assets/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="web_febbook_assets/css/style.css" type="text/css"/>

    <style type="text/css">
    
    body {
       
       height:100%;
       width:100%;
       background-image:url(web_febbook_assets/images/signup_background.jpg);
       background-repeat:no-repeat;  
       background-size:cover;  
       filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='.web_febbook_assets/images/signup_background.jpg',sizingMethod='scale');
       -ms-filter:"progid:DXImageTransform.Microsoft.AlphaImageLoader(src='web_febbook_assets/images/signup_background.jpg',sizingMethod='scale')";

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

    #signup-form {
       margin:3% auto;
       max-width:500px;
    }

    </style>

    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
    <script type="text/javascript" src="web_febbook_assets/js/bootstrap.min.js"></script>

</head>

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
                <li class="active">
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
    <div id="signup-form">
    
        <form method="post">

            <div class="col-md-12">

                <div class="form-group">
                    <hr/>
                </div>

                <div class="form-group">
                    <h4 style = "color: #FFFFFF; font-weight: bold;"><center>Enter Your Credentials to Sign Up for a Febulous Experience !</center></h4>
                </div>

                <div class="form-group">
                    <hr/>
                </div>     

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-user"></span></span>
                        <input type="text" name="signup_username" style="color: #2196F3; font-weight: bold;" class="form-control" placeholder="Username" value="<?=(isset($user_username) ? $user_username : "")?>" required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-envelope"></span></span>
                        <input type="email" name="signup_email" style="color: #2196F3; font-weight: bold;" class="form-control" placeholder="Email-Id" value="<?=(isset($user_email) ? $user_email : "")?>" required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-lock"></span></span>
                        <input type="password" name="signup_password" style="color: #2196F3; font-weight: bold;" class="form-control" placeholder="Password" value="<?=(isset($user_password) ? $user_password : "")?>" required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="background-color: #2196F3; color: #FFFFFF"><span class="glyphicon glyphicon-education"></span></span>
                        <select name="signup_course" class="form-control" style="color: #2196F3; font-weight: bold;" required>
                            <option value="Course Type" selected="selected">Course Type</option>
                            <option value="General" <?php if($user_course == 'General') { ?> selected <?php } ?>>General</option>
                            <option value="Engineering" <?php if($user_course == 'Engineering') { ?> selected <?php } ?>>Engineering</option>
                            <option value="Medicine" <?php if($user_course == 'Medicine') { ?> selected <?php } ?>>Medicine</option>
                            <option value="Arts" <?php if($user_course == 'Arts') { ?> selected <?php } ?>>Arts</option>
                            <option value="Science" <?php if($user_course == 'Science') { ?> selected <?php } ?>>Science</option>
                        </select>
                    </div>
                </div>

                <div class="checkbox" style="background-color: #FFFFFF; padding: 10px">
                    <label><input type="checkbox" name="user_tos" value="TOS" >
                    <a href="https://febtech.000webhostapp.com/FebTechT&C.pdf" style="color: #2196F3;  font-weight: bold;" target="_blank">I agree to your Terms and Conditions. Click Here to View it.</a>
                    </label>
                </div>

                <?php
                if (isset($errMSG)) {

                    ?>
                    <div class="form-group">
                        <div class="alert alert-<?php echo ($errTyp == "success") ? "success" : $errTyp; ?>">
                            <span class="glyphicon glyphicon-info-sign"></span> <?php echo $errMSG; ?>
                        </div>
                    </div>
                    <?php
                }
                ?>

                <div class="form-group">
                    <button type="submit" style = "color: #FFFFFF; background-color: #2196F3;  font-weight: bold;" class="btn btn-block btn-primary" name="btn-signup" id="signup">SIGN IN</button>
                </div>

                <div class="form-group">
                    <hr/>
                </div>

            </div>

        </form>
    </div>

</div>

</body>

</html>