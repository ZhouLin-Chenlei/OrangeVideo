/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.community.yuequ.util;


import com.community.yuequ.BuildConfig;

public class Log {

    public static final int NONE = -1;
    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int ERROR = android.util.Log.ERROR;
    public static final int ASSERT = android.util.Log.ASSERT;


    public static void println(int priority, String tag, String msg, Throwable tr) {
        if(!BuildConfig.DEBUG){
            return;
        }
        String priorityStr = null;
        // For the purposes of this View, we want to print the priority as readable text.
        switch(priority) {
            case android.util.Log.VERBOSE:
                priorityStr = "VERBOSE";
                if (tr != null) {
                    android.util.Log.v(tag,msg,tr);
                }else{
                    android.util.Log.v(tag,msg);
                }
                break;
            case android.util.Log.DEBUG:
                priorityStr = "DEBUG";
                if (tr != null) {
                    android.util.Log.d(tag,msg,tr);
                }else{
                    android.util.Log.d(tag,msg);
                }
                break;
            case android.util.Log.INFO:
                priorityStr = "INFO";
                if (tr != null) {
                    android.util.Log.i(tag,msg,tr);
                }else{
                    android.util.Log.i(tag,msg);
                }
                break;
            case android.util.Log.WARN:
                priorityStr = "WARN";
                if (tr != null) {
                    android.util.Log.w(tag,msg,tr);
                }else{
                    android.util.Log.w(tag,msg);
                }
                break;
            case android.util.Log.ERROR:
                priorityStr = "ERROR";
                if (tr != null) {
                    android.util.Log.e(tag,msg,tr);
                }else{
                    android.util.Log.e(tag,msg);
                }
                break;
            case android.util.Log.ASSERT:
                priorityStr = "ASSERT";
                if (tr != null) {
                    android.util.Log.wtf(tag,msg,tr);
                }else{
                    android.util.Log.wtf(tag,msg);
                }
                break;
            default:
                break;
        }


//        String exceptionStr = null;
//        if (tr != null) {
//            exceptionStr = android.util.Log.getStackTraceString(tr);
//        }
//        final StringBuilder outputBuilder = new StringBuilder();
//
//        String delimiter = "\t";
//        appendIfNotNull(outputBuilder, priorityStr, delimiter);
//        appendIfNotNull(outputBuilder, tag, delimiter);
//        appendIfNotNull(outputBuilder, msg, delimiter);
//        appendIfNotNull(outputBuilder, exceptionStr, delimiter);



    }
    /** Takes a string and adds to it, with a separator, if the bit to be added isn't null. Since
     * the logger takes so many arguments that might be null, this method helps cut out some of the
     * agonizing tedium of writing the same 3 lines over and over.
     * @param source StringBuilder containing the text to append to.
     * @param addStr The String to append
     * @param delimiter The String to separate the source and appended strings. A tab or comma,
     *                  for instance.
     * @return The fully concatenated String as a StringBuilder
     */
    private static StringBuilder appendIfNotNull(StringBuilder source, String addStr, String delimiter) {
        if (addStr != null) {
            if (addStr.length() == 0) {
                delimiter = "";
            }

            return source.append(addStr).append(delimiter);
        }
        return source;
    }



    public static void println(int priority, String tag, String msg) {
        println(priority, tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable tr) {
        println(VERBOSE, tag, msg, tr);
    }

    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable tr) {
        println(DEBUG, tag, msg, tr);
    }


    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable tr) {
        println(INFO, tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable tr) {
        println(WARN, tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, Throwable tr) {
        w(tag, null, tr);
    }


    public static void e(String tag, String msg, Throwable tr) {
        println(ERROR, tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }


    public static void wtf(String tag, String msg, Throwable tr) {
        println(ASSERT, tag, msg, tr);
    }

    public static void wtf(String tag, String msg) {
        wtf(tag, msg, null);
    }

    public static void wtf(String tag, Throwable tr) {
        wtf(tag, null, tr);
    }
}
