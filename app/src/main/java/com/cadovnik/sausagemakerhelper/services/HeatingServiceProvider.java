package com.cadovnik.sausagemakerhelper.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.cadovnik.sausagemakerhelper.view.MainActivity;

public class HeatingServiceProvider  implements ServiceProvider<Intent>{
    private MainActivity mainActivity;
    private ServiceCallback callback = null;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public  HeatingServiceProvider(MainActivity activity){
        this.mainActivity = activity;
    }

    @Override
    public void serviceBind(Intent service) {
        mainActivity.bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void serviceUnbind() {
        mainActivity.unbindService(connection);
    }

    @Override
    public void serviceDataCallback(ServiceCallback callback) {
        this.callback = callback;
    }

    @Override
    public void serviceSendStartCommand() {
    }

}
