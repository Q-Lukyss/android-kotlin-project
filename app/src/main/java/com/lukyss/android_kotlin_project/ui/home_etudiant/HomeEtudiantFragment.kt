package com.lukyss.android_kotlin_project.ui.home_etudiant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukyss.android_kotlin_project.databinding.FragmentHomeEtudiantBinding
import com.lukyss.android_kotlin_project.viewmodels.*
import java.text.SimpleDateFormat
import java.util.*

class HomeEtudiantFragment : Fragment() {

    private var _binding: FragmentHomeEtudiantBinding? = null
    private val binding get() = _binding!!

    // Déclaration des ViewModels
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var presenceViewModel: PresenceViewModel
    private lateinit var seanceViewModel: SeanceViewModel
    private lateinit var coursViewModel: CoursViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeEtudiantBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Instanciation des ViewModels
        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        presenceViewModel = ViewModelProvider(this).get(PresenceViewModel::class.java)
        seanceViewModel = ViewModelProvider(this).get(SeanceViewModel::class.java)
        coursViewModel = ViewModelProvider(this).get(CoursViewModel::class.java)

        // Observer les News
        newsViewModel.news.observe(viewLifecycleOwner) { newsList ->
            binding.newsContent.text = newsList.joinToString("\n") { "${it.titre} : ${it.contenu}" }
        }
        newsViewModel.loadingNews.observe(viewLifecycleOwner) { isLoading ->
            binding.newsProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        newsViewModel.errorNews.observe(viewLifecycleOwner) { error ->
            binding.newsError.text = error ?: ""
            binding.newsError.visibility = if (error != null) View.VISIBLE else View.GONE
        }

        // Observer la moyenne générale
        noteViewModel.averageGrade.observe(viewLifecycleOwner) { avg ->
            binding.averageGradeText.text = String.format("%.2f", avg)
        }
        noteViewModel.loadingGlobalNotes.observe(viewLifecycleOwner) { isLoading ->
            binding.averageGradeProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        noteViewModel.errorGlobalNotes.observe(viewLifecycleOwner) { error ->
            binding.averageGradeError.text = error ?: ""
            binding.averageGradeError.visibility = if (error != null) View.VISIBLE else View.GONE
        }

        // Observer les présences/absences
        presenceViewModel.nbPresences.observe(viewLifecycleOwner) { count ->
            binding.nbPresencesText.text = count.toString()
        }
        presenceViewModel.nbAbsences.observe(viewLifecycleOwner) { count ->
            binding.nbAbsencesText.text = count.toString()
        }
        presenceViewModel.loadingPresence.observe(viewLifecycleOwner) { isLoading ->
            binding.presenceProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        presenceViewModel.errorPresence.observe(viewLifecycleOwner) { error ->
            binding.presenceError.text = error ?: ""
            binding.presenceError.visibility = if (error != null) View.VISIBLE else View.GONE
        }

        // Observer les séances à venir
        seanceViewModel.upcomingSeances.observe(viewLifecycleOwner) { upcoming ->
            if (upcoming.isNotEmpty()) {
                val sorted = upcoming.sortedBy { it.date }
                val nextDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(sorted.first().date)
                binding.nextCourseDayKey.text = "Séances pour le $nextDay :"

                val nextDaySeances = upcoming.filter { seance ->
                    nextDay == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(seance.date)
                }
                binding.nextCourseSeancesContent.text = nextDaySeances.joinToString("\n") { seance ->
                    val timeStart = SimpleDateFormat("HH:mm", Locale.getDefault()).format(seance.date)
                    val timeEnd = SimpleDateFormat("HH:mm", Locale.getDefault()).format(seance.dateFin)
                    "Cours: ${seance.idCours} à ${seance.lieu}, de $timeStart à $timeEnd"
                }
            } else {
                binding.nextCourseDayKey.text = "Aucune séance à venir"
                binding.nextCourseSeancesContent.text = ""
            }
        }
        seanceViewModel.errorSeances.observe(viewLifecycleOwner) { error ->
            binding.seanceError.text = error ?: ""
            binding.seanceError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        seanceViewModel.loadingSeances.observe(viewLifecycleOwner) { isLoading ->
            binding.seanceProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Exemple d'ID utilisateur (à adapter)
        val currentUserId = getCurrentUserId()

        // Charger les données
        newsViewModel.fetchNews()
        noteViewModel.fetchNotesForUser(currentUserId)
        presenceViewModel.fetchPresencesForUser(currentUserId)
        coursViewModel.fetchFormationAndCoursesForUser(currentUserId)

        // Lorsque les cours sont chargés, charger les séances à venir
        coursViewModel.courses.observe(viewLifecycleOwner) { courses ->
            if (courses.isNotEmpty()) {
                val courseIds = courses.map { it.uid }
                seanceViewModel.fetchUpcomingSeancesForCourses(courseIds)
            }
        }

        return root
    }

    private fun getCurrentUserId(): String {
        return "exempleUserId"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
