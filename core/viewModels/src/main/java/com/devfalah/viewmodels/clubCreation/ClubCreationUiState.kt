package com.devfalah.viewmodels.clubCreation

data class ClubCreationUiState(
    val name: String = "",
    val description: String = "",
    val privacy: ClubPrivacy = ClubPrivacy.PUBLIC,
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
)

enum class ClubPrivacy(val value: Int){
    PUBLIC(2),
    PRIVATE(1),
}