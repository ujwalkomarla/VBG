<?php
//This REST API is used to send a message to client, informing it to change to next Firebase Canvas URL

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


if(empty($_POST['username']) || empty($_POST['url'])){//Verify if the REST API call is used properly
	$response["success"] = 2;
	$response["message"] = "Enter Username and Firebase URL";
	die(json_encode($response));
}

//Reverse lookup identifying the client from guide name in list of active(Guide-Client) connections.
//Then with the client name, we query the 'users' database to get the GCM ID of client and send message to that client.
$tableInDB = 'GuideClient';
$query = "SELECT clientname FROM ".$tableInDB ." WHERE guidename=?";
if($stmt = $conn->prepare($query)){
//Prepare MySQL database Query
	$stmt->bind_param("s",$_POST['username']);
	//Bind the '?' parameter in MySQL prepare statement
	if($stmt->execute()){
	//Execute the query onto the database
		$stmt->bind_result($client);
		//Bind the result to this variable
		if($stmt->fetch()){
		//Fetch the result from database query.

			//$response["toClient"] = $client;
			//$conn1 = new mysqli($servername,$username,$password,$dbname,$portno);
			$response["success"]=1;
			$response["message"]="error";
			//We now query the 'users' database to get the GCM ID of the client and send a GCM message to it.
			$tableInDB1 = 'users';
			$query1 = "SELECT gcmid FROM ".$tableInDB1 ." WHERE username=?";
			if($stmt1 = $conn->prepare($query1)){
				$stmt1->bind_param("s",$client);
				if($stmt1->execute()){
					$stmt1->bind_result($gcmid);//Store the GCM ID result in this variable
					if($stmt1->fetch()){//Fetch the result fo the query
						$gcpm = new GCMPushMessage($apiKey);//Create a new GCMPushMessage object and send message to the client.
						$gcpm->setDevices($gcmid);//The GCM device ID to send the message to.
						$responseC["GCMserverResponse"] = array('type' => 'url', 'url' => $_POST['url']);
						$gcpm->send(null,$responseC["GCMserverResponse"]);//The Message format sent to the client is of JSON format['type'=>'url', 'url'=>'new firebase url']
						$response["success"] = 0;
						$response["message"] = "Text Sent!";
						//$response["toGCMid"] = $gcmid;
					}
				}
			}
			//send the status of the request to the Guide. Contains : 'Success' value[0:Success, Non-0: Different errors], 'message' value[Type of success state].
            echo json_encode($response);
			//Close the database connections
            $stmt->close();
			$stmt1->close();
		}
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
