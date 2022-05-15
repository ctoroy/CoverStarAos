package com.shinleeholdings.coverstar.util;

import android.util.Log;

import com.shinleeholdings.coverstar.AppConstants;

public class DebugLogger {
    public static boolean IS_DEBUG = true;
    private static final String TAG = AppConstants.APP_NAME;

    public static void i(String tag, String message) {
        if (!IS_DEBUG) {
            return;
        }

        Log.i(tag, AppConstants.APP_NAME + " : " + message);
    }

    public static void e(String tag, String message) {
        if (!IS_DEBUG) {
            return;
        }

        Log.e(tag, AppConstants.APP_NAME + " : " + message);
    }

    public static void exception(Exception e) {
        if (!IS_DEBUG) {
            return;
        }

        e.printStackTrace();
    }

    public static void d(String message) {
        if (IS_DEBUG) {
            String tag = "";
            String temp = new Throwable().getStackTrace()[1].getClassName();
            if (temp != null) {
                int lastDotPos = temp.lastIndexOf(".");
                tag = temp.substring(lastDotPos + 1);
            }
            String methodName = new Throwable().getStackTrace()[1]
                    .getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            String logText = "[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message;
            Log.d(TAG, logText);
        }
    }

    public static void d(Exception e) {
        if (IS_DEBUG) {
            e.printStackTrace();
        }
    }

    public static void i(String message) {
        if (IS_DEBUG) {
            String tag = "";
            String temp = new Throwable().getStackTrace()[1].getClassName();
            if (temp != null) {
                int lastDotPos = temp.lastIndexOf(".");
                tag = temp.substring(lastDotPos + 1);
            }
            String methodName = new Throwable().getStackTrace()[1]
                    .getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            String logText = "[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message;
            Log.i(TAG, logText);
        }
    }

    public static void e(String message) {
        if (IS_DEBUG) {
            String tag = "";
            String temp = new Throwable().getStackTrace()[1].getClassName();
            if (temp != null) {
                int lastDotPos = temp.lastIndexOf(".");
                tag = temp.substring(lastDotPos + 1);
            }
            String methodName = new Throwable().getStackTrace()[1]
                    .getMethodName();
            int lineNumber = new Throwable().getStackTrace()[1].getLineNumber();

            String logText = "[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message;
            Log.e(TAG, logText);
        }
    }
}

