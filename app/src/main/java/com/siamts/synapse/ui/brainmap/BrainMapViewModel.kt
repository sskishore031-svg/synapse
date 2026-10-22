package com.siamts.synapse.ui.brainmap

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing Synapse Brain Map state and subject node clicks.
 */
class BrainMapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BrainMapUiState())
    val uiState: StateFlow<BrainMapUiState> = _uiState.asStateFlow()

    fun onNodeClicked(nodeId: String) {
        // Handle node selection
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
