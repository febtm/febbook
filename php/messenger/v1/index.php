<?php


error_reporting(-1);

ini_set('display_errors', 'On');


require_once '../include/db_handler.php';

require '.././libs/Slim/Slim.php';


\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();



$app->post('/user/login', function() use ($app) {

  verifyRequiredParams(array('user_id'));
 
  $user_id = $app->request->post('user_id');

  $db = new DbHandler();

  $response = $db->createUser($user_id);

  echoRespnse(200, $response);

});



$app->get('/chat_rooms/:userid/:index', function($userid, $index) {

    $response = array();

    $db = new DbHandler();
    
    $response = $db->getChatRooms($userid, $index);

    echoRespnse(200, $response);

});



$app->get('/chat_room/:id', function($chat_room_id) {

    $response = array();

    $db = new DbHandler();

    $response = $db->getChatRoomInitial($chat_room_id);

    echoRespnse(200, $response);

});



$app->get('/chat_room/:id/:index', function($chat_room_id, $index) {

    $response = array();

    $db = new DbHandler();

    $response = $db->getChatRoomMore($chat_room_id, $index);

    echoRespnse(200, $response);

});



$app->post('/chat_room/:id/message', function($chat_room_id) {

    global $app;

    $db = new DbHandler();

    verifyRequiredParams(array('user_id', 'message'));

    $user_id = $app->request->post('user_id');

    $message = $app->request->post('message');

    $response = $db->addMessage($user_id, $chat_room_id, $message);

    $db->updateChatRoom($chat_room_id, $user_id);

    if ($response['error'] == false) {

        $user = $db->getUser($user_id);

        $response['user'] = $user;
        $response['error'] = false;
    }

    echoRespnse(200, $response);

});



function verifyRequiredParams($required_fields) {

    $error = false;
    $error_fields = "";
    $request_params = array();
    $request_params = $_REQUEST;

    if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
        $app = \Slim\Slim::getInstance();
        parse_str($app->request()->getBody(), $request_params);
    }

    foreach ($required_fields as $field) {
        if (!isset($request_params[$field]) || strlen(trim($request_params[$field])) <= 0) {
            $error = true;
            $error_fields .= $field . ', ';
        }
    }

    if ($error) {

        $response = array();
        $app = \Slim\Slim::getInstance();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echoRespnse(400, $response);
        $app->stop();
    }
}



function echoRespnse($status_code, $response) {

    $app = \Slim\Slim::getInstance();

    $app->status($status_code);

    $app->contentType('application/json');

    echo json_encode($response);

}

$app->run();

?>