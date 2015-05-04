<?php
//This REST API is used to send message to guide to inform that the client left the connection.

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


if(empty($_POST['username']) ){//Verify if the REST API call is used properly
	$response["success"] = 2;
	$response["message"] = "Enter Username";
	die(json_encode($response));
}

//Reverse lookup identifying the guide from client name in list of active(Guide-Client) connections.
//Then with the guide name, we query the 'users' database to get the GCM ID of guide and send message to that guide.
$tableInDB = 'GuideClient';
$query = "SELECT guidename FROM ".$tableInDB ." WHERE clientname=?";
if($stmt = $conn->prepare($query)){
//Prepare MySQL database Query
	$stmt->bind_param("s",$_POST['username']);
	//Bind the '?' parameter in MySQL prepare statement
	if($stmt->execute()){
	//Execute the query onto the database
		$stmt->bind_result($guide);
		//Bind the result to this variable
		if($stmt->fetch()){
		//Fetch the result from database query.
			//$response["toGuide"] = $guide;
			//$conn2 = new mysqli($servername,$username,$password,$dbname,$portno);

			//Delete the corresponding entry from active(Guide-Client) connections.
			$q2 = " DELETE FROM ".$tableInDB. " WHERE clientname=?";
			if($stmt2=$conn->prepare($q2)){
				$stmt2->bind_param("s" ,$_POST['username']);
				$stmt2->execute();
			}

			//$conn1 = new mysqli($servername,$username,$password,$dbname,$portno);
			$response["success"]=1;
			$response["message"]="error";
			//We now query the 'users' database to get the GCM ID of the guide and send a GCM message to it.
			$tableInDB1 = 'users';
			$query1 = "SELECT gcmid FROM ".$tableInDB1 ." WHERE username=?";
			if($stmt1 = $conn->prepare($query1)){
				$stmt1->bind_param("s",$guide);
				if($stmt1->execute()){
					$stmt1->bind_result($gcmid);//Store the GCM ID result in this variable
					if($stmt1->fetch()){//Fetch the result fo the query
						$gcpm = new GCMPushMessage($apiKey);//Create a new GCMPushMessage object and send message to the guide.
						$gcpm->setDevices($gcmid);//The GCM device ID to send the message to.
						$responseC["GCMserverResponse"] = array('type' => 'stopListening');
						$gcpm->send(null,$responseC["GCMserverResponse"]);//The Message format sent to the guide is of JSON format['type'=>'stopListening']
						$response["success"] = 0;
						$response["message"] = "Text Sent!";
						//$response["toGCMid"] = $gcmid;
					}
				}
			}
			//send the status of the request to the client. Contains : 'Success' value[0:Success, Non-0: Different errors], 'message' value[Type of success state].      
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
