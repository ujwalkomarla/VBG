<?php
//This REST API is used to update information(Guide IP) of an user of VBG app and then do a reverse look up through active connections list, to get client GCM ID and send a GCM message to that client with guide's IP address

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
	
if(empty($_POST['guidename']) || empty($_POST['guideip'])){//Verify if the REST API call is used properly
		$response["success"] = 2;
		$response["message"] = "Enter Guidename and IP";
		die(json_encode($response));
}


$tableInDB = 'GuideClient'
$query = "UPDATE ".$tableInDB ." SET guideip=? WHERE guidename=?";
if($stmt = $conn->prepare($query)){
//Prepare MySQL database Query
	$stmt->bind_param("ss",$_POST['guideip'],$_POST['guidename']);
	//Bind the '?' parameter in MySQL prepare statement
	if($stmt->execute()){
	//Execute the query onto the database
		$query2 = "SELECT clientname FROM ".$tableInDB ." WHERE guideip=? AND guidename=?";
		if($stmt = $conn->prepare($query2)){
		//Prepare MySQL database Query
			$stmt->bind_param("ss",$_POST['guideip'],$_POST['guidename']);
			//Bind the '?' parameter in MySQL prepare statement
			if($stmt->execute()){
			//Prepare MySQL database Query
				$stmt->bind_result($clientname1);
				//Bind the result to this variable
				$stmt->fetch();
				//Fetch the result from database query.
			}
		}

		//Using the client name obtained from reverse lookup through active(Guide Client) Connections
		//We get the client GCM ID and send a message to it with Guide's IP
		$tableInDB1 = "users";
		$query1 = "SELECT gcmid FROM ".$tableInDB1 ." WHERE username=? AND gcmid IS NOT NULL";
		if($stmt1 = $conn->prepare($query1)){
			$stmt1->bind_param("s",$clientname1);
			if($stmt1->execute()){
				$stmt1->bind_result($gcmid);//Store the GCM ID result in this variable
				if($stmt1->fetch()){
					require('GCMserverCode.php');
					require('GCM_API_Key.php')
					$gcpm = new GCMPushMessage($apiKey);//Create a new GCMPushMessage object and send message to the client.
					$gcpm->setDevices($gcmid);//The GCM device ID to send the message to.
					$responseC["GCMserverResponse"] = array('type' => 'guideIP', 'IP' => $_POST['guideip']);
					$gcpm->send(null,$responseC["GCMserverResponse"]);//The Message format sent to the guide is of JSON format['type'=>'guideIP', 'IP'=>'IP address']
				}
			}
		}
        $response["success"] = 0;
        $response["message"] = "Update done!";
		$stmt->close();
		//send the status of the request to the client. Contains : 'Success' value[0:Success, Non-0: Different errors], 'message' value[Type of success state].
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
