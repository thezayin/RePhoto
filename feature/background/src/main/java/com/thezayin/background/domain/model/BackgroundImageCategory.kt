
package com.thezayin.background.domain.model

/**
 * Represents a category of background images.
 *
 * @param name The name of the category.
 * @param imageUrls List of image URLs under this category.
 */
data class BackgroundImageCategory(
    val name: String,
    val imageUrls: List<String>
)
