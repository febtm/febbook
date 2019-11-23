<?php

ob_start();

session_start();

require_once 'init.php';

if (isset($_SESSION['user'])) {

    header("Location: home.php");
    exit;

}

?>


<!DOCTYPE html>

<head>
    
    <meta http-equiv="Content-Type" content="text/html" charset="utf-8"/>
    <title>FebBook . </title>
	<link rel="shortcut icon" type="image/png" href="web_febbook_assets/images/site_logo_main.png" />

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="web_febbook_assets/css/bootstrap.min.css" type="text/css"/>

    <style type="text/css">
        
    body {
       
       height:100%;
       width:100%;
       background-image:url(web_febbook_assets/images/index_background.jpg);
       background-repeat:no-repeat;  
       background-size:cover;  
       filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='.web_febbook_assets/images/index_background.jpg',sizingMethod='scale');
       -ms-filter:"progid:DXImageTransform.Microsoft.AlphaImageLoader(src='web_febbook_assets/images/index_background.jpg',sizingMethod='scale')";

    }

    ul.nav a:hover { 

        color: #FFFFFF !important; 
        background-color: #2196F3 !important;

    }

    </style>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

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
            <a class="navbar-brand" style="background-color: #2196F3; color: #FFFFFF; font-weight: bold; width: 200px;" href="index.php"><center>FebBook</center></a>
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
                <li><a href = "contactus.php" style="color: #2196F3; font-weight: bold;"><i style="padding-right: 10px" class="glyphicon glyphicon-edit"></i>Contact Us</a></li>
			</ul>
		</div>
        </center>

    </div>
</nav>


<center>

    <div style="margin-top: 50px">

        <img src="web_febbook_assets/images/site_logo.png" class="img-responsive" width = "100" height = "100">
        <h3 style = "color: #FFFFFF; font-weight: bold;"><i>FebBook</i></h3>

    </div>
	
	<div style="margin-top: 50px">

        <h4 style = "color: #FFFFFF;">Find FebBook -</h4>

    </div>

	
    <div style="margin-top: 20px; display: inline-block;">

        <a href="http://www.amazon.com/gp/product/B072JQQKTR/ref=mas_pm_Febulous" target="_blank">
        <img src="web_febbook_assets/images/site_amazonbadge.png" width="200" height="60" />
        </a>

    </div>

    <div style="display: inline-block;">
        
        <a href="https://play.google.com/store/apps/details?id=fmt.febulous" target="_blank">
        <img src="web_febbook_assets/images/site_googlebadge.png" width="250" height="100"/>
        </a>

    </div>

    <div style="display: inline-block;">

        <a href="https://www.facebook.com/fmt.febbook" target="_blank">
        <img style="background-color: #FFFFFF; padding: 10px;" src="web_febbook_assets/images/site_facebookbadge.png" width="200" height="60"/>
        </a>

    </div>
	
	
	<div style="margin-top: 50px">

        <h4 style = "color: #FFFFFF;">Also, Find FebWeather -</h4>

    </div>

	
    <div style="margin-top: 20px; display: inline-block;">

        <a href="http://www.amazon.com/gp/product/B071WGY819/ref=mas_pm_FebuWeather" target="_blank">
        <img src="web_febbook_assets/images/site_amazonbadge.png" width="200" height="60" />
        </a>

    </div>

    <div style="display: inline-block;">
        
        <a href="https://play.google.com/store/apps/details?id=fmt.febuweather" target="_blank">
        <img src="web_febbook_assets/images/site_googlebadge.png" width="250" height="100"/>
        </a>

    </div>

    <div style="display: inline-block;">

        <a href="https://www.facebook.com/fmt.febweather" target="_blank">
        <img style="background-color: #FFFFFF; padding: 10px;" src="web_febbook_assets/images/site_facebookbadge.png" width="200" height="60"/>
        </a>

    </div>

</center>

</body>
</html>