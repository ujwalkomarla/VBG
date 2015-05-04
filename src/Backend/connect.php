<?php
//This REST API is used to send a message to guide informing it to connect to a client and guide it.

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


if(empty($_POST['username']) || empty($_POST['guidename']) || empty($_POST['ip'])){//Verify if the REST API call is used properly
	$response["success"] = 2;
	$response["message"] = "Enter Username, guidename, IP";
	die(json_encode($response));
}
$query = "SELECT gcmid FROM ".$tableInDB ." WHERE username=? AND gcmid IS NOT NULL";
if($stmt = $conn->prepare($query)){
//Prepare MySQL database Query
	$stmt->bind_param("s",$_POST['guidename']);
	//Bind the '?' parameter in MySQL prepare statement
	if($stmt->execute()){
	//Execute the query onto the database
		$stmt->bind_result($gcmid);
		//Bind the result to this variable
		if($stmt->fetch()){
		//Fetch the result from database query.
			$gcpm = new GCMPushMessage($apiKey);//Create a new GCMPushMessage object and send message to the guide.
			$gcpm->setDevices($gcmid);//The GCM device ID to send the message to.
			$response["GCMserverResponse"] = array('type' => 'connectTo', 'IP' => $_POST['ip']);
			$gcpm->send(null,$response["GCMserverResponse"]);//The Message format sent to the client is of JSON format['type'=>'connectTo', 'IP'=>'IP address of client']
			
			$response["success"]=1;
			$response["message"]="error";

			//Make an entry into the Active(Guide-Client) connections database
			$tableInDB1 = 'GuideClient';
			$query1 = "INSERT INTO ".$tableInDB1 ." (guidename, clientname, clientip) VALUES (?,?,?)";
			if($stmt1 = $conn->prepare($query1)){
			//Prepare MySQL database Query
				$stmt1->bind_param("sss",$_POST['guidename'],$_POST['username'],$_POST['ip']);
				//Bind the '?' parameter in MySQL prepare statement	
				if($stmt1->execute()){
				//Execute the query onto the database
					$response["success"] = 0;
					$response["message"] = "connect request done";
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
