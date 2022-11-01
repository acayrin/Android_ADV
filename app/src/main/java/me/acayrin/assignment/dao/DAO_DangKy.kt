package me.acayrin.assignment.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import me.acayrin.assignment.model.DangKy

class DAO_DangKy(context: Context?) : DAO_Base<DangKy>("dangky", context) {
    override fun get(value: Any?): DangKy {
        return query(
            "select * from %table% where id = ?",
            arrayOf((value as DangKy).id.toString())
        )[0]
    }

    override val all: ArrayList<DangKy>
        get() = query("select * from %table%", null)

    override fun insert(value: DangKy): Boolean {
        return if (query(
                "select * from %table% where id = ? and code = ?",
                arrayOf(value.id.toString(), value.code.toString())
            ).size > 0
        ) false else this.doWork(value, Work.INSERT)
    }

    override fun update(value: DangKy): Boolean {
        return this.doWork(value, Work.UPDATE)
    }

    override fun delete(value: DangKy): Boolean {
        return this.doWork(value, Work.DELETE)
    }

    override fun query(sql: String?, params: Array<String>?): ArrayList<DangKy> {
        val arrayList = ArrayList<DangKy>()
        val sqLiteDatabase = appDatabase.readableDatabase
        val cursor = sqLiteDatabase.rawQuery(sql!!.replace("%table%".toRegex(), table), params)
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val id = cursor.getInt(0)
                    val code = cursor.getInt(1)
                    arrayList.add(DangKy(id, code))
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

    override fun doWork(DangKy: DangKy, work: Work?): Boolean {
        return try {
            val sqLiteDatabase = appDatabase.writableDatabase
            when (work) {
                Work.INSERT -> {
                    sqLiteDatabase.insert(table, null, getContentValues(DangKy))
                }
                Work.DELETE -> {
                    sqLiteDatabase.delete(
                        table,
                        "id = ? and code = ?",
                        arrayOf(DangKy.id.toString(), DangKy.code.toString())
                    )
                }
                Work.UPDATE -> {
                    sqLiteDatabase.update(
                        table,
                        getContentValues(DangKy),
                        "id = ? and code = ?",
                        arrayOf(DangKy.id.toString(), DangKy.code.toString())
                    )
                }
                else -> {
                    sqLiteDatabase.insert(table, null, getContentValues(DangKy))
                }
            }
            if (sqLiteDatabase.isOpen) sqLiteDatabase.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getContentValues(DangKy: DangKy): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("id", DangKy.id)
        contentValues.put("code", DangKy.code)
        return contentValues
    }
}