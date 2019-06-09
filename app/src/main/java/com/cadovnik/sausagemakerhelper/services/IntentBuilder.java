package com.cadovnik.sausagemakerhelper.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.IntDef;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Command enumeration
// more info - http://blog.shamanland.com/2016/02/int-string-enum.html
@IntDef({Command.INVALID, Command.STOP, Command.START})
@Retention(RetentionPolicy.SOURCE)
@interface Command {

    int INVALID = -1;
    int STOP = 0;
    int START = 1;
}

public class IntentBuilder {

    private static final String KEY_MESSAGE = "msg";
    private static final String KEY_JSON = "jsn";
    private static final String KEY_COMMAND = "cmd";
    private Context mContext;
    private String mMessage;
    private JSONObject json;
    private @Command int mCommandId = Command.INVALID;

    public static IntentBuilder getInstance(Context context) {
        return new IntentBuilder(context);
    }

    public IntentBuilder(Context context) {
        this.mContext = context;
    }

    public IntentBuilder setMessage(String message) {
        this.mMessage = message;
        return this;
    }

    /**
     * @param command Don't use {@link Command#INVALID} as a param. If you do then this method does
     *     nothing.
     */
    public IntentBuilder setCommand(@Command int command) {
        this.mCommandId = command;
        return this;
    }

    public Intent build() {
        Intent intent = new Intent(mContext, HeatingService.class);
        if (mCommandId != Command.INVALID) {
            intent.putExtra(KEY_COMMAND, mCommandId);
        }
        if (mMessage != null) {
            intent.putExtra(KEY_MESSAGE, mMessage);
        }
        if (json != null){
            intent.putExtra(KEY_JSON, json.toString());
        }
        return intent;
    }

    public static boolean containsCommand(Intent intent) {
        return intent.getExtras().containsKey(KEY_COMMAND);
    }

    public static boolean containsMessage(Intent intent) {
        return intent.getExtras().containsKey(KEY_MESSAGE);
    }

    public static @Command int getCommand(Intent intent) {
        final @Command int commandId = intent.getExtras().getInt(KEY_COMMAND);
        return commandId;
    }

    public static String getMessage(Intent intent) {
        return intent.getExtras().getString(KEY_MESSAGE);
    }

    public IntentBuilder setJson(JSONObject json) {
        this.json = json;
        return this;
    }
}