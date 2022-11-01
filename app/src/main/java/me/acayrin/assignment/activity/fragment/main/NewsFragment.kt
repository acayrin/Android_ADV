package me.acayrin.assignment.activity.fragment.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import me.acayrin.assignment.R
import me.acayrin.assignment.activity.WebViewActivity
import me.acayrin.assignment.activity.adapter.NewsAdapter
import me.acayrin.assignment.service.NewsService

class NewsFragment : Fragment(R.layout.fragment_main_news) {
    private lateinit var listView: ListView
    private lateinit var listViewAdapter: NewsAdapter
    private lateinit var newsSwipe: SwipeRefreshLayout

    override fun onResume() {
        super.onResume()

        // register intent broadcaster
        let {
            val intentFilter = IntentFilter()
            intentFilter.addAction("fpt.news.set")
            requireContext().registerReceiver(broadcastReceiver, intentFilter)
        }

        // swipe to refresh news list
        let {
            newsSwipe = requireView().findViewById(R.id.swipe_news)
            newsSwipe.setOnRefreshListener {
                requestNewsList()
                newsSwipe.isRefreshing = false
            }
        }

        // handle list view
        let {
            // set adapter for list view
            listView = requireView().findViewById(R.id.lv_news)
            listViewAdapter = NewsAdapter(requireContext())
            listView.adapter = listViewAdapter

            // handle list view item click event
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val webViewIntent =
                        Intent(this@NewsFragment.requireContext(), WebViewActivity::class.java)
                    val webViewBundle = Bundle()
                    webViewBundle.putString("url", listViewAdapter.list[position].link)
                    webViewIntent.putExtras(webViewBundle)
                    startActivity(webViewIntent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                }
        }

        // send broadcast to fetch news list
        requestNewsList()
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "fpt.news.set" -> {
                    Log.i("Fragment::News", "Received news list from service. ${(intent.extras!!.getSerializable("list") as ArrayList<NewsService.NewsEntry>).size}")

                    listViewAdapter.update(intent.extras!!.getSerializable("list") as ArrayList<NewsService.NewsEntry>)
                }
            }
        }
    }

    private fun requestNewsList() {
        val responseIntent = Intent()
        responseIntent.action = "fpt.news.get"
        requireContext().sendBroadcast(responseIntent)
    }
}