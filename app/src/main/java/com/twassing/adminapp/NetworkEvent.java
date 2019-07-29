package com.twassing.adminapp;

import android.app.admin.ConnectEvent;
import android.app.admin.DnsEvent;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NetworkEvent extends LogEvent {

    private static final int SEVERITY_INFO = 6;

    private long timestamp;
    private String packageName;
    private String logID;
    private int facility;
    private String eventMessage;

    NetworkEvent(android.app.admin.NetworkEvent event)
    {
        timestamp = event.getTimestamp();
        packageName = event.getPackageName();
        logID = String.valueOf(event.getId());

        if (event instanceof DnsEvent)
        {
            facility = FACILITY_DNS;
            eventMessage = parseDnsEvent((DnsEvent)event);
        }
        else if (event instanceof ConnectEvent)
        {
            facility = FACILITY_CONNECT;
            eventMessage = parseConnectEvent((ConnectEvent)event);
        }
    }

    public String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(TimeUnit.MILLISECONDS.toMillis(timestamp));
    }


    public String getLogPRI()
    {
        return String.valueOf((facility * 8) + SEVERITY_INFO);
    }


    public String getMessage()
    {
        String message = "";
        message += "LogID=" + logID + ", ";
        message += "PackageName=" + packageName + ", ";
        message += eventMessage;
        return message;
    }

    private String parseDnsEvent(android.app.admin.DnsEvent event)
    {
        String message = "";
        message += "LogEvent=DnsEvent, ";
        message += "Description=[ ";
        message += "HostName=" + event.getHostname() + ", ";
        message += "IpAddresses=[ ";
        message += joinInetAdresses(event.getInetAddresses(), ", ") + " ] ";
        message += "TotalResolvedAddressCount=" + event.getTotalResolvedAddressCount();
        message += " ]";
        return message;
    }

    private String parseConnectEvent(android.app.admin.ConnectEvent event)
    {
        String message = "";
        message += "LogEvent=ConnectEvent ";
        message += "discription=[ ";
        message += "InetAddr=" + event.getInetAddress().toString().substring(1) + ", ";
        message += "Port=" + event.getPort();
        message += " ]";
        return message;
    }

    private String joinInetAdresses(List<InetAddress> list, String conjunction)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (InetAddress item : list)
        {
            if (first)
                first = false;
            else
                sb.append(conjunction);
            sb.append(item.toString().substring(1));
        }
        return sb.toString();
    }

}
