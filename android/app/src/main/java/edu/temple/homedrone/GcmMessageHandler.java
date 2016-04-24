package edu.temple.homedrone;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService
{

    String mes;
    private Handler handler;

    public GcmMessageHandler()
    {
        super( "GcmMessageHandler" );
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent( Intent intent )
    {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance( this );
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType( intent );

        mes = extras.getString( "title" );
        Log.i( "GCM", "Received : (" + messageType + ")  " + extras.getString( "body" ) );
        String note = "Hello from Server";

        if ( extras.getString( "body" ) != null )
        {
            note = extras.getString( "body" );
        }

        MainActivity.notifyUser( note, getApplicationContext() );

        GcmBroadcastReceiver.completeWakefulIntent( intent );

    }
}