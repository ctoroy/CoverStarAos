package com.shinleeholdings.coverstar.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.shinleeholdings.coverstar.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@SuppressWarnings("deprecation")
public class NetworkHelper {
	private static final String TAG = "NetworkHelper";

	public static NetworkTask mTask = null;

    public static boolean isNetworkConnected() {
        try {
            ConnectivityManager connMan = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo mobileNetInfo = connMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if ((mobileNetInfo != null && mobileNetInfo.isConnected()) || (wifiNetInfo != null && wifiNetInfo.isConnected())) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
	
	public static boolean loadData(String uri, Handler handler, Context context, String msg) {
		
		new NetworkTask(handler, context, msg).execute("loadData", uri);
		
		return true;
	}

	public static class NetworkTask extends AsyncTask<String, Integer, String> {

		private ProgressDialog dialog = null;
		private Handler mHandler = null;
		private Context mContext = null;
		private String mMessage = "";
		
		public NetworkTask(Handler handler, Context context, String msg) {
			mHandler = handler;
			mContext = context;
			mMessage = msg;
		}
		
    	@Override
    	protected void onPreExecute() {

    		if (mContext != null) {
	    		dialog = new ProgressDialog(mContext);
	    		dialog.setMessage(mMessage);
	    		dialog.setIndeterminate(true);
	    		dialog.setCancelable(true);
	    		dialog.show();
    		}
    		
    	    super.onPreExecute();
    	}

    	@Override
        protected String doInBackground(String... arg) {

    		String proc = arg[0];
    		String ret = "";
    		
    		if (proc.equals("loadData")) {

    			ByteArrayOutputStream buffer = loadData(arg[1]);
	    		
    			ret = buffer == null ?  "" : buffer.toString();
    		}

    		if (dialog != null) dialog.dismiss();
    		
    		if (mHandler != null) {

	    		Message msg = new Message();
	        	Bundle data = new Bundle();
	        	data.putString(proc, ret);
	    		msg.setData(data);
	    		
	    		if (mHandler.sendMessage(msg) == false) return "fail";
    		}

    		return "";
        }

    	@Override
        protected void onProgressUpdate(Integer... progress) {
        	
            if (dialog != null) dialog.setProgress(progress[0]);
        }

    	@Override
        protected void onPostExecute(String ret) {
    	}
    }

	public static ByteArrayOutputStream loadData(String uri) {
		
		try {
			URL url = new URL(uri);
			URLConnection conn = url.openConnection();
			conn.connect();
			if(((HttpURLConnection) conn).getResponseCode() == HttpURLConnection.HTTP_OK ){
				InputStream inputStream = conn.getInputStream();
				if (inputStream != null) {
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int nRead;
					byte[] data = new byte[4096];
					while( (nRead = inputStream.read(data)) != -1 ) {
					    buffer.write(data, 0, nRead);
					}
					buffer.flush();
					return buffer;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
	    } 
	    catch (IllegalStateException e) {
	    	e.printStackTrace();
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    } 

		return null;
	}
}
