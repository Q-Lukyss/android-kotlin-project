package com.lukyss.android_kotlin_project.ui.absence

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukyss.android_kotlin_project.database.http.models.Presences
import com.lukyss.android_kotlin_project.databinding.ItemAbsenceBinding

class AbsencesAdapter(
    private var absences: List<Presences>
) : RecyclerView.Adapter<AbsencesAdapter.AbsenceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsenceViewHolder {
        val binding = ItemAbsenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbsenceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbsenceViewHolder, position: Int) {
        holder.bind(absences[position])
    }

    override fun getItemCount(): Int = absences.size

    fun updateAbsences(newAbsences: List<Presences>) {
        absences = newAbsences
        notifyDataSetChanged()
    }

    inner class AbsenceViewHolder(
        private val binding: ItemAbsenceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(absence: Presences) {
            binding.tvSeance.text = "Séance : ${absence.idSeance}"

            val justifs = absence.justificatifs
            if (!justifs.isNullOrEmpty()) {
                // On concatène les justificatifs dans une chaîne
                val joined = justifs.joinToString(separator = ", ")
                binding.tvJustificatifs.text = "Justificatifs : $joined"
            } else {
                // Liste vide ou null
                binding.tvJustificatifs.text = "Justificatif : Aucune justification fournie"
            }
        }

    }
}
