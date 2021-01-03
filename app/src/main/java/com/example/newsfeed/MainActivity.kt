package com.example.newsfeed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import androidx.browser.customtabs.CustomTabsIntent

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity(), NewsItemClicked {
    private lateinit var madapter: NewsListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        var mySwipeRefreshLayout = findViewById<View>(R.id.swiperefresh) as SwipeRefreshLayout
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetch()
        madapter = NewsListAdapter(this)
        recyclerView.adapter = madapter
        mySwipeRefreshLayout.setOnRefreshListener {

            fetch()
            madapter.notifyDataSetChanged()
            mySwipeRefreshLayout.isRefreshing = false
        }

    }

    private fun fetch() {

        val url = "https://81977a6bcf09.ngrok.io"


        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->

                val newsJsonArray = response.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author")
                        , newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )

                    newsArray.add(news)

                }
                madapter.updateNews(newsArray)


            },
            Response.ErrorListener {
                Toast.makeText(this, "error $it", Toast.LENGTH_LONG).show()
            })


        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
}
