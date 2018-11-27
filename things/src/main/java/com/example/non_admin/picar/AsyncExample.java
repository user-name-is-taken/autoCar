package com.example.non_admin.picar;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncExample extends AsyncTask <ArduinoAPI, String, String> {

    @Override
    protected String doInBackground(ArduinoAPI... APIs) {
        return null;
    }
    @Override
    protected  void onProgressUpdate(String... progress){
        //pass
    }
    @Override
    protected void onPostExecute(String result){
        Log.d("Example ", "done");
    }
}
