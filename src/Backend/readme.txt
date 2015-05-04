GCM_API_key.php: Project API key obtained from Google API console for your application
dbInfo.php: Information related to MySQL database hosted on Amazon RDS



register.php: This REST API is used to register an user of VBG app.
login.php: This REST API is used to login an user of VBG app.
regID.php : This REST API is used to update information(GCM registration ID) of an user of VBG app.
listGuides.php: This REST API is used to request active list of guides. Also, this API requests GCM to send a message to all my app users, and wait for reply from app users, to let the application know if a user is active or not.
ping.php: This REST API is used to update information(Last Active Information) of an user of VBG app.
updateStatus.php: This REST API is used to update the status of an app user, i.e., Either Guide/Client. ['G'/'C']
connect.php: This REST API is used to send a message to guide informing it to connect to a client and guide it.
guideip.php: This REST API is used to update information(Guide IP) of an user of VBG app and then do a reverse look up through active connections list, to get client GCM ID and send a GCM message to that client with guide's IP address
text.php:This REST API is used to send text command to client
url.php:This REST API is used to send a message to client, informing it to change to next Firebase Canvas URL
stopToclient.php:This REST API is used to send message to client to inform that the guide left the connection.
stopToguide.php:This REST API is used to send message to guide to inform that the client left the connection.
GCMserverCode.php: 	Class to send push notifications using Google Cloud Messaging for Android
