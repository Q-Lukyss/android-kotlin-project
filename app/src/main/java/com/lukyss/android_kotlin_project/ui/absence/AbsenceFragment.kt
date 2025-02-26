package com.lukyss.android_kotlin_project.ui.absence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.lukyss.android_kotlin_project.databinding.FragmentAbsenceBinding
import com.lukyss.android_kotlin_project.viewmodels.PresenceViewModel

class AbsenceFragment : Fragment() {

    private var _binding: FragmentAbsenceBinding? = null
    private val binding get() = _binding!!

    private lateinit var absenceViewModel: PresenceViewModel
    private lateinit var adapter: AbsencesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAbsenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        absenceViewModel = ViewModelProvider(this)[PresenceViewModel::class.java]

        // Préparer l'adapter avec une liste vide initialement
        adapter = AbsencesAdapter(emptyList())
        binding.rvAbsencesToJustify.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAbsencesToJustify.adapter = adapter

        // Observer le loader
        absenceViewModel.loadingPresence.observe(viewLifecycleOwner) { isLoading ->
            binding.progressPresence.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer l’erreur
        absenceViewModel.errorPresence.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                binding.tvErrorPresence.text = errorMsg
                binding.tvErrorPresence.visibility = View.VISIBLE
            } else {
                binding.tvErrorPresence.visibility = View.GONE
            }
        }

        // Observer nbPresences
        absenceViewModel.nbPresences.observe(viewLifecycleOwner) { count ->
            binding.tvNbPresences.text = "Nombre de présences : $count"
        }

        // Observer nbAbsences
        absenceViewModel.nbAbsences.observe(viewLifecycleOwner) { count ->
            binding.tvNbAbsences.text = "Nombre d'absences : $count"
        }

        // Observer absencesToJustify
        absenceViewModel.absencesToJustify.observe(viewLifecycleOwner) { absencesList ->
            if (absencesList.isNotEmpty()) {
                // On affiche le RecyclerView, on masque le message “tout est justifié”
                binding.rvAbsencesToJustify.visibility = View.VISIBLE
                binding.tvNoAbsencesToJustify.visibility = View.GONE
                adapter.updateAbsences(absencesList)
            } else {
                // On masque le RecyclerView, on affiche le message
                binding.rvAbsencesToJustify.visibility = View.GONE
                binding.tvNoAbsencesToJustify.visibility = View.VISIBLE
            }
        }

        // Récupérer l’UID de l’utilisateur
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            absenceViewModel.fetchPresencesForUser(userId)
        } else {
            // Gérer le cas “pas d’utilisateur”, ex. forcer un logout ou afficher un message
            binding.tvErrorPresence.text = "Aucun utilisateur connecté."
            binding.tvErrorPresence.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
