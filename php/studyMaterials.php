<?php
  
function special($string) {

    return preg_replace('/[^A-Za-z0-9\.]/', ' ', $string);

}

function spaces($string) {

return str_replace(' ', '', $string);

}


    $temp = $_FILES['uploaded_file']['name'];

    $temp = special($temp);

    $temp = spaces($temp);

    $temp = strtolower($temp);
   
    $file_path = "StudyMaterials/";
     
    $file_path = $file_path. basename($temp);

   
    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path))
       echo "Success !";

    else
        echo "Failed !";
    
?>