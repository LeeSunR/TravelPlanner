package com.leesunr.travelplanner.DBHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.leesunr.travelplanner.model.Group
import com.leesunr.travelplanner.model.Message

class GroupDBHelper(context:Context): SQLiteOpenHelper(context,
    DB_NAME, null,
    DB_VERSION
){

    companion object{
        private const val DB_VERSION = 1
        private const val DB_NAME = "TRAVELGROUP"
        private const val TABLE_NAME = "TRAVELGROUP"
        private const val GNO = "gno"
        private const val CONFIRMED= "confirmed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =("CREATE TABLE $TABLE_NAME(" +
                "$GNO INTEGER PRIMARY KEY," +
                "$CONFIRMED BOOLEAN DEFAULT 0)")

        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    fun insert(group: Group, confirmed:Boolean) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(GNO,group.gno)
        contentValues.put(CONFIRMED,confirmed)
        db.insert(TABLE_NAME,null ,contentValues)
    }

    fun update(gno:Int) {
        val updateQueryHandler = "UPDATE $TABLE_NAME SET $CONFIRMED=0 WHERE $GNO = gno"
        Log.e("select",updateQueryHandler)
        val cursor = writableDatabase.rawQuery(updateQueryHandler,null)
    }


    fun deleteAll(){
        writableDatabase.delete(TABLE_NAME,"",null)
    }

    fun unconfirmed(gno:Int):Boolean{
        val selectQueryHandler = "SELECT * FROM $TABLE_NAME WHERE $GNO=$gno AND $CONFIRMED=0"
        val cursor = writableDatabase.rawQuery(selectQueryHandler,null)
        return cursor.count != 0
    }

}