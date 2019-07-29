package com.twassing.adminapp;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SocketConnection {

    private String ipAddr;
    private int port;
    private Socket s;
    private boolean loggingSent = false;
    private Thread thread;
    private Context context;

    private SSLSocket mSSLSocket;
    private SSLCertificateSocketFactory socketFactory;


    public SocketConnection(Context context)
    {
        this.context = context;
        this.ipAddr = "192.168.43.90";
        this.port = 6514;
    }

    public SocketConnection(String ipAddr, int port)
    {
        this.ipAddr = ipAddr;
        this.port = port;
    }

    private boolean connect() {
        try {

            final KeyStore keyStore = KeyStore.getInstance("BKS");
            final KeyStore truststore = KeyStore.getInstance("BKS");

            final InputStream keystore_inputStream = context.getResources().openRawResource(R.raw.client);
            final InputStream truststore_inputStream = context.getResources().openRawResource(R.raw.clienttruststore);

            keyStore.load(keystore_inputStream, "i1DtPnzx.".toCharArray());
            truststore.load(truststore_inputStream, "i1DtPnzx.".toCharArray());

            keystore_inputStream.close();
            truststore_inputStream.close();

            KeyManagerFactory keyFManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyFManager.init(keyStore, "i1DtPnzx.".toCharArray());

            TrustManagerFactory trustMFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustMFactory.init(truststore);

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(keyFManager.getKeyManagers(), trustMFactory.getTrustManagers(), new java.security.SecureRandom());

            mSSLSocket = (SSLSocket) sc.getSocketFactory().createSocket(ipAddr, port);
            mSSLSocket.setSoTimeout(5000);
            if(mSSLSocket.isConnected())
            {
                Log.d("hoi","WOWOWOOWOWOW");
                return true;
            }
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
        return false;
    }


    public void ConnectAndSendMessage (final List<String> list)
    {
        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try  {
                    if(connect())
                    {
                        Log.d("HZLASLK", "WHOOPPPPGPGPPGPGPG");
                        PrintWriter pw = new PrintWriter(new OutputStreamWriter(mSSLSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                        for (String log : list)
                            pw.println(log);
                        loggingSent = true;

                        pw.flush();
                        pw.close();
                        mSSLSocket.close();
                    }
                }
                catch (Exception e)
                {
                    Log.d("socketConnection", "Connection with splunk failed");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public boolean getLoggingSent()
    {
        try
        {
            thread.join();
            return loggingSent;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
