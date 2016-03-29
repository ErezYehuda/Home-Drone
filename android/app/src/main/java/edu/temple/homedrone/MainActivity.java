package edu.temple.homedrone;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.VideoView;
import android.widget.MediaController;


public class MainActivity extends AppCompatActivity
{

    private Button forwardButton;
    private Button backwardButton;
    private Button leftButton;
    private Button rightButton;
    private String IPaddress = "";
    private String portText  = "";
    private EditText       portET;
    private EditText       IPaddressET;
    private EditText       videoURL;
    private WebView        videoView;
    private VideoView      vv;
    private ProgressDialog pDialog;
    private String VideoURLString = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
    AlertDialog.Builder builder;
    LinearLayout        lila1;
    private Handler      handler;
    private MainActivity mainActivity;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mainActivity = this;

        handler = new Handler();
        final Runnable longForward = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 1 );
                messageClass.execute( "" );
                handler.postDelayed( this, 200 );
            }
        };
        final Runnable longBackward = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 2 );
                messageClass.execute( "" );
                handler.postDelayed( this, 200 );
            }
        };
        final Runnable longRight = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 3 );
                messageClass.execute( "" );
                handler.postDelayed( this, 200 );
            }
        };
        final Runnable longLeft = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 4 );
                messageClass.execute( "" );
                handler.postDelayed( this, 200 );
            }
        };


        videoView = ( WebView ) findViewById( R.id.liveVideoFeedView );

        forwardButton = ( Button ) findViewById( R.id.forward );
        backwardButton = ( Button ) findViewById( R.id.backward );
        leftButton = ( Button ) findViewById( R.id.left );
        rightButton = ( Button ) findViewById( R.id.right );

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
                        handler.removeCallbacks( longForward );
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
                        handler.removeCallbacks( longBackward );
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
                        handler.removeCallbacks( longRight );
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
                        handler.removeCallbacks( longLeft );
                }
                return true;
            }
        } );
    }

    private void streamVideo()
    {
        pDialog = new ProgressDialog( MainActivity.this );
        // Set progressbar title
        pDialog.setTitle( "Loading Live Feed" );
        // Set progressbar message
        pDialog.setMessage( "Buffering..." );
        pDialog.setIndeterminate( false );
        pDialog.setCancelable( false );
        // Show progressbar
        pDialog.show();

        try
        {
            // Start the MediaController
            MediaController mediacontroller = new MediaController( MainActivity.this );
            mediacontroller.setAnchorView( vv );
            // Get the URL from String VideoURL
            Uri video = Uri.parse( VideoURLString );
            vv.setMediaController( mediacontroller );
            vv.setVideoURI( video );

        }
        catch ( Exception e )
        {
            Log.e( "Error", e.getMessage() );
            e.printStackTrace();
        }

        vv.requestFocus();
        vv.setOnPreparedListener( new MediaPlayer.OnPreparedListener()
        {
            // Close the progress bar and play the video
            public void onPrepared( MediaPlayer mp )
            {
                pDialog.dismiss();
                vv.start();
            }

        } );

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
        if ( id == R.id.action_settings )
        {
            builder = new AlertDialog.Builder( mainActivity );

            IPaddressET = new EditText( mainActivity );
            portET = new EditText( mainActivity );
            videoURL = new EditText( mainActivity );

            lila1 = new LinearLayout( mainActivity );
            videoURL.setHint( "Video URL" );
            IPaddressET.setHint( "IP Address" );
            portET.setHint( "Port" );
            lila1.addView( videoURL );
            lila1.addView( IPaddressET );
            lila1.addView( portET );
            builder.setView( lila1 );
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
