package com.example.ascassignment.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.ascassignment.Misc.Hashing;
import com.example.ascassignment.Table.Credential;

import java.util.ArrayList;

public class Credentials extends SQLiteOpenHelper {
    private static final String TAG = "Credentials";
    private static Credentials instance = null;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Credentials.db";
    private Credentials(Context context) {
        //the real constructor
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    synchronized public static Credentials getInstance(Context context){
        //singleton object to prevent multiple access to database at a time
        //will only call this function from other classes
        if (instance == null)
            instance = new Credentials(context);

        return instance;
    }

    //region Table Definition
    private static class Column implements BaseColumns {
        public static final String TABLE_NAME = "Credentials";
        public static final String LOGIN_NAME = "login_name";
        public static final String PASSWORD = "password";
        public static final String LAST_LOGIN = "last_login";
        public static final String ROLE = "role";
        public static final String EMP_ID = "emp_id";
        public static final String MOD_USR = "mod_usr";
        public static final String LAST_MODIFIED = "last_modified";
    }
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Column.TABLE_NAME + " (" +
                    Column._ID + " INTEGER PRIMARY KEY," +
                    Column.LOGIN_NAME + " TEXT NOT NULL," +
                    Column.PASSWORD + " TEXT NOT NULL," +
                    Column.ROLE + " TEXT NOT NULL," +
                    Column.EMP_ID + " TEXT NOT NULL," +
                    Column.LAST_LOGIN + " TEXT NOT NULL," +
                    Column.LAST_MODIFIED + " TEXT NOT NULL," +
                    Column.MOD_USR + " TEXT NOT NULL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Column.TABLE_NAME;
    //endregion

    //region Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    //endregion

    public ArrayList<Credential> getAllCredentials(){
        ArrayList<Credential> ret = new ArrayList<>();
        Credential temp = null;
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(Column.TABLE_NAME, null, null, null, null, null, null)) {
            if ((cursor != null) && (cursor.getCount() > 0)) { //if there is data, process it
                cursor.moveToFirst();
                do{
                    temp = new Credential();
                    temp.id = cursor.getInt(0);
                    temp.name = cursor.getString(1);
                    temp.password = cursor.getString(2);
                    temp.role = cursor.getString(3);
                    temp.empID = cursor.getString(4);
                    temp.lastLogin = cursor.getString(5);
                    temp.lastModified = cursor.getString(6);
                    temp.modUsr = cursor.getString(7);

                    ret.add(temp);
                } while(cursor.moveToNext());

            } else{
                Log.d(TAG, "getAllCredentials: No data in db");
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllCredentials: ", e);
        }
        return ret;
    }

    public Long insertCredentials(Credential credential){
        SQLiteDatabase db = getWritableDatabase();
        Long newRowId = null;

        ContentValues values = new ContentValues();
        values.put(Column.LOGIN_NAME, credential.name);
        values.put(Column.PASSWORD, credential.password);
        values.put(Column.ROLE, credential.role);
        values.put(Column.EMP_ID, credential.empID);
        values.put(Column.LAST_LOGIN, "0");
        values.put(Column.LAST_MODIFIED, credential.lastModified);
        values.put(Column.MOD_USR, credential.modUsr);

        try {
            // return value is the primary key
            newRowId = db.insert(Column.TABLE_NAME, null, values);
        } catch (Exception e){
            newRowId = (long) -2;
            Log.e(TAG, "insertCredentials: ", e);
        }

        return newRowId;
    }

    public int updateCredentials(Credential credential, int empID){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Column.LOGIN_NAME, credential.name);
        values.put(Column.PASSWORD, credential.password);
        values.put(Column.ROLE, credential.role);
        values.put(Column.LAST_LOGIN, credential.lastLogin);
        values.put(Column.LAST_MODIFIED, credential.lastModified);
        values.put(Column.MOD_USR, credential.modUsr);

        String selection = Column.EMP_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(empID)};

        return db.update(
                Column.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public int deleteCredentials(String empID){
        SQLiteDatabase db = getWritableDatabase();

        String selection = Column.EMP_ID + " LIKE ?";
        String[] selectionArgs = { empID };

        return db.delete(Column.TABLE_NAME, selection, selectionArgs);

    }

    public Credential loginCheck(String username, String password){
        SQLiteDatabase db = getReadableDatabase();
        Credential ret = null;

        String selection = Column.LOGIN_NAME + " LIKE ? AND " + Column.PASSWORD + " LIKE ? ";
        String[] selectionArgs = { username, password };

        try(Cursor cursor = db.query(Column.TABLE_NAME, null, selection, selectionArgs, null, null, null)){
            if ((cursor != null) && (cursor.getCount() > 0)){
                cursor.moveToFirst();
                ret = new Credential();
                ret.id = cursor.getInt(0);
                ret.name = cursor.getString(1);
                ret.password = cursor.getString(2);
                ret.role = cursor.getString(3);
                ret.empID = cursor.getString(4);
                ret.lastLogin = cursor.getString(5);
                ret.lastModified = cursor.getString(6);
                ret.modUsr = cursor.getString(7);
            }
        } catch(Exception e){
            Log.e(TAG, "loginCheck: ", e);
        }

        return ret;
    }

    public Credential searchEmployee(String EmpID){
        SQLiteDatabase db = getWritableDatabase();
        Credential ret = null;

        String selection = Column.EMP_ID + " LIKE ? ";
        String[] selectionArgs = { EmpID };

        try(Cursor cursor = db.query(Column.TABLE_NAME, null, selection, selectionArgs, null, null, null)){
            if ((cursor != null) && (cursor.getCount() > 0)){
                cursor.moveToFirst();
                ret = new Credential();
                ret.id = cursor.getInt(0);
                ret.name = cursor.getString(1);
                ret.password = cursor.getString(2);
                ret.role = cursor.getString(3);
                ret.empID = cursor.getString(4);
                ret.lastLogin = cursor.getString(5);
                ret.lastModified = cursor.getString(6);
                ret.modUsr = cursor.getString(7);
            }
        } catch(Exception e){
            Log.e(TAG, "searchEmployee: ", e);
        }

        return ret;
    }

    public int updateLastLogin(String lastLogin, String empID){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Column.LAST_LOGIN, lastLogin);

        String selection = Column.EMP_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(empID)};

        return db.update(
                Column.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
}
