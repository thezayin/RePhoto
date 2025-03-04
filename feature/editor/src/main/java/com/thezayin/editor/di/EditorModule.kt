package com.thezayin.editor.di

import com.thezayin.editor.SharedEditorViewModel
import com.thezayin.editor.cropMode.CropViewModel
import com.thezayin.editor.drawMode.DrawModeViewModel
import com.thezayin.editor.editor.presentation.EditorViewModel
import com.thezayin.editor.editor.domain.repository.ImageRepository
import com.thezayin.editor.editor.data.repository.ImageRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val editorModule = module {
    viewModelOf(::EditorViewModel)
    viewModelOf(::SharedEditorViewModel)
    viewModelOf(::CropViewModel)
    viewModelOf(::DrawModeViewModel)
    single<ImageRepository> { ImageRepositoryImpl(get()) }
}