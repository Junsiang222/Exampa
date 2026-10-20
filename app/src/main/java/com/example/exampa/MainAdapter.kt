package com.example.exampa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 适配器:显示电影列表,支持搜索、排序、点击
class MovieAdapter(
    private val allMovies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    private var shownMovies: List<Movie> = allMovies
    private var keyword = ""
    private var sortType = 0


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvId)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvDirector: TextView = view.findViewById(R.id.tvDirector)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = shownMovies[position]
        holder.tvId.text = movie.id.toString()
        holder.tvTitle.text = movie.title
        holder.tvRating.text = "Rating: ${movie.rating}"
        holder.tvDirector.text = "Director: ${movie.director}"
        holder.tvDate.text = "Release: ${movie.release_date}"

        holder.itemView.setOnClickListener { onItemClick(movie) }
    }

    // 一共几格(用 shownMovies)
    override fun getItemCount(): Int = shownMovies.size

    // 搜索:记住关键字,然后刷新
    fun filter(text: String) {
        keyword = text
        refresh()
    }

    // 排序:记住选了哪一种,然后刷新
    fun sort(type: Int) {
        sortType = type
        refresh()
    }

    // 先搜索,再排序,最后刷新画面
    private fun refresh() {
        // 搜索:标题包含关键字(不分大小写)
        var result = allMovies.filter { it.title.contains(keyword, ignoreCase = true) }

        // 排序:0 不排序,1 标题A-Z,2 标题Z-A,3 评分高到低,4 评分低到高,5 最新,6 最旧
        result = when (sortType) {
            1 -> result.sortedBy { it.title.lowercase() }
            2 -> result.sortedByDescending { it.title.lowercase() }
            else -> result   // 不排序
        }

        shownMovies = result
        notifyDataSetChanged()
    }
}