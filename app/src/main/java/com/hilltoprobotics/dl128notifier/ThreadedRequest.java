package com.hilltoprobotics.dl128notifier;

import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class ThreadedRequest
{
    private String url;
    private Handler mHandler;
    private Runnable pRunnable;

    public ThreadedRequest(String newUrl)
    {
        url = newUrl;
        mHandler = new Handler();
    }

    public void start(Runnable newRun)
    {
        pRunnable = newRun;
        processRequest.start();
    }

    private Thread processRequest = new Thread()
    {
        public void run()
        {
            //Do you request here...
        	Log.v("t", "Web Send: " + url);
    			try
    	    	{
    	    		HttpClient hc = new DefaultHttpClient();
    	    		HttpGet post = new HttpGet(url);

    	    		HttpResponse rp = hc.execute(post);

    	    		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    	    		{
    	    			
    	    		}
    	    	}catch(IOException e){
    	    		e.printStackTrace();
    	    	} 
            if (pRunnable == null || mHandler == null) return;
            mHandler.post(pRunnable);
        }
    };
}