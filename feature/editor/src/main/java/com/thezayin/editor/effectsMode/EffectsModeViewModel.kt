package com.thezayin.editor.effectsMode

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.thezayin.editor.effectsMode.effectsPreview.EffectItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EffectsModeViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(EffectsModeState())
    val state: StateFlow<EffectsModeState> = _state

    var shouldGoToNextScreen = false

    fun updateEffectList(effectList: ArrayList<EffectItem>) {
        _state.update { it.copy(effectsList = effectList) }
    }

    fun addToEffectList(
        effectItems: ArrayList<EffectItem>,
        selectInitialBitmap: Boolean = false,
    ) {
        val currList = ArrayList(state.value.effectsList)
        currList.addAll(effectItems)
        _state.update { it.copy(effectsList = currList) }
        if (selectInitialBitmap) {
            selectEffect(selectedIndex = 0)
        }
    }

    fun selectEffect(selectedIndex: Int) {
        if (selectedIndex < 0 || selectedIndex >= state.value.effectsList.size) {
            Log.e(
                "EffectsModeViewModel",
                "selectEffect: index out of bound, selectedIndex = $selectedIndex",
            )
            return
        }
        _state.update {
            it.copy(
                selectedEffectIndex = selectedIndex,
                filteredBitmap = state.value.effectsList[selectedIndex].ogBitmap
            )
        }
    }
}