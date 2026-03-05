package com.example.agenthq.ui.screens.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.data.local.RepositoryDao
import com.example.agenthq.data.local.RepositoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepositoryPickerUiState(
    val repositories: List<RepositoryEntity> = emptyList(),
    val filteredRepositories: List<RepositoryEntity> = emptyList(),
    val watchedIds: Set<Long> = emptySet(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class RepositoryPickerViewModel @Inject constructor(
    private val repositoryDao: RepositoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoryPickerUiState())
    val uiState: StateFlow<RepositoryPickerUiState> = _uiState

    init {
        observeRepositories()
    }

    private fun observeRepositories() {
        viewModelScope.launch {
            repositoryDao.getAll()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load repositories"
                    )
                }
                .collect { repos ->
                    val query = _uiState.value.searchQuery
                    _uiState.value = _uiState.value.copy(
                        repositories = repos,
                        filteredRepositories = repos.filter { it.fullName.contains(query, ignoreCase = true) },
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val repos = _uiState.value.repositories
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredRepositories = repos.filter { it.fullName.contains(query, ignoreCase = true) }
        )
    }

    fun toggleWatched(repoId: Long) {
        val current = _uiState.value.watchedIds.toMutableSet()
        if (repoId in current) current.remove(repoId) else current.add(repoId)
        _uiState.value = _uiState.value.copy(watchedIds = current)
    }
}
