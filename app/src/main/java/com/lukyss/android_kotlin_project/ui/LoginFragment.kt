package com.lukyss.android_kotlin_project.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.lukyss.android_kotlin_project.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.lifecycleScope
import com.lukyss.android_kotlin_project.MainActivity

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val passwordEditText: EditText = view.findViewById(R.id.passwordEditText)
        val loginButton: Button = view.findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if(!::auth.isInitialized) auth = FirebaseAuth.getInstance()

            if (!isValidEmail(email)) {
                emailEditText.error = "Email non valide"
                Toast.makeText(requireContext(), "Email non valide", Toast.LENGTH_LONG).show()
            }
            else if (password.isEmpty()) {
                passwordEditText.error = "Mot de passe requis"
            } else {
                loginUser(email, password)
            }
        }
    }

//    private fun loginUser(email: String, password: String) {
//        // Connecter l'utilisateur ou lancer une autre action
//        // FirebaseAuth par exemple
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(requireContext(), "Connexion réussie", Toast.LENGTH_SHORT).show()
//                    val navController = findNavController()
//                    navController.navigate(R.id.navigation_home)
//                } else {
//                    // Erreur de connexion
//
//                    Log.d("DebugMyApp", task.exception?.message ?: "erreur inconnue");
//
//                    Toast.makeText(requireContext(), "Échec de la connexion : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                // Authentifier l'utilisateur via FirebaseAuth en mode coroutine
                val authResult = FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .await()

                // Récupérer l'UID de l'utilisateur connecté
                val userId = authResult.user?.uid
                if (userId == null) {
                    Toast.makeText(requireContext(), "Utilisateur introuvable", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Récupérer le document utilisateur dans Firestore en mode coroutine
                val documentSnapshot = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(userId)
                    .get()
                    .await()

                if (documentSnapshot.exists()) {
                    val statut = documentSnapshot.getLong("statut")?.toInt() ?: -1

                    when (statut) {
                        0 -> {
                            // Étudiant : rediriger vers le dashboard étudiant (exemple : navigation_home)
                            (activity as? MainActivity)?.updateBottomMenu(R.menu.bottom_nav_menu_etudiant)
                            findNavController().navigate(R.id.navigation_home_etudiant)
                        }
                        10 -> {
                            // Administratif : afficher un message ou rediriger vers un dashboard dédié
                            (activity as? MainActivity)?.updateBottomMenu(R.menu.bottom_nav_menu_administratif)
                            findNavController().navigate(R.id.navigation_home_administratif)
//                            Toast.makeText(requireContext(), "Connexion administratif non implémentée", Toast.LENGTH_LONG).show()
                        }
                        5 -> {
                            // Intervenant : afficher un toast pour l'instant
                            Toast.makeText(requireContext(), "Connexion intervenant non implémentée", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(requireContext(), "Statut inconnu", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Aucun utilisateur trouvé en base", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.d("DebugMyApp", e.message ?: "Erreur inconnue")
                Toast.makeText(requireContext(), "Échec de la connexion : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}