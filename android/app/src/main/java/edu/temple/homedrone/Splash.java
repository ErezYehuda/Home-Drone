package edu.temple.homedrone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity
{

    Timer timer;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );

        timer = new Timer();
        timer.schedule( new TimerTask()
        {

            public void run()
            {

                Intent i = new Intent( Splash.this, MainActivity.class );
                startActivity( i );
                finish();
            }

        }, 3000 );
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        try
        {
            timer.cancel();
        }
        catch ( Exception e )
        {
            Log.d( "Splash", "Timer was canceled" );
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        try
        {
            timer.cancel();
        }
        catch ( Exception e )
        {
            Log.d( "Splash", "Timer was canceled" );
        }
    }
}
