<?php
//This REST API is used to register an user of VBG app.

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
$query = "INSERT INTO ".$tableInDB ." (username, password) VALUES (?,?)";

if($stmt = $conn->prepare($query)){
//Prepare MySQL database Query
    $stmt->bind_param("ss",$_POST['username'],$_POST['password']);
	//Bind the '?' parameter in MySQL prepare statement
    if($stmt->execute()){
	//Execute the query onto the database
		$response["success"] = 0;
		$response["message"] = "User Successfully Added!";

		//send the status of the request to the client. Contains : 'Success' value[0:Success, Non-0: Different errors], 'message' value[Type of success state].
		echo json_encode($response);


		//Close the database connections
		$stmt->close();
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
