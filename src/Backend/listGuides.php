<?php
//This REST API is used to request active list of guides.
//Also, this API requests GCM to send a message to all my app users, and wait for reply from app users, to let the application know if a user is active or not.

//Amazon RDS MySQL Database Information
require("dbInfo.php");


//Get a Connection to database
$conn = new mysqli($servername,$username,$password,$dbname,$portno);
if($conn->connect_errno){
//Database Connection Error Handling
	$response["success"] = 1;
	$response["message"] = "Error (". $conn->connect_errno . ")" . $conn->connect_error;
	die(json_encode($response));
}

header('Content-Type: text/html; charset=utf-8');

require('GCMserverCode.php');
require('GCM_API_Key.php')

$tableInDB = "users";
$query = "SELECT gcmid FROM ".$tableInDB;
$response["GCMsuccess"] = 1;
$response["GCMmessage"] = "FAIL";
if($stmt = $conn->prepare($query)){
//Prepare MySQL database Query
	if($stmt->execute()){
	//Execute the query onto the database
		$stmt->bind_result($gcmID);
		//Bind the result to this variable
		$response["GCMsuccess"] = 0;
		$response["GCMmessage"] = "Success";
		$devices = array();
		while($stmt->fetch()){
		//Fetch the result from database query.
			array_push($devices,$gcmID);
			//store in this array.
		}
	}
}



$gcpm = new GCMPushMessage($apiKey);//Create a new GCMPushMessage object and send message to all app users.
$gcpm->setDevices($devices);//The GCM device ID to send the message to.
$response["GCMserverResponse"] = array('type' => 'ping');
$gcpm->send(null,array('type' => 'ping'));//The Message format sent to the client is of JSON format['type'=>'ping']

$query1 = "SELECT username FROM ".$tableInDB ." WHERE status = 'G' AND created_at > (NOW() - INTERVAL 30 MINUTE) ";
if($stmt1 = $conn->prepare($query1)){
//Prepare MySQL database Query
	if($stmt1->execute()){
	//Execute the query onto the database
		$stmt1->bind_result($name);
		//Bind the result to this variable
		$response["success"] = 0;
		$response["message"] = "Success";
		$i=0;
		while($stmt1->fetch()){
		//Fetch the result from database query.
			$response[$i]=$name;
			$i=$i+1;
		}
		//send the status of the request to the Guide. Contains : 'Success' value[0:Success, Non-0: Different errors], 'message' value[Type of success state], 'list' array[User names]
		$response["list"] = $i;
		echo json_encode($response);
	}else{
	//Statement Execute Error handling
		$response["success"] = 4;
		$response["message"] = "Statement Execute Error";		
		die(json_encode($response));
	}
}else{
//Query Prepare error handling
	$response["success"] = 3;
	$response["message"] = "Query Prepare Error";		
	die(json_encode($response));
}
$conn->close();
?>
