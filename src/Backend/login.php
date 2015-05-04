<?php
//This REST API is used to login an user of VBG app.

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
	

if(empty($_POST['username']) || empty($_POST['password'])){//Verify if the REST API call is used properly
	$response["success"] = 2;
	$response["message"] = "Enter both Username and Password";
	die(json_encode($response));
}

$tableInDB = 'users';
$query = "SELECT username, password FROM ".$tableInDB ." WHERE username = ?";
if($stmt = $conn->prepare($query)){
//Prepare MySQL database Query
    $stmt->bind_param("s",$_POST['username']);
	//Bind the '?' parameter in MySQL prepare statement
	if($stmt->execute()){
	//Execute the query onto the database
		$login_ok = false;
		$stmt->bind_result($name, $pwd);
		//Bind the result to this variable
		if($stmt->fetch()){
		//Fetch the result from database query.
			if($_POST['password'] === $pwd){
			//Check Whether the credentials match or not
				$login_ok = true;
			}
		}
		if($login_ok){
			$response["success"] = 0;
			$response["message"] = "Logged in successfully";
			//send the status of the request to the client. Contains : 'Success' value[0:Success, Non-0: Different errors], 'message' value[Type of success state].
			echo json_encode($response);
		}else{
			$response["success"] = 5;
			$response["message"] = "Invalid Credentials";
			echo json_encode($response);
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
