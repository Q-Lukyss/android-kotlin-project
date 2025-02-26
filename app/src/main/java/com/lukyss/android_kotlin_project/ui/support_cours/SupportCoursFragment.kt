package com.lukyss.android_kotlin_project.ui.support_cours

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.lukyss.android_kotlin_project.database.http.models.Cours
import com.lukyss.android_kotlin_project.databinding.FragmentSupportCoursBinding
import com.lukyss.android_kotlin_project.ui.support_cours.CoursAdapter
import com.lukyss.android_kotlin_project.viewmodels.CoursViewModel
import com.lukyss.android_kotlin_project.viewmodels.SupportViewModel

class SupportCoursFragment : Fragment() {

    private var _binding: FragmentSupportCoursBinding? = null
    private val binding get() = _binding!!

    private lateinit var coursViewModel: CoursViewModel
    private lateinit var supportViewModel: SupportViewModel
    private lateinit var adapter: CoursAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportCoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Récupérer les ViewModels
        coursViewModel = ViewModelProvider(this)[CoursViewModel::class.java]
        supportViewModel = ViewModelProvider(this)[SupportViewModel::class.java]

        // 2) Initialiser l'adapter
        adapter = CoursAdapter(emptyList(), supportViewModel)

        // 3) Configurer le RecyclerView
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = adapter

        // 4) Observer la liste de cours (CoursViewModel)
        coursViewModel.courses.observe(viewLifecycleOwner) { courses: List<Cours> ->
            adapter.updateCourses(courses)
        }

        // 5) Charger les cours pour l'utilisateur actuel
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            coursViewModel.fetchFormationAndCoursesForUser(userId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
