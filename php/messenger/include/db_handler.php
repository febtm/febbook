<?php

class DbHandler {

    private $conn;



    function __construct() {

        require_once dirname(__FILE__) . '/db_connect.php';
        $db = new DbConnect();
        $this->conn = $db->connect();

    }



    public function createUser($user_id) {

        $response = array();
        $response["error"] = false;
        $response["user"] = $this->getUserById($user_id);
        return $response;

    }



    public function getUser($user_id) {

        $stmt = $this->conn->prepare("SELECT p.userid, p.username, p.picture FROM profile p WHERE p.userid = ?");

        $stmt->bind_param("s", $user_id);

        if ($stmt->execute()) {

            $stmt->bind_result($user_id, $username, $userimage);
            $stmt->fetch();

            $user = array();
            $user["user_id"] = $user_id;
            $user["username"] = $username;
            $user["userimage"] = $userimage;

            return $user;

        } else {
            return NULL;
        }

        $stmt->close();

    }



    public function getUserById($user_id) {

        $stmt = $this->conn->prepare("SELECT p.userid, p.username, p.picture FROM profile p WHERE p.userid = ?");

        $stmt->bind_param("s", $user_id);

        if ($stmt->execute()) {

            $stmt->bind_result($user_id, $username, $userimage);
            $stmt->fetch();

            $user = array();
            $user["user_id"] = $user_id;
            $user["username"] = $username;
            $user["userimage"] = $userimage;

            return $user;

        } else {
            return NULL;
        }

        $stmt->close();

  }



    public function getChatRooms($userid, $index) {

        $chat_room = array();

        $chat_room["error"] = false;

        $chat_room["chat_rooms"] = array();

        $stmt = $this->conn->prepare("SELECT cr.chat_room_id, cr.user1_id, p1.username as user1_name, p1.picture as user1_image, cr.user1_online, cr.user1_read, cr.user2_id, p2.username as user2_name, p2.picture as user2_image, cr.user2_online, cr.user2_read, cr.created_at FROM chat_rooms cr, profile p1, profile p2 WHERE (cr.user1_id = $userid OR cr.user2_id = $userid) AND p1.userid = cr.user1_id AND p2.userid = cr.user2_id ORDER BY cr.created_at DESC LIMIT $index, 10");

        if ($stmt->execute()) {

           $stmt->bind_result($chat_room_id, $user1_id, $user1_name, $user1_image, $user1_online, $user1_read, $user2_id, $user2_name, $user2_image, $user2_online, $user2_read, $created_at);

           while($stmt->fetch()){
   
                $tmp = array();

   		$tmp["chat_room_id"] = $chat_room_id;					
		$tmp["user1_id"] = $user1_id;
        	$tmp["user1_name"] = $user1_name;
        	$tmp["user1_image"] = $user1_image;
	   	$tmp["user1_online"] = $user1_online;
	   	$tmp["user1_read"] = $user1_read;
		
		$tmp["user2_id"] = $user2_id;
	   	$tmp["user2_name"] = $user2_name;
        	$tmp["user2_image"] = $user2_image;
	   	$tmp["user2_online"] = $user2_online;
	   	$tmp["user2_read"] = $user2_read;

	   	$tmp["created_at"] = $created_at;

                array_push($chat_room["chat_rooms"], $tmp);

           }   
        
           return $chat_room;

        } else {
            return NULL;
        }

       $stmt->close();

   }



    public function getChatRoomInitial($chat_room_id) {

        $response["error"] = false;
        $response["messages"] = array();
        $response['chat_room'] = array();

        $stmt = $this->conn->prepare("SELECT cr.chat_room_id, cr.user1_id, cr.user2_id, cr.created_at as chat_room_created_at, p.username as username, p.picture as userimage, c.message_id, c.user_id, c.message, c.created_at FROM chat_rooms cr, profile p, chat_messages c WHERE cr.chat_room_id = c.chat_room_id AND p.userid = c.user_id AND cr.chat_room_id = ? ORDER BY c.created_at");

        $stmt->bind_param("i", $chat_room_id);

        if($stmt->execute()) {
        
        $stmt->bind_result($chat_id, $user1_id, $user2_id, $chat_room_created_at, $username, $userimage, $message_id, $user_id, $message, $created_at);

        while($stmt->fetch()) {

            $tmp = array();
            $tmp["chat_room_id"] = $chat_id;            		 
            $tmp["created_at"] = $chat_room_created_at;
            $tmp["user1_id"] = $user1_id;
            $tmp["user2_id"] = $user2_id;

	    $response['chat_room'] = $tmp;
            
            if($tmp["user1_id"] != NULL) {

	    $cmt = array();
            $cmt["message"] = $message;
            $cmt["message_id"] = $message_id;
            $cmt["created_at"] = $created_at;

	    $user = array();
            $user['user_id'] = $user_id;
            $user['username'] = $username;
            $user['userimage'] = $userimage;
	    $cmt['user'] = $user;

            array_push($response["messages"], $cmt);

            }
       }

       return $response;

       }

       else {
             return NULL;
       }

       $stmt->close();

  }



    function getChatRoomMore($chat_room_id, $index) {

        $response["error"] = false;
        $response["messages"] = array();
        $response['chat_room'] = array();

        $stmt = $this->conn->prepare("SELECT cr.chat_room_id, cr.user1_id, cr.user2_id, cr.created_at as chat_room_created_at, p.username as username, p.picture as userimage, c.message_id, c.user_id, c.message, c.created_at FROM chat_rooms cr, profile p, chat_messages c WHERE cr.chat_room_id = c.chat_room_id AND p.userid = c.user_id AND cr.chat_room_id = ? ORDER BY c.created_at LIMIT $index, 10");
        
        $stmt->bind_param("i", $chat_room_id);

        if($stmt->execute()) {
        
        $stmt->bind_result($chat_id, $user1_id, $user2_id, $chat_room_created_at, $username, $userimage, $message_id, $user_id, $message, $created_at);

        while($stmt->fetch()) {

            $tmp = array();
            $tmp["chat_room_id"] = $chat_id;            		 
            $tmp["created_at"] = $chat_room_created_at;
            $tmp["user1_id"] = $user1_id;
            $tmp["user2_id"] = $user2_id;

	    $response['chat_room'] = $tmp;
            
            if($tmp["user1_id"] != NULL) {

	    $cmt = array();
            $cmt["message"] = $message;
            $cmt["message_id"] = $message_id;
            $cmt["created_at"] = $created_at;

	    $user = array();
            $user['user_id'] = $user_id;
            $user['username'] = $username;
            $user['userimage'] = $userimage;
	    $cmt['user'] = $user;

            array_push($response["messages"], $cmt);

            }
       }

       return $response;

       }

       else {
             return NULL;
       }

       $stmt->close();

  }



    public function addMessage($user_id, $chat_room_id, $message) {

        $response = array();

        $stmt = $this->conn->prepare("INSERT INTO chat_messages (chat_room_id, user_id, message) values(?, ?, ?)");

        $stmt->bind_param("iis", $chat_room_id, $user_id, $message);

        if ($result = $stmt->execute()) {

            $response['error'] = false;

            $message_id = $this->conn->insert_id;

            $stmt = $this->conn->prepare("SELECT message_id, user_id, chat_room_id, message, created_at FROM chat_messages WHERE message_id = ?");

            $stmt->bind_param("i", $message_id);

            if ($stmt->execute()) {

                $stmt->bind_result($message_id, $user_id, $chat_room_id, $message, $created_at);
                $stmt->fetch();

                $tmp = array();
                $tmp['message_id'] = $message_id;
                $tmp['chat_room_id'] = $chat_room_id;
                $tmp['message'] = $message;
                $tmp['created_at'] = $created_at;

                $response['message'] = $tmp;

            }
        } else {

            $response['error'] = true;
            $response['message'] = 'Failed send message ' . $stmt->error;

        }

        return $response;

    }



    public function updateChatRoom($chat_room_id, $user_id) {

        $stmt1 = $this->conn->prepare("UPDATE chat_rooms SET created_at = CURRENT_TIMESTAMP WHERE chat_room_id = '$chat_room_id'");

        $result1 = $stmt1->execute();


        $stmt2 = "SELECT * FROM chat_rooms WHERE user1_id = '$user_id' AND chat_room_id = '$chat_room_id'";

        $result2 = mysqli_query($this->conn, $stmt2);


        $stmt3 = "SELECT * FROM chat_rooms WHERE user2_id = '$user_id' AND chat_room_id = '$chat_room_id'";

        $result3 = mysqli_query($this->conn, $stmt3);


        if(mysqli_num_rows($result2)>0)
        $stmt4 = "UPDATE chat_rooms SET user1_read = 1, user2_read = 0 WHERE chat_room_id = '$chat_room_id';";

        else if(mysqli_num_rows($result3)>0)
        $stmt4 = "UPDATE chat_rooms SET user1_read = 0, user2_read = 1 WHERE chat_room_id = '$chat_room_id';";

        else
        $stmt4 = "SELECT * FROM likes";


        $result4 = mysqli_query($this->conn, $stmt4);

     }

 }

?>