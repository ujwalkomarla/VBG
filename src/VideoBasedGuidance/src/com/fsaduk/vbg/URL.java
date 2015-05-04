package com.fsaduk.vbg;

public class URL {
	
public static final String PROJECT_NUMBER = "120432594904";



//<<----------------------Change the IP address of your backend server : replace 192.168.1.1 to the IP of your server ---------------------------------------->>
//Do not change the php URL .Example: if your IP address is   24.211.198.76, then the REGISTER_URL will be:  http://24.211.198.76/register.php

public static final String REGISTER_URL = "http://192.168.1.1/register.php";
public static final String LOGIN_URL = "http://192.168.1.1/login.php";
public static final String STOP_URL = "http://192.168.1.1/stop.php";
public static final String REGISTER_GCM = "http://192.168.1.1/regID.php";
public static final String CONNECT_TO_GUIDE = "http://192.168.1.1/connect.php";
public static final String UPDATE_STATUS="http://192.168.1.1/updateStatus.php";
public static final String LIST_OF_GUIDES="http://192.168.1.1/listGuides.php";
public static final String PING_URL = "http://192.168.1.1/ping.php";
public static final String  GUIDE_IP = "http://192.168.1.1/guideip.php";
public static final String STOP_TO_GUIDE = "http://192.168.1.1/stopToguide.php";
public static final String UPDATE_FIREBASE = "http://192.168.1.1/url.php";
public static final String STOP_TO_CLIENT = "http://192.168.1.1/stopToclient.php";
public static String TEXT_TO_SEND = "http://192.168.1.1/text.php";
public static String CHANGE_FIREBASE = "http://192.168.1.1/url.php";
//<<-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------->>



// Firebase server URL
// Change the URL: https://intense-fire-3198.firebaseio.com to your server URL.
public static String FIREBASE_URL = "https://intense-fire-3198.firebaseio.com/" + GuidesList.guideUsername;
public static final String videoSrc = "rtsp://"+Login.addrOfClient+":1234";
public static String FIREBASE_CLIENT_URL = "https://intense-fire-3198.firebaseio.com/" + Login.username;
}
