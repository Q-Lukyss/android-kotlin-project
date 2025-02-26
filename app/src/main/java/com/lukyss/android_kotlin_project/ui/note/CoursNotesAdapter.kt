package com.lukyss.android_kotlin_project.ui.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.lukyss.android_kotlin_project.database.http.models.Cours
import com.lukyss.android_kotlin_project.database.http.models.Note
import com.lukyss.android_kotlin_project.databinding.ItemCourseWithNotesBinding
import com.lukyss.android_kotlin_project.viewmodels.NoteViewModel

class CoursNotesAdapter(
    private var courses: List<Cours>,
    private val userId: String,
    private val noteViewModel: NoteViewModel
) : RecyclerView.Adapter<CoursNotesAdapter.CourseViewHolder>() {

    // Ensemble des cours actuellement “dépliés”
    private val expandedCourses = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseWithNotesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount() = courses.size

    fun updateCourses(newCourses: List<Cours>) {
        courses = newCourses
        notifyDataSetChanged()
    }

    // Quand la notesMap change, on appelle cette fonction pour rafraîchir l’affichage
    fun updateNotesMap() {
        notifyDataSetChanged()
    }

    inner class CourseViewHolder(
        private val binding: ItemCourseWithNotesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cours: Cours) {
            binding.tvCourseName.text = cours.nom

            val isExpanded = expandedCourses.contains(cours.uid)
            binding.layoutNotes.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Toggle sur le click
            binding.courseHeaderLayout.setOnClickListener {
                if (isExpanded) {
                    expandedCourses.remove(cours.uid)
                    notifyItemChanged(adapterPosition)
                } else {
                    expandedCourses.add(cours.uid)
                    notifyItemChanged(adapterPosition)

                    // On lance la requête de notes si elles ne sont pas déjà présentes
                    val key = "${userId}_${cours.uid}"
                    if (noteViewModel.notesMap.value?.get(key) == null) {
                        noteViewModel.fetchNotesForStudentAndCourse(userId, cours.uid)
                    }
                }
            }

            // Observons (au moment du bind) la map globale de notes depuis le ViewModel
            // pour remplir layoutNotesList
            val key = "${userId}_${cours.uid}"
            val courseNotes = noteViewModel.notesMap.value?.get(key).orEmpty()

            // Vider l’ancien contenu
            binding.layoutNotesList.removeAllViews()

            // Y insérer une TextView par note
            courseNotes.forEach { note ->
                val tv = TextView(binding.root.context)
                tv.text = "${note.libelle} : ${note.note}"
                binding.layoutNotesList.addView(tv)
            }

            // On peut gérer un skeleton local : si noteViewModel.loadingStudentNotes = true,
            // on rend progressNotes visible.
            if (noteViewModel.loadingStudentNotes.value == true && isExpanded) {
                binding.progressNotes.visibility = View.VISIBLE
            } else {
                binding.progressNotes.visibility = View.GONE
            }

            // Pareil pour l’erreur
            if (noteViewModel.errorStudentNotes.value != null && isExpanded) {
                binding.tvErrorNotes.text = noteViewModel.errorStudentNotes.value
                binding.tvErrorNotes.visibility = View.VISIBLE
            } else {
                binding.tvErrorNotes.visibility = View.GONE
            }
        }
    }
}
