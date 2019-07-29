package com.twassing.adminapp;

public abstract class LogEvent {

    static final int FACILITY_SECURITY = 16; //Local 0 = security logging
    static final int FACILITY_DNS = 17; //Local 1 = DNS lookups
    static final int FACILITY_CONNECT = 18; //Local 2 = Connection logs

    abstract public String getTimestamp();

    abstract public String getLogPRI();

    abstract public String getMessage();

}
