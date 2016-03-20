package edu.temple.homedrone;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import java.util.Calendar;
import java.util.Timer;


public class MainActivity extends Activity
{

    private Button forwardButton;
    private Button backwardButton;
    private Button leftButton;
    private Button rightButton;
    private Button setupButton;
    private String IPaddress = "";
    private String portText  = "";
    private EditText       portET;
    private EditText       IPaddressET;
    private EditText       videoURL;
    private WebView        videoView;
    private ProgressDialog pDialog;
    private String VideoURLString = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
    AlertDialog.Builder builder;
    LinearLayout        lila1;
    Timer               timer;
    private Handler handler;

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
                // Print out your letter here...
                MessageClass messageClass = new MessageClass( IPaddress, portText, 1 );
                messageClass.execute( "" );
                Log.d( "HOME", "pressed" );
                // Call the runnable again
                handler.postDelayed( this, 200 );
            }
        };
        final Runnable longBackward = new Runnable()
        {
            @Override
            public void run()
            {
                // Print out your letter here...
                MessageClass messageClass = new MessageClass( IPaddress, portText, 2 );
                messageClass.execute( "" );
                Log.d( "HOME", "pressed" );
                // Call the runnable again
                handler.postDelayed( this, 200 );
            }
        };
        final Runnable longRight = new Runnable()
        {
            @Override
            public void run()
            {
                // Print out your letter here...
                MessageClass messageClass = new MessageClass( IPaddress, portText, 3 );
                messageClass.execute( "" );
                Log.d( "HOME", "pressed" );
                // Call the runnable again
                handler.postDelayed( this, 200 );
            }
        };
        final Runnable longLeft = new Runnable()
        {
            @Override
            public void run()
            {
                // Print out your letter here...
                MessageClass messageClass = new MessageClass( IPaddress, portText, 4 );
                messageClass.execute( "" );
                Log.d( "HOME", "pressed" );
                // Call the runnable again
                handler.postDelayed( this, 200 );
            }
        };


        videoView = ( WebView ) findViewById( R.id.liveVideoFeedView );

        forwardButton = ( Button ) findViewById( R.id.forward );
        backwardButton = ( Button ) findViewById( R.id.backward );
        leftButton = ( Button ) findViewById( R.id.left );
        rightButton = ( Button ) findViewById( R.id.right );
        setupButton = ( Button ) findViewById( R.id.setup );

        builder = new AlertDialog.Builder( this );

        IPaddressET = new EditText( this );//( EditText ) findViewById( R.id.IPadress );
        portET = new EditText( this );//( EditText ) findViewById( R.id.portNumber );
        videoURL = new EditText( this );//( EditText ) findViewById( R.id.videoURL );

        lila1 = new LinearLayout( this );
        videoURL.setText( "Video URL" );
        IPaddressET.setText( "IP Address" );
        portET.setText( "Port" );
        lila1.addView( videoURL );
        lila1.addView( IPaddressET );
        lila1.addView( portET );
        builder.setView( lila1 );

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

        forwardButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 1 );
                messageClass.execute( "" );
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
                        // Start printing the letter in the callback now
                        handler.post( longForward );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // Stop printing the letter
                        handler.removeCallbacks( longForward );
                }
                return true;
            }
        } );
        backwardButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 2 );
                messageClass.execute( "" );
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
                        // Start printing the letter in the callback now
                        handler.post( longBackward );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // Stop printing the letter
                        handler.removeCallbacks( longBackward );
                }
                return true;
            }
        } );
        rightButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 3 );
                messageClass.execute( "" );
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
                        // Start printing the letter in the callback now
                        handler.post( longRight );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // Stop printing the letter
                        handler.removeCallbacks( longRight );
                }
                return true;
            }
        } );
        leftButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 4 );
                messageClass.execute( "" );
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
                        // Start printing the letter in the callback now
                        handler.post( longLeft );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // Stop printing the letter
                        handler.removeCallbacks( longLeft );
                }
                return true;
            }
        } );
        setupButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {

                builder.setTitle( "Settings" );

                lila1.setOrientation( LinearLayout.VERTICAL );
                // Set up the buttons
                builder.setPositiveButton( "OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick( DialogInterface dialog, int which )
                    {
                        portText = portET.getText().toString();
                        IPaddress = IPaddressET.getText().toString();
                        VideoURLString = videoURL.getText().toString();
                        //streamVideo();
                        videoView.loadUrl( VideoURLString );
                    }
                } );
                builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick( DialogInterface dialog, int which )
                    {
                        dialog.cancel();
                    }
                } );

                builder.show();
            }
        } );
    }

//    private void streamVideo()
//    {
//        pDialog = new ProgressDialog( MainActivity.this );
//        // Set progressbar title
//        pDialog.setTitle( "Loading Live Feed" );
//        // Set progressbar message
//        pDialog.setMessage( "Buffering..." );
//        pDialog.setIndeterminate( false );
//        pDialog.setCancelable( false );
//        // Show progressbar
//        pDialog.show();
//
//        try
//        {
//            // Start the MediaController
//            MediaController mediacontroller = new MediaController( MainActivity.this );
//            mediacontroller.setAnchorView( videoView );
//            // Get the URL from String VideoURL
//            Uri video = Uri.parse( VideoURLString );
//            videoView.setMediaController( mediacontroller );
//            videoView.setVideoURI( video );
//
//        }
//        catch ( Exception e )
//        {
//            Log.e( "Error", e.getMessage() );
//            e.printStackTrace();
//        }
//
//        videoView.requestFocus();
//        videoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener()
//        {
//            // Close the progress bar and play the video
//            public void onPrepared( MediaPlayer mp )
//            {
//                pDialog.dismiss();
//                videoView.start();
//            }
//
//        } );
//
//    }

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
        if ( id == R.id.action_settings )
        {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }


}
