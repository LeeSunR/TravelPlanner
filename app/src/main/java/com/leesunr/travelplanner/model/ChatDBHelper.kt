package com.leesunr.travelplanner.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ChatDBHelper(context:Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){

    companion object{
        private val DB_VERSION = 2
        private val DB_NAME = "TRAVELPLANNER"
        private val TABLE_NAME = "CHAT"
        private val CNO = "cno"
        private val GNO = "gno"
        private val ID = "id"
        private val TIMESTAMP = "timestamp"
        private val NICKNAME = "nickname"
        private val MESSAGE = "message"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =("CREATE TABLE $TABLE_NAME(" +
                "$CNO INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$GNO INTEGER," +
                "$ID TEXT," +
                "$TIMESTAMP LONG," +
                "$NICKNAME TEXT," +
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
        writableDatabase.close()
        return list
    }

}