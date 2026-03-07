package com.example.agenthq.ui.screens.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.data.local.RepositoryDao
import com.example.agenthq.data.local.RepositoryEntity
import com.example.agenthq.domain.usecase.SyncRepositoriesUseCase
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
    private val repositoryDao: RepositoryDao,
    private val syncRepositoriesUseCase: SyncRepositoriesUseCase
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
                    val watchedIds = repos.map { it.id }.toSet()
                    _uiState.value = _uiState.value.copy(
                        repositories = repos,
                        filteredRepositories = repos.filter { it.fullName.contains(query, ignoreCase = true) },
                        watchedIds = watchedIds,
                        isLoading = false,
                        error = null
                    )
                    // If no repos are in the DB yet, fetch them from GitHub
                    if (repos.isEmpty() && !_uiState.value.isLoading) {
                        fetchRepositoriesFromGitHub()
                    }
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
        viewModelScope.launch {
            val current = _uiState.value.watchedIds
            if (repoId in current) {
                repositoryDao.deleteById(repoId)
            } else {
                val repo = repositoryDao.getById(repoId)
                if (repo != null) {
                    repositoryDao.upsert(repo)
                }
            }
        }
    }

    private fun fetchRepositoriesFromGitHub() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            syncRepositoriesUseCase()
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to fetch repositories"
                    )
                }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            syncRepositoriesUseCase()
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to refresh repositories"
                    )
                }
        }
    }
}
