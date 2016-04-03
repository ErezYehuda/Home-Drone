package edu.temple.homedrone;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


public class MessageClass extends AsyncTask
{
    int requestCode = 0;
    String IPaddress;
    String port;

    public MessageClass( String IPaddress, String port, int requestCode )
    {
        this.IPaddress = IPaddress;
        if ( !IPaddress.startsWith( "HTTP://" ) )
        {
            this.IPaddress = "HTTP://" + IPaddress;
        }
        this.requestCode = requestCode;
        this.port = port;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute( Object o )
    {
        super.onPostExecute( o );
    }

    @Override
    protected void onProgressUpdate( Object[] values )
    {
        super.onProgressUpdate( values );
    }

    @Override
    protected Object doInBackground( Object[] params )
    {
        String message = "";
        switch ( requestCode )
        {
            case 1:
                message = "Forward";
                break;
            case 2:
                message = "Backward";
                break;
            case 3:
                message = "Right";
                break;
            case 4:
                message = "Left";
                break;
            case 5:
                message = "Stop";
                break;
        }
        Log.d( "Message Sent", message );
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost( IPaddress );
        try
        {
            // Add data
            List< NameValuePair > nameValuePairs = new ArrayList< NameValuePair >( 2 );
            nameValuePairs.add( new BasicNameValuePair( "COMMAND", message ) );
            httppost.setEntity( new UrlEncodedFormEntity( nameValuePairs ) );

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute( httppost );
            String result = EntityUtils.toString( response.getEntity() );
            Log.d( "result ", result );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void notifyUser( String notification, Activity mainActivity )
    {
        PendingIntent pi = PendingIntent.getActivity( mainActivity, 0, new Intent( mainActivity, MainActivity.class ), 0 );
        Resources r = mainActivity.getResources();
        Notification note = new NotificationCompat.Builder( mainActivity )
                .setSmallIcon( android.R.drawable.ic_menu_report_image )
                .setContentTitle( "HomeDrone Attention" )
                .setContentText( notification )
                .setContentIntent( pi )
                .setAutoCancel( true )
                .build();

        NotificationManager notificationManager = ( NotificationManager ) mainActivity.getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( 0, note );
    }
}
