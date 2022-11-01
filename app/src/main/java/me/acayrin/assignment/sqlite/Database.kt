package me.acayrin.assignment.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) : SQLiteOpenHelper(context, "SQL", null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS nguoidung(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT," +
                    "password TEXT," +
                    "name TEXT)"
        )

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS dangky(" +
                    "id INTEGER," +
                    "code INTEGER," +
                    "FOREIGN KEY (id) REFERENCES nguoidung(id)," +
                    "FOREIGN KEY (code) REFERENCES monhoc(code))"
        )

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS monhoc(" +
                    "code INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "teacher TEXT)"
        )

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS thongtin(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "code INTEGER REFERENCES monhoc(code)," +
                    "type INTEGER," +
                    "description TEXT," +
                    "date TEXT," +
                    "room TEXT)"
        )

        // dummy data
        sqLiteDatabase.execSQL(
            "INSERT INTO nguoidung VALUES" +
                    "(0, 1, 1, 'User 1')"
        )

        sqLiteDatabase.execSQL(
            "INSERT INTO monhoc VALUES" +
                    "(0, 'Test course 1', 'Teacher')," + // class A
                    "(1, 'Test course 2', 'Teacher')," + // class B
                    "(2, 'Test course 3', 'Teacher')"    // class C
        )

        sqLiteDatabase.execSQL(
            "INSERT INTO thongtin VALUES" +
                    "(0, 0, 0, 'Description 1', '1/1/1970', 'P101')," + // class A - type Normal
                    "(1, 2, 1, 'Description 2', '2/2/1970', 'P202')," + // class C - type Exam
                    "(2, 2, 0, 'Description 3', '3/3/1970', 'P303')," + // class C - type normal
                    "(3, 1, 1, 'Description 4', '4/4/1970', 'P404')"    // class B - type Exam
        )

        sqLiteDatabase.execSQL(
            "INSERT INTO dangky VALUES" +
                    "(0, 0),"+
                    "(0, 1)"
        )
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS nguoidung")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS dangky")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS monhoc")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS thongtin")
        onCreate(sqLiteDatabase)
    }
}