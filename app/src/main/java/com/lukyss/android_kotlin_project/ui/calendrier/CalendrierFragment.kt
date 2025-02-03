package com.lukyss.android_kotlin_project.ui.calendrier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukyss.android_kotlin_project.databinding.FragmentCalendrierBinding

class CalendrierFragment : Fragment() {

    private var _binding: FragmentCalendrierBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val calendrierViewModel =
            ViewModelProvider(this).get(CalendrierViewModel::class.java)

        _binding = FragmentCalendrierBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCalendrier
        calendrierViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}