package com.lukyss.android_kotlin_project.ui.news_admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lukyss.android_kotlin_project.database.http.models.News
import com.lukyss.android_kotlin_project.databinding.FragmentNewsBinding
import com.lukyss.android_kotlin_project.viewmodels.NewsViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())

    private var editingNews: News? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        // Initialisation du RecyclerView
        newsAdapter = NewsAdapter(emptyList(), ::startEditingNews, ::onDeleteNews)
        binding.rvNewsAdmin.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNewsAdmin.adapter = newsAdapter

        binding.etNewsDate.setOnClickListener {
            showDateTimePicker()
        }

        // Observer les news et mettre à jour la liste
        newsViewModel.news.observe(viewLifecycleOwner, Observer { newsList ->
            newsAdapter.updateNews(newsList)
        })

        newsViewModel.loadingNews.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressNews.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        newsViewModel.errorNews.observe(viewLifecycleOwner, Observer { errorMsg ->
            binding.tvErrorNews.visibility = if (errorMsg != null) View.VISIBLE else View.GONE
            binding.tvErrorNews.text = errorMsg
        })

        // Charger les news
        newsViewModel.fetchNews()

        // Gestion du bouton d'ajout de news
        binding.btnAddNews.setOnClickListener {
            onAddNews()
        }

        // Gestion du bouton de mise à jour
        binding.btnUpdateNews.setOnClickListener {
            onUpdateNews()
        }

        // Gestion du bouton d'annulation
        binding.btnCancelEditing.setOnClickListener {
            cancelEditingNews()
        }
    }

    private fun onAddNews() {
        val titre = binding.etNewsTitle.text.toString().trim()
        val contenu = binding.etNewsContent.text.toString().trim()
        val dateString = binding.etNewsDate.text.toString().trim()

        if (titre.isNotEmpty() && contenu.isNotEmpty() && dateString.isNotEmpty()) {
            try {
                val date = dateFormat.parse(dateString) ?: Date()
                val news = News(uid = "", titre = titre, contenu = contenu, date = date, isActive = true)
                newsViewModel.addNews(news)
                resetForm()
            } catch (e: ParseException) {
                Toast.makeText(requireContext(), "Format de date invalide", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDeleteNews(newsId: String) {
        newsViewModel.deleteNews(newsId)
    }

    private fun startEditingNews(news: News) {
        editingNews = news
        binding.etNewsTitle.setText(news.titre)
        binding.etNewsContent.setText(news.contenu)
        binding.etNewsDate.setText(dateFormat.format(news.date))

        binding.btnAddNews.visibility = View.GONE
        binding.btnUpdateNews.visibility = View.VISIBLE
        binding.btnCancelEditing.visibility = View.VISIBLE
    }

    private fun onUpdateNews() {
        if (editingNews != null) {
            try {
                val updatedNews = editingNews!!.copy(
                    titre = binding.etNewsTitle.text.toString().trim(),
                    contenu = binding.etNewsContent.text.toString().trim(),
                    date = dateFormat.parse(binding.etNewsDate.text.toString()) ?: Date()
                )
                newsViewModel.updateNews(updatedNews)
                editingNews = null
                resetForm()
            } catch (e: ParseException) {
                Toast.makeText(requireContext(), "Format de date invalide", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelEditingNews() {
        editingNews = null
        resetForm()
    }

    private fun resetForm() {
        binding.etNewsTitle.text.clear()
        binding.etNewsContent.text.clear()
        binding.etNewsDate.text.clear()

        binding.btnAddNews.visibility = View.VISIBLE
        binding.btnUpdateNews.visibility = View.GONE
        binding.btnCancelEditing.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val timePicker = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                val selectedDate = dateFormat.format(calendar.time)
                binding.etNewsDate.setText(selectedDate)  // Met à jour le champ avec la bonne date
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

            timePicker.show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }
}
