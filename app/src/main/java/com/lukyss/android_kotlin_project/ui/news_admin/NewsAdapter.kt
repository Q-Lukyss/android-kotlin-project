package com.lukyss.android_kotlin_project.ui.news_admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukyss.android_kotlin_project.database.http.models.News
import com.lukyss.android_kotlin_project.databinding.ItemNewsAdminBinding
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(
    private var newsList: List<News>,
    private val onEditClick: (News) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size

    fun updateNews(newNewsList: List<News>) {
        newsList = newNewsList
        notifyDataSetChanged()
    }

    inner class NewsViewHolder(private val binding: ItemNewsAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        fun bind(news: News) {
            binding.tvNewsTitle.text = news.titre
            binding.tvNewsContent.text = news.contenu
            binding.tvNewsDate.text = "Publié le : ${dateFormat.format(news.date)}"

            binding.btnEditNews.setOnClickListener { onEditClick(news) }
            binding.btnDeleteNews.setOnClickListener { onDeleteClick(news.uid) }
        }
    }
}
