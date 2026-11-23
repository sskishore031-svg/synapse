package com.siamts.synapse.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siamts.synapse.data.model.AnalyticsData
import com.siamts.synapse.data.repository.AnalyticsRepository
import com.siamts.synapse.data.repository.AnalyticsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val timeFilter: String = "this_week", // "this_week", "last_week", "this_month"
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1, // 1..12
    val data: AnalyticsData = AnalyticsData(),
    val errorMessage: String? = null
) {
    val monthDisplayName: String
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.MONTH, selectedMonth - 1)
            cal.set(Calendar.YEAR, selectedYear)
            val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) ?: "November"
            return "$monthName $selectedYear"
        }

    val monthIsoString: String
        get() = String.format(Locale.US, "%04d-%02d", selectedYear, selectedMonth)
}

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository = AnalyticsRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun setTimeFilter(filter: String) {
        val normalized = when (filter.trim().lowercase()) {
            "this week", "this_week" -> "this_week"
            "last week", "last_week" -> "last_week"
            "this month", "this_month" -> "this_month"
            else -> filter.trim().lowercase()
        }
        if (_uiState.value.timeFilter == normalized) return
        _uiState.update { it.copy(timeFilter = normalized) }
        loadAnalytics()
    }

    fun previousMonth() {
        _uiState.update { state ->
            val newMonth = if (state.selectedMonth <= 1) 12 else state.selectedMonth - 1
            val newYear = if (state.selectedMonth <= 1) state.selectedYear - 1 else state.selectedYear
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
        loadAnalytics()
    }

    fun nextMonth() {
        _uiState.update { state ->
            val newMonth = if (state.selectedMonth >= 12) 1 else state.selectedMonth + 1
            val newYear = if (state.selectedMonth >= 12) state.selectedYear + 1 else state.selectedYear
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
        loadAnalytics()
    }

    fun selectMonth(monthNumber: Int) {
        val safeMonth = monthNumber.coerceIn(1, 12)
        if (_uiState.value.selectedMonth == safeMonth) return
        _uiState.update { it.copy(selectedMonth = safeMonth) }
        loadAnalytics()
    }

    fun refresh() {
        loadAnalytics()
    }

    fun logActivity(trackName: String, category: String, durationMinutes: Int = 15) {
        viewModelScope.launch {
            analyticsRepository.logSession(trackName, category, durationMinutes)
            loadAnalytics()
        }
    }

    private fun loadAnalytics() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = analyticsRepository.getAnalytics(
                timeFilter = currentState.timeFilter,
                month = currentState.monthIsoString
            )
            result.onSuccess { analyticsData ->
                _uiState.update { it.copy(isLoading = false, data = analyticsData) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage) }
            }
        }
    }
}
