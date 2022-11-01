package me.acayrin.assignment.dao

import me.acayrin.assignment.model.MonHoc
import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.lang.Exception
import java.util.ArrayList

class DAO_MonHoc(context: Context?) : DAO_Base<MonHoc>("monhoc", context) {
    override fun get(value: Any?): MonHoc {
        return query(
            "select * from %table% where code = ?",
            arrayOf(value.toString())
        )[0]
    }

    override val all: ArrayList<MonHoc>
        get() = query("select * from %table%", null)

    override fun insert(value: MonHoc): Boolean {
        return if (query(
                "select * from %table% where code = ?",
                arrayOf(value.code.toString())
            )!!.size > 0
        ) false else this.doWork(value, Work.INSERT)
    }

    override fun update(value: MonHoc): Boolean {
        return this.doWork(value, Work.UPDATE)
    }

    override fun delete(value: MonHoc): Boolean {
        return this.doWork(value, Work.DELETE)
    }

    override fun query(sql: String?, params: Array<String>?): ArrayList<MonHoc> {
        val arrayList = ArrayList<MonHoc>()
        val sqLiteDatabase = appDatabase.readableDatabase
        val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val code = cursor.getInt(0)
                    val name = cursor.getString(1)
                    val teacher = cursor.getString(2)
                    arrayList.add(MonHoc(code, name, teacher))
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

    override fun doWork(MonHoc: MonHoc, work: Work?): Boolean {
        return try {
            val sqLiteDatabase = appDatabase.writableDatabase
            when (work) {
                Work.INSERT -> {
                    sqLiteDatabase.insert(table, null, getContentValues(MonHoc))
                }
                Work.DELETE -> {
                    sqLiteDatabase.delete(table, "code = ?", arrayOf(MonHoc.code.toString()))
                }
                Work.UPDATE -> {
                    sqLiteDatabase.update(
                        table,
                        getContentValues(MonHoc),
                        "code = ?",
                        arrayOf(MonHoc.code.toString())
                    )
                }
                else -> {
                    sqLiteDatabase.insert(table, null, getContentValues(MonHoc))
                }
            }
            if (sqLiteDatabase.isOpen) sqLiteDatabase.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getContentValues(MonHoc: MonHoc): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("code", MonHoc.code)
        contentValues.put("name", MonHoc.name)
        contentValues.put("teacher", MonHoc.teacher)
        return contentValues
    }
}