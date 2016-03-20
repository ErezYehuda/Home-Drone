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


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Eric on 3/13/2016.
 */
public class MessageClass extends AsyncTask
{
    int requestCode = 0;
    String         IPaddress;
    String         port;
    DatagramSocket datagramSocket;

    public MessageClass( String IPaddress, String port, int requestCode )
    {
        this.IPaddress = IPaddress;
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
        int portNum = 0;
        try
        {
            portNum = Integer.parseInt( port );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
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
        }
        try
        {
            datagramSocket = new DatagramSocket();
            InetAddress local = InetAddress.getByName( IPaddress );
            int msg_lenght = message.length();
            byte[] messageByte = message.getBytes();
            DatagramPacket p = new DatagramPacket( messageByte, msg_lenght, local, portNum );
            datagramSocket.send( p );
        }
        catch ( SocketException e )
        {

            e.printStackTrace();
        }
        catch ( UnknownHostException e )
        {

            e.printStackTrace();
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
