package me.acayrin.assignment.dao

import android.content.Context
import me.acayrin.assignment.sqlite.Database
import java.util.ArrayList

abstract class DAO_Base<T>(protected val table: String, context: Context?) {
    protected val appDatabase: Database

    init {
        appDatabase = Database(context!!)
    }

    abstract operator fun get(value: Any?): T

    abstract val all: ArrayList<T>?

    abstract fun insert(value: T): Boolean

    abstract fun update(value: T): Boolean

    abstract fun delete(value: T): Boolean

    abstract fun query(sql: String?, params: Array<String>?): ArrayList<T>

    abstract fun doWork(value: T, work: Work?): Boolean

    enum class Work {
        INSERT, DELETE, UPDATE
    }
}