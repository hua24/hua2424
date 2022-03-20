package com.example.hua24;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public class sqlite_tools {
    public static void insert_name(String name,String tablename,SQLiteDatabase sqLiteDatabase){//插入
        ContentValues values=new ContentValues();
        values.put("name",name);
        sqLiteDatabase.insert(tablename,null,values);
    }
    public static void create_table(String tablename,SQLiteDatabase sqLiteDatabase){//建新表
        String sql="create table if not exists " +tablename+ "(name varchar(200) unique)";
        sqLiteDatabase.execSQL(sql);
        ContentValues values=new ContentValues();
        values.put("name",tablename);
        sqLiteDatabase.insert("name_list",null,values);
    }
    public static void scan_table(String tablename, SQLiteDatabase sqLiteDatabase, List<String>list){//扫描
        if(list!=null)
            list.clear();
        Cursor cursor=sqLiteDatabase.query(tablename,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            if(cursor.getCount()>0)
                list.add(cursor.getString(cursor.getColumnIndex("name")));
        }
    }
    public static void delete_name(String name,String tablename,SQLiteDatabase sqLiteDatabase){//删除表中的名字
        sqLiteDatabase.delete(tablename,"name=?",new String[]{name});
    }
    public static void delete_table(String tablename,SQLiteDatabase sqLiteDatabase){//删除表
        String sql="drop table if exists " + tablename;
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.delete("name_list","name=?",new String[]{tablename});
    }
    public static void rename_table(String tablename,String newname,SQLiteDatabase sqLiteDatabase){//重命名表
        String sql="alter table " + tablename + " rename to "+newname;
        //String sql2="update name_list "+"set name= "+newname+" where name= "+tablename;
        sqLiteDatabase.execSQL(sql);
        ContentValues values=new ContentValues();
        values.put("name",newname);
        sqLiteDatabase.update("name_list",values,"name=?",new String[]{tablename});
        //sqLiteDatabase.execSQL(sql2);
    }
    public static int getcount(String tablename,SQLiteDatabase sqLiteDatabase){//获取表中元素数量
        Cursor cursor=sqLiteDatabase.query(tablename,null,null,null,null,null,null);
        if(cursor.moveToFirst()==false)
            return 0;
        else
            return cursor.getCount();
    }

}
