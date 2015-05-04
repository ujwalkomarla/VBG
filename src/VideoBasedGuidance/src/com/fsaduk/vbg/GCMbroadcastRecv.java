package com.fsaduk.vbg;


/*
REFERENCES:
1.	https://developer.android.com/google/gcm/index.html
2.	http://captechconsulting.com/blog/antoine-campbell/google-cloud-messaging-cloud-connection-server-tutorial
3.http://javapapers.com/android/google-cloud-messaging-gcm-for-android-and-push-notifications/
4.http://fryerblog.com/post/30057483199/implementing-push-notifications-with-gcm
5.http://hmkcode.com/android-google-cloud-messaging-tutorial/
6.https://github.com/mattg888/GCM-PHP-Server-Push-Message
*/
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GCMbroadcastRecv extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Explicitly specify that GcmMessageHandler will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GCMmsgHandler.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
