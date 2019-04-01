package com.cadovnik.sausagemakerhelper.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HeatingService extends IntentService {
    public HeatingService() {
        super("HeatingService");
    }

    public static void startBackgroundHeatingHandler(Context context) {
        Intent intent = new Intent(context, HeatingService.class);
        context.startService(intent);
        Toast.makeText(context, "service starting", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            HeatingNotification.HeatingProcessNotify(this);
        }
    }

}
