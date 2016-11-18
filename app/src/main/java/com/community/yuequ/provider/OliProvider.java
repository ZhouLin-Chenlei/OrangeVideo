/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.community.yuequ.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

import com.community.yuequ.modle.RProgram;

import java.util.ArrayList;
import java.util.List;

public class OliProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;

    private static final int HISTORY = 1;
    private static final int HISTORY_ID = 2;
    private static final UriMatcher sURLMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI("com.orange.vedio", "history", HISTORY);
        sURLMatcher.addURI("com.orange.vedio", "history/#", HISTORY_ID);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "history.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE history (" +
                    "_id INTEGER PRIMARY KEY," +
                    "pid INTEGER, " +
                    "name TEXT, " +
                    "img_path TEXT, " +
                    "remark TEXT, " +
                    "show_type TEXT, " +
                    "link_url TEXT, " +
                    "type TEXT, " +
                    "collect TEXT, " +
                    "createtime LONG);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {

            db.execSQL("DROP TABLE IF EXISTS history");
            onCreate(db);
        }
    }

    public OliProvider() {
    }

    private static class SqlSelection {
        public StringBuilder mWhereClause = new StringBuilder();
        public List<String> mParameters = new ArrayList<String>();

        public <T> void appendClause(String newClause, final T... parameters) {
            if (TextUtils.isEmpty(newClause)) {
                return;
            }
            if (mWhereClause.length() != 0) {
                mWhereClause.append(" AND ");
            }
            mWhereClause.append("(");
            mWhereClause.append(newClause);
            mWhereClause.append(")");
            if (parameters != null) {
                for (Object parameter : parameters) {
                    mParameters.add(parameter.toString());
                }
            }
        }

        public String getSelection() {
            return mWhereClause.toString();
        }

        public String[] getParameters() {
            String[] array = new String[mParameters.size()];
            return mParameters.toArray(array);
        }
    }

    private static SqlSelection getWhereClause(final Uri uri,
                                               final String where, final String[] whereArgs) {
        SqlSelection selection = new SqlSelection();
        selection.appendClause(where, whereArgs);
        return selection;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sURLMatcher.match(uri);
        if (match == -1) {

            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SqlSelection fullSelection = getWhereClause(uri, selection,
                selectionArgs);
        final String table = getTableFromUri(uri);
        Cursor ret = db.query(table, projection, fullSelection.getSelection(),
                fullSelection.getParameters(), null, null, sortOrder);

        if (ret == null) {

        } else {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }
    private static String getTableFromUri(final Uri uri) {
        return uri.getPathSegments().get(0);
    }
    @Override
    public String getType(Uri url) {
        int match = sURLMatcher.match(url);
        switch (match) {
            case HISTORY:
                return "vnd.android.cursor.dir/history";
            case HISTORY_ID:
                return "vnd.android.cursor.item/history";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sURLMatcher.match(uri);
        if (match == -1) {

            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        final String table = getTableFromUri(uri);
        int count = db.update(table, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sURLMatcher.match(uri) != HISTORY) {
            throw new IllegalArgumentException("Cannot insert into URL: " + uri);
        }

        ContentValues values;
        if (initialValues != null)
            values = new ContentValues(initialValues);
        else
            values = new ContentValues();

        if (!values.containsKey(RProgram.Columns.PID))
            values.put(RProgram.Columns.PID, 0);

        if (!values.containsKey(RProgram.Columns.NAME))
            values.put(RProgram.Columns.NAME, "");

        if (!values.containsKey(RProgram.Columns.IMG_PATH))
            values.put(RProgram.Columns.IMG_PATH, "");

        if (!values.containsKey(RProgram.Columns.REMARK))
            values.put(RProgram.Columns.REMARK, "");

        if (!values.containsKey(RProgram.Columns.SHOW_TYPE))
            values.put(RProgram.Columns.SHOW_TYPE, "");

        if (!values.containsKey(RProgram.Columns.LINK_URL))
            values.put(RProgram.Columns.LINK_URL, "");

        if (!values.containsKey(RProgram.Columns.TYPE))
            values.put(RProgram.Columns.TYPE, "1");

        if (!values.containsKey(RProgram.Columns.COLLECT))
            values.put(RProgram.Columns.COLLECT, "false");
        if (!values.containsKey(RProgram.Columns.CREATETIME))
            values.put(RProgram.Columns.CREATETIME, 0l);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final String table = getTableFromUri(uri);
        long rowID = db.insert(table, null, values);
        if (rowID == -1) {

            return null;
        }

        Uri newUrl = ContentUris.withAppendedId(RProgram.Columns.CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
    }

    public int delete(Uri uri, String where, String[] whereArgs) {
        int match = sURLMatcher.match(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final String table = getTableFromUri(uri);
        SqlSelection selection = getWhereClause(uri, where, whereArgs);
        int count = db.delete(table, selection.getSelection(),
                selection.getParameters());

        if (count == 0) {

            return count;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
