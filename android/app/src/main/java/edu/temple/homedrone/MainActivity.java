package edu.temple.homedrone;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
    private String VideoURLString = "";
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
            }
        };
        final Runnable longBackward = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 2 );
                messageClass.execute( "" );
            }
        };
        final Runnable longRight = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 3 );
                messageClass.execute( "" );
            }
        };
        final Runnable longLeft = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 4 );
                messageClass.execute( "" );
            }
        };
        final Runnable stop = new Runnable()
        {
            @Override
            public void run()
            {
                MessageClass messageClass = new MessageClass( IPaddress, portText, 5 );
                messageClass.execute( "" );
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
                injectCSS();
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
                boolean pressed = false;
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
            portET.setText( portText );
            IPaddressET.setText( IPaddress );
            videoURL.setText( VideoURLString );
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

    private void injectCSS() {
        try {
            videoView.loadUrl( "javascript:(function() {" +
                    "var img = document.getElementsByTagName('img').item(0);" +
                    "img.style.width=1000px })()" );
        } catch (Exception e) {
            e.printStackTrace();
        }
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


}
