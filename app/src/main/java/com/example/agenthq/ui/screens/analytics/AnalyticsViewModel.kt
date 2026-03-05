package com.example.agenthq.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenthq.domain.model.AnalyticsData
import com.example.agenthq.domain.usecase.ComputeAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val computeAnalyticsUseCase: ComputeAnalyticsUseCase
) : ViewModel() {
    val analytics: StateFlow<AnalyticsData?> = computeAnalyticsUseCase.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
