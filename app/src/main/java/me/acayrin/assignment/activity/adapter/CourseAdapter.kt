package me.acayrin.assignment.activity.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.assignment.R
import me.acayrin.assignment.dao.DAO_DangKy
import me.acayrin.assignment.dao.DAO_MonHoc
import me.acayrin.assignment.model.MonHoc

class CourseAdapter(val context: Context) : BaseAdapter() {
    private var daoDangky: DAO_DangKy = DAO_DangKy(context)
    private val list: ArrayList<MonHoc> = ArrayList()

    fun update(newList: ArrayList<MonHoc>?) {
        if (newList != null) {
            list.clear()
            list.addAll(newList)
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1

        if (view == null) {
            view = (context as Activity).layoutInflater.inflate(
                if (chkIfCourseJoined(p0))
                    R.layout.component_list_item_course_joined
                else
                    R.layout.component_list_item_course,
                p2,
                false
            )

            val tvCourseName = view.findViewById<TextView>(R.id.lv_item_course_name)
            val tvCourseTeacher = view.findViewById<TextView>(R.id.lv_item_course_teacher)

            view.tag = ViewStore(tvCourseName, tvCourseTeacher)
        }

        val viewStore = view?.tag as ViewStore
        viewStore.tvCourseName.text = list[p0].name
        viewStore.tvCourseTeacher.text = "Head teacher: ${list[p0].teacher}"
        // TODO: replace

        return view
    }

    private fun chkIfCourseJoined(code: Int): Boolean {
        return daoDangky.all.any {
            it.id == context.getSharedPreferences("fpt_user", Context.MODE_PRIVATE)
                .getInt("id", -1) && it.code == code
        }
    }

    private class ViewStore(val tvCourseName: TextView, val tvCourseTeacher: TextView)
}