package com.lukyss.android_kotlin_project.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.lukyss.android_kotlin_project.database.http.models.Cours
import com.lukyss.android_kotlin_project.databinding.FragmentNoteBinding
import com.lukyss.android_kotlin_project.viewmodels.CoursViewModel
import com.lukyss.android_kotlin_project.viewmodels.NoteViewModel

class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var coursViewModel: CoursViewModel
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var adapter: CoursNotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coursViewModel = ViewModelProvider(this)[CoursViewModel::class.java]
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        // Récupérer l'UID de l'utilisateur courant
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Initialiser l'adapter avec une liste vide
        adapter = CoursNotesAdapter(
            courses = emptyList(),
            userId = userId ?: "UNKNOWN_USER",
            noteViewModel = noteViewModel
        )

        // Configurer le RecyclerView
        binding.rvCoursesWithNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCoursesWithNotes.adapter = adapter

        // Observer les cours
        coursViewModel.courses.observe(viewLifecycleOwner) { coursList: List<Cours> ->
            adapter.updateCourses(coursList)
        }

        // Observer la map de notes pour rafraîchir l'adapter
        noteViewModel.notesMap.observe(viewLifecycleOwner) {
            adapter.updateNotesMap()
        }

        // Chargement de la liste de cours + formation pour l'utilisateur
        if (userId != null) {
            coursViewModel.fetchFormationAndCoursesForUser(userId)
            coursViewModel.fetchCourses()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
