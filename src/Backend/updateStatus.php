<?php
//This REST API is used to update the status of an app user, i.e., Either Guide/Client. ['G'/'C']

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
	

if(empty($_POST['username']) || empty($_POST['status'])){//Verify if the REST API call is used properly
	$response["success"] = 2;
	$response["message"] = "Enter both Username and status";
	die(json_encode($response));
}

//Update the 'users' database entry of the corresponding 'user' entry field of 'status'
$tableInDB = 'users';
$query = "UPDATE ".$tableInDB." SET status=? WHERE username=?";
if($stmt = $conn->prepare($query)){
    $stmt->bind_param("ss",$_POST['status'],$_POST['username']);
    if($stmt->execute()){
	//send the status of the request to the Guide. Contains : 'Success' value[0:Success, Non-0: Different errors], 'message' value[Type of success state].
            $response["success"] = 0;
            $response["message"] = "Status change Done!";
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
