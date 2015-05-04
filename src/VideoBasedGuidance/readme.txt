Team A7: Video based guidance

Team Members:
    Nasrulla khan haris
	Prashant Narayan Kulkarni
	Sowjanya Reddy
	Ujwal Komarla




This file contains the information regarding the source code:

The source code consists of 2 parts: 
 - The backend code which runs on the server
 - The android source code

The android source code can be found in the directory: VideoBasedGuidance/src

The src consists of various packages:
Here there are 2 packages which are important.
1. com.fsaduk.vbg
2. com.vbg.guide

Package: com.fsaduk.vbg
THis package consists of the client part of the android source code and various activities at the start of the application.
 Files:
1. URL.java:  Consists of all the URLs used in the project. This file should be changed accrding to the new URLs of the backend server and Firebase database

2. ClientStreaming.java: Consists of the code which starts the streaming of the video at the client end.

3. CleintDrawingView.java: COnsists the canvas and firebase parameters which are used to receive the gestures from the guide

4. GuideOrClient.java: Activity to select weather the user wants to login as a guide or a client.

5. GuidesList.java: Consists of activity which displays the list of active guides.

6. Login.java: This is the first class which is called at the start of the application. Provides option to register the user or login the existing user . 

7. Register.java: Provision for registering a new user to the database

8. JSONParser.java: This class acts as a parser for constructing messages when interacting with the backend servers.

9. GCMmsgHandler.java: This class is used to receive the messages from the backend server. It has various cases on what should execute depending on the type of the messages received.


Package:com.vbg.guide

This package consists of the guide part of the code and various activities used for the working of guide.

Files:
1. DrawingActivity.java: This consists of the activity for the streaming at the guide part, it also consists of the parameters required for drawing the gestures on the screen and updating the Firebase URL.

2.  DrawingView.java: Sets the parameters for drawing on the canvas.

3. Point.java: This consists of getters and setters for the pixel values which are to be drawn on the the canvas and to be sent to the  firebase server.


Other packages are used for the libstreaming and setting the parameters to start and stop the streaming service.
