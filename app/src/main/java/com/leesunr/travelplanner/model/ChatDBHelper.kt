package com.leesunr.travelplanner.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ChatDBHelper(context:Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){

    companion object{
        private const val DB_VERSION = 7
        private const val DB_NAME = "TRAVELPLANNER"
        private const val TABLE_NAME = "CHAT"
        private const val CNO = "cno"
        private const val GNO = "gno"
        private const val ID = "id"
        private const val TIMESTAMP = "timestamp"
        private const val NICKNAME = "nickname"
        private const val MESSAGE = "message"
        private const val CONFIRMED= "confirmed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =("CREATE TABLE $TABLE_NAME(" +
                "$CNO INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$GNO INTEGER," +
                "$ID TEXT," +
                "$TIMESTAMP LONG," +
                "$NICKNAME TEXT," +
                "$CONFIRMED BOOLEAN DEFAULT FALSE," +
                "$MESSAGE TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    fun insert(message:Message) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CNO,message.cno)
        contentValues.put(GNO,message.gno)
        contentValues.put(ID,message.id)
        contentValues.put(TIMESTAMP,message.timestamp)
        contentValues.put(NICKNAME,message.nickname)
        contentValues.put(MESSAGE,message.message)
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
                Log.e("select",message.message)
                list.add(message)
            }while (cursor.moveToNext())
        }
        val contentValues = ContentValues()
        contentValues.put(CONFIRMED,true)
        writableDatabase.update(TABLE_NAME,contentValues,"",null)
        return list
    }

    fun deleteAll(){
        writableDatabase.delete(TABLE_NAME,"",null)
    }

}