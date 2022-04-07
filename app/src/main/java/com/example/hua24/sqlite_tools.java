package com.example.hua24;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static void create_table_mylist(String tablename,SQLiteDatabase sqLiteDatabase){//用mylist建新表
        String sql="drop table if exists " + tablename;
        sqLiteDatabase.execSQL(sql);
        String sql2="create table if not exists " +tablename+ "(name varchar(200),size varchar(200),time varchar(200),path varchar(200))";
        sqLiteDatabase.execSQL(sql2);
        if(Mydata.mylist.size()>=1){
            ContentValues values=new ContentValues();
            for(Map<String,Object> map:Mydata.mylist){
                values.put("name",map.get("name").toString());
                values.put("size",map.get("size").toString());
                values.put("time",map.get("time").toString());
                values.put("path",map.get("path").toString());
                sqLiteDatabase.insert(tablename,null,values);
            }

        }
    }
    public static void get_mylist_from_database(SQLiteDatabase sqLiteDatabase){//从表中获取主界面列表信息
        if(is_table_exist("mylist",sqLiteDatabase)){
            if(Mydata.mylist!=null)
                Mydata.mylist.clear();
            if(Mydata.play_list1!=null)
                Mydata.play_list1.clear();
            Cursor cursor=sqLiteDatabase.query("mylist",null,null,null,null,null,null);
            Map<String,Object> map;
            try{
                while(cursor.moveToNext()&&cursor.getCount()>0){
                    map = new HashMap<String,Object>();
                    map.put("name",cursor.getString(cursor.getColumnIndex("name")));
                    map.put("size",cursor.getString(cursor.getColumnIndex("size")));
                    map.put("time",cursor.getString(cursor.getColumnIndex("time")));
                    map.put("path",cursor.getString(cursor.getColumnIndex("path")));
                    Mydata.play_list1.add(cursor.getString(cursor.getColumnIndex("path")));
                    Mydata.mylist.add(map);
                }
                cursor.close();
            }catch (Exception e){

            }
        }
        System.out.println(Mydata.mylist);
    }
    public static boolean is_table_exist(String tablename,SQLiteDatabase sqLiteDatabase){//判断表是否存在
        try{
            String sql="select count(*) as c from sqlite_master where type ='table' and name =?";
            Cursor c=sqLiteDatabase.rawQuery(sql,new String[]{tablename});
            if(c.moveToNext()){
                int count=c.getInt(0);
                if(count>0){
                    c.close();
                    return true;
                }
            }
        }catch (Exception e){
        }
        return false;
    }
    public static void scan_table(String tablename, SQLiteDatabase sqLiteDatabase, List<String>list){//扫描
        if(list!=null)
            list.clear();
        Cursor cursor=sqLiteDatabase.query(tablename,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            if(cursor.getCount()>0)
                list.add(cursor.getString(cursor.getColumnIndex("name")));
        }
        cursor.close();
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
        if(newname.length()<1)
            return;
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
