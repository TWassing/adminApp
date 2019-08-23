package com.twassing.adminapp;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class SocketConnection {

    private String ipAddr;
    private String certPassword;
    private int port;
    private boolean loggingSent = false;
    private Thread thread;
    private Context context;

    private SSLSocket mSSLSocket;


    public SocketConnection(Context context)
    {
        this.context = context;
        this.ipAddr = "192.168.43.90";
        this.port = 6514;
        certPassword = "password";
    }

    public SocketConnection(Context context, String ipAddr, int port, String certPassword)
    {
        this.context = context;
        this.ipAddr = ipAddr;
        this.port = port;
        this.certPassword = certPassword;
    }

    private boolean connect() {
        try {

            final KeyStore keyStore = KeyStore.getInstance("BKS");
            final KeyStore truststore = KeyStore.getInstance("BKS");

            final InputStream keystore_inputStream = context.getResources().openRawResource(R.raw.client);
            final InputStream truststore_inputStream = context.getResources().openRawResource(R.raw.clienttruststore);

            keyStore.load(keystore_inputStream, certPassword.toCharArray());
            truststore.load(truststore_inputStream, certPassword.toCharArray());

            keystore_inputStream.close();
            truststore_inputStream.close();

            KeyManagerFactory keyFManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyFManager.init(keyStore, certPassword.toCharArray());

            TrustManagerFactory trustMFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustMFactory.init(truststore);

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(keyFManager.getKeyManagers(), trustMFactory.getTrustManagers(), new java.security.SecureRandom());

            mSSLSocket = (SSLSocket) sc.getSocketFactory().createSocket();
            mSSLSocket.connect(new InetSocketAddress(ipAddr, port), 5000);
            mSSLSocket.startHandshake();
            mSSLSocket.setSoTimeout(0);

            if(mSSLSocket.isConnected())
            {
                Log.d("socketConnection", "Connected with log collector");
                return true;
            }
        }
        catch (Exception e)
        {
            Log.d("socketConnection", "Failed to connect with log collector");
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
                        PrintWriter pw = new PrintWriter(new OutputStreamWriter(mSSLSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                        for (String log : list)
                            pw.println(log);
                        Log.d("socketConnection", "Logging sent");
                        loggingSent = true;

                        pw.flush();
                        pw.close();
                        mSSLSocket.close();
                    }
                }
                catch (Exception e)
                {
                    Log.d("socketConnection", "Connection with log collector failed");
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
