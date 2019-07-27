package com.twassing.adminapp;

import android.annotation.TargetApi;
import android.app.admin.SecurityLog;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SecurityEvent {

    private static final int FACILITY = 4;

    private static final int SEVERITY_INFO = 6;
    private static final int SEVERITY_WARNING = 4;
    private static final int SEVERITY_ERROR = 3;

    private android.app.admin.SecurityLog.SecurityEvent event;
    private long timestamp;
    private String logLevel;
    private String logEvent;
    private String logID;
    private String logDescription;
    private String PRI;

    private Context context;

    SecurityEvent(android.app.admin.SecurityLog.SecurityEvent event)
    {
        this.event = event;
        timestamp = event.getTimeNanos();//new Date(TimeUnit.NANOSECONDS.toMillis(event.getTimeNanos())).toString();
        logID = Long.toString(getEventId(event));
        logLevel = getStringLogLevelFromId(event.getLogLevel());
        logEvent = getStringEventTagFromId(event.getTag());
        StringBuilder sb = new StringBuilder();
        printData(sb, event.getData());
        logDescription = sb.toString();
    }

    public String getTimestamp()
    {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(TimeUnit.NANOSECONDS.toMillis(timestamp));
    }
    public String getLogID()
    {
        return logID;
    }
    public String getLogLevel()
    {
        return logLevel;
    }

    public String getLogEvent()
    {
        return logEvent;
    }

    public String getLogDescription()
    {
        return logDescription;
    }

    public String getLogPRI()
    {
        final int severity;
        switch (event.getLogLevel()) {
            case android.app.admin.SecurityLog.LEVEL_INFO:
                severity = SEVERITY_INFO;
                break;
            case android.app.admin.SecurityLog.LEVEL_WARNING:
                severity = SEVERITY_WARNING;
                break;
            case android.app.admin.SecurityLog.LEVEL_ERROR:
                severity = SEVERITY_ERROR;
                break;
            default:
                Log.d("SecurityEvent", "UNKNOWN LOGLEVEL:(" + logLevel + ")");
                severity = SEVERITY_INFO;
        }
        return String.valueOf((FACILITY * 8) + severity);
    }

    @TargetApi(Build.VERSION_CODES.P)
    private long getEventId(android.app.admin.SecurityLog.SecurityEvent event) {
        return event.getId();
    }

    private String getStringEventTagFromId(int eventId) {
        final String eventTag;
        switch (eventId) {
            case android.app.admin.SecurityLog.TAG_ADB_SHELL_INTERACTIVE:
                eventTag = "ADB_SHELL_INTERACTIVE";
                break;
            case android.app.admin.SecurityLog.TAG_ADB_SHELL_CMD:
                eventTag = "ADB_SHELL_CMD";
                break;
            case android.app.admin.SecurityLog.TAG_SYNC_RECV_FILE:
                eventTag = "SYNC_RECV_FILE";
                break;
            case android.app.admin.SecurityLog.TAG_SYNC_SEND_FILE:
                eventTag = "SYNC_SEND_FILE";
                break;
            case android.app.admin.SecurityLog.TAG_APP_PROCESS_START:
                eventTag = "APP_PROCESS_START";
                break;
            case android.app.admin.SecurityLog.TAG_KEYGUARD_DISMISSED:
                eventTag = "KEYGUARD_DISMISSED";
                break;
            case android.app.admin.SecurityLog.TAG_KEYGUARD_DISMISS_AUTH_ATTEMPT:
                eventTag = "KEYGUARD_DISMISS_AUTH_ATTEMPT";
                break;
            case android.app.admin.SecurityLog.TAG_KEYGUARD_SECURED:
                eventTag = "KEYGUARD_SECURED";
                break;
            case android.app.admin.SecurityLog.TAG_OS_STARTUP:
                eventTag = "OS_STARTUP";
                break;
            case android.app.admin.SecurityLog.TAG_OS_SHUTDOWN:
                eventTag = "OS_SHUTDOWN";
                break;
            case android.app.admin.SecurityLog.TAG_LOGGING_STARTED:
                eventTag = "LOGGING_STARTED";
                break;
            case android.app.admin.SecurityLog.TAG_LOGGING_STOPPED:
                eventTag = "LOGGING_STOPPED";
                break;
            case android.app.admin.SecurityLog.TAG_MEDIA_MOUNT:
                eventTag = "MEDIA_MOUNT";
                break;
            case android.app.admin.SecurityLog.TAG_MEDIA_UNMOUNT:
                eventTag = "MEDIA_UNMOUNT";
                break;
            case android.app.admin.SecurityLog.TAG_LOG_BUFFER_SIZE_CRITICAL:
                eventTag = "LOG_BUFFER_SIZE_CRITICAL";
                break;
            case android.app.admin.SecurityLog.TAG_PASSWORD_EXPIRATION_SET:
                eventTag = "PASSWORD_EXPIRATION_SET";
                break;
            case android.app.admin.SecurityLog.TAG_PASSWORD_COMPLEXITY_SET:
                eventTag = "PASSWORD_COMPLEXITY_SET";
                break;
            case android.app.admin.SecurityLog.TAG_PASSWORD_HISTORY_LENGTH_SET:
                eventTag = "PASSWORD_HISTORY_LENGTH_SET";
                break;
            case android.app.admin.SecurityLog.TAG_MAX_SCREEN_LOCK_TIMEOUT_SET:
                eventTag = "MAX_SCREEN_LOCK_TIMEOUT_SET";
                break;
            case android.app.admin.SecurityLog.TAG_MAX_PASSWORD_ATTEMPTS_SET:
                eventTag = "MAX_PASSWORD_ATTEMPTS_SET";
                break;
            case android.app.admin.SecurityLog.TAG_KEYGUARD_DISABLED_FEATURES_SET:
                eventTag = "KEYGUARD_DISABLED_FEATURES_SET";
                break;
            case android.app.admin.SecurityLog.TAG_REMOTE_LOCK:
                eventTag = "REMOTE_LOCK";
                break;
            case android.app.admin.SecurityLog.TAG_WIPE_FAILURE:
                eventTag = "WIPE_FAILURE";
                break;
            case android.app.admin.SecurityLog.TAG_KEY_GENERATED:
                eventTag = "KEY_GENERATED";
                break;
            case android.app.admin.SecurityLog.TAG_KEY_IMPORT:
                eventTag = "KEY_IMPORT";
                break;
            case android.app.admin.SecurityLog.TAG_KEY_DESTRUCTION:
                eventTag = "KEY_DESTRUCTION";
                break;
            case android.app.admin.SecurityLog.TAG_USER_RESTRICTION_ADDED:
                eventTag = "USER_RESTRICTION_ADDED";
                break;
            case android.app.admin.SecurityLog.TAG_USER_RESTRICTION_REMOVED:
                eventTag = "USER_RESTRICTION_REMOVED";
                break;
            case android.app.admin.SecurityLog.TAG_CERT_AUTHORITY_INSTALLED:
                eventTag = "CERT_AUTHORITY_INSTALLED";
                break;
            case android.app.admin.SecurityLog.TAG_CERT_AUTHORITY_REMOVED:
                eventTag = "CERT_AUTHORITY_REMOVED";
                break;
            case android.app.admin.SecurityLog.TAG_CRYPTO_SELF_TEST_COMPLETED:
                eventTag = "CRYPTO_SELF_TEST_COMPLETED";
                break;
            case android.app.admin.SecurityLog.TAG_KEY_INTEGRITY_VIOLATION:
                eventTag = "KEY_INTEGRITY_VIOLATION";
                break;
            case android.app.admin.SecurityLog.TAG_CERT_VALIDATION_FAILURE:
                eventTag = "CERT_VALIDATION_FAILURE";
                break;
            default:
                eventTag = "UNKNOWN(" + eventId + ")";
        }
        return eventTag;
    }

    private String getStringLogLevelFromId(int logLevel) {
        final String levelTag;
        switch (logLevel) {
            case android.app.admin.SecurityLog.LEVEL_INFO:
                levelTag = "LEVEL_INFO";
                break;
            case android.app.admin.SecurityLog.LEVEL_WARNING:
                levelTag = "LEVEL_WARNING";
                break;
            case android.app.admin.SecurityLog.LEVEL_ERROR:
                levelTag = "LEVEL_ERROR";
                break;
            default:
                levelTag = "UNKNOWN LOGLEVEL(" + logLevel + ")";
        }
        return levelTag;
    }

    private void printData(StringBuilder sb, Object data) {
        if (data instanceof Integer || data instanceof Long || data instanceof Float
                || data instanceof String) {
            sb.append(data.toString()).append(" ");
        } else if (data instanceof Object[]) {
            for (Object item : (Object[]) data) {
                printData(sb, item);
            }
        }
    }
}
