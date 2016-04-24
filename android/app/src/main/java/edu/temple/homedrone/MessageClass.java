package edu.temple.homedrone;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class MessageClass extends AsyncTask
{
    int requestCode = 0;
    private final static String IPaddress = "HTTP://54.152.236.7:8080";
    String regID;

    public MessageClass( String regID, int requestCode )
    {
        this.requestCode = requestCode;
        this.regID = regID;
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
            case 6:
                message = "AUTOON";
                break;
            case 7:
                message = "AUTOOFF";
                break;
            case 8:
                message = regID;
                break;
        }
        Log.d( "Message Sent", message + " " + IPaddress );

        try
        {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost( IPaddress );
            JSONObject jsonObject = null;
            if ( requestCode != 8 )
            {
                jsonObject = new JSONObject( "{\"COMMAND\":\"" + message + "\"}" );
            }
            else
            {
                jsonObject = new JSONObject( "{\"REGISTER_ID\":\"" + message + "\"}" );
            }
            StringEntity se = new StringEntity( jsonObject.toString() );
            httppost.setEntity( se );

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


}
