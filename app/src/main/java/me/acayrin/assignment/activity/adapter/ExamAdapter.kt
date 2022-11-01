package me.acayrin.assignment.activity.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.assignment.R
import me.acayrin.assignment.dao.DAO_MonHoc
import me.acayrin.assignment.model.MonHoc
import me.acayrin.assignment.model.ThongTin

class ExamAdapter(val context: Context) : BaseAdapter() {
    private val daoMonHoc = DAO_MonHoc(context)
    private val list: ArrayList<ThongTin> = ArrayList()

    fun update(newList: ArrayList<ThongTin>?) {
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
                R.layout.component_list_item_exam,
                p2,
                false
            )

            val tvClassCourse = view.findViewById<TextView>(R.id.lv_item_exam_course)
            val tvClassDate = view.findViewById<TextView>(R.id.lv_item_exam_date)
            val tvClassDesc = view.findViewById<TextView>(R.id.lv_item_exam_description)

            view.tag = ViewStore(tvClassCourse, tvClassDate, tvClassDesc)
        }

        val viewStore = view?.tag as ViewStore
        viewStore.tvClassCourse.text = daoMonHoc[list[p0].code].name
        viewStore.tvClassDate.text = list[p0].date
        viewStore.tvClassDesc.text = list[p0].description

        return view
    }

    private class ViewStore(val tvClassCourse: TextView, val tvClassDate: TextView, val tvClassDesc: TextView)
}