package com.example.non_admin.picar;

import android.os.AsyncTask;

public abstract class MikeExecutor extends AsyncTask<String, String, String> {


    /**
     * executes the related task when
     *
     * @param dataFromInput The data for
     * @return
     */
    @Override
    protected abstract String doInBackground(String... dataFromInput);

    /**
     * In many cases, this will probably just Log the result,
     *
     * @param result
     */
    @Override
    protected abstract void onPostExecute(String result);

}
