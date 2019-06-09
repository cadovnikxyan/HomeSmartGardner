package com.cadovnik.sausagemakerhelper.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.cadovnik.sausagemakerhelper.http.HttpConnectionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HeatingService extends Service {
    private HeatingService self = this;
    public HeatingService() {
        mBinder = new Binder();
        mStartMode = 1;
        mAllowRebind = false;
    }
    private Callback espGetCurrentStateCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(this.getClass().toString(), "Error: " + e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                HeatingNotification.updateNotification(self, new JSONObject(response.body().string()));
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSON ESP: " , e);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    @Override
    public void onCreate() {
        // The service is being created
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Timer currentStateTimer = new Timer();
        if ( Command.START == IntentBuilder.getCommand(intent) )
        {
            try {
                JSONObject object = new JSONObject( intent.getStringExtra("jsn"));
                HeatingNotification.createHeatingProcessNotify(this, object);
                currentStateTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try{
                            if ( HttpConnectionHandler.getInstance().IsFindedESP()){
                                HttpConnectionHandler.getInstance().getESPRequest("GetCurrentState", espGetCurrentStateCallback);
                            }
                        }catch (NullPointerException e){
                            Log.e(this.getClass().toString(), "Timer: " , e);
                        }
                    }
                }, 0, 10000);
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSON ESP: " , e);
            }
        }else if ( Command.STOP == IntentBuilder.getCommand(intent)){
            currentStateTimer.cancel();
            HeatingNotification.cancel(this);
            stopSelf();
        }
        return mStartMode;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        super.onDestroy();
    }

}
