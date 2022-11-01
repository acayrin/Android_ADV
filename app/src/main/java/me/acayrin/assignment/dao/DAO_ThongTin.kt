package me.acayrin.assignment.dao

import me.acayrin.assignment.model.ThongTin
import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.lang.Exception
import java.util.ArrayList

class DAO_ThongTin(context: Context?) : DAO_Base<ThongTin>("thongtin", context) {
    override fun get(value: Any?): ThongTin {
        return query("select * from %table% where id = ?", arrayOf((value as ThongTin).id.toString()))[0]
    }

    override val all: ArrayList<ThongTin>
        get() = query("select * from %table%", null)

    override fun insert(value: ThongTin): Boolean {
        return if (query(
                "select * from %table% where id = ?",
                arrayOf(value.id.toString())
            ).size > 0
        ) false else this.doWork(value, Work.INSERT)
    }

    override fun update(value: ThongTin): Boolean {
        return this.doWork(value, Work.UPDATE)
    }

    override fun delete(value: ThongTin): Boolean {
        return this.doWork(value, Work.DELETE)
    }

    override fun query(sql: String?, params: Array<String>?): ArrayList<ThongTin> {
        val arrayList = ArrayList<ThongTin>()
        val sqLiteDatabase = appDatabase.readableDatabase
        val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getInt(0)
                    val code = cursor.getInt(1)
                    val type = cursor.getInt(2)
                    val desc = cursor.getString(3)
                    val date = cursor.getString(4)
                    val address = cursor.getString(5)
                    arrayList.add(ThongTin(id, code, type, desc, date, address))
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

    override fun doWork(ThongTin: ThongTin, work: Work?): Boolean {
        return try {
            val sqLiteDatabase = appDatabase.writableDatabase
            when (work) {
                Work.INSERT -> {
                    sqLiteDatabase.insert(table, null, getContentValues(ThongTin))
                }
                Work.DELETE -> {
                    sqLiteDatabase.delete(table, "id = ?", arrayOf(ThongTin.id.toString()))
                }
                Work.UPDATE -> {
                    sqLiteDatabase.update(
                        table,
                        getContentValues(ThongTin),
                        "id = ?",
                        arrayOf(ThongTin.id.toString())
                    )
                }
                else -> {
                    sqLiteDatabase.insert(table, null, getContentValues(ThongTin))
                }
            }
            if (sqLiteDatabase.isOpen) sqLiteDatabase.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getContentValues(ThongTin: ThongTin): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("id", ThongTin.id)
        contentValues.put("code", ThongTin.code)
        contentValues.put("description", ThongTin.description)
        contentValues.put("date", ThongTin.date)
        contentValues.put("room", ThongTin.room)
        return contentValues
    }
}