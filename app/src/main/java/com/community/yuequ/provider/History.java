package com.community.yuequ.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.community.yuequ.modle.RProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/18.
 */

public class History {




    public static int addHistory(Context context, RProgram program) {
        ContentValues values = createContentValues(program);
        Uri uri = context.getContentResolver().insert(
                RProgram.Columns.CONTENT_URI, values);
       int _id = (int) ContentUris.parseId(uri);
        return _id;
    }

    public static int deleteHistory(Context context, int programId) {
        if (programId == -1) return 0;

        ContentResolver contentResolver = context.getContentResolver();
        String selection =RProgram.Columns.PID+"=?";
        String[] selectionArgs = new String[]{ String.valueOf(programId) };
//        Uri uri = ContentUris.withAppendedId(RProgram.Columns.CONTENT_URI, programId);
        int delete = contentResolver.delete(RProgram.Columns.CONTENT_URI, selection, selectionArgs);
        return delete;

    }

    public static boolean haveHistory(Context context, int programId) {
        if (programId == -1) return false;
        boolean have = false;
        ContentResolver contentResolver = context.getContentResolver();

        String selection =RProgram.Columns.PID+"=?";
        String[] selectionArgs = new String[]{ String.valueOf(programId) };
        Cursor query = contentResolver.query(
                RProgram.Columns.CONTENT_URI, RProgram.Columns.HISTORY_QUERY_COLUMNS,
                selection, selectionArgs, RProgram.Columns.DEFAULT_SORT_ORDER);
        if(query!=null){
            have = query.moveToFirst();
            query.close();
        }
        return have;
    }



    public static int setHistory(Context context, RProgram program) {
        ContentValues values = createContentValues(program);
        ContentResolver resolver = context.getContentResolver();
        int update = resolver.update(
                ContentUris.withAppendedId(RProgram.Columns.CONTENT_URI, program.id),
                values, null, null);
        return update;
    }


    public static Cursor getHistoryCursor(ContentResolver contentResolver) {
        return contentResolver.query(
                RProgram.Columns.CONTENT_URI, RProgram.Columns.HISTORY_QUERY_COLUMNS,
                null, null, RProgram.Columns.DEFAULT_SORT_ORDER);
    }

    private static ContentValues createContentValues(RProgram program) {
        ContentValues values = new ContentValues();
        values.put(RProgram.Columns.PID,program.id);
        values.put(RProgram.Columns.NAME, program.name);
        values.put(RProgram.Columns.IMG_PATH, program.img_path);
        values.put(RProgram.Columns.REMARK, program.remark);
        values.put(RProgram.Columns.SHOW_TYPE, program.show_type);
        values.put(RProgram.Columns.LINK_URL,program.link_url);
        values.put(RProgram.Columns.TYPE, program.type);
        values.put(RProgram.Columns.COLLECT, program.isCollection);
        values.put(RProgram.Columns.CREATETIME, System.currentTimeMillis());
        return values;
    }


    public static List<RProgram> getHistorys(Context context) {
        List<RProgram> list = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = getHistoryCursor(contentResolver);
        if(cursor!=null){
            while (cursor.moveToNext()){
                int _id = cursor.getInt(RProgram.Columns.HISTORY_ID_INDEX);
                int id = cursor.getInt(RProgram.Columns.HISTORY_PID_INDEX);
                String name = cursor.getString(RProgram.Columns.HISTORY_NAME_INDEX);
                String image = cursor.getString(RProgram.Columns.HISTORY_IMG_PATH_INDEX);
                String remark = cursor.getString(RProgram.Columns.HISTORY_REMARK_INDEX);
                String show_type = cursor.getString(RProgram.Columns.HISTORY_SHOW_TYPE_INDEX);
                String link_url = cursor.getString(RProgram.Columns.HISTORY_LINK_URL_INDEX);
                String type = cursor.getString(RProgram.Columns.HISTORY_TYPE_INDEX);
                String collect = cursor.getString(RProgram.Columns.HISTORY_COLLECT_INDEX);
                RProgram program = new RProgram();
                program.id = id;
                program.name = name;
                program.img_path = image;
                program.remark = remark;
                program.show_type = show_type;
                program.link_url = link_url;
                program.type = type;
                program.isCollection = collect;
                list.add(program);
            }

            cursor.close();
        }

        return list;
    }
}
