package com.cadovnik.sausagemakerhelper.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.github.druk.rx2dnssd.BonjourService;
import com.github.druk.rx2dnssd.Rx2Dnssd;
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpConnectionHandler {
    private static final String Hostname = "cadovnik.fvds.ru";
    public static final String ESP_Hostname = "cadovnik_esp8266.local.";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static HttpConnectionHandler instance = null;
    private static SSLContext context;
    private static X509TrustManager trustManager;
    protected Rx2Dnssd rxDnssd;
    protected Disposable disposable;
    private BonjourService bonjourService;
    private String resultedESPIP = "";
    private OkHttpClient client;

    public static void Initialize(InputStream in){
        trustAllHosts(in);
    }
    public static void InitializeRXDNS(Context context){
        getInstance().rxDnssd = new Rx2DnssdEmbedded(context);
        getInstance().getHostnameUndermDNS();
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

    private void getHostnameUndermDNS(){

        disposable = rxDnssd.browse("_http._tcp.", "local.")
                .compose(rxDnssd.resolve())
                .compose(rxDnssd.queryIPRecords())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bonjourService -> {
                    if (!bonjourService.isLost()) {
                        resultedESPIP = bonjourService.getInet4Address().getHostAddress();
                        Log.e("DNSSD", bonjourService.getHostname());
                        Log.e("DNSSD", bonjourService.getDomain());
                        Log.e("DNSSD", bonjourService.getRegType());
                        Log.e("DNSSD", bonjourService.getServiceName());
                        Log.e("DNSSD", bonjourService.getInet4Address().getHostAddress());
                        this.bonjourService = bonjourService;
                    }
                }, throwable -> {
                    Log.e("DNSSD", "Error: ", throwable);
                });
    }

    public void getESPRequest(String url, Callback callback){
        if ( resultedESPIP.isEmpty() )
            getHostnameUndermDNS();
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(resultedESPIP).append("/").append(url);
        Log.e("getESPRequest", builder.toString());
        getRequest(  builder.toString() , callback);
    }
    public void getRequest(String url, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request)
              .enqueue(callback);
    }

    public void postESPRequest(String url,  String json,Callback callback){
        if ( resultedESPIP.isEmpty() )
            getHostnameUndermDNS();
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(resultedESPIP).append("/").append(url);
        Log.e("postESPRequest", builder.toString());
        postRequest( builder.toString(),json , callback);
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
