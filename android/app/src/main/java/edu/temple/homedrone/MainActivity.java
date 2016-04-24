package edu.temple.homedrone;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
{

    private Button  forwardButton;
    private Button  backwardButton;
    private Button  leftButton;
    private Button  rightButton;
    private WebView videoView;
    private static final String VideoURLString = "http://45.79.173.164:8086/";
    private Handler handler;
    private Switch  autoSwitch;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        handler = new Handler();
        final Runnable longForward = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( "", 1 );
                forwardButton.setBackgroundColor( getResources().getColor( R.color.colorPrimaryDark ) );
                messageClass.execute( "" );
            }
        };
        final Runnable longBackward = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( "", 2 );
                backwardButton.setBackgroundColor( getResources().getColor( R.color.colorPrimaryDark ) );
                messageClass.execute( "" );
            }
        };
        final Runnable longRight = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( "", 3 );
                rightButton.setBackgroundColor( getResources().getColor( R.color.colorPrimaryDark ) );
                messageClass.execute( "" );
            }
        };
        final Runnable longLeft = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( "", 4 );
                leftButton.setBackgroundColor( getResources().getColor( R.color.colorPrimaryDark ) );
                messageClass.execute( "" );
            }
        };
        final Runnable stop = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( "", 5 );
                leftButton.setBackgroundResource( android.R.drawable.btn_default );
                rightButton.setBackgroundResource( android.R.drawable.btn_default );
                forwardButton.setBackgroundResource( android.R.drawable.btn_default );
                backwardButton.setBackgroundResource( android.R.drawable.btn_default );
                messageClass.execute( "" );
            }
        };

        getRegId();

        videoView = ( WebView ) findViewById( R.id.liveVideoFeedView );

        forwardButton = ( Button ) findViewById( R.id.forward );
        backwardButton = ( Button ) findViewById( R.id.backward );
        leftButton = ( Button ) findViewById( R.id.left );
        rightButton = ( Button ) findViewById( R.id.right );
        leftButton.setBackgroundResource( android.R.drawable.btn_default );
        rightButton.setBackgroundResource( android.R.drawable.btn_default );
        forwardButton.setBackgroundResource( android.R.drawable.btn_default );
        backwardButton.setBackgroundResource( android.R.drawable.btn_default );

        autoSwitch = ( Switch ) findViewById( R.id.AutonomousMode ); //Check if drone is in auto mode
        SharedPreferences sp = getSharedPreferences( "HomeDronePref", MODE_PRIVATE );
        if ( sp.getBoolean( "AutoOn", false ) )
        {
            autoSwitch.setChecked( true );
            new MessageClass( "", 6 ).execute( "" );
        }
        else
        {
            autoSwitch.setChecked( false );
            new MessageClass( "", 7 ).execute( "" );
        }

        videoView.setWebViewClient( new WebViewClient()
        {
            public boolean shouldOverrideUrlLoading( WebView view, String url )
            {
                Log.i( "HomeDrone", "Processing webview url click..." );
                view.loadUrl( url );
                return true;
            }

            public void onPageFinished( WebView view, String url )
            {
                Log.i( "HomeDrone", "Finished loading URL: " + url );
            }

            public void onReceivedError( WebView view, int errorCode, String description, String failingUrl )
            {
                Log.e( "HomeDrone", "Error: " + description );
            }
        } );

        autoSwitch.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked )
            {
                if ( isChecked )
                {
                    new MessageClass( "", 6 ).execute( "" );
                    SharedPreferences sp = getSharedPreferences( "HomeDronePref", MODE_PRIVATE );
                    SharedPreferences.Editor e = sp.edit();
                    e.putBoolean( "AutoOn", true );
                    e.apply();
                }
                else
                {
                    new MessageClass( "", 7 ).execute( "" );
                    SharedPreferences sp = getSharedPreferences( "HomeDronePref", MODE_PRIVATE );
                    SharedPreferences.Editor e = sp.edit();
                    e.putBoolean( "AutoOn", false );
                    e.apply();
                }
            }
        } );

        forwardButton.setOnTouchListener( new View.OnTouchListener()
        {
            @Override
            public boolean onTouch( View v, MotionEvent event )
            {
                switch ( event.getAction() )
                {
                    case MotionEvent.ACTION_DOWN:
                        handler.post( longForward );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        handler.post( stop );
                }
                return true;
            }
        } );
        backwardButton.setOnTouchListener( new View.OnTouchListener()
        {
            @Override
            public boolean onTouch( View v, MotionEvent event )
            {
                switch ( event.getAction() )
                {
                    case MotionEvent.ACTION_DOWN:
                        handler.post( longBackward );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        handler.post( stop );
                }
                return true;
            }
        } );
        rightButton.setOnTouchListener( new View.OnTouchListener()
        {
            @Override
            public boolean onTouch( View v, MotionEvent event )
            {
                switch ( event.getAction() )
                {
                    case MotionEvent.ACTION_DOWN:
                        handler.post( longRight );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        handler.post( stop );
                }
                return true;
            }
        } );
        leftButton.setOnTouchListener( new View.OnTouchListener()
        {
            @Override
            public boolean onTouch( View v, MotionEvent event )
            {
                switch ( event.getAction() )
                {
                    case MotionEvent.ACTION_DOWN:
                        handler.post( longLeft );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        handler.post( stop );
                }
                return true;
            }
        } );
        videoView.getSettings().setLoadWithOverviewMode( true );
        videoView.getSettings().setUseWideViewPort( true );
        videoView.loadUrl( VideoURLString );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_refresh )
        {
            videoView.reload();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        videoView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        videoView.onPause();
    }

    public static void notifyUser( String notification, Context context )
    {
        PendingIntent pi = PendingIntent.getActivity( context, 0, new Intent( context, MainActivity.class ), 0 );
        Notification note = new NotificationCompat.Builder( context )
                .setSmallIcon( android.R.drawable.ic_menu_report_image )
                .setContentTitle( "HomeDrone Attention" )
                .setContentText( notification )
                .setContentIntent( pi )
                .setAutoCancel( true )
                .build();

        NotificationManager notificationManager = ( NotificationManager ) context.getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( 0, note );
    }

    GoogleCloudMessaging gcm;
    String               regid;
    String PROJECT_NUMBER = "19600472026";

    public void getRegId()
    {
        String ret = "";
        new AsyncTask< Void, Void, String >()
        {
            @Override
            protected String doInBackground( Void... params )
            {
                String msg = "";
                try
                {
                    if ( gcm == null )
                    {
                        gcm = GoogleCloudMessaging.getInstance( getApplicationContext() );
                    }
                    regid = gcm.register( PROJECT_NUMBER );
                    msg = "Device registered, registration ID=" + regid;
                    Log.i( "GCM", msg );

                }
                catch ( IOException ex )
                {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute( String s )
            {
                super.onPostExecute( s );
                new MessageClass( regid, 8 ).execute( "" );
            }
        }.execute( null, null, null );
    }


}
