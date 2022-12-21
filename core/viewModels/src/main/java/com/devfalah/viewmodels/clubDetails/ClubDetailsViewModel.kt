package com.devfalah.viewmodels.clubDetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devfalah.usecases.*
import com.devfalah.viewmodels.Constants
import com.devfalah.viewmodels.clubDetails.mapper.toUIState
import com.devfalah.viewmodels.clubDetails.mapper.toUserUIState
import com.devfalah.viewmodels.userProfile.PostUIState
import com.devfalah.viewmodels.userProfile.mapper.toEntity
import com.devfalah.viewmodels.userProfile.mapper.toUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubDetailsViewModel @Inject constructor(
    private val getClubDetailsUseCase: GetClubDetailsUseCase,
    private val getClubMembersUseCase: GetClubMembersUseCase,
    private val getGroupWallUseCase: GetGroupWallUseCase,
    private val likeUseCase: SetLikeUseCase,
    private val allPosts: GetHomePostUseCase,
    private val favoritePostUseCase: SetFavoritePostUseCase,
    private val joinClubUseCase: JoinClubUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = ClubDetailsArgs(savedStateHandle)
    private val _uiState = MutableStateFlow(ClubDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getData()
    }

    fun getData() {
        getClubDetails()
        getMemberCount()
        getPostCount()
        getMembers()
        getClubPost()

    }

    fun swipeToRefresh(type: Int) {
        viewModelScope.launch {
            if (type == Constants.FIRST_TIME) {
                _uiState.update { it.copy(isLoading = true) }
            } else {
                _uiState.update { it.copy(isPagerLoading = true) }
            }
            try {
                val clubPosts = allPosts.loadData(uiState.value.id)
                _uiState.update {
                    it.copy(
                        isPagerLoading = false,
                        isLoading = false,
                        isEndOfPager = clubPosts.isNotEmpty(),
                        posts = it.posts + clubPosts.toUIState()
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isPagerLoading = false,
                        isLoading = false,
                        errorMessage = t.message.toString()
                    )
                }
            }
        }
    }

    private fun getClubDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            try {
                val clubDetails =
                    getClubDetailsUseCase(userID = args.userID, groupID = args.groupId)

                _uiState.update {
                    it.copy(
                        id = clubDetails.id,
                        name = clubDetails.name,
                        description = clubDetails.description,
                        privacy = getPrivacy(clubDetails.privacy),
                        isMember = clubDetails.isMember,
                        isLoading = false,
                        isSuccessful = true
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccessful = false,
                        errorMessage = throwable.message.toString()
                    )
                }
            }
        }
    }

    private fun getMemberCount() {
        viewModelScope.launch {
            try {
                val memberCount = getClubMembersUseCase(args.groupId).size
                _uiState.update { it.copy(membersCount = memberCount) }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false, isSuccessful = false, errorMessage = t.message.toString()
                    )
                }
            }
        }

    }

    private fun getPostCount() {
        viewModelScope.launch {
            try {
                val postCount = getGroupWallUseCase(args.userID, args.groupId).count
                _uiState.update { it.copy(postCount = postCount) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(errorMessage = t.message.toString()) }
            }
        }
    }

    private fun getPrivacy(value: String): String {
        return when (value) {
            "1" -> PRIVATE_PRIVACY
            else -> PUBLIC_PRIVACY
        }
    }

    private fun getMembers() {
        viewModelScope.launch {
            try {
                val members = getClubMembersUseCase(args.groupId).toUserUIState()
                _uiState.update { it.copy(members = members) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(errorMessage = t.message.toString()) }
            }
        }
    }

    private fun getClubPost() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(posts = getGroupWallUseCase(args.userID, args.groupId).post.toUIState())
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(errorMessage = t.message.toString()) }
            }
        }
    }

    fun onClickLike(post: PostUIState) {
        viewModelScope.launch {
            try {
                val totalLikes = likeUseCase(
                    postID = post.postId, userId = uiState.value.id,
                    isLiked = post.isLikedByUser
                )
                val updatedPost = post.copy(
                    isLikedByUser = !post.isLikedByUser, totalLikes = totalLikes
                )
                _uiState.update {
                    it.copy(posts = uiState.value.posts.map {
                        if (it.postId == post.postId) {
                            updatedPost
                        } else {
                            it
                        }
                    })
                }
            } catch (t: Throwable) {
                Log.e("Test Test Test", t.message.toString())
                _uiState.update { it.copy(errorMessage = t.message.toString()) }
            }
        }
    }

    fun onClickSave(post: PostUIState) {
        viewModelScope.launch {
            try {
                favoritePostUseCase(post.toEntity())
                _uiState.update {
                    it.copy(
                        posts = _uiState.value.posts
                            .map {
                                if (it.postId == post.postId) {
                                    it.copy(isSaved = true)
                                } else {
                                    it
                                }
                            }
                    )
                }
            } catch (t: Throwable) {
                t.message.toString()
            }
        }
    }

    fun joinClubs(clubId: Int) {
        viewModelScope.launch {
            try {
                val join = joinClubUseCase(clubId = args.groupId, userId = args.userID)
                _uiState.update { it.copy(isJoin = join) }
            } catch (t: Throwable) {
                Log.i("error", t.message.toString())
            }
        }
    }


    companion object {
        private const val PRIVATE_PRIVACY = "Private"
        private const val PUBLIC_PRIVACY = "Public"
    }
}