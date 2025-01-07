package com.thezayin.enhance.presentation.event

sealed class EnhanceEvent {
    object LoadBaseImage : EnhanceEvent()
    object EnhanceNormal : EnhanceEvent()
    object EnhancePlus : EnhanceEvent()
    object EnhancePro : EnhanceEvent()
    object Deblur : EnhanceEvent()
}