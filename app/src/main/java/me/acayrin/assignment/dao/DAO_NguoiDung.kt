package me.acayrin.assignment.dao

import me.acayrin.assignment.model.NguoiDung
import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.lang.Exception
import java.util.ArrayList

class DAO_NguoiDung(context: Context?) : DAO_Base<NguoiDung>("nguoidung", context) {
    fun verify(value: NguoiDung): Boolean {
        val fetch = query("select * from %table% where username = ?", arrayOf(value.username))
        return fetch.size > 0 && fetch[0].password == value.password
    }

    fun get(value: NguoiDung): NguoiDung {
        return query("select * from %table% where username = ?", arrayOf(value.username))[0]
    }

    override fun get(value: Any?): NguoiDung {
        return query("select * from %table% where id = ?", arrayOf(value.toString()))[0]
    }

    override val all: ArrayList<NguoiDung>
        get() = query("select * from %table%", null)

    override fun insert(value: NguoiDung): Boolean {
        return if (query(
                "select * from %table% where id = ?",
                arrayOf(value.id.toString())
            ).size > 0
        ) false else this.doWork(value, Work.INSERT)
    }

    override fun update(value: NguoiDung): Boolean {
        return this.doWork(value, Work.UPDATE)
    }

    override fun delete(value: NguoiDung): Boolean {
        return this.doWork(value, Work.DELETE)
    }

    override fun query(sql: String?, params: Array<String>?): ArrayList<NguoiDung> {
        val arrayList = ArrayList<NguoiDung>()
        val sqLiteDatabase = appDatabase.readableDatabase
        val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getInt(0)
                    val username = cursor.getString(1)
                    val password = cursor.getString(2)
                    val fname = cursor.getString(3)
                    arrayList.add(NguoiDung(id, fname, username, password))
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e(table, e.message!!)
        } finally {
            if (!cursor.isClosed) cursor.close()
            if (sqLiteDatabase.isOpen) sqLiteDatabase.close()
        }
        return arrayList
    }

    override fun doWork(nguoiDung: NguoiDung, work: Work?): Boolean {
        return try {
            val sqLiteDatabase = appDatabase.writableDatabase
            when (work) {
                Work.INSERT -> {
                    sqLiteDatabase.insert(table, null, getContentValues(nguoiDung))
                }
                Work.DELETE -> {
                    sqLiteDatabase.delete(table, "id = ?", arrayOf(nguoiDung.id.toString()))
                }
                Work.UPDATE -> {
                    sqLiteDatabase.update(
                        table,
                        getContentValues(nguoiDung),
                        "id = ?",
                        arrayOf(nguoiDung.id.toString())
                    )
                }
                else -> {
                    sqLiteDatabase.insert(table, null, getContentValues(nguoiDung))
                }
            }
            if (sqLiteDatabase.isOpen) sqLiteDatabase.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getContentValues(nguoiDung: NguoiDung): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("id", nguoiDung.id)
        contentValues.put("name", nguoiDung.fname)
        contentValues.put("username", nguoiDung.username)
        contentValues.put("password", nguoiDung.password)
        return contentValues
    }
}