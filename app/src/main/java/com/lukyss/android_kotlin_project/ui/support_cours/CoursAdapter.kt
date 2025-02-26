package com.lukyss.android_kotlin_project.ui.support_cours

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lukyss.android_kotlin_project.database.http.models.Cours
import com.lukyss.android_kotlin_project.viewmodels.SupportViewModel
import com.lukyss.android_kotlin_project.databinding.ItemSupportCoursBinding

class CoursAdapter(
    private var courses: List<Cours>,
    private val supportViewModel: SupportViewModel
) : RecyclerView.Adapter<CoursAdapter.CoursViewHolder>() {

    // Garde en mémoire les cours qui sont en mode «déplié» pour afficher leurs supports
    private val expandedSet = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursViewHolder {
        val binding = ItemSupportCoursBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoursViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoursViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount() = courses.size

    // Pour mettre à jour la liste de cours quand elle change
    fun updateCourses(newCourses: List<Cours>) {
        courses = newCourses
        notifyDataSetChanged()
    }

    inner class CoursViewHolder(
        private val binding: ItemSupportCoursBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cours: Cours) {
            // Nom du cours
            binding.tvCourseName.text = cours.nom

            // Gérer l’affichage de la zone des supports (layoutSupports)
            val isExpanded = expandedSet.contains(cours.uid)
            binding.layoutSupports.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Au clic, on bascule l’« expansion »
            binding.root.setOnClickListener {
                if (isExpanded) {
                    expandedSet.remove(cours.uid)
                    notifyItemChanged(adapterPosition)
                } else {
                    expandedSet.add(cours.uid)
                    notifyItemChanged(adapterPosition)
                    // Appeler le ViewModel pour récupérer les supports de ce cours
                    supportViewModel.fetchSupportsForCourse(cours.uid)
                }
            }

            // Ici, on peut observer `supportViewModel.supports` pour remplir layoutSupports,
            // MAIS si tu as plusieurs cours en expansion simultanément, il faut une Map.
            // Pour un code simple, on se contente d'observer une liste unique :

            supportViewModel.supports.observeForever { supportsList ->
                // On ne met à jour l’affichage que si ce cours est déplié
                if (expandedSet.contains(cours.uid)) {
                    binding.layoutSupportList.removeAllViews()
                    supportsList.forEach { support ->
                        // Vérifier si le support correspond bien à ce cours
                        if (support.idCours == cours.uid) {
                            val tv = LayoutInflater.from(binding.root.context)
                                .inflate(android.R.layout.simple_list_item_1, binding.layoutSupportList, false) as TextView
                            tv.text = support.nom
                            binding.layoutSupportList.addView(tv)
                        }
                    }
                }
            }
        }
    }
}
