package com.cadovnik.sausagemakerhelper.services;

public interface ServiceProvider <T>{
    void serviceBind(T service);
    void serviceUnbind();
    void serviceDataCallback(ServiceCallback callback);
    void serviceSendStartCommand();
}
