package com.example.localdbdemo.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 28/6/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    public DBHelper(Context context) {
        super(context, "LocalDB", null, 33);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void execute(String statement) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(statement);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            db = null;
        }
    }

    public Cursor query(String statement) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = null;
        try {
            cur = db.rawQuery(statement, null);
            cur.moveToPosition(-1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            db = null;
        }
        return cur;
    }

    public void upgrade(int level) {
        switch (level) {
            case 0:
                doUpgrade1();
                break;
        }
    }

    private void doUpgrade1() {
        this.execute("CREATE TABLE Employee(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, date TEXT, image TEXT)");
    }
}
