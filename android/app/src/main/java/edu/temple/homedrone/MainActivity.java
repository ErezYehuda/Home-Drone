package edu.temple.homedrone;


import android.content.Context;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity
{

    private GLSurfaceView mGLView;
    private Button        forwardButton;
    private Button        backwardButton;
    private Button        leftButton;
    private Button        rightButton;
    private String IPaddress = "";
    private String portText = "";
    private EditText portET;
    private EditText IPaddressET;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        LinearLayout ll = ( LinearLayout ) findViewById( R.id.openGLView );
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        //mGLView = new MyGLSurfaceView( this );
        //ll.addView( mGLView );
        //setContentView( mGLView );
//        Square s = new Square();
        forwardButton = (Button)findViewById( R.id.forward );
        backwardButton = (Button)findViewById( R.id.backward );
        leftButton = (Button)findViewById( R.id.left );
        rightButton = (Button)findViewById( R.id.right );

        IPaddressET = (EditText)findViewById( R.id.IPadress );
        portET = (EditText)findViewById( R.id.portNumber );

        forwardButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                portText = portET.getText().toString();
                IPaddress = IPaddressET.getText().toString();

                MessageClass messageClass = new MessageClass( IPaddress, portText, 1 );
                messageClass.execute( "" );
            }
        } );
        backwardButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                portText = portET.getText().toString();
                IPaddress = IPaddressET.getText().toString();

                MessageClass messageClass = new MessageClass( IPaddress, portText, 2 );
                messageClass.execute( "" );
            }
        } );
        rightButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                portText = portET.getText().toString();
                IPaddress = IPaddressET.getText().toString();

                MessageClass messageClass = new MessageClass( IPaddress, portText, 3 );
                messageClass.execute( "" );
            }
        } );
        leftButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                portText = portET.getText().toString();
                IPaddress = IPaddressET.getText().toString();

                MessageClass messageClass = new MessageClass( IPaddress, portText, 4 );
                messageClass.execute( "" );
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
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
//        mGLView.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
       // mGLView.onPause();
    }


}

class MyGLSurfaceView extends GLSurfaceView
{

    private final MyGLRenderer mRenderer;


    public MyGLSurfaceView( Context context )
    {
        super( context );

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion( 2 );

        mRenderer = new MyGLRenderer( context );

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer( mRenderer );
    }


}

