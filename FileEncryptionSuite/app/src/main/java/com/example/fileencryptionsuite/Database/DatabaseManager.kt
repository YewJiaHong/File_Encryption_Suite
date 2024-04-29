package com.example.fileencryptionsuite.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.androidFileEncryptionSuite.tables.Key
import java.math.BigInteger

class DatabaseManager(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "DatabaseManager"

        private const val DATABASE_NAME = "keyDb.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "keys"
        private const val COLUMN_ID = "id"
        private const val KEY_NAME = "keyName"
        private const val KEY_VALUE = "keyValue"
        private const val keyTypeCol = "keyType"

    }


    override fun onCreate(db: SQLiteDatabase?) {
        val createDatabaseQuery =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "$KEY_NAME TEXT, $KEY_VALUE TEXT, $keyTypeCol TEXT)"
        db?.execSQL(createDatabaseQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getKeyList(): ArrayList<Key> {
        val db: SQLiteDatabase = readableDatabase;
        val retList = ArrayList<Key>()

        try {
            db.query(TABLE_NAME, null, null, null, null, null, null)
                .use { cursor ->
                if (cursor != null && cursor.count > 0) { //if there is data, process it
                    cursor.moveToFirst()
                    do {
                        val keyObject = Key()
                        keyObject.idCol = cursor.getString(0)
                        keyObject.keyNameCol = cursor.getString(1)
                        keyObject.keyCol = cursor.getString(2)
                        keyObject.keyTypeCol = cursor.getString(3)
                        retList.add(keyObject)
                    } while (cursor.moveToNext())
                } else {
                    Log.d(TAG, "getKeyList: No data in db")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getKeyList: ", e)
        }

        return retList
    }

    fun changeKeyName(id: String, keyName: String): Int {
        val db: SQLiteDatabase = writableDatabase

        val contentValue = ContentValues()
        contentValue.put(KEY_NAME, keyName)

        val whereClause = "$COLUMN_ID LIKE ?"

        return db.update(TABLE_NAME, contentValue, whereClause, arrayOf(id))
    }

    fun insertNewKey(keyName: String, keyValue: BigInteger, keyType: String): Long {
        val db: SQLiteDatabase = writableDatabase
        var ret: Long

        val contentValue = ContentValues()
        contentValue.put(KEY_NAME, keyName)
        contentValue.put(KEY_VALUE, keyValue.toString())
        contentValue.put(keyTypeCol, keyType)


        try {
            // return value is the primary key
            ret = db.insert(TABLE_NAME, null, contentValue)
        } catch (e: java.lang.Exception) {
            ret = -2L
            Log.e(TAG, "insertNewKey: ", e)
        }

        return ret
    }

    fun deleteKeyFromList(id: String): Int {
        val db: SQLiteDatabase = writableDatabase

        val selection = "$COLUMN_ID LIKE ?"

        return db.delete(TABLE_NAME, selection, arrayOf(id))
    }
}