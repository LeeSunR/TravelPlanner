package com.leesunr.travelplanner.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ChatDBHelper(context:Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){

    companion object{
        private const val DB_VERSION = 10
        private const val DB_NAME = "TRAVELPLANNER"
        private const val TABLE_NAME = "CHAT"
        private const val CNO = "cno"
        private const val GNO = "gno"
        private const val ID = "id"
        private const val TIMESTAMP = "timestamp"
        private const val NICKNAME = "nickname"
        private const val MESSAGE = "message"
        private const val PHOTOURL = "photourl"
        private const val CONFIRMED= "confirmed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =("CREATE TABLE $TABLE_NAME(" +
                "$CNO INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$GNO INTEGER," +
                "$ID TEXT," +
                "$TIMESTAMP LONG," +
                "$NICKNAME TEXT," +
                "$PHOTOURL TEXT," +
                "$CONFIRMED BOOLEAN DEFAULT 0," +
                "$MESSAGE TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    fun insert(message:Message,confirmed:Boolean) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CNO,message.cno)
        contentValues.put(GNO,message.gno)
        contentValues.put(ID,message.id)
        contentValues.put(TIMESTAMP,message.timestamp)
        contentValues.put(NICKNAME,message.nickname)
        contentValues.put(MESSAGE,message.message)
        contentValues.put(PHOTOURL,message.photourl)
        contentValues.put(CONFIRMED,confirmed)
        db.insert(TABLE_NAME ,null ,contentValues)
        Log.e("insert","insert")

    }

    fun select(gno:Int):ArrayList<Message>{
        var list = ArrayList<Message>()

        val selectQueryHandler = "SELECT * FROM $TABLE_NAME WHERE $GNO=$gno"
        Log.e("select",selectQueryHandler)
        val cursor = writableDatabase.rawQuery(selectQueryHandler,null)
        if (cursor.moveToFirst()){
            do{
                val message = Message()
                message.timestamp = cursor.getLong(cursor.getColumnIndex(TIMESTAMP))
                message.nickname = cursor.getString(cursor.getColumnIndex(NICKNAME))
                message.message = cursor.getString(cursor.getColumnIndex(MESSAGE))
                message.gno = cursor.getInt(cursor.getColumnIndex(GNO))
                message.cno = cursor.getInt(cursor.getColumnIndex(CNO))
                message.id = cursor.getString(cursor.getColumnIndex(ID))
                message.photourl = cursor.getString(cursor.getColumnIndex(PHOTOURL))
                list.add(message)
            }while (cursor.moveToNext())
        }
        val contentValues = ContentValues()
        contentValues.put(CONFIRMED,1)
        writableDatabase.update(TABLE_NAME,contentValues,"",null)
        return list
    }

    fun deleteAll(){
        writableDatabase.delete(TABLE_NAME,"",null)
    }

    fun unconfirmedMessage(gno:Int):Boolean{
        val selectQueryHandler = "SELECT * FROM $TABLE_NAME WHERE $GNO=$gno AND $CONFIRMED=0"
        val cursor = writableDatabase.rawQuery(selectQueryHandler,null)
        return cursor.count != 0
    }

}