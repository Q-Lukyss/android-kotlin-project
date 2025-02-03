package com.lukyss.android_kotlin_project.ui.support_cours

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukyss.android_kotlin_project.databinding.FragmentSupportCoursBinding

class SupportCoursFragment : Fragment() {

    private var _binding: FragmentSupportCoursBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val supportCoursViewModel =
            ViewModelProvider(this).get(SupportCoursViewModel::class.java)

        _binding = FragmentSupportCoursBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSupportCours
        supportCoursViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}