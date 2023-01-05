package com.common.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devfalah.repositories.models.PostHomeDto
import com.devfalah.repositories.models.PostLocalDto
import kotlinx.coroutines.flow.Flow

@Dao
interface ClubDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostLocalDto)

    @Query("SELECT * FROM CLUB_TABLE ORDER BY createdTime ASC")
    fun getPosts(): Flow<List<PostLocalDto>>

    @Query("SELECT EXISTS(SELECT 1 FROM CLUB_TABLE WHERE id = :postId)")
    suspend fun getPostWithId(postId: Int): Int

    @Query("SELECT id FROM CLUB_TABLE WHERE groupId==:groupId ORDER BY createdTime ASC")
    fun getPostsIds(groupId: Int): Flow<List<Int>>

    @Query("DELETE FROM club_table WHERE id == :postId")
    suspend fun deletePostById(postId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(post: List<PostHomeDto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostHomeDto)

    @Query("SELECT * FROM HOME_TABLE ORDER BY createdTime DESC ")
    fun getHomePosts(): Flow<List<PostHomeDto>>

    @Query("DELETE FROM HOME_TABLE")
    fun clearHomePosts()
}