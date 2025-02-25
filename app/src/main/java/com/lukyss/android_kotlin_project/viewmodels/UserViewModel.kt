package com.lukyss.android_kotlin_project.viewmodels

import androidx.lifecycle.*
import com.lukyss.android_kotlin_project.database.http.models.User
import com.lukyss.android_kotlin_project.database.http.services.UserService
import kotlinx.coroutines.launch

class UserViewModel(private val userService: UserService = UserService()) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _users = MutableLiveData<List<User>>(emptyList())
    val users: LiveData<List<User>> get() = _users

    private val _loadingUsers = MutableLiveData<Boolean>(false)
    val loadingUsers: LiveData<Boolean> get() = _loadingUsers

    private val _errorUsers = MutableLiveData<String?>()
    val errorUsers: LiveData<String?> get() = _errorUsers

    // Récupère un user par son uid
    fun fetchUser(uid: String) {
        _loadingUsers.value = true
        viewModelScope.launch {
            try {
                val fetchedUser = userService.getUser(uid)
                _user.value = fetchedUser
                _errorUsers.value = null
            } catch (e: Exception) {
                _errorUsers.value = e.message
            } finally {
                _loadingUsers.value = false
            }
        }
    }

    // Récupère la liste des utilisateurs
    fun fetchUsers() {
        _loadingUsers.value = true
        viewModelScope.launch {
            try {
                val fetchedUsers = userService.getUsers()
                _users.value = fetchedUsers
                _errorUsers.value = null
            } catch (e: Exception) {
                _errorUsers.value = e.message
            } finally {
                _loadingUsers.value = false
            }
        }
    }

    // Crée ou met à jour un user
    fun saveUser(user: User) {
        _loadingUsers.value = true
        viewModelScope.launch {
            try {
                userService.createOrUpdateUser(user)
                _errorUsers.value = null
            } catch (e: Exception) {
                _errorUsers.value = e.message
            } finally {
                _loadingUsers.value = false
            }
        }
    }

    // Ajoute un nouveau user et rafraîchit la liste
    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                userService.addUser(user)
                fetchUsers()
            } catch (e: Exception) {
                _errorUsers.value = e.message
            }
        }
    }

    // Met à jour un user existant et rafraîchit la liste
    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userService.updateUser(user)
                fetchUsers()
            } catch (e: Exception) {
                _errorUsers.value = e.message
            }
        }
    }

    // Supprime un user et rafraîchit la liste
    fun deleteUser(uid: String) {
        viewModelScope.launch {
            try {
                userService.deleteUser(uid)
                fetchUsers()
            } catch (e: Exception) {
                _errorUsers.value = e.message
            }
        }
    }
}
