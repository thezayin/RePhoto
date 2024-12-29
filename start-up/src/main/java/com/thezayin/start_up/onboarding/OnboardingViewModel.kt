package com.thezayin.start_up.onboarding

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.pref.PreferencesManager
import com.thezayin.start_up.onboarding.actions.OnboardingActions
import com.thezayin.start_up.onboarding.model.OnboardingPage
import com.thezayin.start_up.onboarding.state.OnboardingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    application: Application,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _state = MutableStateFlow(
        OnboardingState(
            pages = listOf(
                OnboardingPage(
                    beforeImageRes = com.thezayin.values.R.drawable.bg_enhance_before,
                    afterImageRes = com.thezayin.values.R.drawable.bg_enhance_after,
                    title = application.getString(com.thezayin.values.R.string.enhance),
                    subtitle = application.getString(com.thezayin.values.R.string.enhance_desc)
                ),
                OnboardingPage(
                    beforeImageRes = com.thezayin.values.R.drawable.img_before_first,
                    afterImageRes = com.thezayin.values.R.drawable.img_after_first,
                    title = application.getString(com.thezayin.values.R.string.remove_background),
                    subtitle = application.getString(com.thezayin.values.R.string.remove_background_desc)
                ),
                OnboardingPage(
                    beforeImageRes = com.thezayin.values.R.drawable.img_before_third,
                    afterImageRes = com.thezayin.values.R.drawable.img_after_third,
                    title = application.getString(com.thezayin.values.R.string.change_background),
                    subtitle = application.getString(com.thezayin.values.R.string.change_background_desc)
                ),
            )
        )
    )
    val state: StateFlow<OnboardingState> = _state

    fun onAction(action: OnboardingActions) {
        viewModelScope.launch {
            when (action) {
                is OnboardingActions.NextPage -> {
                    _state.update { currentState ->
                        val newPage =
                            (currentState.currentPage + 1).coerceAtMost(currentState.pages.size - 1)
                        currentState.copy(currentPage = newPage)
                    }
                }

                is OnboardingActions.CompleteOnboarding -> {
                    _state.update { currentState ->
                        currentState.copy(isOnboardingCompleted = true)
                    }
                    preferencesManager.setOnboardingCompleted()
                }

                is OnboardingActions.ShowError -> {
                    _state.update { currentState ->
                        currentState.copy(error = action.errorMessage)
                    }
                }
            }
        }
    }
}