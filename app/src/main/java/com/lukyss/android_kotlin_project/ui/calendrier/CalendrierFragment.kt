package com.lukyss.android_kotlin_project.ui.calendrier

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.lukyss.android_kotlin_project.R
import com.lukyss.android_kotlin_project.database.http.models.Cours
import com.lukyss.android_kotlin_project.database.http.models.Seances
import com.lukyss.android_kotlin_project.databinding.FragmentCalendrierBinding
import com.lukyss.android_kotlin_project.viewmodels.CoursViewModel
import com.lukyss.android_kotlin_project.viewmodels.SeanceViewModel
import com.lukyss.android_kotlin_project.database.http.services.UserService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendrierFragment : Fragment() {

    private var _binding: FragmentCalendrierBinding? = null
    private val binding get() = _binding!!

    private lateinit var seanceViewModel: SeanceViewModel
    private lateinit var coursViewModel: CoursViewModel
    private val teacherMap: MutableMap<String, String> = mutableMapOf()
    private val userService = UserService()

    // Formatters pour la date et l'heure
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Map locale des cours (clé = uid du cours, valeur = objet Cours)
    private val courseMap: MutableMap<String, Cours> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendrierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        seanceViewModel = ViewModelProvider(this).get(SeanceViewModel::class.java)
        coursViewModel = ViewModelProvider(this).get(CoursViewModel::class.java)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Appels pour récupérer formation + cours et la liste des cours
            coursViewModel.fetchFormationAndCoursesForUser(userId)
            coursViewModel.fetchCourses()
        } else {
            Log.e("CalendrierFragment", "Aucun utilisateur connecté.")
        }

        // Observer les cours pour construire courseMap et charger les enseignants
        coursViewModel.courses.observe(viewLifecycleOwner, Observer { courses ->
            Log.d("CalendrierFragment", "Nombre de cours = ${courses.size}")
            courseMap.clear()
            courses.forEach { course ->
                // On cast directement à Cours
                courseMap[course.uid] = course
                val teacherId = course.idEnseignant
                if (!teacherMap.containsKey(teacherId)) {
                    loadTeacherName(teacherId)
                }
            }
            // Une fois les cours chargés, on récupère leurs IDs pour charger les séances à venir
            val courseIds = courses.map { it.uid }
            if (courseIds.isNotEmpty()) {
                seanceViewModel.fetchUpcomingSeancesForCourses(courseIds)
            }
        })

        // Observer l'état de chargement et l'erreur des séances
        seanceViewModel.loadingSeances.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressCalendrier.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        seanceViewModel.errorSeances.observe(viewLifecycleOwner, Observer { errorMsg ->
            binding.tvErrorCalendrier.visibility = if (errorMsg != null) View.VISIBLE else View.GONE
            binding.tvErrorCalendrier.text = errorMsg
        })

        // Observer les séances à venir et mettre à jour l'affichage
        seanceViewModel.upcomingSeances.observe(viewLifecycleOwner, Observer { seances ->
            val groups: MutableMap<String, MutableList<Seances>> = mutableMapOf()
            seances.forEach { seance ->
                val dateKey = dateFormatter.format(seance.date)
                if (!groups.containsKey(dateKey)) {
                    groups[dateKey] = mutableListOf()
                }
                groups[dateKey]?.add(seance)
            }
            updateCalendar(groups)
        })
    }

    // Charge le nom de l'enseignant pour un teacherId et le stocke dans teacherMap
    private fun loadTeacherName(teacherId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userData = userService.getUser(teacherId)
                val name =
                    if (userData != null && (userData.prenom.isNotBlank() || userData.nom.isNotBlank()))
                        "${userData.prenom} ${userData.nom}".trim() else "Inconnu"
                teacherMap[teacherId] = name
            } catch (e: Exception) {
                teacherMap[teacherId] = "Erreur"
            }
        }
    }

    // Met à jour dynamiquement le conteneur du calendrier avec les groupes de séances par date
    private fun updateCalendar(groups: Map<String, MutableList<Seances>>) {
        // On vide le conteneur dynamique
        binding.llCalendarContainer.removeAllViews()

        // Pour chaque groupe (date) trié par ordre croissant
        groups.toSortedMap().forEach { (date, seancesList) ->
            // Créer et ajouter un header pour la date
            val headerTextView = TextView(requireContext()).apply {
                text = date
                textSize = 18f
                setTextColor(resources.getColor(R.color.violet, null))
                setPadding(0, 16, 0, 8)
            }
            binding.llCalendarContainer.addView(headerTextView)

            // Pour chaque séance du groupe, ajouter une vue d’item
            seancesList.forEach { seance ->
                val itemView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_calendrier_seance, binding.llCalendarContainer, false)
                // Récupérer et remplir les vues de l’item
                val tvLieu = itemView.findViewById<TextView>(R.id.tvLieu)
                val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
                val tvCourse = itemView.findViewById<TextView>(R.id.tvCourse)
                val tvTeacher = itemView.findViewById<TextView>(R.id.tvTeacher)

                tvLieu.text = "Lieu : ${seance.lieu}"
                tvTime.text = "De ${formatTime(seance.date)} à ${formatTime(seance.dateFin)}"

                val course = courseMap[seance.idCours] // ici courseMap contient des Cours
                if (course is Cours) {
                    tvCourse.text = "Cours : ${course.nom}"
                    val teacherId = course.idEnseignant
                    tvTeacher.text = "Enseignant : ${teacherMap[teacherId] ?: "Chargement..."}"
                } else {
                    tvCourse.text = "Cours : Inconnu"
                    tvTeacher.text = "Enseignant : Inconnu"
                }

                binding.llCalendarContainer.addView(itemView)
            }
        }
    }

    private fun formatTime(date: Date): String {
        return timeFormatter.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}