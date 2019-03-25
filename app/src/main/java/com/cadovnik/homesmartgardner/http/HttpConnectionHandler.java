package com.cadovnik.homesmartgardner.http;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.Log;

import com.cadovnik.homesmartgardner.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpConnectionHandler {
    private static final String Hostname = "cadovnik.fvds.ru";
    private static final String ESP_Hostname = "cadovnik_esp8266.local";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static HttpConnectionHandler instance = null;
    private static SSLContext context;
    private static X509TrustManager trustManager;

    private OkHttpClient client;

    public static void Initialize(InputStream in){
        trustAllHosts(in);
    }
    public static HttpConnectionHandler getInstance(){
        if ( instance == null )
            instance = new HttpConnectionHandler();
        return instance;
    }

    private HttpConnectionHandler(){
        HostnameVerifier hostnameVerifier = (hostname, session) -> hostname.equals(Hostname) || hostname.equals(ESP_Hostname);
        client = new OkHttpClient.Builder().hostnameVerifier(hostnameVerifier)
                .sslSocketFactory(context.getSocketFactory(), trustManager)
                .build();
    }

    public void getRequest(String url, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request)
              .enqueue(callback);
    }

    public void postRequest(String url, String json, Callback callback){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request)
              .enqueue(callback);
    }

    @SuppressLint("TrulyRandom")
    private static void trustAllHosts(InputStream in) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = in;
            Certificate ca = null;
            ca = cf.generateCertificate(caInput);

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            trustManager = (X509TrustManager) tmf.getTrustManagers()[0];
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        }catch (CertificateException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (KeyStoreException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (SecurityException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (NoSuchAlgorithmException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (KeyManagementException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }catch (IOException e){
            Log.e(e.getClass().toString(), "Error: " + e.toString());
        }

    }
}
