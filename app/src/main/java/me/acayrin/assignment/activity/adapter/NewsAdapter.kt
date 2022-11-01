package me.acayrin.assignment.activity.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import me.acayrin.assignment.R
import me.acayrin.assignment.service.NewsService

class NewsAdapter(val context: Context): BaseAdapter() {
    val list: ArrayList<NewsService.NewsEntry> = ArrayList()

    fun update(newList: ArrayList<NewsService.NewsEntry>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): NewsService.NewsEntry {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            val layoutInflater = (context as Activity).layoutInflater
            view = layoutInflater.inflate(R.layout.component_list_item_news, parent, false)

            val tvNewsTitle = view.findViewById<TextView>(R.id.lv_item_news_title)
            val tvNewsPubDate = view.findViewById<TextView>(R.id.lv_item_news_pubdate)

            view.tag = ViewStore(tvNewsTitle, tvNewsPubDate)
        }

        val viewStore = view?.tag as ViewStore
        viewStore.tvNewsTitle.text = list[position].title
        viewStore.tvNewsPubDate.text = list[position].pubDate

        return view
    }

    private class ViewStore(val tvNewsTitle: TextView, val tvNewsPubDate: TextView)
}