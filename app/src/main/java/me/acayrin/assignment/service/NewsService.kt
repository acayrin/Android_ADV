package me.acayrin.assignment.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import org.json.JSONObject
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

class NewsService : Service() {
    private val url = "https://apis.dinhnt.com/edu.json"
    private val newsArrayList: ArrayList<NewsEntry> = ArrayList()

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // register receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("fpt.news.get")
        registerReceiver(broadcastReceiver, intentFilter)

        fetch() // first run

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                fetch()
            }
        }, 0L, 60_000L)

        return super.onStartCommand(intent, flags, startId)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "fpt.news.get" -> {
                    Log.i("Service::News", "Received news request")

                    val responseIntent = Intent()
                    val responseBundle = Bundle()
                    responseBundle.putSerializable("list", newsArrayList)
                    responseIntent.action = "fpt.news.set"
                    responseIntent.putExtras(responseBundle)
                    sendBroadcast(responseIntent)
                }
            }
        }
    }

    // fetch news
    private fun fetch() {
        try {
            val response = String(URL(url).openStream().readBytes(), StandardCharsets.UTF_8)

            val jsonObject = JSONObject(response).getJSONObject("channel")
            val newsList = jsonObject.getJSONArray("item")

            // clear
            newsArrayList.clear()

            for (i in 0 until newsList.length()) {
                val itemJsonObject = newsList.getJSONObject(i)

                newsArrayList.add(
                    NewsEntry(
                        itemJsonObject.getString("title"),
                        itemJsonObject.getJSONObject("description").getString("__cdata")
                            .replace(Regex("<.+>"), ""),
                        itemJsonObject.getString("pubDate"),
                        itemJsonObject.getString("link"),
                        itemJsonObject.getString("guid")
                    )
                )
            }

            Log.i("Service::News", "Fetched total ${newsList.length()} news articles")
        } catch (e: Exception) {
            e.message?.let { Log.e("Service::News", it) }
        }
    }

    class NewsEntry(
        val title: String,
        val description: String,
        val pubDate: String,
        val link: String,
        val guid: String
    ) : java.io.Serializable
}